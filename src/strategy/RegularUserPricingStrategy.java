package strategy;

import java.math.BigDecimal;

/**
 * 일반 사용자 요금 계산 전략
 */
public class RegularUserPricingStrategy implements PricingStrategy {
    private final BigDecimal basePrice = new BigDecimal("2000");  // 기본 요금 2000원
    private final BigDecimal timeRate = new BigDecimal("100");    // 분당 100원
    
    @Override
    public BigDecimal calculatePrice(double usageTime, double distance) {
        BigDecimal timeCost = BigDecimal.valueOf(usageTime).multiply(timeRate);
        return basePrice.add(timeCost);
    }
    
    @Override
    public String getStrategyName() {
        return "일반 사용자 요금제";
    }
}