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
import org.jbpm.casemgmt.model.CaseInstanceImpl.CaseStatus;
import org.jbpm.casemgmt.service.api.CaseInstanceLifeCycleListener;
import org.jbpm.casemgmt.service.api.CaseInstancesService;
import org.jbpm.services.api.AdHocProcessService;
import org.jbpm.services.api.AdHocUserTaskService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.task.utils.TaskFluent;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
public class CaseInstancesServiceCDIImpl implements CaseInstancesService {

    @Inject
    private AdHocUserTaskService adHocTaskService;

    @Inject
    private ProcessService processService;

    @Inject
    private AdHocProcessService adHocProcessService;

    @Inject
    private RuntimeDataService runtimeDataService;

    @Inject
    private UserTaskService userTaskService;

    @Inject
    private DeploymentService deploymentService;

    private Map<Long, CaseInstance> caseInstances = new HashMap<Long, CaseInstance>();

    private List<CaseInstanceLifeCycleListener> listeners = new ArrayList<CaseInstanceLifeCycleListener>();

    public CaseInstancesServiceCDIImpl() {
    }

    @Override
    public List<CaseInstance> getCaseInstances(QueryFilter qf) {

        return new ArrayList<CaseInstance>(caseInstances.values());
    }

    @Override
    public void registerLifeCycleListener(CaseInstanceLifeCycleListener listener) {
        listeners.add(listener);
    }

    @Override
    public Long createCaseInstance(String caseIdentifier, String recipient, String deploymentId, String caseTemplate, Map<String, Object> params) {

        CaseInstance caseInstance = new CaseInstanceImpl(caseIdentifier);
        caseInstance.setDescription("Case Description Here...");
        caseInstance.setRecipient(recipient);
        caseInstances.put(caseInstance.getId(), caseInstance);

        activateCaseInstance(caseInstance.getId(), deploymentId, caseTemplate, params);

        return caseInstance.getId();
    }

    private void activateCaseInstance(Long caseId, String deploymentId, String caseTemplate, Map<String, Object> params) {

        CaseInstance instance = caseInstances.get(caseId);
        for (CaseInstanceLifeCycleListener l : listeners) {
            l.beforeCaseInstanceActive(instance);
        }
        instance.setStatus(CaseStatus.ACTIVE);
        DeployedUnit du = deploymentService.getDeployedUnit(deploymentId);
        Long parentId = processService.startProcess(du.getDeploymentUnit().getIdentifier(), caseTemplate, params);
        instance.setParentAdhocProcessInstance(parentId);
        for (CaseInstanceLifeCycleListener l : listeners) {
            l.afterCaseInstanceActive(instance);
        }
    }

    @Override
    public void activateCaseInstance(Long caseId) {
        CaseInstance instance = caseInstances.get(caseId);
        for (CaseInstanceLifeCycleListener l : listeners) {
            l.beforeCaseInstanceActive(instance);
        }

        instance.setStatus(CaseStatus.ACTIVE);

        for (CaseInstanceLifeCycleListener l : listeners) {
            l.afterCaseInstanceActive(instance);
        }
    }

    @Override
    public void closeCaseInstance(Long caseId) {
        CaseInstance instance = caseInstances.get(caseId);
        for (CaseInstanceLifeCycleListener l : listeners) {
            l.beforeCaseInstanceClosed(instance);
        }
        instance.setStatus(CaseStatus.CLOSED);
        for (CaseInstanceLifeCycleListener l : listeners) {
            l.afterCaseInstanceClosed(instance);
        }
    }

    @Override
    public void terminateCaseInstance(Long caseId) {
        CaseInstance instance = caseInstances.get(caseId);
        for (CaseInstanceLifeCycleListener l : listeners) {
            l.beforeCaseInstanceTerminated(instance);
        }
        instance.setStatus(CaseStatus.TERMINATED);
        for (CaseInstanceLifeCycleListener l : listeners) {
            l.afterCaseInstanceTerminated(instance);
        }
    }

    @Override
    public void suspendCaseInstance(Long caseId) {
        CaseInstance instance = caseInstances.get(caseId);
        for (CaseInstanceLifeCycleListener l : listeners) {
            l.beforeCaseInstanceSuspended(instance);
        }
        instance.setStatus(CaseStatus.SUSPENDED);
        for (CaseInstanceLifeCycleListener l : listeners) {
            l.afterCaseInstanceSuspended(instance);
        }
    }

    public ProcessService getProcessService() {
        return processService;
    }

    @Override
    public void addHumanTask(Long caseId, HumanTask humanTask) {
        CaseInstance instance = caseInstances.get(caseId);
        if (instance.getStatus().equals(CaseStatus.ACTIVE)) {
            for (CaseInstanceLifeCycleListener l : listeners) {
                l.beforeHumanTaskAdded(instance);
            }
            Task task = transformHumanTask(caseId, humanTask);
            Long taskId = adHocTaskService.addTask(task, new HashMap<String, Object>());
            caseInstances.get(caseId).addHumanTaskId(taskId);
            for (CaseInstanceLifeCycleListener l : listeners) {
                l.afterHumanTaskAdded(instance, humanTask);
            }
        } else {
            throw new UnsupportedOperationException("A case must be Active in order to add a Task to it.");
        }
    }

    @Override
    public void addProcessTask(Long caseId, ProcessTask processTask) {
        CaseInstance instance = caseInstances.get(caseId);
        if (instance.getStatus().equals(CaseStatus.ACTIVE)) {
            for (CaseInstanceLifeCycleListener l : listeners) {
                l.beforeProcessTaskAdded(instance);
            }
            DeployedUnit du = deploymentService.getDeployedUnits().iterator().next(); //@TODO implement resolution lookup

            Long processId = adHocProcessService.startProcess(du.getDeploymentUnit().getIdentifier(), processTask.getName(), null,
                    processTask.getParams(), instance.getParentAdhocProcessInstance());
            caseInstances.get(caseId).addProcessTaskId(processId);
            for (CaseInstanceLifeCycleListener l : listeners) {
                l.afterProcessTaskAdded(instance, processTask);
            }
        } else {
            throw new UnsupportedOperationException("A case must be Active in order to add a Process to it.");
        }
    }

    @Override
    public void addCaseTask(Long caseId, CaseTask caseTask) {
        CaseInstance caseInstance = caseInstances.get(caseId);
        if (caseInstance.getStatus().equals(CaseStatus.ACTIVE)) {
        } else {
            throw new UnsupportedOperationException("A case must be Active in order to add a Sub Case to it.");

        }
    }

    @Override
    public List<TaskSummary> getAllCaseHumanTasks(Long caseId) {
        CaseInstance caseInstance = caseInstances.get(caseId);

        Long parentProcessId = caseInstance.getParentAdhocProcessInstance();

        List<TaskSummary> parentProcessTasksIds = runtimeDataService.getTasksByStatusByProcessInstanceId(parentProcessId, null, null);
        List<TaskSummary> childrenTasks = new ArrayList<TaskSummary>();
        for (Long id : caseInstance.getProcessInstanceIds()) {
            childrenTasks.addAll(runtimeDataService.getTasksByStatusByProcessInstanceId(id, null, null));
        }
        parentProcessTasksIds.addAll(childrenTasks);
        return parentProcessTasksIds;
    }

    @Override
    public List<ProcessInstanceDesc> getAllCaseProcessTasks(Long caseId) {
        CaseInstance caseInstance = caseInstances.get(caseId);
        List<ProcessInstanceDesc> processInstances = new ArrayList<ProcessInstanceDesc>();
        processInstances.add(runtimeDataService.getProcessInstanceById(caseInstance.getParentAdhocProcessInstance()));
        for (Long id : caseInstance.getProcessInstanceIds()) {
            processInstances.add(runtimeDataService.getProcessInstanceById(id));
        }
        return processInstances;
    }

    @Override
    public List<CaseInstance> getAllCaseCaseTasks(Long caseId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Task transformHumanTask(Long caseId, HumanTask humanTask) {

        TaskFluent taskFluent = new TaskFluent().setName(humanTask.getName());
        taskFluent.setProcessInstanceId(caseId);
        if (humanTask.getUsers() != null) {
            for (String user : humanTask.getUsers()) {
                taskFluent.addPotentialUser(user);
            }
        }
        if (humanTask.getGroups() != null) {
            for (String group : humanTask.getGroups()) {
                taskFluent.addPotentialGroup(group);
            }
        }
        taskFluent.setAdminUser("Administrator");
        taskFluent.setAdminGroup("Administrators");

        return taskFluent.getTask();
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

}
