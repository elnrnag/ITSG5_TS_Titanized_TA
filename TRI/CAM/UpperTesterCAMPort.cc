// This Test Port skeleton source file was generated by the
// TTCN-3 Compiler of the TTCN-3 Test Executor version CRL 113 200/5 R3A
// for Lenard Nagy (elnrnag@elx1pjld12-hz) on Tue Oct 20 14:37:43 2015

// Copyright Ericsson Telecom AB 2000-2015

// You may modify this file. Complete the body of empty functions and
// add your member functions here.

#include "UpperTesterCAMPort.hh"
#include <unistd.h>

namespace LibItsCam__TestSystem {

  UpperTesterCAMPort::UpperTesterCAMPort(const char *par_port_name)
    : UpperTesterCAMPort_BASE(par_port_name)
  {

  }

  UpperTesterCAMPort::~UpperTesterCAMPort()
  {

  }

  void UpperTesterCAMPort::set_parameter(const char * parameter_name,
					 const char * parameter_value)
  {
    TRI_set_param(parameter_name,parameter_value,mp_data);
  }

  /*void UpperTesterCAMPort::Handle_Fd_Event(int fd, boolean is_readable,
    boolean is_writable, boolean is_error) {}*/

  void UpperTesterCAMPort::Handle_Fd_Event_Error(int fd)
  {
    Handle_Fd_Event_Readable(fd);
  }

  void UpperTesterCAMPort::Handle_Fd_Event_Writable(int fd)
  {

  }

  void UpperTesterCAMPort::Handle_Fd_Event_Readable(int fd)
  {
    int res=TRI__interface__Types::TRI_read_socket(fd,mp_data.buff);
 
    if(res<=0){
      TTCN_error("TRI_Mapper connection lost");
    }
 
    TRI__interface__Types::TRI__mapper__PDU pdu;
 
    while(TRI__interface__Types::TRI_get_message(mp_data.buff,pdu)){
      switch(pdu.msg().get_selection()){
      case TRI__interface__Types::Msg__union::ALT_enqueue__msg:{
	/**** ****/
	// put the decoder here
	//         incoming_message(data);
	const OCTETSTRING tmp=(const OCTETSTRING)pdu.msg().enqueue__msg().data().encoded__data();
	const unsigned char* tmp1 = (const unsigned char*)tmp;
	if (tmp1[0] == 0x01) { //utInitializeResult
	  if (tmp.lengthof()==2) { //Fixed length 2 octets
	    //Subtract header
	    tmp1++;
	    LibItsCommon__TypesAndValues::UtInitializeResult ret_val;
	    ret_val.utInitializeResult() = *tmp1;
	    incoming_message(ret_val);
	  } else {
	    TTCN_error("Invalid message length of UtInitializeResult");
	  }
	} else if (tmp1[0] == 0x21) { //utCamTriggerResult
	  if (tmp.lengthof()==2) { //Fixed length 2 octets
	    //Subtract header
	    tmp1++;
	    LibItsCam__TypesAndValues::UtCamTriggerResult ret_val;
	    ret_val.utCamTriggerResult() = *tmp1;
	    incoming_message(ret_val);
	  } else {
	    TTCN_error("Invalid message length of UtCamTriggerResult");
	  }
	} else if (tmp1[0] == 0x23) {
	  LibItsCam__TypesAndValues::UtCamEventInd ret_val;

	  ret_val = TTCN__EncDec::f__dec__Ut__Cam__EventInd(OCTETSTRING(tmp.lengthof()-1,tmp1+1));

	  incoming_message(ret_val);
	} else if (tmp1[0] == 0x03) { //UtChangePositionResult
	    if (tmp.lengthof()==2) { //Fixed length 2 octets
	    //Subtract header
	    tmp1++;
	    LibItsCommon__TypesAndValues::UtChangePositionResult ret_val;
	    ret_val.utChangePositionResult() = *tmp1;
	    incoming_message(ret_val);
	  } else {
	    TTCN_error("Invalid message length of UtChangePositionResult");
	  }
	} else if (tmp1[0] == 0x55) {
	  TTCN_Logger::begin_event(TTCN_DEBUG);
	  TTCN_Logger::log_event_str("*** UtGnEventIndication discarded ***");
	  TTCN_Logger::end_event();
	}
	else if (tmp1[0] == 0x63) {
	  TTCN_Logger::begin_event(TTCN_DEBUG);
	  TTCN_Logger::log_event_str("*** UtBtpEventIndication discarded ***");
	  TTCN_Logger::end_event();
	}
	else if (tmp1[0] == 0x13) {
	  TTCN_Logger::begin_event(TTCN_DEBUG);
	  TTCN_Logger::log_event_str("*** UtDenmEventIndication discarded ***");
	  TTCN_Logger::end_event();
	}      
	else if (tmp1[0] == 0x83) {
	  TTCN_Logger::begin_event(TTCN_DEBUG);
	  TTCN_Logger::log_event_str("*** UtGn6EventIndication discarded ***");
	  TTCN_Logger::end_event();
	}  
	else if (tmp1[0] == 0xA2) {
	  TTCN_Logger::begin_event(TTCN_DEBUG);
	  TTCN_Logger::log_event_str("*** UtMapEventIndication discarded ***");
	  TTCN_Logger::end_event();
	}
	else if (tmp1[0] == 0xA3) {
	  TTCN_Logger::begin_event(TTCN_DEBUG);
	  TTCN_Logger::log_event_str("*** UtSpatEventIndication discarded ***");
	  TTCN_Logger::end_event();
	} else {
	  TTCN_error("Unexpected message");
	}
      }
          
	/**** ****/
	break;
      case TRI__interface__Types::Msg__union::ALT_result:
	if(pdu.msg().result().result()==TRI__interface__Types::Result__value::TRI__error){
	  TTCN_error("Unsucessfull send");
	}
	break;
      default:
	TTCN_error("Unexpected message");
	break;
      }
    }
  }

  /*void UpperTesterCAMPort::Handle_Timeout(double time_since_last_call) {}*/

  void UpperTesterCAMPort::user_map(const char * system_port)
  {
    TRI_map("<port type name>",system_port,mp_data);
    Handler_Add_Fd_Read(mp_data.tri_socket);
  }

  void UpperTesterCAMPort::user_unmap(const char * system_port)
  {
    TRI_unmap(mp_data);
    Uninstall_Handler();
  }

  void UpperTesterCAMPort::user_start()
  {
    TRI_start("<port type name>",get_name(),(component)self,COMPONENT::get_component_name(self),mp_data);
  }

  void UpperTesterCAMPort::user_stop()
  {
    TRI_stop(mp_data);
  }

  void UpperTesterCAMPort::outgoing_send(const LibItsCommon__TypesAndValues::UtInitialize& send_par)
  {
    TTCN_Buffer sb;

    /**** ****/
    // put the encoder here
    unsigned int header = 0x00;
    sb.put_c(header);
    sb.put_os(send_par.hashedId8());

    /**** ****/
    TRI__interface__Types::TRI__mapper__PDU pdu;

    pdu.msg().sendmsg().addr()=OMIT_VALUE;
    sb.get_string(pdu.msg().sendmsg().data().encoded__data());

    pdu.msg__id()=mp_data.msg_seq_num;
    mp_data.msg_seq_num++;
 
    TRI__interface__Types::TRI_send(mp_data.tri_socket,pdu);
  }

  void UpperTesterCAMPort::outgoing_send(const LibItsCam__TypesAndValues::UtCamTrigger& send_par)
  {
    TRI__interface__Types::TRI__mapper__PDU pdu;
    
    /**** ****/
    // put the encoder here
    TTCN_Buffer sb;
    unsigned int header = 0xFF;
    

    switch(send_par.get_selection()) {
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_changeCurvature: {
      header = 0x30;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_changeSpeed: {
      header = 0x31;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_setAccelerationControlStatus: {
      header = 0x32;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_setExteriorLightsStatus: {
      header = 0x33;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_changeHeading: {
      header = 0x34;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_setDriveDirection: {
      header = 0x35;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_changeYawRate: {
      header = 0x36;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_setStationType: {
      header = 0x39;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_setVehicleRole: {
      header = 0x3a;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_setEmbarkationStatus: {
      header = 0x3b;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_setPtActivation: {
      header = 0x3c;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_setDangerousGoods: {
      header = 0x3d;
    } break;
    case LibItsCam__TypesAndValues::UtCamTrigger::ALT_setLightBarSirene: {
      header = 0x3f;
    } break;
    default:
      TTCN_error("Unable to encode UtCAmTrigger message");
      break;
    }

    sb.put_c(header);
    sb.put_os(TTCN__EncDec::f__enc__UtCAmTrigger(send_par));

    /**** ****/
    sb.get_string(pdu.msg().sendmsg().data().encoded__data());
    pdu.msg().sendmsg().addr()=OMIT_VALUE;
 
    pdu.msg__id()=mp_data.msg_seq_num;
    mp_data.msg_seq_num++;
 
    TRI__interface__Types::TRI_send(mp_data.tri_socket,pdu);
  }

  void UpperTesterCAMPort::outgoing_send(const LibItsCommon__TypesAndValues::UtChangePosition& send_par)
  {
    TRI__interface__Types::TRI__mapper__PDU pdu;
 
    /**** ****/
    // put the encoder here
    TTCN_Buffer sb;
    unsigned int header = 0x02;
    unsigned int delta_latitude  = send_par.latitude();
    unsigned int delta_longitude = send_par.longitude();
    unsigned int delta_elevation = send_par.elevation(); 

    sb.put_c(header);

    sb.put_c( (delta_latitude>>24) & 0xFF );
    sb.put_c( (delta_latitude>>16) & 0xFF );
    sb.put_c( (delta_latitude>>8) & 0xFF );
    sb.put_c( (delta_latitude) & 0xFF );
    
    sb.put_c( (delta_longitude>>24) & 0xFF );
    sb.put_c( (delta_longitude>>16) & 0xFF );
    sb.put_c( (delta_longitude>>8) & 0xFF );
    sb.put_c( (delta_longitude) & 0xFF );
    
    sb.put_c( (delta_elevation) & 0xFF );
    
    //put into the TRI message
    sb.get_string(pdu.msg().sendmsg().data().encoded__data());
    /**** ****/
 
    pdu.msg().sendmsg().addr()=OMIT_VALUE;
 
    pdu.msg__id()=mp_data.msg_seq_num;
    mp_data.msg_seq_num++;
 
    TRI__interface__Types::TRI_send(mp_data.tri_socket,pdu);
  }

} /* end of namespace */
