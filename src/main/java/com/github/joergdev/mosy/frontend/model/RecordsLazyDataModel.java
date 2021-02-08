package com.github.joergdev.mosy.frontend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import com.github.joergdev.mosy.api.model.Record;
import com.github.joergdev.mosy.api.model.RecordSession;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.view.controller.core.AbstractViewController;
import com.github.joergdev.mosy.shared.Utils;

public class RecordsLazyDataModel extends LazyDataModel<Record>
{
  private final String uiPath;
  private final AbstractViewController<?> viewController;

  private RecordSession filterSession;

  private final List<Record> allRecordsLoaded = new ArrayList<>();
  private Integer lastLoadedId;
  private boolean allLoaded = false;

  public RecordsLazyDataModel(String uiPath, AbstractViewController<?> viewController)
  {
    this.uiPath = uiPath;
    this.viewController = viewController;
  }

  @Override
  public List<Record> load(int first, int pageSize, String sortField, SortOrder sortOrder,
                           Map<String, Object> filters)
  {
    return load(first, pageSize);
  }

  @Override
  public List<Record> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters)
  {
    return load(first, pageSize);
  }

  private List<Record> load(int first, int pageSize)
  {
    if (!allLoaded && first >= allRecordsLoaded.size())
    {
      List<Record> records = viewController.invokeApiCall(apiClient -> apiClient.loadRecords(pageSize,
          lastLoadedId, filterSession == null
              ? null
              : filterSession.getRecordSessionID()))
          .getRecords();

      if (!Utils.isCollectionEmpty(records))
      {
        lastLoadedId = records.stream().map(r -> r.getRecordId()).min(Utils.getDefaultComparator())
            .orElse(null);
      }

      allRecordsLoaded.addAll(records);

      if (!allLoaded)
      {
        allLoaded = records.size() < pageSize;
      }

      setRowCount(allRecordsLoaded.size() + (allLoaded
          ? 0
          : 1));

      return records;
    }
    else
    {
      return allRecordsLoaded.isEmpty()
          ? allRecordsLoaded
          : allRecordsLoaded.subList(first, Utils.min(first + pageSize, allRecordsLoaded.size()));
    }
  }

  @Override
  public Record getRowData(String rowKey)
  {
    return getWrappedData().stream().filter(r -> r.getRecordId().toString().equals(rowKey)).findAny()
        .orElse(null);
  }

  @Override
  public Object getRowKey(Record record)
  {
    return record == null
        ? null
        : record.getRecordId();
  }

  public void removeRecord(Record r)
  {
    allRecordsLoaded.remove(r);
  }

  public void reset()
  {
    allRecordsLoaded.clear();
    lastLoadedId = null;
    allLoaded = false;
    setWrappedData(null);
    setRowCount(0);

    DataTable uiDataTable = (DataTable) JsfUtils.getFacesContext().getViewRoot().findComponent(uiPath);
    if (uiDataTable != null)
    {
      uiDataTable.setFirst(0);
    }
  }

  public RecordSession getFilterSession()
  {
    return filterSession;
  }

  public void setFilterSession(RecordSession filterSession)
  {
    this.filterSession = filterSession;
  }
}