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

#include "ecc_api.h"

#include <openssl/evp.h>
#include <openssl/err.h>
#include <openssl/ec.h>
#include <openssl/pem.h>
#include <openssl/sha.h>
#include <openssl/ecdsa.h>
#include <string.h>

#define ARRAYSIZE(A) (sizeof(A)/sizeof(A[0]))

typedef struct {
	int nid;
	const char * name;
	const char * p;
	const char * a;
	const char * b;
	const char * x;
	const char * y;
	const char * z;
	const char * seed;
}ecc_api_curve_data;

static ecc_api_curve_data _config[] = {
	{
		NID_X9_62_prime256v1, "P-256", NULL,
	},
	{
		NID_X9_62_prime256v1, "P-256", NULL,
	},
};

static EC_GROUP * _curves[ARRAYSIZE(_config)] = { NULL };


static EC_GROUP* _ecc_curve_by_data(const ecc_api_curve_data * d)
{
	BIGNUM *p, *a, *b, *x, *y, *z;
	EC_GROUP *group;
	EC_POINT *P;

	if (d->nid){
		return EC_GROUP_new_by_curve_name(d->nid);
	}

	x = BN_new();	y = BN_new();	z = BN_new();
	p = BN_new();	a = BN_new();	b = BN_new();
	x = BN_new();	y = BN_new();	z = BN_new();

	BN_hex2bn(&p, d->p);
	BN_hex2bn(&a, d->a);
	BN_hex2bn(&b, d->b);

	group = EC_GROUP_new_curve_GFp(p, a, b, NULL);
	EC_GROUP_set_asn1_flag(group, OPENSSL_EC_NAMED_CURVE);

	BN_hex2bn(&x, d->x);
	BN_hex2bn(&z, d->z);
	P = EC_POINT_new(group);
	EC_POINT_set_compressed_coordinates_GFp(group, P, x, 1, NULL);
	if (EC_POINT_is_on_curve(group, P, NULL)){
		EC_GROUP_set_generator(group, P, z, BN_value_one());
	}
	EC_POINT_free(P);
	BN_clear_free(p); BN_clear_free(a); BN_clear_free(b);
	BN_clear_free(x); BN_clear_free(y); BN_clear_free(z);
	return group;
}

int ecc_api_init()
{
	int i;
	for (i = 0; i < ARRAYSIZE(_config); i++){
		_curves[i] = _ecc_curve_by_data(&_config[i]);
	}
	return 0;
}

int ecc_api_done()
{
	int i;
	for (i = 0; i < ARRAYSIZE(_config); i++){
		EC_GROUP_free(_curves[i]);
	}
	return 0;
}

void * ecc_api_key_gen(ecc_pk_algorithm pk_alg, ecc_sym_algorithm sym_alg)
{
	EC_KEY * key = NULL;
	if (_curves[pk_alg]){
		key = EC_KEY_new();
		if (key){
			EC_KEY_set_group(key, _curves[pk_alg]);
			EC_KEY_set_asn1_flag(key, OPENSSL_EC_NAMED_CURVE);

			if (!EC_KEY_generate_key(key)){
				ERR_print_errors_fp(stderr);
				fflush(stderr);
				EC_KEY_free(key);
				key = NULL;
			}
		}
	}
	return key;
}

void * ecc_api_key_init(ecc_pk_algorithm pk_alg, ecc_sym_algorithm sym_alg, const char* pkey)
{
	EC_KEY * eckey = NULL;
	BIGNUM * bn = BN_new();
	const EC_GROUP * group = _curves[pk_alg];
	if (group){
		if (BN_bin2bn((const unsigned char*)pkey, 32, bn)){
			eckey = EC_KEY_new();
			if (eckey){
				if (EC_KEY_set_group(eckey, group)){
					if (EC_KEY_set_private_key(eckey, bn)){
						EC_POINT * point;
						point = EC_POINT_new(group);
						if (EC_POINT_mul(group, point, bn, NULL, NULL, NULL)){
							EC_KEY_set_public_key(eckey, point);
						}
						EC_POINT_free(point);
					}
				}
			}
		}
	}
	BN_free(bn);
	return (void*)eckey;
}

void   ecc_api_key_free(void* key)
{
	EC_KEY_free(key);
}

int ecc_api_key_private(void* key, char* buf)
{
	int len = -1;
	if (key){
		const BIGNUM   * bn;
		bn = EC_KEY_get0_private_key(key);
		if (bn){
			len = BN_num_bytes(bn);
			if (buf){
				BN_bn2bin(bn, (unsigned char*)buf);
			}
		}
	}
	return len;
}

int    ecc_api_key_public(void* key, char * px, char * py)
{
	const EC_GROUP * ecgroup;
	const EC_POINT * ecpoint;
	const EC_KEY   * eckey = (EC_KEY*)key;
	BIGNUM x, y;
	int bcount = -1;

	ecgroup = EC_KEY_get0_group(eckey);
	ecpoint = EC_KEY_get0_public_key(eckey);

	//fill public key data
	BN_init(&x); BN_init(&y);
	if (EC_POINT_get_affine_coordinates_GFp(ecgroup, ecpoint, &x, &y, NULL)){
		bcount = BN_num_bytes(&x);
		if (px){
			BN_bn2bin(&x, (unsigned char*)px);
		}

		bcount = BN_num_bytes(&y);
		if (py){
			BN_bn2bin(&y, (unsigned char*)py);
		}
	}
	BN_clear_free(&x); BN_clear_free(&y);
	return bcount;
}

static int _pass_cb(char *buf, int size, int rwflag, void *u)
{
	fprintf(stderr, "Ask for a pass phrase");
	return 0;
}

int    ecc_api_key_private_save(void* key, const char* path, ecc_format format)
{
	int rc = -1;
	EC_KEY   * eckey = (EC_KEY *)key;
	if (eckey){
		FILE * f = fopen(path, "wb");
		if (f){
			if (format == ecc_pem){
				rc = PEM_write_ECPrivateKey(f, eckey, NULL, NULL, 0, _pass_cb, NULL) ? 0 : -1;
				if (rc < 0){
					ERR_print_errors_fp(stderr);
				}
			}
			else{
				const BIGNUM   * ecbn;
				ecbn = EC_KEY_get0_private_key(eckey);
				if (ecbn){
					char * buf = NULL;
					int len = BN_num_bytes(ecbn);
					if (format == ecc_bin){
						buf = (char *)OPENSSL_malloc(len);
						BN_bn2bin(ecbn, (unsigned char *)buf);
						rc = 0;
					}
					else if (format == ecc_hex){
						buf = BN_bn2hex(ecbn);
						len = strlen(buf);
						rc = 0;
					}
					if (buf){
						rc = (len == fwrite(buf, 1, len, f)) ? 0 : -1;
						OPENSSL_free(buf);
					}
				}
			}
			fclose(f);
			if (rc < 0){
				ERR_print_errors_fp(stderr);
				remove(path);
				rc = -1;
			}
		}
		else{
			perror(path);
		}
	}
	return rc;
}

void * ecc_api_key_private_load(const char* path, ecc_pk_algorithm pk_alg)
{
	EC_KEY * eckey = NULL;
	FILE * f = fopen(path, "rb");
	if (f){
		eckey = PEM_read_ECPrivateKey(f, NULL, NULL, NULL);
		if (eckey == NULL){
			BIGNUM * bn = NULL;
			fseek(f, 0, SEEK_END);
			int len = ftell(f);
			fseek(f, 0, SEEK_SET);
			char * buf = OPENSSL_malloc(len + 1);
			if (len == fread(buf, 1, len, f)){
				buf[len] = 0;
				// try hex first
				if (len != BN_hex2bn(&bn, buf)){
					if (bn){
						BN_free(bn); bn = NULL;
					}
					bn = BN_bin2bn((const unsigned char*)buf, len, NULL);
				}
			}
			OPENSSL_free(buf);
			if (bn){
				eckey = EC_KEY_new();
				if (eckey){
					EC_KEY_set_group(eckey, _curves[pk_alg]);
					if (EC_KEY_set_private_key(eckey, bn)){
						EC_POINT * point;
						const EC_GROUP * group;
						group = EC_KEY_get0_group(eckey);
						point = EC_POINT_new(group);
						if (EC_POINT_mul(group, point, bn, NULL, NULL, NULL)){
							EC_KEY_set_public_key(eckey, point);
						}
						EC_POINT_free(point);
					}
					else{
						EC_KEY_free(eckey); eckey = NULL;
					}
				}
				BN_free(bn);
			}
		}
		fclose(f);
	}
	return eckey;
}

int ecc_api_key_public_save(void* key, const char* path, ecc_format format)
{
	EC_KEY   * eckey = (EC_KEY *)key;
	int rc = -1;
	if (eckey){
		FILE * f = fopen(path, "wb");
		if (f){
			if (format == ecc_pem){
				rc = PEM_write_EC_PUBKEY(f, eckey) ? 0 : -1;
			}
			else{
				size_t len;
				char * buf = NULL;
				const EC_POINT * point = EC_KEY_get0_public_key(eckey);
				const EC_GROUP * group = EC_KEY_get0_group(eckey);

				if (format == ecc_hex){
					buf = EC_POINT_point2hex(group, point, POINT_CONVERSION_UNCOMPRESSED, NULL);
					len = strlen(buf);
				}
				else if (format == ecc_bin){
					len = EC_POINT_point2oct(group, point, POINT_CONVERSION_UNCOMPRESSED, NULL, 0, NULL);
					if (len > 0){
						buf = OPENSSL_malloc(len + 1);
						if (len != EC_POINT_point2oct(group, point, POINT_CONVERSION_UNCOMPRESSED, (unsigned char*)buf, len, NULL)){
							OPENSSL_free(buf); buf = NULL;
						}
					}
				}
				if (buf){
					if (len == fwrite(buf, 1, len, f)){
						rc = 0;
					}
					OPENSSL_free(buf); buf = NULL;
				}
			}
			fclose(f);
			if (rc < 0){
				ERR_print_errors_fp(stderr);
				remove(path);
			}
		}
		else{
			perror(path);
		}
	}
	return rc;
}

void * ecc_api_key_public_set(ecc_pk_algorithm pk_alg, ecc_point_type ptype, const char * px, const char * py)
{
	EC_KEY * eckey = NULL;
	EC_POINT * point = NULL;
	const EC_GROUP * group;
	BIGNUM *x, *y = NULL;
	int rc = 0;

	group = _curves[0];
	point = EC_POINT_new(group);
	x = BN_bin2bn(px, 32, NULL);
	if (x){
		if (ptype == ecc_uncompressed){
			y = BN_bin2bn(py, 32, NULL);
			if (y){
				rc = EC_POINT_set_affine_coordinates_GFp(group, point, x, y, NULL);
				BN_clear_free(y);
			}
		}
		else {
			rc = EC_POINT_set_compressed_coordinates_GFp(group, point, x, ptype & 1, NULL);
		}
		BN_clear_free(x);
		if (rc){
			eckey = EC_KEY_new();
			if (eckey){
				EC_KEY_set_group(eckey, group);
				EC_KEY_set_public_key(eckey, point);
			}
		}
	}
	EC_POINT_free(point);
	return eckey;
}

void * ecc_api_key_public_load(const char* path, ecc_pk_algorithm pk_alg)
{
	EC_KEY * eckey = NULL;
	FILE * f = fopen(path, "rb");
	EC_POINT * point = NULL;
	const EC_GROUP * group = _curves[0];
	if (f){
		eckey = PEM_read_EC_PUBKEY(f, &eckey, NULL, NULL);
		if (eckey == NULL){
			fseek(f, 0, SEEK_END);
			int len = ftell(f);
			fseek(f, 0, SEEK_SET);
			char * buf = OPENSSL_malloc(len + 1);
			if (len == fread(buf, 1, len, f)){
				buf[len] = 0;
				// try hex first
				point = EC_POINT_hex2point(group, buf, NULL, NULL);
				if (point == NULL){
					// try oct
					point = EC_POINT_new(group);
					if (!EC_POINT_oct2point(group, point, (const unsigned char*)buf, len, NULL)){
						EC_POINT_free(point);
						point = NULL;
					}
				}
				if (point){
					eckey = EC_KEY_new();
					if (eckey){
						EC_KEY_set_group(eckey, group);
						EC_KEY_set_public_key(eckey, point);
					}
					EC_POINT_free(point);
				}
			}
			OPENSSL_free(buf);
		}
		fclose(f);
	}
	return eckey;
}

int sha256_calculate(char* hash, const char * ptr, int len)
{
	SHA256_CTX ctx;
	SHA256_Init(&ctx);
	SHA256_Update(&ctx, ptr, len);
	SHA256_Final((unsigned char*)hash, &ctx);
	return 0;
}

int    ecc_sign(void * key, const char * data, int length, char ** psig, int maxsize)
{
	EC_KEY   * eckey;
	unsigned char *sig = (unsigned char *)*psig; 

	if (65 <= maxsize){
		eckey = EC_KEY_dup((EC_KEY*)key);
		if(eckey){
			unsigned char hash[32];
			SHA256_CTX ctx;
			ECDSA_SIG * ecdsa;

			SHA256_Init(&ctx);
			SHA256_Update(&ctx, data, length);
			SHA256_Final(hash, &ctx);

			ecdsa = ECDSA_do_sign(hash, 32, eckey);
			EC_KEY_free(eckey);
			if (ecdsa){
				int bcount;
				*(sig++) = 0; // ECC_POINT type (x_coordinate_only)
				bcount = BN_num_bytes(ecdsa->r);
				BN_bn2bin(ecdsa->r, sig);
				sig += bcount;
				bcount = BN_num_bytes(ecdsa->s);
				BN_bn2bin(ecdsa->s, sig);
				sig += bcount;
				ECDSA_SIG_free(ecdsa);
				*psig = (char *)sig;
				return 0;
			}
		}
	}
	return -1;
}
