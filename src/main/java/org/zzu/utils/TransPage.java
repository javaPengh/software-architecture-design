package org.zzu.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @className TransPage
 * @description 将分页结果封装为Result对象
 */
@Component
public class TransPage {
    public static Result PageListTOJSON(IPage<?> page,String name) {
        Map<String, Object> list = new HashMap<>();
        list.put("list", page.getRecords());
        list.put("pageNum", page.getCurrent());
        list.put("pageSize", page.getSize());
        list.put("totalPage", page.getPages());
        list.put("totalSize", page.getTotal());

        Map<String, Object> ListMap = new HashMap<>();
        ListMap.put(name, list);

        return Result.ok(ListMap);
    }
}
