package com.lj.gulimail.product.feign;

import com.lj.common.to.es.SkuEsModel;
import com.lj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimail-search")
public interface SearchFeignService {
    @PostMapping("/search/upSkuStatus")
    R upSkuStatus(@RequestBody List<SkuEsModel> skuEsModels);
}
