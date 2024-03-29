package com.lj.gulimail.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品满减信息
 * 
 * @author lijing
 * @email 3188794511@qq.com
 * @date 2022-06-20 17:32:15
 */
@Data
@TableName("sms_sku_full_reduction")
public class SmsSkuFullReductionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * spu_id
	 */
	private Long skuId;
	/**
	 * 满多少
	 */
	private BigDecimal fullPrice;
	/**
	 * 减多少
	 */
	private BigDecimal reducePrice;
	/**
	 * 是否参与其他优惠
	 */
	private Integer addOther;

	/**
	 *   private BigDecimal fullCount;
	 *     private BigDecimal discount;
	 *     private int countStatus;
	 *     private BigDecimal fullPrice;
	 *     private BigDecimal reducePrice;
	 *     private int priceStatus;
	 */
}
