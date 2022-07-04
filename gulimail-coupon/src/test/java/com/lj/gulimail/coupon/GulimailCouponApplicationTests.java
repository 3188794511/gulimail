package com.lj.gulimail.coupon;

import com.lj.gulimail.coupon.service.SmsCouponService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
class GulimailCouponApplicationTests {
    @Resource
    private SmsCouponService smsCouponService;

    @Test
    void testCouponService() {
        smsCouponService.getById(1L);
    }

}
