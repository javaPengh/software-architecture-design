package org.zzu.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.zzu.pojo.PortalVo;
import org.zzu.pojo.Ticket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.zzu.pojo.TicketDto;

/**
* @description 针对表【ticket】的数据库操作Mapper
* @Entity org.zzu.pojo.Ticket
*/
public interface TicketMapper extends BaseMapper<Ticket> {
    IPage<TicketDto> showOrders(IPage<TicketDto> iPage, @Param("portalVo") PortalVo portalVo);

    void updateOrderStatus(@Param("id") Integer id);
}




