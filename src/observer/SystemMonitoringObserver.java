package observer;

/**
 * 시스템 모니터링 옵저버
 */
public class SystemMonitoringObserver implements Observer {
    private String systemName;
    private int eventCount;
    private int criticalEventCount;
    
    public SystemMonitoringObserver(String systemName) {
        this.systemName = systemName;
        this.eventCount = 0;
        this.criticalEventCount = 0;
    }
    
    @Override
    public void update(String bicycleId, String event, String message) {
        eventCount++;
        
        String logLevel = getLogLevel(event);
        String timestamp = java.time.LocalDateTime.now().toString();
        
        if (isCriticalEvent(event)) {
            criticalEventCount++;
        }
        
        logSystemEvent(timestamp, logLevel, bicycleId, event, message);
        
        // 시스템 통계 업데이트
        if (eventCount % 10 == 0) {  // 10개 이벤트마다 통계 출력
            showSystemStats();
        }
    }
    
    private String getLogLevel(String event) {
        switch (event) {
            case "BROKEN":
            case "MAINTENANCE":
                return "ERROR";
            case "LOW_BATTERY":
                return "WARN";
            case "RENT":
            case "RETURN":
            case "LOCATION_CHANGE":
                return "INFO";
            default:
                return "DEBUG";
        }
    }
    
    private boolean isCriticalEvent(String event) {
        return event.equals("BROKEN") || event.equals("MAINTENANCE") || 
               (event.equals("LOCATION_CHANGE") && criticalEventCount > 0);
    }
    
    private void logSystemEvent(String timestamp, String level, String bicycleId, String event, String message) {
        System.out.println("🖥️  [" + systemName + " 시스템 로그]");
        System.out.printf("   %s [%s] Bicycle=%s Event=%s Message=%s%n", 
                timestamp.substring(0, 19), level, bicycleId, event, message);
    }
    
    private void showSystemStats() {
        System.out.println("\n📊 [시스템 통계 - " + systemName + "]");
        System.out.println("   총 이벤트 수: " + eventCount);
        System.out.println("   중요 이벤트 수: " + criticalEventCount);
        System.out.printf("   중요 이벤트 비율: %.1f%%%n", 
                (eventCount > 0 ? (double) criticalEventCount / eventCount * 100 : 0.0));
    }
    
    public void resetStats() {
        eventCount = 0;
        criticalEventCount = 0;
        System.out.println(systemName + " 시스템 통계가 초기화되었습니다.");
    }
    
    public void generateSystemReport() {
        System.out.println("\n📋 [시스템 보고서 - " + systemName + "]");
        System.out.println("   시스템 상태: " + (criticalEventCount > eventCount * 0.3 ? "주의" : "정상"));
        System.out.println("   총 처리된 이벤트: " + eventCount);
        System.out.println("   중요 이벤트: " + criticalEventCount);
        
        if (criticalEventCount > eventCount * 0.3) {
            System.out.println("   ⚠️  중요 이벤트 비율이 높습니다. 시스템 점검이 필요할 수 있습니다.");
        }
    }
    
    // Getter 메소드들
    public String getSystemName() { return systemName; }
    public int getEventCount() { return eventCount; }
    public int getCriticalEventCount() { return criticalEventCount; }
}