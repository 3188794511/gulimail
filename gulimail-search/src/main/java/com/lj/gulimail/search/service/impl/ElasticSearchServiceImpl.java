package com.lj.gulimail.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.lj.common.constant.EsConstant;
import com.lj.common.to.es.SkuEsModel;
import com.lj.gulimail.search.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ElasticSearchServiceImpl implements ElasticSearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    /**
     * 将商品数据存入es
     * @param skuEsModels
     * @return
     */
    public boolean upSkuStatus(List<SkuEsModel> skuEsModels) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            //将每个商品的信息存入indexRequest中
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id( skuEsModel.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(skuEsModel), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        //发送请求
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        boolean hasFailures = bulk.hasFailures();//执行是否成功
        if (hasFailures == true){
            //执行出错
            List<Integer> ids = Arrays.stream(bulk.getItems()).map(item -> item.getItemId()).collect(Collectors.toList());
            log.error("执行upSkuStatus方法出错,发送错误的商品id为{}",ids);
            return false;
        }
        return true;
    }
}
