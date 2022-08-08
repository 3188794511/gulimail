package com.lj.gulimail.search.controller;

import com.lj.common.constant.ResultConstant;
import com.lj.common.to.es.SkuEsModel;
import com.lj.common.utils.R;
import com.lj.gulimail.search.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/search")
@Slf4j
public class ElasticSearchController {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @PostMapping("/upSkuStatus")
    public R upSkuStatus(@RequestBody List<SkuEsModel> skuEsModels){
        boolean res = false;
        try {
            res = elasticSearchService.upSkuStatus(skuEsModels);
        } catch (IOException e) {
            log.error("ElasticSearchController执行upSkuStatus出错了,原因:{}",e);
            return R.error(ResultConstant.Message.UP_FAIL.getCode(),ResultConstant.Message.UP_FAIL.getMsg());
        }
        if (res == true){
            return R.ok();
        }else {
            return R.error(ResultConstant.Message.UP_FAIL.getCode(),ResultConstant.Message.UP_FAIL.getMsg());
        }
    }
}
