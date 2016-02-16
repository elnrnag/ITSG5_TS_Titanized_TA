// This C++ source file was generated by the TTCN-3 compiler
// of the TTCN-3 Test Executor version CRL 113 200/5 R4A
// for Aitor (aitorkun@aitorkun-HP-EliteBook-2530p) on Tue Feb 16 10:46:43 2016

// Copyright (c) 2000-2015 Ericsson Telecom AB

// Do not edit this file unless you know what you are doing.

/* Including header files */

#include "CAM_Types.hh"

namespace CAM__Types {

/* Prototypes of static functions */

static void pre_init_module();
static void post_init_module();

/* Literal string constants */

static const unsigned char module_checksum[] = { 0x53, 0x5b, 0xe2, 0xf9, 0x77, 0xab, 0x07, 0x4f, 0x9c, 0x40, 0x64, 0xd7, 0x2c, 0xab, 0xe4, 0x80 };

/* Global variable definitions */

static const size_t num_namespaces = 0;
TTCN_Module module_object("CAM_Types", __DATE__, __TIME__, module_checksum, pre_init_module, NULL, 0U, 4294967295U, 4294967295U, 4294967295U, NULL, 0LU, 0, post_init_module, NULL, NULL, NULL, NULL, NULL, NULL);

static const RuntimeVersionChecker ver_checker(  current_runtime_version.requires_major_version_5,
  current_runtime_version.requires_minor_version_4,
  current_runtime_version.requires_patch_level_0,  current_runtime_version.requires_runtime_1);

/* Bodies of functions, altsteps and testcases */

OCTETSTRING f__enc__CamReq(const LibItsCam__TestSystem::CamReq& pdu)
{
TTCN_Location current_location("CAM_Types.ttcn", 27, TTCN_Location::LOCATION_FUNCTION, "f_enc_CamReq");
current_location.update_lineno(28);
/* CAM_Types.ttcn, line 28 */
return enc__CAM__PDU(const_cast< const LibItsCam__TestSystem::CamReq&>(pdu).msgOut());
}

LibItsCam__TestSystem::CamReq f__dec__CamReq(const OCTETSTRING& stream)
{
TTCN_Location current_location("CAM_Types.ttcn", 31, TTCN_Location::LOCATION_FUNCTION, "f_dec_CamReq");
current_location.update_lineno(32);
/* CAM_Types.ttcn, line 32 */
LibItsCam__TestSystem::CamReq v__CamReq;
current_location.update_lineno(33);
/* CAM_Types.ttcn, line 33 */
v__CamReq.msgOut() = dec__CAM__PDU(stream);
current_location.update_lineno(34);
/* CAM_Types.ttcn, line 34 */
return v__CamReq;
}

LibItsCam__TestSystem::CamInd f__dec__CamInd(const OCTETSTRING& stream)
{
TTCN_Location current_location("CAM_Types.ttcn", 40, TTCN_Location::LOCATION_FUNCTION, "f_dec_CamInd");
current_location.update_lineno(41);
/* CAM_Types.ttcn, line 41 */
LibItsCam__TestSystem::CamInd v__CamInd;
current_location.update_lineno(42);
/* CAM_Types.ttcn, line 42 */
TTCN__EncDec::ExtCamInd v__ExtCamInd;
current_location.update_lineno(43);
/* CAM_Types.ttcn, line 43 */
OCTETSTRING v__os__per;
current_location.update_lineno(43);
/* CAM_Types.ttcn, line 43 */
OCTETSTRING v__os__raw;
current_location.update_lineno(45);
/* CAM_Types.ttcn, line 45 */
INTEGER v__lengthof__ExtCamInd(12);
current_location.update_lineno(47);
/* CAM_Types.ttcn, line 47 */
INTEGER v__start__of__raw((stream.lengthof() - v__lengthof__ExtCamInd));
current_location.update_lineno(49);
/* CAM_Types.ttcn, line 49 */
v__os__per = substr(stream, 0, v__start__of__raw);
current_location.update_lineno(50);
/* CAM_Types.ttcn, line 50 */
v__os__raw = substr(stream, v__start__of__raw, v__lengthof__ExtCamInd);
current_location.update_lineno(52);
/* CAM_Types.ttcn, line 52 */
v__ExtCamInd = TTCN__EncDec::f__dec__ExtCamInd(v__os__raw);
current_location.update_lineno(54);
/* CAM_Types.ttcn, line 54 */
v__CamInd.msgIn() = dec__CAM__PDU(v__os__per);
current_location.update_lineno(56);
/* CAM_Types.ttcn, line 56 */
v__CamInd.gnNextHeader() = const_cast< const TTCN__EncDec::ExtCamInd&>(v__ExtCamInd).gnNextHeader();
current_location.update_lineno(57);
/* CAM_Types.ttcn, line 57 */
v__CamInd.gnHeaderType() = const_cast< const TTCN__EncDec::ExtCamInd&>(v__ExtCamInd).gnHeaderType();
current_location.update_lineno(58);
/* CAM_Types.ttcn, line 58 */
v__CamInd.gnHeaderSubtype() = const_cast< const TTCN__EncDec::ExtCamInd&>(v__ExtCamInd).gnHeaderSubtype();
current_location.update_lineno(59);
/* CAM_Types.ttcn, line 59 */
v__CamInd.gnLifetime() = const_cast< const TTCN__EncDec::ExtCamInd&>(v__ExtCamInd).gnLifetime();
current_location.update_lineno(60);
/* CAM_Types.ttcn, line 60 */
v__CamInd.gnTrafficClass() = const_cast< const TTCN__EncDec::ExtCamInd&>(v__ExtCamInd).gnTrafficClass();
current_location.update_lineno(61);
/* CAM_Types.ttcn, line 61 */
v__CamInd.btpDestinationPort() = const_cast< const TTCN__EncDec::ExtCamInd&>(v__ExtCamInd).btpDestinationPort();
current_location.update_lineno(62);
/* CAM_Types.ttcn, line 62 */
v__CamInd.btpInfo() = const_cast< const TTCN__EncDec::ExtCamInd&>(v__ExtCamInd).btpInfo();
current_location.update_lineno(64);
/* CAM_Types.ttcn, line 64 */
return v__CamInd;
}


/* Bodies of static functions */

static void pre_init_module()
{
TTCN_Location current_location("CAM_Types.ttcn", 0, TTCN_Location::LOCATION_UNKNOWN, "CAM_Types");
CAM__PDU__Descriptions::module_object.pre_init_module();
LibItsCam__TestSystem::module_object.pre_init_module();
TTCN__EncDec::module_object.pre_init_module();
module_object.add_function("enc_CAM_PDU", (genericfunc_t)&enc__CAM__PDU, NULL);
module_object.add_function("dec_CAM_PDU", (genericfunc_t)&dec__CAM__PDU, NULL);
module_object.add_function("f_enc_CamReq", (genericfunc_t)&f__enc__CamReq, NULL);
module_object.add_function("f_dec_CamReq", (genericfunc_t)&f__dec__CamReq, NULL);
module_object.add_function("f_dec_CamInd", (genericfunc_t)&f__dec__CamInd, NULL);
}

static void post_init_module()
{
TTCN_Location current_location("CAM_Types.ttcn", 0, TTCN_Location::LOCATION_UNKNOWN, "CAM_Types");
LibItsCam__TestSystem::module_object.post_init_module();
TTCN__EncDec::module_object.post_init_module();
}


} /* end of namespace */
