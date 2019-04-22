package com.study.activiti;

import com.study.activiti.eventlistener.CustomerEventListener;
import com.study.activiti.eventlistener.ProcessEventListener;
import org.activiti.engine.*;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiActivityEventImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes ={ActivitiApplication.class} )
public class ActivitiApplicationTests {

	@Autowired
	@Qualifier("mysqlEngineConfiguration")
	ProcessEngineConfiguration processEngineConfiguration;

	@Test
	public void contextLoads() {

	}

	@Test
	public void testA() {



		//创建流程引擎
		//ProcessEngineConfiguration processEngineConfiguration=ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		ProcessEngine processEngine=processEngineConfiguration.buildProcessEngine();

		String name=processEngine.getName();
		String version=processEngine.VERSION;

		//部署流程定义文件
		RepositoryService repositoryService=processEngine.getRepositoryService();
		DeploymentBuilder deploymentBuilder=repositoryService.createDeployment();

		deploymentBuilder.addClasspathResource("processes/内转外WBS变更.bpmn20.xml");
		Deployment deployment=deploymentBuilder.deploy();
		String deploymentId=deployment.getId();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();

		//启动运行流程
		RuntimeService runtimeService=processEngine.getRuntimeService();
		ProcessInstance processInstance=runtimeService.startProcessInstanceByKey(deployment.getKey());

		System.out.println(processInstance.getProcessDefinitionKey());


	}


	@Resource
	private RepositoryService repositoryService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;
	@Test
	public void testB(){


		ProcessEngine processEngine=processEngineConfiguration.buildProcessEngine();

		String name=processEngine.getName();
		String version=processEngine.VERSION;

		//部署流程定义文件
        RepositoryService repositoryService=processEngine.getRepositoryService();
		DeploymentBuilder deploymentBuilder=repositoryService.createDeployment();

		deploymentBuilder.addClasspathResource("processes/内转外WBS变更.bpmn20.xml");
		Deployment deployment=deploymentBuilder.deploy();
		String deploymentId=deployment.getId();

		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();

		Long businessKey=new Double(1000000*Math.random()).longValue();

		RuntimeService runtimeService=processEngine.getRuntimeService();
		//启动流程
		runtimeService.startProcessInstanceById(processDefinition.getId(),businessKey.toString());

		TaskService taskService=processEngine.getTaskService();

		//代码中手动添加事假监听器(全局)
		runtimeService.addEventListener(new CustomerEventListener());
		//代码中手动添加事假监听器(特定类型的事件操作)
		runtimeService.addEventListener(new ProcessEventListener(),ActivitiEventType.PROCESS_STARTED);

		//代码中手动发布自定义的事件
		runtimeService.dispatchEvent(new ActivitiActivityEventImpl(ActivitiEventType.CUSTOM));

		//查询任务实例
		List<Task> taskList=taskService.createTaskQuery().processDefinitionId(processDefinition.getId()).list();
		for(Task task:taskList){
			System.out.println("task name is " + task.getName() + " ,task key is " + task.getTaskDefinitionKey());
		}

	}

}
