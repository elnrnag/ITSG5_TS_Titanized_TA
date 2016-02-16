/*********************************************************************
######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2008 - 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
*********************************************************************/

#ifndef cxml_h
#define cxml_h

#ifdef __cplusplus
extern "C" {
#endif

#if defined(_DEBUG) && !defined(DEBUG)
#define DEBUG _DEBUG
#endif

	typedef struct cxml_attr_t   cxml_attr_t;
	typedef struct cxml_tag_t    cxml_tag_t;
	struct cxml_attr_t
	{
		cxml_attr_t * next;
		const char  * name;
		const char  * value;
	};

	typedef enum {
		CXML_TAG_SYSTEM = 0,
		CXML_TAG_OPEN,
		CXML_TAG_CLOSE,
		CXML_TAG_AUTOCLOSE,
		CXML_TAG_COMMENT,
		/* internal tag types */
		CXML_TAG_CDATA = 8,
		CXML_TAG_ENTITY = 12,
	}cxml_tag_type;

#define cxml_tag_is_open(tag) (tag->type & CXML_TAG_OPEN)
#define cxml_tag_is_close(tag) (tag->type & CXML_TAG_CLOSE)

	struct cxml_tag_t
	{
		int           ucount;
		cxml_tag_type type;
		const char  * nspace;
		const char  * name;
		cxml_attr_t * attributes;
	};

	typedef struct cxml_handler_t      cxml_handler_t;
	typedef struct cxml_handler_class  cxml_handler_class;
	typedef struct cxml_entity_t       cxml_entity_t;

	typedef int (cxml_doc_f)(cxml_handler_t* const h, int rc);
	typedef int (cxml_tag_f)(cxml_handler_t* const h, cxml_tag_t * const tag);
	typedef int (cxml_text_f)(cxml_handler_t* const h, char * const text, int length);

	typedef void * (cxml_alloc_f)(unsigned int);
	typedef void   (cxml_free_f)(void *);

#define CXML_RETURN_OK    0
#define CXML_RETURN_ERROR -1
#define CXML_RETURN_STOP  -2
	
	struct cxml_handler_t
	{
		const cxml_handler_class * Class;
		const char               * File;
		const char               * Version;
		const char               * Encoding;
		const char               * WaitTag;
		cxml_entity_t            * Entities;
	};

	struct cxml_handler_class
	{
		unsigned int        size;
		cxml_doc_f        * doc_begin;
		cxml_doc_f        * doc_end;
		cxml_tag_f        * tag_begin;
		cxml_tag_f        * tag_end;
		cxml_text_f       * text;

		/* Allocation halpers.
		 * Can be NULL to use default ones */
		cxml_alloc_f      * tag_alloc;
		cxml_free_f       * tag_free;
		cxml_alloc_f      * alloc;
		cxml_free_f       * free;
	};

	struct cxml_entity_t
	{
		cxml_entity_t * next;
		int             nlen;
		const char    * name;
		int             vlen;
		char            value[];
	};

	void            cxml_handler_init(void * const h, const cxml_handler_class * const Class);
	void *          cxml_handler_new(const cxml_handler_class * const Class);
	void            cxml_handler_delete(void * const h);
	void            cxml_handler_close(void * const h);

	int             cxml_handler_parse(void * const h, char* const text);
	int             cxml_handler_parse_file(void * const h, const char * file);
#if defined(SEEK_SET) || defined (_FILE_DEFINED)
#define _CXML_HANDLER_PARSE_STREAM_DEFINED
	int             cxml_handler_parse_stream(void * const h, FILE * f);
#endif

	/* Internal functions for tags manipulations */
	cxml_tag_t *    cxml_tag_new(void * const h, const cxml_tag_type type, const char * const name);
	cxml_tag_t *    cxml_tag_use(cxml_tag_t * const tag);
	void            cxml_tag_free(void * const h, cxml_tag_t * const tag);
	void            cxml_wait_for_close(void * const p, cxml_tag_t * const tag);

	cxml_attr_t *   cxml_attr_new(void * const h, const char * const name);
	void            cxml_attr_free(void * const h, cxml_attr_t * const a);

	int             cxml_tag_is(const cxml_tag_t * const tag, const char * const tagname);
	int             cxml_tag_has_attr(cxml_tag_t * const tag, const char * const key);
	const char *    cxml_tag_attr_value(cxml_tag_t * const tag, const char * const key);
	int             cxml_tag_attr_int(cxml_tag_t * const tag, const char * const key);
	unsigned int    cxml_tag_attr_uint(cxml_tag_t * const tag, const char * const key);
	int             cxml_tag_attr_boolean(cxml_tag_t * const tag, const char * const key);

	int             cxml_handler_add_entity(void * const h, const char * const name, char * const value, const int value_len);
	int             cxml_handler_add_entity_from_tag(void * const h, cxml_tag_t * const tag);
	const cxml_entity_t *
		cxml_handler_find_entity(void * const h, const char * s, int maxlen);
	int             cxml_text_decode(void * const handler, char * const src, int const srclen);
	int             cxml_text_encode(void * const handler, char * * const p_dst,
		const char * const src, int const srclen);

	/** Utility function to process basic DCMI encoding */
	int             cxml_to_dcmi(char ** const pdst, const char * const src, int const len);

	/** XML states and tag handlers support */
	typedef struct cxml_taghandler_t cxml_taghandler_t;

	struct cxml_taghandler_t
	{
		const char              * tagname;
		cxml_tag_f              * on_tag;
		cxml_text_f             * on_text;
		const cxml_taghandler_t * childs;
	};
#define CXML_HDEFAULT   ((const char *)1)
#define CXML_HCOMMENT   ((const char *)2)
#define CXML_HSYSTEM    ((const char *)3)

	void * cxml_st_handler_new(const cxml_handler_class * const Class,
		const cxml_taghandler_t * const Handlers);
	void   cxml_st_handler_delete(void * const h);

	const cxml_tag_t * cxml_st_current_tag(void * const h);

	void * cxml_alloc(void * const h, unsigned int size);
	void   cxml_free(void * const h, void const * ptr);

#ifdef DEBUG
#define ASSERT_RETURN(RC)  return cxml_checkassert(RC)
#define ASSERT_GOTO(LABEL) do {cxml_assert(); goto LABEL; }while(0)
	void   cxml_assert(void);
	int    cxml_checkassert(int rc);
#else
#define ASSERT_RETURN(RC)  return RC
#define ASSERT_GOTO(LABEL) goto LABEL
#endif
#endif

#ifdef __cplusplus
}
#endif
