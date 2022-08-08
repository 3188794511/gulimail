package com.lj.gulimail.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.to.MemberPrice;
import com.lj.common.to.SkuFullReductionTo;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.coupon.dao.SmsSkuFullReductionDao;
import com.lj.gulimail.coupon.entity.SmsMemberPriceEntity;
import com.lj.gulimail.coupon.entity.SmsSkuFullReductionEntity;
import com.lj.gulimail.coupon.entity.SmsSkuLadderEntity;
import com.lj.gulimail.coupon.service.SmsMemberPriceService;
import com.lj.gulimail.coupon.service.SmsSkuFullReductionService;
import com.lj.gulimail.coupon.service.SmsSkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("smsSkuFullReductionService")
public class SmsSkuFullReductionServiceImpl extends ServiceImpl<SmsSkuFullReductionDao, SmsSkuFullReductionEntity> implements SmsSkuFullReductionService {

    @Autowired
    private SmsSkuLadderService smsSkuLadderService;
    @Autowired
    private SmsMemberPriceService smsMemberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SmsSkuFullReductionEntity> page = this.page(
                new Query<SmsSkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SmsSkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存所有优惠相关数据
     * @param skuFullReductionTo
     */
    @Transactional
    public void saveSkuFullReduction(SkuFullReductionTo skuFullReductionTo) {
        //保存满减信息
        if(skuFullReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1 && skuFullReductionTo.getReducePrice().compareTo(new BigDecimal(0)) == 1){
            SmsSkuFullReductionEntity smsSkuFullReductionEntity = new SmsSkuFullReductionEntity();
            BeanUtils.copyProperties(skuFullReductionTo,smsSkuFullReductionEntity);
            smsSkuFullReductionEntity.setAddOther(skuFullReductionTo.getCountStatus());
            this.save(smsSkuFullReductionEntity);
        }
        //保存打折信息
        if (skuFullReductionTo.getFullCount().compareTo(new BigDecimal(0)) == 1 && skuFullReductionTo.getDiscount().compareTo(new BigDecimal(0)) == 1){
            SmsSkuLadderEntity smsSkuLadderEntity = new SmsSkuLadderEntity();
            BeanUtils.copyProperties(skuFullReductionTo,smsSkuLadderEntity);
            smsSkuLadderEntity.setAddOther(skuFullReductionTo.getCountStatus());
            smsSkuLadderService.save(smsSkuLadderEntity);
        }
        //保存商品会员价格信息
        List<MemberPrice> memberPrices = skuFullReductionTo.getMemberPrice();
        List<SmsMemberPriceEntity> smsMemberPriceEntities = memberPrices.stream().map(memberPrice -> {
            SmsMemberPriceEntity smsMemberPriceEntity = new SmsMemberPriceEntity();
            smsMemberPriceEntity.setSkuId(skuFullReductionTo.getSkuId());
            smsMemberPriceEntity.setMemberPrice(memberPrice.getPrice());
            smsMemberPriceEntity.setMemberLevelName(memberPrice.getName());
            smsMemberPriceEntity.setAddOther(1);
            return smsMemberPriceEntity;
        }).filter(member -> member.getMemberPrice().compareTo(new BigDecimal(0)) == 1).collect(Collectors.toList());
        smsMemberPriceService.saveBatch(smsMemberPriceEntities);
    }

}