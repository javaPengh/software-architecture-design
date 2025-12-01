package org.zzu.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageSender {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void sendOrderSuccess(Long orderId, String userPhone) {
        String message = String.format("订单创建成功: 订单ID=%d, 用户手机=%s", orderId, userPhone);
        rabbitTemplate.convertAndSend("order.exchange", "order.success", message);
        System.out.println("发送订单消息: " + message);
    }
}