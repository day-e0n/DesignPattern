package decorator;

import factory.Bicycle;

/**
 * 도난 방지 알람 기능 데코레이터
 */
public class AntiTheftAlarmDecorator extends BicycleDecorator {
    private boolean alarmEnabled;
    private boolean alarmTriggered;
    private double lastKnownLatitude;
    private double lastKnownLongitude;
    private long lastUpdateTime;
    
    public AntiTheftAlarmDecorator(Bicycle bicycle) {
        super(bicycle);
        this.alarmEnabled = true;
        this.alarmTriggered = false;
        this.lastKnownLatitude = 0.0;
        this.lastKnownLongitude = 0.0;
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    @Override
    public void updateLocation(double latitude, double longitude) {
        super.updateLocation(latitude, longitude);
        
        if (alarmEnabled && !use) {  // 사용 중이 아닌데 위치가 변경되면
            checkForUnauthorizedMovement(latitude, longitude);
        }
        
        this.lastKnownLatitude = latitude;
        this.lastKnownLongitude = longitude;
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    private void checkForUnauthorizedMovement(double newLatitude, double newLongitude) {
        double distance = calculateDistance(lastKnownLatitude, lastKnownLongitude, 
                                          newLatitude, newLongitude);
        
        // 50미터 이상 이동 시 알람 발생
        if (distance > 0.05) {
            triggerAlarm();
        }
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 간단한 유클리드 거리 계산 (실제로는 Haversine 공식 사용 권장)
        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;
        return Math.sqrt(deltaLat * deltaLat + deltaLon * deltaLon);
    }
    
    private void triggerAlarm() {
        if (!alarmTriggered) {
            alarmTriggered = true;
            System.out.println("🚨 [도난 방지 알람] " + bicycleId + " 자전거에서 무단 이동이 감지되었습니다!");
            System.out.printf("   현재 위치: (%.6f, %.6f)%n", lastKnownLatitude, lastKnownLongitude);
            System.out.println("   관리자에게 알림이 전송되었습니다.");
        }
    }
    
    public void enableAlarm() {
        this.alarmEnabled = true;
        this.alarmTriggered = false;
        System.out.println("[도난 방지] " + bicycleId + " 도난 방지 알람이 활성화되었습니다.");
    }
    
    public void disableAlarm() {
        this.alarmEnabled = false;
        this.alarmTriggered = false;
        System.out.println("[도난 방지] " + bicycleId + " 도난 방지 알람이 비활성화되었습니다.");
    }
    
    public void resetAlarm() {
        this.alarmTriggered = false;
        System.out.println("[도난 방지] " + bicycleId + " 알람이 초기화되었습니다.");
    }
    
    public void checkAlarmStatus() {
        System.out.println("[도난 방지 상태]");
        System.out.println("  자전거 ID: " + bicycleId);
        System.out.println("  알람 활성화: " + (alarmEnabled ? "ON" : "OFF"));
        System.out.println("  알람 발생: " + (alarmTriggered ? "YES" : "NO"));
        System.out.printf("  마지막 위치: (%.6f, %.6f)%n", lastKnownLatitude, lastKnownLongitude);
    }
    
    @Override
    public String toString() {
        return super.toString() + " [도난방지: " + (alarmEnabled ? "ON" : "OFF") + 
               (alarmTriggered ? " - 알람발생!" : "") + "]";
    }
    
    // Getter 메소드들
    public boolean isAlarmEnabled() { return alarmEnabled; }
    public boolean isAlarmTriggered() { return alarmTriggered; }
    public double getLastKnownLatitude() { return lastKnownLatitude; }
    public double getLastKnownLongitude() { return lastKnownLongitude; }
}