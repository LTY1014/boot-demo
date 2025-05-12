package com.lty.service;

import com.lty.util.QLExpress.QLExpressService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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

    @Test
    public void testOrderDiscount() {
        // 订单金额、产品类型和会员等级
        BigDecimal originalAmount = new BigDecimal("5000");
        String productType = "ELECTRONICS";
        int vipLevel = 5;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", originalAmount);
        params.put("productType", productType);
        params.put("vipLevel", vipLevel);

        // 执行规则并累计折扣
        Double totalDiscount = 0.0;
        for (String rule : getRules()) {
            Object result = qlExpressService.executeExpression(rule, params);
            totalDiscount += ((Number) result).doubleValue();
        }

        // 转换为 BigDecimal
        BigDecimal finalDiscount = BigDecimal.valueOf(totalDiscount);

        System.out.println("最终折扣金额：" + finalDiscount + "元");
        System.out.println("实际支付金额：" + originalAmount.subtract(finalDiscount) + "元");
    }

    public List<String> getRules(){
        // 规则1：满1000元打9折，且会员等级大于2
        String rule1 = "amount >= 1000 && vipLevel > 2 ? amount * 0.1 : 0";
        // 规则2：电子产品额外优惠50元
        String rule2 = "productType == 'ELECTRONICS' ? 50 : 0";
        // 规则3：VIP5用户再减100元
        String rule3 = "vipLevel == 5 ? 100 : 0";
        return List.of(rule1,rule2,rule3);
    }
}
