package com.anshi.service.impl;


import com.anshi.common.CustomException;
import com.anshi.dao.DishDao;
import com.anshi.dao.DishFlavorDao;
import com.anshi.domain.Dish;
import com.anshi.domain.DishFlavor;
import com.anshi.dto.DishDto;
import com.anshi.service.DishFlavorService;
import com.anshi.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Autowired
    private DishDao dishDao;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时插入菜品对应的口味数据
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        dishDao.insert(dishDto);

        //获得菜品id
        Long dishId = dishDto.getId();

        //菜品口味
        ArrayList<DishFlavor> flavors = dishDto.getFlavors();
//        flavors.stream().map((item) -> {
//            item.setDishId(dishId);
//            return item;
//        }).collect(Collectors.toList());

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }


        //保存口味信息到口味表 dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id获得菜品数据
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = dishDao.selectById(id);
        //查询口味信息，从口味表差

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        ArrayList<DishFlavor> flavors = (ArrayList<DishFlavor>) dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 修改菜品
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        dishDao.updateById(dishDto);

        //清理当前菜品对应口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据，插入口味操作
        ArrayList<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 删除菜品数据，同时删除菜品关联的口味数据
     * @param ids
     */
    @Override
    public void removeWithFlavor(List<Long> ids) {
        //判断菜品是否为起售状态，如果是起售状态则不能删除
        for (Long id : ids) {
            Dish dish = dishDao.selectById(id);
            Integer status = dish.getStatus();
            if(status == 1){
                throw new CustomException("当前菜品为起售状态，不能删除");
            }
        }

        //删除菜品表数据
        dishDao.deleteBatchIds(ids);

        //删除关联的口味表数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,DishFlavor::getDishId,ids);

        dishFlavorService.remove(queryWrapper);
    }
}
