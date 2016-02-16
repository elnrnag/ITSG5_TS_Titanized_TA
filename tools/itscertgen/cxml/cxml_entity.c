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
#include <ctype.h>

static cxml_entity_t * cxml_entity_new(void * const p, const char * const name, char * const value, const int value_len)
{
    cxml_entity_t * e;

    e = cxml_alloc(p, sizeof(cxml_entity_t) + value_len + 1);
    e->next  = NULL;
    e->name  = name;
    e->nlen  = strlen(name);
    if(value && value_len) {
        memcpy(e->value, value, value_len);
        e->value[value_len] = 0;
        e->vlen = cxml_text_decode(p, e->value, value_len);
        if(e->vlen < 0){
            cxml_free(p, e);
            return NULL;
        }
        e->value[e->vlen] = 0;
    }
    return e;
}

static cxml_entity_t * _defEnt = NULL;
void cxml_entity_append(cxml_handler_t * const h, cxml_entity_t * const e)
{
	cxml_entity_t **r, *c, *p;
	if (h) r = & h->Entities;
	else   r = & _defEnt;
	c = *r; p = NULL;
    int found = 0;
    while(c){
        if(!found && 0 == strcmp(c->name, e->name)){
            /* remove old one */
            if(p){
                p->next = c->next;
                cxml_free(h, c);
                c = p->next;
            }else{
                *r  = c->next;
                cxml_free(h, c);
                c = *r;
            }
            found = 1;
        }
        p = c;
        c = c->next;
    }
    if(p){
        p->next = e;
    }else{
        *r = e;
    }
}

int cxml_handler_add_entity(void * const p, const char * const name, char * const value, const int value_len)
{
    cxml_entity_t  * e = cxml_entity_new(p, name, value, value_len);
    if(e){
        cxml_entity_append(p, e);
        return 0;
    }
    return -1;
}

const cxml_entity_t * cxml_handler_find_entity(void * const handler, const char * s, int maxlen)
{
	const cxml_handler_t * h = (const cxml_handler_t *)handler;
	const cxml_entity_t *c, *fc = NULL;
	c = h ? h->Entities : _defEnt;
	while (c){
		if (maxlen >= c->vlen){
			if (0 == memcmp(s, c->value, c->vlen)){
				if (fc == NULL || fc->vlen < c->vlen){
					fc = c;
				}
			}
		}
		c = c->next;
	}
	return fc;
}

void cxml_handler_add_default_entities(void * const h)
{
    cxml_handler_add_entity(h, "gt",   "&#x3E;", 6);
    cxml_handler_add_entity(h, "lt",   "&#x3C;", 6);
    cxml_handler_add_entity(h, "amp",  "&#x26;", 6);
    cxml_handler_add_entity(h, "quot", "&#x22;", 6);
    cxml_handler_add_entity(h, "apos", "&#x27;", 6);
}

int cxml_handler_add_entity_from_tag(void * const h, cxml_tag_t * const tag)
{
    char * v = NULL;
    int vlen = 0;

    if(tag->type != CXML_TAG_ENTITY){
        return -1;
    }
    if(tag->attributes){
        v = (char*)tag->attributes->value;
        if(v){
            vlen = strlen(v);
        }
    }
    return cxml_handler_add_entity(h, tag->name, v, vlen);
}
