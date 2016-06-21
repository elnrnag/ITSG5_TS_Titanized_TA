/*
 * Generated by asn1c-0.9.27 (http://lionet.info/asn1c)
 * From ASN.1 module "DSRC"
 * 	found in "DSRC_REG_D.asn"
 * 	`asn1c -gen-PER`
 */

#ifndef	_OverlayLaneList_H_
#define	_OverlayLaneList_H_


#include <asn_application.h>

/* Including external dependencies */
#include "LaneID.h"
#include <asn_SEQUENCE_OF.h>
#include <constr_SEQUENCE_OF.h>

#ifdef __cplusplus
extern "C" {
#endif

/* OverlayLaneList */
typedef struct OverlayLaneList {
	A_SEQUENCE_OF(LaneID_t) list;
	
	/* Context for parsing across buffer boundaries */
	asn_struct_ctx_t _asn_ctx;
} OverlayLaneList_t;

/* Implementation */
extern asn_TYPE_descriptor_t asn_DEF_OverlayLaneList;

#ifdef __cplusplus
}
#endif

#endif	/* _OverlayLaneList_H_ */
#include <asn_internal.h>