package com.rocketmq.trade.user.server;

import com.rocketmq.trade.common.constants.TradeEnums;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author 杜名洋
 * 使用Jetty服务器上传用户提供的服务，响应其他模块提交的请求
 **/
public class UserRestServer {
    public static void main(String[] args) throws Exception {
        // server绑定的端口
        Server server = new Server(TradeEnums.RestEnum.USER.getServerPort());
        // 设置全局环境
        ServletContextHandler springContextHandler = new ServletContextHandler();
        springContextHandler.setContextPath("/" + TradeEnums.RestEnum.USER.getContextPath());
        // 设置配置的xml文件
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setConfigLocation("classpath:xml/spring-web-user.xml");
        // 设置listener
        springContextHandler.addEventListener(new ContextLoaderListener(context));
        // 添加servlet
        springContextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)),"/*");
        server.setHandler(springContextHandler);
        server.start();
        server.join();
    }
}
