package org.zzu.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zzu.pojo.Movie;
import org.zzu.service.MovieDataService;
import org.zzu.service.MovieService;

import java.util.List;

@Service
public class MovieDataServiceImpl implements MovieDataService {

    @Autowired
    private MovieService movieService;

    @Override
    public List<Movie> getAllMovies() {
        // 获取数据库中的电影数据
        return movieService.list();
    }
}