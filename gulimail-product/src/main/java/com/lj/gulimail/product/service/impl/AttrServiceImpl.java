package com.lj.gulimail.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.common.constant.Constant;
import com.lj.common.utils.PageUtils;
import com.lj.common.utils.Query;
import com.lj.gulimail.product.dao.AttrDao;
import com.lj.gulimail.product.entity.AttrAttrgroupRelationEntity;
import com.lj.gulimail.product.entity.AttrEntity;
import com.lj.gulimail.product.entity.ProductAttrValueEntity;
import com.lj.gulimail.product.service.*;
import com.lj.gulimail.product.vo.AttrRespVo;
import com.lj.gulimail.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 新增属性,并关联属性分组和分类
     * @param attr
     */
    @Transactional
    public void saveDetail(AttrVo attr) {
        Integer type = attr.getAttrType();
        //保存属性基本数据
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity,"attrGroupId");
        this.save(attrEntity);
        //保存关联的属性分组信息
        if (type == Constant.Product.BASE_TYPE.getType() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity attrAttrgroupRelation = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelation.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelation.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationService.save(attrAttrgroupRelation);
        }
    }

    /**
     * 根据分类id查询属性
     * @param params
     * @param catelogId
     * @param type
     * @return
     */
    public PageUtils queryPageByCatelogId(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        //判断当前查找的是销售属性还是基本属性,销售属性不需要添加属性分组
        wrapper.eq("attr_type"
                , Constant.Product.BASE_TYPE.getEq().equals(type) ? Constant.Product.BASE_TYPE.getType() : Constant.Product.SALE_TYPE.getType());
        if (catelogId != 0){
            wrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(wrapper1 -> {
                wrapper1.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        List<AttrEntity> records = page.getRecords();
        //对返回结果进行处理,添加需要的属性(分类名称和属性分组)
        List<AttrRespVo> newRecords = records.stream().map(item -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(item, attrRespVo);
            String cateName = categoryService.getById(item.getCatelogId()).getName();
            attrRespVo.setCatelogName(cateName);
            if (Constant.Product.BASE_TYPE.getEq().equals(type)) {
                //若属性为基本属性
                AttrAttrgroupRelationEntity attrgroupRelationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", item.getAttrId()));
                if (attrgroupRelationEntity!=null){
                    Long groupId = attrgroupRelationEntity.getAttrGroupId();
                    String groupName = attrGroupService.getById(groupId).getAttrGroupName();
                    attrRespVo.setGroupName(groupName);
                }
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(newRecords);
        return pageUtils;
    }

    /**
     * 根据id查询属性详细信息
     * @param attrId
     * @return
     */
    public AttrRespVo getDetailById(Long attrId) {
        //属性基本信息
        AttrEntity attr = this.getById(attrId);
        //查询分类信息和属性分组信息
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attr,attrRespVo);
        Long[] catelogPath = categoryService.findPathById(attr.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);
        if (attr.getAttrType() == Constant.Product.BASE_TYPE.getType()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelation = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrAttrgroupRelation != null){
                Long attrGroupId = attrAttrgroupRelation.getAttrGroupId();
                attrRespVo.setAttrGroupId(attrGroupId);
            }
        }
        return attrRespVo;
    }

    /**
     * 根据id修改属性信息
     * @param attr
     */
    @Transactional
    public void updateAttById(AttrVo attr) {
        //修改属性基本信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity,"attrGroupId");
        //修改属性分组信息
        if (attr.getAttrType() == Constant.Product.BASE_TYPE.getType()) {
            QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId());
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(wrapper);
            if (attrAttrgroupRelationEntity == null){
                attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
                attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
                //新增
                attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
            }
            else{
                //修改
                attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
                attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
                attrAttrgroupRelationService.update(attrAttrgroupRelationEntity,wrapper);
            }
        }
        //修改属性基本信息
        this.updateById(attrEntity);
    }

    /**
     * 根据spuId查询商品的基本属性
     * @param spuId
     * @return
     */
    public List<ProductAttrValueEntity> getBaseAttrBySpuId(Long spuId) {
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return attrValueEntities;
    }

    /**
     * 修改商品基本属性
     * @param spuId
     * @param entities
     */
    @Transactional
    public void updateBaseAttr(Long spuId, List<ProductAttrValueEntity> entities) {
        //先查询出商品的基本属性   为空,直接插入数据 ,不为空,先删除旧数据,在插入新数据
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        if (attrValueEntities != null && !attrValueEntities.isEmpty()){
            productAttrValueService.getBaseMapper().delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
        }
        List<ProductAttrValueEntity> updateData = entities.stream().map(e -> {
            e.setSpuId(spuId);
            return e;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(updateData);
    }

}