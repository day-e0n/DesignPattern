# 자전거 공유 시스템 - 4가지 디자인 패턴 구현

이 프로젝트는 **전략 패턴**, **팩토리 메소드 패턴**, **데코레이터 패턴**, **옵저버 패턴**을 활용하여 구현된 콘솔 기반 자전거 공유 시스템입니다.

## 📁 프로젝트 구조

```
src/
├── BicycleSharingSystem.java  # 메인 콘솔 애플리케이션
├── strategy/                  # 전략 패턴 - 요금 계산
│   ├── PricingStrategy.java
│   ├── RegularUserPricingStrategy.java
│   ├── StudentPricingStrategy.java
│   ├── PremiumUserPricingStrategy.java
│   └── PricingContext.java
├── factory/                   # 팩토리 메소드 패턴 - 자전거 생성
│   ├── Bicycle.java
│   ├── RegularBicycle.java
│   ├── ElectricBicycle.java
│   ├── BicycleFactory.java
│   ├── RegularBicycleFactory.java
│   └── ElectricBicycleFactory.java
├── decorator/                 # 데코레이터 패턴 - 추가 기능
│   ├── BicycleDecorator.java
│   ├── GPSTrackingDecorator.java
│   ├── SmartLockDecorator.java
│   └── AntiTheftAlarmDecorator.java
└── observer/                  # 옵저버 패턴 - 알림 시스템
    ├── Observer.java
    ├── Subject.java
    ├── UserNotificationObserver.java
    ├── AdminNotificationObserver.java
    └── SystemMonitoringObserver.java
```

## 🎯 구현된 디자인 패턴

### 1. 전략 패턴 (Strategy Pattern)
- **목적**: 사용자 유형별 요금 계산 방식 동적 변경
- **구현**: 일반 사용자, 학생 할인, 프리미엄 사용자 요금제
- **특징**: 런타임에 요금 계산 전략 변경 가능

### 2. 팩토리 메소드 패턴 (Factory Method Pattern)
- **목적**: 자전거 객체 생성의 캡슐화
- **구현**: 일반 자전거, 전기 자전거 팩토리
- **특징**: 자전거 유형 확장 시 기존 코드 수정 없이 새 팩토리 추가

### 3. 데코레이터 패턴 (Decorator Pattern)
- **목적**: 자전거에 추가 기능을 동적으로 부여
- **구현**: GPS 추적, 스마트 잠금, 도난 방지 알람
- **특징**: 기본 자전거 객체를 수정하지 않고 기능 확장

### 4. 옵저버 패턴 (Observer Pattern)
- **목적**: 자전거 상태 변화를 여러 관련 객체에 알림
- **구현**: 사용자 알림, 관리자 알림, 시스템 모니터링
- **특징**: 느슨한 결합으로 알림 대상 동적 추가/제거

## 🚀 실행 방법

### 컴파일
```bash
cd /home/dy/workspace/design_pattern/bicycle_sharing_system/src
javac -cp . *.java */*.java
```

### 실행
```bash
java BicycleSharingSystem
```

## 📱 주요 기능

### 1. 사용자 로그인
- 일반 사용자, 학생, 프리미엄 사용자 선택
- 선택한 유형에 따라 요금 전략 자동 설정

### 2. 자전거 생성 (관리자)
- 일반 자전거 또는 전기 자전거 생성
- 위치 좌표 설정 (위도, 경도)

### 3. 자전거 기능 추가
- GPS 추적: 실시간 위치 추적
- 스마트 잠금: 코드 기반 잠금/해제
- 도난 방지: 무단 이동 감지 알람

### 4. 자전거 대여/반납
- 사용 가능한 자전거 목록 확인
- 스마트 잠금 코드 입력
- 사용 시간과 거리 기반 요금 계산

### 5. 알림 시스템
- 사용자 알림: SMS, 이메일
- 관리자 알림: 긴급상황, 정비 요청
- 시스템 모니터링: 이벤트 로깅, 통계

## 💡 사용 시나리오

1. **시스템 시작**: 초기 자전거 2대 자동 생성
2. **사용자 로그인**: 사용자 유형 선택으로 요금제 설정
3. **자전거 대여**: 위치 확인 후 잠금 해제 코드로 대여
4. **추가 기능**: GPS, 스마트 잠금, 도난 방지 기능 추가
5. **알림 설정**: 대여/반납/고장 시 자동 알림 발송
6. **반납 및 결제**: 사용 시간과 거리로 요금 자동 계산

## 🔧 확장 가능성

- **새로운 자전거 유형**: 팩토리 패턴으로 쉬운 확장
- **새로운 요금제**: 전략 패턴으로 요금 정책 추가
- **새로운 기능**: 데코레이터 패턴으로 기능 확장
- **새로운 알림 채널**: 옵저버 패턴으로 알림 방식 추가

## 📍 위치 기반 서비스

- 기본 위치: 단국대학교 (37.3206, 127.1270)
- 실시간 위치 추적 및 업데이트
- 무단 이동 감지 시 자동 알람

이 시스템은 실제 자전거 공유 서비스의 핵심 기능들을 4가지 디자인 패턴으로 깔끔하게 구현하여, 확장성과 유지보수성을 높인 예제입니다.