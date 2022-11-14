package com.anshi.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer type;  //菜品分类 1 ， 套餐分类 2

    private String name;  //分类名称

    private Integer sort;  //菜品套餐顺序

    @TableField(fill = FieldFill.INSERT)  //公共字段设置,插入生效
    private LocalDateTime createTime;  //账号创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)  //公共字段设置，插入和修改生效
    private LocalDateTime updateTime;  //账号修改时间

    @TableField(fill = FieldFill.INSERT)  //公共字段设置,插入生效
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)  //公共字段设置，插入和修改生效
    private long updateUser;
}
