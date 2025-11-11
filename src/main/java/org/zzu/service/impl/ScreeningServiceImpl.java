package org.zzu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.zzu.mapper.HallMapper;
import org.zzu.mapper.MovieMapper;
import org.zzu.mapper.TicketMapper;
import org.zzu.pojo.*;
import org.zzu.service.ScreeningService;
import org.zzu.mapper.ScreeningMapper;
import org.zzu.utils.Result;
import org.zzu.utils.TransPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description 针对表【screening】的数据库操作Service实现
 */
@Service
public class ScreeningServiceImpl extends ServiceImpl<ScreeningMapper, Screening>
        implements ScreeningService {
    @Autowired
    private ScreeningMapper screeningMapper;
    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private HallMapper hallMapper;
    @Autowired
    private TicketMapper ticketMapper;

    @Override
    public Result getScreeningDtoList(PortalVo portalVo) {
        IPage<ScreeningDto> page = new Page<>(portalVo.getPageNum(), portalVo.getPageSize());
        screeningMapper.getScreeningDtoList(page, portalVo);
        return TransPage.PageListTOJSON(page, "sDtoList");
    }

    @Override
    public Result showScreeningList(PortalVo portalVo) {
        IPage<Screening> page = new Page<>(portalVo.getPageNum(), portalVo.getPageSize());
        screeningMapper.showScreeningList(page, portalVo);
        return TransPage.PageListTOJSON(page, "screeningList");
    }

    @Override
    public boolean insertData(Screening screening) {
        int num = screeningMapper.checkConflict(screening);
        System.out.println("num = " + num);
        if (num == 0) {
            Hall hall = hallMapper.selectById(screening.getHid());
            Movie movie = movieMapper.selectById(screening.getMid());
            screening.setSeatCount(hall.getRowCapacity() * hall.getRowNum());
            screening.setRemainingSeats(screening.getSeatCount());
            Date endTime = new Date(screening.getShowTime().getTime() + TimeUnit.MINUTES.toMillis(movie.getRuntime()));
            screening.setEndTime(endTime);
            screeningMapper.insert(screening);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateData(Screening screening) {
        int num = screeningMapper.checkConflict(screening);
        System.out.println("num = " + num);
        if (num == 0) {
            Hall hall = hallMapper.selectById(screening.getHid());
            Movie movie = movieMapper.selectById(screening.getMid());
            screening.setSeatCount(hall.getRowCapacity() * hall.getRowNum());
            LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Ticket::getSid, screening.getSid());
            wrapper.in(Ticket::getOrderStatus, "已支付", "待支付");
            Long count = ticketMapper.selectCount(wrapper);
            screening.setRemainingSeats(screening.getSeatCount() - count.intValue());
            Date endTime = new Date(screening.getShowTime().getTime() + TimeUnit.MINUTES.toMillis(movie.getRuntime()));
            screening.setEndTime(endTime);
            screeningMapper.updateById(screening);
            return true;
        }
        return false;
    }
}




