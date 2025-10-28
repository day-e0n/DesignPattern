package repair;

import database.RepairDatabase;
import database.CSVDatabase;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 고장 신고 및 수리 관리 서비스
 */
public class RepairService {
    
    // 고장 신고 처리
    public static void reportIssue(String bicycleId, String userId, String issueDescription) {
        String reportId = RepairDatabase.generateReportId();
        RepairDatabase.RepairReport report = new RepairDatabase.RepairReport(reportId, bicycleId, userId, issueDescription);
        RepairDatabase.saveRepairReport(report);
        
        System.out.println("고장 신고가 접수되었습니다.");
        System.out.println("신고 번호: " + reportId);
        System.out.println("관리자 승인을 대기 중입니다...");
    }
    
    // 관리자가 고장 신고를 승인
    public static void approveRepairReport(String reportId, String adminId, String adminResponse) {
        RepairDatabase.RepairReport report = RepairDatabase.getRepairReport(reportId);
        if (report == null) {
            System.out.println("존재하지 않는 신고입니다.");
            return;
        }
        
        if (!"PENDING".equals(report.status)) {
            System.out.println("이미 처리된 신고입니다.");
            return;
        }
        
        report.status = "APPROVED";
        report.adminId = adminId;
        report.adminResponse = adminResponse;
        report.approvedTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        RepairDatabase.updateRepairReport(report);
        
        // 자전거를 고장 상태로 변경
        CSVDatabase.BicycleData bicycle = CSVDatabase.getBicycle(report.bicycleId);
        if (bicycle != null) {
            bicycle.isAvailable = false;
            CSVDatabase.saveBicycle(bicycle);
        }
        
        System.out.println("고장 신고가 승인되었습니다. 자전거가 잠금 처리되었습니다.");
        
        // 1분 후 자동 수리 완료 처리 (시연용)
        scheduleAutoRepair(reportId);
    }
    
    // 관리자가 고장 신고를 거부
    public static void rejectRepairReport(String reportId, String adminId, String adminResponse) {
        RepairDatabase.RepairReport report = RepairDatabase.getRepairReport(reportId);
        if (report == null) {
            System.out.println("존재하지 않는 신고입니다.");
            return;
        }
        
        if (!"PENDING".equals(report.status)) {
            System.out.println("이미 처리된 신고입니다.");
            return;
        }
        
        report.status = "REJECTED";
        report.adminId = adminId;
        report.adminResponse = adminResponse;
        
        RepairDatabase.updateRepairReport(report);
        
        System.out.println("고장 신고가 거부되었습니다. 사유: " + adminResponse);
    }
    
    // 수리 완료 처리
    public static void completeRepair(String reportId) {
        RepairDatabase.RepairReport report = RepairDatabase.getRepairReport(reportId);
        if (report == null) {
            System.out.println("존재하지 않는 신고입니다.");
            return;
        }
        
        if (!"APPROVED".equals(report.status)) {
            System.out.println("승인되지 않은 신고입니다.");
            return;
        }
        
        report.status = "FIXED";
        report.fixedTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        RepairDatabase.updateRepairReport(report);
        
        // 자전거를 다시 사용 가능 상태로 변경
        CSVDatabase.BicycleData bicycle = CSVDatabase.getBicycle(report.bicycleId);
        if (bicycle != null) {
            bicycle.isAvailable = true;
            CSVDatabase.saveBicycle(bicycle);
        }
        
        System.out.println("자전거 " + report.bicycleId + " 수리가 완료되었습니다. 다시 사용 가능합니다.");
    }
    
    // 시연용: 1분 후 자동 수리 완료
    private static void scheduleAutoRepair(String reportId) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                completeRepair(reportId);
                timer.cancel();
            }
        }, 60000); // 1분 = 60000ms
        
        System.out.println("⏰ 시연용: 1분 후 자동으로 수리가 완료됩니다.");
    }
    
    // 대기 중인 고장 신고 목록 조회
    public static void showPendingReports() {
        List<RepairDatabase.RepairReport> pendingReports = RepairDatabase.getPendingReports();
        
        if (pendingReports.isEmpty()) {
            System.out.println("대기 중인 고장 신고가 없습니다.");
            return;
        }
        
        System.out.println("\n=== 대기 중인 고장 신고 목록 ===");
        System.out.printf("%-12s %-12s %-15s %-20s %-30s%n", 
                "신고번호", "자전거ID", "신고자", "접수시간", "고장내용");
        System.out.println("-".repeat(100));
        
        for (RepairDatabase.RepairReport report : pendingReports) {
            String shortTime = report.reportTime.length() > 16 ? 
                    report.reportTime.substring(0, 16).replace("T", " ") : report.reportTime;
            String shortDescription = report.issueDescription.length() > 28 ? 
                    report.issueDescription.substring(0, 25) + "..." : report.issueDescription;
                    
            System.out.printf("%-12s %-12s %-15s %-20s %-30s%n",
                    report.reportId, report.bicycleId, report.userId, shortTime, shortDescription);
        }
    }
    
    // 전체 고장 신고 이력 조회
    public static void showAllReports() {
        List<RepairDatabase.RepairReport> allReports = RepairDatabase.getAllRepairReports();
        
        if (allReports.isEmpty()) {
            System.out.println("고장 신고 이력이 없습니다.");
            return;
        }
        
        System.out.println("\n=== 전체 고장 신고 이력 ===");
        System.out.printf("%-12s %-12s %-15s %-10s %-15s %-20s%n", 
                "신고번호", "자전거ID", "신고자", "상태", "처리관리자", "고장내용");
        System.out.println("-".repeat(100));
        
        for (RepairDatabase.RepairReport report : allReports) {
            String adminId = report.adminId.isEmpty() ? "-" : report.adminId;
            String shortDescription = report.issueDescription.length() > 18 ? 
                    report.issueDescription.substring(0, 15) + "..." : report.issueDescription;
                    
            System.out.printf("%-12s %-12s %-15s %-10s %-15s %-20s%n",
                    report.reportId, report.bicycleId, report.userId, 
                    report.status, adminId, shortDescription);
        }
    }
    
    // 특정 자전거의 고장 신고 이력 조회
    public static void showBicycleRepairHistory(String bicycleId) {
        List<RepairDatabase.RepairReport> allReports = RepairDatabase.getAllRepairReports();
        
        System.out.println("\n=== " + bicycleId + " 자전거 고장 신고 이력 ===");
        
        boolean hasReports = false;
        for (RepairDatabase.RepairReport report : allReports) {
            if (report.bicycleId.equals(bicycleId)) {
                hasReports = true;
                System.out.println("신고번호: " + report.reportId);
                System.out.println("신고자: " + report.userId);
                System.out.println("신고내용: " + report.issueDescription);
                System.out.println("상태: " + report.status);
                System.out.println("접수시간: " + report.reportTime.replace("T", " "));
                
                if (!report.adminResponse.isEmpty()) {
                    System.out.println("관리자 응답: " + report.adminResponse);
                }
                
                System.out.println("-".repeat(50));
            }
        }
        
        if (!hasReports) {
            System.out.println("해당 자전거의 고장 신고 이력이 없습니다.");
        }
    }
}