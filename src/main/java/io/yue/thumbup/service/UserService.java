package io.yue.thumbup.service;

import io.yue.thumbup.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 东行
* @description 针对表【user】的数据库操作Service
* @createDate 2025-05-08 21:06:48
*/
public interface UserService extends IService<User> {

    User getLoginUser(HttpServletRequest request);
}
