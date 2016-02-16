/*********************************************************************
######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2003 - 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
*********************************************************************/
#include "cstr.h"
#include <string.h>
#include <stdio.h>
#include <stdarg.h>
#include <stdlib.h>

int    cstrlen(const char * str)
{
	return str ? strlen(str) : 0;
}

char * cstrcpy(char * dst, const char * src)
{
	if(!dst) return (char*)0;
	int len = 0;
	if(src){
		len = strlen(src);
		if(len){
			memcpy(dst, src, len);
		}
	}
	dst[len]=0;
	return dst + len;
}

/* copy up to maxsize characters from src to dst and return pointer to the next byte after the end */ 
char * cstrncpy(char * dst, int maxsize, const char * src)
{
	if(!dst) return (char*)0;
	unsigned int ms = maxsize;
	unsigned int len = 0;
	if(src && ms > 0){
		len = strlen(src);
		if(len > ms){
			len = ms;
			if(len){
				memcpy(dst, src, len);
			}
		}
	}
	dst[len]=0;
	return dst + len;
}

/* copy up to maxsize characters from src to dst and return pointer to the next byte after the end */ 
char * cvstrncpy(char * dst, int maxsize, const char * ptr, ...)
{
	va_list ap;
	char * p = dst;
	const char * r = ptr;
	unsigned int ms = maxsize;
	if(ms > 0){
		va_start(ap, ptr);
		while(r){
			unsigned int len = strlen(r);
			if(len > ms) len = ms;
			memcpy(p, r, len);
			p  += len;
			ms -= len;
			r = va_arg(ap, const char*);
		}
		va_end(ap);
		*p = 0;
	}
	return p;
}

char * cstralloc(int size)
{
	return (char*)malloc((size+0xF)&(~0xF));
}
char * cstrdup(const char * str)
{
	char * ret = NULL;
	if(str){
		int len = strlen(str);
		if(len){
			ret = cstralloc(len);
			memcpy(ret, str, len+1);
		}
	}
	return ret;
}

char * cstrndup(const char * str, int max_size)
{
	char * ret = NULL;
	unsigned int ms = max_size;
	if(str){
		unsigned int len = strlen(str);
		if(len>ms) len=ms;
		if(len){
			ret = cstralloc(len);
			memcpy(ret, str, len);
			ret[len] = 0;
		}
	}
	return ret;
}

char * cvstrdup(const char * ptr, ...)
{
	va_list ap;
	int len = 0;
	char *dst, *p;
	const char * r;
	
	if(!ptr) return (char*)ptr;

	// calculate size
	r = ptr;
	va_start(ap, ptr);
	while(r){
		len += strlen(r);
		r = va_arg(ap, const char*);
	}
	va_end(ap);
	
	p = dst = cstralloc(len+1);
	if(dst){
		r = ptr;
		va_start(ap, ptr);
		while(r){
			len = strlen(r);
			memcpy(p, r, len);
			p += len;
			r = va_arg(ap, const char*);
		}
		va_end(ap);
		*p = 0;
	}
	return dst;
}

char * cstrnload(char * dst, int max_size, const char * path)
{
	FILE * f = fopen(path, "rb");
	unsigned long len;
	unsigned long ms = max_size;
	if (!f) return NULL;
	fseek(f, 0, SEEK_END);
	len = ftell(f);
	fseek(f, 0, SEEK_SET);
	if (len > ms) len = ms;
	ms = fread(dst, 1, len, f);
	fclose(f);
	if ((int)ms < 0){
		return NULL;
	}
	dst[len] = 0;
	return dst + len;
}

char * cstraload(char ** p, const char * path)
{
	char * ret = NULL;
	FILE * f = fopen(path, "rb");
	int len;
	if (f){
		fseek(f, 0, SEEK_END);
		len = ftell(f);
		fseek(f, 0, SEEK_SET);
		if (len > 0){
			ret = malloc(len);
			if (ret){
				int ms = fread(ret, 1, len, f);
				if (ms < len){
					free(ret);
					*p = ret = NULL;
				}
				else{
					*p = ret;
					ret += len;
				}
			}
		}
		fclose(f);
	}
	return ret;
}

const char * cstrlastpathelement(const char * str)
{
	const char * p = strrchr(str, '/');
	const char * p2 = strrchr(str, '/');
	if(p<p2)p=p2;
	else if(p == NULL) p = str;
	return p;
}

char * cstr_hex2bin(char * bin, int blen, const char * hex, int hlen)
{
	// check
	const char * h = hex;
	const char * e = hex+hlen;
	char * b = bin;
	int n = 0;
	while (h<e){
		char ch = *h++;
		if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') continue;
		if (ch >= '0' && ch <= '9') continue;
		if (ch >= 'A' && ch <= 'F') continue;
		if (ch >= 'a' && ch <= 'f') continue;
		return NULL;
	}
	h = hex;
	while (h < e){
		char ch = *h++;
		if (ch >= '0' && ch <= '9') ch -= '0';
		else if (ch >= 'A' && ch <= 'F') ch -= 'A' - 0x0A;
		else if (ch >= 'a' && ch <= 'f') ch -= 'a' - 0x0A;
		else continue;
		if (!n){
			*b = ch;
		}
		else{
			char ch1 = *b;
			*b++ = (ch1 << 4) | ch;
		}
		n = !n;
	}
	if (n){
		char ch1 = *b;
		*b++ = (ch1 << 4);
		n = 0;
	}
	return b;
}
static const char* _hexDigits = "0123456789ABCDEF";
char * cstr_bin2hex(char * hex, int hlen, const char * bin, int blen)
{
	const unsigned char *b, *e;
	char * s;

	// sanity check
	if(hlen >=0 && hlen < blen * 2) return NULL;

	b = (const unsigned char *)bin;
	e = b + blen - 1;
	s = hex + blen * 2;
	if(s < hex + hlen) *s = 0;
	for (; b <= e; e--){
		*(--s) = _hexDigits[(*e) & 0xF];
		*(--s) = _hexDigits[(*e) >> 4];
	}
	return hex + blen * 2;
}
