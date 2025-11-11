package org.zzu.service;

import org.zzu.pojo.Hall;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zzu.pojo.PortalVo;
import org.zzu.utils.Result;

/**
* @description 针对表【hall】的数据库操作Service
*/
public interface HallService extends IService<Hall> {

    Result showHallList(PortalVo portalVo);
}
