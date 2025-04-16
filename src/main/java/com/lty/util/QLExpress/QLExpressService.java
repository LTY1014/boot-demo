package com.lty.util.QLExpress;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class QLExpressService {

    @Resource
    private ExpressRunner expressRunner;

    @Resource
    private DefaultContext<String, Object> defaultContext;

    public Object executeExpression(String expression, Map<String, Object> params) {
        try {
            // 将参数放入上下文
            defaultContext.putAll(params);
            // 执行表达式
            return expressRunner.execute(expression, defaultContext, null, true, false);
        } catch (Exception e) {
            log.error("Error executing QLExpress expression: {}", expression);
            return null;
        }
    }
}