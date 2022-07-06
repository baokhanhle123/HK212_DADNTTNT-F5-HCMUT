import random
import time
import sys
import json
from Adafruit_IO import MQTTClient

AIO_FEED_ID = "homeinfo"
AIO_USERNAME = "baokhanhle123"
AIO_KEY = "aio_Onze223vCipIRE6K9Sr1zI5JoJK4"

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

client = MQTTClient(AIO_USERNAME, AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()

import cv2
from playsound import playsound


fire_cascade = cv2.CascadeClassifier('fire_detection.xml')

cap = cv2.VideoCapture("fire.mp4")

while(True):
    ret, frame = cap.read()
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    fire = fire_cascade.detectMultiScale(frame, 1.2, 5)

    for (x,y,w,h) in fire:
        cv2.rectangle(frame,(x-20,y-20),(x+w+20,y+h+20),(255,0,0),2)
        roi_gray = gray[y:y+h, x:x+w]
        roi_color = frame[y:y+h, x:x+w]
        print("fire is detected")
        client.publish("alarm", 1)
        playsound('audio.mp3')

    cv2.imshow('frame', frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
