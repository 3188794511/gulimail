package com.lj.gulimail.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.common.utils.PageUtils;
import com.lj.gulimail.member.entity.UmsMemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-21 08:35:45
 */
public interface UmsMemberService extends IService<UmsMemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

