package com.lty.service;

import cn.hutool.system.SystemUtil;
import org.junit.Test;

/**
 *
 * @author lty
 */
public class HuToolTest {

    @Test
    public void testSystemUtil() {
        System.out.println(SystemUtil.getJvmSpecInfo().getVersion());
        System.out.println(SystemUtil.getOsInfo());
        System.out.println(SystemUtil.getUserInfo());
        System.out.println(SystemUtil.getHostInfo());
        System.out.println(SystemUtil.getRuntimeInfo());
    }
}
