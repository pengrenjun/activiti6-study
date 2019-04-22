package com.study.activiti.interceptor;

import org.activiti.engine.impl.interceptor.AbstractCommandInterceptor;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:  自定义的activivit默认命令执行前的拦截器 打印命令的执行时间
 * @Author：pengrj
 * @Date : 2019/3/30 0030 15:42
 * @version:1.0
 */
public class CustomerPreCommandInteraptor extends AbstractCommandInterceptor {

    Logger logger=LoggerFactory.getLogger(this.getClass());

    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {

        long startTime=System.currentTimeMillis();

        try {
            return getNext().execute(config,command);
        } finally {
            long durationTime=System.currentTimeMillis()-startTime;
            logger.info("{} 执行时间 {} 毫秒",command.getClass().getName(),durationTime );
        }
    }
}
