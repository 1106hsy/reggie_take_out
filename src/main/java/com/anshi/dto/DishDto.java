package com.anshi.dto;

import com.anshi.domain.Dish;
import com.anshi.domain.DishFlavor;
import lombok.Data;

import java.util.ArrayList;


@Data
public class DishDto extends Dish {
    private ArrayList<DishFlavor> flavors;

    private String categoryName;

    private Integer copies;
}
