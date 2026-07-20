package com.lty.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lty.constant.BaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础客户数据服务
 * <p>
 * 从本地文件（JSON + Excel）加载数据，
 * 启动时通过 {@link PostConstruct} 初始化，后续提供只读视图。
 */
@Slf4j
@Service
public class BasicCustomerService {

    /** 客户型号 映射 */
    private Map<String, String> customerMap = new HashMap<>();

    /** 工厂配置 Key → 配置对象 映射 */
    private Map<String, String> factoryConfigMap = new HashMap<>();

    /** 持久化文件路径 */
    public static final String JSON_FILE = Paths.get(BaseConstant.PROJECT_ROOT_DIRECTORY, "project.json").toString();

    /** 客户型号对应表路径 */
    public static final String BASIC_CUSTOMER_FILE = Paths.get(BaseConstant.PROJECT_ROOT_DIRECTORY, "型号对应表.xlsx").toString();

    // ========== 初始化 ==========

    @PostConstruct
    public void init() {
        loadFactoryConfig();
        loadCustomerMap();
        log.info("data init success");
    }

    // ========== 工厂配置 ==========

    /**
     * 从 JSON 文件加载工厂配置
     */
    @SuppressWarnings("unchecked")
    private void loadFactoryConfig() {
        File file = new File(JSON_FILE);
        if (!file.exists()) {
            System.out.println("数据文件不存在，使用空队列初始化");
            factoryConfigMap = new HashMap<>();
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> data = mapper.readValue(file, new TypeReference<>() {
            });
            factoryConfigMap = data != null ? data : new HashMap<>();
        } catch (Exception e) {
            System.out.println("loadFactoryConfig error");
            e.printStackTrace();
            factoryConfigMap = new HashMap<>();
        }
    }

    /**
     * 重新加载工厂配置（接收上传文件后调用）
     */
    public Map<String, String> reloadFactoryConfig() {
        loadFactoryConfig();
        return getFactoryConfigMap();
    }

    /**
     * 获取工厂配置的只读视图
     */
    public Map<String, String> getFactoryConfigMap() {
        return Collections.unmodifiableMap(factoryConfigMap);
    }

    // ========== 客户型号映射 ==========

    /**
     * 从 Excel 文件加载客户型号映射
     */
    public void loadCustomerMap() {
        if (!new File(BASIC_CUSTOMER_FILE).exists()) {
            System.out.println("对应表文件不存在，使用空队列初始化");
            customerMap = new HashMap<>();
            return;
        }
        // 正常应该读取 Excel 文件
        List<Map<String, String>> maps = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        customerMap = map;
    }

    /**
     * 获取客户型号映射的只读视图
     */
    public Map<String, String> getCustomerMap() {
        return Collections.unmodifiableMap(customerMap);
    }

}
