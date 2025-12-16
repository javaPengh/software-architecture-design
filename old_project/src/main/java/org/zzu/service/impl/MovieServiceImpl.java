package org.zzu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.zzu.pojo.Movie;
import org.zzu.pojo.PortalVo;
import org.zzu.service.MovieService;
import org.zzu.mapper.MovieMapper;
import org.zzu.utils.Result;
import org.zzu.utils.TransPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @description 针对表【movie】的数据库操作Service实现
 */
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie>
        implements MovieService {
    @Autowired
    private MovieMapper movieMapper;


    @Override
    public Result showMovieList(PortalVo portalVo) {
        IPage<Movie> page = new Page<>(portalVo.getPageNum(), portalVo.getPageSize());
        if(portalVo.getKeyword()!=null){
            LambdaQueryWrapper<Movie> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(Movie::getMname, portalVo.getKeyword());
            movieMapper.selectPage(page, queryWrapper);
        }else {
            movieMapper.selectPage(page, null);
        }
        return TransPage.PageListTOJSON(page, "movieList");
    }

}




