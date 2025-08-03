#  스마트팩토리 Zone 이상 탐지 및 보고서 자동화 시스템 스프링부트 서버

## 개요
공장 내 여러 Zone에서 발생 가능한 이상 상황(예: 화재, 단선 등)을 객체탐지를 통해 감지하고, 
현황 데이터를 서버로 전송. Spring Boot 기반 서버는 수신된 데이터를 시각화 및 분석 후, 
open ai API 호출 후 사용자에게 PDF 리포트를 생성하는 시스템입니다.

## 사용 기술
- 백엔드 서버: Spring Boot, WebSocket
- 리포트 생성: Open Ai API / html2pdf.js
- 통합 테스트 환경 예시: 젯슨 나노, 아두이노 기반 동키카

## 핵심 기능
- 실시간 객체 감지 데이터 수신가능 서버 
- 상황별 누적 값 및 발생시간, zone위치, 상황종류 저장 및 html보고서 페이지에 시각화
- 객체 탐지 후 이상 상황 발생 시 사용자 페이지에 팝업
- Open Ai API 호출 및 PDF 보고서 생성 기능


## 프로젝트 구조
<img width="1476" height="666" alt="Image" src="https://github.com/user-attachments/assets/68c83d82-cca7-4b47-bb71-19cc7e1182d0" />


## 실행 이미지
<img width="598" height="590" alt="Image" src="https://github.com/user-attachments/assets/bdb5d374-60c5-4462-971c-42525b69233f" />

<img width="1205" height="876" alt="Image" src="https://github.com/user-attachments/assets/7c18abdf-568c-4a06-ae2d-f76b8e6d5556" />
