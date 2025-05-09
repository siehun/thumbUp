package io.yue.thumbup.mapper;

import io.yue.thumbup.model.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
* @author 东行
* @description 针对表【blog】的数据库操作Mapper
* @createDate 2025-05-08 21:06:00
* @Entity generator.domain.Blog
*/
@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
    void batchUpdateThumbCount(@Param("countMap") Map<Long, Long> countMap);

}




