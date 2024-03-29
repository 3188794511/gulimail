package com.lj.gulimail.product.feign;

import com.lj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimail-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasStock")
    R hasStock(@RequestBody List<Long> skuIds);
}
