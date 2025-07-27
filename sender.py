import requests

def send(itemName,areaName):
    url = "http://192.168.0.4:8080/api/send"

    data = {
        "itemName": itemName,
        "value": 1,
        "areaName": areaName   # 구역 이름 추가
    }

    response = requests.post(url, json=data)
    print(response.text)


send("불","Zone A")
send("연기","Zone B")


