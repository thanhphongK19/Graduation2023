import paho.mqtt.client as paho
from paho import mqtt
import array as arr


import parseString

# setting callbacks for different events to see if it works, print the message etc.
def on_connect(client, userdata, flags, rc, properties=None):
    print("CONNACK received with code %s." % rc)

# with this callback you can see if your publish was successful
def on_publish(client, userdata, mid, properties=None):
    print("mid: " + str(mid))

# print which topic was subscribed to
def on_subscribe(client, userdata, mid, granted_qos, properties=None):
    print("Subscribed: " + str(mid) + " " + str(granted_qos))

# print message, useful for checking if it was successful
# def on_message(client, userdata, msg):
# print(msg.topic + " " + str(msg.qos) + " " + str(msg.payload))
    


################################################################################################################################    
# subscribe to all topics
# topic in ZONE 1
def subscribe_topic_zoneControl():
# control Pump
    
#     client.subscribe("zoneControl/pump", qos=0)
    
#     client.subscribe("zoneControl/alarmPump", qos=0)
#     client.subscribe("zoneControl/startHour", qos=0)
#     client.subscribe("zoneControl/startMinute", qos=0)
#     client.subscribe("zoneControl/endHour", qos=0)
#     client.subscribe("zoneControl/endMinute", qos=0)
# # topic pH
#     client.subscribe("zoneControl/pH", qos=0)
    

    client.subscribe("zoneControl/dataControl",qos = 0)

# # topic tank
#     client.subscribe("zoneTank/pH", qos=0)
#     client.subscribe("zoneTank/ORP", qos = 0)
# topic status valve 1 -> valve 2 -> valve 3
# client.subscribe("zoneControl/statusValve", qos=0)


# topic in zone Sensor 1 and Sensor 2
def subscribe_topic_zoneSensor():
# topic Data 1 2 3 4 5 
# 1.soil_temperature
# 2.soid_moisture
# 3.air_humidity
# 4.air_temperature
# 5.status node sensor

    client.subscribe("zoneSensor1/Data", qos=0)
    client.subscribe("zoneSensor2/Data", qos=0)
################################################################################################################################






################################################################################################################################
# function for publish data MQTT cloud
def publish_data(topic,data):
    client.publish(topic, payload = data, qos=0)





################################################################################################################################
# Function call back for MQTT
# Pump
# arrayData = arr.array('I',[0,0,0,0,0,0,0])
# def pump_callback(client, userdata, message):
#     global pump,inComing,arrayData
#     arrayData[0] = int(message.payload)
#     inComing = inComing + 1

# # alarm Pump
# def alarmPump_callback(client, userdata, message):
#     global alarmPump,inComing
#     arrayData[2] = int(message.payload)
#     inComing = inComing + 1

# def startHour_callback(client, userdata, message):
#     global startHour,inComing
#     arrayData[3] = int(message.payload)
#     inComing = inComing + 1
# def startMinute_callback(client, userdata, message):
#     global startMinute,inComing
#     arrayData[4] = int(message.payload)
#     inComing = inComing + 1
# def endHour_callback(client, userdata, message):
#     global endHour,inComing
#     arrayData[5] = int(message.payload)
#     inComing = inComing + 1   
# def endMinute_callback(client, userdata, message):
#     global endMinute,inComing
#     arrayData[6] = int(message.payload)
#     inComing = inComing + 1

# # pump pH
# def pH_callback(client, userdata, message):
#     global pH,inComing
#     arrayData[1] = int(message.payload)
#     inComing = inComing + 1

inComing = 0
global dataControl
def dataControl_callback(client,userdata,message):
    global inComing,dataControl
    dataControl = message.payload.decode("utf-8")
    print(dataControl)
    inComing = 1

################################################################################################################################



################################################################################################################################
# client_id is the given name of the client
def initConnectMQTT():
    global client
    client = paho.Client(client_id="", userdata=None, protocol=paho.MQTTv5)
    client.on_connect = on_connect

    # enable TLS for secure connection
    client.tls_set(tls_version=mqtt.client.ssl.PROTOCOL_TLS)
    # set username and password
    client.username_pw_set("hivemq.webclient.1678716757601", "5#HNeSZ9><4gV&Tdn3sp")
    client.connect("7c8f4430aabc40439e135b034a097278.s2.eu.hivemq.cloud", 8883)
        
    # setting callbacks, use separate functions like above for better visibility
    # client.on_subscribe = on_subscribe
    # client.on_message = on_message
    client.on_publish = on_publish
    ################################################################################################################################


    ################################################################################################################################
    # set call back for control pump
    
    
    # client.message_callback_add("zoneControl/pump", pump_callback)
    
    # client.message_callback_add("zoneControl/alarmPump", alarmPump_callback)
    # client.message_callback_add("zoneControl/startHour", startHour_callback)
    # client.message_callback_add("zoneControl/startMinute", startMinute_callback)
    # client.message_callback_add("zoneControl/endHour", endHour_callback)
    # client.message_callback_add("zoneControl/endMinute", endMinute_callback)

    # client.message_callback_add("zoneControl/pH", pH_callback)
    
  
    client.message_callback_add("zoneControl/dataControl", dataControl_callback)
################################################################################################################################



################################################################################################################################

def initSubcribe():
    subscribe_topic_zoneControl()
    subscribe_topic_zoneSensor()

def openConnect():
    client.loop_start()
    

def closeConnect():
    client.loop_stop(force=False)

def dataInComing():
    global inComing
    if inComing == 1:
        inComing = 0
        return 1
    elif inComing == 0:
        return 0







    
