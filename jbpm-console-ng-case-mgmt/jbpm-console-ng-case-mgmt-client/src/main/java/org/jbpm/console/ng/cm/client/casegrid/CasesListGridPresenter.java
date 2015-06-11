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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.cm.client.i18n.Constants;
import org.jbpm.console.ng.cm.model.CaseSummary;
import org.jbpm.console.ng.cm.service.CaseInstancesService;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.paging.PageResponse;

@Dependent
@WorkbenchScreen(identifier = "Cases List")
public class CasesListGridPresenter extends AbstractScreenListPresenter<CaseSummary> {

  

  public interface CaseListView extends ListView<CaseSummary, CasesListGridPresenter> {

    }

  @Inject
  private CaseListView view;

  private Constants constants = GWT.create(Constants.class);


  @Inject
  private Caller<CaseInstancesService> casesService;
  
 

  

  

  public CasesListGridPresenter() {
    dataProvider = new AsyncDataProvider<CaseSummary>() {

      @Override
      protected void onRangeChanged(HasData<CaseSummary> display) {
        view.showBusyIndicator(constants.Loading());
        final Range visibleRange = display.getVisibleRange();
          getData(visibleRange);

      }
    };
  }

  @Override
    protected ListView getListView() {
        return view;
    }

    @Override
    public void getData(Range visibleRange) {
       ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
        if (currentFilter == null) {
          currentFilter = new PortableQueryFilter(visibleRange.getStart(),
                  visibleRange.getLength(),
                  false, "",
                  (columnSortList.size() > 0) ? columnSortList.get(0)
                  .getColumn().getDataStoreName() : "",
                  (columnSortList.size() > 0) ? columnSortList.get(0)
                  .isAscending() : true);

        }
        // If we are refreshing after a search action, we need to go back to offset 0
        if (currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
                || currentFilter.getParams().get("textSearch") == null || currentFilter.getParams().get("textSearch").equals("")) {
          currentFilter.setOffset(visibleRange.getStart());
          currentFilter.setCount(visibleRange.getLength());
          currentFilter.setFilterParams("");
        } else {
          currentFilter.setFilterParams("(LOWER(t.name) like '"+currentFilter.getParams().get("textSearch")
                                        +"' or LOWER(t.description) like '"+currentFilter.getParams().get("textSearch")+"') ");
          currentFilter.setOffset(0);
          currentFilter.setCount(view.getListGrid().getPageSize());
        }
        
        
        
        currentFilter.getParams().put("userId", identity.getIdentifier());
        
        currentFilter.setOrderBy((columnSortList.size() > 0) ? columnSortList.get(0)
                .getColumn().getDataStoreName() : "");
        currentFilter.setIsAscending((columnSortList.size() > 0) ? columnSortList.get(0)
                .isAscending() : true);

        casesService.call(new RemoteCallback<PageResponse<CaseSummary>>() {
          @Override
          public void callback(PageResponse<CaseSummary> response) {
              updateDataOnCallback(response);
          }
        }, new ErrorCallback<Message>() {
          @Override
          public boolean error(Message message, Throwable throwable) {
            view.hideBusyIndicator();
            view.displayNotification("Error: Getting Cases: " + throwable.toString());
            GWT.log(message.toString());
            return true;
          }
        }).getData(currentFilter);
    }
 
  
  @WorkbenchPartTitle
  public String getTitle() {
    return constants.Cases_List();
  }

  @WorkbenchPartView
  public UberView<CasesListGridPresenter> getView() {
    return view;
  }

  
}
