package com.study.activiti.activiCoreApi;

import com.study.activiti.ActivitiApplication;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: RepositoryService 服务测试
 * @Author：pengrj
 * @Date : 2019/4/20 0020 10:37
 * @version:1.0
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes ={ActivitiApplication.class} )
public class RepositoryServiceTest {

    Logger logger=LoggerFactory.getLogger(this.getClass());

    @Resource(name = "repositoryService")
    private RepositoryService repositoryService;

    @Resource(name="runtimeService")
    private RuntimeService runtimeService;


    //查询流程部署的信息记录
    @Test
    public void testA(){
        //从 act_re_deployment 表中查询数据
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        Deployment deployment=deploymentQuery.deploymentId("1").singleResult();
        System.out.println(deployment);
    }

    //查询流程定义的信息记录 act_re_procdef
    @Test
    public void testB(){

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //列表查询
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();

        //基于流程定义id查询
        ProcessDefinitionQuery processDefinitionQuery1 = processDefinitionQuery.processDefinitionId("process2015101309111201:1:10");

        //基于流程定义的key进行查询
        ProcessDefinitionQuery processDefinitionQuery2=processDefinitionQuery.processDefinitionKey("process2015101309111201");

        ProcessDefinition processDefinition1 = processDefinitionQuery1.singleResult();
        ProcessDefinition processDefinition2 = processDefinitionQuery2.singleResult();

    }
    
    //启动挂起流程定义
    @Test
    public void testC(){
        
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //基于流程定义的key进行查询
        ProcessDefinitionQuery processDefinitionQuery2=processDefinitionQuery.processDefinitionKey("process2015101309111201");

        ProcessDefinition processDefinition2 = processDefinitionQuery2.singleResult();
        
        //挂起流程定义 SUSPENSION_STATE_ 1->2
        try {
             //如果已被挂起，则不能再次进行挂起操作
             repositoryService.suspendProcessDefinitionById(processDefinition2.getId());

             //根据流程定义发起流程 流程定义挂起后不能进行发起流程
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition2.getId());

        } catch (Exception e) {
            //Cannot start process instance.
            // Process definition 二级审批流程 (id = process2015101309111201:1:10) is suspended
            e.printStackTrace();
            //激活流程定义
            repositoryService.activateProcessDefinitionById(processDefinition2.getId());
        }


    }

    //指定流程定义文件只能通过用户和用户组进行执行
    //activiti提供了将流程定义文件和用户、用户组进行关联的API 绑定后的关联关系存入 act_ru_identitylink 表中
    //再在实际业务中进行用户的判断即可做到流程定义文件只能通过用户和用户组进行执行
    @Test
    public void testD(){


        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //基于流程定义的key进行查询
        ProcessDefinitionQuery processDefinitionQuery2=processDefinitionQuery.processDefinitionKey("process2015101309111201");

        ProcessDefinition processDefinition2 = processDefinitionQuery2.singleResult();

        //将流程定义文件绑定用户
        repositoryService.addCandidateStarterUser(processDefinition2.getId(),"user1");
        repositoryService.addCandidateStarterUser(processDefinition2.getId(),"user2");
        //将流程定义文件绑定用户组
        repositoryService.addCandidateStarterGroup(processDefinition2.getId(),"groupUser");

        //查询流程定义文件绑定的用户和用户组信息
        List<IdentityLink> identityLinksForProcessDefinition = repositoryService.getIdentityLinksForProcessDefinition(processDefinition2.getId());
        logger.info("List<IdentityLink> {}",identityLinksForProcessDefinition.toString());
    }

    



}
