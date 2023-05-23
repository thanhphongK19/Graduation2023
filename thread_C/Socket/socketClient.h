#include <stdio.h> 
#include <sys/socket.h> 
#include <stdlib.h> 
#include <netinet/in.h> 
#include <string.h> 

#ifdef __cplusplus
extern "C"{
#endif

extern int setupSocket();
extern void sendSocket(char *data);
extern char *receiveSocket();
extern void setTimeout();
extern void createThread();
extern bool receivePacketFromServer();
void *threadCheckPacketsFromServer(void *vargp);

extern void clearBuffer();
#ifdef __cplusplus
} // extern "C"
#endif
