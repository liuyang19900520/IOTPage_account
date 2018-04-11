package com.liuyang19900520.utils;

import org.springframework.mobile.device.Device;

/**
 * Created by liuyang on 2018/3/29
 *
 * @author liuya
 */
public class TokenUtil {


    private static final String AUDIENCE_UNKNOWN = "unknown";
    private static final String AUDIENCE_WEB = "web";
    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";


    /**
     * 根据用户取得token
     *
     * @param username
     * @return
     */
    public static String getToken(String username) {

        return "";
    }








    /**
     * 通过spring-mobile-device的device检测访问主体
     *
     * @param device
     * @return
     */
    private String generateAudience(Device device) {
        String audience = AUDIENCE_UNKNOWN;
        if (device.isNormal()) {
            //PC端
            audience = AUDIENCE_WEB;
        } else if (device.isTablet()) {
            //平板
            audience = AUDIENCE_TABLET;
        } else if (device.isMobile()) {
            //手机
            audience = AUDIENCE_MOBILE;
        }
        return audience;
    }


}
