#include "TRI_mapper_API.hh"

#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <errno.h>
#include <unistd.h>
#include <execinfo.h>
#include <stdio.h>
#include <stdlib.h>

namespace TRI__interface__Types {

TRI_mapper_data::TRI_mapper_data(){
  TRI_mapper_addr=NULL;
  TRI_mapper_port=NULL;
  tri_socket=-1;
  msg_seq_num=0;
}

TRI_mapper_data::~TRI_mapper_data(){
  Free(TRI_mapper_addr);
  Free(TRI_mapper_port);
}

bool TRI_set_param(const char *parameter_name, const char *parameter_value, TRI_mapper_data &mp_data){
  if(!strcasecmp(parameter_name,"TRI_mapper_address")){
    Free(mp_data.TRI_mapper_addr);
    mp_data.TRI_mapper_addr=mcopystr(parameter_value);
  } else if(!strcasecmp(parameter_name,"TRI_mapper_port")){
    Free(mp_data.TRI_mapper_port);
    mp_data.TRI_mapper_port=mcopystr(parameter_value);
  } else {
    return false;
  }
  return true;

}
void TRI_start(const char* pt_name,const char* name,int comp_id,const char* comp_name, TRI_mapper_data &mp_data){
  mp_data.tri_socket=TRI_open_connection(mp_data.TRI_mapper_addr,mp_data.TRI_mapper_port);
  TRI__mapper__PDU pdu;
  Register& reg= pdu.msg().reg();
  reg.portid().portName()=name;
  reg.portid().portTypeName()=pt_name;


  reg.portid().component__().componentId()=int2oct(comp_id,4);
  if(comp_id == MTC_COMPREF){  // MTC can't have name, so name it MTC 
    reg.portid().component__().componentName()="MTC";
  } else {
    reg.portid().component__().componentName()=comp_name;
  }
  reg.portid().component__().componentTypeName()="N/A";

  reg.portid().portindex()=OMIT_VALUE;
  reg.type__list()=OMIT_VALUE;
  
  pdu.msg__id()=mp_data.msg_seq_num;
  mp_data.msg_seq_num++;
  
  TRI_send(mp_data.tri_socket,pdu);
  
  Result  res;
  TRI_wait_for_res(mp_data.tri_socket,pdu.msg__id(),mp_data.buff,res);

  if(res.result() == Result__value::TRI__error){
    TTCN_error("%s: registration failed: %s",pt_name,res.result__string().ispresent()?(const char*)res.result__string()():"unknonw reason" );
  }

}

void TRI_unmap( TRI_mapper_data &mp_data){
  TRI__mapper__PDU pdu;
  pdu.msg().unmapped()=UnMap(NULL_VALUE);
  pdu.msg__id()=mp_data.msg_seq_num;
  mp_data.msg_seq_num++;
  
  TRI_send(mp_data.tri_socket,pdu);
  
  Result  res;
  TRI_wait_for_res(mp_data.tri_socket,pdu.msg__id(),mp_data.buff,res);
  
}

void TRI_map(const char* pt_name, const char* system_port, TRI_mapper_data &mp_data){
  TRI__mapper__PDU pdu;

  Map& mp= pdu.msg().mapped();
  mp.portid().portName()=system_port;
  mp.portid().portTypeName()=pt_name;


  mp.portid().component__().componentId()=int2oct(1,4);
  mp.portid().component__().componentName()="System";
  mp.portid().component__().componentTypeName()="N/A";

  mp.portid().portindex()=OMIT_VALUE;

  pdu.msg__id()=mp_data.msg_seq_num;
  mp_data.msg_seq_num++;
  
  TRI_send(mp_data.tri_socket,pdu);
  
  Result  res;
  TRI_wait_for_res(mp_data.tri_socket,pdu.msg__id(),mp_data.buff,res);

  if(res.result() == Result__value::TRI__error){
    TTCN_error("%s: mapping failed: %s",pt_name,res.result__string().ispresent()?(const char*)res.result__string()():"unknonw reason" );
  }

  mp_data.buff.clear();
}

int TRI_open_connection(const char* address, const char* port){

  struct addrinfo		*res;
  struct addrinfo		hints;
  int			error;

  bzero(&hints, sizeof (hints));
  hints.ai_flags = /*AI_ALL|*/AI_ADDRCONFIG|AI_PASSIVE;
  hints.ai_socktype = SOCK_STREAM;

  error = getaddrinfo(address, port, &hints, &res);

  if(error != 0){
    TTCN_error("TRI_mapper_connection: can not resolve address: %s %s reason: %s", address,port,gai_strerror(error));
  }

  int fd=socket(res->ai_family, res->ai_socktype, res->ai_protocol);
  
  if(connect(fd, res->ai_addr, res->ai_addrlen) == -1){
    freeaddrinfo(res);
    TTCN_error("TRI_mapper_connection: can not connect %d %s",errno,strerror(errno));
  }
  freeaddrinfo(res);
  
  return fd;
}

void TRI_send(int fd, const TRI__mapper__PDU& pdu){
  OCTETSTRING stream=TRI__encode(pdu);
  
  int length=stream.lengthof();
  
  unsigned char head[4];
  
  head[0] =3; // see RFC2126
  head[1] =0;
  head[3] = length & 0xff;
  length >>=8;
  head[2] = length & 0xff;
  
  if(send(fd, &head, 4, 0)!=4){
    TTCN_error("TRI_send: can not send %d %s",errno,strerror(errno));
  }


  if(send(fd, (const unsigned char*)stream,stream.lengthof() , 0)!=stream.lengthof()){
    TTCN_error("TRI_send: can not send %d %s",errno,strerror(errno));
  }
  
}

int TRI_read_socket(int fd,TTCN_Buffer& buff ){
  unsigned char rb[1024];
  
  int res=recv(fd,rb,1024,0);
  
  if(res>0){
    buff.put_s(res,rb);
  }
  
  return res;
}

void TRI_stop(TRI_mapper_data &mp_data){
  TRI__mapper__PDU pdu;
  pdu.msg().unregister()=Unregister(NULL_VALUE);
  pdu.msg__id()=mp_data.msg_seq_num;
  mp_data.msg_seq_num++;
  
  TRI_send(mp_data.tri_socket,pdu);
  
  Result  res;
  TRI_wait_for_res(mp_data.tri_socket,pdu.msg__id(),mp_data.buff,res);

  close(mp_data.tri_socket);
  mp_data.tri_socket=-1;
  
}

bool TRI_get_message(TTCN_Buffer& buff,TRI__mapper__PDU& pdu){
   const unsigned char* p=buff.get_data();
   size_t l=buff.get_len();
   
   if(l>4){
     size_t data_length = p[2];
     data_length<<=8;
     data_length+=p[3];
     if((data_length+4)<=l){
       OCTETSTRING data=OCTETSTRING(data_length,p+4);
       buff.set_pos(4+data_length);
       buff.cut();
       if(TRI__decode(data,pdu)!=0){
         TTCN_error("Message decoding error");
       }
       return true;
     }
   }
   return false;
}

void TRI_wait_for_res(int fd,int msg_id,TTCN_Buffer& buff,Result& res){
  TRI__mapper__PDU pdu;
  
  do{
    while(!TRI_get_message(buff,pdu)){
      if(TRI_read_socket(fd,buff)<=0){
        TTCN_error("TRI_Mapper connection lost %d %s",errno,strerror(errno));
      }
    }
  } while(pdu.msg__id() != msg_id);

  res=pdu.msg().result();
}

}
