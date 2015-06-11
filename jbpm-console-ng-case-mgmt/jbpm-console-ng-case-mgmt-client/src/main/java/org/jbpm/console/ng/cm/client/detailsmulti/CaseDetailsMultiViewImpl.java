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
package org.jbpm.console.ng.cm.client.detailsmulti;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.MINI;

import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.cm.client.details.CaseDetailsPresenter;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import com.google.gwt.user.client.ui.ScrollPanel;


@Dependent
public class CaseDetailsMultiViewImpl extends AbstractTabbedDetailsView<CaseDetailsMultiPresenter>
        implements CaseDetailsMultiPresenter.CaseDetailsMultiView, RequiresResize {

    interface Binder
            extends
            UiBinder<Widget, CaseDetailsMultiViewImpl> {

    }

    

    private CaseDetailsPresenter caseDetailsPresenter;

    

    
    private ScrollPanel caseDetailsScrollPanel = new ScrollPanel();

    
    @Override
    public void init( final CaseDetailsMultiPresenter presenter ) {
        super.init( presenter );
    }

    @Override
    public void initTabs() {
        
        tabPanel.addTab( "Case Details", Constants.INSTANCE.Details() );
        
        caseDetailsScrollPanel.add(caseDetailsPresenter.getView());
        
        
        ( (HTMLPanel) tabPanel.getWidget( 0 ) ).add( caseDetailsScrollPanel ); 

        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection( SelectionEvent<Integer> event ) {
                caseDetailsPresenter.refreshCase();
//                if ( event.getSelectedItem() == 0 ) {
//                    
//                } 
                
            }
        } );
    }

    @Override
    public Button getCloseButton() {
        return new Button() {
            {
                setIcon( IconType.REMOVE );
                setTitle( Constants.INSTANCE.Close() );
                setSize( MINI );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.closeDetails();
                    }
                } );
            }
        };
    }

    @Override
    public void setupPresenters( final CaseDetailsPresenter taskDetailsPresenter) {
        this.caseDetailsPresenter = taskDetailsPresenter;
    }

    @Override
    public IsWidget getRefreshButton() {
        return new Button() {
            {
                setIcon( IconType.REFRESH );
                setTitle( Constants.INSTANCE.Refresh() );
                setSize( MINI );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.refresh();
                    }
                } );
            }
        };
    }

    @Override
    public void onResize() {
        super.onResize(); 
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
               tabPanel.setHeight(CaseDetailsMultiViewImpl.this.getParent().getOffsetHeight()-30+"px");
              
               caseDetailsScrollPanel.setHeight(CaseDetailsMultiViewImpl.this.getParent().getOffsetHeight()-30+"px");
               
            }
        });
    }
    
    

}
