package org.zzu.service;

import org.zzu.pojo.Movie;
import com.baomidou.mybatisplus.extension.service.IService;
import org.zzu.vo.PortalVo;
import org.zzu.utils.Result;

/**
* @description 针对表【movie】的数据库操作Service
*/
public interface MovieService extends IService<Movie> {

    Result showMovieList(PortalVo portalVo);
}
