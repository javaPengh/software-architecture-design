package org.zzu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.zzu.pojo.Movie;
import org.zzu.vo.PortalVo;
import org.zzu.service.MovieService;
import org.zzu.utils.Result;
import org.zzu.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @className MovieController
 * @description 电影表的增删改查操作
 */
@RestController
@RequestMapping("movie")
@CrossOrigin
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public Result getTotalList() {
        LambdaQueryWrapper<Movie> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(Movie::getMid, Movie::getMname, Movie::getDirector, Movie::getReleaseDate, Movie::getSynopsis, Movie::getPoster, Movie::getType, Movie::getRuntime);
        return Result.ok(movieService.list(lambdaQueryWrapper));
    }

    @PostMapping
    public Result showMovieList(@RequestBody PortalVo portalVo) {
        return movieService.showMovieList(portalVo);
    }

    @PutMapping
    public Result saveOrUpdate(@RequestBody Movie movie) {
        try {
            if (movieService.saveOrUpdate(movie)) {
                return Result.ok(null);
            }
            return Result.build(null, ResultCodeEnum.UPDATE_FAILED);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.UPDATE_FAILED);
        }
    }

    @DeleteMapping("/{mid}")
    public Result delete(@PathVariable Integer mid) {
        try {
            if (movieService.removeById(mid)) {
                return Result.ok(null);
            }
            return Result.build(null, ResultCodeEnum.DELETE_FAILED);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.DELETE_FAILED);
        }
    }
}
