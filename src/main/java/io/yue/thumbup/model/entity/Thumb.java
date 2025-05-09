package io.yue.thumbup.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName thumb
 */
@TableName(value ="thumb")
@Data
public class Thumb {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 
     */
    @TableField(value = "blogId")
    private Long blogId;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;
}