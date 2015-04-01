/*
 * Copyright 2012 JBoss by Red Hat.
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

package org.jbpm.console.ng.cm.backend.server.util;

import java.util.ArrayList;
import java.util.List;
import org.jbpm.casemgmt.api.CaseInstance;
import org.jbpm.console.ng.cm.model.CaseSummary;



public class CaseSummaryHelper {
    
    public static List<CaseSummary> adaptCollection(List<CaseInstance> caseInstances) {
        List<CaseSummary> caseSummaries = new ArrayList<CaseSummary>(caseInstances.size());
        for (CaseInstance caseInstance : caseInstances) {
            caseSummaries.add(adapt(caseInstance));
        }
        return caseSummaries;
    }
    
    public static CaseSummary adapt(CaseInstance caseInstance) { 
        return new CaseSummary(caseInstance.getId(), caseInstance.getName(), caseInstance.getDescription(),caseInstance.getStatus());
    
    }

    
}
