package com.wsz.service;

import com.wsz.pojo.Stu;

/**
 * @author by 完善者
 * @date 2020/8/5 22:20
 * @DESC
 */
public interface StuService {

    public Stu getStuInfo(Integer id);

    public void saveStu();

    public void updateStu(Integer id);

    public void deleteStu(Integer id);
}
