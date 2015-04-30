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
package org.jbpm.console.ng.cm.backend.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.cm.backend.server.util.CaseSummaryHelper;
import org.jbpm.console.ng.cm.model.CaseKey;
import org.jbpm.console.ng.cm.model.CaseSummary;
import org.jbpm.console.ng.cm.service.CaseInstancesService;
import org.jbpm.console.ng.ga.model.QueryFilter;


import org.uberfire.paging.PageResponse;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class CaseInstancesServiceImpl implements CaseInstancesService {

    @Inject
    private org.jbpm.casemgmt.service.api.CaseInstancesService caseService;

    public CaseInstancesServiceImpl() {
    }

    @Override
    public void createCaseInstance(String caseIdentifier, String deploymentId, String templateName, Map<String, Object> params) {
        caseService.createCaseInstance(caseIdentifier, deploymentId, templateName, params);
    }

    @Override
    public PageResponse<CaseSummary> getData(QueryFilter filter) {

        PageResponse<CaseSummary> response = new PageResponse<CaseSummary>();

        String stringFilter = "";
        if (filter.getParams() != null) {

            stringFilter = (String) filter.getParams().get("filter");
        }

        org.kie.internal.query.QueryFilter qf = new org.kie.internal.query.QueryFilter(filter.getOffset(), filter.getCount() + 1,
                filter.getOrderBy(), filter.isAscending());
        qf.setFilterParams(filter.getFilterParams());
        List<CaseSummary> caseSummaries = CaseSummaryHelper.adaptCollection(caseService.getCaseInstances(qf));

        response.setStartRowIndex(filter.getOffset());
        response.setTotalRowSize(caseSummaries.size() - 1);
        if (caseSummaries.size() > filter.getCount()) {
            response.setTotalRowSizeExact(false);
        } else {
            response.setTotalRowSizeExact(true);
        }

        if (!caseSummaries.isEmpty() && caseSummaries.size() > (filter.getCount() + filter.getOffset())) {
            response.setPageRowList(new ArrayList<CaseSummary>(caseSummaries.subList(filter.getOffset(), filter.getOffset() + filter.getCount())));
            response.setLastPage(false);

        } else {
            response.setPageRowList(new ArrayList<CaseSummary>(caseSummaries));
            response.setLastPage(true);

        }
        return response;

    }

    @Override
    public CaseSummary getItem(CaseKey key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CaseSummary> getAll(QueryFilter filter) {
        String stringFilter = "";
        if (filter.getParams() != null) {

            stringFilter = (String) filter.getParams().get("filter");
        }

        org.kie.internal.query.QueryFilter qf = new org.kie.internal.query.QueryFilter(filter.getOffset(), filter.getCount() + 1,
                filter.getOrderBy(), filter.isAscending());
        qf.setFilterParams(filter.getFilterParams());

        return CaseSummaryHelper.adaptCollection(caseService.getCaseInstances(qf));
    }

}
