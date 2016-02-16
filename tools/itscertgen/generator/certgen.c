/*********************************************************************
######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
*********************************************************************/
#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include "cxml/cxml.h"
#include "cshared/copts.h"
#include "cshared/cserialize.h"
#include "cshared/cstr.h"

#include "ecc_api.h"
#include "mkgmtime.h"
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <assert.h>
#include <limits.h>
#include <time.h>
#include <math.h>

//#define DEBUG_BOOKMARKS
//#define DEBUG_DATA
#define MAX_BOOKMARKS 16

typedef struct bookmark_t bookmark_t;
struct bookmark_t {
	bookmark_t * prev;
	char       * ptr;
};

typedef struct cert_cxml_handler_t
{
	cxml_handler_t  h;
	unsigned int    subject_type;
	int             signer_type;
	const char    * signer;
	int             vr_type;
	int             sa_type;
	void          * verificationKey;
	void          * encryptionKey;

	int				pk_alg;
	int				pk_sym_alg;
	int				pk_ptype;
	char          * pk_data;
	int				pk_datasize;

	char   		  * buf;
	char          * ptr;
	const char    * end;

	char          * bookmarks[MAX_BOOKMARKS];
	int             bidx;

	unsigned int    nTmpValue;
	void          * key;
}cert_cxml_handler_t;

static void bookmark_position(cert_cxml_handler_t * const h, const cxml_tag_t * tag)
{
	if (h->bidx >= MAX_BOOKMARKS){
		fprintf(stderr, "FATAL: Profile is tooooooo complicated!!!\n");
		exit(-1);
	}
	h->bookmarks[h->bidx] = h->ptr;
	h->bidx++;
	h->ptr++;
#ifdef DEBUG_BOOKMARKS
	printf("BOOKMARK 0x%08X (%s)\n", (unsigned int)h->ptr, tag->name);
#endif
}

static int apply_bookmark_size(cert_cxml_handler_t * const h, const cxml_tag_t * tag)
{
	int size, bcount;
	char * psize;
	if (h->bidx == 0){
		fprintf(stderr, "FATAL: Inconsistent bookmarks!!!\n");
		exit(-1);
	}
	psize = h->bookmarks[--h->bidx];
	size = h->ptr - psize - 1;
#ifdef DEBUG_BOOKMARKS
	printf("APPLY    0x%08X [%d] (%s)\n", (unsigned int)h->ptr, size, tag->name);
#endif
	bcount = cintx_bytecount(size);
	if (bcount == 1){
		*(unsigned char*)psize = size;
		size = 0;
	}
	else{
		memmove(psize + bcount, psize + 1, h->ptr - psize - 1);
		h->ptr += bcount - 1;
		size = cintx_write(size, &psize, psize + bcount, NULL);
	}
	return size; // size is overridden to be 0 or -1
}

static int attribute_public_key_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static int attribute_eccpoint_tag   (cxml_handler_t* const h, cxml_tag_t * const tag);
static int attribute_eccpoint_text  (cxml_handler_t* const h, char * const text, int length);
static int attribute_assurance_tag  (cxml_handler_t* const h, cxml_tag_t * const tag);
static int attribute_aid_tag        (cxml_handler_t* const h, cxml_tag_t * const tag);
static int attribute_ssp_tag        (cxml_handler_t* const h, cxml_tag_t * const tag);
static int attribute_ssp_text       (cxml_handler_t* const h, char * const text, int length);

static const cxml_taghandler_t h_publickey[] = {
	{ "eccpoint",  attribute_eccpoint_tag, attribute_eccpoint_text, NULL },
	{ "ecc-point", attribute_eccpoint_tag, attribute_eccpoint_text, NULL },
	{ "ecc_point", attribute_eccpoint_tag, attribute_eccpoint_text, NULL },
	{ NULL }
};

static const cxml_taghandler_t h_attribute[] = {
	{ "public-key", attribute_public_key_tag, NULL, h_publickey },
	{ "public_key", attribute_public_key_tag, NULL, h_publickey },
	{ "publickey",  attribute_public_key_tag, NULL, h_publickey },
	{ "eccpoint",   attribute_eccpoint_tag, attribute_eccpoint_text, NULL },
	{ "ecc-point",  attribute_eccpoint_tag, attribute_eccpoint_text, NULL },
	{ "ecc_point",  attribute_eccpoint_tag, attribute_eccpoint_text, NULL },
	{ "assurance",  attribute_assurance_tag, NULL, NULL },
    { "aid",        attribute_aid_tag,         NULL,  NULL },
    { "ssp",        attribute_ssp_tag,         attribute_ssp_text,  NULL },
    {NULL}
};

static int subject_attribute_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static const cxml_taghandler_t h_subject[] = {
    {"attribute",  subject_attribute_tag,  NULL,  h_attribute},
    {NULL}
};

static int location_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static const cxml_taghandler_t h_location_list [] = {
    {"location",   location_tag,   NULL, NULL },
    {NULL}
};

static int region_none_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static int region_circle_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static int region_rectangle_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static int region_polygon_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static int region_id_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static const cxml_taghandler_t h_restriction[] = {
    {"none",       region_none_tag,         NULL, NULL },
    {"circle",     region_circle_tag,         NULL, NULL },
    {"rectangle",  region_rectangle_tag,      NULL, h_location_list },
    {"polygon",    region_polygon_tag,        NULL, h_location_list },
    {"id",         region_id_tag,             NULL, NULL },
    {NULL}
};

static int validity_restriction_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static const cxml_taghandler_t h_validity[] = {
    {"restriction",  validity_restriction_tag,    NULL, h_restriction },
    {NULL}
};
	
static int certificate_version_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static int certificate_version_text(cxml_handler_t* const h, char * const text, int length);
static int certificate_signer_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static int certificate_signer_text(cxml_handler_t* const h, char * const text, int length);
static int certificate_subject_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static int certificate_validity_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static int certificate_signature_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static const cxml_taghandler_t h_certificate[] = {
    {"version",   certificate_version_tag, certificate_version_text, NULL },
    {"signer",    certificate_signer_tag,  certificate_signer_text,  NULL },
    {"subject",   certificate_subject_tag, NULL,  h_subject },
    {"validity",  certificate_validity_tag, NULL,  h_validity },
    {"signature", certificate_signature_tag, NULL,  h_validity },
    {NULL}
};

static int root_certificate_tag (cxml_handler_t* const h, cxml_tag_t * const tag);
static const cxml_taghandler_t h_root[] = {
    {"certificate", root_certificate_tag, NULL, h_certificate },
    {NULL}
};

static int  _Begin_Tag(cxml_handler_t* const h, cxml_tag_t * const tag);
static int  _End_Tag(cxml_handler_t* const h, cxml_tag_t * const tag);
static int  _Text(cxml_handler_t* const h, char * const text, int length);


static cxml_handler_class Class = {
    sizeof(cert_cxml_handler_t),
    NULL, NULL,
    _Begin_Tag, _End_Tag,
    _Text
};

#define STR2ENUM(N,V) _str_2_enum(N, sizeof(N)/sizeof(N[0]), V)

static int _str_2_enum(const char ** values, size_t size, const char * v)
{
	while (*v && isspace(*v))v++;
	if (*v){
		size_t i;
		for (i = 0; i<size; i++){
			if (values[i] && 0 == strcmp(values[i], v)) {
				return i;
			}
		}
		if (isdigit(*v)){
			return strtoul(v, NULL, 0);
		}
	}
	return -1;
}

#ifdef DEBUG_DATA
static void print_x(FILE * f, const char * ptr, int len)
{
	const unsigned char * e = (const unsigned char *)(ptr + len);
	for (; ptr < e; ptr++){
		unsigned char c = *ptr;
		fprintf(f, "%02X", c);
	}
}
#endif

static unsigned int _convert_time(const char * v);

static const char *_outPath = ".";
static const char *_searchPath = NULL;
static const char *_certName = NULL;
static       char *_profileName = NULL;
static const char *_signerName = NULL;
static unsigned int _defaultTime = 0;
static ecc_format   _outFormat = 0;
static const char * _verificationKey = NULL;
static const char * _decriptionKey   = NULL;
static const char * _keyPath = NULL;
static int          _reuseKeys = 0;

static long         _refLat = 0;
static long         _refLon = 0;

static const char * _cfgFile = NULL;


static int _time_option(const copt_t * opt, const char * option, const copt_value_t * value)
{
	_defaultTime = _convert_time(value->v_str);
	return (_defaultTime == (unsigned int)-1) ? -1 : 0;
}

static int _refPoint_option(const copt_t * opt, const char * option, const copt_value_t * value)
{
	char * e;
	long double lat, lon;
	lat = strtold(value->v_str, &e);
	if (*e == ':'){
		lon = strtold(e + 1, &e);
		if (*e == 0){
			if (lat <= 90.0 &&  lat >= -90.0) lat *= 10000000.0; // degree
			_refLat = (int32_t)floorl(lat);
			if (lon <= 180.0 &&  lon >= -180.0) lon *= 10000000.0; // degree
			_refLon = (int32_t)floorl(lon);
			return 0;
		}
	}
	return -1;
}

static const char * _o_formats[] = {
	"bin", "hex", "pem", NULL
};

static copt_t options [] = {
	{ "h?", "help", COPT_HELP, NULL,                 "Print this help page" },
	{ "C", "config", COPT_CFGFILE, (void*)&_cfgFile, "Config file path [no cfg file]" },
	{ "o", "out", COPT_STR, (void*)&_outPath, "Output path [current dir by default]" },
	{ "O", "format", COPT_STRENUM, (void*)_o_formats, "Output format (bin|hex|pem)[binary by default]" },
	{ "S", "certs", COPT_STR, (void*)&_searchPath, "Certificates search path [Output path by default]" },
	{ "K", "keys",  COPT_STR,  (void*)&_keyPath,    "Private key storage path [Output path by default]" },
	{ "R", "reuse", COPT_BOOL, (void*)&_reuseKeys,  "Reuse existing key pair [regenerate by default]" },
	{ "n", "name", COPT_STR, (void*)&_certName, "Certificate name (take from profile by default)" },
	{ "v", "vkey", COPT_STR, (void*)&_verificationKey, "Verification public key (generate key pair by default)" },
	{ "e", "ekey", COPT_STR, (void*)&_decriptionKey, "Decription public key (generate key pair if neccessary)" },
	{ "s", "signer", COPT_STR, (void*)&_signerName, "Signer certificate name [take from profile by default]" },
	{ "t", "reftime",     COPT_STR | COPT_CALLBACK, (void*)&_time_option, "Reference UTC time point (YYY-DD-MM) [current date]" },
	{ "l", "reflocation", COPT_STR | COPT_CALLBACK, (void*)&_refPoint_option, "Reference location in form <lat>:<lon> [0.0:0.0]" },
	//	{ "c", "chain",   COPT_BOOL , &_createChain,     "Produce a text file for ITS Test Suite" },
	{ NULL, NULL,     COPT_END,  NULL, NULL }
};

int main(int argc, char ** argv)
{
	FILE *f;
	int rc;

	cert_cxml_handler_t * h = cxml_st_handler_new(&Class, h_root);
	h->vr_type = -1;
	h->sa_type = -1;
	
	argc = coptions(argc, argv, COPT_HELP_NOVALUES , options);

    if(argc < 2){
		if(argc<0 && (0-argc)<((sizeof(options)/sizeof(options[0]))-1)){
			printf("Unknown option %s\n", argv[0-argc]);
		}
		const char * a = strrchr(argv[0], '/');
		if (a == NULL) a = argv[0];
		coptions_help(stdout, a, COPT_HELP_NOVALUES, options, "<profile> [signer]");
		return -1;
    }

	if (_searchPath == NULL) _searchPath = _outPath;
	if (_keyPath == NULL)    _keyPath    = _outPath;
	_outFormat = copts_enum_value(options, 3, _o_formats);
	if (_defaultTime == 0){
		//set it to begining of this year
		time_t t = time(NULL);
		struct tm tm;
		struct tm * ptm = gmtime(&t);
		memcpy(&tm, ptm, sizeof(tm));
		tm.tm_hour = tm.tm_min = tm.tm_sec = 0;
		tm.tm_mday = 1; tm.tm_mon = 0;
		_defaultTime = mkitstime32(&tm);
	}

	if(argc > 2){
		// set signer certificate file name
		_signerName = argv[2];
	}

    f = fopen(argv[1], "rb");
	if(f == NULL){
		fprintf(stderr, "%s: Certificate profile not found\n", argv[1]);
		return -1;
	}else{
		
		if(ecc_api_init()){
			return -1;
		}

		_profileName = cstrdup(cstrlastpathelement(argv[1]));
		if(_profileName){
			char * p = strrchr(_profileName, '.');
			if(p) *p = 0;
		}

		rc = cxml_handler_parse_stream(&h->h, f);
		if (rc == -1){
			fprintf(stderr, "%s Profile parsing error\n", argv[1]);
			return -1;
		}
		fclose(f);

		ecc_api_done();
	}
	return 0;
}

static int root_certificate_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		if(_certName == NULL){
			_certName = cxml_tag_attr_value(tag, "name");
			if(_certName == NULL){
				_certName = _profileName;
			}
		}
		h->ptr = h->buf = malloc(4096);
		h->end = h->buf + 4096;
		if (cxml_tag_attr_boolean(tag, "keep-existing")){
			FILE * f;
			char * path = h->ptr;
			cvstrncpy(path, h->end - path, _outPath, "/", _certName, ".crt", NULL);
			f = fopen(path, "r+b");
			if (f){
				fclose(f);
				free(h->buf);
				return CXML_RETURN_STOP;
			}
		}
	}else{
		FILE * f;
		char * path = h->ptr;
		char * end = cvstrncpy(path, h->end-path, _outPath, "/", _certName, ".crt", NULL);
		f = fopen(path, "wb");
		if (!f){
			fprintf(stderr, "ERROR: Certificate write failed to '%s'\n", path);
		}else{
			if (_outFormat == 0){ 
				fwrite(h->buf, 1, h->ptr - h->buf, f);
			}else{ // hex or pem format
				const unsigned char * p = (const unsigned char *)h->buf;
				for (; p < (const unsigned char *)h->ptr; p++){
					fprintf(f, "%02X", *p);
				}
			}
			fclose(f);
		}
		if (h->verificationKey){
			if (_verificationKey == NULL && _reuseKeys == 0){
				cvstrncpy(path, h->end - path, _keyPath, "/", _certName, ".vkey", NULL);
				ecc_api_key_private_save(h->verificationKey, path, _outFormat);
				cvstrncpy(path, h->end - path, _keyPath, "/", _certName, ".vpkey", NULL);
				ecc_api_key_public_save(h->verificationKey, path, _outFormat);
			}
		}
		if (h->encryptionKey){
			if (_decriptionKey == NULL && _reuseKeys == 0){
				cvstrncpy(path, h->end - path, _keyPath, "/", _certName, ".ekey", NULL);
				ecc_api_key_private_save(h->encryptionKey, path, _outFormat);
				cvstrncpy(path, h->end - path, _keyPath, "/", _certName, ".epkey", NULL);
				ecc_api_key_public_save(h->encryptionKey, path, _outFormat);
			}
		}
		free(h->buf);
	}
	return 0;
}

static int certificate_version_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		h->nTmpValue = 2;
		const char * v = cxml_tag_attr_value(tag, "version");
		if(v == NULL)v = cxml_tag_attr_value(tag, "value");
		if(v){
			while(isspace(*v))v++;
			h->nTmpValue = strtoul(v, NULL, 0);
		}
	}else{
		cint8_write(h->nTmpValue, &h->ptr, h->end, &rc);
	}
	return rc;
}

static int certificate_version_text(cxml_handler_t* const _h, char * const text, int length)
{
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if(text){
		const char * p = text;
		while(*p && isspace(*p))p++;
		if(*p) h->nTmpValue = strtoul(p, NULL, 0);
	}
	return 0;
}

static const char * _signer_types [] = {
	"self",
	"digest",
	"certificate",
	"chain",
	"other",
};

static size_t load_certificate(const char * path, char * ptr, const char * e)
{
	FILE *f;
	size_t size;
	char * cert = ptr;

	f = fopen(path, "rb");
	if (f == NULL){
		perror(path);
		return -1;
	}
	fseek(f, 0, SEEK_END);
	size = ftell(f);
	fseek(f, 0, SEEK_SET);
	if (size < 67){
		fprintf(stderr, "%s: File too small\n", path);
		return -1;
	}
	if (size > (e - cert)){
		fprintf(stderr, "%s: no enough space\n", path);
		return -1;
	}
	if (size != fread(cert, 1, size, f)){
		perror(path);
		return -1;
	}
	fclose(f);
	if (cert[0] != 2) {
		char * c = cstr_hex2bin(cert, size, cert, size);
		if (c){
			size = c - cert;
		}
	}
	return size;
}

static int certificate_signer_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	// write signer info
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		h->signer_type = 1; // digest by default
		const char * v = cxml_tag_attr_value(tag, "type");
		if(v){
			h->signer_type = STR2ENUM(_signer_types, v);
			if(h->signer_type <0){
				fprintf(stderr, "%s: Unknown signer type\n", v);
				return -1;
			}
		}
		cint8_write(h->signer_type, &h->ptr, h->end, &rc);

		if (h->signer_type > 0){
			if (_signerName){
				h->signer = _signerName;
			}
			else{
				v = cxml_tag_attr_value(tag, "name");
				if (v == NULL){
					fprintf(stderr, "%s: Signer name shall be provided\n", v);
					return -1;
				}
				h->signer = v;
			}
		}
	}else{
		// write signer info
		if (h->signer_type > 0){
			if (h->signer_type > 2){
				fprintf(stderr, "%d: signer method unsupported\n", h->signer_type);
				rc = -1;
			}
			else{
				// load signer certificate
				int plen = strlen(_searchPath) + strlen(h->signer);
				char * path = malloc(plen + 16);
				cvstrncpy(path, plen + 16, _searchPath, "/", h->signer, ".crt", NULL);
				size_t size = load_certificate(path, h->ptr, h->end);
				if (size < 0){
					fprintf(stderr, "%s: signer certificate not found or error\n", h->signer);
					rc = -1;
				}
				else{
					if (h->signer_type == 1){ // digest
						char hash[sha256_hash_size];
						// change eccpoint type of the signature to x_coordinate_only(0) 
						// to follow canonical encoding
						h->ptr[size-65] = 0;
						sha256_calculate(hash, h->ptr, size);
#ifdef DEBUG_DATA
						fprintf(stderr, "HASH (%s): ", h->signer);
						print_x(stderr, hash, sha256_hash_size);
						fprintf(stderr, "\n");
						fprintf(stderr, "DIGEST (%s): ", h->signer);
						print_x(stderr, &hash[sha256_hash_size - 8], 8);
						fprintf(stderr, "\n");
#endif
						cbuf_write(hash + sha256_hash_size - 8, 8, &h->ptr, h->end, &rc);
					}
					else {// certificate
						h->ptr += size;
					}
				}
				free(path);
			}
		}
	}
	return rc;
}

static int certificate_signer_text(cxml_handler_t* const h, char * const text, int length)
{ return 0;}

static const char * _subject_type [] = {
	"EC",	"AT",	"AA",	"EA",	"ROOT",	"CRL"
};

typedef struct list_pointer_t
{
	unsigned char * ptr;
	unsigned char cnt;
}list_pointer_t;

static int certificate_subject_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	// write signer info
	if (cxml_tag_is_open(tag)){
		int len;
		const char * v = cxml_tag_attr_value(tag, "type");
		if(v == NULL){
			fprintf(stderr, "Subject type must be set in Certificate profile\n");
			return -1;
		}
		h->subject_type = STR2ENUM(_subject_type, v);
		if(h->subject_type < 0) {
			fprintf(stderr, "%s: Unknown subject type\n", v);
			return -1;
		}
		cint8_write(h->subject_type, &h->ptr, h->end, &rc);
		v  = cxml_tag_attr_value(tag, "name");
		len = cstrlen(v);
		if(0 == cintx_write(len, &h->ptr, h->end, &rc) && len > 0){
			cbuf_write(v, len, &h->ptr, h->end, &rc);
		}
		bookmark_position(h, tag);
	}else{
		apply_bookmark_size(h, tag);
	}
	return rc;
}

static const char * _subject_attribute_types[] = {
	[0]  = "verification_key",
	[1]  = "encryption_key",
	[2]  = "assurance_level",
	[3]  = "reconstruction_value",
	[32] = "its_aid_list",
	[33] = "its_aid_ssp_list"
};

static int subject_attribute_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		const char * v = cxml_tag_attr_value(tag, "type");
		int atype;
		if(v == NULL){
			fprintf(stderr, "Attribute type must be set\n");
			return -1;
		}
		atype = STR2ENUM(_subject_attribute_types, v);
		if(atype < 0) {
			fprintf(stderr, "%s: Unknown attribute type\n", v);
			return -1;
		}
		cint8_write(atype, &h->ptr, h->end, &rc);
		h->sa_type = atype;

		const char * keyPath = _verificationKey;
		switch (h->sa_type){
		case  1: // encryption_key
			keyPath = _decriptionKey;
		case  0: //verificationKey
			if (keyPath){
				h->key = ecc_api_key_public_load(keyPath, 0);
				if (h->key == NULL){
					fprintf(stderr, "%s: Can't load public key\n", keyPath);
					return -1;
				}
			}
			else if (_reuseKeys){
				cvstrncpy(h->ptr, h->end - h->ptr, _keyPath, "/", _certName,
					".", h->sa_type ? "e" : "v", "key", NULL);
				h->key = ecc_api_key_private_load(h->ptr, 0);
				if (h->key == NULL){
					_reuseKeys = 0;
				}
			}
			break;
		case 32: //its_aid_list
		case 33: //its_aid_ssp_list
			bookmark_position(h, tag);
			break;
		};
	}else{
		switch(h->sa_type){
		case  0: // verification_key
			h->verificationKey = h->key;
			h->key = NULL;
			break;
		case  1: // encryption_key
			h->encryptionKey = h->key;
			h->key = NULL;
			break;
		case 32: //its_aid_list
		case 33: //its_aid_ssp_list
			apply_bookmark_size(h, tag);
			break;
		}
		if(h->key){
			ecc_api_key_free(h->key);
		}
		h->sa_type = -1;
	}
	return rc;
}

static const char * _pk_algorithms[] = {
	[0]  = "ecdsa_nistp256_with_sha256",
	[1]  = "ecies_nistp256",
};

static const char * _sym_algorithms[] = {
	[0]  = "aes_128_ccm",
};
static const char * _point_types [] = {
	[0] = "x_coordinate_only",
	[1] = "compressed",
	[2] = "compressed_y0",
	[3] = "compressed_y1",
	[4] = "uncompressed"
};

static int attribute_public_key_tag(cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		int alg = 0, sym_alg = 0;
		const char * v = cxml_tag_attr_value(tag, "algorithm");
		if (v) {
			alg = STR2ENUM(_pk_algorithms, v);
			if (alg < 0){
				fprintf(stderr, "%s: Unknown public key algorithm\n", v);
				return -1;
			}
		}
		cint8_write(alg, &h->ptr, h->end, &rc);

		v = cxml_tag_attr_value(tag, "point-type");
		if (v){
			h->pk_ptype = STR2ENUM(_point_types, v);
			if (h->pk_ptype < 0){
				fprintf(stderr, "%s: Unknown point type\n", v);
				return -1;
			}
			if (h->pk_ptype == 1) h->pk_ptype = 2;
		}

		switch (alg){
		case 0:
			break;
		case 1:
			v = cxml_tag_attr_value(tag, "sym_alg");
			if (v){
				sym_alg = STR2ENUM(_sym_algorithms, v);
				if (sym_alg < 0){
					fprintf(stderr, "%s: Unknown symmetric algorithm\n", v);
					return -1;
				}
			}
			cint8_write(sym_alg, &h->ptr, h->end, &rc);
			break;
		default:
			bookmark_position(h, tag);
			break;
		}
		h->pk_alg = alg;
		h->pk_sym_alg = sym_alg;
	}
	else{
		if (h->pk_alg > 1){
			apply_bookmark_size(h, tag);
		}
		h->pk_alg = -1;
		h->pk_sym_alg = -1;
	}
	return rc;
}

static int attribute_eccpoint_tag   (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	int pointtype;
	if (cxml_tag_is_open(tag)){
		const char * v;
		pointtype = 0;
		v = cxml_tag_attr_value(tag, "type");
		if (v){
			pointtype = STR2ENUM(_point_types, v);
			if (pointtype < 0){
				fprintf(stderr, "%s: Unknown point type\n", v);
				return -1;
			}
			if (pointtype == 1) pointtype = 2;
		}
		h->pk_ptype = pointtype;
		h->pk_data = NULL;
		h->pk_datasize = 0;

	}
	else{
		void * key = h->key;
		pointtype = h->pk_ptype;
		// generate private and public key pair
		char x[32], y[32];
		if (key == NULL){
			if (h->pk_data){
				if (h->pk_datasize < 32 || (h->pk_datasize < 64 && pointtype == 4)){
					fprintf(stderr, "Insufficient size of public key\n");
					return -1;
				}
				key = ecc_api_key_public_set(h->pk_alg, pointtype, h->pk_data, h->pk_data + 32);
			}
			else{
				// generate PK
				if (h->pk_alg < 0 || h->pk_sym_alg < 0){
					fprintf(stderr, "Public key algorythm must be specified\n");
					return -1;
				}
				key = ecc_api_key_gen(h->pk_alg, h->pk_sym_alg);
			}
		}
		if (key){
			h->key = key;
			rc = ecc_api_key_public(key, &x[0], &y[0]);
			if (rc >= 0){
				if (pointtype == 2) pointtype |= (y[31] & 1);
				cint8_write(pointtype, &h->ptr, h->end, &rc);
				if (rc == 0)cbuf_write(&x[0], 32, &h->ptr, h->end, &rc);
				if (rc == 0 && pointtype == 4){
					cbuf_write(&y[0], 32, &h->ptr, h->end, &rc);
				}
			}
		}
		if (h->pk_data){
			free(h->pk_data);
			h->pk_data = NULL;
		}
	}
	return rc;
}

static int attribute_eccpoint_text(cxml_handler_t* const _h, char * const text, int length)
{
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (h->pk_data == NULL){
		h->pk_data = malloc(64);
		h->pk_datasize = 0;
	}
	char * e = cstr_hex2bin(h->pk_data + h->pk_datasize, 64 - h->pk_datasize, text, length);
	h->pk_datasize = e - h->pk_data;
	return 0;
}


static int attribute_assurance_tag  (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		uint8_t assurance = 0;
		const char * v = cxml_tag_attr_value(tag, "level");
		if(v){
			while(*v && isspace(*v))v++;
			if(*v){
				long n = strtol(v, NULL, 0); 
				if(n<0 || n > 7){
					fprintf(stderr, "%s: Invalid assurance level\n", v);
					return -1;
				}
				assurance |= n<<5;
			}
		}
		v = cxml_tag_attr_value(tag, "confidence");
		if(v){
			while(*v && isspace(*v))v++;
			if(*v){
				long n = strtol(v, NULL, 0); 
				if(n<0 || n > 3){
					fprintf(stderr, "%s: Invalid assurance confidence\n", v);
					return -1;
				}
				assurance |= n;
			}
		}
		cint8_write(assurance, &h->ptr, h->end, &rc);
	}
	return rc;
}

static int attribute_aid_tag        (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if(0 == (tag->type & CXML_TAG_OPEN)){
		const char * v = cxml_tag_attr_value(tag, "value");
		if(NULL == v)v = cxml_tag_attr_value(tag, "aid");
		if(NULL == v){
			fprintf(stderr, "WARNING: Value required for AID tag. Item was skiped.\n");
		}else{
			uint32_t n;
			while(isspace(*v))v++;
			if(!isdigit(*v)){
				fprintf(stderr, "ERROR: Invalid AID '%s'\n", v);
				rc = -1;
			}else{
				n = strtoul(v, NULL, 0);
				cintx_write(n, &h->ptr, h->end, &rc);
			}
		}
	}
	return rc;
}

static int attribute_ssp_tag        (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		uint32_t n;
		const char * v = cxml_tag_attr_value(tag, "aid");
		if(NULL == v){
			fprintf(stderr, "ERROR: AID shall be supplied for SSP.\n");
			return -1;
		}
		while(isspace(*v))v++;
		if(!isdigit(*v)){
			fprintf(stderr, "ERROR: Invalid AID '%s' in SSP\n", v);
			return -1;
		}
		n = strtoul(v, NULL, 0);
		cintx_write(n, &h->ptr, h->end, &rc);
		bookmark_position(h, tag);
	}else{
		// write ssp buffer data
		apply_bookmark_size(h, tag);
	}
	return rc;
}

static int attribute_ssp_text(cxml_handler_t* const _h, char * const text, int length)
{
	int rc=0;
	if(text && length){
		cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
		rc = cbuf_write(text, length, &h->ptr, h->end, NULL);
	}
	return rc;
}

static int certificate_validity_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		bookmark_position(h, tag);
		rc = 0;
	}else{
		rc = apply_bookmark_size(h, tag);
	}
	return rc;
}

static unsigned int _convert_time(const char * v)
{
	unsigned int ret;
	char * e;
	struct tm tm;
	
	// can me a difference of the predefined point in time
	if(*v == '-' || *v == '+'){
		const char * p;
		if(_defaultTime == 0){
			fprintf(stderr, "Default time must be set\n");
			return 0;
		}
		p = v+1;
		ret = 0;
		for(;;){
			unsigned int n;
			n = strtoul(p, &e, 10);
			if(n == ULONG_MAX) break;
			switch(*e){
			case 'd': n *= 24;
			case 'h': n *= 60;
			case 'm': n *= 60;
			case 's': e++;
			}
			ret += n;
			if(isdigit(*e)){
				p = e;
				continue;
			}
			break;
		}
		if(*v == '-'){
			ret = _defaultTime - ret;
		}else{
			ret = _defaultTime + ret;
		}
	}
	else{
		// next try load as integer seconds since epoch
		ret = strtoul(v, &e, 0);
		if (ret == ULONG_MAX || *e){
			ret = 0;
			//next try to convert ISO text representation
			memset(&tm, 0, sizeof(tm));
			if (3 == sscanf(v, "%d-%d-%d", &tm.tm_year, &tm.tm_mon, &tm.tm_mday)){
				tm.tm_mon--; // STARTED FROM 0
				if (tm.tm_year > 500){
					tm.tm_year -= 1900;
				}
				ret = mkitstime32(&tm);
				if (ret == (time_t)-1) {
					fprintf(stderr, "%s: Date format specification error. Use YYY-MM-DD\n", v);
					ret = 0;
				}
			}
		}
	}
	return ret;
}

static int validity_restriction_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		int vr_type = -1;
		int nTime;
		const char * v = cxml_tag_attr_value(tag, "type");
		const char *start, *end, *duration;
		if(NULL == v){
			fprintf(stderr, "ERROR: Restriction shall have a type.\n");
			return -1;
		}
		
		while(isspace(*v))v++;
		if(0 == strcmp("time", v)){
			start    = cxml_tag_attr_value(tag, "start");
			end      = cxml_tag_attr_value(tag, "end");
			duration = cxml_tag_attr_value(tag, "duration");
			if(end && *end){
				vr_type = (start && *start) ? 1 : 0;
			}else if(start && *start && duration && *duration){
				vr_type = 2;
			}else{
				fprintf(stderr, "ERROR: Either end or start and duration shall be specified for time restriction.\n");
				return -1;
			}
		}else if(0 == strcmp("region", v)){
			vr_type = 3;
		}else if(isdigit(*v)){
			vr_type=strtoul(v, NULL,0);
		}else{
			fprintf(stderr, "%s: Unknown validity restriction type.\n", v);
			return -1;
		}
		h->vr_type = vr_type;
		cint8_write(vr_type, &h->ptr, h->end, &rc);
		
		// save time restrictions
		switch(vr_type){
		case 1: /* time_start_and_end */
			nTime = _convert_time(start);
			cint32_write(nTime, &h->ptr, h->end, &rc);
		case 0: /* time_end */
			nTime = _convert_time(end);
			cint32_write(nTime, &h->ptr, h->end, &rc);
			break;
		case 2: /* time_start_and_duration */
			nTime = _convert_time(start);
			cint32_write(nTime, &h->ptr, h->end, &rc);
			nTime = _convert_time(duration);
			cint32_write(nTime, &h->ptr, h->end, &rc);
			break;
		case 3: /* region */
			break;
		default: // opaque
			bookmark_position(h, tag); // for opaque data
		}
	}else{
		if(h->vr_type > 3){
			apply_bookmark_size(h, tag);
		}
		h->vr_type = -1;
	}
	return 0;
}
static int region_none_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	if (cxml_tag_is_open(tag)){
		cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
		return cint8_write(0, &h->ptr, h->end, NULL);
	}
	return 0;
}

static int region_circle_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		//region type
		if(0 == cint8_write(1, &h->ptr, h->end, NULL)){
			//latitude and longitude
			rc = location_tag (_h, tag);
			if(0 == rc){
				//radius
				uint32_t n;
				const char * v    = cxml_tag_attr_value(tag, "radius");
				if(NULL == v){
					fprintf(stderr, "ERROR: radius shall be specified for circle.\n");
					return -1;
				}
				n = strtoul(v, NULL, 0);
				if(n > 0xFFFF){
					fprintf(stderr, "ERROR: %ul: radius is too big.\n", n);
					return -1;
				}
				cint16_write(n, &h->ptr, h->end, &rc);
			}
		}
	}
	return rc;
}

static int region_rectangle_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		// region type
		rc = cint8_write(2, &h->ptr, h->end, NULL);
		if(0 == rc){
			bookmark_position(h, tag);
		}
	}else{
		rc = apply_bookmark_size(h, tag);
	}
	return rc;
}

static int region_polygon_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		// region type
		rc = cint8_write(3, &h->ptr, h->end, NULL);
		if(0 == rc){
			bookmark_position(h, tag);
		}
	}else{
		rc = apply_bookmark_size(h, tag);
	}
	return rc;
}

static int location_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	if (cxml_tag_is_open(tag)){
		int32_t lat, lon;
		long double d;
		cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
		const char * v    = cxml_tag_attr_value(tag, "latitude");
		if(v == NULL){
			fprintf(stderr, "ERROR: Latitude shall be specified for location.\n");
			return -1;
		}
		d = strtold(v, NULL);
		if (d <= 90.0 &&  d >= -90.0) d *= 10000000.0; // degree
		lat = (int32_t)floorl(d+_refLat);

		v    = cxml_tag_attr_value(tag, "longitude");
		if(v == NULL){
			fprintf(stderr, "ERROR: Longitude shall be specified for location.\n");
			return -1;
		}
		d = strtold(v, NULL);
		if (d <= 180.0 &&  d >= -180.0) d *= 10000000.0; // degree
		lon = (int32_t)floorl(d + _refLon);

		cint32_write(lat, &h->ptr, h->end, &rc);
		cint32_write(lon, &h->ptr, h->end, &rc);
	}
	return rc;
}

static const char * _id_dictionaries[] = {
	"iso_3166_1",
	"un_stats",
};

static int region_id_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		int value = 0;
		unsigned int uvalue = 0;
		const char * v;

		// region type
		rc = cint8_write(4, &h->ptr, h->end, NULL);

		// region dictionary. use 0 by default
		v = cxml_tag_attr_value(tag, "dictionary");
		if(v == NULL)v = cxml_tag_attr_value(tag, "dict");
		if(v){
			value = STR2ENUM(_id_dictionaries, v);
			if(value<0){
				fprintf(stderr, "%s: Unknown dictionary type\n", v);
				return -1;
			}
		}
		if(cint8_write(value, &h->ptr, h->end, NULL)){
			return -1;
		}
			
		v = cxml_tag_attr_value(tag, "id");
		if(v == NULL){
			fprintf(stderr, "ERROR: Region identifier must be set\n");
			return -1;
		}
		while(isspace(*v))v++;
		uvalue = strtoul(v, NULL, 0);
		if(uvalue > 0xFFFF){
			fprintf(stderr, "%s: Invalid region identifier\n", v);
			return -1;
		}
		if(cint16_write(uvalue, &h->ptr, h->end, NULL)){
			return -1;
		}
			
		uvalue = 0;
		v = cxml_tag_attr_value(tag, "local");
		if(v){
			while(isspace(*v))v++;
			uvalue = strtoul(v, NULL, 0);
			if(!isdigit(*v) || uvalue > 0xFFFF){
				fprintf(stderr, "%s: Invalid region identifier\n", v);
				return -1;
			}
		}
		cintx_write(uvalue, &h->ptr, h->end, &rc);
		if(rc) return -1;
	}
	return rc;
}

static const char * _signature_algorithms[] = {
	"ecdsa_nistp256_with_sha256",
};

static int certificate_signature_tag (cxml_handler_t* const _h, cxml_tag_t * const tag)
{
	int rc = 0;
	cert_cxml_handler_t * h = (cert_cxml_handler_t *)_h;
	if (cxml_tag_is_open(tag)){
		void * key;
		int alg = 0;
		if(h->signer_type == 0){
			// self signed certificate
			key = h->verificationKey;
			if(!key){
				fprintf(stderr, "ERROR: Verification key attribute was not provided for self-signed certificate\n");
				return -1;
			}
		}else{
			const char * v = cxml_tag_attr_value(tag, "algorithm");
			if (v){
				alg = STR2ENUM(_signature_algorithms, v);
				if (alg < 0){
					fprintf(stderr, "%s: Unknown signature algorithm\n", v);
					return -1;
				}
			}

			if (h->signer == NULL){
				fprintf(stderr, "ERROR: Signer certificate name shall be provided\n");
				return -1;
			}

			// load signer certificate
			int plen = strlen(_searchPath) + strlen(h->signer);
			char * path = malloc(plen + 16);

			cvstrncpy(path, plen + 16, _searchPath, "/", h->signer, ".vkey", NULL);
			key = ecc_api_key_private_load(path, alg);
			if (key == NULL){
				fprintf(stderr, "%s: Could not load issuing private key\n", path);
				free(path);
				return -1;
			}
		}
		cint8_write(alg, &h->ptr, h->end, &rc);
		rc = ecc_sign(key, h->buf, h->ptr - h->buf - 1, &h->ptr, h->end - h->ptr);
	}
	return rc;
}

static int  _Begin_Tag(cxml_handler_t* const h, cxml_tag_t * const tag)
{
	fprintf(stderr, "WARNING: %s: Unknown tag", tag->name);
	return 0;
}

static int  _End_Tag(cxml_handler_t* const h, cxml_tag_t * const tag)
{
	return 0;
}
static int  _Text(cxml_handler_t* const h, char * const text, int length)
{return 0;}
