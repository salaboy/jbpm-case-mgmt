package org.kie.server.remote.rest.jbpm.casemgmt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import static org.kie.server.api.rest.RestURI.CONTAINER_ID;
import static org.kie.server.api.rest.RestURI.PROCESS_ID;
import org.kie.server.services.api.KieServerApplicationComponentsService;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.api.SupportedTransports;
import org.kie.server.services.jbpm.casemgmt.JbpmCaseMgmtKieServerExtension;

public class JbpmCaseMgmtRestApplicationComponentsService implements KieServerApplicationComponentsService {

    private static final String OWNER_EXTENSION = JbpmCaseMgmtKieServerExtension.EXTENSION_NAME;
    
    

    @Override
    public Collection<Object> getAppComponents( String extension, SupportedTransports type, Object... services ) {
        // skip calls from other than owning extension
        if ( !OWNER_EXTENSION.equals(extension) ) {
            return Collections.emptyList();
        }

        ProcessService  processService = null;
        RuntimeDataService runtimeDataService = null;
        DefinitionService definitionService = null;
        UserTaskService userTaskService = null;
        KieServerRegistry context = null;

        for( Object object : services ) {
            if( ProcessService.class.isAssignableFrom(object.getClass()) ) {
               processService = (ProcessService) object;
               continue;
            } else if( RuntimeDataService.class.isAssignableFrom(object.getClass()) ) {
               runtimeDataService = (RuntimeDataService) object;
               continue;
            } else if( DefinitionService.class.isAssignableFrom(object.getClass()) ) {
               definitionService = (DefinitionService) object;
               continue;
            } else if( UserTaskService.class.isAssignableFrom(object.getClass()) ) {
                userTaskService = (UserTaskService) object;
                continue;
            } else if( KieServerRegistry.class.isAssignableFrom(object.getClass()) ) {
                context = (KieServerRegistry) object;
                continue;
            }
        }
        List<Object> components = new ArrayList<Object>(4);
        components.add(new CaseResource(processService, definitionService, runtimeDataService, context));

        return components;
    }

}
