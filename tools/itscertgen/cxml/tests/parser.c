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
static int  cxml_Test_Begin(cxml_handler_t* const h, int dummy)
{
    printf("Document begin\n");
    deep = 0;
    return 0;
}

static int  cxml_Test_End(cxml_handler_t* const h, int rc)
{
    printf("Document end, rc=%d\n", rc);
    return 0;
}

static void print_tag(cxml_handler_t* const h, const cxml_tag_t * const tag)
{
    cxml_attr_t * a;
    printf("<");
    switch(tag->type){
    case CXML_TAG_CLOSE:
        printf("/");
        break;
    case CXML_TAG_SYSTEM:
        printf("?");
        break;
    case CXML_TAG_COMMENT:
        printf("--");
        break;
    default:
        break;
    };
    printf("%s", tag->name);

    a = tag->attributes;
    while(a){
        if(a->value){
            char * v;
            int ret = cxml_text_encode(h, &v, a->value, -1);
            if(ret >= 0){
                printf(" %s=\"%s\"", a->name, v);
                cxml_free(h, v);
            }
        }else{
            printf(" %s", a->name);
        }
        a = a->next;
    }
    switch(tag->type){
    case CXML_TAG_AUTOCLOSE:
        printf("/");
        break;
    case CXML_TAG_SYSTEM:
        printf("?");
        break;
    case CXML_TAG_COMMENT:
        printf("--");
        break;
    default:
        break;
    };
    printf(">");
}

static int  cxml_Test_Tag_Begin(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    print_tag(h, tag);
    return 0;
}

static int  cxml_Test_Tag_End(cxml_handler_t* const h, cxml_tag_t * const tag)
{
    if(tag->type != CXML_TAG_AUTOCLOSE){
        print_tag(h, tag);
    }
    return 0;
}

static int  cxml_Test_Text(cxml_handler_t* const h, char * const text)
{
    if(text){
        char * ptr;
        int ret = cxml_text_encode(h, &ptr, text, -1);
        if(ret >= 0){
            printf("%s", ptr);
            cxml_free(h, ptr);
        }
    }
    return 0;
}

static cxml_handler_class Class = {
    sizeof(cxml_handler_t),
    cxml_Test_Begin,     cxml_Test_End,
    cxml_Test_Tag_Begin, cxml_Test_Tag_End,
    cxml_Test_Text
};

int main(int argc, char** argv)
{
    int i;
    cxml_handler_t * h;
    h = cxml_handler_new(&Class);
    for(i=1; i<argc; i++){
        int rc = cxml_handler_parse_file(h, argv[i]);
        printf("Parsing Done. rc=%d\n", rc);
    }
    cxml_handler_delete(h);
    return 0;
}
