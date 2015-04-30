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
package org.jbpm.casemgmt.api;

import java.util.List;

/**
 *
 * @author salaboy
 */
public interface CaseInstance {
    
    Long getId();
    
    String getName();
    
    String getDescription();
    
    String getStatus();

    void setDescription(String desc);

    void setStatus(String status);
    
    Long getParentAdhocProcessInstance();

    void setParentAdhocProcessInstance(Long parentAdhocProcessInstance);
    
    List<Long> getProcessInstanceIds();

    void setProcessInstanceIds(List<Long> processInstanceIds);

    List<Long> getTaskIds();

    void setTaskIds(List<Long> taskIds);
    
    void addHumanTaskId(Long taskId);
    
    void addProcessTaskId(Long processId);
    
    void addCaseTaskId(Long processId);

    List<Long> getCaseIds();

    void setCaseIds(List<Long> caseIds);
}
