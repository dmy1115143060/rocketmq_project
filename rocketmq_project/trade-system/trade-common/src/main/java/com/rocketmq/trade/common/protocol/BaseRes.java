package com.rocketmq.trade.common.protocol;

/**
 * @author 杜名洋
 **/
public class BaseRes {
    private String returnCode;
    private String returnInfo;

    public String getReturnCode() {
        return returnCode;
    }

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }
}
