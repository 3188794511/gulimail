package com.lj.gulimail.member.feign;

import com.lj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "gulimail-coupon")
public interface CouponFeignService {

    @RequestMapping("coupon/coupon/getByMemberId")
    R getByMemberId();
}
