package net.fangyi.acmsb.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String getNowTime() {
        // 获取当前日期和时间
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化输出
        return currentDateTime.format(formatter);
    }
}
