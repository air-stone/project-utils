package com.air.project.utils.ip;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * @author yanghao
 * @date 2014年10月2日 下午3:18:14
 */
public class IPHelper {

    /**
     * @category 判断一个ip地址是不是ipv4
     * @param ipAddress
     * @return 指定的字符串是否为ipv4地址
     */
    public static boolean isIpV4(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
}
