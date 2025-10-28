package strategy;

import java.math.BigDecimal;

/**
 * 전기 자전거 월정액 요금 전략
 */
public class ElectricMonthlyPricingStrategy implements PricingStrategy {
    private final BigDecimal monthlyFee = new BigDecimal("25000");  // 월 정액 25000원
    private final BigDecimal overageFee = new BigDecimal("150");    // 초과 시 분당 150원
    private final double freeTimePerDay = 45.0; // 하루 45분 무료
    
    @Override
    public BigDecimal calculatePrice(double usageTime, double distance) {
        // 월정액 사용자는 하루 45분까지 무료
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
        return "전기 자전거 월정액 (25,000원/월)";
    }
    
    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }
}