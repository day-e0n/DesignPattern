package factory;

/**
 * 일반 자전거 팩토리
 */
public class RegularBicycleFactory extends BicycleFactory {
    
    @Override
    public Bicycle createBicycle(String bicycleId) {
        System.out.println("일반 자전거를 생성합니다: " + bicycleId);
        return new RegularBicycle(bicycleId);
    }
}