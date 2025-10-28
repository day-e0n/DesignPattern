package factory;

/**
 * 자전거 팩토리 추상 클래스 (팩토리 메소드 패턴)
 */
public abstract class BicycleFactory {
    
    /**
     * 자전거 생성 팩토리 메소드 (추상 메소드)
     * @param bicycleId 자전거 ID
     * @return 생성된 자전거 객체
     */
    public abstract Bicycle createBicycle(String bicycleId);
    
    /**
     * 자전거 생성 및 초기화 프로세스
     * @param bicycleId 자전거 ID
     * @param latitude 초기 위치 위도
     * @param longitude 초기 위치 경도
     * @return 생성되고 초기화된 자전거 객체
     */
    public Bicycle orderBicycle(String bicycleId, double latitude, double longitude) {
        Bicycle bicycle = createBicycle(bicycleId);
        
        // 공통 초기화 작업
        bicycle.updateLocation(latitude, longitude);
        
        System.out.println("새로운 " + bicycle.getBicycleType() + "가 생성되고 배치되었습니다.");
        System.out.println(bicycle.toString());
        
        return bicycle;
    }
}