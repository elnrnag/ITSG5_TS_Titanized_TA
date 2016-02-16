#ifndef TRImapperAPI_HH
#define TRImapperAPI_HH

#include "TRI_interface_Types.hh"
namespace TRI__interface__Types {

class TRI_mapper_data{
public:
  char* TRI_mapper_addr;
  char* TRI_mapper_port;
  
  int   tri_socket;
  
  int   msg_seq_num;
  
  TTCN_Buffer buff;
  TRI_mapper_data();
  ~TRI_mapper_data();
};

int TRI_open_connection(const char* address, const char* port);

void TRI_send(int fd, const TRI__mapper__PDU& pdu);
void TRI_wait_for_res(int fd,int msg_id,TTCN_Buffer& buff,Result& res);


int TRI_read_socket(int fd,TTCN_Buffer& buff );

bool TRI_get_message(TTCN_Buffer& buff,TRI__mapper__PDU& pdu);


void TRI_map(const char* pt_name, const char* system_port, TRI_mapper_data &mp_data);

bool TRI_set_param(const char *parameter_name, const char *parameter_value, TRI_mapper_data &mp_data);

void TRI_unmap( TRI_mapper_data &mp_data);

void TRI_start(const char* pt_name,const char* name,int comp_id,const char* comp_name, TRI_mapper_data &mp_data);

void TRI_stop(TRI_mapper_data &mp_data);

}

#endif
