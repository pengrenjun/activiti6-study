package com.study.activiti.eventlistener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;

/**
 * @Description: 定义自定义类型的事件监听器
 * @Author：pengrj
 * @Date : 2019/3/30 0030 14:05
 * @version:1.0
 */
public class CustomerEventListener implements ActivitiEventListener {
    @Override
    public void onEvent(ActivitiEvent event) {

        if(event.getType()==ActivitiEventType.CUSTOM){
            System.out.println("自定义事件监听启动处理"+event.getProcessInstanceId());
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
