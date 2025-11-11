package org.zzu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.zzu.pojo.Hall;
import org.zzu.pojo.PortalVo;
import org.zzu.service.HallService;
import org.zzu.utils.Result;
import org.zzu.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @className HallController
 * @description 影厅表的增删改查操作
 */
@RestController
@RequestMapping("hall")
@CrossOrigin
public class HallController {

    @Autowired
    private HallService hallService;

    @GetMapping
    public Result getTotalList() {
        LambdaQueryWrapper<Hall> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(Hall::getHid, Hall::getHname, Hall::getRowNum, Hall::getRowCapacity, Hall::getScreenType);
        return Result.ok(hallService.list(lambdaQueryWrapper));
    }

    @PostMapping
    public Result showHallList(@RequestBody PortalVo portalVo) {
        return hallService.showHallList(portalVo);
    }

    @PutMapping
    public Result saveOrUpdate(@RequestBody Hall hall) {
        try {
            if (hallService.saveOrUpdate(hall)) {
                return Result.ok(null);
            }
            return Result.build(null, ResultCodeEnum.UPDATE_FAILED);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.UPDATE_FAILED);
        }
    }

    @DeleteMapping("/{hid}")
    public Result delete(@PathVariable Integer hid) {
        try {
            if (hallService.removeById(hid)) {
                return Result.ok(null);
            }
            return Result.build(null, ResultCodeEnum.DELETE_FAILED);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.DELETE_FAILED);
        }
    }
}
