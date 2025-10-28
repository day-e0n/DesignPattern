package strategy;

import java.math.BigDecimal;

/**
 * 전략 패턴 - 요금 계산 전략 인터페이스
 */
public interface PricingStrategy {
    /**
     * 사용 시간과 거리를 기반으로 요금 계산
     * @param usageTime 사용 시간 (분)
     * @param distance 이동 거리 (km)
     * @return 계산된 요금
     */
    BigDecimal calculatePrice(double usageTime, double distance);
    
    /**
     * 전략 이름 반환
     * @return 전략 이름
     */
    String getStrategyName();
}