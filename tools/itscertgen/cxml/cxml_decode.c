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
#include <stdlib.h>
#include <string.h>
#include <errno.h>
int cxml_text_decode(void * const handler, char * const src, int const srclen)
{
    const char * s = src;
    const char * send = src + srclen;
    char * d = src;
    char * dend = d + srclen;
    char * e;
    cxml_entity_t * c;
    cxml_handler_t * h = (cxml_handler_t *)handler;

    while(s != send){
        if(d>=dend)return -1;
        if(*s != '&'){
            if(d != s){
                *d = *s;
            }
            d++; s++;
            continue;
        }
        s++;
        if(*s == '#'){
            unsigned int value;
            s++;
            errno = 0;
            if(*s == 'x' || *s == 'X'){
                /* hexadecimal symbol representation */
                s++;
                value = strtoul(s, &e, 16);
            }else if(*s >= '0' && *s <= '9'){
                /* decimal symbol representation */
                value = strtoul(s, &e, 10);
            }else{
                ASSERT_RETURN(-1);
            }
            if(errno != 0 || *e != ';'){
                ASSERT_RETURN(-1);
            }
            if(value > 0xFFFFFF)
                *(d++) = (char)((value>>24)&0xFF);
            if(value > 0xFFFF)
                *(d++) = (char)((value>>16)&0xFF);
            if(value > 0xFF)
                *(d++) = (char)((value>>8)&0xFF);
            *(d++) = value&0xFF;
            s = e+1;
            continue;
        }

        c = h->Entities;
        while(c){
            if(c->nlen < (send-s) && s[c->nlen] == ';'){
                if(0 == memcmp(c->name, s, c->nlen)){
                    break;
                }
            }
            c = c->next;
        }
        if(c == NULL){
            ASSERT_RETURN(-1);
        }
        if(d + c->vlen > s + (c->nlen + 1)){
            ASSERT_RETURN(-1);
        }
        memcpy(d, c->value, c->vlen);
        d += c->vlen;
        s += c->nlen + 1;
    }
    *d = 0;
    return d - src;
}
