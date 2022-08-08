package com.lj.gulimail.search.service;

import com.lj.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ElasticSearchService {
    boolean upSkuStatus(List<SkuEsModel> skuEsModels) throws IOException;
}
