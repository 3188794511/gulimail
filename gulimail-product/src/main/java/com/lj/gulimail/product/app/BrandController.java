package com.lj.gulimail.product.app;

import com.lj.common.utils.PageUtils;
import com.lj.common.utils.R;
import com.lj.gulimail.product.entity.BrandEntity;
import com.lj.gulimail.product.service.BrandService;
import com.lj.gulimail.product.valid.AddGroup;
import com.lj.gulimail.product.valid.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 品牌
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:23:16
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@RequestBody @Validated(AddGroup.class) BrandEntity brand){
		brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@RequestBody @Validated(UpdateGroup.class) BrandEntity brand){
		brandService.updateDetailById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));
        return R.ok();
    }

    @PostMapping("/update/status")
    public R updateStatus(@RequestBody BrandEntity brand){
        brandService.updateById(brand);
        return R.ok();
    }

}
