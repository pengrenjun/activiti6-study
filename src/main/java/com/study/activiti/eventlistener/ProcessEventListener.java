package com.study.activiti.eventlistener;


import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description:  全局的流程事件监听器,会监听各个流程事件进行事件的处理
 * @Author：pengrj
 * @Date : 2019/3/30 0030 12:32
 * @version:1.0
 */
public class ProcessEventListener implements ActivitiEventListener {

    private Logger logger=LoggerFactory.getLogger(ProcessEventListener.class);


    @Override
    public void onEvent(ActivitiEvent event) {

        //事件类型
        ActivitiEventType type = event.getType();
        //事件为流程启动
        if(type==ActivitiEventType.PROCESS_STARTED){
            logger.info("{} 流程实例ID为:{} 启动",event.getType(),event.getProcessInstanceId());
        }

        if(type==ActivitiEventType.PROCESS_COMPLETED){

            logger.info("{} 流程实例ID为:{} 启动完成",event.getType(),event.getProcessInstanceId());

        }

    }

    @Override
    public boolean isFailOnException() {
        return false;
    }


}
