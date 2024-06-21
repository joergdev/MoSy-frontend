package de.joergdev.mosy.frontend.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.api.model.PathParam;
import de.joergdev.mosy.api.model.Record;
import de.joergdev.mosy.api.model.RecordSession;
import de.joergdev.mosy.api.model.UrlArgument;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.frontend.model.RecordsLazyDataModel;
import de.joergdev.mosy.frontend.model.YesNoGlobalOrInterfaceIndividuallyType;
import de.joergdev.mosy.frontend.utils.ColumnModel;
import de.joergdev.mosy.frontend.utils.TreeData;
import de.joergdev.mosy.frontend.utils.WidthUnit;
import de.joergdev.mosy.frontend.view.controller.MainVC;
import de.joergdev.mosy.frontend.view.core.AbstractView;
import de.joergdev.mosy.shared.ValueWrapper;

@ManagedBean("main")
@ViewScoped
public class MainV extends AbstractView<MainVC>
{
  // --- Tree --------------------------
  private TreeNode treeRoot;
  private TreeNode selectedNode;
  private TreeNode treeNodeDefault;

  private List<TreeData> treeDataList;
  // -----------------------------------

  // DataPanel
  private String dataPanel;

  // --- Basedata ----------------------
  private final List<YesNoGlobalOrInterfaceIndividuallyType> yesNoGlobalTypes = Arrays.asList(YesNoGlobalOrInterfaceIndividuallyType.values());

  private YesNoGlobalOrInterfaceIndividuallyType mockActive;
  private YesNoGlobalOrInterfaceIndividuallyType mockActiveOnStartup;
  private YesNoGlobalOrInterfaceIndividuallyType routingOnNoMockData;
  private YesNoGlobalOrInterfaceIndividuallyType record;

  private boolean routingOnNoMockDataDisabled;
  private boolean recordDisabled;
  // -----------------------------------

  // --- Interfaces ----------------------
  private final List<Interface> tblInterfaces = new ArrayList<>();
  private List<Interface> selectedInterfaces;

  private final List<ColumnModel> tblInterfacesColumns = new ArrayList<>();

  private boolean editInterfaceDisabled = true;
  private boolean deleteInterfaceDisabled = true;
  // -------------------------------------

  // --- MockProfiles ----------------------
  private final List<MockProfile> tblMockProfiles = new ArrayList<>();
  private List<MockProfile> selectedMockProfiles;

  private List<ColumnModel> tblMockProfilesColumns = new ArrayList<>();

  private boolean editMockProfileDisabled = true;
  private boolean deleteMockProfileDisabled = true;
  // ---------------------------------------

  //--- Records ----------------------------

  private List<RecordSession> recordSessions = new ArrayList<>();
  private RecordSession recordSessionOvSelected;

  private final RecordsLazyDataModel tblRecords = new RecordsLazyDataModel(":form:tblRecords", controller);
  private List<Record> selectedRecords;

  private final List<ColumnModel> tblRecordsColumns = new ArrayList<>();

  private boolean downloadRecordsDisabled = true;
  private boolean showRecordsDisabled = true;
  private boolean deleteRecordsDisabled = true;
  private boolean deleteAllRecordsDisabled = true;

  private StreamedContent fileRecords;
  // ---------------------------------------

  //--- Record ----------------------------
  private String recRecordSession;
  private String recInterface;
  private String recMethod;
  private String recCreated;

  private List<PathParam> tblRecordPathParams = new ArrayList<>();
  private List<ColumnModel> tblRecordPathParamsColumns = new ArrayList<>();

  private List<UrlArgument> tblRecordUrlArguments = new ArrayList<>();
  private List<ColumnModel> tblRecordUrlArgumentsColumns = new ArrayList<>();

  private String recRequest;
  private Integer recHttpResponseCode;
  private String recResponse;

  //component states
  private boolean recPathParamsRendered = false;
  private boolean recUrlArgumentsRendered = false;
  private boolean recHttpReturnCodeRendered = false;

  // ---------------------------------------

  //--- RecordSessions ----------------------
  private List<RecordSession> tblRecordSessions = new ArrayList<>();
  private List<RecordSession> selectedRecordSessions;

  private List<ColumnModel> tblRecordSessionsColumns = new ArrayList<>();

  private boolean deleteRecordSessionDisabled = true;
  // ---------------------------------------

  @PostConstruct
  public void init()
  {
    treeRoot = new DefaultTreeNode("MoSy", null);

    initTblInterfaces();
    initTblMockProfiles();
    initTblRecords();
    initTblRecordSessions();
    initTblRecordPathParams();
    initTblRecordUrlArguments();

    controller.loadRefresh();
  }

  private void initTblInterfaces()
  {
    ColumnModel colType = new ColumnModel(Resources.getLabel("type"), "type");
    colType.setWidth(10, WidthUnit.PERCENT);
    tblInterfacesColumns.add(colType);

    ColumnModel colName = new ColumnModel(Resources.getLabel("name"), "name");
    colName.setWidth(70, WidthUnit.PERCENT);
    tblInterfacesColumns.add(colName);

    ColumnModel colMockDisabled = new ColumnModel(Resources.getLabel("mock_active"), "mockActive");
    colMockDisabled.setWidth(10, WidthUnit.PERCENT);
    tblInterfacesColumns.add(colMockDisabled);

    ColumnModel colRecord = new ColumnModel(Resources.getLabel("record"), "record");
    colRecord.setWidth(10, WidthUnit.PERCENT);
    tblInterfacesColumns.add(colRecord);
  }

  public void onNodeSelect(NodeSelectEvent event)
  {
    TreeData treeData = (TreeData) event.getTreeNode().getData();

    controller.treeNodeSelected(treeData);
  }

  public void onNodeUnselect(NodeUnselectEvent event)
  {
    if (treeNodeDefault != null)
    {
      selectedNode = treeNodeDefault;

      selectedNode.setSelected(true);

      controller.treeNodeSelected((TreeData) selectedNode.getData());
    }
  }

  public void updateTree()
  {
    treeRoot.getChildren().clear();
    treeNodeDefault = null;

    ValueWrapper<TreeNode> nodeToSelect = new ValueWrapper<>(null);

    if (treeDataList != null)
    {
      addSubTreeNodes(treeDataList, treeRoot, nodeToSelect);
    }

    selectedNode = nodeToSelect.getValue();

    if (selectedNode == null && treeNodeDefault != null)
    {
      selectedNode = treeNodeDefault;
      selectedNode.setSelected(true);

      controller.treeNodeSelected((TreeData) selectedNode.getData());
    }
  }

  private void addSubTreeNodes(List<TreeData> treeDataListTmp, TreeNode rootNode, ValueWrapper<TreeNode> nodeToSelect)
  {
    for (TreeData treeData : treeDataListTmp)
    {
      TreeNode node = new DefaultTreeNode(treeData, rootNode);

      if (selectedNode != null && nodeToSelect.getValue() == null && treeData.isEqual((TreeData) selectedNode.getData()))
      {
        nodeToSelect.setValue(node);
      }

      if (treeData.isDefaultSelection())
      {
        treeNodeDefault = node;
      }

      // recursive call
      addSubTreeNodes(treeData.getSubEntries(), node, nodeToSelect);
    }
  }

  public void uploadMockData()
  {
    controller.uploadMockData();
  }

  // --- Basedata ----------------------

  public void baseDataChanged()
  {
    controller.baseDataChanged();
  }

  public void boot()
  {
    controller.boot();
  }

  // --- End Basedata ----------------------

  // --- Interfaces ----------------------

  public void newInterface()
  {
    controller.newInterface();
  }

  public void editInterface()
  {
    controller.editInterface();
  }

  public void deleteInterfaces()
  {
    controller.deleteInterfaces();
  }

  public void onInterfacesRowSelect(SelectEvent event)
  {
    controller.handleInterfacesSelection();
  }

  public void onInterfacesRowUnselect(UnselectEvent event)
  {
    controller.handleInterfacesSelection();
  }

  public void onInterfacesRowDoubleClick(SelectEvent event)
  {
    controller.editInterface();
  }

  // --- End Interfaces ----------------------

  // --- MockProfiles ----------------------

  private void initTblMockProfiles()
  {
    ColumnModel colName = new ColumnModel(Resources.getLabel("name"), "name");
    colName.setWidth(34, WidthUnit.PERCENT);
    tblMockProfilesColumns.add(colName);

    ColumnModel colPersistent = new ColumnModel(Resources.getLabel("persistent"), "persistent");
    colPersistent.setWidth(33, WidthUnit.PERCENT);
    tblMockProfilesColumns.add(colPersistent);

    ColumnModel colUseCommonMocks = new ColumnModel(Resources.getLabel("use_common_mocks"), "useCommonMocks");
    colUseCommonMocks.setWidth(33, WidthUnit.PERCENT);
    tblMockProfilesColumns.add(colUseCommonMocks);
  }

  public void newMockProfile()
  {
    controller.newMockProfile();
  }

  public void deleteMockProfiles()
  {
    controller.deleteMockProfiles();
  }

  public void onMockProfilesRowSelect()
  {
    controller.handleMockProfilesSelection();
  }

  public void onMockProfilesRowUnselect()
  {
    controller.handleMockProfilesSelection();
  }

  public void onMockProfilesRowDoubleClick()
  {
    controller.editMockProfile();
  }

  public void editMockProfile()
  {
    controller.editMockProfile();
  }

  // --- End MockProfiles ----------------------

  // --- Records -------------------------------

  private void initTblRecords()
  {
    ColumnModel colRecordsession = new ColumnModel(Resources.getLabel("record_session"), "recordSessionID");
    colRecordsession.setWidth(10, WidthUnit.PERCENT);
    tblRecordsColumns.add(colRecordsession);

    ColumnModel colInterface = new ColumnModel(Resources.getLabel("interface"), "interfaceName");
    colInterface.setWidth(30, WidthUnit.PERCENT);
    tblRecordsColumns.add(colInterface);

    ColumnModel colMethod = new ColumnModel(Resources.getLabel("method"), "methodName");
    colMethod.setWidth(30, WidthUnit.PERCENT);
    tblRecordsColumns.add(colMethod);

    ColumnModel colCreated = new ColumnModel(Resources.getLabel("created"), "createdAsString");
    colCreated.setWidth(30, WidthUnit.PERCENT);
    tblRecordsColumns.add(colCreated);

    tblRecords.addDataModelListener(e -> controller.updateComponentsRecords());
  }

  //--- End Records -------------------------------

  //--- RecordSessions -------------------------------

  private void initTblRecordSessions()
  {
    ColumnModel colID = new ColumnModel(Resources.getLabel("id"), "recordSessionID");
    colID.setWidth(40, WidthUnit.PERCENT);
    tblRecordSessionsColumns.add(colID);

    ColumnModel colCreated = new ColumnModel(Resources.getLabel("created"), "createdAsString");
    colCreated.setWidth(60, WidthUnit.PERCENT);
    tblRecordSessionsColumns.add(colCreated);
  }

  public void onRecordSessionsRowSelect()
  {
    controller.handleRecordSessionsSelection();
  }

  public void onRecordSessionsRowUnselect()
  {
    controller.handleRecordSessionsSelection();
  }

  public void newRecordSession()
  {
    controller.newRecordSession();
  }

  public void deleteRecordSessions()
  {
    controller.deleteRecordSessions();
  }

  //--- End RecordSessions -------------------------------

  public void showRecord()
  {
    controller.showRecord();
  }

  public void deleteRecords()
  {
    controller.deleteRecords();
  }

  public void deleteAllRecords()
  {
    controller.deleteAllRecords();
  }

  public void onRecordsRowSelect()
  {
    controller.handleRecordsSelection();
  }

  public void onRecordsRowUnselect()
  {
    controller.handleRecordsSelection();
  }

  public void onRecordsRowDoubleClick(SelectEvent event)
  {
    controller.showRecord();
  }

  public StreamedContent getFileRecords()
  {
    fileRecords = controller.getFileRecords();

    return fileRecords;
  }

  public void useRecordsAsMockdata()
  {
    controller.useRecordsAsMockdata();
  }

  public void onRecordSessionOvSelect()
  {
    controller.handleRecordOverviewSessionSelected();
  }

  // --- End Records ---------------------------

  //--- Record --------------------------------- 

  public void deleteRecord()
  {
    controller.deleteRecord();
  }

  private void initTblRecordPathParams()
  {
    ColumnModel colName = new ColumnModel(Resources.getLabel("name"), "key");
    colName.setWidth(40, WidthUnit.PERCENT);
    tblRecordPathParamsColumns.add(colName);

    ColumnModel colValue = new ColumnModel(Resources.getLabel("value"), "value");
    colValue.setWidth(60, WidthUnit.PERCENT);
    tblRecordPathParamsColumns.add(colValue);
  }

  private void initTblRecordUrlArguments()
  {
    ColumnModel colName = new ColumnModel(Resources.getLabel("name"), "key");
    colName.setWidth(40, WidthUnit.PERCENT);
    tblRecordUrlArgumentsColumns.add(colName);

    ColumnModel colValue = new ColumnModel(Resources.getLabel("value"), "value");
    colValue.setWidth(60, WidthUnit.PERCENT);
    tblRecordUrlArgumentsColumns.add(colValue);
  }

  // --- End Record ----------------------------

  @Override
  protected MainVC getControllerInstance()
  {
    MainVC vc = new MainVC();
    vc.setView(this);

    return vc;
  }

  public TreeNode getTreeRoot()
  {
    return treeRoot;
  }

  public void setTreeRoot(TreeNode treeRoot)
  {
    this.treeRoot = treeRoot;
  }

  public List<Record> getSelectedRecords()
  {
    if (selectedRecords == null)
    {
      selectedRecords = new ArrayList<>();
    }

    return selectedRecords;
  }

  public void setSelectedRecords(List<Record> selectedRecords)
  {
    this.selectedRecords = selectedRecords;
  }

  public boolean isDownloadRecordsDisabled()
  {
    return downloadRecordsDisabled;
  }

  public void setDownloadRecordsDisabled(boolean downloadRecordsDisabled)
  {
    this.downloadRecordsDisabled = downloadRecordsDisabled;
  }

  public boolean isShowRecordsDisabled()
  {
    return showRecordsDisabled;
  }

  public void setShowRecordsDisabled(boolean showRecordsDisabled)
  {
    this.showRecordsDisabled = showRecordsDisabled;
  }

  public boolean isDeleteRecordsDisabled()
  {
    return deleteRecordsDisabled;
  }

  public void setDeleteRecordsDisabled(boolean deleteRecordsDisabled)
  {
    this.deleteRecordsDisabled = deleteRecordsDisabled;
  }

  public boolean isDeleteAllRecordsDisabled()
  {
    return deleteAllRecordsDisabled;
  }

  public void setDeleteAllRecordsDisabled(boolean deleteAllRecordsDisabled)
  {
    this.deleteAllRecordsDisabled = deleteAllRecordsDisabled;
  }

  public String getRecInterface()
  {
    return recInterface;
  }

  public void setRecInterface(String recInterface)
  {
    this.recInterface = recInterface;
  }

  public String getRecMethod()
  {
    return recMethod;
  }

  public void setRecMethod(String recMethod)
  {
    this.recMethod = recMethod;
  }

  public String getRecCreated()
  {
    return recCreated;
  }

  public void setRecCreated(String recCreated)
  {
    this.recCreated = recCreated;
  }

  public String getRecRequest()
  {
    return recRequest;
  }

  public void setRecRequest(String recRequest)
  {
    this.recRequest = recRequest;
  }

  public String getRecResponse()
  {
    return recResponse;
  }

  public void setRecResponse(String reqResponse)
  {
    this.recResponse = reqResponse;
  }

  public RecordsLazyDataModel getTblRecords()
  {
    return tblRecords;
  }

  public List<ColumnModel> getTblRecordsColumns()
  {
    return tblRecordsColumns;
  }

  public TreeNode getSelectedNode()
  {
    return selectedNode;
  }

  public void setSelectedNode(TreeNode selectedNode)
  {
    this.selectedNode = selectedNode;
  }

  public String getDataPanel()
  {
    return dataPanel;
  }

  public void setDataPanel(String dataPanel)
  {
    this.dataPanel = dataPanel;
  }

  public List<TreeData> getTreeDataList()
  {
    return treeDataList;
  }

  public void setTreeDataList(List<TreeData> treeDataList)
  {
    this.treeDataList = treeDataList;
  }

  public YesNoGlobalOrInterfaceIndividuallyType getMockActive()
  {
    return mockActive;
  }

  public void setMockActive(YesNoGlobalOrInterfaceIndividuallyType mockActive)
  {
    this.mockActive = mockActive;
  }

  public YesNoGlobalOrInterfaceIndividuallyType getMockActiveOnStartup()
  {
    return mockActiveOnStartup;
  }

  public void setMockActiveOnStartup(YesNoGlobalOrInterfaceIndividuallyType mockActiveOnStartup)
  {
    this.mockActiveOnStartup = mockActiveOnStartup;
  }

  public YesNoGlobalOrInterfaceIndividuallyType getRoutingOnNoMockData()
  {
    return routingOnNoMockData;
  }

  public void setRoutingOnNoMockData(YesNoGlobalOrInterfaceIndividuallyType routingOnNoMockData)
  {
    this.routingOnNoMockData = routingOnNoMockData;
  }

  public YesNoGlobalOrInterfaceIndividuallyType getRecord()
  {
    return record;
  }

  public void setRecord(YesNoGlobalOrInterfaceIndividuallyType record)
  {
    this.record = record;
  }

  public TreeNode getTreeNodeDefault()
  {
    return treeNodeDefault;
  }

  public void setTreeNodeDefault(TreeNode treeNodeDefault)
  {
    this.treeNodeDefault = treeNodeDefault;
  }

  public List<Interface> getTblInterfaces()
  {
    return tblInterfaces;
  }

  public List<ColumnModel> getTblInterfacesColumns()
  {
    return tblInterfacesColumns;
  }

  public boolean isRoutingOnNoMockDataDisabled()
  {
    return routingOnNoMockDataDisabled;
  }

  public void setRoutingOnNoMockDataDisabled(boolean routingOnNoMockDataDisabled)
  {
    this.routingOnNoMockDataDisabled = routingOnNoMockDataDisabled;
  }

  public boolean isRecordDisabled()
  {
    return recordDisabled;
  }

  public void setRecordDisabled(boolean recordDisabled)
  {
    this.recordDisabled = recordDisabled;
  }

  public List<Interface> getSelectedInterfaces()
  {
    return selectedInterfaces;
  }

  public void setSelectedInterfaces(List<Interface> selectedInterfaces)
  {
    this.selectedInterfaces = selectedInterfaces;
  }

  public boolean isEditInterfaceDisabled()
  {
    return editInterfaceDisabled;
  }

  public void setEditInterfaceDisabled(boolean editInterfaceDisabled)
  {
    this.editInterfaceDisabled = editInterfaceDisabled;
  }

  public boolean isDeleteInterfaceDisabled()
  {
    return deleteInterfaceDisabled;
  }

  public void setDeleteInterfaceDisabled(boolean deleteInterfaceDisabled)
  {
    this.deleteInterfaceDisabled = deleteInterfaceDisabled;
  }

  public List<YesNoGlobalOrInterfaceIndividuallyType> getYesNoGlobalTypes()
  {
    return yesNoGlobalTypes;
  }

  /**
   * @return the selectedMockProfiles
   */
  public List<MockProfile> getSelectedMockProfiles()
  {
    return selectedMockProfiles;
  }

  /**
   * @param selectedMockProfiles the selectedMockProfiles to set
   */
  public void setSelectedMockProfiles(List<MockProfile> selectedMockProfiles)
  {
    this.selectedMockProfiles = selectedMockProfiles;
  }

  /**
   * @return the tblMockProfilesColumns
   */
  public List<ColumnModel> getTblMockProfilesColumns()
  {
    return tblMockProfilesColumns;
  }

  /**
   * @param tblMockProfilesColumns the tblMockProfilesColumns to set
   */
  public void setTblMockProfilesColumns(List<ColumnModel> tblMockProfilesColumns)
  {
    this.tblMockProfilesColumns = tblMockProfilesColumns;
  }

  /**
   * @return the deleteMockProfileDisabled
   */
  public boolean isDeleteMockProfileDisabled()
  {
    return deleteMockProfileDisabled;
  }

  /**
   * @param deleteMockProfileDisabled the deleteMockProfileDisabled to set
   */
  public void setDeleteMockProfileDisabled(boolean deleteMockProfileDisabled)
  {
    this.deleteMockProfileDisabled = deleteMockProfileDisabled;
  }

  public List<MockProfile> getTblMockProfiles()
  {
    return tblMockProfiles;
  }

  public boolean isEditMockProfileDisabled()
  {
    return editMockProfileDisabled;
  }

  public void setEditMockProfileDisabled(boolean editMockProfileDisabled)
  {
    this.editMockProfileDisabled = editMockProfileDisabled;
  }

  public String getRecRecordSession()
  {
    return recRecordSession;
  }

  public void setRecRecordSession(String recRecordSession)
  {
    this.recRecordSession = recRecordSession;
  }

  public List<RecordSession> getRecordSessions()
  {
    return recordSessions;
  }

  public void setRecordSessions(List<RecordSession> recordSessions)
  {
    this.recordSessions = recordSessions;
  }

  public RecordSession getRecordSessionOvSelected()
  {
    return recordSessionOvSelected;
  }

  public void setRecordSessionOvSelected(RecordSession recordSessionOvSelected)
  {
    this.recordSessionOvSelected = recordSessionOvSelected;
  }

  /**
   * @return the tblRecordSessions
   */
  public List<RecordSession> getTblRecordSessions()
  {
    return tblRecordSessions;
  }

  /**
   * @param tblRecordSessions the tblRecordSessions to set
   */
  public void setTblRecordSessions(List<RecordSession> tblRecordSessions)
  {
    this.tblRecordSessions = tblRecordSessions;
  }

  /**
   * @return the selectedRecordSessions
   */
  public List<RecordSession> getSelectedRecordSessions()
  {
    return selectedRecordSessions;
  }

  /**
   * @param selectedRecordSessions the selectedRecordSessions to set
   */
  public void setSelectedRecordSessions(List<RecordSession> selectedRecordSessions)
  {
    this.selectedRecordSessions = selectedRecordSessions;
  }

  /**
   * @return the deleteRecordSessionDisabled
   */
  public boolean isDeleteRecordSessionDisabled()
  {
    return deleteRecordSessionDisabled;
  }

  /**
   * @param deleteRecordSessionDisabled the deleteRecordSessionDisabled to set
   */
  public void setDeleteRecordSessionDisabled(boolean deleteRecordSessionDisabled)
  {
    this.deleteRecordSessionDisabled = deleteRecordSessionDisabled;
  }

  public List<ColumnModel> getTblRecordSessionsColumns()
  {
    return tblRecordSessionsColumns;
  }

  public void setTblRecordSessionsColumns(List<ColumnModel> tblRecordSessionsColumns)
  {
    this.tblRecordSessionsColumns = tblRecordSessionsColumns;
  }

  public List<PathParam> getTblRecordPathParams()
  {
    return tblRecordPathParams;
  }

  public void setTblRecordPathParams(List<PathParam> tblRecordPathParams)
  {
    this.tblRecordPathParams = tblRecordPathParams;
  }

  public List<ColumnModel> getTblRecordPathParamsColumns()
  {
    return tblRecordPathParamsColumns;
  }

  public void setTblRecordPathParamsColumns(List<ColumnModel> tblRecordPathParamsColumns)
  {
    this.tblRecordPathParamsColumns = tblRecordPathParamsColumns;
  }

  public boolean isRecPathParamsRendered()
  {
    return recPathParamsRendered;
  }

  public void setRecPathParamsRendered(boolean recPathParamsRendered)
  {
    this.recPathParamsRendered = recPathParamsRendered;
  }

  public List<UrlArgument> getTblRecordUrlArguments()
  {
    return tblRecordUrlArguments;
  }

  public void setTblRecordUrlArguments(List<UrlArgument> tblRecordUrlArguments)
  {
    this.tblRecordUrlArguments = tblRecordUrlArguments;
  }

  public List<ColumnModel> getTblRecordUrlArgumentsColumns()
  {
    return tblRecordUrlArgumentsColumns;
  }

  public void setTblRecordUrlArgumentsColumns(List<ColumnModel> tblRecordUrlArgumentsColumns)
  {
    this.tblRecordUrlArgumentsColumns = tblRecordUrlArgumentsColumns;
  }

  public boolean isRecUrlArgumentsRendered()
  {
    return recUrlArgumentsRendered;
  }

  public void setRecUrlArgumentsRendered(boolean recUrlArgumentsRendered)
  {
    this.recUrlArgumentsRendered = recUrlArgumentsRendered;
  }

  public Integer getRecHttpResponseCode()
  {
    return recHttpResponseCode;
  }

  public void setRecHttpResponseCode(Integer recHttpResponseCode)
  {
    this.recHttpResponseCode = recHttpResponseCode;
  }

  public boolean isRecHttpReturnCodeRendered()
  {
    return recHttpReturnCodeRendered;
  }

  public void setRecHttpReturnCodeRendered(boolean recHttpReturnCodeRendered)
  {
    this.recHttpReturnCodeRendered = recHttpReturnCodeRendered;
  }
}
