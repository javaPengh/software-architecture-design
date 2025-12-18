package org.zzu.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.zzu.pojo.Movie;

import java.util.List;

public interface MovieRepository extends ElasticsearchRepository<Movie, String> {
    
    /**
     * 使用Elasticsearch的multi-match查询搜索电影
     * @param keyword 搜索关键词
     * @return 匹配的电影列表
     */
    @Query("""
        {
            "multi_match": {
                "query": "?0",
                "fields": ["mname^3", "director^2", "type^2", "synopsis"],
                "fuzziness": "AUTO"
            }
        }
    """)
    List<Movie> findByKeyword(String keyword);
}
