package com.lj.gulimail.order.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lj.gulimail.order.entity.OmsRefundInfoEntity;
import com.lj.gulimail.order.service.OmsRefundInfoService;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.R;



/**
 * 退款信息
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-21 09:22:04
 */
@RestController
@RequestMapping("order/omsrefundinfo")
public class OmsRefundInfoController {
    @Autowired
    private OmsRefundInfoService omsRefundInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("order:omsrefundinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = omsRefundInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("order:omsrefundinfo:info")
    public R info(@PathVariable("id") Long id){
		OmsRefundInfoEntity omsRefundInfo = omsRefundInfoService.getById(id);

        return R.ok().put("omsRefundInfo", omsRefundInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("order:omsrefundinfo:save")
    public R save(@RequestBody OmsRefundInfoEntity omsRefundInfo){
		omsRefundInfoService.save(omsRefundInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("order:omsrefundinfo:update")
    public R update(@RequestBody OmsRefundInfoEntity omsRefundInfo){
		omsRefundInfoService.updateById(omsRefundInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("order:omsrefundinfo:delete")
    public R delete(@RequestBody Long[] ids){
		omsRefundInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
