package io.yue.thumbup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.yue.thumbup.constant.UserConstant;
import io.yue.thumbup.domain.entity.User;
import io.yue.thumbup.service.UserService;
import io.yue.thumbup.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
* @author 东行
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-05-08 21:06:48
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public User getLoginUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(UserConstant.LOGIN_USER);
    }

}




