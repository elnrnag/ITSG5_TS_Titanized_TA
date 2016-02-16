/*********************************************************************
######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2008 - 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
*********************************************************************/
#ifdef _MSC_VER
#define _CRT_SECURE_NO_WARNINGS
#endif

#include "cxml.h"
#include <ctype.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#ifdef _MSC_VER
#define inline __inline
#endif

#ifdef DEBUG
//#define DEBUG_XML_STRUCTURE
#include <signal.h>
#endif

#ifdef GNUC
#define CXML_INC(X) __sync_add_and_fetch(X, 1)
#define CXML_DEC(X) __sync_sub_and_fetch(X, 1)
#else
#define CXML_INC(X) (++(*((int*)(X))))
#define CXML_DEC(X) (--(*((int*)(X))))
#endif
typedef unsigned int uint;

#define CXML_BUFFER_SIZE 1024

#ifndef _CXML_HANDLER_PARSE_STREAM_DEFINED
int  cxml_handler_parse_stream(void * const h, FILE * f);
#endif
void cxml_handler_add_default_entities(void * const h);

void cxml_handler_init(void * const p, const cxml_handler_class * const Class)
{
    cxml_handler_t * h = p;
    memset(h, 0, Class->size);
    h->Class = Class;
    h->File = NULL;
    h->Version = NULL;
    h->Encoding = NULL;
    cxml_handler_add_default_entities(h);
}

void * cxml_handler_new(const cxml_handler_class * const Class)
{
    cxml_handler_t * h;
    unsigned int size;
    if(Class->size < sizeof(cxml_handler_t))
        size = sizeof(cxml_handler_t);
    else
        size = Class->size;
    if(Class->alloc){
        h = Class->alloc(size);
    }else{
        h = malloc(size);
    }
    cxml_handler_init(h, Class);
    return h;
}

void cxml_handler_close(void * const p)
{
    cxml_handler_t * h = (cxml_handler_t*)p;
    if(h->WaitTag) {
        h->WaitTag = NULL;
    }
    h->File = NULL;
    h->Version = NULL;
    h->Encoding = NULL;
}

void   cxml_handler_delete( void * const p )
{
    cxml_free_f * f_free;
    cxml_handler_t * h = (cxml_handler_t*)p;
    f_free = (h->Class->free) ? h->Class->free : free;
    cxml_handler_close(p);
    f_free(p);
}

void cxml_wait_for_close(void * const p, cxml_tag_t * const tag)
{
    cxml_handler_t * h = (cxml_handler_t*)p;
    h->WaitTag = tag->name;
}


cxml_tag_t * cxml_tag_new(void * const p, const cxml_tag_type type, const char * const name)
{
    cxml_tag_t * t;
    cxml_handler_t * h = (cxml_handler_t*)p;

    if(h->Class->tag_alloc){
        t = h->Class->tag_alloc(sizeof(cxml_tag_t));
    }else{
        t = cxml_alloc(h, sizeof(cxml_tag_t));
    }
    t->ucount = 1;
    t->type = type;
    t->name = name;
    t->attributes = NULL;
    return t;
}

void       cxml_tag_free(void * const p, cxml_tag_t * const t)
{
	if(0 == CXML_DEC(&t->ucount)){
        cxml_handler_t * h = (cxml_handler_t*)p;
        cxml_attr_t * a = t->attributes;
        while(a){
            cxml_attr_t * n = a->next;
            cxml_attr_free(h, a);
            a = n;
        }
        if(h->Class->tag_free){
            h->Class->tag_free(t);
        }else if(h->Class->free){
            h->Class->free(t);
        }else{
            free(t);
        }
    }
}

cxml_tag_t * cxml_tag_use(cxml_tag_t * const t)
{
	CXML_INC(&t->ucount);
    return t;
}

cxml_attr_t * cxml_attr_new(void * const p, const char * const name)
{
    cxml_attr_t * a = cxml_alloc(p, sizeof(cxml_attr_t));
    a->name = name;
    a->value = NULL;
    return a;
}

void cxml_attr_free(void * const p, cxml_attr_t * const a)
{
    cxml_free(p, a);
}

int cxml_tag_is(const cxml_tag_t * const tag, const char * const tagname)
{
    return 0 == strcmp(tag->name, tagname);
}

static inline cxml_attr_t * cxml_tag_attr_find(cxml_tag_t * const tag, const char * const key)
{
    cxml_attr_t * a = tag->attributes;
    while(a && strcmp(a->name, key))
        a = a->next;
    return a;
}

int cxml_tag_has_attr(cxml_tag_t * const tag, const char * const key)
{
    return cxml_tag_attr_find(tag, key) ? 1 : 0;
}

const char * cxml_tag_attr_value(cxml_tag_t * const tag, const char * const key)
{
    cxml_attr_t * a = cxml_tag_attr_find(tag, key);
    if(a){
        return a->value;
    }
    return NULL;
}

int cxml_tag_attr_boolean(cxml_tag_t * const tag, const char * const key)
{
    cxml_attr_t * a = cxml_tag_attr_find(tag, key);
    if(a && a->value){
        const char * p = a->value;
        if(isdigit(*p)){
            while(*p == '0')p++;
            return isdigit(*p) ? 1 : 0 ;
        }

        if(0 == strcmp(p, "true") ||
           0 == strcmp(p, "yes") ||
           0 == strcmp(p, "on")){
            return 1;
        }
    }
    return 0;
}

int  cxml_tag_attr_int(cxml_tag_t * const tag, const char * const key)
{
    int ret=-1;
    cxml_attr_t * a = cxml_tag_attr_find(tag, key);
    if(a && a->value){
        char *e;
		const char *b = a->value;
		while(isspace(*b))b++;
		if(*b){
			ret = strtol(b, &e, 0);
			while(isspace(*e))e++;
			if(*e != 0){
				ret = -1;
			}
        }
    }
    return ret;
}

uint cxml_tag_attr_uint(cxml_tag_t * const tag, const char * const key)
{
    uint ret=(uint)-1;
    cxml_attr_t * a = cxml_tag_attr_find(tag, key);
    if(a && a->value){
        char *e;
		const char *b = a->value;
		while(isspace(*b))b++;
		if(*b){
			ret = strtoul(b, &e, 0);
			while(isspace(*e))e++;
			if(*e != 0){
				ret = (uint)-1;
			}
		}
    }
    return ret;
}

//typedef int (*cxml_tag_f)  (cxml_handler_t* const h, cxml_tag_t * const tag);

static int cxml_call_handler(cxml_tag_f * handler, cxml_handler_t * const h, cxml_tag_t * const tag)
{
#ifdef DEBUG_XML_STRUCTURE
	printf("<%s%s>\n", (tag->type&CXML_TAG_CLOSE)?"/":"",tag->name);
#endif	
	return handler ? handler(h, tag) : 0;
}

static int cxml_handler_post_text(cxml_handler_t * const h, char* b, char* e)
{
    int ret = 0;
    if(e>b){
        if(h->Class->text){
            char ch = *e;
            *e = 0;
            ret = cxml_text_decode(h, b, e-b);
            if(0 <= ret ){
                ret = h->Class->text(h, b, ret);
            }
            *e = ch;
        }
    }
    ASSERT_RETURN(ret);
}

static int istagend(uint const type, const char* ch)
{
    switch(type){
    case CXML_TAG_SYSTEM:
        return (*ch == '?' && ch[1] == '>');
    case CXML_TAG_COMMENT:
        return (ch[0] == '-' && ch[1] == '-' && ch[2] == '>');
    default:
        return ch[0] == '>' || (ch[0] == '/' && ch[1] == '>');
    }
}

static const char * _alnum =
"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
"abcdefghijklmnopqrstuvwxyz"
"1234567890"
"_.-:";

static int isxmlalnum(const char ch)
{
    return strchr(_alnum, ch) ? 1 : 0;
}

static int cxml_handler_parse_tag(cxml_handler_t * h, cxml_tag_t * * const tag, char * * ptr)
{
    char *b, *e;
    char * tend = NULL;
    char * aend = NULL;
    char * vend = NULL;
    cxml_tag_t  * t = NULL;
    cxml_attr_t * a = NULL;
    uint type = CXML_TAG_OPEN;

    b = (*ptr)+1;
    if (b[0] == '!' && b[1] == '-' && b[2] == '-'){
        b+=3;
        e = b;
        while(*b && !istagend(type, b))b++;
        if(*b == 0) ASSERT_GOTO(err);
        tend = b;
        t=cxml_tag_new(h, CXML_TAG_COMMENT, e);
    }else{
        if(*b == '/'){
            type = CXML_TAG_CLOSE;
            b++;
        } else if (*b == '?'){
            type = CXML_TAG_SYSTEM;
            b++;
        } else {
            type = CXML_TAG_OPEN;
        }
        if(0==*b || isspace(*b)) goto err;
        e = b;
        do{
            if(isspace(*e))break;
            if(0==*e) ASSERT_GOTO(err);
            if(istagend(type, e))break;
            e++;
        }while(1);
        if(e>b){
            /* b:e - tag name */
            tend = e;
            while(*e && isspace(*e))e++;
            t = cxml_tag_new(h, type, b);
            b = e;
            /* parse attributes */
            while(*b && !istagend(type, b)){
                while(*b && isspace(*b))b++;
                e = b;
                while(isxmlalnum(*e))e++;
                if(e>b){
                    if(aend) *aend = 0;
                    aend = e;
                    while(*e && isspace(*e))e++;
                    a = cxml_attr_new(h, b);
                    if(*e == '='){
                        e++;
                        b = e;
                        while(*b && isspace(*b))b++;
                        if(*b == '"'){
                            b++;
                            e = strchr(b, '"');
                            if(!e) ASSERT_GOTO(err);
                            *e = 0;
                            if(0 > cxml_text_decode(h, b, e-b))
                                ASSERT_GOTO(err);
                            e++;
                            a->value = b;
                        }else{
                            e = b;
                            while(*e && !istagend(type, e) && !isspace(*e))e++;
                            if(0==*e)ASSERT_GOTO(err);
                            if(vend){
                                *vend = 0;
                                if(0 > cxml_text_decode(h, (char*)t->attributes->value, vend - t->attributes->value))
                                    ASSERT_GOTO(err);
                            }
                            vend = e;
                            a->value = b;
                        }
                    }else{
                        /* EXTENSION */
                        /* boolean values */
                        a->value = "true";
                        vend = NULL;
                    }
                    a->next = t->attributes;
                    t->attributes = a;
                    a = NULL;
                }
                b = e;
            }
        }
    }
    while(*b && *b != '>') b++;
    if(*(b-1) == '/' && t->type == CXML_TAG_OPEN){
        t->type = CXML_TAG_AUTOCLOSE;
    }
    *tend = 0;
    if(aend) *aend = 0;
    if(vend){
        *vend = 0;
        if(0 > cxml_text_decode(h, (char*)t->attributes->value, vend - t->attributes->value))
            ASSERT_GOTO(err);
    }
    *tag = t;
    *ptr = b;
    return 0;
err:
    if(a){
        cxml_attr_free(h, a);
    }
    if(t){
        cxml_tag_free(h, t);
    }
    return -1;
}

static int cxml_equal_name(const char * p1, const char * p2)
{
    int rc;

    if(0 == *p1)
        return 1;

    do{
        if(!isxmlalnum(*p2)){
            rc = -1;
            break;
        }
        rc = *p1 - *p2;
        p1++;
        p2++;
    }while(rc == 0 && *p1);
    if(rc == 0 && !isxmlalnum(*p2)){
        return 1;
    }
    return 0;
}

static int cxml_handler_parse_buffer(void * const ph, char* const text)
{
    int ret = 0;
    char *b, *e;
    cxml_handler_t   * h = (cxml_handler_t*)ph;
    b = text;

    /* strip leading spaces */
    while(*b && isspace(*b))b++;
    e = b;
    while(*e){
        cxml_tag_t * tag  = NULL;
        char * q;
        if(*e == '<'){
            q=e;
            if(h->WaitTag){
                if(e[1] != '/' || !cxml_equal_name(h->WaitTag, e+2)){
                    e++;
                    continue;
                }
                h->WaitTag = NULL;
            }
            ret = cxml_handler_parse_tag(h, &tag, &e);
            if(ret < 0 ){
                return ret;
            }
            if(q>b){
                /* text exist */
                if(h->Class->text){
                    ret = cxml_handler_post_text(h, b, q);
                    if(ret < 0) ASSERT_RETURN(-1);
                }
            }
            ret = 0;
            switch(tag->type){
            case CXML_TAG_AUTOCLOSE: /* autoclose */
				ret = cxml_call_handler(h->Class->tag_begin, h, tag);
                if(ret < 0) break;
            case CXML_TAG_CLOSE: /* close */
				ret = cxml_call_handler(h->Class->tag_end, h, tag);
//                if(h->Class->tag_end){
//                    ret = h->Class->tag_end(h, tag);
//                }
                break;
            case CXML_TAG_SYSTEM:
                if(cxml_tag_is(tag, "xml")){
                    /* fill in version and encoding */
                    h->Version = cxml_tag_attr_value(tag, "version");
                    h->Encoding = cxml_tag_attr_value(tag, "encoding");
                }
            case CXML_TAG_OPEN : /* opening */
            default:
				ret = cxml_call_handler(h->Class->tag_begin, h, tag);
//                if(h->Class->tag_begin){
//                   ret = h->Class->tag_begin(h, tag);
//                }
                break;
            };
            cxml_tag_free(h, tag);
            if(ret!=0) ASSERT_RETURN(ret);
            b=e+1;
        }
        e++;
    }
    if(e > b){
        ret = cxml_handler_post_text(h, b, e);
    }
    return ret;
}

int    cxml_handler_parse(void * const ph, char* const text)
{
    int ret = 0;
    cxml_handler_t   * h    = (cxml_handler_t*)ph;
    if(h->Class->doc_begin){
        ret = h->Class->doc_begin((cxml_handler_t*) h, 0);
    }

    if(ret >= 0){
        ret = cxml_handler_parse_buffer(ph, text);
    }

    if(h->Class->doc_end){
        ret = h->Class->doc_end((cxml_handler_t*) h, ret);
    }
    return ret;
}

int cxml_handler_parse_stream(void * const ph, FILE * f)
{
    char *buf;
    long fsize;
    size_t rsize;
    int ret = -1;

    cxml_handler_t * h    = (cxml_handler_t*)ph;

    if(fseek(f, 0, SEEK_END)){
		perror("fseek");
        return -1;
    }
    if( 0 > (fsize = ftell(f))){
		perror("ftell");
        return -1;
    }
    if(fsize > 0xFFFF){
        return -1;
    }
    if(fseek(f, 0, SEEK_SET)){
		perror("fseek");
        return -1;
	}
	
    buf = cxml_alloc(h, fsize+1);
    if(NULL == buf){
        return -1;
    }

    rsize = fread(buf, 1, fsize, f);
    if(rsize == (size_t)fsize){
        ret = 0;
        if(h->Class->doc_begin){
            ret = h->Class->doc_begin((cxml_handler_t*) h, 0);
        }
        buf[rsize] = 0;
        if(ret >= 0){
            ret = cxml_handler_parse_buffer(ph, buf);
        }
        if(h->Class->doc_end){
            ret = h->Class->doc_end((cxml_handler_t*) h, ret);
        }
    }
    cxml_free(h, buf);
    ASSERT_RETURN(ret);
}

int cxml_handler_parse_file(void * const ph, const char * file)
{
    FILE * f;
    int ret = -1;
    f = fopen(file, "r");
    if(f){
        cxml_handler_t * h    = (cxml_handler_t*)ph;
        h->File = file;
        ret = cxml_handler_parse_stream(ph, f);
        fclose(f);
    }
    return ret;
}

void * cxml_alloc(void * const p, unsigned int size)
{
    void * ptr;
    cxml_handler_t * h = (cxml_handler_t*)p;
    if(h && h->Class->alloc){
        ptr = h->Class->alloc(size);
    }else{
        ptr = malloc(size);
    }
    return ptr;
}

void   cxml_free(void * const p, void const * ptr)
{
    cxml_handler_t * h = (cxml_handler_t*)p;
    if(h && h->Class->free){
        h->Class->free((void*)ptr);
    }else{
        free((void*)ptr);
    }
}

#ifdef DEBUG
int cxml_checkassert(int rc)
{
    if(rc == -1){
		cxml_assert();
    }
    return rc;
}
void cxml_assert(void)
{
    raise(SIGINT);
}
#endif
