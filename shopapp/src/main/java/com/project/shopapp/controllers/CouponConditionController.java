package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CouponConditionDTO;
import com.project.shopapp.models.Coupon;
import com.project.shopapp.models.CouponCondition;
import com.project.shopapp.repositories.CouponConditionRepository;
import com.project.shopapp.repositories.CouponRepository;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.Coupon.CouponConditionService;
import com.project.shopapp.services.Coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/coupon-conditions")
@RequiredArgsConstructor
public class CouponConditionController {
    private final CouponConditionService couponConditionService;
    private final CouponService couponService;
    private final CouponConditionRepository couponConditionRepository;
    private final CouponRepository couponRepository;


    @GetMapping("/{conditionId}")
    public ResponseEntity<CouponCondition> getCouponConditionById(@PathVariable Long conditionId) {
        CouponCondition couponCondition = couponConditionService.findCouponConditionById(conditionId);
        if (couponCondition == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(couponCondition);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCouponCondition(@PathVariable("id") Long id, @RequestBody CouponCondition updatedCouponCondition) {
        return couponConditionRepository.findById(id)
                .map(couponCondition -> {
                    couponCondition.setAttribute(updatedCouponCondition.getAttribute());
                    couponCondition.setOperator(updatedCouponCondition.getOperator());
                    couponCondition.setValue(updatedCouponCondition.getValue());
                    couponCondition.setDiscountAmount(updatedCouponCondition.getDiscountAmount());
                    CouponCondition savedCouponCondition = couponConditionRepository.save(couponCondition);
                    return ResponseEntity.ok(savedCouponCondition);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<?> addCouponCondition(@RequestBody CouponCondition newCouponCondition) {
        Coupon coupon = couponRepository.findById(newCouponCondition.getCoupon().getId()).orElse(null);
        if (coupon == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Coupon not found");
        }

        newCouponCondition.setCoupon(coupon);
        CouponCondition savedCouponCondition = couponConditionRepository.save(newCouponCondition);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCouponCondition);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCouponCondition(@PathVariable("id") Long id) {
        if (couponConditionRepository.existsById(id)) {
            couponConditionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CouponCondition not found");
        }
    }
}

