package decorator;

import factory.Bicycle;

/**
 * 자전거 데코레이터 추상 클래스 (데코레이터 패턴)
 */
public abstract class BicycleDecorator extends Bicycle {
    protected Bicycle bicycle;
    
    public BicycleDecorator(Bicycle bicycle) {
        super(bicycle.getBicycleId());
        this.bicycle = bicycle;
        this.bicycleType = bicycle.getBicycleType();
        this.use = bicycle.isUse();
        this.speed = bicycle.getSpeed();
    }
    
    @Override
    public void lock() {
        bicycle.lock();
    }
    
    @Override
    public void unlock() {
        bicycle.unlock();
    }
    
    @Override
    public void markBroken() {
        bicycle.markBroken();
    }
    
    @Override
    public boolean isAvailable() {
        return bicycle.isAvailable();
    }
    
    @Override
    public void updateLocation(double latitude, double longitude) {
        bicycle.updateLocation(latitude, longitude);
    }
    
    @Override
    public String toString() {
        return bicycle.toString();
    }
}