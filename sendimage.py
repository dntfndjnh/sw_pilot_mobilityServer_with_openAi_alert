import requests

# 업로드할 이미지 경로
file_path = 'C:\\Users\\user\\Desktop\\경북대\\25-1학기\\SW_pilot\\smoke.jpg'


# 스프링부트 서버 URL
url = 'http://192.168.0.4:8080/imagesend'

# 이미지 전송
with open(file_path, 'rb') as f:
    files = {'file': f}
    response = requests.post(url, files=files)

# 응답 출력
print('Status Code:', response.status_code)
print('Response:', response.text)
