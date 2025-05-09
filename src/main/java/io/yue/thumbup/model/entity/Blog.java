package io.yue.thumbup.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName blog
 */
@TableName(value ="blog")
@Data
public class Blog {
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
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 封面
     */
    @TableField(value = "coverImg")
    private String coverImg;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 点赞数
     */
    @TableField(value = "thumbcount")
    private Integer thumbcount;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;
}