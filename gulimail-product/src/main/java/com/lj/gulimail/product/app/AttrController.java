package com.lj.gulimail.product.app;

import com.lj.common.utils.PageUtils;
import com.lj.common.utils.R;
import com.lj.gulimail.product.entity.ProductAttrValueEntity;
import com.lj.gulimail.product.service.AttrService;
import com.lj.gulimail.product.vo.AttrRespVo;
import com.lj.gulimail.product.vo.AttrVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品属性
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:23:16
 */
@RestController
@RequestMapping("product/attr")
@Slf4j
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 根据分类查询属性
     */
    @RequestMapping("/{type}/list/{catelogId}")
    public R listPage(@RequestParam Map<String, Object> params
            ,@PathVariable("type") String type,@PathVariable("catelogId") Long catelogId){
        PageUtils page = attrService.queryPageByCatelogId(params,catelogId,type);
        log.info("属性类型为{}",type);
        return R.ok().put("page", page);
    }

    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 查询商品基本属性
     * @param spuId
     * @return
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttr(@PathVariable Long spuId){
        List<ProductAttrValueEntity> list = attrService.getBaseAttrBySpuId(spuId);
        return R.ok().put("data",list );
    }

    @PostMapping("/update/{spuId}")
    public R updateBaseAttr(@PathVariable Long spuId,@RequestBody List<ProductAttrValueEntity> entities){
        attrService.updateBaseAttr(spuId,entities);
        return R.ok();
    }




    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		AttrRespVo attr = attrService.getDetailById(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveDetail(attr);

        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttById(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
