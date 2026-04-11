package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CouponDTO;
import com.project.shopapp.models.Coupon;
import com.project.shopapp.models.CouponCondition;
import com.project.shopapp.responses.CouponCalculationResponse;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.Coupon.CouponConditionService;
import com.project.shopapp.services.Coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
    private final CouponConditionService couponConditionService;

    @GetMapping("/calculate")
    public ResponseEntity<ResponseObject> calculateCouponValue(
            @RequestParam("couponCode") String couponCode,
            @RequestParam("totalAmount") double totalAmount
    ){
        double finalAmount = couponService.calculateCouponValue(couponCode, totalAmount);
        CouponCalculationResponse couponCalculationResponse = CouponCalculationResponse.builder()
                .result(finalAmount)
                .build();
        return ResponseEntity.ok(new ResponseObject(
                "Calculate coupn successfully",
                HttpStatus.OK,
                couponCalculationResponse
        ));
    }

    @GetMapping("")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok().body(coupons);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseObject> getAllCouponConditionsAndCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<CouponCondition> couponConditions = couponConditionService.findAllCouponConditions(pageable);
        Page<Coupon> coupons = couponService.findAllCoupons(pageable);

        List<CouponDTO> couponDTOS = couponConditions.stream()
                .map(condition -> {
                    Coupon coupon = condition.getCoupon();
                    return new CouponDTO(
                            condition.getId(),
                            condition.getAttribute(),
                            condition.getOperator(),
                            condition.getValue(),
                            condition.getDiscountAmount(),
                            coupon.getCode(),
                            coupon.isActive()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get all coupon conditions and coupons successfully")
                .status(HttpStatus.OK)
                .data(couponDTOS)
                .build());
    }

    @PostMapping("")
    public ResponseEntity<Coupon> addCoupon(@RequestBody Coupon coupon) {
        Coupon savedCoupon = couponService.addCoupon(coupon);
        return ResponseEntity.ok(savedCoupon);
    }

}
