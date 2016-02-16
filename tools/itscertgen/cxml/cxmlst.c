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

#if __SIZEOF_POINTER__ == __SIZEOF_LONG__
typedef unsigned long intpointer_t;
#elif __SIZEOF_POINTER__ == __SIZEOF_LONG_LONG__
typedef unsigned long long intpointer_t;
#else
typedef unsigned int intpointer_t;
#endif

typedef struct cxmlst_tag_t cxmlst_tag_t;

struct cxmlst_tag_t
{
    cxml_tag_t                tag;
    cxmlst_tag_t            * parent;
    cxml_tag_f              * on_close;
    cxml_text_f             * p_text;
    const cxml_taghandler_t * subhandlers;
};

typedef struct cxmlst_handler_t
{
    const cxml_taghandler_t  * handlers;
    cxmlst_tag_t             * stack;
    cxml_handler_t             h;
}cxmlst_handler_t;

static cxmlst_handler_t * to_cxmlst_handler(void * const p)
{
    char * ptr = p;
    ptr -= sizeof(cxmlst_handler_t)-sizeof(cxml_handler_t);
    return (cxmlst_handler_t *)ptr;
}
/*
#define to_cxmlst_handler(p) \
    ((cxmlst_handler_t*) \
        ((char*)p)-(sizeof(cxmlst_handler_t)-sizeof(cxml_handler_t)))
*/
static int _cxmlst_tag_begin(cxml_handler_t* const h, cxml_tag_t * const tag);
static int _cxmlst_tag_end  (cxml_handler_t* const h, cxml_tag_t * const tag);

static void * _cxmlst_tag_alloc(unsigned int size)
{
    if(size < sizeof(cxmlst_tag_t))
        size = sizeof(cxmlst_tag_t);
    return malloc(size);
}
static void   _cxmlst_tag_free (void * p)
{
    free((cxmlst_tag_t*)p);
}

void * cxml_st_handler_new(const cxml_handler_class * const Class,
                           const cxml_taghandler_t * const Handlers)
{
    cxmlst_handler_t   * h;
    cxml_handler_class * cl;
    size_t hsize = Class->size + sizeof(cxmlst_handler_t) - sizeof(cxml_handler_t);
    if(hsize < sizeof(cxmlst_handler_t)) hsize = sizeof(cxmlst_handler_t);

    cl = malloc(sizeof(cxml_handler_class));
    memcpy(cl, Class, sizeof(cxml_handler_class));
    cl->tag_begin = _cxmlst_tag_begin;
    cl->tag_end   = _cxmlst_tag_end;
    cl->tag_alloc = _cxmlst_tag_alloc;
    cl->tag_free  = _cxmlst_tag_free;

    h = malloc(hsize);
    cxml_handler_init(&h->h, cl);
    h->handlers = Handlers;
    h->stack = NULL;
    return &h->h;
}

void   cxml_st_handler_delete(void * const p)
{
    cxmlst_handler_t * h = to_cxmlst_handler(p);
    cxml_handler_class * cl = (cxml_handler_class *)h->h.Class;
    while(h->stack){
        cxmlst_tag_t * t = h->stack;
        h->stack = t->parent;
        cxml_tag_free(p, &t->tag);
    }
    cxml_handler_close(p);
    free(h);
    free(cl);
}

static int _cxmlst_tag_begin(cxml_handler_t* const p, cxml_tag_t * const tag)
{
    int rc = -1;
    cxmlst_handler_t * h = to_cxmlst_handler(p);
    const cxml_taghandler_t * i;
    const cxml_taghandler_t * found = NULL;

    if(NULL == h->handlers){
        return -1;
    }


    i=h->handlers;
    for(;i->tagname; i++){
        intpointer_t nll = (intpointer_t)i->tagname;
        if(nll <= 10){
            if(i->tagname == CXML_HDEFAULT){
                found = i;
            }else if(i->tagname == CXML_HCOMMENT){
                if(tag->type == CXML_TAG_COMMENT) found = i;
            }else if(i->tagname == CXML_HSYSTEM){
                if(tag->type == CXML_TAG_SYSTEM) found = i;
            }else{
                return -1;
            }
        }else{
			if(tag->type != CXML_TAG_COMMENT){
				if(0==strcmp(tag->name, i->tagname)){
					found = i;
					break;
				}
            }
        }
    }

    if(found) {
        rc = found->on_tag ? found->on_tag(&h->h, tag) : 0;

        if( rc >= 0 ){
            /* store tag in the stack */
            cxmlst_tag_t * tst = (cxmlst_tag_t *)tag;
            cxml_handler_class * cl = (cxml_handler_class *)h->h.Class;
            cxml_tag_use(tag);
            tst->subhandlers = h->handlers;
            tst->parent      = h->stack;
            tst->p_text      = cl->text;
            h->stack         = tst;

            /* update class fields */
            /* check if this tag have a non-xml body */
            if(NULL == found->childs &&
               tag->type == CXML_TAG_OPEN){
                cxml_wait_for_close(&h->h, tag);
            }
            h->handlers   = found->childs;
            tst->on_close = found->on_tag;
            cl->text      = found->on_text;
        }
    }else{
		if(tag->type == (tag->type & ~0x3)){
			rc = 0;
		}
	}
    return rc;
}

static int _cxmlst_tag_end  (cxml_handler_t* const p, cxml_tag_t * const tag)
{
    int rc;
    cxml_handler_class * cl = (cxml_handler_class *)p->Class;
    cxmlst_handler_t * h = to_cxmlst_handler(p);
    cxmlst_tag_t *parent, *cur;

    /* check for empty stack */
    if(h->stack == NULL)
        return -1;

    cur = h->stack;
    parent = cur->parent;

    /* check the stack top for the same tag */
    if(strcmp(tag->name, cur->tag.name)){
        return -1;
    }

    if(tag->type == CXML_TAG_AUTOCLOSE){
        tag->type = CXML_TAG_CLOSE;
    }

    h->handlers = cur->subhandlers;
    cl->text    = cur->p_text;
    if(cur->on_close)
        rc = cur->on_close(p, tag);
    else
        rc = 0;
    h->stack = parent;
    cxml_tag_free(p, &cur->tag);
    return rc;
}

const cxml_tag_t * cxml_st_current_tag (void * const p)
{
    cxmlst_handler_t * h = to_cxmlst_handler(p);
    return (cxml_tag_t*)h->stack;
}
