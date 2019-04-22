package com.study.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 基于Spring配置整合的流程引擎测试
 * @Author：pengrj
 * @Date : 2019/4/8 0008 22:09
 * @version:1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:activiti-context.xml"})
public class ActivitiSpringTest {

    @Autowired
    private ActivitiRule activitiRule;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;



    @Test
    //@Deployment(resources = {"processes/simpleProcess.bpmn20.xml"})
    public void activitiSpringTest(){


        //部署流程定义文件
        DeploymentBuilder deploymentBuilder=repositoryService.createDeployment();

        deploymentBuilder.addClasspathResource("processes/secondApprove.bpmn20.xml");
        org.activiti.engine.repository.Deployment deployment=deploymentBuilder.deploy();
        String deploymentId=deployment.getId();

        ProcessInstance processInstance=runtimeService.startProcessInstanceByKey("process2015101309111201");
        Assert.assertNotNull(processInstance);

        Task task=taskService.createTaskQuery().singleResult();

        Map<String, Object> variables =new HashMap<>();

        variables.put("submitFlag","Y");


        taskService.complete(task.getId(),variables);



    }

}
