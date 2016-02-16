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

static const char _hex_digits[] = "0123456789ABCDEF";

static int _prepare(void * const handler,
                    char * * const p_beg,
                    char * * const p_cur,
                    char * * const p_end)
{
    char * beg, *cur, *end;
    beg = *p_beg;
    cur = *p_cur;
    end = *p_cur;

    char * p = cxml_alloc(handler, 17 + (end-beg));
    if(p){
        if(cur>beg){
            memcpy(p, beg, cur-beg);
        }
        cxml_free(handler, beg);
        *p_cur = p + (cur-beg);
        *p_end = p + 16 + (end-beg);
        *p_beg = p;
        return (end-beg) + 16;
    }
    cxml_free(handler, beg);
    return -1;
}

int cxml_text_encode(void * const handler, char * * const p_dst,
                     const char * const src, int const len)
{
    cxml_handler_t * h = (cxml_handler_t *)handler;
    int srclen;
    if(len <= 0 ) srclen = strlen(src);
    else          srclen  = len;

    char * db = cxml_alloc(handler, srclen+1);
    char * de  = db + srclen;
    const char * s  = src;
    const char * se = src + srclen;
    char * d = db;

    while(s < se){
        /* search for entities */
        const cxml_entity_t *fc = cxml_handler_find_entity(handler, s, se - s);
        if(fc){
            while(d + fc->nlen + 2 > de){
                if(-1 == _prepare(handler, &db, &d, &de)){
                    return -1;
                }
            }
            * d ++  = '&';
            memcpy(d, fc->name, fc->nlen); d+=fc->nlen;
            * d ++  = ';';
            s += fc->vlen;
        }else{
            /* check for supported symbol range */
            unsigned char ch = *s;
            if(ch  < ' ' && ch != '\t' && ch != '\n' && ch != '\r'){
                if(d + 5 > de){
                    if(-1 == _prepare(handler, &db, &d, &de)){
                        return -1;
                    }
                }
                * d ++  = '&';
                * d ++  = '#';
                * d ++  = _hex_digits[(ch >> 4)];
                * d ++  = _hex_digits[(ch &0xF)];
                * d ++  = ';';
            }else{
                if(d >= de){
                    if(-1 == _prepare(handler, &db, &d, &de)){
                        return -1;
                    }
                }
                * d ++  = ch;
            }
            s++;
        }
    }
    *d = 0;
    *p_dst = db;
    return d-db;
}
