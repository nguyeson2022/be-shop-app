package com.project.shopapp.services.Coupon;

import com.project.shopapp.models.Coupon;
import com.project.shopapp.models.CouponCondition;
import com.project.shopapp.repositories.CouponConditionRepository;
import com.project.shopapp.repositories.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;

/**
 * CouponService class
 */
@RequiredArgsConstructor
@Service
public class CouponService implements ICouponService {
    private final CouponRepository couponRepository;
    private final CouponConditionRepository couponConditionRepository;

    @Override
    public double calculateCouponValue(String couponCode, double totalAmount) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        if (!coupon.isActive()) {
            throw new IllegalArgumentException("Coupon is not active");
        }
        double discount = calculateDiscount(coupon, totalAmount);
        double finalAmount = totalAmount - discount;
        return finalAmount;
    }

    private double calculateDiscount(Coupon coupon, double totalAmount) {
        List<CouponCondition> conditions = couponConditionRepository
                .findByCouponId(coupon.getId());
        double discount = 0.0;
        double updatedTotalAmount = totalAmount;

        for (CouponCondition condition : conditions) {
            String attribute = condition.getAttribute();
            String operator = condition.getOperator();
            String value = condition.getValue();
            double percentDiscount = Double.valueOf(String.valueOf(condition.getDiscountAmount()));

            if (attribute.equals("amount")) {
                double amountValue = Double.parseDouble(value);
                switch (operator) {
                    case ">":
                        if (updatedTotalAmount > amountValue) {
                            discount += updatedTotalAmount * percentDiscount / 100;
                        }
                        break;
                    case ">=":
                        if (updatedTotalAmount >= amountValue) {
                            discount += updatedTotalAmount * percentDiscount / 100;
                        }
                        break;
                    case "<":
                        if (updatedTotalAmount < amountValue) {
                            discount += updatedTotalAmount * percentDiscount / 100;
                        }
                        break;
                    case "<=":
                        if (updatedTotalAmount <= amountValue) {
                            discount += updatedTotalAmount * percentDiscount / 100;
                        }
                        break;
                    case "=":
                        if (updatedTotalAmount == amountValue) {
                            discount += updatedTotalAmount * percentDiscount / 100;
                        }
                        break;
                }
            } else if (attribute.equals("date")) {
                LocalDate applicableDate = LocalDate.parse(value);
                LocalDate currentDate = LocalDate.now();

                switch (operator) {
                    case ">":
                        if (currentDate.isAfter(applicableDate)) {
                            discount += updatedTotalAmount * percentDiscount / 100;
                        }
                        break;
                    case ">=":
                        if (!currentDate.isBefore(applicableDate)) {
                            discount += updatedTotalAmount * percentDiscount / 100;
                        }
                        break;
                    case "<":
                        if (currentDate.isBefore(applicableDate)) {
                            discount += updatedTotalAmount * percentDiscount / 100;
                        }
                        break;
                    case "<=":
                        if (!currentDate.isAfter(applicableDate)) {
                            discount += updatedTotalAmount * percentDiscount / 100;
                        }
                        break;
                    case "=":
                        if (currentDate.isEqual(applicableDate)) {
                            discount += updatedTotalAmount * percentDiscount / 100;
                        }
                        break;
                }
            }
            updatedTotalAmount = updatedTotalAmount - discount;
        }
        return discount;
    }

    public Page<Coupon> findAllCoupons(Pageable pageable) {
        return couponRepository.findAll(pageable);
    }

    public Coupon findCouponById(Long id) {
        return couponRepository.findById(id).orElse(null);
    }

    public List<Coupon> getAllCoupons(){
        return couponRepository.findAll();
    }

    public Coupon addCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }
}