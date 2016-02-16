/*********************************************************************
######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
*********************************************************************/
#ifndef ecc_api_h
#define ecc_api_h

#ifdef __cplusplus
extern "C" {
#endif


int ecc_api_init();
int ecc_api_done();

typedef enum {
	ecdsa_nistp256_with_sha256,
	ecies_nistp256
}ecc_pk_algorithm;

typedef enum {
	aes_128_ccm
}ecc_sym_algorithm;

typedef enum {
	ecc_bin,
	ecc_hex,
	ecc_pem
}ecc_format;

typedef enum {
	ecc_x_only = 0,
	ecc_compressed_y0 = 2,
	ecc_compressed_y1 = 3,
	ecc_uncompressed = 4
}ecc_point_type;

void * ecc_api_key_gen(ecc_pk_algorithm pk_alg, ecc_sym_algorithm sym_alg);
void * ecc_api_key_init(ecc_pk_algorithm pk_alg, ecc_sym_algorithm sym_alg, const char* pkey);
void   ecc_api_key_free(void*);
int    ecc_api_key_private(void*, char* buf);
int    ecc_api_key_public(void*, char* x, char* y);
int    ecc_api_key_private_save(void*, const char* path, ecc_format format);
void * ecc_api_key_private_load(const char* path, ecc_pk_algorithm pk_alg);
int    ecc_api_key_public_save(void*, const char* path, ecc_format format);
void * ecc_api_key_public_load(const char* path, ecc_pk_algorithm pk_alg);
void * ecc_api_key_public_set(ecc_pk_algorithm pk_alg, ecc_point_type ptype, const char * px, const char * py);

#define sha256_hash_size 32
int sha256_calculate(char* hash, const char * ptr, int len);

int    ecc_sign(void * key, const char * data, int length, char ** sig, int maxsize);

#ifdef __cplusplus
}
#endif
#endif
