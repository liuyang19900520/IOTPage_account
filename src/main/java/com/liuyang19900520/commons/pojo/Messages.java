package com.liuyang19900520.commons.pojo;

/**
 * Message类别
 * @author liuya
 */

public enum Messages {

    //HttpStatus业务相关，负责exception的管理

    OK(200, "请求成功"),
    BAD_REQUEST(400, "请求出错"),
    UNAUTHORIZED(401, "未通过认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "未找到资源"),
    INTERNAL_SERVER_ERROR(500, "服务器出错");

    private final int value;

    private final String msg;

    Messages(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public int value() {
        return value;
    }

    public String msg() {
        return msg;
    }
}
