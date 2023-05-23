import parseString
#import mqttProtocol

# mqttProtocol.initConnectMQTT()
# mqttProtocol.initSubcribe()
# mqttProtocol.openConnect()

receiveSocketData = "102 0 0 0 0 1100 0 0 0 0 1"
print(len(receiveSocketData))
parseString.separateString(receiveSocketData)
if parseString.doublePacketFlag == 1:
    if parseString.control == 1:
        print(parseString.dataFromControl)
    if parseString.sensor1 == 1:
        print(parseString.sensor1)
    if parseString.sensor2 == 1:
        print(parseString.dataFromSenSor2)

# a = receiveSocketData[13:16]
# print(a)


# run = True
# index = 0
# while run:
#     if index < 2:
#       print(index)
#     else:
#        break
#     index = index + 1
#parseString.separateString(receiveSocketData)
# print(parseString.dataFromSenSor1)
# Data = parseString.concatenateString(parseString.dataFromSenSor1)
# print(Data)
# #Publish data to MQTT cloud
# mqttProtocol.publish_data('zoneSensor1/data',Data)
# print("publish data \r\n")

#String
"""
phpMyAdmin.initConnectDatabase()
dataFromSenSor2 = [1,1,1,1,1,1]
data = '102 50 68 65 75 1'
dataFromSenSor2 = parseString.separateString(data)
print(parseString.control)
print(parseString.sensor1)
print(parseString.sensor2)
print(dataFromSenSor2)
phpMyAdmin.saveDatabase(2,dataFromSenSor2)
"""



"""
 zoneSensor1/data :  1 2 3 4 5
   thứ tự dữ liệu:
   1. soil_temperature
   2. soil_moisture
   3. air_humidity
   4. air_temperature
   5. status
"""




"""
dataFromControl = [1,1,1,1,1,1,2]
data = '100 34 56 65 65 7.1 3.4'
dataFromControl = parseString.separateString(data)
print(parseString.control)
print(parseString.sensor1)
print(parseString.sensor2)
print(dataFromControl)
"""






"""
dataToControl = [0,0,1,7,0,7,20]
sendSocketData = parseString.concatenateString(dataToControl)
print(sendSocketData)
"""

##############
#MQTT



##############


#Socket
"""
severSocket.initSocket()
severSocket.sendSocket()
data = severSocket.receiveSocket()
"""