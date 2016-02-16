/*********************************************************************
######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2008 - 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
*********************************************************************/
#include "cxml.h"
#include <string.h>
#include <stdlib.h>
#include <errno.h>

static const char * _dcmi_chars = "&<>'\"";
struct dcmi_tag{
    const char * str;
    int          len;
};
static const struct dcmi_tag _dcmi_strs[] = {
    {"&amp;",  5},
    {"&lt;",   4},
    {"&gt;",   4},
    {"&apos;", 6},
    {"&quot;", 6},
};
#define dcmi_tag_count (sizeof(_dcmi_strs)/sizeof(_dcmi_strs[0]))

int cxml_to_dcmi  (char ** const pdst, const char * const src, int const src_length)
{
    const char *s, *se;
    char *d, *dst;
    int slen;
    int len, alen;

    if(src_length > 0) slen = src_length;
    else               slen = strlen(src);

    len = slen;
    alen = (slen | 0xF) + 1;
    dst = malloc(alen);

    s  = src;
    se = s + slen;
    d  = dst;

    while(s < se){
        char * n;
        n = strchr(_dcmi_chars, *s);
        if(n){
            const struct dcmi_tag * to;
            to = &_dcmi_strs[n-_dcmi_chars];
            len += to->len - 1;
            if(len  > alen){
                alen = (len | 0xF) + 1;
                n = realloc(dst, alen);
                d = n + (d-dst);
                dst = n;
            }
            memcpy(d, to->str, to->len);
            d += to->len;
        }else{
            *(d++) = *s;
        }
        s++;
    }
    *pdst = dst;
    return len;
}

int to_dcmi(char ** s)
{
    int count = 0;
    char *b, *p, *e;
    int len, alen;

    b = p = *s;
    len = strlen(b);
    e = b + len;
    alen=len+2;

    while(p < e){
        char * n;
        n = strchr(_dcmi_chars, *p);
        if(n){
            const struct dcmi_tag * to;
            to = &_dcmi_strs[n-_dcmi_chars];
            if(len + (to->len) > alen){
                int l = len + to->len - 1;
                alen = (l & 0xF) + 1;
                n = realloc(b, alen);
                p = n + (p-b);
                b = n;
            }
            memmove(p + to->len, p+1, e-p);
            memcpy(p, to->str, to->len);
            e += to->len - 1;
            p += to->len;
            count++;
        }else{
            p++;
        }
    }
    *s = b;
    return count;
}

int cxml_from_dcmi(char * const s, int len)
{
    char * p = s;
    if(len <= 0){
        len = strlen(p);
    }

    while(1){
        unsigned int i;
        char * e;
        p = strchr(p, '&');
        if(!p) break;
        if(p[1] == '#'){
            unsigned long val;
            errno = 0;
            if(p[2] == 'x'){ /* hexadecimal value */
                val = strtoul(p+3, &e, 16);
            }else{           /* decimal value */
                val = strtoul(p+3, &e, 10);
            }
            if(errno || val > 0xFFFF || *e != ';'){
                return -1;
            }
            if(val > 0xFF){
                *p++ = (char)(val>>8);
            }
            *p++ = val&0xFF;
            e++;
        }else{
            for(i=0; i<dcmi_tag_count; i++){
                if(0 == strncmp(_dcmi_strs[i].str, p, _dcmi_strs[i].len)){
                    e = p + _dcmi_strs[i].len;
                    *p++ = _dcmi_chars[i];
                    break;
                }
            }
            if(i == dcmi_tag_count){
                return -1;
            }
        }
        memmove(p, e, len-(s-e)+1);
        len -= (e-p);
    }
    return len;
}
