package com.project.shopapp.services.Coupon;

import com.project.shopapp.models.CouponCondition;
import com.project.shopapp.repositories.CouponConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponConditionService {
    private final CouponConditionRepository couponConditionRepository;

    public Page<CouponCondition> findAllCouponConditions(Pageable pageable) {
        return couponConditionRepository.findAll(pageable);
    }

    public void addCouponCondition(CouponCondition couponCondition) {
        couponConditionRepository.save(couponCondition);
    }

    public void updateCouponCondition(CouponCondition couponCondition) {
        couponConditionRepository.save(couponCondition);
    }

    public void deleteCouponCondition(Long conditionId) {
        couponConditionRepository.deleteById(conditionId);
    }

    public CouponCondition findCouponConditionById(Long id) {
        return couponConditionRepository.findById(id).orElse(null);
    }
}

