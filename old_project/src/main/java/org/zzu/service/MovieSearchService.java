package org.zzu.service;

import org.zzu.pojo.Movie;
import java.util.List;

public interface MovieSearchService {
    
    /**
     * 根据关键词搜索电影
     * @param keyword 搜索关键词
     * @return 匹配的电影列表
     */
    List<Movie> searchByKeyword(String keyword);
    
    /**
     * 同步电影数据到Elasticsearch
     */
    void syncMovies();
}
