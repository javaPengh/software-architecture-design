package org.zzu.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.zzu.config.AlipayConfig;
import org.zzu.mapper.TicketMapper;
import org.zzu.pojo.Ticket;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * @className AlipayController
 * @description 支付宝沙箱控制类
 */

// 沙箱账号：iwytqr1851@sandbox.com
@RestController
@RequestMapping("/alipay")
@Slf4j
public class AlipayController {

    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    //签名方式
    private static final String SIGN_TYPE = "RSA2";

    @Autowired
    private AlipayConfig aliPayConfig;

    @Autowired
    private TicketMapper ticketMapper;

    @GetMapping("/pay") // ?subject=电影票&traceNo=111&totalAmount=45
    public void pay(Alipay aliPay, HttpServletResponse httpResponse) throws Exception {

        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);
        // 2. 创建Request并设置Request参数
        AlipayTradePagePayRequest request = getAlipayTradePagePayRequest(aliPay);
        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    private @NotNull AlipayTradePagePayRequest getAlipayTradePagePayRequest(Alipay aliPay) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", aliPay.getTraceNo());  // 我们自己生成的订单编号
        bizContent.set("total_amount", aliPay.getTotalAmount()); // 订单的总金额
        bizContent.set("subject", aliPay.getSubject());   // 支付的名称
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());
        return request;
    }

    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws Exception {
        System.out.println("支付宝异步回调到达");
        Map<String, String> params = convertParams(request); // 提取参数

        // 1. 检查交易状态
        String tradeStatus = params.get("trade_status");
        if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
            System.out.println("忽略状态：" + tradeStatus);
            return "success";
        }

        // 2. 验签
        String sign = params.get("sign");
        String content = AlipaySignature.getSignCheckContentV1(params);
        boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign,
                aliPayConfig.getAlipayPublicKey(), "UTF-8");

        if (!checkSignature) {
            System.out.println("验签失败！");
            return "failure"; // 通知支付宝重试
        }

        // 3. 处理订单
        String outTradeNo = params.get("out_trade_no");
        System.out.println("开始更新订单：" + outTradeNo);

        Ticket ticket = ticketMapper.selectOne(new LambdaQueryWrapper<Ticket>()
                .eq(Ticket::getTid, outTradeNo));

        if (ticket == null) {
            System.out.println("订单不存在：" + outTradeNo);
            return "failure";
        }

        ticket.setOrderStatus("已支付");
        int rows = ticketMapper.updateById(ticket);
        System.out.println(rows > 0 ? "订单更新成功" : "订单更新失败");
        return "success"; // 必须返回success
    }

    // 辅助方法：转换请求参数
    private Map<String, String> convertParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) ->
                params.put(key, String.join(",", values)));
        return params;
    }
}
