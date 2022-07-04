package com.lj.gulimail.coupon.controller;

import com.lj.common.utils.PageUtils;
import com.lj.common.utils.R;
import com.lj.gulimail.coupon.entity.SmsCouponEntity;
import com.lj.gulimail.coupon.service.SmsCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 优惠券信息
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:32:15
 */
@RestController
@RefreshScope
@RequestMapping("coupon/coupon")
public class SmsCouponController {
    @Autowired
    private SmsCouponService smsCouponService;

    @Value("${coupon.user.name}")
    private String name;

    @Value("${coupon.user.sex}")
    private String sex;

    @RequestMapping("/test")
    public R test(){
        return R.ok().put("name",name).put("sex",sex);
    }

    @RequestMapping("/getByMemberId")
    public R getByMemberId(){
        SmsCouponEntity coupon = new SmsCouponEntity();
        coupon.setCouponName("满100减20优惠券");
        return R.ok().put("coupon",coupon);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("coupon:smscoupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = smsCouponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:smscoupon:info")
    public R info(@PathVariable("id") Long id){
		SmsCouponEntity smsCoupon = smsCouponService.getById(id);

        return R.ok().put("smsCoupon", smsCoupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:smscoupon:save")
    public R save(@RequestBody SmsCouponEntity smsCoupon){
		smsCouponService.save(smsCoupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:smscoupon:update")
    public R update(@RequestBody SmsCouponEntity smsCoupon){
		smsCouponService.updateById(smsCoupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:smscoupon:delete")
    public R delete(@RequestBody Long[] ids){
		smsCouponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
