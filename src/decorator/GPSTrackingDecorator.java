package decorator;

import factory.Bicycle;

/**
 * GPS 추적 기능 데코레이터
 */
public class GPSTrackingDecorator extends BicycleDecorator {
    private boolean gpsEnabled;
    private double currentLatitude;
    private double currentLongitude;
    
    public GPSTrackingDecorator(Bicycle bicycle) {
        super(bicycle);
        this.gpsEnabled = true;
        this.currentLatitude = 0.0;
        this.currentLongitude = 0.0;
    }
    
    @Override
    public void updateLocation(double latitude, double longitude) {
        super.updateLocation(latitude, longitude);
        if (gpsEnabled) {
            this.currentLatitude = latitude;
            this.currentLongitude = longitude;
            trackLocation();
        }
    }
    
    public void trackLocation() {
        if (gpsEnabled) {
            System.out.printf("[GPS 추적] %s 실시간 위치: (%.6f, %.6f)%n", 
                    bicycleId, currentLatitude, currentLongitude);
        }
    }
    
    public void enableGPS() {
        this.gpsEnabled = true;
        System.out.println("[GPS] " + bicycleId + " GPS 추적이 활성화되었습니다.");
    }
    
    public void disableGPS() {
        this.gpsEnabled = false;
        System.out.println("[GPS] " + bicycleId + " GPS 추적이 비활성화되었습니다.");
    }
    
    public void reportLocation() {
        if (gpsEnabled) {
            System.out.printf("[GPS 위치 보고] 자전거 %s 현재 위치: (%.6f, %.6f)%n", 
                    bicycleId, currentLatitude, currentLongitude);
        } else {
            System.out.println("[GPS] GPS가 비활성화되어 위치를 확인할 수 없습니다.");
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + " [GPS 추적: " + (gpsEnabled ? "ON" : "OFF") + "]";
    }
    
    // Getter 메소드들
    public boolean isGpsEnabled() { return gpsEnabled; }
    public double getCurrentLatitude() { return currentLatitude; }
    public double getCurrentLongitude() { return currentLongitude; }
}