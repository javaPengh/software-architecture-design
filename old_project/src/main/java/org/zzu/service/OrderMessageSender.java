package org.zzu.service;

import org.springframework.stereotype.Component;

/**
 * RabbitMQ消息发送器（条件化版本）
 * 自动检测RabbitMQ依赖是否存在
 */
@Component
public class OrderMessageSender {
    
    private Object rabbitTemplate;
    
    /**
     * 发送订单创建成功消息
     */
    public void sendOrderSuccess(Long orderId, String userPhone) {
        String message = String.format("订单创建成功: 订单ID=%d, 用户手机=%s", orderId, userPhone);
        
        try {
            // 尝试使用RabbitMQ，如果不存在则跳过
            Class<?> rabbitTemplateClass = Class.forName("org.springframework.amqp.rabbit.core.RabbitTemplate");
            if (rabbitTemplate != null) {
                rabbitTemplateClass.getMethod("convertAndSend", String.class, String.class, Object.class)
                    .invoke(rabbitTemplate, "order.exchange", "order.success", message);
                System.out.println("✅ RabbitMQ消息发送: " + message);
                return;
            }
        } catch (Exception e) {
            // RabbitMQ依赖不存在，仅打印日志
        }
        
        System.out.println("📝 订单消息（RabbitMQ未启用）: " + message);
    }
    
    /**
     * 设置RabbitTemplate（如果有）
     */
    public void setRabbitTemplate(Object rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
}