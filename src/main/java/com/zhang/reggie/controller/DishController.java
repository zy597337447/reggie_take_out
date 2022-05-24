package com.zhang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.reggie.common.R;
import com.zhang.reggie.dto.DishDto;
import com.zhang.reggie.entity.Category;
import com.zhang.reggie.entity.Dish;
import com.zhang.reggie.entity.DishFlavor;
import com.zhang.reggie.service.CategoryService;
import com.zhang.reggie.service.DishFlavorService;
import com.zhang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping
    public R<String> adddish(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增分类成功");

    }

    /**
     * 要实现分页功能，不能简单地构建分页对象，然后对数据库查询返回分页结果，因为在Dish表中没有类别名字只有类别id
     * 为了避免MP中使用多表查询，单表查询Dish，得到没有包含类别名的分页结果.
     * 分页对象中records包含表显的所有信息，将dishPage除records外的所有信息拷贝至dishDtoPage
     * 再针对records进行处理
     * 使用stream方式对records逐个查询增加类名信息，最后collect得到DishDtolist
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name ){
        //菜品分页
        Page<Dish> dishPage = new Page<>(page,pageSize);
        //带有菜品类名字的数据传输对象分页
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.like(name!=null,Dish::getName,name);
        qw.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,qw);


        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        List<Dish> records = dishPage.getRecords();
        List<DishDto> dishDtoRecords = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoRecords);

        return R.success(dishDtoPage);
    }



    @GetMapping("/{id}")
    public R<DishDto> getdish(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }
}
