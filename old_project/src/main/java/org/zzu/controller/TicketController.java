package org.zzu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.zzu.pojo.PortalVo;
import org.zzu.pojo.Ticket;
import org.zzu.service.TicketService;
import org.zzu.utils.Result;
import org.zzu.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @className TicketController
 * @description 处理电影票表相关请求
 */
@RestController
@RequestMapping("ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    /**
     * 获取用户当前选择购票的放映场次的座位占用情况
     * @param sid 放映场次ID
     * @return 包含响应码，响应信息和座位号列表的Result类
     */
    @GetMapping("order/{sid}")
    public Result getOccupiedSeats(@PathVariable Integer sid) {
        LambdaQueryWrapper<Ticket> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Ticket::getSid, sid);
        lambdaQueryWrapper.eq(Ticket::getOrderStatus, "已支付").or().eq(Ticket::getOrderStatus, "待支付");
        lambdaQueryWrapper.select(Ticket::getSeatNumber);
        //在电影票表中查询出放映场次对应的所有已支付的订单信息
        List<Ticket> list = ticketService.list(lambdaQueryWrapper);
        //提取订单信息中的座位号
        List<Integer> seatList = list.stream().map(Ticket::getSeatNumber).collect(Collectors.toList());
        //将座位号列表封装成Result类返回
        return Result.ok(seatList);
    }

    /**
     * 处理购票请求
     *
     * @param ticket
     * @return 请求处理结果
     */
    @PostMapping("order")
    public Result buy(@RequestBody Ticket ticket) {
        Result result = Result.ok(null);
        try {
            ticketService.buy(ticket);
        } catch (Exception e) {
            // 如果是座位被占用的情况，返回特定错误码
            if (e.getMessage() != null && e.getMessage().contains("该座位已被占用")) {
                return Result.build(null, ResultCodeEnum.SEAT_OCCUPIED);
            }
            // 其他异常返回购票失败
            result = Result.build(null, ResultCodeEnum.TICKET_BOOKED_FAILED);
        }
        // 返回成功结果
        return result;
    }

    /**
     * 获取用户的全部订单
     * @param portalVo 包含分页参数和用户ID
     * @return 包含分页信息和订单列表的Result类
     */
    @PostMapping("getOrders")
    public Result showOrders(@RequestBody PortalVo portalVo) {
        return ticketService.showOrders(portalVo);
    }

    /**
     * 取消订单
     * @param tid 要取消的订单号
     * @return 请求处理结果
     */
    @PutMapping("order/{tid}")
    public Result cancelOrder(@PathVariable Integer tid) {
        Result result = Result.ok(null);
        try {
            ticketService.cancelOrder(tid);
        } catch (Exception e) {
            //返回订单取消失败的提示
            result = Result.build(null, ResultCodeEnum.TICKET_CANCEL_FAILED);
        }
        return result;
    }

    /**
     * 根据筛选条件查询电影票列表
     * @param portalVo 包含查询条件和分页参数
     * @return 查询结果
     */
    @GetMapping
    public Result showTicketList(@RequestBody PortalVo portalVo) {
        return ticketService.showTicketList(portalVo);
    }

    /**
     * 新增或更新电影票记录
     * @param ticket 要插入或更新的电影票记录
     * @return 请求处理结果
     */
    @PostMapping
    public Result saveOrUpdate(@RequestBody Ticket ticket) {
        try {
            if (ticketService.saveOrUpdate(ticket)) {
                return Result.ok(null);
            }
            return Result.build(null, ResultCodeEnum.UPDATE_FAILED);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.UPDATE_FAILED);
        }

    }

    /**
     * 删除电影票记录
     * @param ticket 订单信息（仅包含订单ID）
     * @return 请求处理结果
     */
    @DeleteMapping
    public Result delete(@RequestBody Ticket ticket) {
        try {
            if (ticketService.removeById(ticket.getTid())) {
                return Result.ok(null);
            }
            return Result.build(null, ResultCodeEnum.DELETE_FAILED);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.DELETE_FAILED);
        }
    }
}