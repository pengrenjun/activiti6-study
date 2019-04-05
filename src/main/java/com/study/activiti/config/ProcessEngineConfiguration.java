package com.study.activiti.config;

import com.study.activiti.eventlistener.CustomerEventListener;
import com.study.activiti.interceptor.CustomerPreCommandInteraptor;
import com.study.activiti.interceptor.MdcCommandInvoker;
import com.study.activiti.eventlistener.ProcessEventListener;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.asyncexecutor.DefaultAsyncJobExecutor;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.interceptor.CommandInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

/**
 * @Description: 配置流程引擎的配置文件
 * @Author：pengrj
 * @Date : 2019/3/25 0025 21:38
 * @version:1.0
 */

@Configuration
public class ProcessEngineConfiguration {

    @Bean(name="mysqlEngineConfiguration")
    public StandaloneProcessEngineConfiguration setProcessEngineConfiguration(){
        StandaloneProcessEngineConfiguration processEngineConfiguration=new StandaloneProcessEngineConfiguration();
        processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
        processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/spring-boot-activiti?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true");
        processEngineConfiguration.setJdbcUsername("root");
        processEngineConfiguration.setJdbcPassword("123456");
        processEngineConfiguration.setDatabaseSchemaUpdate("true");

        //配置LogMdc 打印流程过程中的日志信息
        processEngineConfiguration.setCommandExecutor((CommandExecutor)getMdcCommandInvoker());
        //流程引擎历史数据的保存记录默认级别 activiti
        processEngineConfiguration.setHistory(HistoryLevel.ACTIVITY.getKey());
        //配置开启基于事件的DB日志配置
        processEngineConfiguration.setEnableDatabaseEventLogging(true);


        List<ActivitiEventListener> eventListenerList = Collections.emptyList();
        //全局的流程事件监听器
        eventListenerList.add(new ProcessEventListener());
        //自定义事件监听器
        eventListenerList.add(new CustomerEventListener());
        //添加事件监听器
        processEngineConfiguration.setEventListeners(eventListenerList);

        //特定类型事件操作的监听配置
        //setTypedEventListeners(Map<String, List<ActivitiEventListener>> typedListeners) {
        Map<String, List<ActivitiEventListener>> typedListeners=new HashMap<>();
        typedListeners.put(ActivitiEventType.PROCESS_STARTED.name(),eventListenerList);
        processEngineConfiguration.setTypedEventListeners(typedListeners);

        //添加配置命令执行前拦截器
        List<CommandInterceptor > customPreCommandInterceptors=Collections.emptyList();
        customPreCommandInterceptors.add(new CustomerPreCommandInteraptor());
        processEngineConfiguration.setCustomPreCommandInterceptors(customPreCommandInterceptors);

        //配置job执行器
        //激活job执行器
        processEngineConfiguration.setAsyncExecutorActivate(true);
        //配置activit的异步执行器
        processEngineConfiguration.setAsyncExecutor(getAsyncJobExecutor());
        return processEngineConfiguration;
    }



    @Bean(name="asyncJobExecutor")
    public DefaultAsyncJobExecutor getAsyncJobExecutor(){
        DefaultAsyncJobExecutor defaultAsyncJobExecutor=new DefaultAsyncJobExecutor();
        defaultAsyncJobExecutor.setExecutorService(getExecutorService().getObject());
        return defaultAsyncJobExecutor;
    }

    @Bean(name = "executorServiceFactory")
    public ThreadPoolExecutorFactoryBean getExecutorService(){
        ThreadPoolExecutorFactoryBean executorFactoryBean=
                new ThreadPoolExecutorFactoryBean();
        //线程名前缀
        executorFactoryBean.setThreadNamePrefix("activiti-job-");
        //核心线程数
        executorFactoryBean.setCorePoolSize(5);
        //最大线程数
        executorFactoryBean.setMaxPoolSize(100);
        //队列容量
        executorFactoryBean.setQueueCapacity(200);
        //拒绝策略
        executorFactoryBean.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return executorFactoryBean;
    }


    @Bean(name="mdcCommandInvoker")
    public MdcCommandInvoker getMdcCommandInvoker(){
        return new MdcCommandInvoker();
    }



}
