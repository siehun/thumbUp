package io.yue.thumbup.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.repository.AbstractRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.yue.thumbup.constant.RedisLuaScriptConstant;
import io.yue.thumbup.constant.ThumbConstant;
import io.yue.thumbup.mapper.ThumbMapper;
import io.yue.thumbup.model.dto.DoThumbRequest;
import io.yue.thumbup.model.entity.Blog;
import io.yue.thumbup.model.entity.Thumb;
import io.yue.thumbup.model.entity.User;
import io.yue.thumbup.model.enums.LuaStatusEnum;
import io.yue.thumbup.service.BlogService;
import io.yue.thumbup.service.ThumbService;
import io.yue.thumbup.service.UserService;
import io.yue.thumbup.util.RedisKeyUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

/**
* @author 东行
* @description 针对表【thumb】的数据库操作Service实现
* @createDate 2025-05-08 21:06:40
*/
@Service("thumbService")
@Slf4j
@RequiredArgsConstructor
public class ThumbServiceRedisImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final UserService userService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        Long blogId = doThumbRequest.getBlogId();
        String timeSlice = getTimeSlice();
        String tempThumbKey = RedisKeyUtil.getTempThumbKey(timeSlice);
        String userThubKey = RedisKeyUtil.getUserThumbKey(loginUser.getId());
        long result = redisTemplate.execute(RedisLuaScriptConstant.THUMB_SCRIPT, Arrays.asList(tempThumbKey, userThubKey), loginUser.getId(),blogId);
        if (LuaStatusEnum.FAIL.getValue() == result) {
            throw new RuntimeException("用户已点赞");
        }
        return LuaStatusEnum.SUCCESS.getValue() == result;

    }
    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || doThumbRequest.getBlogId() == null) {
            throw new RuntimeException("参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        Long blogId = doThumbRequest.getBlogId();
        String timeSlice = getTimeSlice();
        String tempThumbKey = RedisKeyUtil.getTempThumbKey(timeSlice);
        String userThumbKey = RedisKeyUtil.getUserThumbKey(loginUser.getId());
        // 加锁
        long result = redisTemplate.execute(RedisLuaScriptConstant.UNTHUMB_SCRIPT, Arrays.asList(tempThumbKey, userThumbKey), loginUser.getId(), blogId);
        if (result == LuaStatusEnum.FAIL.getValue()) {
            throw new RuntimeException("用户未点赞");
        }
        return LuaStatusEnum.SUCCESS.getValue() == result;
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
     * 获取离当前时间最近的10整数秒
     */
    public String getTimeSlice() {
        DateTime nowDate = DateUtil.date();
        return DateUtil.format(nowDate, "HH:mm") + (DateUtil.second(nowDate) / 10) * 10;
    }
}




