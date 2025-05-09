package io.yue.thumbup.util;

import io.yue.thumbup.constant.ThumbConstant;

public class RedisKeyUtil {

    public static String getUserThumbKey(Long userId) {
        return ThumbConstant.USER_THUMB_KEY_PREFIX + userId;
    }

    /**
     * 获取临时点赞记录 key
     * @param time
     * @return
     */
    public static String getTempThumbKey(String time) {
        return ThumbConstant.TEMP_THUMB_KEY_PREFIX.formatted(time);
    }
}
