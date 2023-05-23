#include <stdio.h> 
#include <stdint.h>
#include <string.h>
#include <signal.h>
#include <time.h>

#include <sys/socket.h> 
#include <stdlib.h> 
#include <netinet/in.h> 
#include <string.h> 
#include <arpa/inet.h>
#include <unistd.h>
#include "socketClient.h"
#include <pthread.h>

#ifdef __cplusplus
extern "C" {
#endif

#define PORT 4000 

struct sockaddr_in address; 
int sock = 0, valread; 
struct sockaddr_in serv_addr; 

char buffer[1024] = {0};
volatile bool is_packet_from_server = false;

 int acce ;

/* address sever*/ 
char add[] = "127.0.0.1";

unsigned char continu = 1; 
int data ;

struct timeval timeout;      


extern int setupSocket()
{
    timeout.tv_sec = 1;
    timeout.tv_usec = 0;
    
 

    //printf("Nhap dia chi server\n");
    //gets(add);
    //tao socket
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) 
    { 
        printf("\n Socket creation error \n"); 
        return 0; 
    } 
    printf(" Socket creation successfully \r\n");

    memset(&serv_addr, '0', sizeof(serv_addr)); 
    serv_addr.sin_family = AF_INET; 
    serv_addr.sin_port = htons(PORT); 

    // Convert IPv4 and IPv6 addresses from text to binary form 
    if(inet_pton(AF_INET, add, &serv_addr.sin_addr) <= 0)  
    { 
        printf("\nInvalid address/ Address not supported \n"); 
        return 0; 
    } 
    // connect
    if (connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0) 
    { 
        printf("\nConnection Failed \n"); 
        return 0; 
    }

    printf("Connect sever successfully \r\n");
    return 1;
}

extern void sendSocket(char *data)
{
    //printf("Nhap noi dung tin nhan gui den server\n");
    //gets(mess_from_client);
    //fflush(stdin);
    //hello = &mess_from_client;
    send(sock , data , strlen(data) , 0);
}

extern char *receiveSocket()
{
    //printf("Tin nhan ban nhan dc tu server: \n");
     
    //printf("%s\n",buffer);
    return buffer;
}

extern void setTimeout()
{
    if (setsockopt (sock, SOL_SOCKET, SO_RCVTIMEO, &timeout,
                sizeof timeout) < 0)
        perror("setsockopt failed\n");

    if (setsockopt (sock, SOL_SOCKET, SO_SNDTIMEO, &timeout,
                sizeof timeout) < 0)
        perror("setsockopt failed\n");
}

void *threadCheckPacketsFromServer(void *vargp) { 
	//sleep(1); 
	printf("PACKETS FROM SERVER:: this is thread for waiting packets from server \n");
	while(true){
		valread = read(sock , buffer, 1024);
		is_packet_from_server = true;
	}
	return NULL; 
}

extern void createThread()
{
    pthread_t thread_check_packets_from_server;
    pthread_create(&thread_check_packets_from_server,NULL,&threadCheckPacketsFromServer,NULL);
    //configThread(thread_check_packets_from_server,&threadCheckPacketsFromServer);
}


extern bool receivePacketFromServer()
{
    return is_packet_from_server;
}

extern void clearBuffer()
{
    is_packet_from_server = false;
    for(int i = 0;i<20;i++){
        buffer[i] = 0;
    }
}

#ifdef __cplusplus
} // extern "C"
#endif
