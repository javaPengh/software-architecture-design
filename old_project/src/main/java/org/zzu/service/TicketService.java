package org.zzu.service;

import org.zzu.vo.PortalVo;
import org.zzu.pojo.Ticket;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zzu.utils.Result;

/**
* @description 针对表【ticket】的数据库操作Service
*/
public interface TicketService extends IService<Ticket> {

    void buy(Ticket ticket);

    Result showOrders(PortalVo uid);

    void cancelOrder(Integer tid);

    Result showTicketList(PortalVo portalVo);

}
