package org.zzu.controller;
import lombok.Data;

/**
 * 支付宝支付信息封装类
 * @className Alipay
 * @description 封装支付请求和响应参数
 */
@Data
public class Alipay {
    /**
     * 商户订单号（本系统生成的唯一订单标识）
     */
    private String traceNo;

    /**
     * 订单总金额（单位：元）
     */
    private double totalAmount;

    /**
     * 订单标题/商品名
     */
    private String subject;

    /**
     * 支付宝交易号（支付成功后支付宝返回的唯一标识）
     */
    private String alipayTraceNo;
}