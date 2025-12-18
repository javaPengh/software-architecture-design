package org.zzu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zzu.pojo.Movie;
import org.zzu.repository.MovieRepository;
import org.zzu.service.MovieDataService;
import org.zzu.service.MovieSearchService;

import java.util.List;

@Service
public class MovieSearchServiceImpl implements MovieSearchService {

    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private MovieDataService movieDataService; // 假设有一个服务从数据库获取电影数据

    @Override
    public List<Movie> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        return movieRepository.findByKeyword(keyword);
    }

    @Override
    public void syncMovies() {
        // 1. 从数据库获取所有电影数据
        List<Movie> movies = movieDataService.getAllMovies();
        
        // 2. 删除现有索引（可选，根据需求决定）
        movieRepository.deleteAll();
        
        // 3. 批量保存到Elasticsearch
        movieRepository.saveAll(movies);
    }
}
