package com.anshi.dto;

import com.anshi.domain.Setmeal;
import com.anshi.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
