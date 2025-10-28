package strategy;

import java.math.BigDecimal;

/**
 * 학생 할인 요금 계산 전략
 */
public class StudentPricingStrategy implements PricingStrategy {
    private final BigDecimal basePrice = new BigDecimal("1000");  // 학생 기본 요금 1000원
    private final BigDecimal timeRate = new BigDecimal("50");     // 분당 50원 (50% 할인)
    
    @Override
    public BigDecimal calculatePrice(double usageTime, double distance) {
        BigDecimal timeCost = BigDecimal.valueOf(usageTime).multiply(timeRate);
        return basePrice.add(timeCost);
    }
    
    @Override
    public String getStrategyName() {
        return "학생 할인 요금제";
    }
}