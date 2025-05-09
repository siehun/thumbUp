package io.yue.thumbup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.yue.thumbup.constant.ThumbConstant;
import io.yue.thumbup.model.dto.DoThumbRequest;
import io.yue.thumbup.model.entity.Thumb;
import io.yue.thumbup.model.entity.User;
import io.yue.thumbup.service.BlogService;
import io.yue.thumbup.service.ThumbService;
import io.yue.thumbup.mapper.ThumbMapper;
import io.yue.thumbup.service.UserService;
import io.yue.thumbup.model.entity.Blog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
* @author 东行
* @description 针对表【thumb】的数据库操作Service实现
* @createDate 2025-05-08 21:06:40
*/
@Service("thumbServiceDB")
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserService userService;

    private final BlogService blogService;

    private final TransactionTemplate transactionTemplate;

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        // 加锁
        synchronized (loginUser.getId().toString().intern()) {

            // 编程式事务
            return transactionTemplate.execute(status -> {
                Long blogId = doThumbRequest.getBlogId();
                boolean exists = this.hasThumb(blogId, loginUser.getId());
                if (exists) {
                    throw new RuntimeException("用户已点赞");
                }

                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount + 1")
                        .update();

                Thumb thumb = new Thumb();
                thumb.setUserId(loginUser.getId());
                thumb.setBlogId(blogId);
                // 更新成功才执行
                boolean success =  update && this.save(thumb);
                if (success) {
                    redisTemplate.opsForHash().put(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId().toString(), blogId.toString(),thumb.getId());
                }
                return success;
            });
        }
    }
    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        // 加锁
        synchronized (loginUser.getId().toString().intern()) {

            // 编程式事务
            return transactionTemplate.execute(status -> {
                Long blogId = doThumbRequest.getBlogId();
                Object thumbIdObj = queryThumbRedis(blogId, loginUser.getId());
                if (thumbIdObj == null) {
                    throw new RuntimeException("用户未点赞");
                }
                Long thumbId = Long.valueOf(thumbIdObj.toString());
                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, blogId)
                        .setSql("thumbCount = thumbCount - 1")
                        .update();

                //return update && this.removeById(thumb.getId());
                boolean success = update && this.removeById(thumbId);
                if (success) {
                    redisTemplate.opsForHash().delete(ThumbConstant.USER_THUMB_KEY_PREFIX + loginUser.getId(), blogId.toString());
                }
                return success;
            });
        }
    }

    /**
     * mysql中查询是否点赞方法
     * @param blogId
     * @param userId
     * @return
     */
    public boolean hasThumbMysql(Long blogId, Long userId) {
        return this.lambdaQuery().eq(Thumb::getUserId, userId)
                .eq(Thumb::getBlogId, blogId)
                .exists();
    }

    /**
     * redis中查询是否点赞方法
     * @param blogId
     * @param userId
     * @return
     */
    @Override
    public Boolean hasThumb(Long blogId, Long userId) {
        return redisTemplate.opsForHash().hasKey(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, blogId.toString());
    }

    /**
     * mysql中查询点赞的方法
     * @param blogId
     * @param userId
     * @return
     */
    public Thumb queryThumbMysql(Long blogId, Long userId) {
        Thumb thumb = this.lambdaQuery().eq(Thumb::getBlogId, blogId)
                .eq(Thumb::getUserId, userId)
                .one();
        return thumb;
    }

    /**
     * redis中查询点赞的方法
     * @param blogId
     * @param userId
     * @return
     */
    public Object queryThumbRedis(Long blogId, Long userId) {
        return redisTemplate.opsForHash().get(ThumbConstant.USER_THUMB_KEY_PREFIX + userId, blogId.toString());
    }




}




