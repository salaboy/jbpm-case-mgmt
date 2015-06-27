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
package org.jbpm.casemgmt.service.cdi.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jbpm.casemgmt.service.impl.CaseInstancesServiceImpl;
import org.jbpm.services.api.AdHocProcessService;
import org.jbpm.services.api.AdHocUserTaskService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
public class CaseInstancesServiceCDIImpl extends CaseInstancesServiceImpl {

    public CaseInstancesServiceCDIImpl() {
        super(true);
    }

    @Inject
    @Override
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    @Inject
    @Override
    public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
        this.runtimeDataService = runtimeDataService;
    }

    @Inject
    @Override
    public void setUserTaskService(UserTaskService userTaskService) {
        this.userTaskService = userTaskService;
    }
    
    @Inject
    @Override
    public void setAdHocTaskService(AdHocUserTaskService adHocTaskService) {
        this.adHocTaskService = adHocTaskService;
    }

    @Inject
    @Override
    public void setAdHocProcessService(AdHocProcessService adHocProcessService) {
        this.adHocProcessService = adHocProcessService;
    }

    @Inject
    @Override
    public void setDeploymentService(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

}
