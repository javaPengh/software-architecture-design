package org.zzu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zzu.pojo.Movie;
import org.zzu.service.MovieSearchService;
import org.zzu.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/search/movie")
public class MovieSearchController {

    @Autowired
    private MovieSearchService movieSearchService;

    @GetMapping("/keyword/{keyword}")
    public Result<List<Movie>> searchByKeyword(@PathVariable String keyword) {
        List<Movie> movies = movieSearchService.searchByKeyword(keyword);
        return Result.ok( movies);
    }

    @PostMapping("/sync")
    public Result<String> syncMovies() {
        try {
            movieSearchService.syncMovies();
            return Result.ok("电影数据同步成功");
        } catch (Exception e) {
            return Result.fail("同步失败: " + e.getMessage());
        }
    }
}
