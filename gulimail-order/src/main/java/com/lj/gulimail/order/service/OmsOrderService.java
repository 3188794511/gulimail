package com.lj.gulimail.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.common.utils.PageUtils;
import com.lj.gulimail.order.entity.OmsOrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-21 09:22:04
 */
public interface OmsOrderService extends IService<OmsOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

