package factory;

/**
 * 일반 자전거 구현 클래스
 */
public class RegularBicycle extends Bicycle {
    private boolean isLocked;
    private boolean isBroken;
    private double latitude;
    private double longitude;
    
    public RegularBicycle(String bicycleId) {
        super(bicycleId);
        this.bicycleType = "일반 자전거";
        this.isLocked = true;
        this.isBroken = false;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }
    
    @Override
    public void lock() {
        this.isLocked = true;
        this.use = false;
        this.speed = 0.0;
        System.out.println(bicycleId + " 자전거가 잠금되었습니다.");
    }
    
    @Override
    public void unlock() {
        if (isAvailable()) {
            this.isLocked = false;
            this.use = true;
            System.out.println(bicycleId + " 자전거 잠금이 해제되었습니다.");
        } else {
            System.out.println(bicycleId + " 자전거를 사용할 수 없습니다.");
        }
    }
    
    @Override
    public void markBroken() {
        this.isBroken = true;
        this.use = false;
        this.isLocked = true;
        System.out.println(bicycleId + " 자전거가 고장으로 표시되었습니다.");
    }
    
    @Override
    public boolean isAvailable() {
        return !use && !isBroken;
    }
    
    @Override
    public void updateLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        System.out.printf("%s 자전거 위치 업데이트: (%.6f, %.6f)%n", 
                bicycleId, latitude, longitude);
    }
    
    // Getter 메소드들
    public boolean isLocked() { return isLocked; }
    public boolean isBroken() { return isBroken; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}