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
package org.jbpm.console.ng.cm.backend.server;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.cm.model.CaseEventKey;
import org.jbpm.console.ng.cm.model.CaseEventSummary;
import org.jbpm.console.ng.cm.service.CaseAuditService;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.uberfire.paging.PageResponse;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class CaseAuditServiceImpl implements CaseAuditService{

    @Override
    public PageResponse<CaseEventSummary> getData(QueryFilter filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CaseEventSummary getItem(CaseEventKey key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CaseEventSummary> getAll(QueryFilter filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  
    
}
