package database;

import java.io.*;
import java.util.*;

/**
 * CSV 파일 기반 데이터베이스 관리 클래스
 */
public class CSVDatabase {
    private static final String USERS_FILE = "users.csv";
    private static final String BICYCLES_FILE = "bicycles.csv";
    
    // 사용자 데이터 관리
    public static class User {
        public String userId;
        public String password;
        public String name;
        public String userType; // regular, student, premium, regular_monthly, electric_monthly
        public String phone;
        public String email;
        public boolean isAdmin;
        public String subscriptionStartDate; // 구독 시작일 (월정액 사용자용)
        
        public User(String userId, String password, String name, String userType, String phone, String email) {
            this.userId = userId;
            this.password = password;
            this.name = name;
            this.userType = userType;
            this.phone = phone;
            this.email = email;
            this.isAdmin = false;
            this.subscriptionStartDate = "";
        }
        
        public User(String userId, String password, String name, String userType, String phone, String email, boolean isAdmin, String subscriptionStartDate) {
            this.userId = userId;
            this.password = password;
            this.name = name;
            this.userType = userType;
            this.phone = phone;
            this.email = email;
            this.isAdmin = isAdmin;
            this.subscriptionStartDate = subscriptionStartDate != null ? subscriptionStartDate : "";
        }
        
        public String toCSV() {
            return String.join(",", userId, password, name, userType, phone, email, 
                    String.valueOf(isAdmin), subscriptionStartDate);
        }
        
        public static User fromCSV(String csvLine) {
            String[] parts = csvLine.split(",", 8);
            if (parts.length < 6) return null;
            
            boolean isAdmin = parts.length > 6 ? Boolean.parseBoolean(parts[6]) : false;
            String subscriptionStartDate = parts.length > 7 ? parts[7] : "";
            
            // admin 타입인 경우 자동으로 isAdmin을 true로 설정
            if ("admin".equals(parts[3])) {
                isAdmin = true;
            }
            
            return new User(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], isAdmin, subscriptionStartDate);
        }
    }
    
    // 자전거 데이터 관리
    public static class BicycleData {
        public String bicycleId;
        public String bicycleType;
        public String location;
        public boolean isAvailable;
        public boolean inUse;
        public String currentUser;
        
        public BicycleData(String bicycleId, String bicycleType, String location, boolean isAvailable, boolean inUse, String currentUser) {
            this.bicycleId = bicycleId;
            this.bicycleType = bicycleType;
            this.location = location;
            this.isAvailable = isAvailable;
            this.inUse = inUse;
            this.currentUser = currentUser != null ? currentUser : "";
        }
        
        public String toCSV() {
            return String.join(",", bicycleId, bicycleType, location, 
                    String.valueOf(isAvailable), String.valueOf(inUse), currentUser);
        }
        
        public static BicycleData fromCSV(String csvLine) {
            String[] parts = csvLine.split(",");
            if (parts.length != 6) return null;
            return new BicycleData(parts[0], parts[1], parts[2], 
                    Boolean.parseBoolean(parts[3]), Boolean.parseBoolean(parts[4]), parts[5]);
        }
    }
    
    // 사용자 관련 메소드들
    public static void saveUser(User user) {
        try (FileWriter fw = new FileWriter(USERS_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            // 파일이 비어있으면 헤더 추가
            File file = new File(USERS_FILE);
            if (file.length() == 0) {
                pw.println("userId,password,name,userType,phone,email,isAdmin,subscriptionStartDate");
            }
            
            pw.println(user.toCSV());
            System.out.println("사용자 정보가 저장되었습니다: " + user.userId);
            
        } catch (IOException e) {
            System.err.println("사용자 저장 중 오류 발생: " + e.getMessage());
        }
    }
    
    public static User getUser(String userId) {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            // 파일이 없는 경우는 정상 (첫 실행)
            return null;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                User user = User.fromCSV(line);
                if (user != null && user.userId.equals(userId)) {
                    return user;
                }
            }
        } catch (IOException e) {
            System.err.println("사용자 조회 중 오류 발생: " + e.getMessage());
        }
        
        return null;
    }
    
    public static boolean userExists(String userId) {
        return getUser(userId) != null;
    }
    
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            // 파일이 없는 경우는 정상 (첫 실행)
            return users;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                User user = User.fromCSV(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("사용자 목록 조회 중 오류 발생: " + e.getMessage());
        }
        
        return users;
    }
    
    // 자전거 관련 메소드들
    public static void saveBicycle(BicycleData bicycle) {
        List<BicycleData> bicycles = getAllBicycles();
        
        // 기존 자전거 업데이트 또는 새로 추가
        boolean updated = false;
        for (int i = 0; i < bicycles.size(); i++) {
            if (bicycles.get(i).bicycleId.equals(bicycle.bicycleId)) {
                bicycles.set(i, bicycle);
                updated = true;
                break;
            }
        }
        
        if (!updated) {
            bicycles.add(bicycle);
        }
        
        // 전체 파일 다시 쓰기
        try (FileWriter fw = new FileWriter(BICYCLES_FILE);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("bicycleId,bicycleType,location,isAvailable,inUse,currentUser");
            
            for (BicycleData b : bicycles) {
                pw.println(b.toCSV());
            }
            
        } catch (IOException e) {
            System.err.println("자전거 저장 중 오류 발생: " + e.getMessage());
        }
    }
    
    public static BicycleData getBicycle(String bicycleId) {
        File file = new File(BICYCLES_FILE);
        if (!file.exists()) {
            // 파일이 없는 경우는 정상 (첫 실행)
            return null;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(BICYCLES_FILE))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                BicycleData bicycle = BicycleData.fromCSV(line);
                if (bicycle != null && bicycle.bicycleId.equals(bicycleId)) {
                    return bicycle;
                }
            }
        } catch (IOException e) {
            System.err.println("자전거 조회 중 오류 발생: " + e.getMessage());
        }
        
        return null;
    }
    
    public static List<BicycleData> getAllBicycles() {
        List<BicycleData> bicycles = new ArrayList<>();
        File file = new File(BICYCLES_FILE);
        if (!file.exists()) {
            // 파일이 없는 경우는 정상 (첫 실행)
            return bicycles;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(BICYCLES_FILE))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                BicycleData bicycle = BicycleData.fromCSV(line);
                if (bicycle != null) {
                    bicycles.add(bicycle);
                }
            }
        } catch (IOException e) {
            System.err.println("자전거 목록 조회 중 오류 발생: " + e.getMessage());
        }
        
        return bicycles;
    }
    
    public static List<BicycleData> getBicyclesByLocation(String location) {
        List<BicycleData> locationBicycles = new ArrayList<>();
        List<BicycleData> allBicycles = getAllBicycles();
        
        for (BicycleData bicycle : allBicycles) {
            if (bicycle.location.equals(location)) {
                locationBicycles.add(bicycle);
            }
        }
        
        return locationBicycles;
    }
    
    // 초기 데이터 생성
    public static void initializeData() {
        // 초기 자전거 데이터 생성
        List<BicycleData> bicycles = getAllBicycles();
        if (bicycles.isEmpty()) {
            System.out.println("초기 자전거 데이터를 생성합니다...");
            
            // 각 지역별로 자전거 배치
            String[] locations = {"죽전동", "보정동", "구미동", "성복동", "상현동"};
            int bicycleCounter = 1;
            
            for (String location : locations) {
                // 각 지역에 일반 자전거 2대, 전기 자전거 1대 배치
                for (int i = 0; i < 2; i++) {
                    String id = String.format("REG%03d", bicycleCounter++);
                    saveBicycle(new BicycleData(id, "일반 자전거", location, true, false, ""));
                }
                
                String id = String.format("ELC%03d", bicycleCounter++);
                saveBicycle(new BicycleData(id, "전기 자전거", location, true, false, ""));
            }
            
            System.out.println("초기 자전거 " + (bicycleCounter - 1) + "대가 배치되었습니다.");
        }
    }
}