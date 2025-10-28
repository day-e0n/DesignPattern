package factory;

/**
 * 자전거 추상 클래스
 */
public abstract class Bicycle {
    protected String bicycleId;
    protected String bicycleType;
    protected boolean use;
    protected double speed;
    
    public Bicycle(String bicycleId) {
        this.bicycleId = bicycleId;
        this.use = false;
        this.speed = 0.0;
    }
    
    // 추상 메소드들
    public abstract void lock();
    public abstract void unlock();
    public abstract void markBroken();
    public abstract boolean isAvailable();
    public abstract void updateLocation(double latitude, double longitude);
    
    // Getter/Setter
    public String getBicycleId() { return bicycleId; }
    public String getBicycleType() { return bicycleType; }
    public boolean isUse() { return use; }
    public double getSpeed() { return speed; }
    
    public void setUse(boolean use) { this.use = use; }
    public void setSpeed(double speed) { this.speed = speed; }
    
    @Override
    public String toString() {
        return String.format("자전거 ID: %s, 타입: %s, 사용중: %s, 속도: %.1f km/h", 
                bicycleId, bicycleType, use ? "예" : "아니오", speed);
    }
}