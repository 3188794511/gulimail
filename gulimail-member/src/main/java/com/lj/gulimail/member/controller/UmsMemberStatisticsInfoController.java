package com.lj.gulimail.member.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lj.gulimail.member.entity.UmsMemberStatisticsInfoEntity;
import com.lj.gulimail.member.service.UmsMemberStatisticsInfoService;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.R;



/**
 * 会员统计信息
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-21 08:35:45
 */
@RestController
@RequestMapping("member/umsmemberstatisticsinfo")
public class UmsMemberStatisticsInfoController {
    @Autowired
    private UmsMemberStatisticsInfoService umsMemberStatisticsInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:umsmemberstatisticsinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = umsMemberStatisticsInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:umsmemberstatisticsinfo:info")
    public R info(@PathVariable("id") Long id){
		UmsMemberStatisticsInfoEntity umsMemberStatisticsInfo = umsMemberStatisticsInfoService.getById(id);

        return R.ok().put("umsMemberStatisticsInfo", umsMemberStatisticsInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:umsmemberstatisticsinfo:save")
    public R save(@RequestBody UmsMemberStatisticsInfoEntity umsMemberStatisticsInfo){
		umsMemberStatisticsInfoService.save(umsMemberStatisticsInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:umsmemberstatisticsinfo:update")
    public R update(@RequestBody UmsMemberStatisticsInfoEntity umsMemberStatisticsInfo){
		umsMemberStatisticsInfoService.updateById(umsMemberStatisticsInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:umsmemberstatisticsinfo:delete")
    public R delete(@RequestBody Long[] ids){
		umsMemberStatisticsInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}