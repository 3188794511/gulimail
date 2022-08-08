package com.lj.gulimail.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.constant.WareConstant;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.ware.dao.WmsPurchaseDao;
import com.lj.gulimail.ware.entity.WmsPurchaseDetailEntity;
import com.lj.gulimail.ware.entity.WmsPurchaseEntity;
import com.lj.gulimail.ware.service.WmsPurchaseDetailService;
import com.lj.gulimail.ware.service.WmsPurchaseService;
import com.lj.gulimail.ware.service.WmsWareSkuService;
import com.lj.gulimail.ware.vo.MergeVo;
import com.lj.gulimail.ware.vo.PurchaseDoneVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wmsPurchaseService")
@Slf4j
public class WmsPurchaseServiceImpl extends ServiceImpl<WmsPurchaseDao, WmsPurchaseEntity> implements WmsPurchaseService {
    @Autowired
    private WmsPurchaseDetailService purchaseDetailService;
    @Autowired
    private WmsWareSkuService skuService;

    /**
     * 条件查询
     * @param params
     * @return
     */
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WmsPurchaseEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.eq("id",key).or().like("assignee_name",key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        }
        IPage<WmsPurchaseEntity> page = this.page(
                new Query<WmsPurchaseEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 查询未分配或新建的采购信息
     * @return
     */
    public List<WmsPurchaseEntity> queryUnReceive() {
        QueryWrapper<WmsPurchaseEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", WareConstant.UnReceivePurchase.CREATED.getType()).or()
                .eq("status",WareConstant.UnReceivePurchase.ASSIGNED.getType());
        List<WmsPurchaseEntity> purchaseEntities = this.list(wrapper);
        return purchaseEntities;
    }

    /**
     * 合并采购项
     * @param mergeVo
     */
    @Transactional
    public void merge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        //若采购purchaseId为空,创建新的采购单
        if (purchaseId == null){
            WmsPurchaseEntity purchaseEntity = new WmsPurchaseEntity();
            purchaseEntity.setStatus(WareConstant.UnReceivePurchase.CREATED.getType());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        //将采购项关联到对应的采购单中
        WmsPurchaseEntity purchaseEntity = this.getById(purchaseId);
        if (!(purchaseEntity.getStatus() == WareConstant.UnReceivePurchase.CREATED.getType() || purchaseEntity.getStatus() == WareConstant.UnReceivePurchase.ASSIGNED.getType())){
            return;
        }
        List<WmsPurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.listByIds(mergeVo.getItems());
        Long finalPurchaseId = purchaseId;
        List<WmsPurchaseDetailEntity> detailEntities = purchaseDetailEntities.stream().map(p -> {
            WmsPurchaseDetailEntity purchaseDetailEntity = new WmsPurchaseDetailEntity();
            purchaseDetailEntity.setId(p.getId());
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.UnReceivePurchase.ASSIGNED.getType());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(detailEntities);
        //更新采购单数据 时间
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    /**
     * 改变采购状态  -> 正在采购
     * @param ids
     */
    @Transactional
    public void received(List<Long> ids) {
        //判断采购单状态,过滤出符合条件的采购单集合
        List<WmsPurchaseEntity> collect = this.listByIds(ids).stream().filter(p -> {
            if (p.getStatus() == WareConstant.UnReceivePurchase.CREATED.getType() || p.getStatus() == WareConstant.UnReceivePurchase.ASSIGNED.getType()) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        if (collect == null || collect.isEmpty()){
            return;
        }
        //修改包含的采购项状态
        collect.stream().forEach(p -> {
            purchaseDetailService.updateByPurchaseId(p.getId());
        });
        //修改采购单状态
        List<WmsPurchaseEntity> updateData = collect.stream().map(c -> {
            WmsPurchaseEntity wmsPurchaseEntity = new WmsPurchaseEntity();
            wmsPurchaseEntity.setId(c.getId());
            wmsPurchaseEntity.setStatus(WareConstant.UnReceivePurchase.PURCHASED.getType());
            wmsPurchaseEntity.setUpdateTime(new Date());
            return wmsPurchaseEntity;
        }).collect(Collectors.toList());
        this.updateBatchById(updateData);
    }

    /**
     * 完成采购
     * @param purchaseDoneVo
     */
    public void done(PurchaseDoneVo purchaseDoneVo) {
        //修改包含的采购项状态
        List<Long> successCollect = purchaseDoneVo.getItems().stream()
                //采购成功的采购项集合
                .filter(p -> p.getStatus() == WareConstant.UnReceivePurchase.FINISHED.getType() ? true : false
                )
                .map(p -> p.getItemId())
                .collect(Collectors.toList());
        List<WmsPurchaseDetailEntity> successPurchaseDetails = successCollect.stream().map(s -> {
            WmsPurchaseDetailEntity entity = purchaseDetailService.getById(s);
            WmsPurchaseDetailEntity purchaseDetailEntity = new WmsPurchaseDetailEntity();
            purchaseDetailEntity.setId(entity.getId());
            purchaseDetailEntity.setStatus(WareConstant.UnReceivePurchase.FINISHED.getType());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        List<WmsPurchaseDetailEntity> falsePurchaseDetails = purchaseDoneVo.getItems().stream()
                //采购失败集合
                .filter(p -> successCollect.contains(p.getItemId()) ? false : true)
                .map(p -> {
                    WmsPurchaseDetailEntity entity = purchaseDetailService.getById(p.getItemId());
                    WmsPurchaseDetailEntity purchaseDetailEntity = new WmsPurchaseDetailEntity();
                    purchaseDetailEntity.setId(entity.getId());
                    purchaseDetailEntity.setStatus(p.getStatus());
                    return purchaseDetailEntity;
                })
                .collect(Collectors.toList());
        purchaseDetailService.updateBatchById(successPurchaseDetails);
        purchaseDetailService.updateBatchById(falsePurchaseDetails);

        //修改采购单状态
        WmsPurchaseEntity purchaseEntity = this.getById(purchaseDoneVo.getId());
        WmsPurchaseEntity updatePurchase = new WmsPurchaseEntity();
        updatePurchase.setId(purchaseEntity.getId());
        updatePurchase.setUpdateTime(new Date());
        if (successCollect.size() == purchaseDoneVo.getItems().size()){
            updatePurchase.setStatus(WareConstant.UnReceivePurchase.FINISHED.getType());
        }
        else{
            updatePurchase.setStatus(WareConstant.UnReceivePurchase.HASERROR.getType());
        }
        this.updateById(updatePurchase);
        //新增或修改商品库存
        successCollect.stream().forEach(c -> {
            WmsPurchaseDetailEntity detailEntity = purchaseDetailService.getById(c);
            skuService.addOrUpdateStock(detailEntity);
        });
    }

}