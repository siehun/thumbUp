package io.yue.thumbup.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.yue.thumbup.constant.ThumbConstant;
import io.yue.thumbup.model.entity.Blog;
import io.yue.thumbup.model.entity.User;
import io.yue.thumbup.model.vo.BlogVO;
import io.yue.thumbup.service.BlogService;
import io.yue.thumbup.mapper.BlogMapper;
import io.yue.thumbup.service.ThumbService;
import io.yue.thumbup.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author 东行
* @description 针对表【blog】的数据库操作Service实现
* @createDate 2025-05-08 21:06:00
*/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
    implements BlogService{
    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private ThumbService thumbService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public BlogVO getBlogVOById(long blogId, HttpServletRequest request) {
        Blog blog = this.getById(blogId);
        User loginUser = userService.getLoginUser(request);
        return this.getBlogVO(blog, loginUser);
    }

    private BlogVO getBlogVO(Blog blog, User loginUser) {
        BlogVO blogVO = new BlogVO();
        BeanUtil.copyProperties(blog, blogVO);

        if (loginUser == null) {
            return blogVO;
        }

//        Thumb thumb = thumbService.lambdaQuery()
//                .eq(Thumb::getUserId, loginUser.getId())
//                .eq(Thumb::getBlogId, blog.getId())
//                .one();
//        blogVO.setHasThumb(thumb != null);
        boolean exist = thumbService.hasThumb(blog.getId(), loginUser.getId());
        blogVO.setHasThumb(exist);

        return blogVO;
    }

    @Override
    public List<BlogVO> getBlogVOList(List<Blog> blogList, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Map<Long, Boolean> blogIdHasThumbMap = new HashMap<>();
        if (ObjUtil.isNotEmpty(loginUser)) {
//            Set<Long> blogIdSet = blogList.stream().map(Blog::getId).collect(Collectors.toSet());
//            // 获取点赞
//            List<Thumb> thumbList = thumbService.lambdaQuery()
//                    .eq(Thumb::getUserId, loginUser.getId())
//                    .in(Thumb::getBlogId, blogIdSet)
//                    .list();
//
//            thumbList.forEach(blogThumb -> blogIdHasThumbMap.put(blogThumb.getBlogId(), true));
            List<Object> blogIdList = blogList.stream().map(blog -> blog.getId().toString()).collect(Collectors.toList());
            // 获取点赞
            List<Object> thumbList = redisTemplate.opsForHash().multiGet(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId(), blogIdList);
            for (int i = 0; i < thumbList.size(); i++) {
                if (thumbList.get(i) == null) {
                    continue;
                }
                blogIdHasThumbMap.put(Long.valueOf(blogList.get(i).toString()), true);
            }

        }

        return blogList.stream()
                .map(blog -> {
                    BlogVO blogVO = BeanUtil.copyProperties(blog, BlogVO.class);
                    blogVO.setHasThumb(blogIdHasThumbMap.get(blog.getId()));
                    return blogVO;
                })
                .toList();
    }



}




