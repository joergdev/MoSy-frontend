package com.github.joergdev.mosy.frontend.view;

import java.util.ArrayList;
import java.util.List;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import com.github.joergdev.mosy.api.model.MockData;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.api.model.RecordConfig;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.utils.ColumnModel;
import com.github.joergdev.mosy.frontend.utils.WidthUnit;
import com.github.joergdev.mosy.frontend.view.controller.InterfaceVC;
import com.github.joergdev.mosy.frontend.view.core.AbstractSubView;

public class InterfaceMethodVS extends AbstractSubView<InterfaceV, InterfaceVC>
{
  public InterfaceMethodVS(InterfaceV view)
  {
    super(view);
  }

  //--- Method data -------------------------------------

  private String name;

  private String servicePath;

  private boolean mockDisabled;
  private boolean mockDisabledOnStartup;

  private boolean routingOnNoMockData;

  private boolean record;

  private Integer countCalls;

  // component states
  private boolean routingOnNoMockDataDisabled;
  private boolean recordDisabled;
  private boolean servicePathRendered = true;
  private boolean routingRendered = true;
  private boolean deleteMethodDisabled = true;

  // --- End Method data --------------------------------

  //--- RecordConfig overview data -------------------------------------

  private final List<RecordConfig> tblRecordConfigs = new ArrayList<>();

  private List<RecordConfig> selectedRecordConfigs;

  private final List<ColumnModel> tblRecordConfigsColumns = new ArrayList<>();

  //component states
  private boolean newRecordConfigDisabled = true;
  private boolean editRecordConfigDisabled = true;
  private boolean deleteRecordConfigsDisabled = true;

  // --- End RecordConfig overview data --------------------------------

  //--- RecordConfig data -------------------------------------

  private String rcTitle;
  private boolean rcEnabled;
  private String rcRequestdata;

  // component states
  private boolean deleteRecordConfigDisabled = true;

  // --- End RecordConfig data --------------------------------

  //--- MockData overview data ------------------------------------- 

  private final List<MockData> tblMockData = new ArrayList<>();

  private List<MockData> selectedMockDataList;

  private final List<ColumnModel> tblMockDataColumns = new ArrayList<>();

  //component states
  private boolean newMockDataDisabled = true;
  private boolean editMockDataDisabled = true;
  private boolean deleteMockDataOverviewDisabled = true;

  // --- End MockData overview data --------------------------------

  //--- MockData data -------------------------------------

  private final List<MockSession> mockSessions = new ArrayList<>();
  private MockSession mockSessionSelected;

  private String mdTitle;
  private boolean mdActive;

  private String mdRequest;
  private String mdResponse;

  private String mdCreated;
  private Integer mdCountCalls;

  // component states
  private boolean deleteMockDataDisabled = true;

  // --- End MockData data --------------------------------

  public void init()
  {
    initTblRecordConfigs();
    initTblMockData();
  }

  //--- Method data -------------------------------------

  public void handleMockDisabled()
  {
    controller.updateComponentsMethod();
  }

  public void handleRoutingOnNoMockData()
  {
    controller.updateComponentsMethod();
  }

  public void saveMethod()
  {
    controller.saveMethod();
  }

  public void deleteMethod()
  {
    controller.deleteMethod();
  }

  //--- End Method data -------------------------------------

  //--- RecordConfig overview data -------------------------------------

  private void initTblRecordConfigs()
  {
    ColumnModel colTitle = new ColumnModel(Resources.getLabel("title"), "title");
    colTitle.setWidth(75, WidthUnit.PERCENT);
    tblRecordConfigsColumns.add(colTitle);

    ColumnModel colActive = new ColumnModel(Resources.getLabel("active"), "enabled");
    colActive.setWidth(25, WidthUnit.PERCENT);
    tblRecordConfigsColumns.add(colActive);
  }

  public void newRecordConfig()
  {
    controller.newRecordConfig();
  }

  public void editRecordConfig()
  {
    controller.editRecordConfig();
  }

  public void deleteRecordConfigs()
  {
    controller.deleteRecordConfigs();
  }

  public void onRecordConfigsRowSelect(SelectEvent event)
  {
    controller.handleRecordConfigsSelection();
  }

  public void onRecordConfigsRowUnselect(UnselectEvent event)
  {
    controller.handleRecordConfigsSelection();
  }

  public void onRecordConfigsRowDoubleClick(SelectEvent event)
  {
    controller.editRecordConfig();
  }

  //--- End RecordConfig overview data -------------------------------------

  //--- RecordConfig data -------------------------------------

  public void saveRecordConfig()
  {
    controller.saveRecordConfig();
  }

  public void deleteRecordConfig()
  {
    controller.deleteRecordConfig();
  }

  //--- End RecordConfig data -------------------------------------

  //--- MockData overview data -------------------------------------

  private void initTblMockData()
  {
    ColumnModel colMocksession = new ColumnModel(Resources.getLabel("mock_session"), "mockSessionID");
    colMocksession.setWidth(10, WidthUnit.PERCENT);
    tblMockDataColumns.add(colMocksession);

    ColumnModel colTitle = new ColumnModel(Resources.getLabel("title"), "title");
    colTitle.setWidth(50, WidthUnit.PERCENT);
    tblMockDataColumns.add(colTitle);

    ColumnModel colActive = new ColumnModel(Resources.getLabel("active"), "active");
    colActive.setWidth(5, WidthUnit.PERCENT);
    tblMockDataColumns.add(colActive);

    ColumnModel colCountCalls = new ColumnModel(Resources.getLabel("count_calls"), "countCalls");
    colCountCalls.setWidth(10, WidthUnit.PERCENT);
    tblMockDataColumns.add(colCountCalls);

    ColumnModel colCreated = new ColumnModel(Resources.getLabel("created"), "createdAsString");
    colCreated.setWidth(25, WidthUnit.PERCENT);
    tblMockDataColumns.add(colCreated);
  }

  public void newMockData()
  {
    controller.newMockData();
  }

  public void editMockData()
  {
    controller.editMockData();
  }

  public void deleteMockDataFromOverview()
  {
    controller.deleteMockDataFromOverview();
  }

  public void onMockDataRowSelect(SelectEvent event)
  {
    controller.handleMockDataSelection();
  }

  public void onMockDataRowUnselect(UnselectEvent event)
  {
    controller.handleMockDataSelection();
  }

  public void onMockDataRowDoubleClick(SelectEvent event)
  {
    controller.editMockData();
  }

  public void uploadMockData()
  {
    controller.uploadMockData();
  }

  //--- End MockData overview data -------------------------------------

  //--- MockData data -------------------------------------

  public void saveMockData()
  {
    controller.saveMockData();
  }

  public void deleteMockData()
  {
    controller.deleteMockData();
  }

  //--- End MockData data -------------------------------------

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getServicePath()
  {
    return servicePath;
  }

  public void setServicePath(String servicePath)
  {
    this.servicePath = servicePath;
  }

  public boolean isMockDisabled()
  {
    return mockDisabled;
  }

  public void setMockDisabled(boolean mockDisabled)
  {
    this.mockDisabled = mockDisabled;
  }

  public boolean isMockDisabledOnStartup()
  {
    return mockDisabledOnStartup;
  }

  public void setMockDisabledOnStartup(boolean mockDisabledOnStartup)
  {
    this.mockDisabledOnStartup = mockDisabledOnStartup;
  }

  public boolean isRoutingOnNoMockData()
  {
    return routingOnNoMockData;
  }

  public void setRoutingOnNoMockData(boolean routingOnNoMockData)
  {
    this.routingOnNoMockData = routingOnNoMockData;
  }

  public boolean isRecord()
  {
    return record;
  }

  public void setRecord(boolean record)
  {
    this.record = record;
  }

  public Integer getCountCalls()
  {
    return countCalls;
  }

  public void setCountCalls(Integer countCalls)
  {
    this.countCalls = countCalls;
  }

  public boolean isRoutingOnNoMockDataDisabled()
  {
    return routingOnNoMockDataDisabled;
  }

  public void setRoutingOnNoMockDataDisabled(boolean routingOnNoMockDataDisabled)
  {
    this.routingOnNoMockDataDisabled = routingOnNoMockDataDisabled;
  }

  public List<MockData> getSelectedMockDataList()
  {
    if (selectedMockDataList == null)
    {
      selectedMockDataList = new ArrayList<>();
    }

    return selectedMockDataList;
  }

  public void setSelectedMockDataList(List<MockData> selectedMockDataList)
  {
    this.selectedMockDataList = selectedMockDataList;
  }

  public boolean isNewMockDataDisabled()
  {
    return newMockDataDisabled;
  }

  public void setNewMockDataDisabled(boolean newMockDataDisabled)
  {
    this.newMockDataDisabled = newMockDataDisabled;
  }

  public boolean isEditMockDataDisabled()
  {
    return editMockDataDisabled;
  }

  public void setEditMockDataDisabled(boolean editMockDataDisabled)
  {
    this.editMockDataDisabled = editMockDataDisabled;
  }

  public boolean isDeleteMockDataOverviewDisabled()
  {
    return deleteMockDataOverviewDisabled;
  }

  public void setDeleteMockDataOverviewDisabled(boolean deleteMockDataOverviewDisabled)
  {
    this.deleteMockDataOverviewDisabled = deleteMockDataOverviewDisabled;
  }

  public String getMdTitle()
  {
    return mdTitle;
  }

  public void setMdTitle(String mdTitle)
  {
    this.mdTitle = mdTitle;
  }

  public boolean isMdActive()
  {
    return mdActive;
  }

  public void setMdActive(boolean mdActive)
  {
    this.mdActive = mdActive;
  }

  public String getMdRequest()
  {
    return mdRequest;
  }

  public void setMdRequest(String mdRequest)
  {
    this.mdRequest = mdRequest;
  }

  public String getMdResponse()
  {
    return mdResponse;
  }

  public void setMdResponse(String mdResponse)
  {
    this.mdResponse = mdResponse;
  }

  public String getMdCreated()
  {
    return mdCreated;
  }

  public void setMdCreated(String mdCreated)
  {
    this.mdCreated = mdCreated;
  }

  public Integer getMdCountCalls()
  {
    return mdCountCalls;
  }

  public void setMdCountCalls(Integer mdCountCalls)
  {
    this.mdCountCalls = mdCountCalls;
  }

  public boolean isDeleteMockDataDisabled()
  {
    return deleteMockDataDisabled;
  }

  public void setDeleteMockDataDisabled(boolean deleteMockDataDisabled)
  {
    this.deleteMockDataDisabled = deleteMockDataDisabled;
  }

  public List<MockData> getTblMockData()
  {
    return tblMockData;
  }

  public List<ColumnModel> getTblMockDataColumns()
  {
    return tblMockDataColumns;
  }

  public boolean isRecordDisabled()
  {
    return recordDisabled;
  }

  public void setRecordDisabled(boolean recordDisabled)
  {
    this.recordDisabled = recordDisabled;
  }

  public boolean isServicePathRendered()
  {
    return servicePathRendered;
  }

  public String getRcTitle()
  {
    return rcTitle;
  }

  public void setRcTitle(String rcTitle)
  {
    this.rcTitle = rcTitle;
  }

  public boolean isRcEnabled()
  {
    return rcEnabled;
  }

  public void setRcEnabled(boolean rcEnabled)
  {
    this.rcEnabled = rcEnabled;
  }

  public String getRcRequestdata()
  {
    return rcRequestdata;
  }

  public void setRcRequestdata(String rcRequestdata)
  {
    this.rcRequestdata = rcRequestdata;
  }

  public boolean isDeleteRecordConfigDisabled()
  {
    return deleteRecordConfigDisabled;
  }

  public void setDeleteRecordConfigDisabled(boolean deleteRecordConfigDisabled)
  {
    this.deleteRecordConfigDisabled = deleteRecordConfigDisabled;
  }

  public void setServicePathRendered(boolean servicePathRendered)
  {
    this.servicePathRendered = servicePathRendered;
  }

  public boolean isRoutingRendered()
  {
    return routingRendered;
  }

  public void setRoutingRendered(boolean routingRendered)
  {
    this.routingRendered = routingRendered;
  }

  public boolean isDeleteMethodDisabled()
  {
    return deleteMethodDisabled;
  }

  public void setDeleteMethodDisabled(boolean deleteMethodDisabled)
  {
    this.deleteMethodDisabled = deleteMethodDisabled;
  }

  public List<RecordConfig> getSelectedRecordConfigs()
  {
    if (selectedRecordConfigs == null)
    {
      selectedRecordConfigs = new ArrayList<>();
    }

    return selectedRecordConfigs;
  }

  public void setSelectedRecordConfigs(List<RecordConfig> selectedRecordConfigs)
  {
    this.selectedRecordConfigs = selectedRecordConfigs;
  }

  public boolean isNewRecordConfigDisabled()
  {
    return newRecordConfigDisabled;
  }

  public void setNewRecordConfigDisabled(boolean newRecordConfigDisabled)
  {
    this.newRecordConfigDisabled = newRecordConfigDisabled;
  }

  public boolean isEditRecordConfigDisabled()
  {
    return editRecordConfigDisabled;
  }

  public void setEditRecordConfigDisabled(boolean editRecordConfigDisabled)
  {
    this.editRecordConfigDisabled = editRecordConfigDisabled;
  }

  public boolean isDeleteRecordConfigsDisabled()
  {
    return deleteRecordConfigsDisabled;
  }

  public void setDeleteRecordConfigsDisabled(boolean deleteRecordConfigsDisabled)
  {
    this.deleteRecordConfigsDisabled = deleteRecordConfigsDisabled;
  }

  public List<RecordConfig> getTblRecordConfigs()
  {
    return tblRecordConfigs;
  }

  public List<ColumnModel> getTblRecordConfigsColumns()
  {
    return tblRecordConfigsColumns;
  }

  public List<MockSession> getMockSessions()
  {
    return mockSessions;
  }

  public MockSession getMockSessionSelected()
  {
    return mockSessionSelected;
  }

  public void setMockSessionSelected(MockSession mockSessionSelected)
  {
    this.mockSessionSelected = mockSessionSelected;
  }
}