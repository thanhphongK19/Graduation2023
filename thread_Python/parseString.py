import array as arr

def separateString(data =''):

    global dataFromControl
    global dataFromSenSor1
    global dataFromSenSor2

    global doublePacketFlag 
    doublePacketFlag = 0
    array = ['0','0','0','0','0','0','0','0','0','0','0']
    address = ""

    dataFromSenSor1 = [0,0,0,0,0]
    dataFromSenSor2 = [0,0,0,0,0]
    dataFromControl = [0,0,0,0,0]

    
    
    global control
    global sensor1
    global sensor2

    control = 0
    sensor1 = 0
    sensor2 = 0

    if len(data) > 15:
        doublePacketFlag = 1

    arr1 = data.split()

    if doublePacketFlag == 0:
        index = 0
        for arr in arr1:
            if index == 0:
                if int(arr) == 100:
                    print("control node")
                    control = 1
                    index = index + 1
                    continue
                elif int(arr) == 101:
                    print("sensor 1")
                    sensor1 = 1
                    index = index + 1
                    continue
                elif int(arr) == 102:
                    print("sensor 2")
                    sensor2 = 1
                    index = index + 1
                    continue
            if control == 1:
                dataFromControl[index-1] = arr

            elif sensor1 == 1:
                dataFromSenSor1[index-1] = arr

            elif sensor2 == 1:
                dataFromSenSor2[index-1] = arr

            index = index + 1
    
    if doublePacketFlag == 1:
        index1 = 0
        for arr in arr1:
            array[index1] = arr
            print(arr)
            index1  = index1 + 1
        
        if int(array[0]) == 100:
            control = 1
        elif int(array[0]) == 101:
            sensor1 = 1
        elif int(array[0]) == 102:
            sensor2 = 1
        run = True
        i = 0
        while run:
            if i == 4:
                a = str(array[i+1])
                print(a)
                address = a[1:4]
                print(address)
                if control == 1:
                    dataFromControl[i] = a[0:1]
                elif sensor1 == 1:
                    dataFromSenSor1[i] = a[0:1]
                elif sensor2 == 1:
                    dataFromSenSor2[i] = a[0:1]
                break

            if control == 1:
                dataFromControl[i] = array[i+1]

            elif sensor1 == 1:
                dataFromSenSor1[i] = array[i+1]

            elif sensor2 == 1:
                dataFromSenSor2[i] = array[i+1]

            i = i + 1
    
        if int(address) == 100:
            dataFromControl[0] = array[6]
            dataFromControl[1] = array[7]
            dataFromControl[2] = array[8]                  
            dataFromControl[3] = array[9]
            dataFromControl[4] = array[10]
            control = 1
        elif int(address) == 101:
            dataFromSenSor1[0] = array[6]
            dataFromSenSor1[1] = array[7]
            dataFromSenSor1[2] = array[8]                  
            dataFromSenSor1[3] = array[9]
            dataFromSenSor1[4] = array[10]
            sensor1 = 1
        elif int(address) == 102:
            dataFromSenSor2[0] = array[6]
            dataFromSenSor2[1] = array[7]
            dataFromSenSor2[2] = array[8]                  
            dataFromSenSor2[3] = array[9]
            dataFromSenSor2[4] = array[10]
            sensor2 = 1

    if control == 1:
        return dataFromControl
    elif sensor1 == 1:
        return dataFromSenSor1
    elif sensor2 == 1:
        return dataFromSenSor2
     
    


def concatenateString( array = arr.array('I',[]) ):

    String = ''
    index = 0
    for a in array:
        if index == 0:
            String = str(a)
            index = index + 1
            continue
        String = String + ' ' + str(a)
    return String