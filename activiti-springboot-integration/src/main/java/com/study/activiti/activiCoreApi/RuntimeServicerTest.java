package com.study.activiti.activiCoreApi;

import com.study.activiti.ActivitiApplication;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.runtime.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 流程运行控制服务RuntimeService测试
 * @Author：pengrj
 * @Date : 2019/4/22 0022 21:20
 * @version:1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes ={ActivitiApplication.class} )
public class RuntimeServicerTest {

    @Resource(name="runtimeService")
    private RuntimeService runtimeService;

    //测试启动流程
    @Test
    public void testA(){

        //根据流程部署文件的key启动流程
        //每次部署流程定义文件会生成不同的历史版本,根据流程key启动默认根据最新的版本发起流程
        Map<String,Object> variables=new HashMap<>();
        variables.put("salecode",78915979);

        //常用的流程启动方式
        //通过流程定义的key 业务编号 流程变量启动流程 还有其他多种的启动方式 选择性的传入参数
        ProcessInstance processInstance = runtimeService.
                startProcessInstanceByKey("myProcess", "businesskey1", variables);
        System.out.println(processInstance.toString());
    }

    //通过ProcessInstanceBuilder启动流程
    @Test
    public void testB(){

        ProcessInstanceBuilder processInstanceBuilder=runtimeService.createProcessInstanceBuilder();

        //链式编程方式
        ProcessInstance processInstance = processInstanceBuilder.businessKey("businessKey2")
                .processDefinitionKey("myProcess")
                .variable("managerCode", 123413245)
                .start();

        System.out.println(processInstance.getId());

    }

    //查找启动后流程的相关信息
    //添加 修改流程变量信息
    @Test
    public void testC(){

        //查询流程实例
        ProcessInstanceQuery processInstanceQuery =runtimeService.createProcessInstanceQuery();
        processInstanceQuery.processInstanceBusinessKey("businesskey1");
        ProcessInstance processInstance = processInstanceQuery.singleResult();

        //根据流程实例Id查询流程实例的变量
        Map<String, VariableInstance> variableInstances = runtimeService.getVariableInstances(processInstance.getId());
        System.out.println(variableInstances);

        //添加 重新设置流程变量
        runtimeService.setVariable(processInstance.getId(),"taskNumber",798);
        runtimeService.setVariable(processInstance.getId(),"salecode",1789156);

        Map<String, VariableInstance> variableInstances2 = runtimeService.getVariableInstances(processInstance.getId());
        System.out.println(variableInstances2);

    }

    //流程执行对象的查询
    @Test
    public void testD(){

        ExecutionQuery executionQuery = runtimeService.createExecutionQuery();
        //会查询出流程实例及其关联的流程执行对象
        List<Execution> executions = executionQuery.listPage(0, 100);
        System.out.println(executions);

        /*
        0 = {ExecutionEntityImpl@7351} "ProcessInstance[15001]"
        1 = {ExecutionEntityImpl@7352} "Execution[ id '15003' ] - activity 'usertask1 - parent '15001'"
        2 = {ExecutionEntityImpl@7353} "ProcessInstance[20001]"
        3 = {ExecutionEntityImpl@7354} "Execution[ id '20003' ] - activity 'usertask1 - parent '20001'"
        */
    }

    //测试使用triger触发的节点的执行receiveTask
    @Test
    public void testE(){


        ProcessInstanceBuilder processInstanceBuilder=runtimeService.createProcessInstanceBuilder();

        ProcessInstance processInstance = processInstanceBuilder
                .processDefinitionKey("myProcess-trigger")
                .start();

        //查询执行流程任务(待触发执行)
        Execution usertask1 = runtimeService.
                createExecutionQuery()
                .processDefinitionKey(processInstance.getProcessDefinitionKey())
                .activityId("usertask1").singleResult();

        //Execution[ id '30002' ] - activity 'usertask1 - parent '30001'
        System.out.println(usertask1);

        //trigger触发执行任务
        runtimeService.trigger(usertask1.getId());

        //任务已经执行完毕
        Execution usertask = runtimeService.
                createExecutionQuery()
                .processDefinitionKey(processInstance.getProcessDefinitionKey())
                .activityId("usertask1").singleResult();

        //null
        System.out.println(usertask);
    }

    //signal触发任务执行
    @Test
    public void testF(){

        ProcessInstanceBuilder processInstanceBuilder=runtimeService.createProcessInstanceBuilder();

        ProcessInstance processInstance = processInstanceBuilder
                .processDefinitionKey("myProcess-signal")
                .start();

        //查询执行流程任务(待触发执行)
        //signal触发是全局的，不需要指定任务流执行id
        Execution usertask1 = runtimeService.
                createExecutionQuery()
                .signalEventSubscriptionName("usertask1signal").singleResult();

        //Execution[ id '35002' ] - activity 'usertask1-signal-received - parent '35001'
        System.out.println(usertask1);

        //signal触发执行任务
        runtimeService.signalEventReceived("usertask1signal");

        //任务已经执行完毕
        Execution usertask = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName("usertask1signal").singleResult();

        //null
        System.out.println(usertask);


    }

    //message触发任务流执行
    @Test
    public void testG(){

        ProcessInstanceBuilder processInstanceBuilder=runtimeService.createProcessInstanceBuilder();

        ProcessInstance processInstance = processInstanceBuilder
                .processDefinitionKey("myProcess-message")
                .start();

        //查询执行流程任务(待触发执行)

        Execution usertask1 = runtimeService.
                createExecutionQuery().processInstanceId(processInstance.getProcessInstanceId())
                .messageEventSubscriptionName("usertask1-message").singleResult();


        //Execution[ id '42502' ] - activity 'myProcess-message-received - parent '42501'
        System.out.println(usertask1);

        //mesage触发执行任务
        //mesage需要指定任务流执行id进行任务流的执行
        runtimeService.messageEventReceived("usertask1-message",usertask1.getId());

        //任务已经执行完毕
        Execution usertask = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName("usertask1-message").singleResult();

        //null
        System.out.println(usertask);
    }





    //查询当前流程执行中的对象
    @Test
    public void getexecutionList(){

        List<Execution> executionList =
                runtimeService.createExecutionQuery().processDefinitionKey("myProcess-message").list();

        System.out.println(executionList);

    }


}
