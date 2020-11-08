package com.wsz.controller;

import com.wsz.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author by 完善者
 * @date 2020/7/26 21:25
 * @DESC
 */
@RestController
@ApiIgnore
public class StuFooController {

    @Autowired
    private StuService stuService;

    @RequestMapping("/getStu")
    public Object getStu(Integer id){
        return stuService.getStuInfo(id);
    }

    @PostMapping("/saveStu")
    public Object saveStu(){
        stuService.saveStu();
        return "ok";
    }

    @PutMapping("/updateStu")
    public Object updateStu(Integer id){
        stuService.updateStu(id);
        return "ok";
    }

    @DeleteMapping("/deleteStu")
    public Object deleteStu(Integer id){
        stuService.deleteStu(id);
        return "ok";
    }
}
