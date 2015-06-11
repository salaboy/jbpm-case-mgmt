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
package org.jbpm.console.ng.cm.client.casegrid;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;


import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance.QuickNewProcessInstancePopup;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.jbpm.console.ng.cm.client.i18n.Constants;
import org.jbpm.console.ng.cm.client.quicknewcase.QuickNewCasePopup;
import org.jbpm.console.ng.cm.client.resources.CaseManagementImages;
import org.jbpm.console.ng.cm.model.CaseSummary;
import org.jbpm.console.ng.cm.model.events.CaseSelectionEvent;
import org.jbpm.console.ng.cm.model.events.NewCaseEvent;
import org.jbpm.console.ng.ht.client.editors.quicknewtask.QuickNewTaskPopup;

@Dependent
public class CasesListGridViewImpl extends AbstractListView<CaseSummary, CasesListGridPresenter>
        implements CasesListGridPresenter.CaseListView {

    interface Binder
            extends
            UiBinder<Widget, CasesListGridViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    private final Constants constants = GWT.create(Constants.class);
    private final CaseManagementImages images = GWT.create(CaseManagementImages.class);
    

    @Inject
    private Event<CaseSelectionEvent> caseSelected;

    @Inject
    private QuickNewCasePopup newCasePopup;
    
    @Inject
    private QuickNewTaskPopup quickNewTaskPopup;

    @Inject
    private QuickNewProcessInstancePopup quickNewProcessInstancePopup;



    @Override
    public void init(final CasesListGridPresenter presenter) {
        List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(constants.Id());
        List<String> initColumns = new ArrayList<String>();
        initColumns.add(constants.Case());
        initColumns.add(constants.Recipient());
        initColumns.add(constants.Description());
        initColumns.add(constants.Status());
        super.init(presenter, new GridGlobalPreferences("CaseListGrid", initColumns, bannedColumns));

        

        listGrid.setEmptyTableCaption(constants.No_Cases_Found());
        selectionModel = new NoSelectionModel<CaseSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                boolean close = false;
                if (selectedRow == -1) {
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.setRowStyles(selectedStyles);
                    listGrid.redraw();

                } else if (listGrid.getKeyboardSelectedRow() != selectedRow) {
                    listGrid.setRowStyles(selectedStyles);
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                } else {
                    close = true;
                }

                selectedItem = selectionModel.getLastSelectedObject();

                DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest("Case Details Multi");
                PlaceStatus status = placeManager.getStatus(defaultPlaceRequest);
               
                if (status == PlaceStatus.CLOSE) {
                    placeManager.goTo(defaultPlaceRequest);
                    caseSelected.fire(new CaseSelectionEvent(selectedItem.getCaseId(), selectedItem.getCaseName()));
                } else if (status == PlaceStatus.OPEN && !close) {
                    caseSelected.fire(new CaseSelectionEvent(selectedItem.getCaseId(), selectedItem.getCaseName()));
                } else if (status == PlaceStatus.OPEN && close) {
                    placeManager.closePlace("Case Details Multi");
                }

            }
        });

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager(new DefaultSelectionEventManager.EventTranslator<CaseSummary>() {

                    @Override
                    public boolean clearCurrentSelection(CellPreviewEvent<CaseSummary> event) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<CaseSummary> event) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                            // Ignore if the event didn't occur in the correct column.
                            if (listGrid.getColumnIndex(actionsColumn) == event.getColumn()) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }
                        }
                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }
                });
        listGrid.setSelectionModel(selectionModel, noActionColumnManager);

        listGrid.setRowStyles(selectedStyles);
        initExtraButtons();
        initFiltersBar();
    }

    private void initFiltersBar() {
        HorizontalPanel filtersBar = new HorizontalPanel();
        Label filterLabel = new Label();
        filterLabel.setStyleName("");
        filterLabel.setText(constants.Filters() + ": ");

        listGrid.getCenterToolbar().add(filtersBar);

    }

    private void initExtraButtons() {
        Button newTaskButton = new Button();
        newTaskButton.setTitle(constants.New_Case());
        newTaskButton.setIcon( IconType.PLUS_SIGN );
        newTaskButton.setTitle( Constants.INSTANCE.New_Case() );
        newTaskButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                newCasePopup.show();
            }
        });
        listGrid.getLeftToolbar().add(newTaskButton);

    }

    @Override
    public void initColumns() {
        initCellPreview();
        Column taskIdColumn = initTaskIdColumn();
        Column taskNameColumn = initTaskNameColumn();
        Column descriptionColumn = initTaskDescriptionColumn();
        
        Column statusColumn = initTaskStatusColumn();
        Column recipientColumn = initCaseRecipientColumn();
        actionsColumn = initActionsColumn();

        List<ColumnMeta<CaseSummary>> columnMetas = new ArrayList<ColumnMeta<CaseSummary>>();
        columnMetas.add(new ColumnMeta<CaseSummary>(taskIdColumn, constants.Id()));
        columnMetas.add(new ColumnMeta<CaseSummary>(taskNameColumn, constants.Case()));
        columnMetas.add(new ColumnMeta<CaseSummary>(descriptionColumn, constants.Description()));
        columnMetas.add(new ColumnMeta<CaseSummary>(recipientColumn, constants.Recipient()));
        columnMetas.add(new ColumnMeta<CaseSummary>(statusColumn, constants.Status()));
        
        columnMetas.add(new ColumnMeta<CaseSummary>(actionsColumn, constants.Actions()));
        listGrid.addColumns(columnMetas);
    }

    private void initCellPreview() {
        listGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<CaseSummary>() {

            @Override
            public void onCellPreview(final CellPreviewEvent<CaseSummary> event) {

                if (BrowserEvents.MOUSEOVER.equalsIgnoreCase(event.getNativeEvent().getType())) {
                    onMouseOverGrid(event);
                }

            }
        });

    }

    private void onMouseOverGrid(final CellPreviewEvent<CaseSummary> event) {
        CaseSummary caseInstance = event.getValue();

        if (caseInstance.getDescription() != null) {
            listGrid.setTooltip(listGrid.getKeyboardSelectedRow(), event.getColumn(), caseInstance.getDescription());
        }
    }

    private Column initTaskIdColumn() {
        Column<CaseSummary, Number> caseIdColumn = new Column<CaseSummary, Number>(new NumberCell()) {
            @Override
            public Number getValue(CaseSummary object) {
                return object.getCaseId();
            }
        };
        caseIdColumn.setSortable(true);
        caseIdColumn.setDataStoreName("c.id");
        return caseIdColumn;
    }

    private Column initTaskNameColumn() {
        Column<CaseSummary, String> taskNameColumn = new Column<CaseSummary, String>(new TextCell()) {
            @Override
            public String getValue(CaseSummary object) {
                return object.getCaseName();
            }
        };
        taskNameColumn.setSortable(true);
        taskNameColumn.setDataStoreName("c.name");
        return taskNameColumn;
    }

    private Column initTaskDescriptionColumn() {
        Column<CaseSummary, String> descriptionColumn = new Column<CaseSummary, String>(new TextCell()) {
            @Override
            public String getValue(CaseSummary object) {
                return object.getDescription();
            }
        };
        descriptionColumn.setSortable(true);
        descriptionColumn.setDataStoreName("c.description");
        return descriptionColumn;
    }

    

    private Column initTaskStatusColumn() {
        Column<CaseSummary, String> statusColumn = new Column<CaseSummary, String>(new TextCell()) {
            @Override
            public String getValue(CaseSummary object) {
                return object.getStatus();
            }
        };
        statusColumn.setSortable(true);
        statusColumn.setDataStoreName("c.status");
        return statusColumn;
    }

    private Column initCaseRecipientColumn() {
        Column<CaseSummary, String> recipientColumn = new Column<CaseSummary, String>(new TextCell()) {
            @Override
            public String getValue(CaseSummary object) {
                return object.getRecipient();
            }
        };
        recipientColumn.setSortable(true);
        recipientColumn.setDataStoreName("c.recipient");
        return recipientColumn;
    }

    
    public void onCaseRefreshedEvent(@Observes TaskRefreshedEvent event) {
        presenter.refreshGrid();
    }

    private Column initActionsColumn() {
        List<HasCell<CaseSummary, ?>> cells = new LinkedList<HasCell<CaseSummary, ?>>();
        

        cells.add(new CreateTaskActionHasCell(constants.Create_Task(), new ActionCell.Delegate<CaseSummary>() {
            @Override
            public void execute(CaseSummary caseDefinition) {
                 quickNewTaskPopup.show(caseDefinition.getCaseId());
            }
        }));
        
        cells.add(new CreateProcessActionHasCell(constants.Create_Process(), new ActionCell.Delegate<CaseSummary>() {
            @Override
            public void execute(CaseSummary caseDefinition) {
                quickNewProcessInstancePopup.show(caseDefinition.getCaseId());
            }
        }));
        
        cells.add(new CreateSubCaseActionHasCell(constants.Create_SubCase(), new ActionCell.Delegate<CaseSummary>() {
            @Override
            public void execute(CaseSummary caseDefinition) {
                newCasePopup.show();
            }
        }));


        CompositeCell<CaseSummary> cell = new CompositeCell<CaseSummary>(cells);
        Column<CaseSummary, CaseSummary> actionsColumn = new Column<CaseSummary, CaseSummary>(cell) {
            @Override
            public CaseSummary getValue(CaseSummary object) {
                return object;
            }
        };
        return actionsColumn;

    }

    public void refreshNewCase(@Observes NewCaseEvent newCase) {
        presenter.refreshGrid();
        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Case Details Multi"));
        if (status == PlaceStatus.OPEN) {
            caseSelected.fire(new CaseSelectionEvent(newCase.getNewCaseId(), newCase.getNewCaseName()));
        } else {
            placeManager.goTo("Case Details Multi");
            caseSelected.fire(new CaseSelectionEvent(newCase.getNewCaseId(), newCase.getNewCaseName()));
        }

        selectionModel.setSelected(new CaseSummary(newCase.getNewCaseId(), newCase.getNewCaseName()), true);
    }

    protected class CreateProcessActionHasCell implements HasCell<CaseSummary, CaseSummary> {

        private ActionCell<CaseSummary> cell;

        public CreateProcessActionHasCell(String text, ActionCell.Delegate<CaseSummary> delegate) {
            cell = new ActionCell<CaseSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, CaseSummary value, SafeHtmlBuilder sb) {
                    
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.createCaseGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Create_Process() + "' style='margin-right:5px;'>");
                        mysb.append(imageProto.getSafeHtml());
                        mysb.appendHtmlConstant("</span>");
                        sb.append(mysb.toSafeHtml());
                  
                }
            };
        }

        @Override
        public Cell<CaseSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<CaseSummary, CaseSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public CaseSummary getValue(CaseSummary object) {
            return object;
        }
    }
    
    
    protected class CreateTaskActionHasCell implements HasCell<CaseSummary, CaseSummary> {

        private ActionCell<CaseSummary> cell;

        public CreateTaskActionHasCell(String text, ActionCell.Delegate<CaseSummary> delegate) {
            cell = new ActionCell<CaseSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, CaseSummary value, SafeHtmlBuilder sb) {
                    
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.createCaseGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Create_Task() + "' style='margin-right:5px;'>");
                        mysb.append(imageProto.getSafeHtml());
                        mysb.appendHtmlConstant("</span>");
                        sb.append(mysb.toSafeHtml());
                  
                }
            };
        }

        @Override
        public Cell<CaseSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<CaseSummary, CaseSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public CaseSummary getValue(CaseSummary object) {
            return object;
        }
        
    }

    protected class CreateSubCaseActionHasCell implements HasCell<CaseSummary, CaseSummary> {

        private ActionCell<CaseSummary> cell;

        public CreateSubCaseActionHasCell(String text, ActionCell.Delegate<CaseSummary> delegate) {
            cell = new ActionCell<CaseSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, CaseSummary value, SafeHtmlBuilder sb) {
                    
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.createCaseGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Create_SubCase() + "' style='margin-right:5px;'>");
                        mysb.append(imageProto.getSafeHtml());
                        mysb.appendHtmlConstant("</span>");
                        sb.append(mysb.toSafeHtml());
                  
                }
            };
        }

        @Override
        public Cell<CaseSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<CaseSummary, CaseSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public CaseSummary getValue(CaseSummary object) {
            return object;
        }
    }

    private PlaceStatus getPlaceStatus(String place) {
        DefaultPlaceRequest defaultPlaceRequest = new DefaultPlaceRequest(place);
        PlaceStatus status = placeManager.getStatus(defaultPlaceRequest);
        return status;
    }

    private void closePlace(String place) {
        if (getPlaceStatus(place) == PlaceStatus.OPEN) {
            placeManager.closePlace(place);
        }
    }
}
