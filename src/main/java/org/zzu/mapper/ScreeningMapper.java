package org.zzu.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.zzu.pojo.PortalVo;
import org.zzu.pojo.Screening;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.zzu.pojo.ScreeningDto;

/**
* @description 针对表【screening】的数据库操作Mapper
* @Entity org.zzu.pojo.Screening
*/
public interface ScreeningMapper extends BaseMapper<Screening> {

    IPage<ScreeningDto> getScreeningDtoList(IPage<ScreeningDto> iPage, @Param("portalVo") PortalVo portalVo);

    IPage<Screening> showScreeningList(IPage<Screening> iPage, @Param("portalVo") PortalVo portalVo);

    int checkConflict(@Param("entity") Screening entity);

}




