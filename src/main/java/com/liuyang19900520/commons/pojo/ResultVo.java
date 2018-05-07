package com.liuyang19900520.commons.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * rest接口返回数据对象
 *
 * @author liuya
 */

@Data
public class ResultVo {
    /**
     * 结果-1 失败，1-成功
     */

    private int ret;
    /**
     * 结果代码
     */

    private int code;
    /**
     * 结果消息
     */

    private String msg;
    /**
     * 结果数据
     */

    private Object data;


    private long timestamp;

    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;

    public ResultVo(int ret, int code, String msg, Object data, long timestamp) {
        super();
        this.ret = ret;
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = timestamp;
    }

    public static ResultVo success(Messages messages, Object data) {
        return new ResultVo(SUCCESS, messages.value(), messages.msg(), data, System.currentTimeMillis());
    }

    public static ResultVo error(Messages messages, Object data) {
        return new ResultVo(FAILURE, messages.value(), messages.msg(), data, System.currentTimeMillis());
    }

}
