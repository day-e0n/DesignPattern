package strategy;

import java.math.BigDecimal;

/**
 * 프리미엄 사용자 요금 계산 전략
 */
public class PremiumUserPricingStrategy implements PricingStrategy {
    private final BigDecimal monthlyFee = new BigDecimal("20000");  // 월 정액 20000원
    private final double freeTime = 60.0;  // 60분 무료
    private final BigDecimal timeRate = new BigDecimal("150");      // 추가 분당 150원
    
    @Override
    public BigDecimal calculatePrice(double usageTime, double distance) {
        if (usageTime <= freeTime) {
            return BigDecimal.ZERO;
        }
        
        double extraTime = usageTime - freeTime;
        BigDecimal extraCost = BigDecimal.valueOf(extraTime).multiply(timeRate);
        return extraCost;
    }
    
    @Override
    public String getStrategyName() {
        return "프리미엄 사용자 요금제";
    }
}