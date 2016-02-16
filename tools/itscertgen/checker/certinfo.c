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

#include "../cshared/copts.h"
#include "../cshared/cserialize.h"
#include "../cxml/cxml.h"
#include "../generator/mkgmtime.h"

static size_t load_certificate(const char * path, char ** p);
static EC_KEY * load_public_key(const char* path, const EC_GROUP * group);
static EC_KEY * get_verification_key(const char* buf, size_t len, const EC_GROUP * group);
static void     calculate_certificate_digest(const char* data, int length, char * digest);

static int     print_attributes(const char ** p, const char * e);
static int     print_validity(const char ** p, const char * e);

static void print_x(FILE * f, const char * ptr, int len);

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

static const char * _point_types[] = {
	[0] = "x_coordinate_only",
	[1] = NULL,
	[2] = "compressed_y0",
	[3] = "compressed_y1",
	[4] = "uncompressed"
};

static const char * _signer_types[] = {
	"self",
	"digest",
	"certificate",
	"chain",
	"other",
};

static const char * _subjectTypes[] = {
	"EC", "AT", "AA", "EA", "ROOT", "CRL"
};

static const char * _signature_algorithms[] = {
	"ecdsa_nistp256_with_sha256",
};

static const char * _subject_attribute_types[] = {
	[0] = "verification_key",
	[1] = "encryption_key",
	[2] = "assurance_level",
	[3] = "reconstruction_value",
	[32] = "its_aid_list",
	[33] = "its_aid_ssp_list"
};

static const char * _pk_algorithms[] = {
	[0] = "ecdsa_nistp256_with_sha256",
	[1] = "ecies_nistp256",
};

static const char * _sym_algorithms[] = {
	[0] = "aes_128_ccm",
};

static const char * _region_tag[] = {
	"none", "circle", "rectangle", "polygon", "id"
};
static const char * _id_region_dicts[] = {
	"iso_3166_1", "un_stats"
};






static int _xmlOutput = 0;
static int _numeric = 0;
static int _usePKey = 0;
static const char * _issuerPath = NULL;


char * _es_bufs[8][64];
int _es_bufs_idx = 0;
static const char * _enumstring(int value, const char ** names, int nsize)
{
	const char * ret;
	if (!_numeric && value >= 0 && value < nsize && names[value]){
		return names[value];
	}
	ret = _es_bufs[(++_es_bufs_idx) & 7];
	sprintf(ret, "%d", value);
	return ret;
}
#define ENUMSTRING(V,L) _enumstring(V, L, sizeof(L)/sizeof(L[0]))


static int EccPoint_Size(const EccPoint * p){
	return 1 + 32 + (p->type == 4 ? 32 : 0);
}

static void EccPoint_Print(FILE * f, const char * prefix, const EccPoint * const p)
{
	fprintf(f, "%s<ecc_point type=\"%s\">\n%s\t", prefix, ENUMSTRING(p->type, _point_types), prefix);
	print_x(f, p->x, 32);
	if (p->type == 4){
		fprintf(f, "\n%s\t", prefix);
		print_x(f, p->y, 32);
	}
	fprintf(f, "\n%s</ecc_point>\n", prefix);
}

int PublicKey_Size(const PublicKey * k)
{
	if (k->algorithm == 1){
		return 2 + EccPoint_Size(&k->u.other.key);
	}
	return 1 + EccPoint_Size(&k->u.key);
}

static copt_t options[] = {
	{ "h?", "help", COPT_HELP, NULL, "Print this help page" },
	{ "x", "xml", COPT_BOOL, &_xmlOutput, "Print xml profile" },
	{ "n", "num", COPT_BOOL, &_numeric, "Keep numeric values" },
	{ "k", "pkey", COPT_BOOL, &_usePKey, "Issuer is a public key" },
	{ NULL, NULL, COPT_END, NULL, NULL }
};

int main(int argc, char ** argv)
{
	char *cert;
	size_t certlen;
	int rc = 0;

	argc = coptions(argc, argv, COPT_NOCONFIG | COPT_HELP_NOVALUES, options);

	if (argc < 2){
		if (argc<0 && (0 - argc)<((sizeof(options) / sizeof(options[0])) - 1)){
			printf("Unknown option %s\n", argv[0 - argc]);
		}
		const char * a = strrchr(argv[0], '/');
		if (a == NULL) a = argv[0];
		coptions_help(stdout, a, COPT_HELP_NOVALUES, options, "[options] <certificate> [signer]");
		return -1;
	}
	if (argc < 3){
		_xmlOutput = 1;
	}
	else{
		_issuerPath = argv[2];
	}

	if (0 > (certlen = load_certificate(argv[1], &cert))){
		return -1;
	}

	if (_xmlOutput){
		int length;
		char digest[8];
		const char * p = cert;
		const char * e = cert + certlen;
		calculate_certificate_digest(cert, certlen, digest);
		fprintf(stdout, "<certificate digest=\"");
		print_x(stdout, digest, 8);
		fprintf(stdout, "\">\n");
		fprintf(stdout, "\t<version>%d</version>\n", *p++);

		cxml_handler_add_default_entities(NULL);

		if (*p > 1){
			fprintf(stderr, "Unsupported signer type: %s\n", ENUMSTRING(*p, _signer_types));
			return -1;
		}
		fprintf(stdout, "\t<signer type =\"%s\"", ENUMSTRING(*p, _signer_types));
		if (*p == 1){
			fprintf(stdout, " digest=\"");
			print_x(stdout, p + 1, 8);
			fprintf(stdout, "\"");
			p += 8;
		}
		fprintf(stdout, "/>\n");
		p++;
		const SubjectInfo * si = (const SubjectInfo *)p;
		if (si->subject_type > crl_signer){
			fprintf(stderr, "Unsupported subject type: %d\n", si->subject_type);
			return -1;
		}
		fprintf(stdout, "\t<subject type =\"%s\"", ENUMSTRING(si->subject_type, _subjectTypes));
		if (si->name_length > 0){
			fprintf(stdout, " name=\"");
			fwrite(si->name, 1, si->name_length, stdout);
			fprintf(stdout, "\"");
		}
		fprintf(stdout, ">\n");
		p += 2 + si->name_length;
		length = cintx_read(&p, e, NULL);
		print_attributes(&p, p + length);
		fprintf(stdout, "\t</subject>\n");

		length = cintx_read(&p, e, NULL);
		fprintf(stdout, "\t<validity>\n");
		print_validity(&p, p + length);
		fprintf(stdout, "\t</validity>\n");

		fprintf(stdout, "\t<signature algorithm=\"%s\"/>\n", ENUMSTRING(*p, _signature_algorithms));
		fprintf(stdout, "</certificate>\n");
	}

	if (_issuerPath){

		EC_KEY * key;
		EC_GROUP * group;

		rc = -1;
		group = EC_GROUP_new_by_curve_name(NID_X9_62_prime256v1);

		if (_usePKey){
			// next is a public key
			key = load_public_key(_issuerPath, group);
			if (key == NULL){
				perror(_issuerPath);
				return -1;
			}
		}
		else{
			// load certificate
			char * c;
			size_t l;
			char idigest[8];
			const char * digest;
			if (0 > (l = load_certificate(_issuerPath, &c))){
				return -1;
			}
			key = get_verification_key(c, l, group);
			calculate_certificate_digest(c, l, idigest);
			free(c);
			if (key == NULL){
				fprintf(stderr, "%s: certificate parsing error\n", argv[2]);
				return -1;
			}
			// check signer info
			if (cert[1] == si_digest){
				digest = cert + 2;
				if (memcmp(digest, idigest, 8)){
					fprintf(stderr, "Signer digest mismatch: \n");
					fprintf(stderr, "    Signer info: "); print_x(stderr, digest, 8);  fprintf(stderr, "\n");
					fprintf(stderr, "    Issuer     : "); print_x(stderr, idigest, 8); fprintf(stderr, "\n");
				}
			}
		}
		// calc cert hash
		unsigned char hash[32];
		SHA256_CTX ctx;

		SHA256_Init(&ctx);
		SHA256_Update(&ctx, cert, certlen - 66);
		SHA256_Final(hash, &ctx);

		ECDSA_SIG * ecdsa;
		ecdsa = ECDSA_SIG_new();
		const char * r = cert + certlen - 64;
		const char * s = cert + certlen - 32;

		if (ecdsa->r == BN_bin2bn((const unsigned char *)r, 32, ecdsa->r) &&
			ecdsa->s == BN_bin2bn((const unsigned char *)s, 32, ecdsa->s)){
			rc = ECDSA_do_verify(hash, 32, ecdsa, key);
		}
		if (rc < 0){
			printf("ERROR");
			ERR_print_errors_fp(stderr);
		}
		else if (rc > 0){
			printf("OK");
		}
		else{
			printf("FAILED");
		}
	}
	return rc;
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
		fprintf(stderr, "%s: File too small\n", path);
		return -1;
	}
	cert = malloc(size);
	if (size != fread(cert, 1, size, f)){
		perror(path);
		free(cert);
		return -1;
	}
	if (cert[0] != 2) {
		// try hexadecimal
		int i;
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
	case 1: /* digest */
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

static void print_x(FILE * f, const char * ptr, int len)
{
	const unsigned char * e = (const unsigned char *)(ptr + len);
	for (; ptr < e; ptr++){
		unsigned char c = *ptr;
		fprintf(f, "%02X", c);
	}
}

static int 	   print_aid_list(const char ** pp, const char * e);
static int 	   print_aid_ssp_list(const char ** pp, const char * e);

static int     print_attributes(const char ** pp, const char * e)
{
	int rc = 0;
	const char * p = *pp;
	unsigned char atype;
	int length;
	while (rc == 0 && p < e){
		const PublicKey * key;
		const EccPoint *  point;

		atype = *p++;

		if (atype > 33){
			return -1;
		}
		fprintf(stdout, "\t\t<attribute type=\"%s\">\n", ENUMSTRING(atype, _subject_attribute_types));
		switch (atype){
		case verification_key:
		case encryption_key:
			key = (const PublicKey *)p;
			switch (key->algorithm){
			case 0:
				fprintf(stdout, "\t\t\t<public_key algorythm=\"%s\">\n", 
					ENUMSTRING(key->algorithm, _pk_algorithms));
				EccPoint_Print(stdout, "\t\t\t\t", &key->u.key);
				fprintf(stdout, "\t\t\t</public_key>\n");
				break;
			case 1:
				fprintf(stdout, "\t\t\t<public_key algorythm=\"%s\" symm_alg=\"%s\">\n",
					ENUMSTRING(key->algorithm, _pk_algorithms),
					ENUMSTRING(key->u.other.sym_alg, _sym_algorithms));
				EccPoint_Print(stdout, "\t\t\t\t", &key->u.other.key);
				fprintf(stdout, "\t\t\t</public_key>\n");
				break;
			default:
				fprintf(stdout, "\t\t\t<public_key algorythm=\"%d\">\n", key->algorithm);
			}
			p += PublicKey_Size(key);
			break;
		case assurance_level:
		{
			unsigned char n = *p;
			fprintf(stdout,
				"\t\t\t<assurance level=\"%u\" confidence=\"%u\"/>\n",
				(n >> 5), n & 0x3);
			p++;
		}
			break;
		case reconstruction_value:
			point = (const EccPoint *)p;
			EccPoint_Print(stdout, "\t\t\t", point);
			p += EccPoint_Size(point);
			break;
		case its_aid_list:
			length = cintx_read(&p, e, NULL);
			rc = print_aid_list(&p, p + length);
			break;
		case its_aid_ssp_list:
			length = cintx_read(&p, e, NULL);
			rc = print_aid_ssp_list(&p, p + length);
			break;
		default:
			rc = -1;
			break;
		}
		fprintf(stdout, "\t\t</attribute>\n");
	}

	*pp = e;
	return rc;
}

static int 	   print_aid_list(const char ** pp, const char * e)
{
	int rc = 0;
	const char *p = *pp;
	while (rc == 0 && p < e){
		int n = cintx_read(&p, e, &rc);
		fprintf(stdout, "\t\t\t<aid value=\"%d\"/>\n", n);
	}
	*pp = e;
	return rc;
}

static int 	   print_aid_ssp_list(const char ** pp, const char * e)
{
	int rc = 0;
	const char *p = *pp;
	while (rc == 0 && p < e){
		char * data;
		int n = cintx_read(&p, e, &rc);
		int len = cintx_read(&p, e, &rc);
		fprintf(stdout, "\t\t\t<ssp aid=\"%d\"/>", n);
		if (len){
			int r = cxml_text_encode(NULL, &data, p, len);
			if (r > 0){
				fwrite(data, 1, r, stdout);
				cxml_free(NULL, data);
			}
		}
		fprintf(stdout, "</ssp>\n");
		p += len;
	}
	*pp = e;
	return rc;
}

static int     print_validity(const char ** pp, const char * e)
{
	int rc = 0;
	const char *p = *pp;
	ValidityRestrictionType vtype;
	RegionType rtype;
	int length;
	unsigned int start, end, duration;
	while (rc == 0 && p < e){
		vtype = cint8_read(&p, e, &rc);
		if (rc) break;
		switch (vtype){
		case time_start_and_duration:
			start = cint32_read(&p, e, &rc);
			duration = cint32_read(&p, e, &rc);
			if (rc == 0){
				if (_numeric){
					fprintf(stdout, "\t\t<restriction type=\"%d\" start=\"%d\" duration=\"%d\"/>\n",
						vtype, start, duration);
				}
				else{
					fprintf(stdout, "\t\t<restriction type=\"time\" start=\"%s\" duration=\"%s\"/>\n",
						stritsdate32(start), stritsdate32(duration));
				}
			}
			break;
		case time_start_and_end:
			start = cint32_read(&p, e, &rc);
			end = cint32_read(&p, e, &rc);
			if (rc == 0){
				if (_numeric){
					fprintf(stdout, "\t\t<restriction type=\"%d\" start=\"%d\" end=\"%d\"/>\n",
						vtype, start, end);
				} else {
					fprintf(stdout, "\t\t<restriction type=\"time\" start=\"%s\" end=\"%s\"/>\n",
						stritsdate32(start), stritsdate32(end));
				}
			}
			break;
		case time_end:
			end = cint32_read(&p, e, &rc);
			if (rc == 0){
				if (_numeric){
					fprintf(stdout, "\t\t<restriction type=\"%d\" end=\"%d\"/>\n",
						vtype, end);
				} else {
					fprintf(stdout, "\t\t<restriction type=\"time\" end=\"%s\"/>\n",
						stritsdate32(end));
				}
			}
			break;
		case region:
			fprintf(stdout, "\t\t<restriction type=\"%s\">\n", _numeric ? "4" : "region");
			rtype = cint8_read(&p, e, &rc);
			if (rc == 0){
				int latitude, longitude, radius;
				switch (rtype){
				case region_none:
					fprintf(stdout, "\t\t\t<none/>\n");
					break;
				case region_circle:
					latitude = (int)cint32_read(&p, e, &rc);
					longitude = (int)cint32_read(&p, e, &rc);
					radius = (int)cint16_read(&p, e, &rc);
					fprintf(stdout, "\t\t\t<circle latitude=\"%d\" longitude=\"%d\" radius=\"%u\"/>\n",
						latitude, longitude, radius);
					break;
				case region_rectangle:
				case region_polygon:
					fprintf(stdout, "\t\t\t<%s>\n", _region_tag[rtype]);
					length = (int)cintx_read(&p, e, &rc);
					if (length) {
						const char * pe = p + length;
						if (pe > e){
							rc = -1;
							break;
						}
						while (rc == 0 && p < pe){
							latitude = (int)cint32_read(&p, e, &rc);
							if (rc == 0){
								longitude = (int)cint32_read(&p, e, &rc);
								if (rc == 0){
									fprintf(stdout, "\t\t\t\t<location latitude=\"%d\" longitude=\"%d\"/>\n",
										latitude, longitude);
								}
							}
						}
					}
					fprintf(stdout, "\t\t\t</%s>\n", _region_tag[rtype]);
					break;
				case region_id:
				{
					RegionDictionary dict = cint8_read(&p, e, &rc);
					if (rc == 0){
						uint16_t id = cint16_read(&p, e, &rc);
						if (rc == 0){
							int local = cintx_read(&p, e, &rc);
							if (rc == 0){
								fprintf(stdout, "\t\t\t<id dictionary=\"%s\" id=\"%u\" local=\"%u\"/>\n",
									ENUMSTRING(dict, _id_region_dicts), id, local);
							}
						}
					}
				}
				break;
				}
			}
			fprintf(stdout, "\t\t</restriction>\n");
		}
	}
	*pp = e;
	return rc;
}


