# 📱 Patient Management App
 [![Kotlin](https://img.shields.io/badge/kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
 [![Android](https://img.shields.io/badge/platform-Android-green.svg)](https://developer.android.com)
 [![API](https://img.shields.io/badge/API-34%2B-brightgreen.svg)](https://android-arsenal.com/api?level=34)
## 프로젝트 소개  
카메라 촬영과 AI를 통한 환자 행동 인식 정보를 확인할 수 있고, RTSP 카메라의 실시간 영상을 스트리밍할 수 있는 시니어 위험 행동 감지 전용 Android 애플리케이션입니다.
<br><br/>
## ✨ 주요 기능

- **🏠 홈 화면**: 카메라 현황 대시보드

- **📹 카메라 관리**: RTSP 카메라 설정 및 관리, 실시간 스트리밍 모니터링

- **📊 분석 기능**: 날짜별 데이터 분석, 시각적 데이터 표현

- **📅 캘린더**: 월별 발생 상황 캘린더 뷰, 날짜별 상세 정보 조회, 환자의 행동 인식 정보와 영상 확인

- **🚨 알림 기능**:  FCM을 통한 이상행동 감지 알람
<br><br/>
## 🛠 기술 스택

### 개발 환경
- **언어**: Kotlin
- **SDK**: Android API 34+ (Target: API 34)
- **IDE**: Android Studio
- **빌드 도구**: Gradle (KTS)

### 주요 라이브러리
| 카테고리 | 라이브러리 | 용도 |
|----------|------------|------|
| **Backend** | Firebase | 데이터베이스, 인증, 저장소, FCM |
| **UI** | Jetpack Compose | 사용자 인터페이스 |
| **Media** | ExoPlayer | RTSP 스트리밍 및 미디어 플레이어 |
| **Chart** | Vico | 차트 및 그래프 라이브러리 |
| **Calendar** | Compose Calendar | 캘린더 컴포넌트 |
| **DI** | Dagger Hilt | 의존성 주입 |
| **Async** | Coroutines | 비동기 처리 |
| **Auth** | Google, Kakao, Naver SDK | 소셜 로그인 |
| **Image** | Coil | 이미지 로딩 |

### 아키텍처
- **패턴**: 패턴: MVVM (Model-View-ViewModel)
- **구조**: 구조: Clean Architecture
- **의존성 주입**: 의존성 주입: Dagger Hilt
- **UI**: Jetpack Compose
<br><br/>
## 🚀 시작하기

### 요구사항
- Android SDK 34 이상
- JDK 17
  
### Firebase 설정
  - Firebase 프로젝트 생성
  - google-services.json 파일을 app/ 디렉토리에 추가

### API 키 설정((local.properties 파일에 다음 키들을 추가)
- kakaoLogin_api_key="YOUR_KAKAO_API_KEY"
- kakaoLogin_Redirect_Uri="YOUR_KAKAO_REDIRECT_URI"
- naverLogin_Client_Id="YOUR_NAVER_CLIENT_ID"
- naverLogin_Client_Secret="YOUR_NAVER_CLIENT_SECRET"
- googleLogin_WebClient_Id="YOUR_GOOGLE_WEB_CLIENT_ID"
<br><br/>
## 📊 프로젝트 정보

- 개발 기간: 2024년 7월 ~ 2024년 10월
- 개발 인원:
  - 안드로이드 개발자 1명
  - AI 담당 개발자 3명
<br><br/>
## 📺 구현 화면
### - 카메라 리스트 화면 및 이상 행동 통계 화면   
<img width="255" height="495" alt="Image" src="https://github.com/user-attachments/assets/b695736e-470a-4c4a-8bed-044ffbe3eba1" />
<img width="255" height="495" alt="Image" src="https://github.com/user-attachments/assets/a0e47a98-52eb-4574-94e7-bd374322edab" />

### - 이상 행동 정보 달력 화면 및 이상 행동 상세 정보 화면   
<img width="255" height="495" alt="Image" src="https://github.com/user-attachments/assets/525c5e99-e060-46c3-a463-c666696725c0" />
<img width="255" height="496" alt="Image" src="https://github.com/user-attachments/assets/c61c1350-3881-4cf5-b7f0-b1ffdf9d104d" />

### - 실시간 스트리밍 화면
<img width="811" height="381" alt="Image" src="https://github.com/user-attachments/assets/d774a135-5f4d-493f-9560-5d85be161c3c" />
