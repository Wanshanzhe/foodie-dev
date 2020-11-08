package com.wsz.controller;

import com.wsz.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author by 完善者
 * @date 2020/7/26 21:25
 * @DESC
 */

@ApiIgnore
@RestController
@RequestMapping("redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public Object set(String key,String value){
//        redisTemplate.opsForValue().set(key,value);
        redisOperator.set(key,value);
        return "ok";
    }

    @GetMapping("/get")
    public String get(String key){
        //        return (String) redisTemplate.opsForValue().get(key);
        redisOperator.get(key);
        return redisOperator.get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key){
//        redisTemplate.delete(key);
        redisOperator.del(key);
        return "ok";
    }
}
