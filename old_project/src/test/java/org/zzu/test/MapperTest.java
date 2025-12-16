package org.zzu.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.zzu.mapper.ScreeningMapper;
import org.zzu.mapper.TicketMapper;
import org.zzu.vo.PortalVo;
import org.zzu.dto.ScreeningDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @className MapperTest
 * @description 映射层测试类
 */
@Slf4j
@SpringBootTest
public class MapperTest {
    @Autowired
    private ScreeningMapper screeningMapper;
    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration;

    @Test
    public void getScreeningDtoList() throws ParseException {
        PortalVo portalVo = new PortalVo();
        portalVo.setKeyword("aa");
        portalVo.setPageNum(1);
        portalVo.setPageSize(10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        portalVo.setDate(sdf.parse("2024-08-29"));
        IPage<ScreeningDto> page = new Page<>(portalVo.getPageNum(), portalVo.getPageSize());
        screeningMapper.getScreeningDtoList(page, portalVo);
        Map<String,Object> sDnoList =new HashMap<>();
        sDnoList.put("list",page.getRecords());
        sDnoList.put("pageNum",page.getCurrent());
        sDnoList.put("pageSize",page.getSize());
        sDnoList.put("totalPage",page.getPages());
        sDnoList.put("totalSize",page.getTotal());

        Map<String,Object> sDnoListMap =new HashMap<>();
        sDnoListMap.put("sDnoList",sDnoList);
        System.out.println(sDnoListMap.toString());
    }
}
