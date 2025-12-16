package org.zzu.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.zzu.mapper.ScreeningMapper;
import org.zzu.pojo.*;
import org.zzu.vo.PortalVo;
import org.zzu.dto.TicketDto;
import org.zzu.service.MembershipService;
import org.zzu.service.TicketService;
import org.zzu.mapper.TicketMapper;
import org.zzu.utils.Result;
import org.zzu.utils.TransPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @description 针对表【ticket】的数据库操作Service实现
 */
@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket>
        implements TicketService {
    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private ScreeningMapper screeningMapper;
    
    @Autowired
    private MembershipService membershipService;

    @Transactional
    @Override
    @SentinelResource(
        value = "ticketBuy",
        blockHandler = "handleTicketBuyBlock",
        fallback = "ticketBuyFallback"
    )
    public void buy(Ticket ticket) {
        boolean isMember = membershipService.isUserMember(ticket.getUid());
        if (isMember) {
            ticket.setOrderStatus("已支付");
            ticket.setPrice(BigDecimal.ZERO);
        } else {
            ticket.setOrderStatus("待支付");
            ticket.setPrice(new BigDecimal("50.00"));
        }
        
        // 插入订单信息
        ticketMapper.insert(ticket);
        
        // 更新场次剩余座位数
        LambdaUpdateWrapper<Screening> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Screening::getSid, ticket.getSid());
        updateWrapper.setDecrBy(Screening::getRemainingSeats, 1);
        screeningMapper.update(null, updateWrapper);
    }

    @Override
    public Result showOrders(PortalVo portalVo) {
        //分页参数设置
        IPage<TicketDto> page = new Page<>(portalVo.getPageNum(), portalVo.getPageSize());
        //若当前时间超过电影票对应放映场次的放映时间，则订单状态改为已完成
        ticketMapper.updateOrderStatus(portalVo.getId());
        ticketMapper.showOrders(page, portalVo);
        return TransPage.PageListTOJSON(page, "tDtoList");
    }

    @Transactional
    @Override
    public void cancelOrder(Integer tid) {
        LambdaUpdateWrapper<Ticket> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Ticket::getTid, tid).set(Ticket::getOrderStatus, "已取消");
        //将订单状态更新为已取消
        ticketMapper.update(null, updateWrapper);
        Ticket ticket = ticketMapper.selectById(tid);
        LambdaUpdateWrapper<Screening> updateWrapper1 = new LambdaUpdateWrapper<>();
        //对应的放映场次票数加一
        updateWrapper1.eq(Screening::getSid, ticket.getSid()).setIncrBy(Screening::getRemainingSeats, 1);
        screeningMapper.update(null, updateWrapper1);
    }

    @Override
    public Result showTicketList(PortalVo portalVo) {
        IPage<Ticket> page = new Page<>(portalVo.getPageNum(), portalVo.getPageSize());
        ticketMapper.selectPage(page, null);
        //返回分页表示的订单列表
        return TransPage.PageListTOJSON(page, "TicketList");
    }
    
    /**
     * 限流处理函数
     */
    public void handleTicketBuyBlock(Ticket ticket, BlockException ex) {
        throw new RuntimeException("购票请求过于频繁，请稍后再试");
    }
    
    /**
     * 降级处理函数
     */
    public void ticketBuyFallback(Ticket ticket, Throwable e) {
        throw new RuntimeException("购票服务暂时不可用，请稍后再试");
    }
}




