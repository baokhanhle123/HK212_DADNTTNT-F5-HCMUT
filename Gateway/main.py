# import serial.tools.list_ports
import random
import time
import sys
import json
from Adafruit_IO import MQTTClient

AIO_FEED_ID = "homeinfo"
AIO_USERNAME = "baokhanhle123"
AIO_KEY = "aio_DOYX86EJoTGu2HbllWmTpmdcpBO3"


def connected(client):
    print("Ket noi thanh cong ...")
    client.subscribe(AIO_FEED_ID)


def subscribe(client, userdata, mid, granted_qos):
    print("Subcribe thanh cong ... ")


def disconnected(client):
    print("Ngat ket noi ... ")
    sys.exit(1)


def message(client, feed_id, payload):
    print("Nhan du lieu : " + payload)
    # # print(str(payload))
    # # if str(payload) != "":
    # #     waitingPeriod = 0
    # #     sendingMessageAgain = False
    # ser.write((str(payload) + "#").encode())


# def getPort ():
#     ports = serial.tools.list_ports.comports()
#     N = len(ports)
#     commPort = "None"
#     for i in range(0, N):
#         port = ports[i]
#         strPort = str(port)
#         if "FabulaTech Virtual Serial Port" in strPort:
#             splitPort = strPort.split(" ")
#             commPort = (splitPort[0])
#     return commPort

# ser = serial.Serial(port = getPort(), baudrate = 115200)

# mess = ""
# def processData(data):
#     data = data.replace("!", "")
#     data = data.replace("#", "")
#     splitData = data.split(":")
#     print(splitData)
#     if splitData[1] == "BUTTON":
#         client.publish("button", splitData[2])

# mess = ""
# def readSerial():
#     bytesToRead = ser.inWaiting()
#     if (bytesToRead > 0):
#         global mess
#         mess = mess + ser.read(bytesToRead).decode("UTF-8")
#         while ("#" in mess) and ("!" in mess):
#             start = mess.find("!")
#             end = mess.find("#")
#             processData(mess[start:end + 1])
#             if (end == len(mess)):
#                 mess = ""
#             else:
#                 mess = mess[end + 1:]


client = MQTTClient(AIO_USERNAME, AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()

# waitingPeriod = 0
# sendingMessageAgain = False

# WAITING_ACK = False
# MAX_SEND_COUNT = 5

# def sendMQTTMessage():
#     waitingPeriod = 3
#     data = random.randint(0,100)
#     client.publish("button", data)
#     return

while True:
    data = {
        "temp": random.randint(35, 40),
        "humidity": random.randint(0, 100),
        "gas": random.randint(0, 100)
    }
    json_data = json.dumps(data)
    client.publish("HomeInfo", json_data)
    time.sleep(10)
