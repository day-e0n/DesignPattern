package database;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 고장 신고 및 관리자 승인 시스템 데이터베이스
 */
public class RepairDatabase {
    private static final String REPAIR_REPORTS_FILE = "repair_reports.csv";
    private static final String RENTAL_HISTORY_FILE = "rental_history.csv";
    
    // 고장 신고 데이터 클래스
    public static class RepairReport {
        public String reportId;
        public String bicycleId;
        public String userId;
        public String issueDescription;
        public String reportTime;
        public String status; // "PENDING", "APPROVED", "REJECTED", "FIXED"
        public String adminId;
        public String adminResponse;
        public String approvedTime;
        public String fixedTime;
        
        public RepairReport(String reportId, String bicycleId, String userId, String issueDescription) {
            this.reportId = reportId;
            this.bicycleId = bicycleId;
            this.userId = userId;
            this.issueDescription = issueDescription;
            this.reportTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.status = "PENDING";
            this.adminId = "";
            this.adminResponse = "";
            this.approvedTime = "";
            this.fixedTime = "";
        }
        
        public String toCSV() {
            return String.join(",", reportId, bicycleId, userId, issueDescription, reportTime, 
                    status, adminId, adminResponse, approvedTime, fixedTime);
        }
        
        public static RepairReport fromCSV(String csvLine) {
            String[] parts = csvLine.split(",", 10);
            if (parts.length != 10) return null;
            
            RepairReport report = new RepairReport(parts[0], parts[1], parts[2], parts[3]);
            report.reportTime = parts[4];
            report.status = parts[5];
            report.adminId = parts[6];
            report.adminResponse = parts[7];
            report.approvedTime = parts[8];
            report.fixedTime = parts[9];
            return report;
        }
    }
    
    // 대여 기록 데이터 클래스
    public static class RentalHistory {
        public String rentalId;
        public String userId;
        public String bicycleId;
        public String startTime;
        public String endTime;
        public String startLocation;
        public String endLocation;
        public double usageTimeMinutes;
        public double distanceKm;
        public String price;
        public String paymentStatus; // "PAID", "PENDING", "FAILED"
        
        public RentalHistory(String rentalId, String userId, String bicycleId, String startLocation) {
            this.rentalId = rentalId;
            this.userId = userId;
            this.bicycleId = bicycleId;
            this.startTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.endTime = "";
            this.startLocation = startLocation;
            this.endLocation = "";
            this.usageTimeMinutes = 0.0;
            this.distanceKm = 0.0;
            this.price = "0";
            this.paymentStatus = "PENDING";
        }
        
        public void completeRental(String endLocation, double usageTime, double distance, String price) {
            this.endTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.endLocation = endLocation;
            this.usageTimeMinutes = usageTime;
            this.distanceKm = distance;
            this.price = price;
            this.paymentStatus = "PAID";
        }
        
        public String toCSV() {
            return String.join(",", rentalId, userId, bicycleId, startTime, endTime, 
                    startLocation, endLocation, String.valueOf(usageTimeMinutes), 
                    String.valueOf(distanceKm), price, paymentStatus);
        }
        
        public static RentalHistory fromCSV(String csvLine) {
            String[] parts = csvLine.split(",", 11);
            if (parts.length != 11) return null;
            
            RentalHistory history = new RentalHistory(parts[0], parts[1], parts[2], parts[5]);
            history.startTime = parts[3];
            history.endTime = parts[4];
            history.endLocation = parts[6];
            history.usageTimeMinutes = Double.parseDouble(parts[7]);
            history.distanceKm = Double.parseDouble(parts[8]);
            history.price = parts[9];
            history.paymentStatus = parts[10];
            return history;
        }
    }
    
    // 고장 신고 관련 메소드들
    public static void saveRepairReport(RepairReport report) {
        try (FileWriter fw = new FileWriter(REPAIR_REPORTS_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            File file = new File(REPAIR_REPORTS_FILE);
            if (file.length() == 0) {
                pw.println("reportId,bicycleId,userId,issueDescription,reportTime,status,adminId,adminResponse,approvedTime,fixedTime");
            }
            
            pw.println(report.toCSV());
            
        } catch (IOException e) {
            System.err.println("고장 신고 저장 중 오류 발생: " + e.getMessage());
        }
    }
    
    public static void updateRepairReport(RepairReport report) {
        List<RepairReport> reports = getAllRepairReports();
        
        for (int i = 0; i < reports.size(); i++) {
            if (reports.get(i).reportId.equals(report.reportId)) {
                reports.set(i, report);
                break;
            }
        }
        
        // 전체 파일 다시 쓰기
        try (FileWriter fw = new FileWriter(REPAIR_REPORTS_FILE);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("reportId,bicycleId,userId,issueDescription,reportTime,status,adminId,adminResponse,approvedTime,fixedTime");
            
            for (RepairReport r : reports) {
                pw.println(r.toCSV());
            }
            
        } catch (IOException e) {
            System.err.println("고장 신고 업데이트 중 오류 발생: " + e.getMessage());
        }
    }
    
    public static List<RepairReport> getAllRepairReports() {
        List<RepairReport> reports = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(REPAIR_REPORTS_FILE))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                RepairReport report = RepairReport.fromCSV(line);
                if (report != null) {
                    reports.add(report);
                }
            }
        } catch (IOException e) {
            // 파일이 없는 경우는 정상
        }
        
        return reports;
    }
    
    public static List<RepairReport> getPendingReports() {
        List<RepairReport> pendingReports = new ArrayList<>();
        List<RepairReport> allReports = getAllRepairReports();
        
        for (RepairReport report : allReports) {
            if ("PENDING".equals(report.status)) {
                pendingReports.add(report);
            }
        }
        
        return pendingReports;
    }
    
    public static RepairReport getRepairReport(String reportId) {
        List<RepairReport> reports = getAllRepairReports();
        
        for (RepairReport report : reports) {
            if (report.reportId.equals(reportId)) {
                return report;
            }
        }
        
        return null;
    }
    
    // 대여 기록 관련 메소드들
    public static void saveRentalHistory(RentalHistory history) {
        try (FileWriter fw = new FileWriter(RENTAL_HISTORY_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            File file = new File(RENTAL_HISTORY_FILE);
            if (file.length() == 0) {
                pw.println("rentalId,userId,bicycleId,startTime,endTime,startLocation,endLocation,usageTimeMinutes,distanceKm,price,paymentStatus");
            }
            
            pw.println(history.toCSV());
            
        } catch (IOException e) {
            System.err.println("대여 기록 저장 중 오류 발생: " + e.getMessage());
        }
    }
    
    public static void updateRentalHistory(RentalHistory history) {
        List<RentalHistory> histories = getAllRentalHistories();
        
        for (int i = 0; i < histories.size(); i++) {
            if (histories.get(i).rentalId.equals(history.rentalId)) {
                histories.set(i, history);
                break;
            }
        }
        
        // 전체 파일 다시 쓰기
        try (FileWriter fw = new FileWriter(RENTAL_HISTORY_FILE);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("rentalId,userId,bicycleId,startTime,endTime,startLocation,endLocation,usageTimeMinutes,distanceKm,price,paymentStatus");
            
            for (RentalHistory h : histories) {
                pw.println(h.toCSV());
            }
            
        } catch (IOException e) {
            System.err.println("대여 기록 업데이트 중 오류 발생: " + e.getMessage());
        }
    }
    
    public static List<RentalHistory> getAllRentalHistories() {
        List<RentalHistory> histories = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(RENTAL_HISTORY_FILE))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                RentalHistory history = RentalHistory.fromCSV(line);
                if (history != null) {
                    histories.add(history);
                }
            }
        } catch (IOException e) {
            // 파일이 없는 경우는 정상
        }
        
        return histories;
    }
    
    public static List<RentalHistory> getUserRentalHistory(String userId) {
        List<RentalHistory> userHistories = new ArrayList<>();
        List<RentalHistory> allHistories = getAllRentalHistories();
        
        for (RentalHistory history : allHistories) {
            if (history.userId.equals(userId)) {
                userHistories.add(history);
            }
        }
        
        return userHistories;
    }
    
    public static String generateReportId() {
        return "RPT" + String.format("%06d", (int)(Math.random() * 1000000));
    }
    
    public static String generateRentalId() {
        return "RNT" + String.format("%06d", (int)(Math.random() * 1000000));
    }
}