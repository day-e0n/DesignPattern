package decorator;

import factory.Bicycle;

/**
 * 스마트 잠금 기능 데코레이터
 */
public class SmartLockDecorator extends BicycleDecorator {
    private boolean smartLockEnabled;
    private String unlockCode;
    private int failedAttempts;
    private final int maxFailedAttempts = 3;
    
    public SmartLockDecorator(Bicycle bicycle) {
        super(bicycle);
        this.smartLockEnabled = true;
        this.unlockCode = generateUnlockCode();
        this.failedAttempts = 0;
    }
    
    @Override
    public void unlock() {
        if (smartLockEnabled) {
            System.out.println("[스마트 잠금] 잠금 해제 코드가 필요합니다.");
            System.out.println("현재 잠금 해제 코드: " + unlockCode);
        }
        super.unlock();
    }
    
    public boolean unlockWithCode(String inputCode) {
        if (!smartLockEnabled) {
            super.unlock();
            return true;
        }
        
        if (unlockCode.equals(inputCode)) {
            System.out.println("[스마트 잠금] 올바른 코드입니다. 잠금을 해제합니다.");
            super.unlock();
            failedAttempts = 0;
            return true;
        } else {
            failedAttempts++;
            System.out.println("[스마트 잠금] 잘못된 코드입니다. (" + failedAttempts + "/" + maxFailedAttempts + ")");
            
            if (failedAttempts >= maxFailedAttempts) {
                System.out.println("[스마트 잠금] 시도 횟수 초과. 자전거가 일시적으로 잠금되었습니다.");
                // 실제로는 관리자에게 알림을 보내는 등의 추가 보안 조치 필요
            }
            return false;
        }
    }
    
    @Override
    public void lock() {
        super.lock();
        if (smartLockEnabled) {
            this.unlockCode = generateUnlockCode();
            this.failedAttempts = 0;
            System.out.println("[스마트 잠금] 새로운 잠금 코드가 생성되었습니다: " + unlockCode);
        }
    }
    
    private String generateUnlockCode() {
        // 4자리 랜덤 코드 생성
        return String.format("%04d", (int)(Math.random() * 10000));
    }
    
    public void enableSmartLock() {
        this.smartLockEnabled = true;
        this.unlockCode = generateUnlockCode();
        this.failedAttempts = 0;
        System.out.println("[스마트 잠금] " + bicycleId + " 스마트 잠금이 활성화되었습니다.");
    }
    
    public void disableSmartLock() {
        this.smartLockEnabled = false;
        System.out.println("[스마트 잠금] " + bicycleId + " 스마트 잠금이 비활성화되었습니다.");
    }
    
    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.unlockCode = generateUnlockCode();
        System.out.println("[스마트 잠금] 실패 횟수가 초기화되고 새 코드가 생성되었습니다.");
    }
    
    @Override
    public String toString() {
        return super.toString() + " [스마트 잠금: " + (smartLockEnabled ? "ON" : "OFF") + "]";
    }
    
    // Getter 메소드들
    public boolean isSmartLockEnabled() { return smartLockEnabled; }
    public String getUnlockCode() { return unlockCode; }
    public int getFailedAttempts() { return failedAttempts; }
}