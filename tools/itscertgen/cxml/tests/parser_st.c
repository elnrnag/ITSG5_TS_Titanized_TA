/*********************************************************************
######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2008 - 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
*********************************************************************/
#include "../cxml.h"
#include <stdio.h>
#include <stdlib.h>

static unsigned int  deep = 0;
static int  cxml_Doc_Begin(cxml_handler_t* const h, int rc)
{
    printf("%s: Document begin\n", h->File);
    deep = 0;
    return 0;
}

static int  cxml_Doc_End(cxml_handler_t* const h, int rc)
{
    printf("%s: Document end, rc=%d\n", h->File, rc);
    return rc;
}

static int root_tag1_handler(cxml_handler_t* const h, cxml_tag_t * const tag);
static int root_tag2_handler(cxml_handler_t* const h, cxml_tag_t * const tag);
static int root_tag3_handler(cxml_handler_t* const h, cxml_tag_t * const tag);
static int root_unknown_handler(cxml_handler_t* const h, cxml_tag_t * const tag);
static int root_system_handler(cxml_handler_t* const h, cxml_tag_t * const tag);
static int tag1_tag_12_handler(cxml_handler_t* const h, cxml_tag_t * const tag);
static int tag1_tag_13_handler(cxml_handler_t* const h, cxml_tag_t * const tag);
static int tag1_unknown_handler(cxml_handler_t* const h, cxml_tag_t * const tag);
static int tag2_tag23_handler(cxml_handler_t* const h, cxml_tag_t * const tag);
static int tag2_root_handler(cxml_handler_t* const h, cxml_tag_t * const tag);

static int root_text      (cxml_handler_t* const h, char * const text);
static int tag1_text      (cxml_handler_t* const h, char * const text);
static int tag2_text      (cxml_handler_t* const h, char * const text);
static int tag12_text     (cxml_handler_t* const h, char * const text);
static int tag13_text     (cxml_handler_t* const h, char * const text);
static int default_text   (cxml_handler_t* const h, char * const text);

static cxml_handler_class Class = {
    .size = sizeof(cxml_handler_t),
    .doc_begin = cxml_Doc_Begin,
    .doc_end = cxml_Doc_End,
};

static const cxml_taghandler_t h_tag1[];
static const cxml_taghandler_t h_tag2[];

static const cxml_taghandler_t h_root[] = {
    {"tag1",        root_tag1_handler,    tag1_text, h_tag1 },
    {"tag2",        root_tag2_handler,    tag2_text, h_tag2 },
    {"tag3",        root_tag3_handler,    NULL,      NULL   },
    {CXML_HSYSTEM,  root_system_handler,  NULL,      NULL   },
    {CXML_HDEFAULT, root_unknown_handler, root_text, NULL   },
    {NULL, NULL, NULL, NULL}
};

static const cxml_taghandler_t h_tag1[] = {
    {"tag12", tag1_tag_12_handler, tag12_text, h_tag2 },
    {"tag13", tag1_tag_13_handler, tag13_text, NULL   },
    {CXML_HDEFAULT, tag1_unknown_handler,  default_text, NULL },
    {NULL, NULL, NULL, NULL}
};

static const cxml_taghandler_t h_tag2[] = {
    {"tag23", tag2_tag23_handler, NULL, NULL },
    {"root",  tag2_root_handler,  NULL, h_root },
    {NULL, NULL, NULL, NULL}
};

int main(int argc, char** argv)
{
    int i;
    cxml_handler_t * h;
    h = cxml_st_handler_new(&Class, h_root);
    for(i=1; i<argc; i++){
        cxml_handler_parse_file(h, argv[i]);
    }
    cxml_st_handler_delete(h);
    return 0;
}

static const char* _tag_prefix[] = { "?", "", "/", "", "!--" };
static const char* _tag_suffix[] = { "?", "", "",  "/", "--" };

static int _cxml_echo_tag(const char * func, cxml_tag_t * const tag)
{
    printf("TAG : <%s%s%s> Handler:%s\n",
           _tag_prefix[tag->type],
           tag->name,
           _tag_suffix[tag->type],
           func);
    return 0;
}
static int root_tag1_handler(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    return _cxml_echo_tag(__func__, tag);
}
static int root_tag2_handler(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    return _cxml_echo_tag(__func__, tag);
}
static int root_tag3_handler(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    return _cxml_echo_tag(__func__, tag);
}
static int tag1_tag_12_handler(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    return _cxml_echo_tag(__func__, tag);
}
static int tag1_tag_13_handler(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    return _cxml_echo_tag(__func__, tag);
}
static int tag2_tag23_handler(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    return _cxml_echo_tag(__func__, tag);
}
static int tag2_root_handler(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    return _cxml_echo_tag(__func__, tag);
}
static int _cxml_echo_text(cxml_handler_t* const h, const char * func, char * const text)
{
    const cxml_tag_t * tag = cxml_st_current_tag (h);

    printf("TEXT: <%s%s%s> Handler:%s, Data={%s}\n",
           _tag_prefix[tag->type],
           tag->name,
           _tag_suffix[tag->type],
           func, text);
    return 0;
}

static int default_text   (cxml_handler_t* const h, char * const text)
{
    return _cxml_echo_text(h, __func__, text);
}

static int root_text      (cxml_handler_t* const h, char * const text)
{
    return _cxml_echo_text(h, __func__, text);
}
static int tag1_text     (cxml_handler_t* const h, char * const text)
{
    return _cxml_echo_text(h, __func__, text);
}
static int tag2_text     (cxml_handler_t* const h, char * const text)
{
    return _cxml_echo_text(h, __func__, text);
}
static int tag12_text     (cxml_handler_t* const h, char * const text)
{
    return _cxml_echo_text(h, __func__, text);
}
static int tag13_text     (cxml_handler_t* const h, char * const text)
{
    return _cxml_echo_text(h, __func__, text);
}
static int root_unknown_handler(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    return _cxml_echo_tag(__func__, tag);
}
static int root_system_handler(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    return _cxml_echo_tag(__func__, tag);
}
static int tag1_unknown_handler(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    return _cxml_echo_tag(__func__, tag);
}
