package com.lj.gulimail.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.constant.WareConstant;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.ware.dao.WmsPurchaseDetailDao;
import com.lj.gulimail.ware.entity.WmsPurchaseDetailEntity;
import com.lj.gulimail.ware.service.WmsPurchaseDetailService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wmsPurchaseDetailService")
public class WmsPurchaseDetailServiceImpl extends ServiceImpl<WmsPurchaseDetailDao, WmsPurchaseDetailEntity> implements WmsPurchaseDetailService {

    /**
     * 条件查询
     * @param params
     * @return
     */
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WmsPurchaseDetailEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.eq("purchase_id", key).or().eq("sku_id",key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        IPage<WmsPurchaseDetailEntity> page = this.page(
                new Query<WmsPurchaseDetailEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 根据采购单id修改采购项状态
     * @param purchaseId
     */
    public void updateByPurchaseId(Long purchaseId) {
        List<WmsPurchaseDetailEntity> purchaseDetailEntities = this.list(new QueryWrapper<WmsPurchaseDetailEntity>().eq("purchase_id", purchaseId));
        List<WmsPurchaseDetailEntity> collect = purchaseDetailEntities.stream().map(p -> {
            WmsPurchaseDetailEntity wmsPurchaseDetailEntity = new WmsPurchaseDetailEntity();
            wmsPurchaseDetailEntity.setId(p.getId());
            wmsPurchaseDetailEntity.setStatus(WareConstant.UnReceivePurchase.PURCHASED.getType());
            return wmsPurchaseDetailEntity;
        }).collect(Collectors.toList());
        this.updateBatchById(collect);
    }

}