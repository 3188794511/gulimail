package com.lj.gulimail.order.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;

import com.lj.gulimail.order.dao.OmsRefundInfoDao;
import com.lj.gulimail.order.entity.OmsRefundInfoEntity;
import com.lj.gulimail.order.service.OmsRefundInfoService;


@Service("omsRefundInfoService")
public class OmsRefundInfoServiceImpl extends ServiceImpl<OmsRefundInfoDao, OmsRefundInfoEntity> implements OmsRefundInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OmsRefundInfoEntity> page = this.page(
                new Query<OmsRefundInfoEntity>().getPage(params),
                new QueryWrapper<OmsRefundInfoEntity>()
        );

        return new PageUtils(page);
    }

}