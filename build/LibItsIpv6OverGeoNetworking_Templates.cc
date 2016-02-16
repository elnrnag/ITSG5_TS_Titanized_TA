// This C++ source file was generated by the TTCN-3 compiler
// of the TTCN-3 Test Executor version CRL 113 200/5 R4A
// for Aitor (aitorkun@aitorkun-HP-EliteBook-2530p) on Tue Feb 16 10:46:43 2016

// Copyright (c) 2000-2015 Ericsson Telecom AB

// Do not edit this file unless you know what you are doing.

/* Including header files */

#include "LibItsIpv6OverGeoNetworking_Templates.hh"

namespace LibItsIpv6OverGeoNetworking__Templates {

/* Prototypes of static functions */

static void pre_init_module();
static void post_init_module();

/* Literal string constants */

static const OCTETSTRING os_0(0, NULL);
static const unsigned char module_checksum[] = { 0x1e, 0x3e, 0x56, 0xd2, 0xb8, 0x9b, 0x58, 0x13, 0xc9, 0x6a, 0xb2, 0x0f, 0xf8, 0xa0, 0xd0, 0xbc };

/* Global variable definitions */

static LibItsIpv6OverGeoNetworking__TypesAndValues::AcGn6Primitive_template template_m__acGetInterfaceInfos;
const LibItsIpv6OverGeoNetworking__TypesAndValues::AcGn6Primitive_template& m__acGetInterfaceInfos = template_m__acGetInterfaceInfos;
static LibItsIpv6OverGeoNetworking__TypesAndValues::AcGn6Response_template template_mw__acInterfaceInfos;
const LibItsIpv6OverGeoNetworking__TypesAndValues::AcGn6Response_template& mw__acInterfaceInfos = template_mw__acInterfaceInfos;
static const size_t num_namespaces = 0;
TTCN_Module module_object("LibItsIpv6OverGeoNetworking_Templates", __DATE__, __TIME__, module_checksum, pre_init_module, NULL, 0U, 4294967295U, 4294967295U, 4294967295U, NULL, 0LU, 0, post_init_module, NULL, NULL, NULL, NULL, NULL, NULL);

static const RuntimeVersionChecker ver_checker(  current_runtime_version.requires_major_version_5,
  current_runtime_version.requires_minor_version_4,
  current_runtime_version.requires_patch_level_0,  current_runtime_version.requires_runtime_1);

/* Bodies of functions, altsteps and testcases */

LibItsIpv6OverGeoNetworking__TestSystem::IPv6OverGeoNetworkingReq_template m__ipv6OverGeoNwReq(const CHARSTRING_template& p__interface, const OCTETSTRING_template& p__srcMacAddr, const OCTETSTRING_template& p__dstMacAddr, const LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Packet_template& p__ipv6Packet)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 32, TTCN_Location::LOCATION_TEMPLATE, "m_ipv6OverGeoNwReq");
LibItsIpv6OverGeoNetworking__TestSystem::IPv6OverGeoNetworkingReq_template ret_val;
ret_val.interface() = p__interface;
ret_val.macSourceAddress() = p__srcMacAddr;
ret_val.macDestinationAddress() = p__dstMacAddr;
ret_val.ipv6Packet() = p__ipv6Packet;
return ret_val;
}

LibItsIpv6OverGeoNetworking__TestSystem::IPv6OverGeoNetworkingInd_template mw__ipv6OverGeoNwInd(const CHARSTRING_template& p__interface, const OCTETSTRING_template& p__srcMacAddr, const OCTETSTRING_template& p__dstMacAddr, const LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Packet_template& p__ipv6Packet)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 51, TTCN_Location::LOCATION_TEMPLATE, "mw_ipv6OverGeoNwInd");
LibItsIpv6OverGeoNetworking__TestSystem::IPv6OverGeoNetworkingInd_template ret_val;
ret_val.interface() = p__interface;
ret_val.macSourceAddress() = p__srcMacAddr;
ret_val.macDestinationAddress() = p__dstMacAddr;
ret_val.ipv6Packet() = p__ipv6Packet;
return ret_val;
}

LibItsGeoNetworking__TypesAndValues::Payload_template m__ipv6Payload(const LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Packet_template& p__ipv6Packet)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 71, TTCN_Location::LOCATION_TEMPLATE, "m_ipv6Payload");
LibItsGeoNetworking__TypesAndValues::Payload_template ret_val;
ret_val.decodedPayload().ipv6Packet() = p__ipv6Packet;
ret_val.rawPayload() = os_0;
return ret_val;
}

LibItsGeoNetworking__TypesAndValues::Payload_template mw__ipv6Payload(const LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Packet_template& p__ipv6Packet)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 80, TTCN_Location::LOCATION_TEMPLATE, "mw_ipv6Payload");
LibItsGeoNetworking__TypesAndValues::Payload_template ret_val;
ret_val.decodedPayload().ipv6Packet() = p__ipv6Packet;
ret_val.rawPayload() = ANY_VALUE;
return ret_val;
}

LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Packet_template m__ipv6Packet(const OCTETSTRING_template& p__srcAddr, const OCTETSTRING_template& p__dstAddr, const INTEGER_template& p__nextHdr, const LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Payload_template& p__payload)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 92, TTCN_Location::LOCATION_TEMPLATE, "m_ipv6Packet");
LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Packet_template ret_val;
ret_val.ipv6Hdr() = m__ipv6Header(p__srcAddr, p__dstAddr, p__nextHdr);
ret_val.extHdrList() = OMIT_VALUE;
ret_val.ipv6Payload() = p__payload;
return ret_val;
}

LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Packet_template mw__ipv6Packet(const OCTETSTRING_template& p__srcAddr, const OCTETSTRING_template& p__dstAddr, const INTEGER_template& p__nextHdr, const LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Payload_template& p__payload)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 110, TTCN_Location::LOCATION_TEMPLATE, "mw_ipv6Packet");
LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Packet_template ret_val;
ret_val.ipv6Hdr() = mw__ipv6Header(p__srcAddr, p__dstAddr, p__nextHdr);
ret_val.extHdrList() = OMIT_VALUE;
ret_val.ipv6Payload() = p__payload;
return ret_val;
}

LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Header_template m__ipv6Header(const OCTETSTRING_template& p__srcAddr, const OCTETSTRING_template& p__dstAddr, const INTEGER_template& p__nextHdr)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 127, TTCN_Location::LOCATION_TEMPLATE, "m_ipv6Header");
LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Header_template ret_val;
ret_val.version() = 6;
ret_val.trafficClass() = 0;
ret_val.flowLabel() = 0;
ret_val.payloadLength() = 0;
ret_val.nextHeader() = p__nextHdr;
ret_val.hopLimit() = 255;
ret_val.sourceAddress() = p__srcAddr;
ret_val.destinationAddress() = p__dstAddr;
return ret_val;
}

LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Header_template mw__ipv6Header(const OCTETSTRING_template& p__srcAddr, const OCTETSTRING_template& p__dstAddr, const INTEGER_template& p__nextHdr)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 148, TTCN_Location::LOCATION_TEMPLATE, "mw_ipv6Header");
LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Header_template ret_val;
ret_val.version() = 6;
ret_val.trafficClass() = ANY_VALUE;
ret_val.flowLabel() = ANY_VALUE;
ret_val.payloadLength() = ANY_VALUE;
ret_val.nextHeader() = p__nextHdr;
ret_val.hopLimit() = ANY_VALUE;
ret_val.sourceAddress() = p__srcAddr;
ret_val.destinationAddress() = p__dstAddr;
return ret_val;
}

LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Payload_template m__rtAdvWithOptions(const LibItsIpv6OverGeoNetworking__TypesAndValues::RtAdvOptions_template& p__rtAdvOptions)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 167, TTCN_Location::LOCATION_TEMPLATE, "m_rtAdvWithOptions");
LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Payload_template ret_val;
{
LibItsIpv6OverGeoNetworking__TypesAndValues::RouterAdvertisementMsg_template& tmp_0 = ret_val.routerAdvMsg();
tmp_0.icmpType() = LibItsIpv6OverGeoNetworking__TypesAndValues::c__rtAdvMsg;
tmp_0.icmpCode() = 0;
tmp_0.checksum() = LibCommon__DataStrings::c__2ZeroBytes;
tmp_0.curHopLimit() = 255;
tmp_0.managedConfigFlag() = 0;
tmp_0.otherConfigFlag() = 0;
tmp_0.homeAgentFlag() = 0;
tmp_0.reserved() = 0;
tmp_0.routerLifetime() = LibCommon__BasicTypesAndValues::c__uInt16Max;
tmp_0.reachableTime() = LibCommon__BasicTypesAndValues::c__uInt32Max;
tmp_0.retransTimer() = 0;
tmp_0.rtAdvOptions() = p__rtAdvOptions;
}
return ret_val;
}

LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Payload_template mw__rtAdvWithOptions(const LibItsIpv6OverGeoNetworking__TypesAndValues::RtAdvOptions_template& p__rtAdvOptions)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 190, TTCN_Location::LOCATION_TEMPLATE, "mw_rtAdvWithOptions");
LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Payload_template ret_val;
{
LibItsIpv6OverGeoNetworking__TypesAndValues::RouterAdvertisementMsg_template& tmp_1 = ret_val.routerAdvMsg();
tmp_1.icmpType() = LibItsIpv6OverGeoNetworking__TypesAndValues::c__rtAdvMsg;
tmp_1.icmpCode() = 0;
tmp_1.checksum() = ANY_VALUE;
tmp_1.curHopLimit() = ANY_VALUE;
tmp_1.managedConfigFlag() = ANY_VALUE;
tmp_1.otherConfigFlag() = ANY_VALUE;
tmp_1.homeAgentFlag() = ANY_VALUE;
tmp_1.reserved() = ANY_VALUE;
tmp_1.routerLifetime() = ANY_VALUE;
tmp_1.reachableTime() = ANY_VALUE;
tmp_1.retransTimer() = ANY_VALUE;
tmp_1.rtAdvOptions() = p__rtAdvOptions;
}
return ret_val;
}

LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Payload_template m__octetstringPayload(const OCTETSTRING_template& p__payload)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 213, TTCN_Location::LOCATION_TEMPLATE, "m_octetstringPayload");
LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Payload_template ret_val;
ret_val.octetstringMsg() = p__payload;
return ret_val;
}

LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Payload_template mw__octetstringPayload(const OCTETSTRING_template& p__payload)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 223, TTCN_Location::LOCATION_TEMPLATE, "mw_octetstringPayload");
LibItsIpv6OverGeoNetworking__TypesAndValues::Ipv6Payload_template ret_val;
ret_val.octetstringMsg() = p__payload;
return ret_val;
}

LibItsIpv6OverGeoNetworking__TypesAndValues::RtAdvOptions_template m__rtAdvOpt__prefixOpt(const INTEGER_template& p__prefixLength, const INTEGER_template& p__lFlag, const INTEGER_template& p__aFlag, const INTEGER_template& p__validLifetime, const INTEGER_template& p__preferredLifetime, const OCTETSTRING_template& p__prefix)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 238, TTCN_Location::LOCATION_TEMPLATE, "m_rtAdvOpt_prefixOpt");
LibItsIpv6OverGeoNetworking__TypesAndValues::RtAdvOptions_template ret_val;
ret_val.srcLinkLayerAddr() = OMIT_VALUE;
{
LibItsIpv6OverGeoNetworking__TypesAndValues::PrefixInfoList_template& tmp_2 = ret_val.prefixInfoList();
tmp_2.set_size(1);
tmp_2[0] = m__prefixInfo(p__prefixLength, p__lFlag, p__aFlag, p__validLifetime, p__preferredLifetime, p__prefix);
}
ret_val.otherOption() = OMIT_VALUE;
return ret_val;
}

LibItsIpv6OverGeoNetworking__TypesAndValues::PrefixInfo_template m__prefixInfo(const INTEGER_template& p__prefixLength, const INTEGER_template& p__lFlag, const INTEGER_template& p__aFlag, const INTEGER_template& p__validLifetime, const INTEGER_template& p__preferredLifetime, const OCTETSTRING_template& p__prefix)
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 269, TTCN_Location::LOCATION_TEMPLATE, "m_prefixInfo");
LibItsIpv6OverGeoNetworking__TypesAndValues::PrefixInfo_template ret_val;
ret_val.icmpType() = LibItsIpv6OverGeoNetworking__TypesAndValues::c__prefixInfo;
ret_val.optionLength() = LibItsIpv6OverGeoNetworking__TypesAndValues::c__prefixInfoLen;
ret_val.prefixLength() = p__prefixLength;
ret_val.linkFlag() = p__lFlag;
ret_val.autoConfigFlag() = p__aFlag;
ret_val.rtAddrFlag() = LibItsIpv6OverGeoNetworking__TypesAndValues::c__rtAddrFlag0;
ret_val.reserved1() = 0;
ret_val.validLifetime() = p__validLifetime;
ret_val.preferredLifetime() = p__preferredLifetime;
ret_val.reserved2() = 0;
ret_val.prefix() = p__prefix;
return ret_val;
}


/* Bodies of static functions */

static void pre_init_module()
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 0, TTCN_Location::LOCATION_UNKNOWN, "LibItsIpv6OverGeoNetworking_Templates");
LibItsIpv6OverGeoNetworking__TestSystem::module_object.pre_init_module();
}

static void post_init_module()
{
TTCN_Location current_location("LibItsIpv6OverGeoNetworking_Templates.ttcn", 0, TTCN_Location::LOCATION_UNKNOWN, "LibItsIpv6OverGeoNetworking_Templates");
LibItsIpv6OverGeoNetworking__TestSystem::module_object.post_init_module();
current_location.update_lineno(297);
/* LibItsIpv6OverGeoNetworking_Templates.ttcn, line 297 */
template_m__acGetInterfaceInfos.getInterfaceInfos() = 3;
current_location.update_lineno(304);
/* LibItsIpv6OverGeoNetworking_Templates.ttcn, line 304 */
template_mw__acInterfaceInfos.interfaceInfoList() = ANY_VALUE;
}


} /* end of namespace */
