package com.zhang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.reggie.entity.Dish;
import com.zhang.reggie.dto.DishDto;
import com.zhang.reggie.entity.DishFlavor;
import com.zhang.reggie.mapper.DishMapper;
import com.zhang.reggie.service.DishFlavorService;
import com.zhang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.ls.LSException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品的同时保存对应的口味信息
     * @param dishDto
     */

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        //把dishId放到dishflavor表中
        Long dishId = dishDto.getId();
        //拿到菜品口味
        List<DishFlavor> dishFlavors = dishDto.getDishFlavors();
        //对集合赋值 可以使用循环或者stream流
        //dish_flavors前端数据只封装了name，value，表中需要该数据，所以手动添加
        dishFlavors = dishFlavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(dishDto.getDishFlavors());
    }


    /**
     * 根据id查询菜品信息和对应的口味，
     * 可实现修改菜品页面菜品信息的回显
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(qw);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setDishFlavors(list);
        log.info(dishDto.toString());
        return dishDto;
    }


    /**
     * 更新菜品信息的同时还更新对应的口味信息
     * 分两步更新：
     * 1.对单表菜品信息更新
     * 2.删除旧口味信息，添加新口味信息，更新口味表
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(qw);

        List<DishFlavor> list = dishDto.getDishFlavors();
        list = list.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(list);


    }
}
