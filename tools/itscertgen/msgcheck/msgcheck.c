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
#include <openssl/evp.h>
#include <openssl/err.h>
#include <openssl/ec.h>
#include <openssl/pem.h>
#include <openssl/sha.h>
#include <openssl/ecdsa.h>
#include <string.h>

#include "../cshared/cstr.h"
#include "../cshared/cserialize.h"

static size_t   load_certificate(const char * path, char ** p);
static EC_KEY * load_public_key(const char* path, const EC_GROUP * group);
static EC_KEY * get_verification_key(const char* buf, size_t len, const EC_GROUP * group);
static void     calculate_certificate_digest(const char* data, int length, char * hash);

//static void print_x(FILE * f, const char * ptr, int len);
static const char *cbin2hex(void * bin, size_t len);

#pragma pack(1)

typedef enum {
	enrollment_credential = 0,
	authorization_ticket = 1,
	authorization_authority = 2,
	enrollment_authority = 3,
	root_ca = 4,
	crl_signer = 5
} SubjectType;

typedef enum {
	si_self,
	si_digest,
	si_certificate,
	si_certificate_chain,
	si_digest_with_other_algorithm
} SignerInfoType;

typedef enum {
	verification_key = 0,
	encryption_key = 1,
	assurance_level = 2,
	reconstruction_value = 3,
	its_aid_list = 32,
	its_aid_ssp_list = 33,
} SubjectAttributeType;

typedef enum {
	time_end = 0,
	time_start_and_end = 1,
	time_start_and_duration = 2,
	region = 3
} ValidityRestrictionType;

typedef enum {
	region_none = 0,
	region_circle = 1,
	region_rectangle = 2,
	region_polygon = 3,
	region_id = 4
} RegionType;

typedef enum {
	iso_3166_1 = 0,
	un_stats = 1,
}RegionDictionary;

typedef struct SubjectInfo {
	unsigned char subject_type;
	unsigned char name_length;
	char name[];
}SubjectInfo;

typedef struct EccPoint {
	unsigned char type;
	unsigned char x[32];
	unsigned char y[32];
}EccPoint;

typedef struct PublicKey {
	unsigned char algorithm;
	union{
		EccPoint  key;
		struct {
			unsigned char sym_alg;
			EccPoint  key;
		}other;
	}u;
}PublicKey;

#pragma pack()




char _es_bufs[8][64];
int _es_bufs_idx = 0;
static const char * _enumstring(int value, const char ** names, int nsize)
{
	char * ret;
	if (value >= 0 && value < nsize && names[value]){
		return names[value];
	}
	ret = _es_bufs[(++_es_bufs_idx) & 7];
	sprintf(ret, "%d", value);
	return ret;
}
#define ENUMSTRING(V,L) _enumstring(V, L, sizeof(L)/sizeof(L[0]))


static const char * _signer_types[] = {
	"self",
	"hash",
	"certificate",
	"chain",
	"other",
};

int main(int argc, char ** argv)
{
	int i;
	EC_KEY * defkey = NULL;
	EC_GROUP * group;
	char cert_digest[8] = { 0 };

	if (argc < 2 || 0 == strcmp("-h", argv[1])){
		printf("Usage: msgcheck [-c certificate] [-p pub key] messages\n");
		return -1;
	}

	group = EC_GROUP_new_by_curve_name(NID_X9_62_prime256v1);

	for (i = 1; i < argc; i++){
		EC_KEY * key = NULL;
		ECDSA_SIG * ecdsa;
		char * data = NULL;
		char *e;
		size_t len;
		unsigned char hash[32];

		if (0 == strcmp("-c", argv[i])){
			i++;
			if (i == argc){
				fprintf(stderr, "Usage: msgcheck [-c certificate] [-p pub key] messages\n");
				return -1;
			}
			len = load_certificate(argv[i], &data);
			if (len == -1){
				fprintf(stderr, "%s: can not load certificate\n", argv[i]);
				return -1;
			}
			
			calculate_certificate_digest(data, len, cert_digest);

			if (defkey) EC_KEY_free(defkey);
			defkey = get_verification_key(data, len, group);
			free(data); data = NULL;
			continue;
		}
		if (0 == strcmp("-p", argv[i])){
			i++;
			if (i == argc){
				fprintf(stderr, "Usage: msgcheck [-c certificate] [-p pub key] [-x|-b] messages\n");
				return -1;
			}
			if (defkey) EC_KEY_free(defkey);
			defkey = load_public_key(argv[i], group);
			if (defkey == NULL){
				fprintf(stderr, "%s: can not load public key\n", argv[i]);
				return -1;
			}
			continue;
		}
		if (0 == strcmp("-r", argv[i])){
			if (defkey){
				EC_KEY_free(defkey);
				defkey = NULL;
			}
			continue;
		}
		e = cstraload(&data, argv[i]);
		if (data == NULL){
			fprintf(stderr, "%s: can not load message\n", argv[i]);
			continue;
		}
		if (e - data < 67){
			fprintf(stderr, "%s: message is too small\n", argv[i]);
			free(data); data = NULL;
			continue;
		}
		if (data[0] == '0') {
			// try hexadecimal
			e = cstr_hex2bin(data, e-data, data, e-data);
			if(e == NULL){
				e = cstrnload(data, -1, argv[i]);
				*e = 0;
			}
		}
		{
			SHA256_CTX ctx;
			SHA256_Init(&ctx);
			SHA256_Update(&ctx, data, e-data-66);
			SHA256_Final(hash, &ctx);
		}
		printf("%s: HASH : %d bytes\n", argv[i], e-data-66); 
		printf("%s: HASH : %s\n", argv[i], cbin2hex(hash, 32)); 
		if (defkey == NULL){
			// get key from message signer
			if (data[4] != si_certificate){
				fprintf(stderr, "%s: signer type %s is unsupported. use -c or -p\n", argv[i], ENUMSTRING(data[4], _signer_types));
				free(data); data = NULL;
				continue;
			}
			key = get_verification_key(data + 5, e - data - 5, group);
			if (key == NULL){
				fprintf(stderr, "%s: can not get key from message\n", argv[i]);
				free(data); data = NULL;
				continue;
			}
		}
		else{
			if (data[4] == si_digest){
				if (memcmp(data + 5, cert_digest, 8)){
					fprintf(stderr, "%s: warning: certificate digest mismatch\n", argv[i]);
				}
			}
			key = defkey;
		}

		{
			BIGNUM *x = BN_new(), *y = BN_new();
			EC_POINT_get_affine_coordinates_GFp(group, EC_KEY_get0_public_key(key), x, y, NULL);

			printf("%s: KEY.X: %s\n", argv[i], BN_bn2hex(x));
			printf("%s: KEY.Y: %s\n", argv[i], BN_bn2hex(y));
			BN_clear_free(x);
			BN_clear_free(y);
		}

		ecdsa = ECDSA_SIG_new();
		const char * r = e - 64;
		const char * s = e - 32;

		if (ecdsa->r == BN_bin2bn((const unsigned char *)r, 32, ecdsa->r) &&
			ecdsa->s == BN_bin2bn((const unsigned char *)s, 32, ecdsa->s)){
			printf("%s: SIG.R: %s\n", argv[i], BN_bn2hex(ecdsa->r));
			printf("%s: SIG.S: %s\n", argv[i], BN_bn2hex(ecdsa->s));
			int rc = ECDSA_do_verify(hash, 32, ecdsa, key);
			if (rc < 0){
				printf("%s: ERROR\n", argv[i]);
				ERR_print_errors_fp(stderr);
			}
			else if (rc > 0){
				printf("%s: OK\n", argv[i]);
			}
			else{
				printf("%s: FAILED\n", argv[i]);
			}
		}
		ECDSA_SIG_free(ecdsa);
		free(data); data = NULL;

		if (key != defkey){
			EC_KEY_free(key);
			key = NULL;
		}
	}
	return 0;
}


static size_t load_certificate(const char * path, char ** p)
{
	FILE *f;
	size_t size;
	char * cert;

	f = fopen(path, "rb");
	if (f == NULL){
		perror(path);
		return -1;
	}
	fseek(f, 0, SEEK_END);
	size = ftell(f);
	fseek(f, 0, SEEK_SET);
	if (size < 67){
		return -1;
	}
	cert = malloc(size);
	if (size != fread(cert, 1, size, f)){
		perror(path);
		free(cert);
		return -1;
	}
	if (cert[0] == '0') {
		// try hexadecimal
		size_t i;
		for (i = 0; i < size; i++){
			char ch1 = cert[i];
			if (ch1 < '0'
				|| (ch1 > '9' && ch1 < 'A')
				|| (ch1 > 'F' && ch1 < 'a')
				|| (ch1 > 'f')){
				break;
			}
		}
		if (i == size){
			for (i = 0; i < size; i += 2){
				char ch1 = cert[i];
				if (ch1 >= '0' && ch1 <= '9') ch1 -= '0';
				else if (ch1 >= 'A' && ch1 <= 'F') ch1 -= 'A' - 0x0A;
				else if (ch1 >= 'a' && ch1 <= 'f') ch1 -= 'a' - 0x0A;
				else break;
				char ch2 = cert[i + 1];
				if (ch2 >= '0' && ch2 <= '9') ch2 -= '0';
				else if (ch2 >= 'A' && ch2 <= 'F') ch2 -= 'A' - 0x0A;
				else if (ch2 >= 'a' && ch2 <= 'f') ch2 -= 'a' - 0x0A;
				else break;
				cert[i / 2] = (ch1 << 4) | ch2;
			}
			size /= 2;
		}
	}

	*p = cert;
	return size;
}

static EC_KEY * load_public_key(const char* path, const EC_GROUP * group)
{
	EC_KEY * eckey = NULL;
	EC_POINT * point = NULL;
	FILE * f = fopen(path, "rb");
	if (f){
		eckey = PEM_read_EC_PUBKEY(f, &eckey, NULL, NULL);
		if (eckey == NULL){
			fseek(f, 0, SEEK_END);
			int len = ftell(f);
			fseek(f, 0, SEEK_SET);
			char * buf = OPENSSL_malloc(len + 1);
			if (len == fread(buf, 1, len, f)){
				buf[len] = 0;
				if (len == 65){
					// oct
					point = EC_POINT_new(group);
					if (!EC_POINT_oct2point(group, point, (const unsigned char *)buf, len, NULL)){
						EC_POINT_free(point);
						point = NULL;
					}
				}
				else{
					//hex
					point = EC_POINT_hex2point(group, buf, NULL, NULL);
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

static EC_KEY * get_verification_key(const char* buf, size_t len, const EC_GROUP * group)
{
	EC_KEY * key = NULL;
	const char * ptr;
	switch (buf[1]){
	case 0: /* self*/
		ptr = buf + 2; break;
	case 1: /* hash */
		ptr = buf + 10; break;
	default:
		fprintf(stderr, "Unsupported signer type\n");
		return NULL;
	}
	SubjectInfo * si = (SubjectInfo*)ptr;
	ptr += sizeof(SubjectInfo) + si->name_length;
	cintx_read(&ptr, buf + len, NULL);
	if (*ptr == 0){
		EC_POINT * point = EC_POINT_new(group);
		ptr++; // skip sa type;
		ptr++; // skip alg;
		if (*ptr == 4) len = 65;
		else len = 33;
		if (EC_POINT_oct2point(group, point, (const unsigned char*)ptr, len, NULL)){
			key = EC_KEY_new();
			EC_KEY_set_group(key, group);
			EC_KEY_set_public_key(key, point);
		}
		EC_POINT_free(point);
	}
	return key;
}

static void     calculate_certificate_digest(const char* data, int length, char * digest)
{
	// set signature point type to X
	unsigned char hash[32];
	unsigned char tmp = 0;
	SHA256_CTX ctx;

	SHA256_Init(&ctx);
	SHA256_Update(&ctx, data, length - 65);
	SHA256_Update(&ctx, &tmp, 1);
	SHA256_Update(&ctx, data + length - 64, 64);
	SHA256_Final(hash, &ctx);
	memcpy(digest, hash + 24, 8);
}
/*
static void print_x(FILE * f, const char * ptr, int len)
{
	const unsigned char * b = (const unsigned char *)ptr;
	const unsigned char * e = b + len;
	for (; b < e; b++){
		unsigned char c = *b;
		fprintf(f, "%02X", c);
	}
}
*/
typedef struct memitem
{
	size_t len;
	void * ptr;
}strbinitem;
static strbinitem _memitems[4] = { { 0, NULL } };
static size_t _memitemidx = 0;
static void * _allocmemitem(size_t len){
	strbinitem * p = &_memitems[_memitemidx];
	if (len > p->len){
		p->ptr = realloc(p->ptr, len);
		p->len = len;
	}
	_memitemidx = (_memitemidx + 1) & (sizeof(_memitems) / sizeof(_memitems[0])-1);
	return p->ptr;
}

static const char *cbin2hex(void * bin, size_t len)
{
	int hlen = len * 2 + 1;
	char * ret = _allocmemitem(hlen);
	cstr_bin2hex(ret, hlen, bin, len);
	return ret;
}
