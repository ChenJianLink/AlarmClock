package cn.chenjianlink.android.alarmclock.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author chenjian
 * 配置文件读写工具类
 */
public class PropertiesUtil {
    /**
     * 配置文件读取封装
     */
    private static Properties properties;

    static {
        properties = new Properties();
        InputStream in = PropertiesUtil.class.getResourceAsStream("/assets/config.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取config.properties文件下的配置信息
     * @param configName 配置名称
     * @return String属性的配置内容
     */
    public static String getConfig(String configName){
        return properties.getProperty(configName);
    }

}
