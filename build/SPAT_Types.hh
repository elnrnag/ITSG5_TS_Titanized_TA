// This C++ header file was generated by the TTCN-3 compiler
// of the TTCN-3 Test Executor version CRL 113 200/5 R4A
// for Aitor (aitorkun@aitorkun-HP-EliteBook-2530p) on Tue Feb 16 10:46:43 2016

// Copyright (c) 2000-2015 Ericsson Telecom AB

// Do not edit this file unless you know what you are doing.

#ifndef SPAT__Types_HH
#define SPAT__Types_HH

#ifdef TITAN_RUNTIME_2
#error Generated code does not match with used runtime.\
 Code was generated without -R option but -DTITAN_RUNTIME_2 was used.
#endif

/* Header file includes */

#include "LibItsMapSpat_TestSystem.hh"

#if TTCN3_VERSION != 50400
#error Version mismatch detected.\
 Please check the version of the TTCN-3 compiler and the base library.
#endif

#ifndef LINUX
#error This file should be compiled on LINUX
#endif

namespace SPAT__Types {

/* Function prototypes */

extern OCTETSTRING enc__SPAT__PDU(const MAP__SPAT__ETSI::SPAT__PDU& pdu);
extern MAP__SPAT__ETSI::SPAT__PDU dec__SPAT__PDU(const OCTETSTRING& stream);
extern OCTETSTRING f__enc__SpatReq(const LibItsMapSpat__TestSystem::SpatReq& pdu);

/* Global variable declarations */

extern TTCN_Module module_object;

} /* end of namespace */

#endif
