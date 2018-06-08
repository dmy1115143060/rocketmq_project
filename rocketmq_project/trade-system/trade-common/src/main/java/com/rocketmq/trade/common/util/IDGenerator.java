package com.rocketmq.trade.common.util;

import java.util.UUID;

/**
 * @author 杜名洋
 **/
public class IDGenerator {
    public static String generatorUUID() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
