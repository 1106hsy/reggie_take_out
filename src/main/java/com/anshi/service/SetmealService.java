package com.anshi.service;


import com.anshi.common.R;
import com.anshi.domain.Setmeal;
import com.anshi.dto.DishDto;
import com.anshi.dto.SetmealDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐，同时插入套餐菜品表
    void saveWithDish(SetmealDto setmealDto);

    //删除套餐
    void removeSetmeal(List<Long> ids);

    //修改套餐，回显数据
    SetmealDto getByIdWithDish(Long id);

    //修改套餐，保存数据
    void updateWithSetmealDish(SetmealDto setmealDto);
}
