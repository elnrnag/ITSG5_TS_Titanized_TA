// This C++ source file was generated by the TTCN-3 compiler
// of the TTCN-3 Test Executor version CRL 113 200/5 R4A
// for Aitor (aitorkun@aitorkun-HP-EliteBook-2530p) on Tue Feb 16 10:46:43 2016

// Copyright (c) 2000-2015 Ericsson Telecom AB

// Do not edit this file unless you know what you are doing.

/* Including header files */

#include "LibItsCam_Functions.hh"

namespace LibItsCam__Functions {

/* Prototypes of static functions */

static void pre_init_module();
static void post_init_module();
static boolean start_ptc_function(const char *function_name, Text_Buf& function_arguments);

/* Literal string constants */

static const CHARSTRING cs_1(4, "*** "),
cs_5(38, ": INCONC: Initial CAM not received ***"),
cs_2(66, ": INFO: Could not receive expected UT message from IUT in time ***"),
cs_4(32, ": INFO: Received initial CAM ***"),
cs_3(58, ": INFO: Received unhandled/unknown UT message from IUT ***"),
cs_0(5, "error");
static const unsigned char module_checksum[] = { 0xcc, 0xa8, 0x28, 0x39, 0x35, 0xe4, 0xa8, 0x8f, 0x72, 0xee, 0xa4, 0x00, 0xeb, 0x3f, 0x71, 0x78 };

/* Class definitions for internal use */

class a__default_Default : public Default_Base {
public:
a__default_Default();
alt_status call_altstep();
};

class a__utDefault_Default : public Default_Base {
public:
a__utDefault_Default();
alt_status call_altstep();
};


/* Global variable definitions */

static const size_t num_namespaces = 0;
TTCN_Module module_object("LibItsCam_Functions", __DATE__, __TIME__, module_checksum, pre_init_module, NULL, 0U, 4294967295U, 4294967295U, 4294967295U, NULL, 0LU, 0, post_init_module, NULL, NULL, NULL, NULL, start_ptc_function, NULL);

static const RuntimeVersionChecker ver_checker(  current_runtime_version.requires_major_version_5,
  current_runtime_version.requires_minor_version_4,
  current_runtime_version.requires_patch_level_0,  current_runtime_version.requires_runtime_1);

/* Member functions of C++ classes */

a__default_Default::a__default_Default()
 : Default_Base("a_default")
{
}

alt_status a__default_Default::call_altstep()
{
return a__default_instance();
}

a__utDefault_Default::a__utDefault_Default()
 : Default_Base("a_utDefault")
{
}

alt_status a__utDefault_Default::call_altstep()
{
return a__utDefault_instance();
}


/* Bodies of functions, altsteps and testcases */

void f__utInitializeIut(const LibItsCommon__TypesAndValues::UtInitialize_template& p__init)
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 34, TTCN_Location::LOCATION_FUNCTION, "f_utInitializeIut");
current_location.update_lineno(35);
/* LibItsCam_Functions.ttcn, line 35 */
LibItsCommon__TypesAndValues::UtInitializeResult v__UtInitializeResult;
current_location.update_lineno(36);
/* LibItsCam_Functions.ttcn, line 36 */
v__UtInitializeResult.utInitializeResult() = TRUE;
current_location.update_lineno(37);
/* LibItsCam_Functions.ttcn, line 37 */
LibItsCam__TestSystem::ItsCam_component_utPort.send(p__init);
current_location.update_lineno(38);
/* LibItsCam_Functions.ttcn, line 38 */
LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.start();
current_location.update_lineno(39);
/* LibItsCam_Functions.ttcn, line 39 */
{
tmp_1:
alt_status tmp_1_alt_flag_0 = ALT_MAYBE;
alt_status tmp_1_alt_flag_1 = ALT_MAYBE;
alt_status tmp_1_alt_flag_2 = ALT_MAYBE;
TTCN_Snapshot::take_new(FALSE);
for ( ; ; ) {
if (tmp_1_alt_flag_0 == ALT_MAYBE) {
current_location.update_lineno(40);
/* LibItsCam_Functions.ttcn, line 40 */
tmp_1_alt_flag_0 = LibItsCam__TestSystem::ItsCam_component_utPort.receive(LibItsCommon__TypesAndValues::UtInitializeResult_template(v__UtInitializeResult), NULL, any_compref, NULL);
if (tmp_1_alt_flag_0 == ALT_YES) {
current_location.update_lineno(41);
/* LibItsCam_Functions.ttcn, line 41 */
LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.stop();
current_location.update_lineno(42);
/* LibItsCam_Functions.ttcn, line 42 */
TTCN_Logger::log_str(TTCN_USER, "*** f_utInitializeIut: INFO: IUT initialized ***");
break;
}
}
if (tmp_1_alt_flag_1 == ALT_MAYBE) {
current_location.update_lineno(44);
/* LibItsCam_Functions.ttcn, line 44 */
tmp_1_alt_flag_1 = LibItsCam__TestSystem::ItsCam_component_utPort.receive(any_compref, NULL);
if (tmp_1_alt_flag_1 == ALT_YES) {
current_location.update_lineno(45);
/* LibItsCam_Functions.ttcn, line 45 */
LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.stop();
current_location.update_lineno(46);
/* LibItsCam_Functions.ttcn, line 46 */
TTCN_Logger::log_str(TTCN_USER, "*** f_utInitializeIut: INFO: IUT could not be initialized ***");
current_location.update_lineno(47);
/* LibItsCam_Functions.ttcn, line 47 */
LibCommon__Sync::f__selfOrClientSyncAndVerdict(cs_0, LibCommon__VerdictControl::FncRetCode::e__error);
break;
}
}
if (tmp_1_alt_flag_2 == ALT_MAYBE) {
current_location.update_lineno(49);
/* LibItsCam_Functions.ttcn, line 49 */
tmp_1_alt_flag_2 = LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.timeout();
if (tmp_1_alt_flag_2 == ALT_YES) {
current_location.update_lineno(50);
/* LibItsCam_Functions.ttcn, line 50 */
TTCN_Logger::log_str(TTCN_USER, "*** f_utInitializeIut: INFO: IUT could not be initialized in time ***");
current_location.update_lineno(51);
/* LibItsCam_Functions.ttcn, line 51 */
LibCommon__Sync::f__selfOrClientSyncAndVerdict(cs_0, LibCommon__VerdictControl::FncRetCode::e__timeout);
break;
}
}
TTCN_Snapshot::else_branch_reached();
{
current_location.update_lineno(55);
/* LibItsCam_Functions.ttcn, line 55 */
goto tmp_1;
}
}
}
}

void start_f__utInitializeIut(const COMPONENT& component_reference, const LibItsCommon__TypesAndValues::UtInitialize_template& p__init)
{
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_utInitializeIut(");
p__init.log();
TTCN_Logger::log_event_str(") on component ");
component_reference.log();
TTCN_Logger::log_char('.');
TTCN_Logger::end_event();
Text_Buf text_buf;
TTCN_Runtime::prepare_start_component(component_reference, "LibItsCam_Functions", "f_utInitializeIut", text_buf);
p__init.encode_text(text_buf);
TTCN_Runtime::send_start_component(text_buf);
}

void f__utTriggerEvent(const LibItsCam__TypesAndValues::UtCamTrigger_template& p__event)
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 65, TTCN_Location::LOCATION_FUNCTION, "f_utTriggerEvent");
current_location.update_lineno(66);
/* LibItsCam_Functions.ttcn, line 66 */
LibItsCam__TypesAndValues::UtCamTriggerResult v__UtCamTriggerResult;
current_location.update_lineno(67);
/* LibItsCam_Functions.ttcn, line 67 */
v__UtCamTriggerResult.utCamTriggerResult() = TRUE;
current_location.update_lineno(69);
/* LibItsCam_Functions.ttcn, line 69 */
LibItsCam__TestSystem::ItsCam_component_utPort.send(p__event);
current_location.update_lineno(70);
/* LibItsCam_Functions.ttcn, line 70 */
LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.start();
current_location.update_lineno(71);
/* LibItsCam_Functions.ttcn, line 71 */
{
tmp_3:
alt_status tmp_3_alt_flag_0 = ALT_MAYBE;
alt_status tmp_3_alt_flag_1 = ALT_MAYBE;
alt_status tmp_3_alt_flag_2 = ALT_MAYBE;
TTCN_Snapshot::take_new(FALSE);
for ( ; ; ) {
if (tmp_3_alt_flag_0 == ALT_MAYBE) {
current_location.update_lineno(72);
/* LibItsCam_Functions.ttcn, line 72 */
tmp_3_alt_flag_0 = LibItsCam__TestSystem::ItsCam_component_utPort.receive(LibItsCam__TypesAndValues::UtCamTriggerResult_template(v__UtCamTriggerResult), NULL, any_compref, NULL);
if (tmp_3_alt_flag_0 == ALT_YES) {
current_location.update_lineno(73);
/* LibItsCam_Functions.ttcn, line 73 */
LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.stop();
break;
}
}
if (tmp_3_alt_flag_1 == ALT_MAYBE) {
current_location.update_lineno(75);
/* LibItsCam_Functions.ttcn, line 75 */
tmp_3_alt_flag_1 = LibItsCam__TestSystem::ItsCam_component_utPort.receive(any_compref, NULL);
if (tmp_3_alt_flag_1 == ALT_YES) {
current_location.update_lineno(76);
/* LibItsCam_Functions.ttcn, line 76 */
LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.stop();
break;
}
}
if (tmp_3_alt_flag_2 == ALT_MAYBE) {
current_location.update_lineno(78);
/* LibItsCam_Functions.ttcn, line 78 */
tmp_3_alt_flag_2 = LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.timeout();
if (tmp_3_alt_flag_2 == ALT_YES) break;
}
TTCN_Snapshot::else_branch_reached();
{
current_location.update_lineno(82);
/* LibItsCam_Functions.ttcn, line 82 */
goto tmp_3;
}
}
}
}

void start_f__utTriggerEvent(const COMPONENT& component_reference, const LibItsCam__TypesAndValues::UtCamTrigger_template& p__event)
{
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_utTriggerEvent(");
p__event.log();
TTCN_Logger::log_event_str(") on component ");
component_reference.log();
TTCN_Logger::log_char('.');
TTCN_Logger::end_event();
Text_Buf text_buf;
TTCN_Runtime::prepare_start_component(component_reference, "LibItsCam_Functions", "f_utTriggerEvent", text_buf);
p__event.encode_text(text_buf);
TTCN_Runtime::send_start_component(text_buf);
}

void f__utChangePosition(const LibItsCommon__TypesAndValues::UtChangePosition_template& p__position)
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 91, TTCN_Location::LOCATION_FUNCTION, "f_utChangePosition");
current_location.update_lineno(93);
/* LibItsCam_Functions.ttcn, line 93 */
LibItsCam__TestSystem::ItsCam_component_utPort.send(p__position);
current_location.update_lineno(94);
/* LibItsCam_Functions.ttcn, line 94 */
{
tmp_4:
alt_status tmp_4_alt_flag_0 = ALT_MAYBE;
alt_status tmp_4_alt_flag_1 = ALT_MAYBE;
alt_status tmp_4_alt_flag_2 = ALT_MAYBE;
TTCN_Snapshot::take_new(FALSE);
for ( ; ; ) {
if (tmp_4_alt_flag_0 == ALT_MAYBE) {
current_location.update_lineno(95);
/* LibItsCam_Functions.ttcn, line 95 */
tmp_4_alt_flag_0 = LibItsCam__TestSystem::ItsCam_component_utPort.receive(LibItsCommon__TypesAndValues::UtChangePositionResult_template(ANY_VALUE), NULL, any_compref, NULL);
if (tmp_4_alt_flag_0 == ALT_YES) {
current_location.update_lineno(96);
/* LibItsCam_Functions.ttcn, line 96 */
LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.stop();
break;
}
}
if (tmp_4_alt_flag_1 == ALT_MAYBE) {
current_location.update_lineno(98);
/* LibItsCam_Functions.ttcn, line 98 */
tmp_4_alt_flag_1 = LibItsCam__TestSystem::ItsCam_component_utPort.receive(any_compref, NULL);
if (tmp_4_alt_flag_1 == ALT_YES) {
current_location.update_lineno(99);
/* LibItsCam_Functions.ttcn, line 99 */
LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.stop();
break;
}
}
if (tmp_4_alt_flag_2 == ALT_MAYBE) {
current_location.update_lineno(101);
/* LibItsCam_Functions.ttcn, line 101 */
tmp_4_alt_flag_2 = LibItsCommon__TestSystem::ItsBaseComponent_component_tc__wait.timeout();
if (tmp_4_alt_flag_2 == ALT_YES) {
current_location.update_lineno(102);
/* LibItsCam_Functions.ttcn, line 102 */
try {
TTCN_Logger::begin_event(TTCN_USER);
((cs_1 + TTCN_Runtime::get_testcasename()) + cs_2).log();
TTCN_Logger::end_event();
} catch (...) {
TTCN_Logger::finish_event();
throw;
}
current_location.update_lineno(103);
/* LibItsCam_Functions.ttcn, line 103 */
LibCommon__Sync::f__selfOrClientSyncAndVerdict(cs_0, LibCommon__VerdictControl::FncRetCode::e__timeout);
break;
}
}
TTCN_Snapshot::else_branch_reached();
{
current_location.update_lineno(107);
/* LibItsCam_Functions.ttcn, line 107 */
goto tmp_4;
}
}
}
}

void start_f__utChangePosition(const COMPONENT& component_reference, const LibItsCommon__TypesAndValues::UtChangePosition_template& p__position)
{
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_utChangePosition(");
p__position.log();
TTCN_Logger::log_event_str(") on component ");
component_reference.log();
TTCN_Logger::log_char('.');
TTCN_Logger::end_event();
Text_Buf text_buf;
TTCN_Runtime::prepare_start_component(component_reference, "LibItsCam_Functions", "f_utChangePosition", text_buf);
p__position.encode_text(text_buf);
TTCN_Runtime::send_start_component(text_buf);
}

void f__cfUp()
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 119, TTCN_Location::LOCATION_FUNCTION, "f_cfUp");
current_location.update_lineno(121);
/* LibItsCam_Functions.ttcn, line 121 */
TTCN_Runtime::map_port(self, LibItsCam__TestSystem::ItsCam_component_utPort.get_name(), SYSTEM_COMPREF, "utPort");
current_location.update_lineno(122);
/* LibItsCam_Functions.ttcn, line 122 */
TTCN_Runtime::map_port(self, LibItsCam__TestSystem::ItsCam_component_camPort.get_name(), SYSTEM_COMPREF, "camPort");
current_location.update_lineno(123);
/* LibItsCam_Functions.ttcn, line 123 */
LibCommon__Sync::f__connect4SelfOrClientSync();
}

void start_f__cfUp(const COMPONENT& component_reference)
{
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_cfUp(");
TTCN_Logger::log_event_str(") on component ");
component_reference.log();
TTCN_Logger::log_char('.');
TTCN_Logger::end_event();
Text_Buf text_buf;
TTCN_Runtime::prepare_start_component(component_reference, "LibItsCam_Functions", "f_cfUp", text_buf);
TTCN_Runtime::send_start_component(text_buf);
}

void f__cfDown()
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 130, TTCN_Location::LOCATION_FUNCTION, "f_cfDown");
current_location.update_lineno(133);
/* LibItsCam_Functions.ttcn, line 133 */
TTCN_Runtime::unmap_port(self, LibItsCam__TestSystem::ItsCam_component_camPort.get_name(), SYSTEM_COMPREF, "camPort");
current_location.update_lineno(134);
/* LibItsCam_Functions.ttcn, line 134 */
TTCN_Runtime::unmap_port(self, LibItsCam__TestSystem::ItsCam_component_utPort.get_name(), SYSTEM_COMPREF, "utPort");
current_location.update_lineno(135);
/* LibItsCam_Functions.ttcn, line 135 */
LibCommon__Sync::f__disconnect4SelfOrClientSync();
}

void start_f__cfDown(const COMPONENT& component_reference)
{
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_cfDown(");
TTCN_Logger::log_event_str(") on component ");
component_reference.log();
TTCN_Logger::log_char('.');
TTCN_Logger::end_event();
Text_Buf text_buf;
TTCN_Runtime::prepare_start_component(component_reference, "LibItsCam_Functions", "f_cfDown", text_buf);
TTCN_Runtime::send_start_component(text_buf);
}

alt_status a__default_instance()
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 146, TTCN_Location::LOCATION_ALTSTEP, "a_default");
alt_status ret_val = ALT_NO;
current_location.update_lineno(148);
/* LibItsCam_Functions.ttcn, line 148 */
switch (LibItsCam__TestSystem::ItsCam_component_camPort.receive(LibItsCam__Templates::mw__camInd(LibItsCam__Templates::mw__camMsg__any), NULL, any_compref, NULL)) {
case ALT_YES:
{
current_location.update_lineno(149);
/* LibItsCam_Functions.ttcn, line 149 */
TTCN_Logger::log_str(TTCN_USER, "*** a_default: INFO: CAM message received in default ***");
current_location.update_lineno(150);
/* LibItsCam_Functions.ttcn, line 150 */
LibItsCam__TestSystem::ItsCam_component_vc__camReceived = TRUE;
current_location.update_lineno(151);
/* LibItsCam_Functions.ttcn, line 151 */
return ALT_REPEAT;
}
case ALT_MAYBE:
ret_val = ALT_MAYBE;
default:
break;
}
current_location.update_lineno(153);
/* LibItsCam_Functions.ttcn, line 153 */
switch (LibItsCam__TestSystem::ItsCam_component_camPort.receive(any_compref, NULL)) {
case ALT_YES:
{
current_location.update_lineno(154);
/* LibItsCam_Functions.ttcn, line 154 */
TTCN_Logger::log_str(TTCN_USER, "*** a_default: ERROR: event received on CAM port in default ***");
current_location.update_lineno(155);
/* LibItsCam_Functions.ttcn, line 155 */
LibCommon__Sync::f__selfOrClientSyncAndVerdict(cs_0, LibCommon__VerdictControl::FncRetCode::e__error);
}
return ALT_YES;
case ALT_MAYBE:
ret_val = ALT_MAYBE;
default:
break;
}
current_location.update_lineno(157);
/* LibItsCam_Functions.ttcn, line 157 */
switch (TIMER::any_timeout()) {
case ALT_YES:
{
current_location.update_lineno(158);
/* LibItsCam_Functions.ttcn, line 158 */
TTCN_Logger::log_str(TTCN_USER, "*** a_default: INCONC: a timer expired in default ***");
current_location.update_lineno(159);
/* LibItsCam_Functions.ttcn, line 159 */
LibCommon__Sync::f__selfOrClientSyncAndVerdict(cs_0, LibCommon__VerdictControl::FncRetCode::e__timeout);
}
return ALT_YES;
case ALT_MAYBE:
ret_val = ALT_MAYBE;
default:
break;
}
current_location.update_lineno(161);
/* LibItsCam_Functions.ttcn, line 161 */
switch (LibCommon__Sync::a__shutdown_instance()) {
case ALT_YES:
{
current_location.update_lineno(162);
/* LibItsCam_Functions.ttcn, line 162 */
f__poDefault();
current_location.update_lineno(163);
/* LibItsCam_Functions.ttcn, line 163 */
f__cfDown();
current_location.update_lineno(164);
/* LibItsCam_Functions.ttcn, line 164 */
TTCN_Logger::log_str(TTCN_USER, "*** a_default: INFO: TEST COMPONENT NOW STOPPING ITSELF! ***");
current_location.update_lineno(165);
/* LibItsCam_Functions.ttcn, line 165 */
TTCN_Runtime::stop_execution();
}
case ALT_REPEAT:
return ALT_REPEAT;
case ALT_BREAK:
return ALT_BREAK;
case ALT_MAYBE:
ret_val = ALT_MAYBE;
default:
break;
}
return ret_val;
}

void a__default()
{
altstep_begin:
boolean block_flag = FALSE;
alt_status altstep_flag = ALT_UNCHECKED, default_flag = ALT_UNCHECKED;
for ( ; ; ) {
TTCN_Snapshot::take_new(block_flag);
if (altstep_flag != ALT_NO) {
altstep_flag = a__default_instance();
if (altstep_flag == ALT_YES || altstep_flag == ALT_BREAK) return;
else if (altstep_flag == ALT_REPEAT) goto altstep_begin;
}
if (default_flag != ALT_NO) {
default_flag = TTCN_Default::try_altsteps();
if (default_flag == ALT_YES || default_flag == ALT_BREAK) return;
else if (default_flag == ALT_REPEAT) goto altstep_begin;
}
if (altstep_flag == ALT_NO && default_flag == ALT_NO) TTCN_error("None of the branches can be chosen in altstep a_default.");
else block_flag = TRUE;
}
}

Default_Base *activate_a__default()
{
return new a__default_Default();
}

alt_status a__utDefault_instance()
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 172, TTCN_Location::LOCATION_ALTSTEP, "a_utDefault");
current_location.update_lineno(173);
/* LibItsCam_Functions.ttcn, line 173 */
LibItsCam__TypesAndValues::UtCamEventInd v__event;
alt_status ret_val = ALT_NO;
current_location.update_lineno(174);
/* LibItsCam_Functions.ttcn, line 174 */
switch (LibItsCam__TestSystem::ItsCam_component_utPort.receive(LibItsCam__TypesAndValues::UtCamEventInd_template(ANY_VALUE), &(v__event), any_compref, NULL)) {
case ALT_YES:
{
current_location.update_lineno(176);
/* LibItsCam_Functions.ttcn, line 176 */
LibItsCam__TestSystem::ItsCam_component_vc__utEvents[LibItsCam__TestSystem::ItsCam_component_vc__utEvents.lengthof()] = v__event;
current_location.update_lineno(177);
/* LibItsCam_Functions.ttcn, line 177 */
return ALT_REPEAT;
}
case ALT_MAYBE:
ret_val = ALT_MAYBE;
default:
break;
}
current_location.update_lineno(179);
/* LibItsCam_Functions.ttcn, line 179 */
switch (LibItsCam__TestSystem::ItsCam_component_utPort.receive(any_compref, NULL)) {
case ALT_YES:
{
current_location.update_lineno(180);
/* LibItsCam_Functions.ttcn, line 180 */
try {
TTCN_Logger::begin_event(TTCN_USER);
((cs_1 + TTCN_Runtime::get_testcasename()) + cs_3).log();
TTCN_Logger::end_event();
} catch (...) {
TTCN_Logger::finish_event();
throw;
}
current_location.update_lineno(181);
/* LibItsCam_Functions.ttcn, line 181 */
return ALT_REPEAT;
}
case ALT_MAYBE:
ret_val = ALT_MAYBE;
default:
break;
}
return ret_val;
}

void a__utDefault()
{
altstep_begin:
boolean block_flag = FALSE;
alt_status altstep_flag = ALT_UNCHECKED, default_flag = ALT_UNCHECKED;
for ( ; ; ) {
TTCN_Snapshot::take_new(block_flag);
if (altstep_flag != ALT_NO) {
altstep_flag = a__utDefault_instance();
if (altstep_flag == ALT_YES || altstep_flag == ALT_BREAK) return;
else if (altstep_flag == ALT_REPEAT) goto altstep_begin;
}
if (default_flag != ALT_NO) {
default_flag = TTCN_Default::try_altsteps();
if (default_flag == ALT_YES || default_flag == ALT_BREAK) return;
else if (default_flag == ALT_REPEAT) goto altstep_begin;
}
if (altstep_flag == ALT_NO && default_flag == ALT_NO) TTCN_error("None of the branches can be chosen in altstep a_utDefault.");
else block_flag = TRUE;
}
}

Default_Base *activate_a__utDefault()
{
return new a__utDefault_Default();
}

void f__prDefault()
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 192, TTCN_Location::LOCATION_FUNCTION, "f_prDefault");
current_location.update_lineno(193);
/* LibItsCam_Functions.ttcn, line 193 */
LibItsCam__TestSystem::ItsCam_component_vc__default = activate_a__default();
current_location.update_lineno(194);
/* LibItsCam_Functions.ttcn, line 194 */
activate_a__utDefault();
}

void start_f__prDefault(const COMPONENT& component_reference)
{
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_prDefault(");
TTCN_Logger::log_event_str(") on component ");
component_reference.log();
TTCN_Logger::log_char('.');
TTCN_Logger::end_event();
Text_Buf text_buf;
TTCN_Runtime::prepare_start_component(component_reference, "LibItsCam_Functions", "f_prDefault", text_buf);
TTCN_Runtime::send_start_component(text_buf);
}

void f__prInitialState()
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 201, TTCN_Location::LOCATION_FUNCTION, "f_prInitialState");
current_location.update_lineno(203);
/* LibItsCam_Functions.ttcn, line 203 */
f__utInitializeIut(LibItsCam__Templates::m__camInitialize);
current_location.update_lineno(205);
/* LibItsCam_Functions.ttcn, line 205 */
f__prDefault();
current_location.update_lineno(208);
/* LibItsCam_Functions.ttcn, line 208 */
LibCommon__Time::f__sleep(1.0);
current_location.update_lineno(210);
/* LibItsCam_Functions.ttcn, line 210 */
LibItsCam__TestSystem::ItsCam_component_camPort.clear();
current_location.update_lineno(211);
/* LibItsCam_Functions.ttcn, line 211 */
LibItsCommon__TestSystem::ItsBaseComponent_component_tc__ac.start();
current_location.update_lineno(212);
/* LibItsCam_Functions.ttcn, line 212 */
{
tmp_8:
alt_status tmp_8_alt_flag_0 = ALT_MAYBE;
alt_status tmp_8_alt_flag_1 = ALT_MAYBE;
alt_status tmp_8_default_flag = ALT_MAYBE;
TTCN_Snapshot::take_new(FALSE);
for ( ; ; ) {
if (tmp_8_alt_flag_0 == ALT_MAYBE) {
current_location.update_lineno(213);
/* LibItsCam_Functions.ttcn, line 213 */
tmp_8_alt_flag_0 = LibItsCam__TestSystem::ItsCam_component_camPort.receive(LibItsCam__Templates::mw__camInd(LibItsCam__Templates::mw__camMsg__any), NULL, any_compref, NULL);
if (tmp_8_alt_flag_0 == ALT_YES) {
current_location.update_lineno(214);
/* LibItsCam_Functions.ttcn, line 214 */
LibItsCommon__TestSystem::ItsBaseComponent_component_tc__ac.stop();
current_location.update_lineno(215);
/* LibItsCam_Functions.ttcn, line 215 */
try {
TTCN_Logger::begin_event(TTCN_USER);
((cs_1 + TTCN_Runtime::get_testcasename()) + cs_4).log();
TTCN_Logger::end_event();
} catch (...) {
TTCN_Logger::finish_event();
throw;
}
break;
}
}
if (tmp_8_alt_flag_1 == ALT_MAYBE) {
current_location.update_lineno(217);
/* LibItsCam_Functions.ttcn, line 217 */
tmp_8_alt_flag_1 = LibItsCommon__TestSystem::ItsBaseComponent_component_tc__ac.timeout();
if (tmp_8_alt_flag_1 == ALT_YES) {
current_location.update_lineno(218);
/* LibItsCam_Functions.ttcn, line 218 */
try {
TTCN_Logger::begin_event(TTCN_USER);
((cs_1 + TTCN_Runtime::get_testcasename()) + cs_5).log();
TTCN_Logger::end_event();
} catch (...) {
TTCN_Logger::finish_event();
throw;
}
current_location.update_lineno(219);
/* LibItsCam_Functions.ttcn, line 219 */
LibCommon__Sync::f__selfOrClientSyncAndVerdictPreamble(cs_0, LibCommon__VerdictControl::FncRetCode::e__timeout);
break;
}
}
if (tmp_8_default_flag == ALT_MAYBE) {
tmp_8_default_flag = TTCN_Default::try_altsteps();
if (tmp_8_default_flag == ALT_YES || tmp_8_default_flag == ALT_BREAK) break;
else if (tmp_8_default_flag == ALT_REPEAT) goto tmp_8;
}
current_location.update_lineno(212);
/* LibItsCam_Functions.ttcn, line 212 */
if (tmp_8_alt_flag_0 == ALT_NO && tmp_8_alt_flag_1 == ALT_NO && tmp_8_default_flag == ALT_NO) TTCN_error("None of the branches can be chosen in the alt statement in file LibItsCam_Functions.ttcn between lines 212 and 221.");
TTCN_Snapshot::take_new(TRUE);
}
}
}

void start_f__prInitialState(const COMPONENT& component_reference)
{
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_prInitialState(");
TTCN_Logger::log_event_str(") on component ");
component_reference.log();
TTCN_Logger::log_char('.');
TTCN_Logger::end_event();
Text_Buf text_buf;
TTCN_Runtime::prepare_start_component(component_reference, "LibItsCam_Functions", "f_prInitialState", text_buf);
TTCN_Runtime::send_start_component(text_buf);
}

void f__poDefault()
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 232, TTCN_Location::LOCATION_FUNCTION, "f_poDefault");
}

void start_f__poDefault(const COMPONENT& component_reference)
{
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_poDefault(");
TTCN_Logger::log_event_str(") on component ");
component_reference.log();
TTCN_Logger::log_char('.');
TTCN_Logger::end_event();
Text_Buf text_buf;
TTCN_Runtime::prepare_start_component(component_reference, "LibItsCam_Functions", "f_poDefault", text_buf);
TTCN_Runtime::send_start_component(text_buf);
}

ITS__Container::ReferencePosition f__computePositionUsingDistance(const ITS__Container::ReferencePosition& p__referencePosition, const INTEGER& p__offSet)
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 249, TTCN_Location::LOCATION_FUNCTION, "f_computePositionUsingDistance");
current_location.update_lineno(250);
/* LibItsCam_Functions.ttcn, line 250 */
ITS__Container::ReferencePosition v__referencePosition(p__referencePosition);
current_location.update_lineno(252);
/* LibItsCam_Functions.ttcn, line 252 */
TTCN_Logger::log_str(TTCN_USER, "*** f_computePositionUsingDistance: INFO: calling fx_computePositionUsingDistance() ***");
current_location.update_lineno(253);
/* LibItsCam_Functions.ttcn, line 253 */
LibItsCommon__Functions::fx__computePositionUsingDistance(const_cast< const ITS__Container::ReferencePosition&>(p__referencePosition).latitude(), const_cast< const ITS__Container::ReferencePosition&>(p__referencePosition).longitude(), p__offSet, const_cast< const ITS__Container::ReferencePosition&>(p__referencePosition).positionConfidenceEllipse().semiMajorOrientation(), v__referencePosition.latitude(), v__referencePosition.longitude());
current_location.update_lineno(261);
/* LibItsCam_Functions.ttcn, line 261 */
return v__referencePosition;
}

OCTETSTRING f__generateDefaultCam()
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 267, TTCN_Location::LOCATION_FUNCTION, "f_generateDefaultCam");
current_location.update_lineno(269);
/* LibItsCam_Functions.ttcn, line 269 */
{
OCTETSTRING tmp_9;
TTCN_Buffer tmp_10;
LibItsCam__TestSystem::CamReq const& tmp_11 = LibItsCam__Templates::m__camReq(LibItsCam__Templates::m__camMsg__vehicle(LibItsCommon__Functions::f__getTsStationId(), mod(LibItsCommon__Functions::f__getCurrentTime(), 65536), LibItsCam__Templates::m__tsPosition)).valueof();
tmp_11.encode(LibItsCam__TestSystem::CamReq_descr_, tmp_10, TTCN_EncDec::CT_RAW);
tmp_10.get_string(tmp_9);
return bit2oct(oct2bit(tmp_9));
}
}


/* Bodies of static functions */

static void pre_init_module()
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 0, TTCN_Location::LOCATION_UNKNOWN, "LibItsCam_Functions");
LibItsCam__Templates::module_object.pre_init_module();
module_object.add_function("f_utInitializeIut", (genericfunc_t)&f__utInitializeIut, (genericfunc_t)&start_f__utInitializeIut);
module_object.add_function("f_utTriggerEvent", (genericfunc_t)&f__utTriggerEvent, (genericfunc_t)&start_f__utTriggerEvent);
module_object.add_function("f_utChangePosition", (genericfunc_t)&f__utChangePosition, (genericfunc_t)&start_f__utChangePosition);
module_object.add_function("f_cfUp", (genericfunc_t)&f__cfUp, (genericfunc_t)&start_f__cfUp);
module_object.add_function("f_cfDown", (genericfunc_t)&f__cfDown, (genericfunc_t)&start_f__cfDown);
module_object.add_altstep("a_default", (genericfunc_t)&a__default_instance, (genericfunc_t )&activate_a__default, (genericfunc_t )&a__default);
module_object.add_altstep("a_utDefault", (genericfunc_t)&a__utDefault_instance, (genericfunc_t )&activate_a__utDefault, (genericfunc_t )&a__utDefault);
module_object.add_function("f_prDefault", (genericfunc_t)&f__prDefault, (genericfunc_t)&start_f__prDefault);
module_object.add_function("f_prInitialState", (genericfunc_t)&f__prInitialState, (genericfunc_t)&start_f__prInitialState);
module_object.add_function("f_poDefault", (genericfunc_t)&f__poDefault, (genericfunc_t)&start_f__poDefault);
module_object.add_function("f_computePositionUsingDistance", (genericfunc_t)&f__computePositionUsingDistance, NULL);
module_object.add_function("f_generateDefaultCam", (genericfunc_t)&f__generateDefaultCam, NULL);
}

static void post_init_module()
{
TTCN_Location current_location("LibItsCam_Functions.ttcn", 0, TTCN_Location::LOCATION_UNKNOWN, "LibItsCam_Functions");
LibItsCam__Templates::module_object.post_init_module();
}

static boolean start_ptc_function(const char *function_name, Text_Buf& function_arguments)
{
if (!strcmp(function_name, "f_utInitializeIut")) {
LibItsCommon__TypesAndValues::UtInitialize_template p__init;
p__init.decode_text(function_arguments);
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_utInitializeIut(");
p__init.log();
TTCN_Logger::log_event_str(").");
TTCN_Logger::end_event();
TTCN_Runtime::function_started(function_arguments);
f__utInitializeIut(p__init);
TTCN_Runtime::function_finished("f_utInitializeIut");
return TRUE;
} else if (!strcmp(function_name, "f_utTriggerEvent")) {
LibItsCam__TypesAndValues::UtCamTrigger_template p__event;
p__event.decode_text(function_arguments);
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_utTriggerEvent(");
p__event.log();
TTCN_Logger::log_event_str(").");
TTCN_Logger::end_event();
TTCN_Runtime::function_started(function_arguments);
f__utTriggerEvent(p__event);
TTCN_Runtime::function_finished("f_utTriggerEvent");
return TRUE;
} else if (!strcmp(function_name, "f_utChangePosition")) {
LibItsCommon__TypesAndValues::UtChangePosition_template p__position;
p__position.decode_text(function_arguments);
TTCN_Logger::begin_event(TTCN_Logger::PARALLEL_PTC);
TTCN_Logger::log_event_str("Starting function f_utChangePosition(");
p__position.log();
TTCN_Logger::log_event_str(").");
TTCN_Logger::end_event();
TTCN_Runtime::function_started(function_arguments);
f__utChangePosition(p__position);
TTCN_Runtime::function_finished("f_utChangePosition");
return TRUE;
} else if (!strcmp(function_name, "f_cfUp")) {
TTCN_Logger::log_str(TTCN_Logger::PARALLEL_PTC, "Starting function f_cfUp().");
TTCN_Runtime::function_started(function_arguments);
f__cfUp();
TTCN_Runtime::function_finished("f_cfUp");
return TRUE;
} else if (!strcmp(function_name, "f_cfDown")) {
TTCN_Logger::log_str(TTCN_Logger::PARALLEL_PTC, "Starting function f_cfDown().");
TTCN_Runtime::function_started(function_arguments);
f__cfDown();
TTCN_Runtime::function_finished("f_cfDown");
return TRUE;
} else if (!strcmp(function_name, "f_prDefault")) {
TTCN_Logger::log_str(TTCN_Logger::PARALLEL_PTC, "Starting function f_prDefault().");
TTCN_Runtime::function_started(function_arguments);
f__prDefault();
TTCN_Runtime::function_finished("f_prDefault");
return TRUE;
} else if (!strcmp(function_name, "f_prInitialState")) {
TTCN_Logger::log_str(TTCN_Logger::PARALLEL_PTC, "Starting function f_prInitialState().");
TTCN_Runtime::function_started(function_arguments);
f__prInitialState();
TTCN_Runtime::function_finished("f_prInitialState");
return TRUE;
} else if (!strcmp(function_name, "f_poDefault")) {
TTCN_Logger::log_str(TTCN_Logger::PARALLEL_PTC, "Starting function f_poDefault().");
TTCN_Runtime::function_started(function_arguments);
f__poDefault();
TTCN_Runtime::function_finished("f_poDefault");
return TRUE;
} else return FALSE;
}


} /* end of namespace */
