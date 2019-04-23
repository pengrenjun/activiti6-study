package com.study.activiti.activiCoreApi;

import com.study.activiti.ActivitiApplication;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 任务管理服务测试
 * @Author：pengrj
 * @Date : 2019/4/23 0023 21:52
 * @version:1.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes ={ActivitiApplication.class} )
public class TaskServiceTest {

    @Resource(name="runtimeService")
    private RuntimeService runtimeService;

    @Resource(name="taskService")
    private TaskService taskService;

    //测试任务服务相关的操作 添加变量 查找变量 执行任务
    @Test
    public void testA(){

        Map<String,Object> variables=new HashMap<>();

        variables.put("description","任务管理服务测试");

        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("processTaskTest", variables);

        Task taskA = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();

        System.out.println(ToStringBuilder.reflectionToString(taskA,ToStringStyle.JSON_STYLE));

        //设置变量
        taskService.setVariable(taskA.getId(),"a1","123");
        taskService.setVariableLocal(taskA.getId(),"localKeyVar","1231234245");

        //taskService查找变量
        //0 = {HashMap$Node@7540} "a1" -> "123"
        //1 = {HashMap$Node@7541} "localKeyVar" -> "1231234245"
        //2 = {HashMap$Node@7542} "description" -> "任务管理服务测试"
        Map<String, Object> variables1 = taskService.getVariables(taskA.getId());

        //"localKeyVar" -> "1231234245"
        Map<String, Object> variablesLocal = taskService.getVariablesLocal(taskA.getId());

        //RuntimeService查找变量
//        0 = {HashMap$Node@7586} "a1" -> "123"
//        1 = {HashMap$Node@7587} "description" -> "任务管理服务测试"
        Map<String, Object> variables2 = runtimeService.getVariables(taskA.getExecutionId());

        // size = 0
        Map<String, Object> variablesLocal1 = runtimeService.getVariablesLocal(taskA.getExecutionId());

        //执行完成当前任务
        Map<String,Object> completVar=new HashMap<>();
        completVar.put("completVar","02789");
        taskService.complete(taskA.getId(),completVar);

        Task taskB = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();

        //processTaskTest->usertask2 任务管理服务测试}
        String description=taskB.getDescription();

    }

    @Test
    public void testRuTask(){

        //查找流程实例对应的正在运行的任务
        Task task = taskService.createTaskQuery().processInstanceId("77501").singleResult();


    }


}
