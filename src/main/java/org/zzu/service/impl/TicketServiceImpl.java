package org.zzu.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.zzu.mapper.ScreeningMapper;
import org.zzu.pojo.*;
import org.zzu.service.TicketService;
import org.zzu.mapper.TicketMapper;
import org.zzu.utils.Result;
import org.zzu.utils.TransPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public void buy(Ticket ticket) {
        //插入订单信息
        ticketMapper.insert(ticket);
        LambdaUpdateWrapper<Screening> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Screening::getSid, ticket.getSid());
        updateWrapper.setDecrBy(Screening::getRemainingSeats, 1);
        //对应放映场次的剩余座位数减一
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
}




