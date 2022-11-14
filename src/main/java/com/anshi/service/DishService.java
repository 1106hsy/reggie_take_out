package com.anshi.service;


import com.anshi.domain.Dish;
import com.anshi.dto.DishDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表，dish和dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //获得菜品数据
    DishDto getByIdWithFlavor(Long id);

    //修改菜品数据
    void updateWithFlavor(DishDto dishDto);

    //删除菜品数据，同时删除菜品关联的口味数据
    void removeWithFlavor(List<Long> ids);
}
