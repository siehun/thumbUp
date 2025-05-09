package io.yue.thumbup.service;

import io.yue.thumbup.model.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import io.yue.thumbup.model.vo.BlogVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author 东行
* @description 针对表【blog】的数据库操作Service
* @createDate 2025-05-08 21:06:00
*/
public interface BlogService extends IService<Blog> {

    BlogVO getBlogVOById(long blogId, HttpServletRequest request);
    List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request);


}
