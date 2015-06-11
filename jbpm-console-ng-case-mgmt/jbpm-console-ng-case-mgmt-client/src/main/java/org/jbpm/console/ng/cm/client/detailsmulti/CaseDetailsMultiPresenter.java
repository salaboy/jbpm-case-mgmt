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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.cm.model.events.CaseSelectionEvent;

import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsPresenter;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView.TabbedDetailsView;
import org.jbpm.console.ng.cm.client.details.CaseDetailsPresenter;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
@WorkbenchScreen(identifier = "Case Details Multi", preferredWidth = 500)
public class CaseDetailsMultiPresenter extends AbstractTabbedDetailsPresenter {

    public interface CaseDetailsMultiView
            extends TabbedDetailsView<CaseDetailsMultiPresenter> {

        void setupPresenters(
                final CaseDetailsPresenter taskDetailsPresenter
        );

        IsWidget getRefreshButton();

        IsWidget getCloseButton();
    }

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Inject
    private Event<CaseSelectionEvent> caseSelected;

    @Inject
    private CaseDetailsPresenter taskDetailsPresenter;

    @Inject
    private CaseDetailsMultiView view;

    @PostConstruct
    public void init() {
        view.setupPresenters(taskDetailsPresenter);
    }

    @WorkbenchPartView
    public UberView<CaseDetailsMultiPresenter> getView() {
        return view;
    }

    @DefaultPosition
    public Position getPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.Details();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        super.onStartup(place);
    }

    public void onCaseSelectionEvent(@Observes final CaseSelectionEvent event) {
        deploymentId = String.valueOf(event.getCaseId());
        processId = event.getCaseName();

        view.getTabPanel().getTabWidget(0).getParent().setVisible(true);

        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(this.place, String.valueOf(deploymentId) + " - " + processId));

    }

    public void refresh() {
        caseSelected.fire(new CaseSelectionEvent(Long.valueOf(deploymentId), processId));
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push(MenuFactory.CustomMenuBuilder element) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return view.getRefreshButton();
                            }
                        };
                    }
                }).endMenu()
                .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push(MenuFactory.CustomMenuBuilder element) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {
                            @Override
                            public IsWidget build() {
                                return view.getCloseButton();
                            }
                        };
                    }
                }).endMenu().build();
    }
}
