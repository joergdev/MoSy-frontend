package de.joergdev.mosy.frontend.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import de.joergdev.mosy.api.model.HttpMethod;
import de.joergdev.mosy.api.model.MockData;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.api.model.PathParam;
import de.joergdev.mosy.api.model.RecordConfig;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.frontend.model.YesNoGlobalOrRecordConfigIndividuallyType;
import de.joergdev.mosy.frontend.utils.ColumnModel;
import de.joergdev.mosy.frontend.utils.WidthUnit;
import de.joergdev.mosy.frontend.view.controller.InterfaceVC;
import de.joergdev.mosy.frontend.view.core.AbstractSubView;

public class InterfaceMethodVS extends AbstractSubView<InterfaceV, InterfaceVC>
{
  public InterfaceMethodVS(InterfaceV view)
  {
    super(view);
  }

  //--- Method data -------------------------------------

  private String name;

  private String servicePath;

  private HttpMethod httpMethod;

  private boolean mockActive;
  private boolean mockActiveOnStartup;

  private boolean routingOnNoMockData;

  private YesNoGlobalOrRecordConfigIndividuallyType record;

  private Integer countCalls;

  private final List<HttpMethod> httpMethods = Arrays.asList(HttpMethod.values());

  private final List<YesNoGlobalOrRecordConfigIndividuallyType> yesNoGlobalTypes = Arrays
      .asList(YesNoGlobalOrRecordConfigIndividuallyType.values());

  // component states
  private boolean routingOnNoMockDataDisabled;
  private boolean recordDisabled;
  private boolean servicePathRendered = true;
  private boolean httpMethodRendered = false;
  private boolean deleteMethodDisabled = true;

  // --- End Method data --------------------------------

  //--- RecordConfig overview data -------------------------------------

  private List<RecordConfig> tblRecordConfigs = new ArrayList<>();

  private List<RecordConfig> selectedRecordConfigs;

  private List<ColumnModel> tblRecordConfigsColumns = new ArrayList<>();

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

  private List<MockData> tblMockData = new ArrayList<>();

  private List<MockData> selectedMockDataList;

  private List<ColumnModel> tblMockDataColumns = new ArrayList<>();

  //component states
  private boolean newMockDataDisabled = true;
  private boolean editMockDataDisabled = true;
  private boolean deleteMockDataOverviewDisabled = true;

  // --- End MockData overview data --------------------------------

  //--- MockData data -------------------------------------

  private List<MockProfile> tblMockDataMockProfiles = new ArrayList<>();

  private List<MockProfile> selectedMockDataMockProfiles;

  private List<ColumnModel> tblMockDataMockProfilesColumns = new ArrayList<>();

  // Dialog (choose mockprofile for add)
  private List<MockProfile> mockProfiles = new ArrayList<>();
  private MockProfile mdMockProfile;

  // Dialog (add path param)
  private String mdPathParamKey;
  private String mdPathParamValue;

  private String mdTitle;
  private boolean mdActive;
  private boolean mdCommon;

  private List<PathParam> tblMockDataPathParams = new ArrayList<>();

  private List<PathParam> selectedMockDataPathParams;

  private List<ColumnModel> tblMockDataPathParamsColumns = new ArrayList<>();

  private String mdRequest;
  private Integer mdHttpResponseCode;
  private String mdResponse;
  private String mdCreated;
  private Integer mdCountCalls;

  // component states
  private boolean deleteMockDataDisabled = true;
  private boolean deleteMockDataMockProfileDisabled = true;
  private boolean httpReturnCodeRendered = false;
  private boolean deleteMockDataPathParamDisabled = true;
  private boolean pathParamsRendered = false;

  // --- End MockData data --------------------------------

  public void init()
  {
    initTblRecordConfigs();
    initTblMockData();
    initTblMockDataMockProfiles();
    initTblMockDataPathParams();
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
    ColumnModel colMocksession = new ColumnModel(Resources.getLabel("mock_profiles"), "mockProfileNames");
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

  private void initTblMockDataMockProfiles()
  {
    ColumnModel colName = new ColumnModel(Resources.getLabel("name"), "name");
    colName.setWidth(67, WidthUnit.PERCENT);
    tblMockDataMockProfilesColumns.add(colName);

    ColumnModel colPersistent = new ColumnModel(Resources.getLabel("persistent"), "persistent");
    colPersistent.setWidth(33, WidthUnit.PERCENT);
    tblMockDataMockProfilesColumns.add(colPersistent);
  }

  public void onMockDataMockProfilesRowSelect()
  {
    controller.handleMockDataMockProfilesSelection();
  }

  public void onMockDataMockProfilesRowUnselect()
  {
    controller.handleMockDataMockProfilesSelection();
  }

  public void addMockDataMockProfile()
  {
    controller.addMockDataMockProfile();
  }

  public void addMockDataSelectedMockProfile()
  {
    controller.addMockDataSelectedMockProfile();
  }

  public void deleteMockDataMockProfiles()
  {
    controller.deleteMockDataMockProfiles();
  }

  public void onMockDataPathParamsRowSelect()
  {
    controller.handleMockDataPathParamsSelection();
  }

  public void onMockDataPathParamsRowUnselect()
  {
    controller.handleMockDataPathParamsSelection();
  }

  public void addMockDataPathParam()
  {
    controller.addMockDataPathParam();
  }

  public void addMockDataGivenPathParam()
  {
    controller.addMockDataGivenPathParam();
  }

  public void deleteMockDataPathParams()
  {
    controller.deleteMockDataPathParams();
  }

  private void initTblMockDataPathParams()
  {
    ColumnModel colName = new ColumnModel(Resources.getLabel("name"), "key");
    colName.setWidth(40, WidthUnit.PERCENT);
    tblMockDataPathParamsColumns.add(colName);

    ColumnModel colValue = new ColumnModel(Resources.getLabel("value"), "value");
    colValue.setWidth(60, WidthUnit.PERCENT);
    tblMockDataPathParamsColumns.add(colValue);
  }

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

  public HttpMethod getHttpMethod()
  {
    return httpMethod;
  }

  public void setHttpMethod(HttpMethod httpMethod)
  {
    this.httpMethod = httpMethod;
  }

  public boolean isRoutingOnNoMockData()
  {
    return routingOnNoMockData;
  }

  public void setRoutingOnNoMockData(boolean routingOnNoMockData)
  {
    this.routingOnNoMockData = routingOnNoMockData;
  }

  public YesNoGlobalOrRecordConfigIndividuallyType getRecord()
  {
    return record;
  }

  public void setRecord(YesNoGlobalOrRecordConfigIndividuallyType record)
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

  public boolean isHttpMethodRendered()
  {
    return httpMethodRendered;
  }

  public void setHttpMethodRendered(boolean httpMethodRendered)
  {
    this.httpMethodRendered = httpMethodRendered;
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

  public boolean isMockActive()
  {
    return mockActive;
  }

  public void setMockActive(boolean mockActive)
  {
    this.mockActive = mockActive;
  }

  public boolean isMockActiveOnStartup()
  {
    return mockActiveOnStartup;
  }

  public void setMockActiveOnStartup(boolean mockActiveOnStartup)
  {
    this.mockActiveOnStartup = mockActiveOnStartup;
  }

  /**
   * @return the tblMockDataMockProfiles
   */
  public List<MockProfile> getTblMockDataMockProfiles()
  {
    return tblMockDataMockProfiles;
  }

  /**
   * @param tblMockDataMockProfiles the tblMockDataMockProfiles to set
   */
  public void setTblMockDataMockProfiles(List<MockProfile> tblMockDataMockProfiles)
  {
    this.tblMockDataMockProfiles = tblMockDataMockProfiles;
  }

  /**
   * @return the selectedMockDataMockProfiles
   */
  public List<MockProfile> getSelectedMockDataMockProfiles()
  {
    if (selectedMockDataMockProfiles == null)
    {
      selectedMockDataMockProfiles = new ArrayList<>();
    }

    return selectedMockDataMockProfiles;
  }

  /**
   * @param selectedMockDataMockProfiles the selectedMockDataMockProfiles to set
   */
  public void setSelectedMockDataMockProfiles(List<MockProfile> selectedMockDataMockProfiles)
  {
    this.selectedMockDataMockProfiles = selectedMockDataMockProfiles;
  }

  /**
   * @return the tblMockDataMockProfilesColumns
   */
  public List<ColumnModel> getTblMockDataMockProfilesColumns()
  {
    return tblMockDataMockProfilesColumns;
  }

  /**
   * @param tblMockDataMockProfilesColumns the tblMockDataMockProfilesColumns to set
   */
  public void setTblMockDataMockProfilesColumns(List<ColumnModel> tblMockDataMockProfilesColumns)
  {
    this.tblMockDataMockProfilesColumns = tblMockDataMockProfilesColumns;
  }

  /**
   * @param tblRecordConfigs the tblRecordConfigs to set
   */
  public void setTblRecordConfigs(List<RecordConfig> tblRecordConfigs)
  {
    this.tblRecordConfigs = tblRecordConfigs;
  }

  /**
   * @param tblRecordConfigsColumns the tblRecordConfigsColumns to set
   */
  public void setTblRecordConfigsColumns(List<ColumnModel> tblRecordConfigsColumns)
  {
    this.tblRecordConfigsColumns = tblRecordConfigsColumns;
  }

  /**
   * @param tblMockData the tblMockData to set
   */
  public void setTblMockData(List<MockData> tblMockData)
  {
    this.tblMockData = tblMockData;
  }

  /**
   * @param tblMockDataColumns the tblMockDataColumns to set
   */
  public void setTblMockDataColumns(List<ColumnModel> tblMockDataColumns)
  {
    this.tblMockDataColumns = tblMockDataColumns;
  }

  public boolean isDeleteMockDataMockProfileDisabled()
  {
    return deleteMockDataMockProfileDisabled;
  }

  public void setDeleteMockDataMockProfileDisabled(boolean deleteMockDataMockProfileDisabled)
  {
    this.deleteMockDataMockProfileDisabled = deleteMockDataMockProfileDisabled;
  }

  /**
   * @return the mockProfiles
   */
  public List<MockProfile> getMockProfiles()
  {
    return mockProfiles;
  }

  /**
   * @param mockProfiles the mockProfiles to set
   */
  public void setMockProfiles(List<MockProfile> mockProfiles)
  {
    this.mockProfiles = mockProfiles;
  }

  /**
   * @return the mdMockProfile
   */
  public MockProfile getMdMockProfile()
  {
    return mdMockProfile;
  }

  /**
   * @param mdMockProfile the mdMockProfile to set
   */
  public void setMdMockProfile(MockProfile mdMockProfile)
  {
    this.mdMockProfile = mdMockProfile;
  }

  public List<YesNoGlobalOrRecordConfigIndividuallyType> getYesNoGlobalTypes()
  {
    return yesNoGlobalTypes;
  }

  public List<HttpMethod> getHttpMethods()
  {
    return httpMethods;
  }

  public boolean isMdCommon()
  {
    return mdCommon;
  }

  public void setMdCommon(boolean mdCommon)
  {
    this.mdCommon = mdCommon;
  }

  public List<PathParam> getTblMockDataPathParams()
  {
    return tblMockDataPathParams;
  }

  public void setTblMockDataPathParams(List<PathParam> tblMockDataPathParams)
  {
    this.tblMockDataPathParams = tblMockDataPathParams;
  }

  public List<PathParam> getSelectedMockDataPathParams()
  {
    if (selectedMockDataPathParams == null)
    {
      selectedMockDataPathParams = new ArrayList<>();
    }

    return selectedMockDataPathParams;
  }

  public void setSelectedMockDataPathParams(List<PathParam> selectedMockDataPathParams)
  {
    this.selectedMockDataPathParams = selectedMockDataPathParams;
  }

  public List<ColumnModel> getTblMockDataPathParamsColumns()
  {
    return tblMockDataPathParamsColumns;
  }

  public void setTblMockDataPathParamsColumns(List<ColumnModel> tblMockDataPathParamsColumns)
  {
    this.tblMockDataPathParamsColumns = tblMockDataPathParamsColumns;
  }

  public boolean isHttpReturnCodeRendered()
  {
    return httpReturnCodeRendered;
  }

  public void setHttpReturnCodeRendered(boolean httpReturnCodeRendered)
  {
    this.httpReturnCodeRendered = httpReturnCodeRendered;
  }

  public boolean isPathParamsRendered()
  {
    return pathParamsRendered;
  }

  public void setPathParamsRendered(boolean pathParamsRendered)
  {
    this.pathParamsRendered = pathParamsRendered;
  }

  public String getMdPathParamKey()
  {
    return mdPathParamKey;
  }

  public void setMdPathParamKey(String mdPathParamKey)
  {
    this.mdPathParamKey = mdPathParamKey;
  }

  public String getMdPathParamValue()
  {
    return mdPathParamValue;
  }

  public void setMdPathParamValue(String mdPathParamValue)
  {
    this.mdPathParamValue = mdPathParamValue;
  }

  public boolean isDeleteMockDataPathParamDisabled()
  {
    return deleteMockDataPathParamDisabled;
  }

  public void setDeleteMockDataPathParamDisabled(boolean deleteMockDataPathParamDisabled)
  {
    this.deleteMockDataPathParamDisabled = deleteMockDataPathParamDisabled;
  }

  public Integer getMdHttpResponseCode()
  {
    return mdHttpResponseCode;
  }

  public void setMdHttpResponseCode(Integer mdHttpResponseCode)
  {
    this.mdHttpResponseCode = mdHttpResponseCode;
  }
}