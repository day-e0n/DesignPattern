package factory;

/**
 * 전기 자전거 팩토리
 */
public class ElectricBicycleFactory extends BicycleFactory {
    
    @Override
    public Bicycle createBicycle(String bicycleId) {
        System.out.println("전기 자전거를 생성합니다: " + bicycleId);
        return new ElectricBicycle(bicycleId);
    }
}