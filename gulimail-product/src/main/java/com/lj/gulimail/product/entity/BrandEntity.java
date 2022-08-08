package com.lj.gulimail.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lj.gulimail.product.valid.AddGroup;
import com.lj.gulimail.product.valid.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:23:16
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(groups = AddGroup.class,message = "品牌id必须为空")
	@NotNull(groups = UpdateGroup.class,message = "品牌id必须不为空")
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(groups = AddGroup.class,message = "品牌名不能为空")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(groups = AddGroup.class,message = "品牌logo地址不能为空")
	@URL(groups = {AddGroup.class,UpdateGroup.class},message = "品牌logo地址必须为URL地址")
	private String logo;
	/**
	 * 介绍
	 */
	@NotBlank(groups = AddGroup.class,message = "介绍不能为空")
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = AddGroup.class,message = "显示状态不能为空")
	@Min(groups = {AddGroup.class,UpdateGroup.class},value = 0,message = "显示状态只能为1或0")
	@Max(groups = {AddGroup.class,UpdateGroup.class},value = 1,message = "显示状态只能为1或0")
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(groups = AddGroup.class,message = "检索首字母不能为空")
	@Pattern(groups = {AddGroup.class,UpdateGroup.class},regexp = "^[a-zA-Z]$",message = "检索首字母必须在a-z或A-Z之间")
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups = AddGroup.class,message = "排序字段不能为空")
	@Min(groups = {AddGroup.class,UpdateGroup.class},value = 0,message = "排序必须为不小于0的整数")
	private Integer sort;

}
