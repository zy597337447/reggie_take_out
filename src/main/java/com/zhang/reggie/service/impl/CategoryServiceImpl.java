package com.zhang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.reggie.common.CustomException;
import com.zhang.reggie.entity.Category;
import com.zhang.reggie.entity.Dish;
import com.zhang.reggie.entity.Setmeal;
import com.zhang.reggie.mapper.CategoryMapper;
import com.zhang.reggie.service.CategoryService;
import com.zhang.reggie.service.DishService;
import com.zhang.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private SetmealService setmealservice;
    @Autowired
    private DishService dishService;

    /**
     * 根据id删除分类，删除之前需要判断当前分类是否关联了套餐
     */
    @Override
    public void remove( Long ids){
        //添加查询条件
          LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(Dish::getCategoryId,ids);
        int count1 = dishService.count(qw);
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个异常

        if (count1>0){
            //已经关联了菜品，跑出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> qw2 = new LambdaQueryWrapper<>();
        qw2.eq(Setmeal::getCategoryId,ids);
        int count2 = setmealservice.count(qw2);
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个异常
        if (count2>0){
            //已经关联了套餐，跑出一个业务异常
        }
        //正常删除分类
        super.removeById(ids);
    }




}
