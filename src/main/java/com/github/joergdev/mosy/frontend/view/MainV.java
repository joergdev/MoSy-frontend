package com.github.joergdev.mosy.frontend.view;

import java.util.ArrayList;
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
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.api.model.Record;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.model.RecordsLazyDataModel;
import com.github.joergdev.mosy.frontend.utils.ColumnModel;
import com.github.joergdev.mosy.frontend.utils.TreeData;
import com.github.joergdev.mosy.frontend.utils.WidthUnit;
import com.github.joergdev.mosy.frontend.view.controller.MainVC;
import com.github.joergdev.mosy.frontend.view.core.AbstractView;
import com.github.joergdev.mosy.shared.ValueWrapper;

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
  private boolean mockActive;
  private boolean mockActiveOnStartup;
  private boolean routingOnNoMockData;
  private boolean record;

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

  // --- MockSessions ----------------------
  private final List<MockSession> tblMockSessions = new ArrayList<>();
  private List<MockSession> selectedMockSessions;

  private final List<ColumnModel> tblMockSessionsColumns = new ArrayList<>();

  private boolean deleteMockSessionDisabled = true;
  // ---------------------------------------

  //--- Records ----------------------------
  private final RecordsLazyDataModel tblRecords = new RecordsLazyDataModel(":form:tblRecords", controller);
  private List<Record> selectedRecords;

  private final List<ColumnModel> tblRecordsColumns = new ArrayList<>();

  private boolean downloadRecordsDisabled = true;
  private boolean showRecordsDisabled = true;
  private boolean deleteRecordsDisabled = true;

  private StreamedContent fileRecords;
  // ---------------------------------------

  //--- Record ----------------------------
  private String recInterface;
  private String recMethod;
  private String recCreated;
  private String recRequest;
  private String recResponse;
  // ---------------------------------------

  @PostConstruct
  public void init()
  {
    treeRoot = new DefaultTreeNode("MoSy", null);

    initTblInterfaces();
    initTblMockSessions();
    initTblRecords();

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

    ColumnModel colMockDisabled = new ColumnModel(Resources.getLabel("mock_disabled"), "mockDisabled");
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

  private void addSubTreeNodes(List<TreeData> treeDataListTmp, TreeNode rootNode,
                               ValueWrapper<TreeNode> nodeToSelect)
  {
    for (TreeData treeData : treeDataListTmp)
    {
      TreeNode node = new DefaultTreeNode(treeData, rootNode);

      if (selectedNode != null && nodeToSelect.getValue() == null
          && treeData.isEqual((TreeData) selectedNode.getData()))
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

  // --- MockSessions ----------------------

  private void initTblMockSessions()
  {
    ColumnModel colID = new ColumnModel(Resources.getLabel("id"), "mockSessionID");
    colID.setWidth(50, WidthUnit.PERCENT);
    tblMockSessionsColumns.add(colID);

    ColumnModel colCreated = new ColumnModel(Resources.getLabel("created"), "createdAsString");
    colCreated.setWidth(50, WidthUnit.PERCENT);
    tblMockSessionsColumns.add(colCreated);
  }

  public void newMockSession()
  {
    controller.newMockSession();
  }

  public void deleteMockSessions()
  {
    controller.deleteMockSessions();
  }

  public void onMockSessionsRowSelect()
  {
    controller.handleMockSessionsSelection();
  }

  public void onMockSessionsRowUnselect()
  {
    controller.handleMockSessionsSelection();
  }

  // --- End MockSessions ----------------------

  // --- Records -------------------------------

  private void initTblRecords()
  {
    ColumnModel colInterface = new ColumnModel(Resources.getLabel("interface"), "interfaceName");
    colInterface.setWidth(35, WidthUnit.PERCENT);
    tblRecordsColumns.add(colInterface);

    ColumnModel colMethod = new ColumnModel(Resources.getLabel("method"), "methodName");
    colMethod.setWidth(35, WidthUnit.PERCENT);
    tblRecordsColumns.add(colMethod);

    ColumnModel colCreated = new ColumnModel(Resources.getLabel("created"), "createdAsString");
    colCreated.setWidth(30, WidthUnit.PERCENT);
    tblRecordsColumns.add(colCreated);
  }

  public void showRecord()
  {
    controller.showRecord();
  }

  public void deleteRecords()
  {
    controller.deleteRecords();
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

  // --- End Records ---------------------------

  //--- Record --------------------------------- 

  public void deleteRecord()
  {
    controller.deleteRecord();
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

  public List<MockSession> getSelectedMockSessions()
  {
    return selectedMockSessions;
  }

  public void setSelectedMockSessions(List<MockSession> selectedMockSessions)
  {
    this.selectedMockSessions = selectedMockSessions;
  }

  public boolean isDeleteMockSessionDisabled()
  {
    return deleteMockSessionDisabled;
  }

  public void setDeleteMockSessionDisabled(boolean deleteMockSessionDisabled)
  {
    this.deleteMockSessionDisabled = deleteMockSessionDisabled;
  }

  public List<MockSession> getTblMockSessions()
  {
    return tblMockSessions;
  }

  public List<ColumnModel> getTblMockSessionsColumns()
  {
    return tblMockSessionsColumns;
  }
}