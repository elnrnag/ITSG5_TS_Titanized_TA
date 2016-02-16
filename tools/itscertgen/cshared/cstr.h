/*********************************************************************
######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2003 - 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
*********************************************************************/
#ifndef CSTR_H
#define CSTR_H
#ifdef __cplusplus
extern "C" {
#endif

int    cstrlen(const char * str);
/* copy src to dst and return pointer to the next byte after the end */ 
char * cstrcpy(char * dst, const char * src);

/* copy up to maxsize characters from src to dst and return pointer to the next byte after the end */ 
char * cstrncpy(char * dst, int maxsize, const char * src);

/* copy up to maxsize characters from parameters to dst and return pointer to the next byte after the end */ 
char * cvstrncpy(char * dst, int maxsize, const char * ptr, ...);


/* allocate copy of the str */ 
char * cstralloc(int size);
char * cstrdup(const char * str);
char * cstrndup(const char * str, int max_size);


/* allocate new str and collect all paramaters */ 
char * cvstrdup(const char * ptr, ...);

char * cstrnload(char * dst, int max_size, const char * path);
char * cstraload(char ** p, const char * path);

const char * cstrlastpathelement(const char * str);

int cstr_write(const char * const p, char ** const ptr, const char * const end, int * const error);
int cstr_read (char * const p, const char ** const ptr, const char * const end, int * const error);

int cstrn_write(const char * const p, int length, char ** const ptr, const char * const end, int * const error);
int cstrn_read (char * const p, int length, const char ** const ptr, const char * const end, int * const error);

char * cstr_hex2bin(char * bin, int blen, const char * hex, int hlen);
char * cstr_bin2hex(char * hex, int hlen, const char * bin, int blen);
#ifdef __cplusplus
}
#endif
#endif
