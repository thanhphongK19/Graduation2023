
import severSocket
import parseString
import mqttProtocol
import pyRTOS
import phpMyAdmin


########################################################################################
# Global variable

dataFromSenSor1 = [0,0,0,0,0,0]
dataFromSenSor2 = [0,0,0,0,0,0]
dataFromControl = [0,0,0,0,0,0]

sendSocketData = ' '
receiveSocketData = ' '


glSendSocketFlag = 0
glReceiveSocketFlag = 0
########################################################################################




def networkloop_task(self):
    global glSendSocketFlag
    global sendSocketData
    global inComing 

    mqttProtocol.initConnectMQTT()
    mqttProtocol.initSubcribe()
    mqttProtocol.openConnect()
    yield
    while True:
        #print('task 1 is running')

        inComing = mqttProtocol.dataInComing()
        if inComing == 1:
           #print("data incoming")
           #print(mqttProtocol.dataControl)
           sendSocketData = mqttProtocol.dataControl

           glSendSocketFlag = 1
        elif inComing == 0:
            pass
            

        #mqttProtocol.closeConnect()
        yield[pyRTOS.timeout(1)]



def listenSocketloop_task(self):
    global glSendSocketFlag
    global receiveSocketData
    global glReceiveSocketFlag

    severSocket.initSocket()
    yield
    while True:
        #print('task 2 is running')

        #Wait and listen data incoming from socket
        receiveSocketData = severSocket.receiveSocket()
        if receiveSocketData == 'null':
            pass
        else:
            print("Receive data from thread C: ")
            print(receiveSocketData)
            glReceiveSocketFlag = 1
               
        yield[pyRTOS.timeout(1)]


def main_task(self):
    global glSendSocketFlag
    global glReceiveSocketFlag
    global Data

    phpMyAdmin.initConnectDatabase()  
    yield
    while True:
        #print('main task')

        #Outcoming 
        if glSendSocketFlag == 1:
           
            severSocket.sendSocket(sendSocketData)
            glSendSocketFlag = 0
        
        #Incoming 
        if glReceiveSocketFlag == 1:
            parseString.separateString(receiveSocketData)

            if parseString.control == 1:
                #packet data
                Data = parseString.concatenateString(parseString.dataFromControl)
                
                #publish data to MQTT cloud
                mqttProtocol.publish_data('zoneControl/dataReturn',Data)

            if parseString.sensor1 == 1:
                Data = parseString.concatenateString(parseString.dataFromSenSor1)
                #Publish data to MQTT cloud
                mqttProtocol.publish_data('zoneSensor1/data',Data)

                #Save data to Database
                phpMyAdmin.saveDatabase(1,parseString.dataFromSenSor1)

            if parseString.sensor2 == 1:
                Data = parseString.concatenateString(parseString.dataFromSenSor2)
                #Publish data to MQTT cloud
                mqttProtocol.publish_data('zoneSensor2/data',Data)   

                #save data to Database
                phpMyAdmin.saveDatabase(2,parseString.dataFromSenSor2)

            Data = ''
            glReceiveSocketFlag = 0
           
        yield[pyRTOS.timeout(0.5)]



pyRTOS.add_task(pyRTOS.Task(main_task,priority = 1))
pyRTOS.add_task(pyRTOS.Task(networkloop_task,priority = 2))
pyRTOS.add_task(pyRTOS.Task(listenSocketloop_task,priority = 2))

pyRTOS.start()

# Main Loop
run = True
#while run:
    #print('main is running')
    #client.loop_forever()
    #severSocket.receiveSocket()
    

