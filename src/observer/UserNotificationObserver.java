package observer;

/**
 * 사용자 알림 옵저버
 */
public class UserNotificationObserver implements Observer {
    private String userId;
    private String phoneNumber;
    private String email;
    
    public UserNotificationObserver(String userId, String phoneNumber, String email) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
    
    @Override
    public void update(String bicycleId, String event, String message) {
        switch (event) {
            case "RENT":
                sendUserNotification("대여 확인", "자전거 " + bicycleId + " 대여가 완료되었습니다.");
                break;
            case "RETURN":
                sendUserNotification("반납 확인", "자전거 " + bicycleId + " 반납이 완료되었습니다. 이용해주셔서 감사합니다!");
                break;
            case "LOW_BATTERY":
                sendUserNotification("배터리 알림", "사용 중인 전기자전거의 " + message);
                break;
            case "MAINTENANCE":
                sendUserNotification("정비 안내", "자전거 " + bicycleId + "에 " + message + " 다른 자전거를 이용해주세요.");
                break;
            default:
                // 사용자에게는 필요한 정보만 전달
                break;
        }
    }
    
    private void sendUserNotification(String title, String content) {
        System.out.println("📱 [사용자 알림 - " + userId + "]");
        System.out.println("   제목: " + title);
        System.out.println("   내용: " + content);
        System.out.println("   SMS: " + phoneNumber);
        System.out.println("   이메일: " + email);
    }
    
    // Getter 메소드들
    public String getUserId() { return userId; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
}