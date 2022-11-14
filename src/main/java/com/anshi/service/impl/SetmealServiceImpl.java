package com.anshi.service.impl;


import com.anshi.common.CustomException;
import com.anshi.common.R;
import com.anshi.dao.SetmealDao;
import com.anshi.domain.Setmeal;
import com.anshi.domain.SetmealDish;
import com.anshi.dto.DishDto;
import com.anshi.dto.SetmealDto;
import com.anshi.service.SetmealDishService;
import com.anshi.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDao setmealDao;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息到套餐表
        setmealDao.insert(setmealDto);

        //获取套餐id
        Long setmealId = setmealDto.getId();

        //获取套餐菜品集合
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }

        //保存套餐菜品信息到套餐菜品表
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    public void removeSetmeal(List<Long> ids) {

        //循环查看商品状态判断是否能删除
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,"1");  //售卖中

        Integer count = setmealDao.selectCount(queryWrapper);
        if(count > 0){  //不能删除
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //可以删除
        //根据ids删除套餐表
        setmealDao.deleteBatchIds(ids);
        //根据ids删除套餐菜品关联表
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(ids != null,SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishLambdaQueryWrapper);

    }

    /**
     * 修改套餐，回显数据
     * @param id
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //获取套餐数据
        Setmeal setmeal = setmealDao.selectById(id);

        //对象拷贝，把setmeal对象拷贝给setmealDto对象
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //获取菜品数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(list);

        return setmealDto;

    }

    /**
     * 修改套餐，保存数据
     * @param setmealDto
     */
    @Override
    public void updateWithSetmealDish(SetmealDto setmealDto) {
        //修改套餐表数据
        setmealDao.updateById(setmealDto);

        //修改套餐菜品关联表
        //先删除套餐菜品关联表数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //查询符合条件的SetmealDish对象
        queryWrapper.eq(setmealDto != null,SetmealDish::getSetmealId,setmealDto.getId());
        //删除数据
        setmealDishService.remove(queryWrapper);

        //再新增当前提交的套餐菜品关联表数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //赋给每个setmealDish对象setmeal的id
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        //新增数据
        setmealDishService.saveBatch(setmealDishes);

    }
}
