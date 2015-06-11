/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.cm.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;

@Portable
public class CaseSummary extends GenericSummary {

    private Long caseId;
    private String caseName;
    private String description;
    private String status;
    private String recipient;
    private String humanTasksDetails;
    private String processesDetails;
    private String subCasesDetails;

    public CaseSummary() {
    }

    
    public CaseSummary(Long caseId, String caseName) {
        this.id = caseId;
        this.name = caseName;
        this.caseId = caseId;
        this.caseName = caseName;
    }

    public CaseSummary(long caseId, String caseName, String description, String status, String recipient) {
        this(caseId, caseName);
        this.description = description;
        this.status = status;
        this.recipient = recipient;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getHumanTasksDetails() {
        return humanTasksDetails;
    }

    public void setHumanTasksDetails(String humanTasksDetails) {
        this.humanTasksDetails = humanTasksDetails;
    }

    public String getProcessesDetails() {
        return processesDetails;
    }

    public void setProcessesDetails(String processesDetails) {
        this.processesDetails = processesDetails;
    }

    public String getSubCasesDetails() {
        return subCasesDetails;
    }

    public void setSubCasesDetails(String subCasesDetails) {
        this.subCasesDetails = subCasesDetails;
    }
    
    

    @Override
    public String toString() {
        return "CaseSummary{" + "caseId=" + caseId + ", caseName=" + caseName + ", description=" + description + ", status=" + status + ", recipient=" + recipient + '}';
    }
    
    

}
