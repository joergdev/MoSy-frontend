package com.github.joergdev.mosy.frontend.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.api.model.InterfaceType;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.utils.ColumnModel;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.utils.TreeData;
import com.github.joergdev.mosy.frontend.utils.WidthUnit;
import com.github.joergdev.mosy.frontend.view.controller.InterfaceVC;
import com.github.joergdev.mosy.frontend.view.core.AbstractView;
import com.github.joergdev.mosy.shared.Utils;
import com.github.joergdev.mosy.shared.ValueWrapper;

@ManagedBean("_interface")
@ViewScoped
public class InterfaceV extends AbstractView<InterfaceVC>
{
  public static final String VIEW_PARAM_INTERFACE_ID = "interface_id";

  private Integer interfaceId;

  // --- Tree --------------------------
  private TreeNode treeRoot;
  private TreeNode selectedNode;
  private TreeNode treeNodeDefault;

  private List<TreeData> treeDataList;
  // -----------------------------------

  private boolean refreshDisabled = true;

  // DataPanel
  private String dataPanel;

  // --- Interface data -------------------
  private String name;

  private final List<InterfaceType> interfaceTypes = Arrays.asList(InterfaceType.values());
  private InterfaceType interfaceTypeSelected;

  private String servicePath;

  private boolean mockDisabled;
  private boolean mockDisabledOnStartup;

  private boolean routingOnNoMockData;
  private String routingUrl;

  private boolean record;

  // component states
  private boolean routingOnNoMockDataDisabled;
  private boolean recordDisabled;
  private boolean deleteInterfaceDisabled = true;
  private boolean servicePathRendered = true;
  private boolean routingUrlRendered = true;
  private boolean saveDeleteInterfaceRendered = true;

  // -----------------------------------

  // --- Methods ----------------------
  private final List<InterfaceMethod> tblMethods = new ArrayList<>();

  private List<InterfaceMethod> selectedMethods;

  private final List<ColumnModel> tblMethodsColumns = new ArrayList<>();

  //component states
  private boolean editMethodDisabled = true;
  private boolean deleteMethodDisabled = true;
  // -------------------------------------

  // Subviews
  private final InterfaceMethodVS methodVS = new InterfaceMethodVS(this);

  @PostConstruct
  public void init()
  {
    interfaceId = Utils.asInteger(JsfUtils.getViewParameter(VIEW_PARAM_INTERFACE_ID));

    treeRoot = new DefaultTreeNode("Interface", null);

    initTblInterfaces();

    methodVS.init();

    controller.showLoadRefresh();
  }

  private void initTblInterfaces()
  {
    ColumnModel colName = new ColumnModel(Resources.getLabel("name"), "name");
    colName.setWidth(50, WidthUnit.PERCENT);
    tblMethodsColumns.add(colName);

    ColumnModel colMockDisabled = new ColumnModel(Resources.getLabel("mock_disabled"), "mockDisabled");
    colMockDisabled.setWidth(10, WidthUnit.PERCENT);
    tblMethodsColumns.add(colMockDisabled);

    ColumnModel colRoutingOnNoMockData = new ColumnModel(Resources.getLabel("routing_on_no_mockdata"),
        "routingOnNoMockData");
    colRoutingOnNoMockData.setWidth(20, WidthUnit.PERCENT);
    tblMethodsColumns.add(colRoutingOnNoMockData);

    ColumnModel colRecord = new ColumnModel(Resources.getLabel("record"), "record");
    colRecord.setWidth(10, WidthUnit.PERCENT);
    tblMethodsColumns.add(colRecord);

    ColumnModel colCountCalls = new ColumnModel(Resources.getLabel("count_calls"), "countCalls");
    colCountCalls.setWidth(10, WidthUnit.PERCENT);
    tblMethodsColumns.add(colCountCalls);
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

  public void onNodeExpand(NodeExpandEvent event)
  {
    TreeData treeData = (TreeData) event.getTreeNode().getData();

    controller.treeNodeExpanded(treeData);
  }

  public void updateTree()
  {
    // Collect expanded paths
    Collection<Collection<String>> colExpandedPaths = new ArrayList<>();
    collectExpandedTreeNodes(colExpandedPaths, treeRoot.getChildren(), new ArrayList<>());

    treeRoot.getChildren().clear();
    treeNodeDefault = null;

    ValueWrapper<TreeNode> nodeToSelect = new ValueWrapper<>(null);

    if (treeDataList != null)
    {
      addSubTreeNodes(treeDataList, treeRoot, nodeToSelect, colExpandedPaths, new ArrayList<>());
    }

    selectedNode = nodeToSelect.getValue();

    if (selectedNode == null && treeNodeDefault != null)
    {
      selectedNode = treeNodeDefault;
      selectedNode.setSelected(true);

      controller.treeNodeSelected((TreeData) selectedNode.getData());
    }
  }

  private void collectExpandedTreeNodes(Collection<Collection<String>> colExpandedPaths,
                                        List<TreeNode> treeNodes, List<String> currentPath)
  {
    for (TreeNode treeNode : treeNodes)
    {
      if (treeNode.isExpanded())
      {
        List<String> pathForTreeNode = new ArrayList<>(currentPath);
        pathForTreeNode.add(treeNode.toString());

        colExpandedPaths.add(pathForTreeNode);

        collectExpandedTreeNodes(colExpandedPaths, treeNode.getChildren(), pathForTreeNode);
      }
    }
  }

  private void addSubTreeNodes(List<TreeData> treeDataListTmp, TreeNode rootNode,
                               ValueWrapper<TreeNode> nodeToSelect,
                               Collection<Collection<String>> colExpandedPaths, List<String> currentPath)
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

      List<String> pathForTreeNode = new ArrayList<>(currentPath);
      pathForTreeNode.add(treeData.getText());

      // maybe node was expanded before then expand it again
      if (colExpandedPaths.contains(pathForTreeNode))
      {
        node.setExpanded(true);
      }

      // recursive call
      addSubTreeNodes(treeData.getSubEntries(), node, nodeToSelect, colExpandedPaths, pathForTreeNode);
    }
  }

  // --- Basedata ----------------------

  public void onInterfaceTypeSelect()
  {
    // set visible state of servicePath
    controller.updateComponents();
  }

  public void handleMockDisabled()
  {
    controller.updateComponents();
  }

  public void handleRoutingOnNoMockData()
  {
    controller.updateComponents();
  }

  public void saveInterface()
  {
    controller.saveInterface();
  }

  public void deleteInterface()
  {
    controller.deleteInterface();
  }

  public void cancel()
  {
    controller.cancel();
  }

  // --- End Basedata ----------------------

  // --- Methods ----------------------

  public void newMethod()
  {
    controller.newMethod();
  }

  public void editMethod()
  {
    controller.editMethod();
  }

  public void deleteMethods()
  {
    controller.deleteMethods();
  }

  public void onMethodsRowSelect(SelectEvent event)
  {
    controller.handleMethodsSelection();
  }

  public void onMethodsRowUnselect(UnselectEvent event)
  {
    controller.handleMethodsSelection();
  }

  public void onMethodsRowDoubleClick(SelectEvent event)
  {
    controller.editMethod();
  }

  // --- End Methods ----------------------

  @Override
  protected InterfaceVC getControllerInstance()
  {
    InterfaceVC vc = new InterfaceVC();
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

  public TreeNode getTreeNodeDefault()
  {
    return treeNodeDefault;
  }

  public void setTreeNodeDefault(TreeNode treeNodeDefault)
  {
    this.treeNodeDefault = treeNodeDefault;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public List<InterfaceType> getInterfaceTypes()
  {
    return interfaceTypes;
  }

  public InterfaceType getInterfaceTypeSelected()
  {
    return interfaceTypeSelected;
  }

  public void setInterfaceTypeSelected(InterfaceType interfaceTypeSelected)
  {
    this.interfaceTypeSelected = interfaceTypeSelected;
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

  public String getRoutingUrl()
  {
    return routingUrl;
  }

  public void setRoutingUrl(String routingUrl)
  {
    this.routingUrl = routingUrl;
  }

  public boolean isRecord()
  {
    return record;
  }

  public void setRecord(boolean record)
  {
    this.record = record;
  }

  public List<InterfaceMethod> getTblMethods()
  {
    return tblMethods;
  }

  public List<ColumnModel> getTblMethodsColumns()
  {
    return tblMethodsColumns;
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

  public boolean isDeleteInterfaceDisabled()
  {
    return deleteInterfaceDisabled;
  }

  public void setDeleteInterfaceDisabled(boolean deleteInterfaceDisabled)
  {
    this.deleteInterfaceDisabled = deleteInterfaceDisabled;
  }

  public Integer getInterfaceId()
  {
    return interfaceId;
  }

  public void setInterfaceId(Integer interfaceId)
  {
    this.interfaceId = interfaceId;
  }

  public boolean isServicePathRendered()
  {
    return servicePathRendered;
  }

  public void setServicePathRendered(boolean servicePathRendered)
  {
    this.servicePathRendered = servicePathRendered;
  }

  public boolean isRoutingUrlRendered()
  {
    return routingUrlRendered;
  }

  public void setRoutingUrlRendered(boolean routingUrlRendered)
  {
    this.routingUrlRendered = routingUrlRendered;
  }

  public boolean isRefreshDisabled()
  {
    return refreshDisabled;
  }

  public void setRefreshDisabled(boolean refreshDisabled)
  {
    this.refreshDisabled = refreshDisabled;
  }

  public List<InterfaceMethod> getSelectedMethods()
  {
    if (selectedMethods == null)
    {
      selectedMethods = new ArrayList<>();
    }

    return selectedMethods;
  }

  public void setSelectedMethods(List<InterfaceMethod> selectedMethods)
  {
    this.selectedMethods = selectedMethods;
  }

  public boolean isEditMethodDisabled()
  {
    return editMethodDisabled;
  }

  public void setEditMethodDisabled(boolean editMethodDisabled)
  {
    this.editMethodDisabled = editMethodDisabled;
  }

  public boolean isDeleteMethodDisabled()
  {
    return deleteMethodDisabled;
  }

  public void setDeleteMethodDisabled(boolean deleteMethodDisabled)
  {
    this.deleteMethodDisabled = deleteMethodDisabled;
  }

  public InterfaceMethodVS getMethodVS()
  {
    return methodVS;
  }

  public boolean isSaveDeleteInterfaceRendered()
  {
    return saveDeleteInterfaceRendered;
  }

  public void setSaveDeleteInterfaceRendered(boolean saveDeleteInterfaceRendered)
  {
    this.saveDeleteInterfaceRendered = saveDeleteInterfaceRendered;
  }
}