package io.yue.thumbup.mapper;

import io.yue.thumbup.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 东行
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-05-08 21:06:48
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




