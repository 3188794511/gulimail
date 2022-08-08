package com.lj.gulimail.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.constant.ProductConstant;
import com.lj.common.to.BoundsTo;
import com.lj.common.to.SkuFullReductionTo;
import com.lj.common.to.es.SkuEsModel;
import com.lj.common.to.es.SkuWareVo;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.common.utils.R;
import com.lj.gulimail.product.dao.SpuInfoDao;
import com.lj.gulimail.product.entity.*;
import com.lj.gulimail.product.feign.CouponFeignService;
import com.lj.gulimail.product.feign.SearchFeignService;
import com.lj.gulimail.product.feign.WareFeignService;
import com.lj.gulimail.product.service.*;
import com.lj.gulimail.product.vo.SpuSaveVo;
import com.lj.gulimail.product.vo.basevo.Attr;
import com.lj.gulimail.product.vo.basevo.BaseAttrs;
import com.lj.gulimail.product.vo.basevo.Bounds;
import com.lj.gulimail.product.vo.basevo.Skus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 保存商品的所有相关信息
     * @param spuSaveVo
     */
    @Transactional
    public void saveSpuSaveVo(SpuSaveVo spuSaveVo) {
        //1.保存spu的基本信息   pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);
        //2.添加商品的描述信息  pms_spu_info_desc
        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.save(spuInfoDescEntity);
        //3.保存spu的图片集 pms_spu_images
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);
        //4.保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        productAttrValueService.saveBatchBaseAttrs(spuInfoEntity.getId(),baseAttrs);
        //5.保存sku相关信息
        List<Skus> skus = spuSaveVo.getSkus();
        skus.forEach(sku -> {
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            sku.getImages().forEach(image -> {
                if (image.getDefaultImg() == 1){
                    skuInfoEntity.setSkuDefaultImg(image.getImgUrl());//设置默认图片
                }
            }) ;
            //保存sku的基本数据 pms_sku_info
            BeanUtils.copyProperties(sku,skuInfoEntity);
            skuInfoEntity.setSpuId(spuInfoEntity.getId());
            skuInfoEntity.setSaleCount(0L);
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoService.save(skuInfoEntity);
            //保存sku的销售属性  pms_sku_sale_attr_value
            List<Attr> attr = sku.getAttr();
            skuSaleAttrValueService.saveBatchSaleAttrs(skuInfoEntity.getSkuId(),attr);
            //保存sku的图片集 pms_sku_images
            skuImagesService.saveBatchImages(skuInfoEntity.getSkuId(),sku.getImages());
            //保存满减 sms_sku_full_reduction  打折等相关数据 sms_sku_ladder
            SkuFullReductionTo skuFullReductionTo = new SkuFullReductionTo();
            BeanUtils.copyProperties(sku,skuFullReductionTo);
            skuFullReductionTo.setSkuId(skuInfoEntity.getSkuId());
            R r = couponFeignService.saveSkuFullReduction(skuFullReductionTo);
            if (r.getCode() != 0){
                log.error("gulimail-coupon远程saveSkuFullReduction服务调用失败");
            }
        });

        //6.保存积分成长 购物积分相关信息
        Bounds bounds = spuSaveVo.getBounds();
        BoundsTo boundsTo = new BoundsTo();
        boundsTo.setSpuId(spuInfoEntity.getId());
        BeanUtils.copyProperties(bounds, boundsTo);
        R r = couponFeignService.saveBounds(boundsTo);
        if (r.getCode() != 0){
            log.error("gulimail-coupon远程saveBounds服务调用失败");
        }


    }

    /**
     * 条件查询
     * @param params
     * @return
     */
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.eq("id",key).or().like("spu_name", key);
            });
        }
        if (!StringUtils.isEmpty(params.get("catalogId")) && !"0".equals(params.get("catalogId"))){
            wrapper.eq("catalog_id", params.get("catalogId"));
        }
        if (!StringUtils.isEmpty(params.get("brandId")) && !"0".equals(params.get("brandId"))){
            wrapper.eq("brand_id",params.get("brandId"));
        }
        if(!StringUtils.isEmpty(params.get("status"))){
            wrapper.eq("publish_status", params.get("status"));
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 商品上架
     * @param spuId
     */
    public void up(Long spuId) {
        //查询出spuId下的所有sku
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        List<Long> skuIds  = skuInfoEntities.stream().map(skuInfo -> skuInfo.getSkuId()).collect(Collectors.toList());
        //查询商品的规格参数
        List<ProductAttrValueEntity> attrEntities = productAttrValueService.getBaseAttrs(spuId);
        //过滤出能快速展示的规格参数
        List<SkuEsModel.Attrs> attrsList = attrEntities.stream()
                .filter(attr -> attr.getQuickShow() != 0)
                .map(t -> {
                    SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(t, attrs);
                    return attrs;
                }).collect(Collectors.toList());
        //查询商品库存
        Map<Long, Boolean> stockDataMap = null;
        try{
            //远程调用ware的微服务查询商品库存
            R hasStock = wareFeignService.hasStock(skuIds);
            List<SkuWareVo> stockData = hasStock.getData(new TypeReference<List<SkuWareVo>>(){});
            stockDataMap = stockData.stream()
                    .collect(Collectors.toMap(SkuWareVo::getSkuId, item -> item.getHasStock()));
        }catch (Exception e){
            log.error("调用库存服务失败,原因{}",e);
        }

        Map<Long, Boolean> finalStockDataMap = stockDataMap;
        List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map(sku -> {
            //将sku的基本属性填充到es领域模型
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            //添加spu对应的brand信息和category信息
            BrandEntity brand = brandService.getById(skuEsModel.getBrandId());
            CategoryEntity category = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());
            skuEsModel.setCatalogName(category.getName());
            //添加规格参数属性
            skuEsModel.setAttrs(attrsList);
            //添加商品热度  0
            skuEsModel.setHotScore(0L);
            //添加库存信息
            if (finalStockDataMap == null){
                //服务调用失败,默认库存充足
                skuEsModel.setHasStock(false);
            }else{
                //服务调用成功
                skuEsModel.setHasStock(finalStockDataMap.get(skuEsModel.getSkuId()));
            }
            return skuEsModel;
        }).collect(Collectors.toList());

        //商品上架,将商品数据存入es
        try {
            R r = searchFeignService.upSkuStatus(skuEsModels);
            if (r.getCode() == 0){
                //修改商品的状态
                this.getBaseMapper().updateStatusById(spuId, ProductConstant.ProductState.PUBLISH.getType());
            }
        } catch (Exception e) {
            log.error("调用search服务失败,原因:{]",e);
        }
    }

}