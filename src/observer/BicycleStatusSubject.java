package observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 자전거 상태 관리 및 알림 서브젝트
 */
public class BicycleStatusSubject implements Subject {
    private List<Observer> observers;
    private String bicycleId;
    
    public BicycleStatusSubject(String bicycleId) {
        this.observers = new ArrayList<>();
        this.bicycleId = bicycleId;
    }
    
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
        System.out.println("새로운 관찰자가 " + bicycleId + " 자전거를 구독했습니다.");
    }
    
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
        System.out.println("관찰자가 " + bicycleId + " 자전거 구독을 해제했습니다.");
    }
    
    @Override
    public void notifyObservers(String event, String message) {
        System.out.println("\n[알림 발송] " + bicycleId + " - " + event);
        for (Observer observer : observers) {
            observer.update(bicycleId, event, message);
        }
    }
    
    // 자전거 상태 변화 메소드들
    public void bicycleRented(String userId) {
        notifyObservers("RENT", userId + "님이 자전거를 대여했습니다.");
    }
    
    public void bicycleReturned(String userId, double usageTime) {
        notifyObservers("RETURN", userId + "님이 자전거를 반납했습니다. (사용시간: " + usageTime + "분)");
    }
    
    public void bicycleBroken(String issue) {
        notifyObservers("BROKEN", "자전거에 문제가 발생했습니다: " + issue);
    }
    
    public void lowBattery(int batteryLevel) {
        notifyObservers("LOW_BATTERY", "배터리가 부족합니다: " + batteryLevel + "%");
    }
    
    public void locationChanged(double latitude, double longitude) {
        String locationMsg = String.format("위치가 변경되었습니다: (%.6f, %.6f)", latitude, longitude);
        notifyObservers("LOCATION_CHANGE", locationMsg);
    }
    
    public void maintenanceRequired(String reason) {
        notifyObservers("MAINTENANCE", "정비가 필요합니다: " + reason);
    }
}