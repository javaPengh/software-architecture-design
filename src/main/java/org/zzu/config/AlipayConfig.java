package org.zzu.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝沙箱环境配置类
 * @className AlipayConfig
 * @description 支付宝支付相关配置参数，包括应用ID、密钥、异步通知地址和同步跳转地址等
 */
@Data
@Component
@ConfigurationProperties(prefix = "alipay")  // 绑定配置文件前缀
public class AlipayConfig {
    /**
     * 支付宝应用ID（APPID）
     * 需从支付宝开放平台申请获取
     */
    private String appId;

    /**
     * 应用私钥（RSA2格式）
     * 用于生成请求签名，需妥善保管避免泄露
     */
    private String appPrivateKey;

    /**
     * 支付宝公钥（RSA2格式）
     * 用于验证支付宝返回的签名，需从开放平台获取
     */
    private String alipayPublicKey;

    /**
     * 异步通知接收地址（服务器回调URL）
     * 支付完成后支付宝服务器主动通知商户的地址
     * 需公网可访问，处理支付结果通知
     */
    private String notifyUrl;

    /**
     * 同步跳转地址（页面重定向URL）
     * 支付完成后用户浏览器自动跳转回商户页面的地址
     * 通常用于展示支付结果页面
     */
    private String returnUrl;
}