package observer;

/**
 * 옵저버 인터페이스 (옵저버 패턴)
 */
public interface Observer {
    /**
     * 자전거 상태 변화 알림 받기
     * @param bicycleId 자전거 ID
     * @param event 발생한 이벤트
     * @param message 알림 메시지
     */
    void update(String bicycleId, String event, String message);
}