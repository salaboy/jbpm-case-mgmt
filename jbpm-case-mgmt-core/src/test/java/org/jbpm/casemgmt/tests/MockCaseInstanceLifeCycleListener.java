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
package org.jbpm.casemgmt.tests;

import java.util.ArrayList;
import java.util.List;
import org.jbpm.casemgmt.api.CaseInstance;
import org.jbpm.casemgmt.api.CaseTask;
import org.jbpm.casemgmt.api.HumanTask;
import org.jbpm.casemgmt.api.ProcessTask;
import org.jbpm.casemgmt.service.api.CaseInstanceLifeCycleListener;

/**
 *
 * @author salaboy
 */
public class MockCaseInstanceLifeCycleListener implements CaseInstanceLifeCycleListener{

    private List<String> logs = new ArrayList<String>();
    
    @Override
    public void beforeCaseInstanceActive(CaseInstance instance) {
        logs.add(">> BEFORE CASE ACTIVE: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

    @Override
    public void afterCaseInstanceActive(CaseInstance instance) {
        logs.add(">> AFTER CASE ACTIVE: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

    @Override
    public void beforeCaseInstanceClosed(CaseInstance instance) {
        logs.add(">> BEFORE CASE CLOSED: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

    @Override
    public void afterCaseInstanceClosed(CaseInstance instance) {
        logs.add(">> AFTER CASE CLOSED: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

    @Override
    public void beforeCaseInstanceSuspended(CaseInstance instance) {
        logs.add(">> BEFORE CASE SUSPENDED: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

    @Override
    public void afterCaseInstanceSuspended(CaseInstance instance) {
        logs.add(">> AFTER CASE SUSPENDED: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

    @Override
    public void beforeCaseInstanceTerminated(CaseInstance instance) {
        logs.add(">> BEFORE CASE TERMINATED: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

    @Override
    public void afterCaseInstanceTerminated(CaseInstance instance) {
        logs.add(">> AFTER CASE TERMINATED: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

    @Override
    public void beforeHumanTaskAdded(CaseInstance instance) {
        logs.add(">> BEFORE HUMAN TASK ADDED: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

   

    @Override
    public void beforeProcessTaskAdded(CaseInstance instance) {
        logs.add(">> BEFORE PROCESS TASK ADDED: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

   
    @Override
    public void beforeCaseTaskAdded(CaseInstance instance) {
        logs.add(">> BEFORE CASE TASK ADDED: "+ instance.getId() + " - Name: "+ instance.getName() + " - for : "+ instance.getRecipient());
    }

    @Override
    public void afterHumanTaskAdded(CaseInstance instance, HumanTask task) {
        logs.add(">> AFTER HUMAN TASK ADDED: "+ instance.getId() + 
                " - Name: "+ instance.getName() +
                " - for : "+ instance.getRecipient() + 
                " - Task: "+ task.getName() + " - user: "+ task.getUsers());
    }

    @Override
    public void afterProcessTaskAdded(CaseInstance instance, ProcessTask process) {
        logs.add(">> AFTER PROCESS TASK ADDED: "+ instance.getId() + 
                " - Name: "+ instance.getName() +
                " - for : "+ instance.getRecipient() + 
                " - Process: "+ process.getName());
    }

    @Override
    public void afterCaseTaskAdded(CaseInstance instance, CaseTask caseTask) {
        logs.add(">> AFTER CASE TASK ADDED: "+ instance.getId() + 
                " - Name: "+ instance.getName() +
                " - for : "+ instance.getRecipient() + 
                " - Sub Case: "+ caseTask.getName());
    }

    public List<String> getLogs() {
        return logs;
    }

    
    
}
