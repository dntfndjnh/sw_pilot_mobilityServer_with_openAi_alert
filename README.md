#  스마트팩토리 Zone 이상 탐지 및 보고서 자동화 시스템 스프링부트 서버

## 개요
공장 내 여러 Zone에서 발생 가능한 이상 상황(예: 화재, 단선, 재고부족 등)을 3D프린터 제작 차량에 장착된 카메라 모듈과 YOLOv5n 기반 AI 모델을 활용해 실시간으로 탐지. 탐지된 데이터는 Arduino와 Jetson Nano 간 시리얼 통신을 통해 수집 후 Spring Boot 기반 서버로 전송되며, 서버에서는 수집된 데이터를 시각화, 분석하고 OpenAI API를 활용하여 사용자 맞춤 PDF 리포트를 생성. 또한, 연기, 화재 등 위험 상황 발생 시 WebSocket 통신으로 사용자 인터페이스에 즉시 알림 팝업을 제공하여 안전 대응을 지원하는 통합 산업 모니터링 시스템.

## 사용 기술
- 백엔드 서버: Spring Boot, WebSocket
- 리포트 생성: Open Ai API / html2pdf.js
- 하드웨어/통합 테스트 환경: Jetson Nano, Arduino 기반 동키카, RFID 센서, 3D프린터 제작 차량

## 핵심 기능
- 실시간 객체 탐지 데이터 수집 및 서버 전송
- 발생 시간, Zone 위치, 상황 종류 등 이상 상황 기록 및 누적 데이터 저장
- HTML 기반 대시보드에서 이상 상황 시각화
- 위험 상황 발생 시 사용자 페이지에 WebSocket 팝업 알림 제공
- OpenAI API 호출을 통한 분석 후 PDF 리포트 생성 및 저장

- 


## 프로젝트 구조
<img width="1476" height="666" alt="Image" src="https://github.com/user-attachments/assets/68c83d82-cca7-4b47-bb71-19cc7e1182d0" />


## 실행 이미지
<img width="598" height="590" alt="Image" src="https://github.com/user-attachments/assets/bdb5d374-60c5-4462-971c-42525b69233f" />

<img width="1205" height="876" alt="Image" src="https://github.com/user-attachments/assets/7c18abdf-568c-4a06-ae2d-f76b8e6d5556" />
