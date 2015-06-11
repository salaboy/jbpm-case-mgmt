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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "CaseDetailsViewImpl.html")
public class CaseDetailsViewImpl extends Composite implements CaseDetailsPresenter.CaseDetailsView {

    private CaseDetailsPresenter presenter;



    @Inject
    @DataField
    public TextArea taskDescriptionTextArea;

   


    @Inject
    @DataField
    public ControlLabel taskDescriptionLabel;

 
    
    @Inject
    private PlaceManager placeManager;
    
    

    @Inject
    private Event<NotificationEvent> notification;
    

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( CaseDetailsPresenter presenter ) {
        this.presenter = presenter;
        
        

        taskDescriptionLabel.add( new HTMLPanel( constants.Description() ) );
       
        
    }

   

   
    @Override
    public TextArea getTaskDescriptionTextArea() {
        return taskDescriptionTextArea;
    }


    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

   
}
