package strategy;

import java.math.BigDecimal;

/**
 * 요금 계산 컨텍스트 클래스
 */
public class PricingContext {
    private PricingStrategy strategy;
    
    public PricingContext(PricingStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }
    
    public BigDecimal calculatePrice(double usageTime, double distance) {
        return strategy.calculatePrice(usageTime, distance);
    }
    
    public String getCurrentStrategyName() {
        return strategy.getStrategyName();
    }
}