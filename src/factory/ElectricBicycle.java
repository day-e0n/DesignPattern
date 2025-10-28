package factory;

/**
 * 전기 자전거 구현 클래스
 */
public class ElectricBicycle extends Bicycle {
    private boolean isLocked;
    private boolean isBroken;
    private double latitude;
    private double longitude;
    private int batteryLevel;  // 배터리 잔량 (0-100%)
    private boolean electricMode;  // 전기 모드 on/off
    
    public ElectricBicycle(String bicycleId) {
        super(bicycleId);
        this.bicycleType = "전기 자전거";
        this.isLocked = true;
        this.isBroken = false;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.batteryLevel = 100;  // 초기 배터리 100%
        this.electricMode = false;
    }
    
    @Override
    public void lock() {
        this.isLocked = true;
        this.use = false;
        this.speed = 0.0;
        this.electricMode = false;
        System.out.println(bicycleId + " 전기자전거가 잠금되었습니다.");
    }
    
    @Override
    public void unlock() {
        if (isAvailable()) {
            this.isLocked = false;
            this.use = true;
            System.out.println(bicycleId + " 전기자전거 잠금이 해제되었습니다. (배터리: " + batteryLevel + "%)");
        } else {
            System.out.println(bicycleId + " 전기자전거를 사용할 수 없습니다.");
        }
    }
    
    @Override
    public void markBroken() {
        this.isBroken = true;
        this.use = false;
        this.isLocked = true;
        this.electricMode = false;
        System.out.println(bicycleId + " 전기자전거가 고장으로 표시되었습니다.");
    }
    
    @Override
    public boolean isAvailable() {
        return !use && !isBroken && batteryLevel > 10;  // 배터리 10% 이상이어야 사용 가능
    }
    
    @Override
    public void updateLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        System.out.printf("%s 전기자전거 위치 업데이트: (%.6f, %.6f)%n", 
                bicycleId, latitude, longitude);
    }
    
    // 전기자전거 전용 메소드들
    public void toggleElectricMode() {
        if (use && batteryLevel > 10) {
            electricMode = !electricMode;
            System.out.println("전기 모드가 " + (electricMode ? "활성화" : "비활성화") + "되었습니다.");
        } else {
            System.out.println("전기 모드를 변경할 수 없습니다. (배터리 부족 또는 미사용 상태)");
        }
    }
    
    public void chargeBattery() {
        this.batteryLevel = 100;
        System.out.println(bicycleId + " 전기자전거 배터리가 완충되었습니다.");
    }
    
    public void consumeBattery(int amount) {
        this.batteryLevel = Math.max(0, this.batteryLevel - amount);
        if (batteryLevel <= 10) {
            this.electricMode = false;
            System.out.println("배터리 부족으로 전기 모드가 자동 비활성화되었습니다.");
        }
    }
    
    // Getter 메소드들
    public boolean isLocked() { return isLocked; }
    public boolean isBroken() { return isBroken; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getBatteryLevel() { return batteryLevel; }
    public boolean isElectricMode() { return electricMode; }
}