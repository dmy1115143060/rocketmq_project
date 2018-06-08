package com.rocketmq.trade.common.client;

import com.rocketmq.trade.common.constants.TradeEnums;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author 杜名洋
 * 使用动态代理来实现各个功能对应的接口
 **/
public class RestClientProxyFactoryBeanSpring implements FactoryBean {

    /**
     * 服务接口
     */
    private Class serviceInterface;

    /**
     * 服务器相关的常量枚举类
     */
    private TradeEnums.RestEnum serverEnum;

    /**
     * 负责发送消息的服务器对象
     */
    private RestTemplate restTemplate = new RestTemplate();

    public void setServerEnum(TradeEnums.RestEnum serverEnum) {
        this.serverEnum = serverEnum;
    }

    public void setServiceInterface(Class serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    /**
     * 使用动态代理生成接口的实现类对象，从而来调用服务器端的相关方法
     * @return 实现接口的代理对象
     * @throws Exception
     */
    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(getClass().getClassLoader()
                , new Class[]{serviceInterface}
                , new ClientProxy());
    }

    @Override
    public Class<?> getObjectType() {
        return this.serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 实现InvocationHandler接口，在该方法中实现调用方法
     */
    private class ClientProxy implements InvocationHandler {
        /**
         * 调用代理对象的方法时会执行invoke方法
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            // 使用服务器对象来发送请求，method.getName()的结果即为调用的方法
            return restTemplate.postForObject(serverEnum.getServerUrl() + method.getName()
                    , args[0]
                    , method.getReturnType());
        }
    }
}
