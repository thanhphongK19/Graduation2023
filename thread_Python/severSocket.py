import socket

# Định nghĩa host và port mà server sẽ chạy và lắng nghe
host = 'localhost'
port = 8000

def initSocket():
  #AF_INET ipv4
  #SOCK_STREAM TCP
  s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  s.bind((host, port))

  s.listen(1) # only accept one client
  print("Server listening on port", port)

  global c
  c, addr = s.accept()
  print("Connect from ", str(addr))


#c.send(bytes(dataSendSocket,"utf8"))
def sendSocket(data =''):   
  #data = input('Nhập tin nhắn gửi đến client: ')
  c.send(bytes(data,"utf8"))
    

def receiveSocket():
  c.settimeout(5)
  try:
    while True:
      msg = c.recv(1024)       
      if msg:
        print("here is the message:",msg.decode())
        return msg.decode()
      else:
        c.close()
        break
  except Exception as e:
    return 'null'
          #print("error is",e)

  #print("Recvied ", msg)
  
  