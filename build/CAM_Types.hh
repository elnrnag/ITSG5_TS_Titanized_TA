// This C++ header file was generated by the TTCN-3 compiler
// of the TTCN-3 Test Executor version CRL 113 200/5 R4A
// for Aitor (aitorkun@aitorkun-HP-EliteBook-2530p) on Tue Feb 16 10:46:43 2016

// Copyright (c) 2000-2015 Ericsson Telecom AB

// Do not edit this file unless you know what you are doing.

#ifndef CAM__Types_HH
#define CAM__Types_HH

#ifdef TITAN_RUNTIME_2
#error Generated code does not match with used runtime.\
 Code was generated without -R option but -DTITAN_RUNTIME_2 was used.
#endif

/* Header file includes */

#include "CAM_PDU_Descriptions.hh"
#include "LibItsCam_TestSystem.hh"
#include "TTCN_EncDec.hh"

#if TTCN3_VERSION != 50400
#error Version mismatch detected.\
 Please check the version of the TTCN-3 compiler and the base library.
#endif

#ifndef LINUX
#error This file should be compiled on LINUX
#endif

namespace CAM__Types {

/* Function prototypes */

extern OCTETSTRING enc__CAM__PDU(const CAM__PDU__Descriptions::CAM& pdu);
extern CAM__PDU__Descriptions::CAM dec__CAM__PDU(const OCTETSTRING& stream);
extern OCTETSTRING f__enc__CamReq(const LibItsCam__TestSystem::CamReq& pdu);
extern LibItsCam__TestSystem::CamReq f__dec__CamReq(const OCTETSTRING& stream);
extern LibItsCam__TestSystem::CamInd f__dec__CamInd(const OCTETSTRING& stream);

/* Global variable declarations */

extern TTCN_Module module_object;

} /* end of namespace */

#endif
