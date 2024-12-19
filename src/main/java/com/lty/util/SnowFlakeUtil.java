package com.lty.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 唯一id生成器(19位)
 * @author lty
 */
@Slf4j
public class SnowFlakeUtil {
    /**
     * 派号器workId：0~31
     * 机房datacenterId：0~31
     */
    private static Snowflake snowflake = IdUtil.createSnowflake(1, 1);

    public static Long nextId() {
        return snowflake.nextId();
    }

    /** 测试生成id */
    public static void main(String[] args) {
        Long aLong = SnowFlakeUtil.nextId();
        System.out.println(aLong);
    }
}
