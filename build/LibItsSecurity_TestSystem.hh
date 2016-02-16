// This C++ header file was generated by the TTCN-3 compiler
// of the TTCN-3 Test Executor version CRL 113 200/5 R4A
// for Aitor (aitorkun@aitorkun-HP-EliteBook-2530p) on Tue Feb 16 10:46:43 2016

// Copyright (c) 2000-2015 Ericsson Telecom AB

// Do not edit this file unless you know what you are doing.

#ifndef LibItsSecurity__TestSystem_HH
#define LibItsSecurity__TestSystem_HH

#ifdef TITAN_RUNTIME_2
#error Generated code does not match with used runtime.\
 Code was generated without -R option but -DTITAN_RUNTIME_2 was used.
#endif

/* Header file includes */

#include "LibItsSecurity_Pixits.hh"

#if TTCN3_VERSION != 50400
#error Version mismatch detected.\
 Please check the version of the TTCN-3 compiler and the base library.
#endif

#ifndef LINUX
#error This file should be compiled on LINUX
#endif

namespace LibItsSecurity__TestSystem {

/* Type definitions */

typedef COMPONENT ItsSecurityBaseComponent;
typedef COMPONENT_template ItsSecurityBaseComponent_template;

/* Global variable declarations */

extern const TTCN_Typedescriptor_t& ItsSecurityBaseComponent_descr_;
extern LibItsSecurity__TypesAndValues::Certificate ItsSecurityBaseComponent_component_vc__aaCertificate;
extern LibItsSecurity__TypesAndValues::Certificate ItsSecurityBaseComponent_component_vc__atCertificate;
extern LibItsSecurity__TypesAndValues::Certificate ItsSecurityBaseComponent_component_vc__lastAtCertificateUsed;
extern CHARSTRING ItsSecurityBaseComponent_component_vc__hashedId8ToBeUsed;
extern OCTETSTRING ItsSecurityBaseComponent_component_vc__signingPrivateKey;
extern OCTETSTRING ItsSecurityBaseComponent_component_vc__encryptPrivateKey;
extern LibItsSecurity__TypesAndValues::ThreeDLocation ItsSecurityBaseComponent_component_vc__location;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__A;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__B;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__C;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__D;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__E;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__F;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__G;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__EC;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__AA;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__B__BO;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__C__BO;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__D__BO;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert__E__BO;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert0101BO;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert0102BO;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert0103BO;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__taCert0104BO;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__iutCert__A;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__iutCert__B;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__iutCert__C;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__iutCert__D;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__iutCert__E;
extern const CHARSTRING& ItsSecurityBaseComponent_component_cc__iutCert__F;
extern TTCN_Module module_object;

} /* end of namespace */

#endif
