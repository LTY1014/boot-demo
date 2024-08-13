package com.lty.config.MybatisPlus;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.lty.util.SerialUtil;


public class CustomIdGenerator implements IdentifierGenerator  {

    @Override
    public Long nextId(Object entity) {
        String serialId = SerialUtil.generateSerial();
        return Long.valueOf(serialId);
    }
}
