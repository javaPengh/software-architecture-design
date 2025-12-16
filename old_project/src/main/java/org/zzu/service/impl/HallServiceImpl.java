package org.zzu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.zzu.pojo.Hall;
import org.zzu.vo.PortalVo;
import org.zzu.service.HallService;
import org.zzu.mapper.HallMapper;
import org.zzu.utils.Result;
import org.zzu.utils.TransPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Chen
 * @description 针对表【hall】的数据库操作Service实现
 * @createDate 2024-08-10 10:10:22
 */
@Service
public class HallServiceImpl extends ServiceImpl<HallMapper, Hall>
        implements HallService {
    @Autowired
    private HallMapper hallMapper;
    @Override
    public Result showHallList(PortalVo portalVo) {
        IPage<Hall> page = new Page<>(portalVo.getPageNum(), portalVo.getPageSize());
        if(portalVo.getKeyword() != null){
            LambdaQueryWrapper<Hall> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Hall::getHname, portalVo.getKeyword());
            hallMapper.selectPage(page, wrapper);
        }
        else {
            hallMapper.selectPage(page, null);
        }
        return TransPage.PageListTOJSON(page, "hallList");
    }
}




