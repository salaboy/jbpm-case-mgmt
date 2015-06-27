package org.kie.server.remote.rest.jbpm.casemgmt;

import java.text.MessageFormat;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.server.remote.rest.common.exception.ExecutionServerRestOperationException;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.impl.marshal.MarshallerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.server.api.rest.RestURI.*;
import static org.kie.server.remote.rest.common.util.RestUtils.*;
import static org.kie.server.remote.rest.jbpm.resources.Messages.*;

@Path("/server")
public class CaseResource  {

     public static final String START_CASE_POST_URI = "containers/{" + CONTAINER_ID + "}/case/{" + PROCESS_ID +"}/instances";
     
    public static final Logger logger = LoggerFactory.getLogger(CaseResource.class);

    private ProcessService processService;
    private DefinitionService definitionService;
    private RuntimeDataService runtimeDataService;
    private MarshallerHelper marshallerHelper;

    private CorrelationKeyFactory correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();

    public CaseResource(ProcessService processService, DefinitionService definitionService, RuntimeDataService runtimeDataService, KieServerRegistry context) {
        this.processService = processService;
        this.definitionService = definitionService;
        this.runtimeDataService = runtimeDataService;
        this.marshallerHelper = new MarshallerHelper(context);
    }

    protected static String getRelativePath(HttpServletRequest httpRequest) {
        String url =  httpRequest.getRequestURI();
        url.replaceAll( ".*/rest", "");
        return url;
    }

    @POST
    @Path(START_CASE_POST_URI)
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response startCase(@javax.ws.rs.core.Context HttpHeaders headers, @PathParam("id") String containerId, @PathParam("pId") String processId, @DefaultValue("") String payload) {
        Variant v = getVariant(headers);
        String type = getContentType(headers);
        // Check for presence of process id
        try {
            ProcessDefinition procDef = definitionService.getProcessDefinition(containerId, processId);
            if( procDef == null ) {
                throw ExecutionServerRestOperationException.notFound(MessageFormat.format(PROCESS_DEFINITION_NOT_FOUND, processId, containerId), v);
            }
        } catch( Exception e ) {
            logger.error("Unexpected error during processing {}", e.getMessage(), e);
            throw ExecutionServerRestOperationException.internalServerError(
                        MessageFormat.format(PROCESS_DEFINITION_FETCH_ERROR, processId, containerId, e.getMessage()), v);
        }
        logger.debug("About to unmarshal parameters from payload: '{}'", payload);
        Map<String, Object> parameters = marshallerHelper.unmarshal(containerId, payload, type, Map.class);

        logger.debug("Calling start process with id {} on container {} and parameters {}", processId, containerId, parameters);
        Long processInstanceId = processService.startProcess(containerId, processId, parameters);

        // return response
        try {
            String response = marshallerHelper.marshal(containerId, type, processInstanceId);
            logger.debug("Returning CREATED response with content '{}'", response);
            return createResponse(response, v, Response.Status.CREATED);
        } catch (Exception e) {
            logger.error("Unexpected error during processing {}", e.getMessage(), e);
            throw ExecutionServerRestOperationException.internalServerError(
                    MessageFormat.format(CREATE_RESPONSE_ERROR, e.getMessage()), v);
        }
    }

  

    

}
