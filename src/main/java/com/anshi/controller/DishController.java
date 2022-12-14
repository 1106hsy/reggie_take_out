package com.anshi.controller;

import com.anshi.common.R;
import com.anshi.domain.Category;
import com.anshi.domain.Dish;
import com.anshi.domain.DishFlavor;
import com.anshi.dto.DishDto;
import com.anshi.service.CategoryService;
import com.anshi.service.DishFlavorService;
import com.anshi.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Dish::getName,name);
        queryWrapper.orderByAsc(Dish::getSort);

        //执行查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = new ArrayList<>();

        DishDto dishDto;
        for (Dish record : records) {
            dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);

            Long categoryId = record.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            list.add(dishDto);
        }

//        List<DishDto> list = records.stream().map((item) -> {
//
//            DishDto dishDto = new DishDto();
//
//            BeanUtils.copyProperties(item,dishDto);
//
//            Long categoryId = item.getCategoryId();
//
//            Category category = categoryService.getById(categoryId);
//            if(category != null){
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
//
//            return dishDto;
//        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 获得菜品数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据分类id查询菜品
     * @param
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        log.info(dish.toString());
//        //构造条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //根据条件查询,查询菜品id和分类id相同的
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        //查询菜品状态为1的（起售）
//        queryWrapper.eq(Dish::getStatus,1);
//        //排序
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishList = dishService.list(queryWrapper);
//
//        return R.success(dishList);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        log.info(dish.toString());
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据条件查询,查询菜品id和分类id相同的
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //查询菜品状态为1的（起售）
        queryWrapper.eq(Dish::getStatus,1);
        //排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> records = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = new ArrayList<>();

        DishDto dishDto;
        for (Dish record : records) {
            dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);

            Long categoryId = record.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            Long dishId = record.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors((ArrayList<DishFlavor>) dishFlavors);

            dishDtoList.add(dishDto);
        }

        return R.success(dishDtoList);
    }

    /**
     * 菜品停售起售
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,@RequestParam List<Long> ids){
        log.info("ids:" + ids);
        log.info("status:"+ status);
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);  //查询
        List<Dish> dishList = dishService.list(queryWrapper);

        for (Dish dish : dishList) {

            dish.setStatus(status);
            dishService.updateById(dish);

        }

        return R.success("修改售卖状态成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids){
        dishService.removeWithFlavor(ids);
        return R.success("删除菜品成功");
    }
}
