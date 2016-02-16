/*********************************************************************
######################################################################
##
##  Created by: Denis Filatov
##
##  Copyleft (c) 2015
##  This code is provided under the CeCill-C license agreement.
######################################################################
*********************************************************************/
#include "ecc_api.h"
#include <stdio.h>
static const char *_hex = "0123456789ABCDEF";
static void bn2buf(char * buf, const unsigned char * p)
{
	const unsigned char * e = p+32;
	for(;p<e;p++){
		unsigned char c = *p;
		*(buf++) = _hex[c>>4];
		*(buf++) = _hex[c&0x0F];
	}
	*buf = 0;
}

int main(int argc, char** argv)
{
	ecc_point_t point;
	
	if(0 == ecc_api_init()){
		int i;
		for(i=0; i<=4; i++){
			if(i==1) continue;
			point.type = i;
			char s[256];
			sprintf(s, "key%d.pem", i);
			if(0 == ecc_api_keygen(0, 0, s, &point)){
				FILE * f;
				sprintf(s, "key%d.pnt", i);
				f = fopen(s, "w");
				if(f){
					bn2buf(s, &point.x[0]);
					fprintf(f, "x=%s\n", s);
					if(point.type == compressed_lsb_y_0){
						fprintf(f, "y=0\n");
					}else if(point.type == compressed_lsb_y_1){
						fprintf(f, "y=1\n");
					}else if(point.type == uncompressed){
						bn2buf(s, &point.y[0]);
						fprintf(f, "y=%s\n", s);
					}
					fclose(f);
				}
			}
		}
	}
	ecc_api_done();
	return 1;
}
