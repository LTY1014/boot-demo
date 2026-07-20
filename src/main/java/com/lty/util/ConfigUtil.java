package com.lty.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lty.constant.BaseConstant;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 持久化配置类
 */
public class ConfigUtil {

    // 配置项
    public static final Map<String, Object> config = new HashMap<>();

    // 持久化文件路径
    public static final String DATA_FILE = Paths.get(BaseConstant.PROJECT_ROOT_DIRECTORY, "config.json").toString();

    // 静态块，类加载时执行，初始化配置
    static {
        loadFromFile();
    }

    /**
     * 保存静态变量到本地文件，序列化到JSON文件
     * 注意业务处理最后要调用保存数据到文件方法
     */
    public static void saveToFile() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = new HashMap<>();

            data.putAll(config);
            // 确保父目录存在
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            System.out.println("数据已保存到: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("保存数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 从本地文件恢复静态变量值
     * 从JSON文件反序列化
     */
    @SuppressWarnings("unchecked")
    private static void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("数据文件不存在，使用空队列初始化");
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(file, new TypeReference<>() {
            });

            if (data != null) {
                config.putAll(data);
                System.out.println("加载数据成功: " + config);
            }
        } catch (Exception e) {
            System.err.println("加载数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 清空所有静态队列数据（用于重置或测试）
     */
    public static void clear() {
        config.clear();
        saveToFile();
        System.out.println("已清空");
    }
}
