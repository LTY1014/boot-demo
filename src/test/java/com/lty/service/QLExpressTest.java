package com.lty.service;

import com.lty.util.QLExpress.QLExpressService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QLExpressTest {

    @Resource
    public QLExpressService qlExpressService;

    @Test
    public void test() throws Exception {
        String expression = "(a + b) * c";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", 10);
        paramMap.put("b", 5);
        paramMap.put("c", 2);
        Object o = qlExpressService.executeExpression(expression, paramMap);
        Assert.assertEquals(30, o);
    }

    @Test
    public void test2() throws Exception {
        String expression = "if (a>b) then {return a;} else {return b;}";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", 10);
        paramMap.put("b", 20);
        Object o = qlExpressService.executeExpression(expression, paramMap);
        System.out.println(o);
    }
}
