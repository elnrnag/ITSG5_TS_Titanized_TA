/*********************************************************************
######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2003 - 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
*********************************************************************/

#include "cserialize.h"
#include <errno.h>

#ifdef __GNUC__
#define cint_cpy(D,N,S) __builtin_memcpy((char*)(D),N,S)
#else
#include <string.h>
#define cint_cpy(D,N,S) memcpy((char*)(D),N,S)
uint64_t cint64_swap(uint64_t);
uint32_t cint32_swap(uint32_t);
uint16_t cint16_swap(uint16_t);
static const uint64_t one64 = 1;
#endif

int _cint64_write(const uint64_t value, char** const ptr, const char* const end, int * const error)
{
	register unsigned char* p = (unsigned char*)*ptr;
	if(p + 8 <= (unsigned char*)end){
		if(0 == (((int)p)&0x7)){
			*((uint64_t*)p) = cint64_hton(value);
			p+=8;
		}else{
			int i;
			for(i=7; i>=0; i--){
				*p++ = (value>>(8*i))&0xFF;
			}
		}
		*ptr = (char*)p;
		if(error) *error = 0;
		return 0;
	}
	if(error) *error = ENOMEM;
	return -1;
}

int _cint32_write(const uint32_t value, char** const ptr, const char* const end, int * const error)
{
	register unsigned char* p = (unsigned char*)*ptr;
	if(p + 4 <= (unsigned char*)end){
		if(0 == (((int)p)&0x3)){
			*((uint32_t*)p) = cint32_hton(value);
			p+=4;
		}else{
			int i;
			for(i=3; i>=0; i--){
				*p++ = (value>>(8*i))&0xFF;
			}
		}
		*ptr = (char*)p;
		if(error) *error = 0;
		return 0;
	}
	if(error) *error = ENOMEM;
	return -1;
}

int _cint16_write(const uint16_t value, char** const ptr, const char* const end, int * const error)
{
	register unsigned char* p = (unsigned char*)*ptr;
	if(p + 2 <= (unsigned char*)end){
		*p++ = (value>>8)&0xFF;
		*p++ = value&0xFF;
		*ptr = (char*)p;
		if(error) *error = 0;
		return 0;
	}
	if(error) *error = ENOMEM;
	return -1;
}

int _cint8_write(const uint8_t value, char** const ptr, const char* const end, int * const error)
{
	if(*ptr < end) {
		*((uint8_t*)*ptr) = value;
		(*ptr) ++;
		if(error) *error = 0;
		return 0;
	}
	if(error) *error = ENOMEM;
	return -1;
}

uint64_t cint64_read(const char** const ptr, const char* const end, int * const error)
{
	uint64_t value;
	register const uint8_t * p = (const  uint8_t *)*ptr;
	if(p + 8 <= (const uint8_t *)end) {
		if(0 == (((int)p)&0x7)){
			value = cint64_hton(*p);
			*ptr = (char*)(p+8);
		}else{
			int i;
			value=0;
			for(i=0; i<8; i++){
				value  = (value<<8) | *(p++);
			}
			*ptr = (char*)p;
		}
		if(error) *error = 0;
	}else{
		value = (unsigned)-1;
		if(error) *error = ENOMEM;
	}
	return value;
}

uint32_t cint32_read(const char** const ptr, const char* const end, int * const error)
{
	uint32_t value;
	register const uint8_t * p = (const uint8_t*)*ptr;
	if(p + 4 <= (const uint8_t *)end) {
		value = ((uint32_t)p[0])<<24 | ((uint32_t)p[1])<<16 | ((uint32_t)p[2])<<8 | p[3];
		*ptr = (char*)(p+4);
	if(error) *error = 0;
	}else{
		value = (unsigned)-1;
		if(error) *error = ENOMEM;
	}
	return value;
}

uint16_t cint16_read(const char** const ptr, const char* const end, int * const error)
{
	uint32_t value;
	register const uint8_t * p = (const uint8_t*)*ptr;
	if(p + 2 > (const uint8_t*)end) {
		value = (unsigned)-1;
		if(error) *error = ENOMEM;
	}else{
		value = ((uint16_t)p[0])<<8 | p[1];
		*ptr = (const char*)(p+2);
		if(error) *error = 0;
	}
	return value;
}

uint8_t cint8_read(const char** const ptr, const char* const end, int * const error)
{
	if(error) *error = 0;
	if(*ptr >= end) {
		if(error) *error = ENOMEM;
		return -1;
	}
	return *(const uint8_t*)((*ptr)++);
}

int cintx_bytecount(uint64_t value)
{
	int num_bytes = 0;
#ifdef __GNUC__
	if(value){
		num_bytes = (64 + 6 - __builtin_clzll(value))/7;
	}else{
		num_bytes = 1;
	}
#else
	uint64_t overflow = 0;
	while(value >= overflow){
		num_bytes++;
		overflow = one64 << (7*num_bytes);
	}
#endif
	return num_bytes;
}

int _cintx_write (const uint64_t value, char ** const ptr, const char * const end, int * const error)
{
	int num_bytes = 0;
	uint8_t c;
	uint8_t *out = (uint8_t*)(*ptr);
	num_bytes = cintx_bytecount(value);
	if(num_bytes > 8 || out+num_bytes > ((const uint8_t*)end)){
		if(error) *error = ENOMEM;
		return -1;
	}
	num_bytes--;
	c  = ~((1<<(8-num_bytes))-1);
	c |= (value >> (num_bytes*8)) & 0xFF;
	*out++ = c;
	while(num_bytes){
		num_bytes--;
		c = (value >> (num_bytes*8)) & 0xFF;
		*out++ = c;
	}
	*ptr = (char*)out;
	if(error) *error = 0;
	return 0;
}

static int countof1(int c)
{
	int r = 0;
	while(c & 0x80){
#ifdef __GNUC__
		return 1 + __builtin_clrsb(c<<24);
#else
		r++;
		c<<=1;
#endif		
	}
	return r;
}

uint64_t cintx_read(const char** const ptr, const char* const end, int * const error)
{
	uint8_t c;
	const uint8_t* in;
	int i, lead_ones;
	in = (const uint8_t*)*ptr;
	if(in <= (const uint8_t*)end){
		c = *in;
		lead_ones = countof1(c);
		if(in + 1 + lead_ones <= (const uint8_t*)end) {
			uint64_t value;
			value = c & ((1<<(7-lead_ones))-1);
			for(i=1; i<=lead_ones; i++){
				value  = (value<<8) | in[i];
			}
			*ptr = (const char*)(in + 1 + lead_ones);
			if(error) *error = 0;
			return value;
		}
	}
	if(error) *error = ENOMEM;
	return -1;
}

int cbuf_write(const void * const p, int length, char ** const ptr, const char * const end, int * const error)
{
	if(error) *error = 0;
	if((*ptr) + length >= end) {
		if(error) *error = ENOMEM;
		return -1;
	}
	cint_cpy(*ptr, p, length);
	*ptr = (*ptr) + length;
	return 0;
}

int cbuf_read (void * const p, int length, const char ** const ptr, const char * const end, int * const error)
{
	if(error) *error = 0;
	if((*ptr) + length >= end) {
		if(error) *error = ENOMEM;
		return -1;
	}
	cint_cpy(p, *ptr, length);
	*ptr = (*ptr) + length;
	return 0;
}
