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
package org.jbpm.casemgmt.service.api;

import org.jbpm.casemgmt.api.CaseInstance;
import org.jbpm.casemgmt.api.CaseTask;
import org.jbpm.casemgmt.api.HumanTask;
import org.jbpm.casemgmt.api.ProcessTask;

/**
 *
 * @author salaboy
 */
public interface CaseInstanceLifeCycleListener {
    void beforeCaseInstanceActive(CaseInstance instance);
    void afterCaseInstanceActive(CaseInstance instance);
    void beforeCaseInstanceClosed(CaseInstance instance);
    void afterCaseInstanceClosed(CaseInstance instance);
    void beforeCaseInstanceSuspended(CaseInstance instance);
    void afterCaseInstanceSuspended(CaseInstance instance);
    void beforeCaseInstanceTerminated(CaseInstance instance);
    void afterCaseInstanceTerminated(CaseInstance instance);
    void beforeHumanTaskAdded(CaseInstance instance);
    void afterHumanTaskAdded(CaseInstance instance, HumanTask task);
    void beforeProcessTaskAdded(CaseInstance instance);
    void afterProcessTaskAdded(CaseInstance instance, ProcessTask process);
    void beforeCaseTaskAdded(CaseInstance instance);
    void afterCaseTaskAdded(CaseInstance instance, CaseTask caseTask);
}
