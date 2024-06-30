package com.lty.service;

import com.alibaba.excel.EasyExcel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EasyExcelTest {

    @Test
    public void test() {
        String filename = "D:\\Desktop\\test.xlsx";
        List<Map<Integer, String>> totalList =
                EasyExcel.read(filename).sheet().headRowNumber(0).doReadSync();
        System.out.println(totalList);
    }
}