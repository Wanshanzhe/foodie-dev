package com.wsz.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author by 完善者
 * @date 2020/10/7 20:36
 * @DESC 编写定时任务的类
 */

@Component
public class OrderJob {

    @Scheduled(cron = "0/3 * * * * ?")
    public void autoCloseOrder(){
        //System.out.println("执行定时任务，当前时间为："+ DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
    }
}
