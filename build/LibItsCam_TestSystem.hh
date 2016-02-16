// This C++ header file was generated by the TTCN-3 compiler
// of the TTCN-3 Test Executor version CRL 113 200/5 R4A
// for Aitor (aitorkun@aitorkun-HP-EliteBook-2530p) on Tue Feb 16 10:46:43 2016

// Copyright (c) 2000-2015 Ericsson Telecom AB

// Do not edit this file unless you know what you are doing.

#ifndef LibItsCam__TestSystem_HH
#define LibItsCam__TestSystem_HH

#ifdef TITAN_RUNTIME_2
#error Generated code does not match with used runtime.\
 Code was generated without -R option but -DTITAN_RUNTIME_2 was used.
#endif

/* Header file includes */

#include "LibItsCommon_TestSystem.hh"
#include "LibItsCommon_TypesAndValues.hh"
#include "LibItsCam_TypesAndValues.hh"
#include "General_Types.hh"
#include "TTCN_EncDec.hh"

#if TTCN3_VERSION != 50400
#error Version mismatch detected.\
 Please check the version of the TTCN-3 compiler and the base library.
#endif

#ifndef LINUX
#error This file should be compiled on LINUX
#endif

#undef LibItsCam__TestSystem_HH
#endif

namespace LibItsCam__TestSystem {

/* Forward declarations of classes */

class UpperTesterCAMPort_BASE;
class UpperTesterCAMPort;
class CamPort_BASE;
class CamPort;
class CamInd;
class CamInd_template;
class CamReq;
class CamReq_template;

} /* end of namespace */

#ifndef LibItsCam__TestSystem_HH
#define LibItsCam__TestSystem_HH

namespace LibItsCam__TestSystem {

/* Type definitions */

typedef COMPONENT ItsCamSystem;
typedef COMPONENT_template ItsCamSystem_template;
typedef COMPONENT ItsCam;
typedef COMPONENT_template ItsCam_template;

/* Class definitions */

class UpperTesterCAMPort_BASE : public PORT {
enum msg_selection { MESSAGE_0, MESSAGE_1, MESSAGE_2, MESSAGE_3 };
struct msg_queue_item : public msg_queue_item_base {
msg_selection item_selection;
union {
LibItsCommon__TypesAndValues::UtInitializeResult *message_0;
LibItsCam__TypesAndValues::UtCamTriggerResult *message_1;
LibItsCommon__TypesAndValues::UtChangePositionResult *message_2;
LibItsCam__TypesAndValues::UtCamEventInd *message_3;
};
component sender_component;
};

void remove_msg_queue_head();
protected:
void clear_queue();
public:
UpperTesterCAMPort_BASE(const char *par_port_name);
~UpperTesterCAMPort_BASE();
void send(const LibItsCommon__TypesAndValues::UtInitialize& send_par, const COMPONENT& destination_component);
void send(const LibItsCommon__TypesAndValues::UtInitialize& send_par);
void send(const LibItsCommon__TypesAndValues::UtInitialize_template& send_par, const COMPONENT& destination_component);
void send(const LibItsCommon__TypesAndValues::UtInitialize_template& send_par);
void send(const LibItsCam__TypesAndValues::UtCamTrigger& send_par, const COMPONENT& destination_component);
void send(const LibItsCam__TypesAndValues::UtCamTrigger& send_par);
void send(const LibItsCam__TypesAndValues::UtCamTrigger_template& send_par, const COMPONENT& destination_component);
void send(const LibItsCam__TypesAndValues::UtCamTrigger_template& send_par);
void send(const LibItsCommon__TypesAndValues::UtChangePosition& send_par, const COMPONENT& destination_component);
void send(const LibItsCommon__TypesAndValues::UtChangePosition& send_par);
void send(const LibItsCommon__TypesAndValues::UtChangePosition_template& send_par, const COMPONENT& destination_component);
void send(const LibItsCommon__TypesAndValues::UtChangePosition_template& send_par);
protected:
virtual void outgoing_send(const LibItsCommon__TypesAndValues::UtInitialize& send_par) = 0;
virtual void outgoing_send(const LibItsCam__TypesAndValues::UtCamTrigger& send_par) = 0;
virtual void outgoing_send(const LibItsCommon__TypesAndValues::UtChangePosition& send_par) = 0;
public:
alt_status receive(const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status check_receive(const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status trigger(const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status receive(const LibItsCommon__TypesAndValues::UtInitializeResult_template& value_template, LibItsCommon__TypesAndValues::UtInitializeResult *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status check_receive(const LibItsCommon__TypesAndValues::UtInitializeResult_template& value_template, LibItsCommon__TypesAndValues::UtInitializeResult *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status trigger(const LibItsCommon__TypesAndValues::UtInitializeResult_template& value_template, LibItsCommon__TypesAndValues::UtInitializeResult *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status receive(const LibItsCam__TypesAndValues::UtCamTriggerResult_template& value_template, LibItsCam__TypesAndValues::UtCamTriggerResult *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status check_receive(const LibItsCam__TypesAndValues::UtCamTriggerResult_template& value_template, LibItsCam__TypesAndValues::UtCamTriggerResult *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status trigger(const LibItsCam__TypesAndValues::UtCamTriggerResult_template& value_template, LibItsCam__TypesAndValues::UtCamTriggerResult *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status receive(const LibItsCommon__TypesAndValues::UtChangePositionResult_template& value_template, LibItsCommon__TypesAndValues::UtChangePositionResult *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status check_receive(const LibItsCommon__TypesAndValues::UtChangePositionResult_template& value_template, LibItsCommon__TypesAndValues::UtChangePositionResult *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status trigger(const LibItsCommon__TypesAndValues::UtChangePositionResult_template& value_template, LibItsCommon__TypesAndValues::UtChangePositionResult *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status receive(const LibItsCam__TypesAndValues::UtCamEventInd_template& value_template, LibItsCam__TypesAndValues::UtCamEventInd *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status check_receive(const LibItsCam__TypesAndValues::UtCamEventInd_template& value_template, LibItsCam__TypesAndValues::UtCamEventInd *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status trigger(const LibItsCam__TypesAndValues::UtCamEventInd_template& value_template, LibItsCam__TypesAndValues::UtCamEventInd *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
private:
void incoming_message(const LibItsCommon__TypesAndValues::UtInitializeResult& incoming_par, component sender_component);
void incoming_message(const LibItsCam__TypesAndValues::UtCamTriggerResult& incoming_par, component sender_component);
void incoming_message(const LibItsCommon__TypesAndValues::UtChangePositionResult& incoming_par, component sender_component);
void incoming_message(const LibItsCam__TypesAndValues::UtCamEventInd& incoming_par, component sender_component);
protected:
inline void incoming_message(const LibItsCommon__TypesAndValues::UtInitializeResult& incoming_par) { incoming_message(incoming_par, SYSTEM_COMPREF); }
inline void incoming_message(const LibItsCam__TypesAndValues::UtCamTriggerResult& incoming_par) { incoming_message(incoming_par, SYSTEM_COMPREF); }
inline void incoming_message(const LibItsCommon__TypesAndValues::UtChangePositionResult& incoming_par) { incoming_message(incoming_par, SYSTEM_COMPREF); }
inline void incoming_message(const LibItsCam__TypesAndValues::UtCamEventInd& incoming_par) { incoming_message(incoming_par, SYSTEM_COMPREF); }
boolean process_message(const char *message_type, Text_Buf& incoming_buf, component sender_component, OCTETSTRING& slider);
};

class CamPort_BASE : public PORT {
enum msg_selection { MESSAGE_0 };
struct msg_queue_item : public msg_queue_item_base {
msg_selection item_selection;
union {
CamInd *message_0;
};
component sender_component;
};

void remove_msg_queue_head();
protected:
void clear_queue();
public:
CamPort_BASE(const char *par_port_name);
~CamPort_BASE();
void send(const CamReq& send_par, const COMPONENT& destination_component);
void send(const CamReq& send_par);
void send(const CamReq_template& send_par, const COMPONENT& destination_component);
void send(const CamReq_template& send_par);
protected:
virtual void outgoing_send(const CamReq& send_par) = 0;
public:
alt_status receive(const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status check_receive(const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status trigger(const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status receive(const CamInd_template& value_template, CamInd *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status check_receive(const CamInd_template& value_template, CamInd *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
alt_status trigger(const CamInd_template& value_template, CamInd *value_ptr, const COMPONENT_template& sender_template, COMPONENT *sender_ptr);
private:
void incoming_message(const CamInd& incoming_par, component sender_component);
protected:
inline void incoming_message(const CamInd& incoming_par) { incoming_message(incoming_par, SYSTEM_COMPREF); }
boolean process_message(const char *message_type, Text_Buf& incoming_buf, component sender_component, OCTETSTRING& slider);
};

class CamInd : public Base_Type {
  CAM__PDU__Descriptions::CAM field_msgIn;
  INTEGER field_gnNextHeader;
  INTEGER field_gnHeaderType;
  INTEGER field_gnHeaderSubtype;
  INTEGER field_gnLifetime;
  INTEGER field_gnTrafficClass;
  INTEGER field_btpDestinationPort;
  INTEGER field_btpInfo;
  boolean bound_flag;
public:
  CamInd();
  CamInd(const CAM__PDU__Descriptions::CAM& par_msgIn,
    const INTEGER& par_gnNextHeader,
    const INTEGER& par_gnHeaderType,
    const INTEGER& par_gnHeaderSubtype,
    const INTEGER& par_gnLifetime,
    const INTEGER& par_gnTrafficClass,
    const INTEGER& par_btpDestinationPort,
    const INTEGER& par_btpInfo);
  CamInd(const CamInd& other_value);
  inline boolean is_component() { return FALSE; }
  void clean_up();
  CamInd& operator=(const CamInd& other_value);
  boolean operator==(const CamInd& other_value) const;
  inline boolean operator!=(const CamInd& other_value) const
    { return !(*this == other_value); }

  boolean is_bound() const;

inline boolean is_present() const { return is_bound(); }
  boolean is_value() const;

  inline CAM__PDU__Descriptions::CAM& msgIn()
    {return field_msgIn;}
  inline const CAM__PDU__Descriptions::CAM& msgIn() const
    {return field_msgIn;}
  inline INTEGER& gnNextHeader()
    {return field_gnNextHeader;}
  inline const INTEGER& gnNextHeader() const
    {return field_gnNextHeader;}
  inline INTEGER& gnHeaderType()
    {return field_gnHeaderType;}
  inline const INTEGER& gnHeaderType() const
    {return field_gnHeaderType;}
  inline INTEGER& gnHeaderSubtype()
    {return field_gnHeaderSubtype;}
  inline const INTEGER& gnHeaderSubtype() const
    {return field_gnHeaderSubtype;}
  inline INTEGER& gnLifetime()
    {return field_gnLifetime;}
  inline const INTEGER& gnLifetime() const
    {return field_gnLifetime;}
  inline INTEGER& gnTrafficClass()
    {return field_gnTrafficClass;}
  inline const INTEGER& gnTrafficClass() const
    {return field_gnTrafficClass;}
  inline INTEGER& btpDestinationPort()
    {return field_btpDestinationPort;}
  inline const INTEGER& btpDestinationPort() const
    {return field_btpDestinationPort;}
  inline INTEGER& btpInfo()
    {return field_btpInfo;}
  inline const INTEGER& btpInfo() const
    {return field_btpInfo;}
  int size_of() const;
  void log() const;
  void set_param(Module_Param& param);
Module_Param* get_param(Module_Param_Name& param_name) const;
  void set_implicit_omit();
  void encode_text(Text_Buf& text_buf) const;
  void decode_text(Text_Buf& text_buf);
void encode(const TTCN_Typedescriptor_t&, TTCN_Buffer&, TTCN_EncDec::coding_t, ...) const;
void decode(const TTCN_Typedescriptor_t&, TTCN_Buffer&, TTCN_EncDec::coding_t, ...);
int RAW_encode(const TTCN_Typedescriptor_t&, RAW_enc_tree&) const;
int RAW_decode(const TTCN_Typedescriptor_t&, TTCN_Buffer&, int, raw_order_t, boolean no_err=FALSE,int sel_field=-1, boolean first_call=TRUE);
int JSON_encode(const TTCN_Typedescriptor_t&, JSON_Tokenizer&) const;
int JSON_decode(const TTCN_Typedescriptor_t&, JSON_Tokenizer&, boolean);
};

class CamInd_template : public Base_Template {
struct single_value_struct;
union {
single_value_struct *single_value;
struct {
unsigned int n_values;
CamInd_template *list_value;
} value_list;
};

void set_specific();
void copy_value(const CamInd& other_value);
void copy_template(const CamInd_template& other_value);

public:
CamInd_template();
CamInd_template(template_sel other_value);
CamInd_template(const CamInd& other_value);
CamInd_template(const OPTIONAL<CamInd>& other_value);
CamInd_template(const CamInd_template& other_value);
~CamInd_template();
CamInd_template& operator=(template_sel other_value);
CamInd_template& operator=(const CamInd& other_value);
CamInd_template& operator=(const OPTIONAL<CamInd>& other_value);
CamInd_template& operator=(const CamInd_template& other_value);
boolean match(const CamInd& other_value, boolean legacy = FALSE) const;
boolean is_bound() const;
boolean is_value() const;
void clean_up();
CamInd valueof() const;
void set_type(template_sel template_type, unsigned int list_length);
CamInd_template& list_item(unsigned int list_index) const;
CAM__PDU__Descriptions::CAM_template& msgIn();
const CAM__PDU__Descriptions::CAM_template& msgIn() const;
INTEGER_template& gnNextHeader();
const INTEGER_template& gnNextHeader() const;
INTEGER_template& gnHeaderType();
const INTEGER_template& gnHeaderType() const;
INTEGER_template& gnHeaderSubtype();
const INTEGER_template& gnHeaderSubtype() const;
INTEGER_template& gnLifetime();
const INTEGER_template& gnLifetime() const;
INTEGER_template& gnTrafficClass();
const INTEGER_template& gnTrafficClass() const;
INTEGER_template& btpDestinationPort();
const INTEGER_template& btpDestinationPort() const;
INTEGER_template& btpInfo();
const INTEGER_template& btpInfo() const;
int size_of() const;
void log() const;
void log_match(const CamInd& match_value, boolean legacy = FALSE) const;
void encode_text(Text_Buf& text_buf) const;
void decode_text(Text_Buf& text_buf);
void set_param(Module_Param& param);
Module_Param* get_param(Module_Param_Name& param_name) const;
void check_restriction(template_res t_res, const char* t_name=NULL, boolean legacy = FALSE) const;
boolean is_present(boolean legacy = FALSE) const;
boolean match_omit(boolean legacy = FALSE) const;
};

class CamReq : public Base_Type {
  CAM__PDU__Descriptions::CAM field_msgOut;
  boolean bound_flag;
public:
  CamReq();
  CamReq(const CAM__PDU__Descriptions::CAM& par_msgOut);
  CamReq(const CamReq& other_value);
  inline boolean is_component() { return FALSE; }
  void clean_up();
  CamReq& operator=(const CamReq& other_value);
  boolean operator==(const CamReq& other_value) const;
  inline boolean operator!=(const CamReq& other_value) const
    { return !(*this == other_value); }

  boolean is_bound() const;

inline boolean is_present() const { return is_bound(); }
  boolean is_value() const;

  inline CAM__PDU__Descriptions::CAM& msgOut()
    {return field_msgOut;}
  inline const CAM__PDU__Descriptions::CAM& msgOut() const
    {return field_msgOut;}
  int size_of() const;
  void log() const;
  void set_param(Module_Param& param);
Module_Param* get_param(Module_Param_Name& param_name) const;
  void set_implicit_omit();
  void encode_text(Text_Buf& text_buf) const;
  void decode_text(Text_Buf& text_buf);
void encode(const TTCN_Typedescriptor_t&, TTCN_Buffer&, TTCN_EncDec::coding_t, ...) const;
void decode(const TTCN_Typedescriptor_t&, TTCN_Buffer&, TTCN_EncDec::coding_t, ...);
int RAW_encode(const TTCN_Typedescriptor_t&, RAW_enc_tree&) const;
int RAW_decode(const TTCN_Typedescriptor_t&, TTCN_Buffer&, int, raw_order_t, boolean no_err=FALSE,int sel_field=-1, boolean first_call=TRUE);
int JSON_encode(const TTCN_Typedescriptor_t&, JSON_Tokenizer&) const;
int JSON_decode(const TTCN_Typedescriptor_t&, JSON_Tokenizer&, boolean);
};

class CamReq_template : public Base_Template {
struct single_value_struct;
union {
single_value_struct *single_value;
struct {
unsigned int n_values;
CamReq_template *list_value;
} value_list;
};

void set_specific();
void copy_value(const CamReq& other_value);
void copy_template(const CamReq_template& other_value);

public:
CamReq_template();
CamReq_template(template_sel other_value);
CamReq_template(const CamReq& other_value);
CamReq_template(const OPTIONAL<CamReq>& other_value);
CamReq_template(const CamReq_template& other_value);
~CamReq_template();
CamReq_template& operator=(template_sel other_value);
CamReq_template& operator=(const CamReq& other_value);
CamReq_template& operator=(const OPTIONAL<CamReq>& other_value);
CamReq_template& operator=(const CamReq_template& other_value);
boolean match(const CamReq& other_value, boolean legacy = FALSE) const;
boolean is_bound() const;
boolean is_value() const;
void clean_up();
CamReq valueof() const;
void set_type(template_sel template_type, unsigned int list_length);
CamReq_template& list_item(unsigned int list_index) const;
CAM__PDU__Descriptions::CAM_template& msgOut();
const CAM__PDU__Descriptions::CAM_template& msgOut() const;
int size_of() const;
void log() const;
void log_match(const CamReq& match_value, boolean legacy = FALSE) const;
void encode_text(Text_Buf& text_buf) const;
void decode_text(Text_Buf& text_buf);
void set_param(Module_Param& param);
Module_Param* get_param(Module_Param_Name& param_name) const;
void check_restriction(template_res t_res, const char* t_name=NULL, boolean legacy = FALSE) const;
boolean is_present(boolean legacy = FALSE) const;
boolean match_omit(boolean legacy = FALSE) const;
};


/* Global variable declarations */

extern const TTCN_Typedescriptor_t& ItsCamSystem_descr_;
extern UpperTesterCAMPort ItsCamSystem_component_utPort;
extern CamPort ItsCamSystem_component_camPort;
extern const TTCN_Typedescriptor_t& ItsCam_descr_;
extern UpperTesterCAMPort ItsCam_component_utPort;
extern CamPort ItsCam_component_camPort;
extern const XERdescriptor_t CamInd_gnNextHeader_xer_;
extern const TTCN_Typedescriptor_t CamInd_gnNextHeader_descr_;
extern const XERdescriptor_t CamInd_gnHeaderType_xer_;
extern const TTCN_Typedescriptor_t CamInd_gnHeaderType_descr_;
extern const XERdescriptor_t CamInd_gnHeaderSubtype_xer_;
extern const TTCN_Typedescriptor_t CamInd_gnHeaderSubtype_descr_;
extern const XERdescriptor_t CamInd_gnLifetime_xer_;
extern const TTCN_Typedescriptor_t CamInd_gnLifetime_descr_;
extern const XERdescriptor_t CamInd_gnTrafficClass_xer_;
extern const TTCN_Typedescriptor_t CamInd_gnTrafficClass_descr_;
extern const XERdescriptor_t CamInd_btpDestinationPort_xer_;
extern const TTCN_Typedescriptor_t CamInd_btpDestinationPort_descr_;
extern const XERdescriptor_t CamInd_btpInfo_xer_;
extern const TTCN_Typedescriptor_t CamInd_btpInfo_descr_;
extern const TTCN_RAWdescriptor_t CamInd_raw_;
extern const TTCN_JSONdescriptor_t CamInd_json_;
extern const TTCN_Typedescriptor_t CamInd_descr_;
extern CamInd ItsCam_component_vc__camMsg;
extern DEFAULT ItsCam_component_vc__default;
extern BOOLEAN ItsCam_component_vc__camReceived;
extern LibItsCam__TypesAndValues::UtCamEventIndList ItsCam_component_vc__utEvents;
extern const TTCN_RAWdescriptor_t CamReq_raw_;
extern const TTCN_JSONdescriptor_t CamReq_json_;
extern const TTCN_Typedescriptor_t CamReq_descr_;
extern TTCN_Module module_object;

} /* end of namespace */

/* Test port header files */

#include "UpperTesterCAMPort.hh"
#include "CamPort.hh"

#endif
