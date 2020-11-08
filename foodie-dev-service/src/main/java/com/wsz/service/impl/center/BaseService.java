package com.wsz.service.impl.center;

import com.github.pagehelper.PageInfo;
import com.wsz.utils.PagedGridResult;

import java.util.List;

/**
 * @author by 完善者
 * @date 2020/10/11 20:31
 * @DESC
 */
public class BaseService {

    public PagedGridResult setterPagedGrid(List<?> list, Integer page){
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }

}
