package com.lj.gulimail.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lj.gulimail.coupon.entity.SmsSeckillSessionEntity;
import com.lj.gulimail.coupon.service.SmsSeckillSessionService;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.R;



/**
 * 秒杀活动场次
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:32:14
 */
@RestController
@RequestMapping("coupon/smsseckillsession")
public class SmsSeckillSessionController {
    @Autowired
    private SmsSeckillSessionService smsSeckillSessionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("coupon:smsseckillsession:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = smsSeckillSessionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:smsseckillsession:info")
    public R info(@PathVariable("id") Long id){
		SmsSeckillSessionEntity smsSeckillSession = smsSeckillSessionService.getById(id);

        return R.ok().put("smsSeckillSession", smsSeckillSession);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:smsseckillsession:save")
    public R save(@RequestBody SmsSeckillSessionEntity smsSeckillSession){
		smsSeckillSessionService.save(smsSeckillSession);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:smsseckillsession:update")
    public R update(@RequestBody SmsSeckillSessionEntity smsSeckillSession){
		smsSeckillSessionService.updateById(smsSeckillSession);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:smsseckillsession:delete")
    public R delete(@RequestBody Long[] ids){
		smsSeckillSessionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
