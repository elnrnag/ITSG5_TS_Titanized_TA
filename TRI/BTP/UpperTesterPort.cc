// This Test Port skeleton source file was generated by the
// TTCN-3 Compiler of the TTCN-3 Test Executor version CRL 113 200/5 R3A
// for Gabor Szalai (ethgasz@esekilxxen1842) on Fri Sep 11 11:14:39 2015

// Copyright Ericsson Telecom AB 2000-2014

// You may modify this file. Complete the body of empty functions and
// add your member functions here.

#include "UpperTesterPort.hh"

namespace LibItsBtp__TestSystem {

  UpperTesterPort::UpperTesterPort(const char *par_port_name)
    : UpperTesterPort_BASE(par_port_name)
  {

  }

  UpperTesterPort::~UpperTesterPort()
  {

  }

  void UpperTesterPort::set_parameter(const char * parameter_name,
				      const char * parameter_value)
  {
    TRI_set_param(parameter_name,parameter_value,mp_data);

  }

  /*void UpperTesterPort::Handle_Fd_Event(int fd, boolean is_readable,
    boolean is_writable, boolean is_error) {}*/

  void UpperTesterPort::Handle_Fd_Event_Error(int fd)
  {
    Handle_Fd_Event_Readable(fd);

  }

  void UpperTesterPort::Handle_Fd_Event_Writable(int /*fd*/)
  {

  }

  void UpperTesterPort::Handle_Fd_Event_Readable(int fd)
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
	    TTCN_error("Invalid message length of UtBtpInitializeResult");
	  }
	} else if (tmp1[0] == 0x61) { //utBtpTriggerResult
	  if (tmp.lengthof()==2) { //Fixed length 2 octets
	    //Subtract header
	    tmp1++;
	    LibItsBtp__TypesAndValues::UtBtpTriggerResult ret_val;
	    ret_val.utBtpTriggerResult() = *tmp1;
	    incoming_message(ret_val);
	  } else {
	    TTCN_error("Invalid message length of UtBtpTriggerResult");
	  }
	} else if (tmp1[0] == 0x63) {
	  if (tmp1[1]*256+tmp1[2] == tmp.lengthof()-3) {
	    LibItsBtp__TypesAndValues::UtBtpEventInd ret_val;
	    ret_val.rawPayload() = OCTETSTRING(tmp.lengthof()-3,tmp1+3);
	    incoming_message(ret_val);
	  } else {
	    TTCN_error("Invalid message length of UtBtpEventInd");
	  }
	} else if (tmp1[0] == 0x55) {
	  TTCN_Logger::begin_event(TTCN_DEBUG);
	  TTCN_Logger::log_event_str("*** UtGnEventIndication discarded ***");
	  TTCN_Logger::end_event();
	}
	else if (tmp1[0] == 0x23) {
	  TTCN_Logger::begin_event(TTCN_DEBUG);
	  TTCN_Logger::log_event_str("*** UtCamEventIndication discarded ***");
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

  /*void UpperTesterPort::Handle_Timeout(double time_since_last_call) {}*/

  void UpperTesterPort::user_map(const char * system_port)
  {
    TRI_map("UpperTesterPort",system_port,mp_data);
    Handler_Add_Fd_Read(mp_data.tri_socket);

  }

  void UpperTesterPort::user_unmap(const char * /*system_port*/)
  {
    TRI_unmap(mp_data);
    Uninstall_Handler();

  }

  void UpperTesterPort::user_start()
  {
    TRI_start("UpperTesterPort",get_name(),(component)self,COMPONENT::get_component_name(self),mp_data);

  }

  void UpperTesterPort::user_stop()
  {
    TRI_stop(mp_data);

  }

  void UpperTesterPort::outgoing_send(const LibItsCommon__TypesAndValues::UtInitialize& send_par)
  {
    TTCN_Buffer  sb;

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

  void UpperTesterPort::outgoing_send(const LibItsBtp__TypesAndValues::UtBtpTrigger& send_par)
  {
    TTCN_Buffer sb;

    /**** ****/
    // put the encoder here
    unsigned int dp;
    unsigned int sp_pi;
   
    if(send_par.get_selection()==LibItsBtp__TypesAndValues::UtBtpTrigger::ALT_btpA){
      unsigned int header = 0x70;
      sb.put_c(header);
      dp=send_par.btpA().btpAHeader().destinationPort();
      sp_pi=send_par.btpA().btpAHeader().sourcePort();
    } else {
      unsigned int header = 0x71;
      sb.put_c(header);
      dp=send_par.btpB().btpBHeader().destinationPort();
      sp_pi=send_par.btpB().btpBHeader().destinationPortInfo();
    }

    sb.put_c( (dp>>8) & 0xFF );
    sb.put_c( (dp) & 0xFF );
    sb.put_c( (sp_pi>>8) & 0xFF );
    sb.put_c( (sp_pi) & 0xFF );
    /**** ****/

    TRI__interface__Types::TRI__mapper__PDU pdu;
    pdu.msg().sendmsg().addr()=OMIT_VALUE;
    sb.get_string(pdu.msg().sendmsg().data().encoded__data());

    pdu.msg__id()=mp_data.msg_seq_num;
    mp_data.msg_seq_num++;
  
    TRI__interface__Types::TRI_send(mp_data.tri_socket,pdu);

  }

} /* end of namespace */
