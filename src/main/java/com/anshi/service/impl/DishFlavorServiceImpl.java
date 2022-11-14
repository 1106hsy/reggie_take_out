package com.anshi.service.impl;


import com.anshi.dao.DishFlavorDao;
import com.anshi.domain.DishFlavor;
import com.anshi.service.DishFlavorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorDao, DishFlavor> implements DishFlavorService {
}
