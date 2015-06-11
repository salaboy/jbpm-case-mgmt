/*
 * Copyright 2014 JBoss Inc
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

package org.jbpm.console.ng.cm.client.quicknewcase;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jbpm.console.ng.cm.client.i18n.Constants;
import org.jbpm.console.ng.cm.model.events.CaseRefreshedEvent;
import org.jbpm.console.ng.cm.model.events.NewCaseEvent;
import org.jbpm.console.ng.cm.service.CaseInstancesService;

@Dependent
public class QuickNewCasePopup extends BaseModal {
    interface Binder
            extends
            UiBinder<Widget, QuickNewCasePopup> {

    }

    @UiField
    public TabPanel tabPanel;

    @UiField
    public Tab basicTab;

    @UiField
    public Tab advancedTab;

    @UiField
    public TextBox deploymentIdText;
    
    @UiField
    public ControlGroup deploymentIdControlGroup;

    @UiField
    public HelpBlock deploymentIdHelpLabel;
   
    @UiField
    public TextBox caseNameText;
    
   

    @UiField
    public ControlGroup caseNameControlGroup;

    @UiField
    public HelpBlock caseNameHelpLabel;
    
    @UiField
    public TextBox recipientText;
     
    @UiField
    public ControlGroup recipientControlGroup;

    @UiField
    public HelpBlock recipientHelpLabel;

    @UiField
    public HelpBlock errorMessages;
    
    @UiField
    public ListBox caseTemplatesListBox;

    @UiField
    public ControlGroup errorMessagesGroup;

    @Inject
    User identity;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<CaseRefreshedEvent> caseRefreshed;

    @Inject
    private Event<NewCaseEvent> newCaseEvent;

    @Inject
    Caller<CaseInstancesService> caseService;

    private static Binder uiBinder = GWT.create( Binder.class );


    private HandlerRegistration textKeyPressHandler;

   

 

    public QuickNewCasePopup() {
        setTitle( Constants.INSTANCE.New_Case_Instance() );

        add( uiBinder.createAndBindUi( this ) );
        init();
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( Constants.INSTANCE.Create(),
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY );

        add( footer );
    }

    public void show() {
        cleanForm();
        super.show();
    }

    private void okButton() {
        if ( validateForm() ) {
            createCaseInstance();
        }
    }

    public void init() {

       

        KeyPressHandler keyPressHandlerText = new KeyPressHandler() {
            @Override
            public void onKeyPress( KeyPressEvent event ) {
                clearErrorMessages();
                if ( event.getNativeEvent().getKeyCode() == 13 ) {
                    createCaseInstance();
                }
            }
        };
        textKeyPressHandler = caseNameText.addKeyPressHandler( keyPressHandlerText );

        caseNameText.setFocus( true );

        caseTemplatesListBox.addItem( "Select Case Template ...", "" );
        caseTemplatesListBox.addItem( "Ad Hoc Template", "org.jbpm.empty.adhoc" );
       
    }

    public void cleanForm() {
        tabPanel.selectTab( 0 );
        basicTab.setActive( true );
        advancedTab.setActive(false);

    
        clearErrorMessages();
        caseNameText.setValue( "" );

     
        caseNameText.setFocus( true );
    }


    public void closePopup() {
        cleanForm();
        hide();
        super.hide();
    }

    private boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();

        if ( caseNameText.getText() != null && caseNameText.getText().trim().length() == 0 ) {
            tabPanel.selectTab( 0 );
            caseNameText.setFocus( true );
            caseNameText.setErrorLabel(caseNameHelpLabel );

            errorMessages.setText( Constants.INSTANCE.Case_Must_Have_A_Name() );
            errorMessagesGroup.setType( ControlGroupType.ERROR );
            caseNameHelpLabel.setText( Constants.INSTANCE.Case_Must_Have_A_Name() );
            caseNameControlGroup.setType( ControlGroupType.ERROR );
            valid = false;
        } else {
            caseNameControlGroup.setType( ControlGroupType.SUCCESS );
        }
        return valid;
    }

   

  


  

    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    private void createCaseInstance() {
        textKeyPressHandler.removeHandler();
       

        if ( caseNameText.getText().equals("") ) {
            
            errorMessages.setText( Constants.INSTANCE.Provide_Case_Name() );
            errorMessagesGroup.setType( ControlGroupType.ERROR );
            tabPanel.selectTab( 1 );
        } else {
            createCase(caseNameText.getText(), recipientText.getText(), deploymentIdText.getText(), caseTemplatesListBox.getValue(caseTemplatesListBox.getSelectedIndex()));
        }

    }


    public void createCase( final String caseName,  final String recipient, final String deploymentId, final String template) {
        

        caseService.call( new RemoteCallback<Long>() {
            @Override
            public void callback( Long caseId ) {
                cleanForm();
                refreshCaseTask( caseId, caseName, Constants.INSTANCE.CaseCreatedWithId( String.valueOf( caseId ) ) );
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( Message message, Throwable throwable ) {
                errorMessages.setText( throwable.getMessage() );
                errorMessagesGroup.setType( ControlGroupType.ERROR );
                //ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).createCaseInstance(caseName, recipient, deploymentId, template  );


    }

    private void refreshCaseTask( Long caseId, String caseName, String msj ) {
        displayNotification( msj );
        newCaseEvent.fire( new NewCaseEvent( caseId, caseName ) );
        closePopup();
    }

   

    

    private void clearErrorMessages(){
        errorMessages.setText( "" );
        caseNameHelpLabel.setText( "" );
        deploymentIdHelpLabel.setText("");
        caseNameControlGroup.setType( ControlGroupType.NONE );
    }

}
