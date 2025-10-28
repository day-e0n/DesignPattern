package strategy;

import java.math.BigDecimal;

/**
 * 일반 자전거 월정액 요금 전략
 */
public class RegularMonthlyPricingStrategy implements PricingStrategy {
    private final BigDecimal monthlyFee = new BigDecimal("15000");  // 월 정액 15000원
    private final BigDecimal overageFee = new BigDecimal("100");    // 초과 시 분당 100원
    private final double freeTimePerDay = 60.0; // 하루 60분 무료
    
    @Override
    public BigDecimal calculatePrice(double usageTime, double distance) {
        // 월정액 사용자는 하루 60분까지 무료
        if (usageTime <= freeTimePerDay) {
            return BigDecimal.ZERO;
        }
        
        // 초과 시간에 대해서만 요금 부과
        double overageTime = usageTime - freeTimePerDay;
        BigDecimal overageCost = BigDecimal.valueOf(overageTime).multiply(overageFee);
        
        return overageCost;
    }
    
    @Override
    public String getStrategyName() {
        return "일반 자전거 월정액 (15,000원/월)";
    }
    
    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }
}