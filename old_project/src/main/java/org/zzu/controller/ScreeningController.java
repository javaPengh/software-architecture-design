package org.zzu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.zzu.pojo.PortalVo;
import org.zzu.pojo.Screening;
import org.zzu.service.ScreeningService;
import org.zzu.utils.Result;
import org.zzu.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @className ScreeningController
 * @description 排片表的增删改查操作
 */
@RestController
@RequestMapping("screening")
@CrossOrigin
public class ScreeningController {

    @Autowired
    private ScreeningService screeningService;

    @GetMapping
    public Result getTotalList() {
        LambdaQueryWrapper<Screening> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Screening::getSid, Screening::getMid, Screening::getHid, Screening::getShowTime, Screening::getPrice, Screening::getRemainingSeats, Screening::getSeatCount);
        return Result.ok(screeningService.list(wrapper));
    }

    @PostMapping
    public Result showScreeningList(@RequestBody PortalVo portalVo) {
        return screeningService.showScreeningList(portalVo);
    }

    @PostMapping("dtoList")
    public Result getScreeningDtoList(@RequestBody PortalVo portalVo) {
        return screeningService.getScreeningDtoList(portalVo);
    }

    @PutMapping("insert")
    public Result insert(@RequestBody Screening screening) {
        try {
            if(screeningService.insertData(screening)){
                return Result.ok(null);
            }else{
                return Result.build(null, ResultCodeEnum.SCREENING_CONFLICT);
            }
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.UPDATE_FAILED);
        }
    }

    @PutMapping("update")
    public Result update(@RequestBody Screening screening) {
        try {
            if(screeningService.updateData(screening)){
                return Result.ok(null);
            }else{
                return Result.build(null, ResultCodeEnum.SCREENING_CONFLICT);
            }
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.UPDATE_FAILED);
        }
    }

    @DeleteMapping("/{sid}")
    public Result delete(@PathVariable Integer sid) {
        try {
            if (screeningService.removeById(sid)) {
                return Result.ok(null);
            }
            return Result.build(null, ResultCodeEnum.DELETE_FAILED);
        } catch (Exception e) {
            return Result.build(null, ResultCodeEnum.DELETE_FAILED);
        }
    }
}
