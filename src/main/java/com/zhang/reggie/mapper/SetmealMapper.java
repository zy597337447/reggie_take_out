package com.zhang.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhang.reggie.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
}
