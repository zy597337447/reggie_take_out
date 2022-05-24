package com.zhang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.reggie.common.R;
import com.zhang.reggie.entity.Category;
import com.zhang.reggie.service.CategoryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 1.页面backend/page/category/list.html发送ajax请求，将新增分类窗口输入的数据以json的形式提交到服务端
     * 2.服务端Controller接收页面提供的数据并调用Service将数据进行保存
     * 3.Service调用Mapper操作数据库，保存数据
     */


    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }


    @RequestMapping("/page")
    public R<Page> page(int page,int pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> qw= new LambdaQueryWrapper<>();
        qw.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,qw);
        return R.success(pageInfo);

    }


    /**
     * 删除分类
     * 1.页面发送ajax请求，将参数(id)提交到服务端
     * 2.服务端controller接收页面提交的数据调用service删除数据
     * 3.service调用mapper操作数据库
     */


    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id){
        log.info("删除分类，id:{}",id);
        categoryService.remove(id);

        return R.success("分类信息删除成功");
    }


    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类：{}",category);
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> qw  = new LambdaQueryWrapper<>();
        qw.eq(category.getType()!=null,Category::getType,category.getType());
        qw.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        List<Category> list = categoryService.list(qw);
        return R.success(list);
    }
}
