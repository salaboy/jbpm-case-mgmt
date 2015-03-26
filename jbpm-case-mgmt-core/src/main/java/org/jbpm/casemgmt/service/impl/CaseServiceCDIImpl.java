/*
 * Copyright 2015 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.casemgmt.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jbpm.casemgmt.api.CaseInstance;
import org.jbpm.casemgmt.api.CaseTask;
import org.jbpm.casemgmt.api.HumanTask;
import org.jbpm.casemgmt.api.ProcessTask;
import org.jbpm.casemgmt.model.CaseInstanceImpl;
import org.jbpm.casemgmt.service.api.CaseService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.task.utils.TaskFluent;
import org.kie.api.task.model.Task;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.InternalTaskService;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
public class CaseServiceCDIImpl implements CaseService {

    @Inject
    private InternalTaskService internalTaskService;

    @Inject
    private ProcessService processService;

    @Inject
    private RuntimeDataService runtimeDataService;

    @Inject
    private UserTaskService userTaskService;

    @Inject
    private DeploymentService deploymentService;

    private Map<Long, CaseInstance> caseInstances = new HashMap<Long, CaseInstance>();

    public CaseServiceCDIImpl() {
    }

    @Override
    public List<CaseInstance> getCaseInstances(QueryFilter qf) {
        return new ArrayList<CaseInstance>(caseInstances.values());
    }

    @Override
    public Long createCaseInstance(String caseId, Map<String, Object> params) {
        DeployedUnit du = deploymentService.getDeployedUnits().iterator().next();
        CaseInstance caseInstance = new CaseInstanceImpl();
        Long parentId = processService.startProcess(du.getDeploymentUnit().getIdentifier(), caseId, params);
        caseInstance.setParentAdhocProcessInstance(parentId);
        caseInstances.put(caseInstance.getId(), caseInstance);
        return caseInstance.getId();
    }

    public ProcessService getProcessService() {
        return processService;
    }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    public RuntimeDataService getRuntimeDataService() {
        return runtimeDataService;
    }

    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        this.runtimeDataService = runtimeDataService;
    }

    public UserTaskService getUserTaskService() {
        return userTaskService;
    }

    public void setUserTaskService(UserTaskService userTaskService) {
        this.userTaskService = userTaskService;
    }

    @Override
    public void addHumanTask(Long caseId, HumanTask humanTask) {
        Task task = transformHumanTask(caseId, humanTask);
        Long taskId = internalTaskService.addTask(task, (Map) null);
        caseInstances.get(caseId).addTaskId(taskId);
    }

    @Override
    public void addProcessTask(Long caseId, ProcessTask processTask) {

    }

    @Override
    public void addCaseTask(Long caseId, CaseTask caseTask) {

    }

    private Task transformHumanTask(Long caseId, HumanTask humanTask) {

        TaskFluent taskFluent = new TaskFluent().setName(humanTask.getName());
        taskFluent.setProcessInstanceId(caseId);
        if(humanTask.getUsers()!= null){
            for (String user : humanTask.getUsers()) {
                taskFluent.addPotentialUser(user);
            }
        }
        if(humanTask.getGroups() != null){
            for (String group : humanTask.getGroups()) {
                taskFluent.addPotentialGroup(group);
            }
        }
        taskFluent.setAdminUser("Administrator");
        taskFluent.setAdminGroup("Administrators");

        return taskFluent.getTask();
    }

}
