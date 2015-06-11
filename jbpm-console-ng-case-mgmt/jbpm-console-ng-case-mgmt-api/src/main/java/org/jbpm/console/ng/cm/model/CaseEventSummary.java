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
public class CaseEventSummary extends GenericSummary {

    private Long caseId;
    private String type;
    private String description;

    public CaseEventSummary() {
    }

    public CaseEventSummary(Long caseId, String description) {
        this.caseId = caseId;
        this.description = description;
    }

    public CaseEventSummary(long caseId, String description, String type) {
        super();
        this.caseId = caseId;
        this.description = description;
        this.type = type;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

  
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CaseEventSummary{" + "caseId=" + caseId + ", description=" + description + ", type=" + type + '}';
    }

}
