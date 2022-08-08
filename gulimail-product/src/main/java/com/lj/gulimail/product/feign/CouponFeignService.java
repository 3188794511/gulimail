package com.lj.gulimail.product.feign;

import com.lj.common.to.BoundsTo;
import com.lj.common.to.SkuFullReductionTo;
import com.lj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimail-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/smsspubounds/save")
    R saveBounds(@RequestBody BoundsTo boundsTo);

    @RequestMapping("/coupon/smsskufullreduction/save")
    R saveSkuFullReduction(@RequestBody SkuFullReductionTo skuFullReductionTo);




}
