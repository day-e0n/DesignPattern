package location;

import java.util.*;
import database.CSVDatabase.BicycleData;
import database.CSVDatabase;

/**
 * 위치 관리 서비스 클래스
 */
public class LocationService {
    
    // 지역 정보 클래스
    public static class Location {
        public String name;
        public double latitude;
        public double longitude;
        
        public Location(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
    
    // 용인시 기흥구 지역들 (단국대학교 주변)
    private static final Map<String, Location> LOCATIONS = new HashMap<>();
    
    static {
        // 실제 용인시 기흥구 좌표 기준
        LOCATIONS.put("죽전동", new Location("죽전동", 37.3238, 127.1069));
        LOCATIONS.put("보정동", new Location("보정동", 37.3195, 127.1154));
        LOCATIONS.put("구미동", new Location("구미동", 37.2896, 127.1139));
        LOCATIONS.put("성복동", new Location("성복동", 37.3089, 127.1285));
        LOCATIONS.put("상현동", new Location("상현동", 37.2985, 127.1234));
    }
    
    public static List<String> getAvailableLocations() {
        return new ArrayList<>(LOCATIONS.keySet());
    }
    
    public static Location getLocation(String locationName) {
        return LOCATIONS.get(locationName);
    }
    
    public static boolean isValidLocation(String locationName) {
        return LOCATIONS.containsKey(locationName);
    }
    
    public static void showLocationMenu() {
        System.out.println("\n=== 지역 선택 ===");
        List<String> locations = getAvailableLocations();
        
        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i + 1) + ". " + locations.get(i));
        }
    }
    
    public static String selectLocationByNumber(int number) {
        List<String> locations = getAvailableLocations();
        if (number >= 1 && number <= locations.size()) {
            return locations.get(number - 1);
        }
        return null;
    }
    
    public static void showLocationStatus(String locationName) {
        if (!isValidLocation(locationName)) {
            System.out.println("올바르지 않은 지역명입니다.");
            return;
        }
        
        List<BicycleData> bicycles = CSVDatabase.getBicyclesByLocation(locationName);
        
        System.out.println("\n=== " + locationName + " 자전거 현황 ===");
        
        if (bicycles.isEmpty()) {
            System.out.println("이 지역에는 자전거가 배치되어 있지 않습니다.");
            return;
        }
        
        // 통계 계산
        int totalBicycles = bicycles.size();
        int availableBicycles = 0;
        int inUseBicycles = 0;
        int regularBicycles = 0;
        int electricBicycles = 0;
        
        for (BicycleData bicycle : bicycles) {
            if (bicycle.isAvailable) availableBicycles++;
            if (bicycle.inUse) inUseBicycles++;
            
            if (bicycle.bicycleType.contains("일반")) regularBicycles++;
            else if (bicycle.bicycleType.contains("전기")) electricBicycles++;
        }
        
        // 현황 출력
        System.out.println("📍 위치: " + locationName);
        Location loc = getLocation(locationName);
        System.out.printf("🗺️  좌표: (%.4f, %.4f)%n", loc.latitude, loc.longitude);
        System.out.println("🚴 총 자전거 수: " + totalBicycles + "대");
        System.out.println("✅ 대여 가능: " + availableBicycles + "대");
        System.out.println("🔄 사용 중: " + inUseBicycles + "대");
        System.out.println("🚲 일반 자전거: " + regularBicycles + "대");
        System.out.println("⚡ 전기 자전거: " + electricBicycles + "대");
        
        // 대여 가능한 자전거 목록
        if (availableBicycles > 0) {
            System.out.println("\n📋 대여 가능한 자전거 목록:");
            for (BicycleData bicycle : bicycles) {
                if (bicycle.isAvailable && !bicycle.inUse) {
                    System.out.println("  - " + bicycle.bicycleId + " (" + bicycle.bicycleType + ")");
                }
            }
        } else {
            System.out.println("\n❌ 현재 대여 가능한 자전거가 없습니다.");
        }
    }
    
    public static void showAllLocationsStatus() {
        System.out.println("\n=== 전체 지역 자전거 현황 ===");
        
        for (String locationName : getAvailableLocations()) {
            List<BicycleData> bicycles = CSVDatabase.getBicyclesByLocation(locationName);
            
            int totalBicycles = bicycles.size();
            int availableBicycles = 0;
            
            for (BicycleData bicycle : bicycles) {
                if (bicycle.isAvailable && !bicycle.inUse) {
                    availableBicycles++;
                }
            }
            
            String status = availableBicycles > 0 ? "✅" : "❌";
            System.out.printf("%s %s: %d/%d대 사용가능%n", 
                    status, locationName, availableBicycles, totalBicycles);
        }
    }
    
    public static List<BicycleData> getAvailableBicyclesInLocation(String locationName) {
        List<BicycleData> availableBicycles = new ArrayList<>();
        List<BicycleData> allBicycles = CSVDatabase.getBicyclesByLocation(locationName);
        
        for (BicycleData bicycle : allBicycles) {
            if (bicycle.isAvailable && !bicycle.inUse) {
                availableBicycles.add(bicycle);
            }
        }
        
        return availableBicycles;
    }
}