package com.zhang.reggie.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.zhang.reggie.entity.Dish;
import com.zhang.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    @JsonProperty("flavors")
    private List<DishFlavor> dishFlavors = new ArrayList<>();
    private String categoryName;
    private Integer copies;
}
