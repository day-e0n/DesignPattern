import factory.*;
import strategy.*;
import decorator.*;
import observer.*;
import database.*;
import location.*;
import repair.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDate;

// 좀 작게 쪼개야할 필요가 있음

/*
 * 자전거 공유 시스템 메인 콘솔 애플리케이션
 * 4가지 디자인 패턴을 통합하여 구현
 */
public class BicycleSharingSystem {
    private Scanner scanner;
    private Map<String, Bicycle> bicycles;
    private Map<String, BicycleStatusSubject> bicycleSubjects;
    private PricingContext pricingContext;
    
    // 사용자 정보 (CSV 파일로 관리)
    private CSVDatabase.User currentUser;
    private String currentUserType; // "regular", "student", "premium"
    
    public BicycleSharingSystem() {
        this.scanner = new Scanner(System.in);
        this.bicycles = new HashMap<>();
        this.bicycleSubjects = new HashMap<>();
        
        // 기본 요금 전략 설정 (일반 사용자)
        this.pricingContext = new PricingContext(new RegularUserPricingStrategy());
    }
    
    public void start() {
        System.out.println("=== 자전거 공유 시스템에 오신 것을 환영합니다! ===");
        
        // 초기 설정
        setupInitialData();
        // CSV 데이터베이스 초기화
        CSVDatabase.initializeData();
        
        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = getIntInput("메뉴를 선택하세요: ");
            
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    showLocationStatus();
                    break;
                case 4:
                    rentBicycle();
                    break;
                case 5:
                    returnBicycle();
                    break;
                case 6:
                    reportBicycleIssue();
                    break;
                case 7:
                    subscribeMonthly();
                    break;
                case 8:
                    calculatePrice();
                    break;
                case 9:
                    setupNotifications();
                    break;
                case 10:
                    adminMenu();
                    break;
                case 0:
                    running = false;
                    System.out.println("시스템을 종료합니다. 안전한 라이딩 되세요!");
                    break;
                default:
                    System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
            }
            
            if (running) {
                System.out.println("\n계속하려면 Enter를 누르세요...");
                scanner.nextLine();
            }
        }
    }
    
    private void showMainMenu() {
        System.out.println("=".repeat(50));
        System.out.println("           스마트 자전거 공유 시스템");
        System.out.println("=".repeat(50));
        if (currentUser != null) {
            String userTypeDisplay = currentUser.userType;
            if (currentUser.isAdmin) {
                userTypeDisplay = "관리자";
            }
            System.out.println("현재 사용자: " + currentUser.name + " (" + userTypeDisplay + ")");
        }
        System.out.println("1. 회원가입");
        System.out.println("2. 로그인");
        System.out.println("3. 지역별 자전거 현황");
        System.out.println("4. 자전거 대여");
        System.out.println("5. 자전거 반납");
        System.out.println("6. 고장 신고");
        System.out.println("7. 월정액 구독");
        System.out.println("8. 요금 계산");
        System.out.println("9. 알림 설정");
        System.out.println("10. 관리자 메뉴");
        System.out.println("0. 종료");
        System.out.println("=".repeat(50));
    }
    
    private void setupInitialData() {
        // 초기 자전거 몇 대 생성
        BicycleFactory regularFactory = new RegularBicycleFactory();
        BicycleFactory electricFactory = new ElectricBicycleFactory();
        
        // 위치는 단국대학교 좌표 기준으로 설정
        double dku_lat = 37.3206;
        double dku_lon = 127.1270;
        
        Bicycle bike1 = regularFactory.orderBicycle("REG001", dku_lat, dku_lon);
        Bicycle bike2 = electricFactory.orderBicycle("ELC001", dku_lat + 0.001, dku_lon + 0.001);
        
        bicycles.put("REG001", bike1);
        bicycles.put("ELC001", bike2);
        
        // 각 자전거에 대한 서브젝트 생성
        bicycleSubjects.put("REG001", new BicycleStatusSubject("REG001"));
        bicycleSubjects.put("ELC001", new BicycleStatusSubject("ELC001"));
        
        System.out.println("\n초기 자전거가 시스템에 등록되었습니다.");
    }
    
    private void registerUser() {
        System.out.println("\n=== 회원가입 ===");
        
        System.out.print("사용자 ID를 입력하세요: ");
        String userId = scanner.nextLine();
        
        // 중복 ID 체크
        try {
            if (CSVDatabase.userExists(userId)) {
                System.out.println("이미 존재하는 사용자 ID입니다.");
                return;
            }
        } catch (Exception e) {
            System.out.println("사용자 확인 중 오류가 발생했습니다: " + e.getMessage());
            return;
        }
        
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();
        
        System.out.print("이름을 입력하세요: ");
        String name = scanner.nextLine();
        
        System.out.print("전화번호를 입력하세요: ");
        String phone = scanner.nextLine();
        
        System.out.print("이메일을 입력하세요: ");
        String email = scanner.nextLine();
        
        System.out.println("사용자 유형을 선택하세요:");
        System.out.println("1. 일반 사용자 (시간당 결제)");
        System.out.println("2. 학생 (할인 요금)");
        System.out.println("3. 프리미엄 사용자");
        System.out.println("4. 일반 자전거 월정액 (15,000원/월)");
        System.out.println("5. 전기 자전거 월정액 (25,000원/월)");
        System.out.println("6. 관리자");
        
        int userType = getIntInput("선택: ");
        String userTypeStr;
        boolean isAdmin = false;
        String subscriptionStartDate = "";
        
        switch (userType) {
            case 2:
                userTypeStr = "student";
                break;
            case 3:
                userTypeStr = "premium";
                break;
            case 4:
                userTypeStr = "regular_monthly";
                subscriptionStartDate = LocalDate.now().toString();
                break;
            case 5:
                userTypeStr = "electric_monthly";
                subscriptionStartDate = LocalDate.now().toString();
                break;
            case 6:
                userTypeStr = "admin";
                isAdmin = true;
                break;
            default:
                userTypeStr = "regular";
        }
        
        CSVDatabase.User newUser = new CSVDatabase.User(userId, password, name, userTypeStr, phone, email, isAdmin, subscriptionStartDate);
        CSVDatabase.saveUser(newUser);
        
        if (userTypeStr.contains("monthly")) {
            System.out.println("월정액 구독이 시작되었습니다!");
            if (userTypeStr.equals("regular_monthly")) {
                System.out.println("일반 자전거 월정액: 15,000원/월 (하루 60분 무료)");
            } else {
                System.out.println("전기 자전거 월정액: 25,000원/월 (하루 45분 무료)");
            }
        }
        
        System.out.println("회원가입이 완료되었습니다! 로그인해주세요.");
    }

    private void loginUser() {
        System.out.println("\n=== 로그인 ===");
        System.out.print("사용자 ID를 입력하세요: ");
        String userId = scanner.nextLine();
        
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();
        
        CSVDatabase.User user = CSVDatabase.getUser(userId);
        
        if (user == null) {
            System.out.println("존재하지 않는 사용자 ID입니다.");
            return;
        }
        
        if (!user.password.equals(password)) {
            System.out.println("비밀번호가 틀렸습니다.");
            return;
        }
        
        currentUser = user;
        currentUserType = user.userType;
        
        // 사용자 유형에 따른 요금 전략 설정
        switch (user.userType) {
            case "student":
                pricingContext.setStrategy(new StudentPricingStrategy());
                break;
            case "premium":
                pricingContext.setStrategy(new PremiumUserPricingStrategy());
                break;
            case "regular_monthly":
                pricingContext.setStrategy(new RegularMonthlyPricingStrategy());
                break;
            case "electric_monthly":
                pricingContext.setStrategy(new ElectricMonthlyPricingStrategy());
                break;
            case "admin":
                pricingContext.setStrategy(new RegularUserPricingStrategy());
                break;
            default:
                pricingContext.setStrategy(new RegularUserPricingStrategy());
        }
        
        System.out.println("로그인 완료! " + user.name + "님 환영합니다.");
        
        if (user.isAdmin) {
            System.out.println("관리자 권한으로 로그인되었습니다. 🔧");
            System.out.println("관리자 메뉴에서 시스템 관리 기능을 이용하실 수 있습니다.");
        } else {
            System.out.println("적용된 요금제: " + pricingContext.getCurrentStrategyName());
        }
    }
    
    private void createBicycle() {
        System.out.println("\n=== 자전거 생성 ===");
        
        System.out.print("자전거 ID를 입력하세요: ");
        String bicycleId = scanner.nextLine();
        
        if (CSVDatabase.getBicycle(bicycleId) != null) {
            System.out.println("이미 존재하는 자전거 ID입니다.");
            return;
        }
        
        System.out.println("자전거 유형을 선택하세요:");
        System.out.println("1. 일반 자전거");
        System.out.println("2. 전기 자전거");
        
        int type = getIntInput("선택: ");
        
        // 지역 선택
        LocationService.showLocationMenu();
        int locationChoice = getIntInput("자전거를 배치할 지역을 선택하세요: ");
        String locationName = LocationService.selectLocationByNumber(locationChoice);
        
        if (locationName == null) {
            System.out.println("올바르지 않은 지역 선택입니다.");
            return;
        }
        
        String bicycleType = (type == 2) ? "전기 자전거" : "일반 자전거";
        
        // CSV 데이터베이스에 저장
        CSVDatabase.BicycleData newBicycle = new CSVDatabase.BicycleData(
                bicycleId, bicycleType, locationName, true, false, "");
        CSVDatabase.saveBicycle(newBicycle);
        
        // 팩토리 패턴으로 실제 자전거 객체도 생성 (메모리 관리용)
        BicycleFactory factory = (type == 2) ? new ElectricBicycleFactory() : new RegularBicycleFactory();
        LocationService.Location loc = LocationService.getLocation(locationName);
        
        Bicycle bicycle = factory.orderBicycle(bicycleId, loc.latitude, loc.longitude);
        bicycles.put(bicycleId, bicycle);
        bicycleSubjects.put(bicycleId, new BicycleStatusSubject(bicycleId));
        
        System.out.println("자전거가 성공적으로 생성되었습니다!");
        System.out.println("자전거 ID: " + bicycleId);
        System.out.println("타입: " + bicycleType);
        System.out.println("배치 지역: " + locationName);
    }
    
    private void addBicycleFeatures() {
        System.out.println("\n=== 자전거 기능 추가 ===");
        
        System.out.print("기능을 추가할 자전거 ID를 입력하세요: ");
        String bicycleId = scanner.nextLine();
        
        Bicycle bicycle = bicycles.get(bicycleId);
        if (bicycle == null) {
            System.out.println("존재하지 않는 자전거 ID입니다.");
            return;
        }
        
        System.out.println("추가할 기능을 선택하세요:");
        System.out.println("1. GPS 추적");
        System.out.println("2. 스마트 잠금");
        System.out.println("3. 도난 방지 알람");
        System.out.println("4. 모든 기능");
        
        int feature = getIntInput("선택: ");
        
        Bicycle decoratedBicycle = bicycle;
        
        switch (feature) {
            case 1:
                decoratedBicycle = new GPSTrackingDecorator(bicycle);
                System.out.println("GPS 추적 기능이 추가되었습니다.");
                break;
            case 2:
                decoratedBicycle = new SmartLockDecorator(bicycle);
                System.out.println("스마트 잠금 기능이 추가되었습니다.");
                break;
            case 3:
                decoratedBicycle = new AntiTheftAlarmDecorator(bicycle);
                System.out.println("도난 방지 알람 기능이 추가되었습니다.");
                break;
            case 4:
                decoratedBicycle = new GPSTrackingDecorator(bicycle);
                decoratedBicycle = new SmartLockDecorator(decoratedBicycle);
                decoratedBicycle = new AntiTheftAlarmDecorator(decoratedBicycle);
                System.out.println("모든 기능이 추가되었습니다.");
                break;
            default:
                System.out.println("잘못된 선택입니다.");
                return;
        }
        
        bicycles.put(bicycleId, decoratedBicycle);
        System.out.println("업데이트된 자전거 정보: " + decoratedBicycle.toString());
    }
    
    private void showLocationStatus() {
        System.out.println("\n=== 지역별 자전거 현황 ===");
        
        System.out.println("1. 전체 지역 현황 보기");
        System.out.println("2. 특정 지역 상세 보기");
        
        int choice = getIntInput("선택: ");
        
        if (choice == 1) {
            LocationService.showAllLocationsStatus();
        } else if (choice == 2) {
            LocationService.showLocationMenu();
            int locationChoice = getIntInput("지역을 선택하세요 (번호): ");
            String locationName = LocationService.selectLocationByNumber(locationChoice);
            
            if (locationName != null) {
                LocationService.showLocationStatus(locationName);
            } else {
                System.out.println("올바르지 않은 선택입니다.");
            }
        } else {
            System.out.println("올바르지 않은 선택입니다.");
        }
    }

    private void rentBicycle() {
        if (currentUser == null) {
            System.out.println("먼저 로그인해주세요.");
            return;
        }
        
        System.out.println("\n=== 자전거 대여 ===");
        
        // 지역 선택
        LocationService.showLocationMenu();
        int locationChoice = getIntInput("자전거를 대여할 지역을 선택하세요: ");
        String locationName = LocationService.selectLocationByNumber(locationChoice);
        
        if (locationName == null) {
            System.out.println("올바르지 않은 지역 선택입니다.");
            return;
        }
        
        // 선택한 지역의 자전거 현황 표시
        LocationService.showLocationStatus(locationName);
        
        List<CSVDatabase.BicycleData> availableBicycles = LocationService.getAvailableBicyclesInLocation(locationName);
        
        if (availableBicycles.isEmpty()) {
            System.out.println("선택한 지역에 대여 가능한 자전거가 없습니다.");
            return;
        }
        
        System.out.print("대여할 자전거 ID를 입력하세요: ");
        String bicycleId = scanner.nextLine();
        
        // 선택한 자전거가 해당 지역에 있고 대여 가능한지 확인
        CSVDatabase.BicycleData selectedBicycle = null;
        for (CSVDatabase.BicycleData bicycle : availableBicycles) {
            if (bicycle.bicycleId.equals(bicycleId)) {
                selectedBicycle = bicycle;
                break;
            }
        }
        
        if (selectedBicycle == null) {
            System.out.println("선택한 자전거를 찾을 수 없거나 대여할 수 없습니다.");
            return;
        }
        
        // 대여 기록 생성
        String rentalId = RepairDatabase.generateRentalId();
        RepairDatabase.RentalHistory rentalHistory = new RepairDatabase.RentalHistory(rentalId, currentUser.userId, bicycleId, locationName);
        RepairDatabase.saveRentalHistory(rentalHistory);
        
        // 자전거 대여 처리
        selectedBicycle.isAvailable = false;
        selectedBicycle.inUse = true;
        selectedBicycle.currentUser = currentUser.userId;
        
        CSVDatabase.saveBicycle(selectedBicycle);
        
        // 옵저버 패턴 - 대여 알림
        BicycleStatusSubject subject = bicycleSubjects.get(bicycleId);
        if (subject != null) {
            subject.bicycleRented(currentUser.userId);
        }
        
        System.out.println("자전거 대여가 완료되었습니다!");
        System.out.println("대여 번호: " + rentalId);
        System.out.println("대여 자전거: " + bicycleId + " (" + selectedBicycle.bicycleType + ")");
        System.out.println("대여 지역: " + locationName);
        System.out.println("안전한 라이딩 되세요! 🚴‍♂️");
    }
    
    private void returnBicycle() {
        if (currentUser == null) {
            System.out.println("먼저 로그인해주세요.");
            return;
        }
        
        System.out.println("\n=== 자전거 반납 ===");
        
        // 현재 사용자가 대여 중인 자전거 찾기
        List<CSVDatabase.BicycleData> userBicycles = new ArrayList<>();
        List<CSVDatabase.BicycleData> allBicycles = CSVDatabase.getAllBicycles();
        
        for (CSVDatabase.BicycleData bicycle : allBicycles) {
            if (bicycle.currentUser.equals(currentUser.userId) && bicycle.inUse) {
                userBicycles.add(bicycle);
            }
        }
        
        if (userBicycles.isEmpty()) {
            System.out.println("현재 대여 중인 자전거가 없습니다.");
            return;
        }
        
        System.out.println("현재 대여 중인 자전거:");
        for (CSVDatabase.BicycleData bicycle : userBicycles) {
            System.out.println("- " + bicycle.bicycleId + " (" + bicycle.bicycleType + ") - " + bicycle.location);
        }
        
        System.out.print("반납할 자전거 ID를 입력하세요: ");
        String bicycleId = scanner.nextLine();
        
        CSVDatabase.BicycleData selectedBicycle = null;
        for (CSVDatabase.BicycleData bicycle : userBicycles) {
            if (bicycle.bicycleId.equals(bicycleId)) {
                selectedBicycle = bicycle;
                break;
            }
        }
        
        if (selectedBicycle == null) {
            System.out.println("올바르지 않은 자전거 ID입니다.");
            return;
        }
        
        // 반납 지역 선택
        System.out.println("반납할 지역을 선택하세요:");
        LocationService.showLocationMenu();
        int locationChoice = getIntInput("지역 선택: ");
        String returnLocation = LocationService.selectLocationByNumber(locationChoice);
        
        if (returnLocation == null) {
            System.out.println("올바르지 않은 지역 선택입니다.");
            return;
        }
        
        double usageTime = getDoubleInput("사용 시간(분)을 입력하세요: ");
        double distance = getDoubleInput("이동 거리(km)를 입력하세요: ");
        
        // 요금 계산
        BigDecimal price = pricingContext.calculatePrice(usageTime, distance);
        
        // 대여 기록 업데이트
        List<RepairDatabase.RentalHistory> userHistories = RepairDatabase.getUserRentalHistory(currentUser.userId);
        RepairDatabase.RentalHistory currentRental = null;
        
        for (RepairDatabase.RentalHistory history : userHistories) {
            if (history.bicycleId.equals(bicycleId) && history.endTime.isEmpty()) {
                currentRental = history;
                break;
            }
        }
        
        if (currentRental != null) {
            currentRental.completeRental(returnLocation, usageTime, distance, price.toString());
            RepairDatabase.updateRentalHistory(currentRental);
        }
        
        // 자전거 상태 업데이트
        selectedBicycle.isAvailable = true;
        selectedBicycle.inUse = false;
        selectedBicycle.currentUser = "";
        selectedBicycle.location = returnLocation; // 반납 지역으로 위치 변경
        
        CSVDatabase.saveBicycle(selectedBicycle);
        
        // 옵저버 패턴 - 반납 알림
        BicycleStatusSubject subject = bicycleSubjects.get(bicycleId);
        if (subject != null) {
            subject.bicycleReturned(currentUser.userId, usageTime);
        }
        
        System.out.println("자전거 반납이 완료되었습니다!");
        System.out.println("반납 자전거: " + bicycleId + " (" + selectedBicycle.bicycleType + ")");
        System.out.println("반납 지역: " + returnLocation);
        System.out.println("사용 시간: " + usageTime + "분");
        System.out.println("이동 거리: " + distance + "km");
        System.out.println("사용 요금: " + price + "원 (" + pricingContext.getCurrentStrategyName() + ")");
        System.out.println("이용해주셔서 감사합니다!");
    }
    
    private void checkBicycleStatus() {
        System.out.println("\n=== 자전거 상태 확인 ===");
        
        if (bicycles.isEmpty()) {
            System.out.println("등록된 자전거가 없습니다.");
            return;
        }
        
        for (Bicycle bicycle : bicycles.values()) {
            System.out.println(bicycle.toString());
            System.out.println("사용 가능: " + (bicycle.isAvailable() ? "예" : "아니오"));
            System.out.println("-".repeat(40));
        }
    }
    
    private void showAvailableBicycles() {
        System.out.println("\n사용 가능한 자전거 목록:");
        boolean hasAvailable = false;
        
        for (Bicycle bicycle : bicycles.values()) {
            if (bicycle.isAvailable()) {
                System.out.println("- " + bicycle.toString());
                hasAvailable = true;
            }
        }
        
        if (!hasAvailable) {
            System.out.println("현재 사용 가능한 자전거가 없습니다.");
        }
    }
    
    private void calculatePrice() { // 재검토 해봐야 하는 부분
        System.out.println("\n=== 요금 계산 ===");
        
        double usageTime = getDoubleInput("사용 시간(분)을 입력하세요: ");
        double distance = getDoubleInput("이동 거리(km)를 입력하세요: ");
        
        System.out.println("\n다양한 요금제별 비교:");
        
        // 각 전략별로 요금 계산
        PricingStrategy[] strategies = {
            new RegularUserPricingStrategy(),
            new StudentPricingStrategy(),
            new PremiumUserPricingStrategy(),
            new RegularMonthlyPricingStrategy(),
            new ElectricMonthlyPricingStrategy()
        };
        
        for (PricingStrategy strategy : strategies) {
            PricingContext tempContext = new PricingContext(strategy);
            BigDecimal price = tempContext.calculatePrice(usageTime, distance);
            System.out.println("- " + strategy.getStrategyName() + ": " + price + "원");
        }
        
        // 현재 사용자 요금
        BigDecimal currentPrice = pricingContext.calculatePrice(usageTime, distance);
        System.out.println("\n현재 사용자 (" + currentUserType + ") 요금: " + currentPrice + "원");
    }
    
    private void changeLocation() {
        System.out.println("\n=== 위치 변경 (시뮬레이션) ===");
        
        System.out.print("위치를 변경할 자전거 ID를 입력하세요: ");
        String bicycleId = scanner.nextLine();
        
        Bicycle bicycle = bicycles.get(bicycleId);
        if (bicycle == null) {
            System.out.println("존재하지 않는 자전거 ID입니다.");
            return;
        }
        
        double latitude = getDoubleInput("새로운 위도를 입력하세요: ");
        double longitude = getDoubleInput("새로운 경도를 입력하세요: ");
        
        bicycle.updateLocation(latitude, longitude);
        
        // 옵저버 패턴 - 위치 변경 알림
        BicycleStatusSubject subject = bicycleSubjects.get(bicycleId);
        if (subject != null) {
            subject.locationChanged(latitude, longitude);
        }
    }
    
    private void setupNotifications() {
        System.out.println("\n=== 알림 설정 ===");
        
        System.out.print("알림을 설정할 자전거 ID를 입력하세요: ");
        String bicycleId = scanner.nextLine();
        
        BicycleStatusSubject subject = bicycleSubjects.get(bicycleId);
        if (subject == null) {
            System.out.println("존재하지 않는 자전거 ID입니다.");
            return;
        }
        
        System.out.println("설정할 알림 유형을 선택하세요:");
        System.out.println("1. 사용자 알림 추가");
        System.out.println("2. 관리자 알림 추가");
        System.out.println("3. 시스템 모니터링 추가");
        
        int type = getIntInput("선택: ");
        
        switch (type) {
            case 1:
                System.out.print("사용자 ID: ");
                String userId = scanner.nextLine();
                System.out.print("전화번호: ");
                String phone = scanner.nextLine();
                System.out.print("이메일: ");
                String email = scanner.nextLine();
                
                UserNotificationObserver userObserver = new UserNotificationObserver(userId, phone, email);
                subject.addObserver(userObserver);
                break;
                
            case 2:
                System.out.print("관리자 ID: ");
                String adminId = scanner.nextLine();
                System.out.print("부서: ");
                String department = scanner.nextLine();
                
                AdminNotificationObserver adminObserver = new AdminNotificationObserver(adminId, department);
                subject.addObserver(adminObserver);
                break;
                
            case 3:
                System.out.print("시스템 이름: ");
                String systemName = scanner.nextLine();
                
                SystemMonitoringObserver systemObserver = new SystemMonitoringObserver(systemName);
                subject.addObserver(systemObserver);
                break;
                
            default:
                System.out.println("잘못된 선택입니다.");
        }
    }
    
    private void reportBicycleIssue() {
        if (currentUser == null) {
            System.out.println("먼저 로그인해주세요.");
            return;
        }
        
        System.out.println("\n=== 자전거 고장 신고 ===");
        System.out.print("고장 신고할 자전거 ID를 입력하세요: ");
        String bicycleId = scanner.nextLine();
        
        CSVDatabase.BicycleData bicycle = CSVDatabase.getBicycle(bicycleId);
        if (bicycle == null) {
            System.out.println("존재하지 않는 자전거 ID입니다.");
            return;
        }
        
        System.out.print("고장 내용을 입력하세요: ");
        String issueDescription = scanner.nextLine();
        
        RepairService.reportIssue(bicycleId, currentUser.userId, issueDescription);
    }
    
    private void subscribeMonthly() {
        if (currentUser == null) {
            System.out.println("먼저 로그인해주세요.");
            return;
        }
        
        if (currentUser.userType.contains("monthly")) {
            System.out.println("이미 월정액을 구독하고 계십니다.");
            return;
        }
        
        System.out.println("\n=== 월정액 구독 ===");
        System.out.println("1. 일반 자전거 월정액 (15,000원/월) - 하루 60분 무료");
        System.out.println("2. 전기 자전거 월정액 (25,000원/월) - 하루 45분 무료");
        System.out.println("3. 취소");
        
        int choice = getIntInput("선택: ");
        
        String newUserType = "";
        String subscriptionInfo = "";
        
        switch (choice) {
            case 1:
                newUserType = "regular_monthly";
                subscriptionInfo = "일반 자전거 월정액 (15,000원/월)";
                pricingContext.setStrategy(new RegularMonthlyPricingStrategy());
                break;
            case 2:
                newUserType = "electric_monthly";
                subscriptionInfo = "전기 자전거 월정액 (25,000원/월)";
                pricingContext.setStrategy(new ElectricMonthlyPricingStrategy());
                break;
            case 3:
                return;
            default:
                System.out.println("올바르지 않은 선택입니다.");
                return;
        }
        
        // 사용자 정보 업데이트
        currentUser.userType = newUserType;
        currentUser.subscriptionStartDate = LocalDate.now().toString();
        currentUserType = newUserType;
        
        CSVDatabase.saveUser(currentUser);
        
        System.out.println("월정액 구독이 완료되었습니다!");
        System.out.println("구독 상품: " + subscriptionInfo);
        System.out.println("구독 시작일: " + currentUser.subscriptionStartDate);
        System.out.println("매월 자동 결제됩니다.");
    }

    private void adminMenu() {
        if (currentUser == null || !currentUser.isAdmin) {
            System.out.println("관리자 권한이 필요합니다.");
            return;
        }
        
        System.out.println("\n=== 관리자 메뉴 ===");
        System.out.println("1. 자전거 생성");
        System.out.println("2. 전체 자전거 상태 확인");
        System.out.println("3. 사용자 목록 조회");
        System.out.println("4. 고장 신고 승인/거부");
        System.out.println("5. 고장 신고 이력 조회");
        System.out.println("6. 대여 이력 조회");
        System.out.println("7. 시스템 통계");
        System.out.println("8. 자전거 기능 추가 (데코레이터)");
        System.out.println("9. 돌아가기");
        
        int choice = getIntInput("선택: ");
        
        switch (choice) {
            case 1:
                createBicycle();
                break;
            case 2:
                checkAllBicycleStatus();
                break;
            case 3:
                showAllUsers();
                break;
            case 4:
                handleRepairReports();
                break;
            case 5:
                RepairService.showAllReports();
                break;
            case 6:
                showRentalHistory();
                break;
            case 7:
                showSystemStats();
                break;
            case 8:
                addBicycleFeatures();
                break;
            case 9:
                return;
            default:
                System.out.println("올바르지 않은 선택입니다.");
        }
    }
    
    private void handleRepairReports() {
        RepairService.showPendingReports();
        
        System.out.print("\n처리할 신고 번호를 입력하세요 (취소: 0): ");
        String reportId = scanner.nextLine();
        
        if ("0".equals(reportId)) {
            return;
        }
        
        RepairDatabase.RepairReport report = RepairDatabase.getRepairReport(reportId);
        if (report == null) {
            System.out.println("존재하지 않는 신고 번호입니다.");
            return;
        }
        
        System.out.println("\n신고 상세 정보:");
        System.out.println("신고 번호: " + report.reportId);
        System.out.println("자전거 ID: " + report.bicycleId);
        System.out.println("신고자: " + report.userId);
        System.out.println("고장 내용: " + report.issueDescription);
        System.out.println("접수 시간: " + report.reportTime.replace("T", " "));
        
        System.out.println("\n처리 방법을 선택하세요:");
        System.out.println("1. 승인 (자전거 잠금)");
        System.out.println("2. 거부 (허위 신고)");
        System.out.println("3. 취소");
        
        int action = getIntInput("선택: ");
        
        switch (action) {
            case 1:
                System.out.print("승인 사유를 입력하세요: ");
                String approveReason = scanner.nextLine();
                RepairService.approveRepairReport(reportId, currentUser.userId, approveReason);
                break;
            case 2:
                System.out.print("거부 사유를 입력하세요: ");
                String rejectReason = scanner.nextLine();
                RepairService.rejectRepairReport(reportId, currentUser.userId, rejectReason);
                break;
            case 3:
                return;
            default:
                System.out.println("올바르지 않은 선택입니다.");
        }
    }
    
    private void showRentalHistory() {
        System.out.println("1. 전체 대여 이력");
        System.out.println("2. 특정 사용자 대여 이력");
        
        int choice = getIntInput("선택: ");
        
        if (choice == 1) {
            List<RepairDatabase.RentalHistory> allHistories = RepairDatabase.getAllRentalHistories();
            
            if (allHistories.isEmpty()) {
                System.out.println("대여 이력이 없습니다.");
                return;
            }
            
            System.out.println("\n=== 전체 대여 이력 ===");
            System.out.printf("%-12s %-15s %-12s %-10s %-10s %-8s %-10s%n", 
                    "대여번호", "사용자ID", "자전거ID", "시작지역", "종료지역", "사용시간", "요금");
            System.out.println("-".repeat(80));
            
            for (RepairDatabase.RentalHistory history : allHistories) {
                System.out.printf("%-12s %-15s %-12s %-10s %-10s %-8.0f %-10s%n",
                        history.rentalId, history.userId, history.bicycleId,
                        history.startLocation, history.endLocation.isEmpty() ? "사용중" : history.endLocation,
                        history.usageTimeMinutes, history.price + "원");
            }
        } else if (choice == 2) {
            System.out.print("조회할 사용자 ID: ");
            String userId = scanner.nextLine();
            
            List<RepairDatabase.RentalHistory> userHistories = RepairDatabase.getUserRentalHistory(userId);
            
            if (userHistories.isEmpty()) {
                System.out.println("해당 사용자의 대여 이력이 없습니다.");
                return;
            }
            
            System.out.println("\n=== " + userId + " 사용자 대여 이력 ===");
            for (RepairDatabase.RentalHistory history : userHistories) {
                System.out.println("대여번호: " + history.rentalId);
                System.out.println("자전거: " + history.bicycleId);
                System.out.println("시작: " + history.startLocation + " (" + history.startTime.replace("T", " ") + ")");
                if (!history.endTime.isEmpty()) {
                    System.out.println("종료: " + history.endLocation + " (" + history.endTime.replace("T", " ") + ")");
                    System.out.println("사용시간: " + history.usageTimeMinutes + "분");
                    System.out.println("요금: " + history.price + "원");
                } else {
                    System.out.println("상태: 사용 중");
                }
                System.out.println("결제상태: " + history.paymentStatus);
                System.out.println("-".repeat(50));
            }
        }
    }
    
    private void checkAllBicycleStatus() {
        System.out.println("\n=== 전체 자전거 상태 ===");
        List<CSVDatabase.BicycleData> allBicycles = CSVDatabase.getAllBicycles();
        
        if (allBicycles.isEmpty()) {
            System.out.println("등록된 자전거가 없습니다.");
            return;
        }
        
        System.out.printf("%-10s %-15s %-10s %-10s %-10s %-15s%n", 
                "자전거ID", "타입", "지역", "사용가능", "사용중", "현재사용자");
        System.out.println("-".repeat(80));
        
        for (CSVDatabase.BicycleData bicycle : allBicycles) {
            System.out.printf("%-10s %-15s %-10s %-10s %-10s %-15s%n",
                    bicycle.bicycleId,
                    bicycle.bicycleType,
                    bicycle.location,
                    bicycle.isAvailable ? "O" : "X",
                    bicycle.inUse ? "O" : "X",
                    bicycle.currentUser.isEmpty() ? "-" : bicycle.currentUser);
        }
    }
    
    private void showAllUsers() {
        System.out.println("\n=== 사용자 목록 ===");
        List<CSVDatabase.User> users = CSVDatabase.getAllUsers();
        
        if (users.isEmpty()) {
            System.out.println("등록된 사용자가 없습니다.");
            return;
        }
        
        System.out.printf("%-15s %-15s %-15s %-15s %-20s%n", 
                "사용자ID", "이름", "유형", "전화번호", "이메일");
        System.out.println("-".repeat(80));
        
        for (CSVDatabase.User user : users) {
            System.out.printf("%-15s %-15s %-15s %-15s %-20s%n",
                    user.userId, user.name, user.userType, user.phone, user.email);
        }
    }

    private void showSystemStats() {
        System.out.println("\n=== 시스템 통계 ===");
        
        List<CSVDatabase.BicycleData> allBicycles = CSVDatabase.getAllBicycles();
        List<CSVDatabase.User> allUsers = CSVDatabase.getAllUsers();
        
        int totalBicycles = allBicycles.size();
        int availableCount = 0;
        int inUseCount = 0;
        int regularCount = 0;
        int electricCount = 0;
        
        for (CSVDatabase.BicycleData bicycle : allBicycles) {
            if (bicycle.isAvailable && !bicycle.inUse) availableCount++;
            if (bicycle.inUse) inUseCount++;
            if (bicycle.bicycleType.contains("일반")) regularCount++;
            else if (bicycle.bicycleType.contains("전기")) electricCount++;
        }
        
        int regularUsers = 0, studentUsers = 0, premiumUsers = 0;
        for (CSVDatabase.User user : allUsers) {
            switch (user.userType) {
                case "regular": regularUsers++; break;
                case "student": studentUsers++; break;
                case "premium": premiumUsers++; break;
            }
        }
        
        System.out.println("📊 자전거 현황:");
        System.out.println("  총 자전거 수: " + totalBicycles + "대");
        System.out.println("  사용 가능: " + availableCount + "대");
        System.out.println("  사용 중: " + inUseCount + "대");
        System.out.println("  일반 자전거: " + regularCount + "대");
        System.out.println("  전기 자전거: " + electricCount + "대");
        
        System.out.println("\n👥 사용자 현황:");
        System.out.println("  총 사용자 수: " + allUsers.size() + "명");
        System.out.println("  일반 사용자: " + regularUsers + "명");
        System.out.println("  학생 사용자: " + studentUsers + "명");
        System.out.println("  프리미엄 사용자: " + premiumUsers + "명");
        
        if (currentUser != null) {
            System.out.println("\n🙋‍♂️ 현재 사용자:");
            System.out.println("  이름: " + currentUser.name);
            System.out.println("  유형: " + currentUser.userType);
            System.out.println("  적용 요금제: " + pricingContext.getCurrentStrategyName());
        }
        
        System.out.println("\n📍 지역별 현황:");
        LocationService.showAllLocationsStatus();
    }
    
    // 유틸리티 메소드들
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int result = Integer.parseInt(scanner.nextLine());
                return result;
            } catch (NumberFormatException e) {
                System.out.println("올바른 숫자를 입력하세요.");
            }
        }
    }
    
    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double result = Double.parseDouble(scanner.nextLine());
                return result;
            } catch (NumberFormatException e) {
                System.out.println("올바른 숫자를 입력하세요.");
            }
        }
    }
    
    public static void main(String[] args) {
        BicycleSharingSystem system = new BicycleSharingSystem();
        system.start();
    }
}