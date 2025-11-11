package org.zzu.service;

import org.zzu.pojo.PortalVo;
import org.zzu.pojo.Screening;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zzu.utils.Result;

/**
 * @description 针对表【screening】的数据库操作Service
 */
public interface ScreeningService extends IService<Screening> {

    Result getScreeningDtoList(PortalVo portalVo);

    Result showScreeningList(PortalVo portalVo);

    boolean insertData(Screening screening);

    boolean updateData(Screening screening);
}
