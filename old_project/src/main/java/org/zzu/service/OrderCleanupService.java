package org.zzu.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zzu.mapper.TicketMapper; // 改为 TicketMapper
import java.time.LocalDateTime; // 确保导入正确

@Service
@Slf4j
public class OrderCleanupService {

    @Autowired
    private TicketMapper ticketMapper; // 改为 TicketMapper

    /**
     * 定时任务：每分钟检查一次，清理2分钟内未付款的订单
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void cleanupUnpaidOrders() {
        try {
            log.info("开始清理超过2分钟未付款的电影票订单...");

            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(2);
            log.info("删除截止时间: {}", cutoffTime);

            // 先查询符合条件的记录数
            int countBefore = ticketMapper.selectCountForCleanup(cutoffTime);
            log.info("清理前符合条件的订单数: {}", countBefore);

            int deletedCount = ticketMapper.deleteExpiredUnpaidOrders(cutoffTime);

            log.info("实际删除的订单数: {}", deletedCount);
        } catch (Exception e) {
            log.error("清理未付款订单时发生错误", e);
        }
    }

}
