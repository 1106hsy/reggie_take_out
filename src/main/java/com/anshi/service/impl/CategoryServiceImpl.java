package com.anshi.service.impl;


import com.anshi.common.CustomException;
import com.anshi.dao.CategoryDao;
import com.anshi.domain.Category;
import com.anshi.domain.Dish;
import com.anshi.domain.Setmeal;
import com.anshi.service.CategoryService;
import com.anshi.service.DishService;
import com.anshi.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryDao categoryDao;
    /**
     * 删除菜品，套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果已关联则抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);

        if(dishCount > 0){
            //已关联,抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除-_-");

        }
        //查询当前分类是否关联了套餐，如果已关联则抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);

        if(setmealCount > 0){
            //已关联，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除-_-");
        }

        //与菜品，套餐都无关联,正常删除操作
        categoryDao.deleteById(id);
    }
}
