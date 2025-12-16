package org.zzu.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.zzu.vo.PortalVo;
import org.zzu.pojo.Ticket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.zzu.dto.TicketDto;
import java.time.LocalDateTime;

/**
* @description 针对表【ticket】的数据库操作Mapper
* @Entity org.zzu.pojo.Ticket
*/
public interface TicketMapper extends BaseMapper<Ticket> {
    IPage<TicketDto> showOrders(IPage<TicketDto> iPage, @Param("portalVo") PortalVo portalVo);

    @Delete("DELETE FROM ticket WHERE order_status = '待支付' AND purchase_time < #{cutoffTime}")
    int deleteExpiredUnpaidOrders(@Param("cutoffTime") LocalDateTime cutoffTime);
    @Select("SELECT COUNT(*) FROM ticket WHERE order_status = '待支付' AND purchase_time < #{cutoffTime}")
    int selectCountForCleanup(@Param("cutoffTime") LocalDateTime cutoffTime);

    void updateOrderStatus(@Param("id") Integer id);
}




