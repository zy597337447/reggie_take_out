package com.zhang.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.reggie.common.R;
import com.zhang.reggie.entity.Employee;
import com.zhang.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1.将页面拿到的password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面拿到的用户名username查询数据库

        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(qw);
        //3.如果没有查到则返回登录失败
        if (emp == null){
            return R.error("没有该用户名，登录失败");
        }

//        4.比对密码，如果不一致返回登录失败
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误，登录失败");
        }

        //5.查看员工状态，如果是禁用状态，则返回员工已禁用
        if (emp.getStatus() == 0){
            return R.error("员工账号已禁用");
        }
        //6.登陆成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出登录成功");
    }


    @PostMapping
    public R<String> save(HttpServletRequest request ,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置默认初始密码123456，需要进行md5加密
        employee.setPassword("123456");
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        long employeeId = (long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(employeeId);
//        employee.setUpdateUser(employeeId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //过滤条件
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //排序条件
        qw.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,qw);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
//        Long id = Thread.currentThread().getId();
//        log.info("线程id：{}",id);
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据Id查询员工");
        Employee employee = employeeService.getById(id);
        if (employee!=null) {
            return R.success(employee);
        }
        return R.error("没有对应员工信息");
    }



}
