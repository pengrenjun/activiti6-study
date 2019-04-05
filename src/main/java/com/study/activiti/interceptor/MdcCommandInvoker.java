package com.study.activiti.interceptor;

import org.activiti.engine.impl.agenda.AbstractOperation;
import org.activiti.engine.impl.interceptor.CommandInvoker;
import org.activiti.engine.impl.interceptor.DebugCommandInvoker;
import org.activiti.engine.logging.LogMDC;

/**
 * @Description:  Mdc 日志打印拦截器 用于打印流程操作的日志信息
 * @Author：pengrj
 * @Date : 2019/3/27 0027 22:00
 * @version:1.0
 */
public class MdcCommandInvoker extends DebugCommandInvoker {

    @Override
    public void executeOperation(Runnable runnable) {

        boolean mdcEnale=LogMDC.isMDCEnabled();

        LogMDC.setMDCEnabled(true);

        //当前系统线程操作属于工作流的操作
        if (runnable instanceof AbstractOperation) {
            AbstractOperation operation = (AbstractOperation) runnable;

            // Execute the operation if the operation has no execution (i.e. it's an operation not working on a process instance)
            // or the operation has an execution and it is not ended
            if (operation.getExecution() == null || !operation.getExecution().isEnded()) {
                runnable.run();
            }
            else {
                //将流程操作实体的上下文环境存放到Logmdc中
                LogMDC.putMDCExecution(operation.getExecution());
            }


        } else {
            runnable.run();
        }

        LogMDC.clear();
        if(!mdcEnale){
            LogMDC.setMDCEnabled(false);
        }
    }
}
