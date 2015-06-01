/*
 * Copyright 2014 JBoss by Red Hat.
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

import static org.junit.Assert.assertNotNull;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.casemgmt.api.CaseInstance;
import org.jbpm.casemgmt.api.HumanTask;
import org.jbpm.casemgmt.api.ProcessTask;
import org.jbpm.casemgmt.model.HumanTaskImpl;
import org.jbpm.casemgmt.model.ProcessTaskImpl;
import org.jbpm.casemgmt.service.api.CaseInstancesService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.scanner.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CaseServiceImplTest extends CaseAbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CaseServiceImplTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    @Before
    public void prepare() {
        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/expenses.bpmn");
        processes.add("repo/processes/general/travel.bpmn");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

        ReleaseId releaseId3 = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, "1.1.0-SNAPSHOT");

        InternalKieModule kJar3 = createKieJar(ks, releaseId3, processes);
        File pom3 = new File("target/kmodule3", "pom.xml");
        pom3.getParentFile().mkdirs();
        try {
            FileOutputStream fs = new FileOutputStream(pom3);
            fs.write(getPom(releaseId3).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        repository = getMavenRepository();
        repository.deployArtifact(releaseId3, kJar3, pom3);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                try {
                    deploymentService.undeploy(unit);
                } catch (Exception e) {
                    // do nothing in case of some failed tests to avoid next test to fail as well
                }
            }
            units.clear();
        }
        close();
    }

    @Test
    public void testCreateCase() {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        Long caseId = caseService.createCaseInstance("myFirst Case",deploymentUnit.getIdentifier(),"org.jbpm.examples.checklist.travel", null);

        assertNotNull(caseId);
        List<String> users = new ArrayList<String>();
        users.add("salaboy");
        HumanTask humanTask = new HumanTaskImpl("my first case task", users, null);
        caseService.addHumanTask(caseId, humanTask);

        List<CaseInstance> cases = caseService.getCaseInstances(null);
        assertNotNull(cases);
        assertTrue(cases.size() == 1);

        CaseInstance caseInstance = cases.get(0);
        assertTrue(caseInstance.getTaskIds().size() == 1);
        Task task = userTaskService.getTask(caseInstance.getTaskIds().get(0));
        assertEquals("my first case task", task.getName());
        userTaskService.start(caseInstance.getTaskIds().get(0), "salaboy");

        List<Long> tasksByProcessId = runtimeDataService.getTasksByProcessInstanceId(caseInstance.getParentAdhocProcessInstance());
        assertEquals(2, tasksByProcessId.size());

        ProcessTask processTask = new ProcessTaskImpl();
        processTask.setName("com.sample.bpmn");
        caseService.addProcessTask(caseId, processTask);

        assertEquals(1, caseService.getCaseInstances(null).get(0).getProcessInstanceIds().size());

        List<TaskSummary> ts = caseService.getAllCaseHumanTasks(caseId);
        assertEquals(3, ts.size());
        
        List<ProcessInstanceDesc> processInstances = caseService.getAllCaseProcessTasks(caseId);
        assertEquals(2, processInstances.size());
    }

}
