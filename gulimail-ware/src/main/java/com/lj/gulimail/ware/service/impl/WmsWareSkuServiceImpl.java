package com.lj.gulimail.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.to.es.SkuWareVo;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.common.utils.R;
import com.lj.gulimail.ware.dao.WmsWareSkuDao;
import com.lj.gulimail.ware.entity.WmsPurchaseDetailEntity;
import com.lj.gulimail.ware.entity.WmsWareSkuEntity;
import com.lj.gulimail.ware.feign.ProductFeignService;
import com.lj.gulimail.ware.service.WmsWareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wmsWareSkuService")
@Slf4j
public class WmsWareSkuServiceImpl extends ServiceImpl<WmsWareSkuDao, WmsWareSkuEntity> implements WmsWareSkuService {
    @Autowired
    private ProductFeignService productFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WmsWareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        IPage<WmsWareSkuEntity> page = this.page(
                new Query<WmsWareSkuEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 新增或修改商品库存
     * @param detailEntity
     */
    @Transactional
    public void addOrUpdateStock(WmsPurchaseDetailEntity detailEntity) {
        Long skuId = detailEntity.getSkuId();
        Integer skuNum = detailEntity.getSkuNum();
        Long wareId = detailEntity.getWareId();
        //先查询该商品库存是否已经存在 ,存在,修改库存商品数量,不存在,创建库存
        List<WmsWareSkuEntity> wmsWareSkuEntities = this.list(new QueryWrapper<WmsWareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (wmsWareSkuEntities == null || wmsWareSkuEntities.isEmpty()){
            //新增
            try {
                R info = productFeignService.info(skuId);
                Map<String,Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                log.info("查询到的商品信息:{}",skuInfo);
                String skuName = (String) skuInfo.get("skuName");
                getBaseMapper().addStock(skuId,skuNum,wareId,skuName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            //修改
            getBaseMapper().updateStock(skuId,skuNum,wareId);
        }

    }

    /**
     * 根据商品id判断库存是否为空
     * @param skuIds
     * @return
     */
    public List<SkuWareVo> hasStockBySkuIds(List<Long> skuIds) {
        List<SkuWareVo> skuWareVos = skuIds.stream().map(id -> {
            Integer stock = this.getBaseMapper().selectStockBySkuId(id);
            SkuWareVo skuWareVo = new SkuWareVo();
            skuWareVo.setSkuId(id);
            skuWareVo.setHasStock(stock == null ? false : stock > 0);
            return skuWareVo;
        }).collect(Collectors.toList());
        return skuWareVos;
    }

}