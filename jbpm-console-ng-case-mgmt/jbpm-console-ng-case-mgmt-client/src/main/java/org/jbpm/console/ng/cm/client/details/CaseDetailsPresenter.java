/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.cm.client.details;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextArea;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.cm.model.CaseKey;
import org.jbpm.console.ng.cm.model.CaseSummary;
import org.jbpm.console.ng.cm.model.events.CaseRefreshedEvent;
import org.jbpm.console.ng.cm.model.events.CaseSelectionEvent;
import org.jbpm.console.ng.cm.service.CaseInstancesService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Dependent
public class CaseDetailsPresenter {

    public interface CaseDetailsView extends IsWidget {

        void init( final CaseDetailsPresenter presenter );

        void displayNotification( final String text );

        TextArea getTaskDescriptionTextArea();

  
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    CaseDetailsView view;

    @Inject
    private Caller<CaseInstancesService> casesService;
  

    @Inject
    private Event<CaseRefreshedEvent> caseRefreshed;



    private long currentCaseId = 0;

    private String currentCaseName = "";

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public IsWidget getView() {
        return view;
    }


    

    public void refreshCase() {
        
        casesService.call(new RemoteCallback<CaseSummary>(){

            @Override
            public void callback(CaseSummary response) {
                String text = response.toString() + "\n";
                text += "Human Tasks" + response.getHumanTasksDetails() + "\n";
                text += "Process Tasks" + response.getProcessesDetails() + "\n";
                view.getTaskDescriptionTextArea().setText(text);
            }
        }, new ErrorCallback<Message>(){

            @Override
            public boolean error(Message message, Throwable throwable) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        }).getItem(new CaseKey(currentCaseId));
        

    }

    

    public void onCaseSelectionEvent( @Observes final CaseSelectionEvent event ) {
        this.currentCaseId = event.getCaseId();
        this.currentCaseName = event.getCaseName();
        refreshCase();
    }

    public void onCaseRefreshedEvent( @Observes final CaseRefreshedEvent event ) {
        if ( currentCaseId == event.getCaseId() ) {
            refreshCase();
        }
    }
}
