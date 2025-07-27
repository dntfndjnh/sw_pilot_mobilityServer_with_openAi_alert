import serial
import time
import requests
import RPi.GPIO as GPIO

#  서버 전송 함수
def send(itemName, areaName):
    url = "http://localhost:8080/api/send"
    data = {
        "itemName": itemName,
        "value": 1,
        "areaName": areaName
    }
    try:
        response = requests.post(url, json=data)
        print("📨 전송 완료:", response.text)
    except Exception as e:
        print("⚠️ 서버 전송 실패:", e)

#  L298N 핀 설정
ENA1 = 33; IN1_1 = 35; IN2_1 = 37; ENB1 = 32; IN3_1 = 36; IN4_1 = 38
ENA2 = 12; IN1_2 = 16; IN2_2 = 18; ENB2 = 13; IN3_2 = 15; IN4_2 = 19

#  GPIO 초기화
GPIO.setmode(GPIO.BOARD)
GPIO.setwarnings(False)
for pin in [ENA1, ENB1, IN1_1, IN2_1, IN3_1, IN4_1, ENA2, ENB2, IN1_2, IN2_2, IN3_2, IN4_2]:
    GPIO.setup(pin, GPIO.OUT)
    GPIO.output(pin, GPIO.LOW)

#  모터 제어 함수
def all_stop():
    GPIO.output(IN1_1, GPIO.LOW); GPIO.output(IN2_1, GPIO.LOW)
    GPIO.output(IN3_1, GPIO.LOW); GPIO.output(IN4_1, GPIO.LOW)
    GPIO.output(IN1_2, GPIO.LOW); GPIO.output(IN2_2, GPIO.LOW)
    GPIO.output(IN3_2, GPIO.LOW); GPIO.output(IN4_2, GPIO.LOW)

def all_forward():
    GPIO.output(ENA1, GPIO.HIGH); GPIO.output(ENB1, GPIO.HIGH)
    GPIO.output(ENA2, GPIO.HIGH); GPIO.output(ENB2, GPIO.HIGH)
    GPIO.output(IN1_1, GPIO.HIGH); GPIO.output(IN2_1, GPIO.LOW)
    GPIO.output(IN3_1, GPIO.HIGH); GPIO.output(IN4_1, GPIO.LOW)
    GPIO.output(IN1_2, GPIO.HIGH); GPIO.output(IN2_2, GPIO.LOW)
    GPIO.output(IN3_2, GPIO.HIGH); GPIO.output(IN4_2, GPIO.LOW)

#  RFID UID → 플랫폼 매핑
UID_TO_PLATFORM = {
    "357AE359": "Zone A",  # 플랫폼 1
    "038737FC": "Zone B",  # 플랫폼 2
    "E32C41FC": "Zone C"   # 플랫폼 3
}

#  시리얼 통신 설정
ser = serial.Serial('/dev/ttyACM0', 9600, timeout=0.1)
time.sleep(2)

print("🚗 기본 전진 시작")
all_forward()
current_state = "FORWARD"

try:
    while True:
        if ser.in_waiting > 0:
            line = ser.readline().decode('utf-8').strip()
            if line.startswith("UID:"):
                uid = line[4:].strip().upper()
                print(f"📡 UID 수신: {uid}")

                if uid in UID_TO_PLATFORM:
                    platform = UID_TO_PLATFORM[uid]
                    print(f" RFID 인식: {platform}, 정지 중...")
                    all_stop()

                    send("smoke", platform)  # 🔁 서버에 전송

                    print(" 전송 완료, 다시 전진 시작")
                    all_forward()

except KeyboardInterrupt:
    print("종료합니다.")

finally:
    ser.close()
    all_stop()
    GPIO.cleanup()
