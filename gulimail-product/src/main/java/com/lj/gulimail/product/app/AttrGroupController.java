package com.lj.gulimail.product.app;

import com.lj.common.utils.PageUtils;
import com.lj.common.utils.R;
import com.lj.gulimail.product.entity.AttrAttrgroupRelationEntity;
import com.lj.gulimail.product.entity.AttrEntity;
import com.lj.gulimail.product.entity.AttrGroupEntity;
import com.lj.gulimail.product.service.AttrGroupService;
import com.lj.gulimail.product.service.CategoryService;
import com.lj.gulimail.product.vo.AttrGroupWithAttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 属性分组
 *
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:23:16
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable Long catelogId){
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long[] path = categoryService.findPathById(attrGroup.getCatelogId());
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);
        return R.ok();
    }

    @PostMapping("/attr/relation")
    public R saveAttrRelation(@RequestBody AttrAttrgroupRelationEntity[] relation){
        attrGroupService.saveAttrRelation(relation);
        return R.ok();
    }

    @GetMapping("/{attrGroupId}/attr/relation")
    public R findAttrRelationById(@PathVariable("attrGroupId") Long attrGroupId){
        AttrEntity[] attrEntities = attrGroupService.findAttrRelationById(attrGroupId);
        return R.ok().put("data" ,attrEntities);
    }

    @GetMapping("/{attrGroupId}/noattr/relation")
    public R findAllAttrRelation(@PathVariable("attrGroupId") Long attrGroupId,@RequestParam Map<String,Object> params){
        PageUtils pageUtils = attrGroupService.findNotAttrRelation(attrGroupId,params);
        return R.ok().put("page",pageUtils);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrAttrgroupRelationEntity[] attrAttrgroupRelationEntities){
        attrGroupService.deleteBatchRelation(attrAttrgroupRelationEntities);
        return R.ok();
    }

    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttr(@PathVariable("catelogId") Long catelogId){
        AttrGroupWithAttrVo[] attrGroupWithAttrVo = attrGroupService.findAttrGroupWithAttr(catelogId);
        return R.ok().put("data", attrGroupWithAttrVo);
    }

}
