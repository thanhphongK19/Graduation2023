import mysql.connector
import array as arr

def initConnectDatabase():

    global mydb
    global mycursor

    mydb = mysql.connector.connect(
        host="localhost",
        user="*********",
        password="*********",
        database="zonesensor_data"
    )
    mycursor=mydb.cursor()


"""
data[0] =  soil_temperature 
data[1] =  soil_moisture 
data[2] =  air_humidity 
data[3] =  air_temperature 
"""

def saveDatabase(zone = 0, data = arr.array('I',[])):

    if zone == 1:    
        sql = "INSERT INTO zone1 (soil_temperature, soil_moisture, air_humidity, air_temperature) VALUES (%s,%s,%s,%s)"
    elif zone == 2:
        sql = "INSERT INTO zone2 (soil_temperature, soil_moisture, air_humidity, air_temperature) VALUES (%s,%s,%s,%s)"
    

    val = (data[0], data[1], data[2], data[3])
    mycursor.execute(sql, val)    

    mydb.commit()

    print("save successfully")

#initConnectDatabase()
#saveDatabase()    
