package observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 관리자 알림 옵저버
 */
public class AdminNotificationObserver implements Observer {
    private String adminId;
    private String department;
    private List<String> alertHistory;
    
    public AdminNotificationObserver(String adminId, String department) {
        this.adminId = adminId;
        this.department = department;
        this.alertHistory = new ArrayList<>();
    }
    
    @Override
    public void update(String bicycleId, String event, String message) {
        String timestamp = java.time.LocalDateTime.now().toString();
        String alertMessage = String.format("[%s] %s - %s: %s", 
                timestamp, bicycleId, event, message);
        
        alertHistory.add(alertMessage);
        
        switch (event) {
            case "BROKEN":
                sendCriticalAlert("자전거 고장", bicycleId, message);
                break;
            case "MAINTENANCE":
                sendMaintenanceAlert("정비 요청", bicycleId, message);
                break;
            case "LOW_BATTERY":
                sendInfoAlert("배터리 경고", bicycleId, message);
                break;
            case "LOCATION_CHANGE":
                // 무단 이동 의심 시에만 알림 (실제로는 더 복잡한 로직 필요)
                if (message.contains("무단")) {
                    sendCriticalAlert("도난 의심", bicycleId, message);
                }
                break;
            case "RENT":
            case "RETURN":
                // 대여/반납은 일반 로그로만 기록
                logActivity(bicycleId, event, message);
                break;
            default:
                sendInfoAlert("일반 알림", bicycleId, message);
                break;
        }
    }
    
    private void sendCriticalAlert(String alertType, String bicycleId, String message) {
        System.out.println("🚨 [긴급 - 관리자 알림 - " + adminId + "]");
        System.out.println("   부서: " + department);
        System.out.println("   유형: " + alertType);
        System.out.println("   자전거: " + bicycleId);
        System.out.println("   내용: " + message);
        System.out.println("   ⚠️  즉시 조치가 필요합니다!");
    }
    
    private void sendMaintenanceAlert(String alertType, String bicycleId, String message) {
        System.out.println("🔧 [정비 - 관리자 알림 - " + adminId + "]");
        System.out.println("   부서: " + department);
        System.out.println("   유형: " + alertType);
        System.out.println("   자전거: " + bicycleId);
        System.out.println("   내용: " + message);
    }
    
    private void sendInfoAlert(String alertType, String bicycleId, String message) {
        System.out.println("ℹ️  [정보 - 관리자 알림 - " + adminId + "]");
        System.out.println("   부서: " + department);
        System.out.println("   유형: " + alertType);
        System.out.println("   자전거: " + bicycleId);
        System.out.println("   내용: " + message);
    }
    
    private void logActivity(String bicycleId, String event, String message) {
        System.out.println("📋 [활동 로그 - " + adminId + "] " + bicycleId + " - " + event + ": " + message);
    }
    
    public void showAlertHistory() {
        System.out.println("\n=== " + adminId + " 관리자 알림 이력 ===");
        if (alertHistory.isEmpty()) {
            System.out.println("알림 이력이 없습니다.");
        } else {
            for (int i = Math.max(0, alertHistory.size() - 10); i < alertHistory.size(); i++) {
                System.out.println((i + 1) + ". " + alertHistory.get(i));
            }
        }
    }
    
    public void clearAlertHistory() {
        alertHistory.clear();
        System.out.println(adminId + " 관리자의 알림 이력이 초기화되었습니다.");
    }
    
    // Getter 메소드들
    public String getAdminId() { return adminId; }
    public String getDepartment() { return department; }
    public List<String> getAlertHistory() { return new ArrayList<>(alertHistory); }
}