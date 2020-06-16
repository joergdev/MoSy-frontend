package com.github.joergdev.mosy.frontend.view.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.api.model.MockData;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.api.model.RecordConfig;
import com.github.joergdev.mosy.frontend.Message;
import com.github.joergdev.mosy.frontend.MessageLevel;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.utils.TreeData;
import com.github.joergdev.mosy.frontend.validation.NotNull;
import com.github.joergdev.mosy.frontend.validation.SelectionValidation;
import com.github.joergdev.mosy.frontend.validation.StringNotEmpty;
import com.github.joergdev.mosy.frontend.validation.UniqueData;
import com.github.joergdev.mosy.frontend.view.InterfaceMethodVS;
import com.github.joergdev.mosy.frontend.view.InterfaceV;
import com.github.joergdev.mosy.frontend.view.UploadMockdataV;
import com.github.joergdev.mosy.frontend.view.controller.core.AbstractViewController;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;

public class InterfaceVC extends AbstractViewController<InterfaceV>
{
  private Interface apiInterface;
  private Interface apiInterfaceClone;

  private InterfaceMethod apiMethodSelected;
  private Set<Integer> methodsDetailDataLoaded = new HashSet<>();

  private RecordConfig apiRecordConfigSelected;

  private MockData apiMockDataSelected;

  public void showLoadRefresh()
  {
    new ShowLoadRefreshExecution().execute();
  }

  @Override
  public void refresh()
  {
    showLoadRefresh();
  }

  private class ShowLoadRefreshExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // no validation
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      if (view.getInterfaceId() != null)
      {
        return new Message(MessageLevel.INFO, "loaded_var", Resources.getLabel("interface"));
      }

      return null;
    }

    @Override
    protected void _execute()
      throws Exception
    {
      if (view.getInterfaceId() != null)
      {
        apiInterface = invokeApiCall(apiClient -> apiClient.loadInterface(view.getInterfaceId()))
            .getInterface();
      }
      else
      {
        apiInterface = new Interface();
      }

      apiInterfaceClone = apiInterface.clone();

      buildTreeData();

      transferInterfaceBaseDataToView();

      // Methods
      view.getTblMethods().clear();

      if (apiInterfaceClone.getMethods() != null)
      {
        view.getTblMethods().addAll(apiInterfaceClone.getMethods());
      }

      updateComponents();

      view.updateTree();
    }

    private void transferInterfaceBaseDataToView()
    {
      view.setName(apiInterfaceClone.getName());
      view.setInterfaceTypeSelected(apiInterfaceClone.getType());
      view.setServicePath(apiInterfaceClone.getServicePath());

      view.setMockDisabled(Boolean.TRUE.equals(apiInterfaceClone.getMockDisabled()));
      view.setMockDisabledOnStartup(Boolean.TRUE.equals(apiInterfaceClone.getMockDisabledOnStartup()));

      view.setRoutingOnNoMockData(Boolean.TRUE.equals(apiInterfaceClone.getRoutingOnNoMockData()));
      view.setRoutingUrl(apiInterfaceClone.getRoutingUrl());

      view.setRecord(Boolean.TRUE.equals(apiInterfaceClone.getRecord()));
    }

    private void buildTreeData()
    {
      List<TreeData> treeDataList = new ArrayList<>();

      // baseData
      treeDataList
          .add(new TreeData(Resources.getLabel("interface_data"), Resources.SITE_INTERFACE_BASEDATA, true));

      // Methods
      buildTreeDataMethods(treeDataList);

      view.setTreeDataList(treeDataList);
    }

    private void buildTreeDataMethods(List<TreeData> treeDataList)
    {
      TreeData treeDataMethods = new TreeData(Resources.getLabel("methods"),
          Resources.SITE_INTERFACE_METHODS);
      treeDataList.add(treeDataMethods);

      if (apiInterfaceClone.getMethods() != null)
      {
        for (InterfaceMethod method : apiInterfaceClone.getMethods())
        {
          buildTreeDataMethod(treeDataMethods, method);
        }
      }
    }
  }

  private void buildTreeDataMethod(TreeData treeDataMethods, InterfaceMethod method)
  {
    TreeData treeDataMethod = new TreeData(method.getName(), Resources.SITE_INTERFACE_METHOD_BASEDATA,
        method);

    treeDataMethods.getSubEntries().add(treeDataMethod);

    // MockData
    TreeData treeDataMockDataOv = new TreeData(Resources.getLabel("mockdata"),
        Resources.SITE_INTERFACE_METHOD_MOCKDATA_OVERVIEW);
    treeDataMethod.getSubEntries().add(treeDataMockDataOv);

    for (MockData apiMockData : method.getMockData())
    {
      addTreeDataMockData(treeDataMockDataOv, apiMockData);
    }

    // RecordConfigs
    TreeData treeDataRecordConfOv = new TreeData(Resources.getLabel("recordconfigs"),
        Resources.SITE_INTERFACE_METHOD_RECORDCONF_OVERVIEW);
    treeDataMethod.getSubEntries().add(treeDataRecordConfOv);

    for (RecordConfig apiRc : method.getRecordConfigs())
    {
      addTreeDataRecordConfig(treeDataRecordConfOv, apiRc);
    }
  }

  private void addTreeDataMockData(TreeData treeDataMockDataOv, MockData apiMockData)
  {
    treeDataMockDataOv.getSubEntries()
        .add(new TreeData(apiMockData.getTitle(), Resources.SITE_INTERFACE_METHOD_MOCKDATA, apiMockData));
  }

  private void addTreeDataRecordConfig(TreeData treeDataRecordConfOv, RecordConfig apiRc)
  {
    treeDataRecordConfOv.getSubEntries()
        .add(new TreeData(apiRc.getTitle(), Resources.SITE_INTERFACE_METHOD_RECORDCONF, apiRc));
  }

  public void updateComponents()
  {
    view.setRefreshDisabled(view.getInterfaceId() == null);

    boolean mockEnabled = !Boolean.TRUE.equals(view.isMockDisabled());
    boolean mockEnabledOnStartup = !Boolean.TRUE.equals(view.isMockDisabledOnStartup());

    boolean routinOnNoMockDataEnabled = mockEnabled || mockEnabledOnStartup;

    // only if mock (onStartup)
    view.setRoutingOnNoMockDataDisabled(!routinOnNoMockDataEnabled);
    if (!routinOnNoMockDataEnabled)
    {
      view.setRoutingOnNoMockData(false);
    }

    // disable record if not mock
    //    or mock + no routing
    view.setRecordDisabled((!mockEnabled || !mockEnabledOnStartup) || !view.isRoutingOnNoMockData());
    if (view.isRecordDisabled())
    {
      view.setRecord(false);
    }

    view.setDeleteInterfaceDisabled(apiInterfaceClone.getInterfaceId() == null);

    // servicePath rendered
    view.setServicePathRendered(
        view.getInterfaceTypeSelected() == null || view.getInterfaceTypeSelected().servicePathSettable);

    if (!view.isServicePathRendered())
    {
      view.setServicePath(null);
    }

    // RoutingURL rendererd
    view.setRoutingUrlRendered(
        view.getInterfaceTypeSelected() != null && view.getInterfaceTypeSelected().directRoutingPossible);

    if (!view.isRoutingUrlRendered())
    {
      view.setRoutingUrl(null);
    }

    updateComponentsMethodOverview();
    updateComponentsMethod();
    updateComponentsRecordConfigOverview();
    updateComponentsRecordConfig();
    updateComponentsMockDataOverview();
    updateComponentsMockData();
    updateComponentsSaveDeleteInterfaceRendered();
  }

  private void updateComponentsSaveDeleteInterfaceRendered()
  {
    view.setSaveDeleteInterfaceRendered(!Arrays.asList(Resources.SITE_INTERFACE_METHOD_RECORDCONF_OVERVIEW,
        Resources.SITE_INTERFACE_METHOD_RECORDCONF, Resources.SITE_INTERFACE_METHOD_MOCKDATA_OVERVIEW,
        Resources.SITE_INTERFACE_METHOD_MOCKDATA).contains(view.getDataPanel()));
  }

  private void updateComponentsMethodOverview()
  {
    int cntMethodsSelected = view.getSelectedMethods().size();

    view.setDeleteMethodDisabled(cntMethodsSelected == 0);
    view.setEditMethodDisabled(cntMethodsSelected != 1);
  }

  public void cancel()
  {
    new CancelExecution().execute();
  }

  private class CancelExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // no validation
    }

    @Override
    protected void _execute()
      throws Exception
    {
      JsfUtils.redirect(Resources.SITE_MAIN);
    }
  }

  public void treeNodeSelected(TreeData treeData)
  {
    new TreeNodeSelectedExecution(treeData).execute();
  }

  private class TreeNodeSelectedExecution extends Execution
  {
    private TreeData treeData;

    protected TreeNodeSelectedExecution(TreeData treeData)
    {
      this.treeData = treeData;
    }

    @Override
    protected void createPreValidations()
    {
      // no validations
    }

    @Override
    protected void _execute()
      throws Exception
    {
      String viewPage = treeData.getViewPage();

      // page in interface view
      if (!Utils.isEmpty(viewPage))
      {
        if (Resources.SITE_INTERFACE_METHOD_BASEDATA.equals(viewPage))
        {
          apiMethodSelected = treeData.getEntity();

          showMethod(this);
        }
        else if (Resources.SITE_INTERFACE_METHOD_RECORDCONF_OVERVIEW.equals(viewPage))
        {
          // set selected method
          TreeData tdMethod = getTreeDataMethods().getSubEntries().stream()
              .filter(tdM -> tdM.getSubEntries().contains(treeData)).findAny().orElse(null);

          apiMethodSelected = tdMethod.getEntity();

          updateComponentsRecordConfigOverview();

          view.setDataPanel(viewPage);
        }
        else if (Resources.SITE_INTERFACE_METHOD_RECORDCONF.equals(viewPage))
        {
          // set selected method
          for (TreeData tdMethod : getTreeDataMethods().getSubEntries())
          {
            TreeData tdRcOverview = getTreeDataMethodRecordconfigOverview(tdMethod);

            if (tdRcOverview.getSubEntries().contains(treeData))
            {
              apiMethodSelected = tdMethod.getEntity();
              break;
            }
          }

          apiRecordConfigSelected = treeData.getEntity();

          showRecordConfig(this);
        }
        else if (Resources.SITE_INTERFACE_METHOD_MOCKDATA_OVERVIEW.equals(viewPage))
        {
          // set selected method
          TreeData tdMethod = getTreeDataMethods().getSubEntries().stream()
              .filter(tdM -> tdM.getSubEntries().contains(treeData)).findAny().orElse(null);

          apiMethodSelected = tdMethod.getEntity();

          updateComponentsMockDataOverview();

          view.setDataPanel(viewPage);
        }
        else if (Resources.SITE_INTERFACE_METHOD_MOCKDATA.equals(viewPage))
        {
          // set selected method
          for (TreeData tdMethod : getTreeDataMethods().getSubEntries())
          {
            TreeData tdMdOverview = getTreeDataMethodMockDataOverview(tdMethod);

            if (tdMdOverview.getSubEntries().contains(treeData))
            {
              apiMethodSelected = tdMethod.getEntity();
              break;
            }
          }

          apiMockDataSelected = treeData.getEntity();

          showMockData(this);
        }
        else
        {
          view.setDataPanel(viewPage);
        }

        updateComponentsSaveDeleteInterfaceRendered();
      }
    }
  }

  public void treeNodeExpanded(TreeData treeData)
  {
    new TreeNodeExpandedExecution(treeData).execute();
  }

  private class TreeNodeExpandedExecution extends Execution
  {
    private TreeData treeData;

    protected TreeNodeExpandedExecution(TreeData treeData)
    {
      this.treeData = treeData;
    }

    @Override
    protected void createPreValidations()
    {
      // no validations
    }

    @Override
    protected void _execute()
      throws Exception
    {
      String viewPage = treeData.getViewPage();

      if (Resources.SITE_INTERFACE_METHOD_BASEDATA.equals(viewPage))
      {
        postLoadMethodDetailData(this, treeData.getEntity());
      }
    }
  }

  public void saveInterface()
  {
    new SaveInterfaceExecution().execute();
  }

  private class SaveInterfaceExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new StringNotEmpty(view.getName(), "name"));

      addValidation(new NotNull(view.getInterfaceTypeSelected(), "type"));

      addValidation(new StringNotEmpty(view.getRoutingUrl(), "routing_URL")
          .addCondition(() -> view.isRoutingOnNoMockData() && view.getInterfaceTypeSelected() != null
                              && view.getInterfaceTypeSelected().directRoutingPossible));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "saved_var", Resources.getLabel("interface"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      // update copyModel
      apiInterfaceClone.setName(view.getName());
      apiInterfaceClone.setType(view.getInterfaceTypeSelected());
      apiInterfaceClone.setServicePath(view.getServicePath());
      apiInterfaceClone.setMockDisabled(view.isMockDisabled());
      apiInterfaceClone.setMockDisabledOnStartup(view.isMockDisabledOnStartup());
      apiInterfaceClone.setRoutingOnNoMockData(view.isRoutingOnNoMockData());
      apiInterfaceClone.setRoutingUrl(view.getRoutingUrl());
      apiInterfaceClone.setRecord(view.isRecord());

      // save
      Interface apiInterfaceSaved = invokeApiCall(apiClient -> apiClient.saveInterface(apiInterfaceClone))
          .getInterface();

      // update model (copyModel=mode, +id's aus save uebertragen)
      apiInterfaceClone.setInterfaceId(apiInterfaceSaved.getInterfaceId());

      apiInterfaceClone.getMethods().removeIf(m -> m.isDelete());

      for (InterfaceMethod apiMethod : apiInterfaceClone.getMethods())
      {
        InterfaceMethod apiMethodSaved = apiInterfaceSaved.getMethodByName(apiMethod.getName());

        apiMethod.setInterfaceMethodId(apiMethodSaved.getInterfaceMethodId());
      }

      apiInterface = apiInterfaceClone.clone();

      view.setInterfaceId(apiInterfaceClone.getInterfaceId());

      // updateComponents
      updateComponents();
    }
  }

  public void deleteInterface()
  {
    new DeleteInterfaceExecution().execute();
  }

  private class DeleteInterfaceExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(view.getInterfaceId(), "id"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel("interface"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      invokeApiCall(apiClient -> apiClient.deleteInterface(view.getInterfaceId()));

      JsfUtils.redirect(Resources.SITE_MAIN);
    }
  }

  //--- Methods ----------------------

  public void newMethod()
  {
    apiMethodSelected = null;

    new ShowMethodExecution().execute();
  }

  public void editMethod()
  {
    if (view.getSelectedMethods().size() == 1)
    {
      apiMethodSelected = Utils.getFirstElementOfCollection(view.getSelectedMethods());
    }

    new ShowMethodExecution().execute();
  }

  public void deleteMethods()
  {
    new DeleteMethodsExecution().execute();
  }

  private class DeleteMethodsExecution extends Execution
  {
    private int cntDeleted = 0;
    private int cntMarkedForDeletion = 0;

    @Override
    protected void createPreValidations()
    {
      addValidation(new SelectionValidation(view.getSelectedMethods(), "method"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      String msg = null;

      if (cntDeleted > 0 && cntMarkedForDeletion > 0)
      {
        msg = "deleted_var_var_marked_for_deletion";
      }
      else if (cntDeleted > 0)
      {
        msg = "deleted_var";
      }
      else if (cntMarkedForDeletion > 0)
      {
        msg = "var_marked_for_deletion";
      }

      if (msg != null)
      {
        List<String> details = new ArrayList<>(2);

        if (cntDeleted > 0)
        {
          details.add(String.valueOf(cntDeleted) + " " + (cntDeleted > 1
              ? Resources.getLabel("methods")
              : Resources.getLabel("method")));
        }

        if (cntMarkedForDeletion > 0)
        {
          details.add(String.valueOf(cntMarkedForDeletion) + " " + (cntMarkedForDeletion > 1
              ? Resources.getLabel("methods")
              : Resources.getLabel("method")));
        }

        return new Message(MessageLevel.INFO, msg, details.toArray(new String[details.size()]));
      }

      return null;
    }

    @Override
    protected void _execute()
      throws Exception
    {
      List<InterfaceMethod> methodsToDelete = new ArrayList<>(view.getSelectedMethods());

      for (InterfaceMethod apiMethod : methodsToDelete)
      {
        if (apiMethod.getInterfaceMethodId() == null)
        {
          apiInterfaceClone.getMethods().remove(apiMethod);

          cntDeleted++;
        }
        else
        {
          apiMethod.setDelete(true);

          cntMarkedForDeletion++;
        }

        removeMethodFromView(apiMethod);
      }

      // Update
      updateComponents();

      getView().updateTree();
    }
  }

  private void removeMethodFromView(InterfaceMethod apiMethod)
  {
    view.getTblMethods().remove(apiMethod);
    view.getSelectedMethods().remove(apiMethod);

    getTreeDataMethods().getSubEntries().removeIf(td -> apiMethod.equals(td.getEntity()));
  }

  public void handleMethodsSelection()
  {
    updateComponentsMethodOverview();
  }

  // --- End Methods ----------------------

  // --- Methods -------------------------

  private class ShowMethodExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // no validations
    }

    @Override
    protected void _execute()
      throws Exception
    {
      showMethod(this);
    }
  }

  private void showMethod(Execution execution)
  {
    postLoadMethodDetailData(execution, apiMethodSelected);

    getView().setDataPanel(Resources.SITE_INTERFACE_METHOD_BASEDATA);

    // Transfer Model->View
    InterfaceMethodVS methodVS = view.getMethodVS();
    InterfaceMethod apiMethod = Utils.nvl(apiMethodSelected, () -> new InterfaceMethod());

    methodVS.setName(apiMethod.getName());

    methodVS.setServicePath(apiMethod.getServicePath());

    methodVS.setMockDisabled(Boolean.TRUE.equals(apiMethod.getMockDisabled()));
    methodVS.setMockDisabledOnStartup(Boolean.TRUE.equals(apiMethod.getMockDisabledOnStartup()));

    methodVS.setRoutingOnNoMockData(Boolean.TRUE.equals(apiMethod.getRoutingOnNoMockData()));

    methodVS.setRecord(Boolean.TRUE.equals(apiMethod.getRecord()));

    methodVS.setCountCalls(apiMethod.getCountCalls());

    // UpdateComponents
    updateComponentsMethod();
  }

  private void postLoadMethodDetailData(Execution execution, InterfaceMethod apiMethod)
  {
    if (apiMethod != null)
    {
      Integer methodID = apiMethod.getInterfaceMethodId();

      if (methodID != null && !methodsDetailDataLoaded.contains(methodID))
      {
        loadMethodMockdataAndRecordConfigs(execution, apiMethod);

        // transfer recordconfigs to table
        view.getMethodVS().getTblRecordConfigs().clear();
        view.getMethodVS().getTblRecordConfigs().addAll(apiMethod.getRecordConfigs());

        // transfer mockdata to table
        view.getMethodVS().getTblMockData().clear();
        view.getMethodVS().getTblMockData().addAll(apiMethod.getMockData());

        // Transfer to tree
        transferMethodDetailDataToTree(apiMethod);

        view.updateTree();

        methodsDetailDataLoaded.add(methodID);
      }
    }
  }

  private TreeData getTreeDataMethods()
  {
    return view.getTreeDataList().stream()
        .filter(td -> Resources.SITE_INTERFACE_METHODS.equals(td.getViewPage())).findAny().orElse(null);
  }

  private TreeData getTreeDataMethod()
  {
    return getTreeDataMethods().getSubEntries().stream()
        .filter(td -> apiMethodSelected.equals(td.getEntity())).findAny().orElse(null);
  }

  private TreeData getTreeDataMethodRecordconfigOverview()
  {
    return getTreeDataMethodRecordconfigOverview(getTreeDataMethod());
  }

  private TreeData getTreeDataMethodRecordconfigOverview(TreeData tdMethod)
  {
    return tdMethod.getSubEntries().stream()
        .filter(td -> Resources.SITE_INTERFACE_METHOD_RECORDCONF_OVERVIEW.equals(td.getViewPage())).findAny()
        .orElse(null);
  }

  private TreeData getTreeDataMethodMockDataOverview()
  {
    return getTreeDataMethodMockDataOverview(getTreeDataMethod());
  }

  private TreeData getTreeDataMethodMockDataOverview(TreeData tdMethod)
  {
    return tdMethod.getSubEntries().stream()
        .filter(td -> Resources.SITE_INTERFACE_METHOD_MOCKDATA_OVERVIEW.equals(td.getViewPage())).findAny()
        .orElse(null);
  }

  private void transferMethodDetailDataToTree(InterfaceMethod apiMethod)
  {
    TreeData tdMethod = getTreeDataMethods().getSubEntries().stream().filter(td -> apiMethod
        .getInterfaceMethodId().equals(td.getEntity(InterfaceMethod.class).getInterfaceMethodId())).findAny()
        .orElse(null);

    // Mockdata
    TreeData tdMockDataOv = tdMethod.getSubEntries().stream()
        .filter(td -> Resources.SITE_INTERFACE_METHOD_MOCKDATA_OVERVIEW.equals(td.getViewPage())).findAny()
        .orElse(null);

    tdMockDataOv.getSubEntries().clear();

    for (MockData apiMockData : apiMethod.getMockData())
    {
      addTreeDataMockData(tdMockDataOv, apiMockData);
    }

    // RecordConfigs
    TreeData tdRecordConfigOv = tdMethod.getSubEntries().stream()
        .filter(td -> Resources.SITE_INTERFACE_METHOD_RECORDCONF_OVERVIEW.equals(td.getViewPage())).findAny()
        .orElse(null);

    tdRecordConfigOv.getSubEntries().clear();

    for (RecordConfig apiRecordConfig : apiMethod.getRecordConfigs())
    {
      addTreeDataRecordConfig(tdRecordConfigOv, apiRecordConfig);
    }
  }

  private void loadMethodMockdataAndRecordConfigs(Execution execution, InterfaceMethod apiMethod)
  {
    Integer methodID = apiMethod.getInterfaceMethodId();

    List<MockData> listApiMockData = execution
        .invokeApiCall(apiClient -> apiClient.loadMethodMockData(view.getInterfaceId(), methodID))
        .getMockData();

    apiMethod.getMockData().clear();
    apiMethod.getMockData().addAll(listApiMockData);

    List<RecordConfig> listApiRecordConfigs = execution
        .invokeApiCall(apiClient -> apiClient.loadMethodRecordConfigs(view.getInterfaceId(), methodID))
        .getRecordConfigs();

    apiMethod.getRecordConfigs().clear();
    apiMethod.getRecordConfigs().addAll(listApiRecordConfigs);
  }

  public void updateComponentsMethod()
  {
    InterfaceMethodVS methodVS = view.getMethodVS();

    boolean mockEnabled = !Boolean.TRUE.equals(methodVS.isMockDisabled());
    boolean mockEnabledOnStartup = !Boolean.TRUE.equals(methodVS.isMockDisabledOnStartup());

    boolean routinOnNoMockDataEnabled = mockEnabled || mockEnabledOnStartup;

    // only if mock (onStartup)
    methodVS.setRoutingOnNoMockDataDisabled(!routinOnNoMockDataEnabled);
    if (!routinOnNoMockDataEnabled)
    {
      methodVS.setRoutingOnNoMockData(false);
    }

    // disable record if not mock
    //    or mock + no routing
    methodVS.setRecordDisabled((!mockEnabled || !mockEnabledOnStartup) || !methodVS.isRoutingOnNoMockData());
    if (methodVS.isRecordDisabled())
    {
      methodVS.setRecord(false);
    }

    // servicePath rendered
    methodVS.setServicePathRendered(
        view.getInterfaceTypeSelected() == null || view.getInterfaceTypeSelected().servicePathSettable);

    if (!methodVS.isServicePathRendered())
    {
      methodVS.setServicePath(null);
    }

    // delete method disabled if new
    methodVS.setDeleteMethodDisabled(apiMethodSelected == null);
  }

  public void saveMethod()
  {
    new SaveMethodExecution().execute();
  }

  private class SaveMethodExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      final String name = view.getMethodVS().getName();

      addValidation(new StringNotEmpty(name, "name") //
          .addSubValidation( //
              new UniqueData<>(apiInterfaceClone.getMethods(),
                  m -> apiMethodSelected != m && name.equalsIgnoreCase(m.getName()))));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "data_for_Var_confirmed", Resources.getLabel("method"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      InterfaceMethodVS methodVS = view.getMethodVS();
      String nameNew = methodVS.getName();

      boolean updateTree = false;

      if (apiMethodSelected == null)
      {
        apiMethodSelected = new InterfaceMethod();
        apiMethodSelected.setName(nameNew);

        apiInterfaceClone.getMethods().add(apiMethodSelected);

        view.getTblMethods().add(apiMethodSelected);

        buildTreeDataMethod(getTreeDataMethods(), apiMethodSelected);

        updateTree = true;
      }
      else
      {
        String nameOld = apiMethodSelected.getName();

        if (!nameOld.equals(nameNew))
        {
          apiMethodSelected.setName(nameNew);

          TreeData tdMethod = getTreeDataMethods().getSubEntries().stream()
              .filter(td -> nameOld.equals(td.getText())).findAny().orElse(null);

          tdMethod.setText(nameNew);

          updateTree = true;
        }
      }

      // update copyModel
      apiMethodSelected.setServicePath(methodVS.getServicePath());
      apiMethodSelected.setMockDisabled(methodVS.isMockDisabled());
      apiMethodSelected.setMockDisabledOnStartup(methodVS.isMockDisabledOnStartup());
      apiMethodSelected.setRoutingOnNoMockData(methodVS.isRoutingOnNoMockData());
      apiMethodSelected.setRecord(methodVS.isRecord());

      // updateComponents
      updateComponentsMethod();

      if (updateTree)
      {
        view.updateTree();
      }
    }
  }

  public void deleteMethod()
  {
    new DeleteMethodExecution().execute();
  }

  private class DeleteMethodExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(apiMethodSelected, "method"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel("method"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      if (apiMethodSelected.getInterfaceMethodId() == null)
      {
        apiInterfaceClone.getMethods().remove(apiMethodSelected);
      }
      else
      {
        apiMethodSelected.setDelete(true);
      }

      removeMethodFromView(apiMethodSelected);

      apiMethodSelected = null;

      getView().setDataPanel(Resources.SITE_INTERFACE_METHODS);

      // update
      updateComponents();

      getView().updateTree();
    }
  }

  // --- End Method ----------------------

  //--- Recordconfig overview ----------------------

  public void newRecordConfig()
  {
    apiRecordConfigSelected = null;

    new ShowRecordConfigExecution().execute();
  }

  public void editRecordConfig()
  {
    if (view.getMethodVS().getSelectedRecordConfigs().size() == 1)
    {
      apiRecordConfigSelected = Utils
          .getFirstElementOfCollection(view.getMethodVS().getSelectedRecordConfigs());
    }

    new ShowRecordConfigExecution().execute();
  }

  public void deleteRecordConfigs()
  {
    new DeleteRecordConfigsExecution().execute();
  }

  private class DeleteRecordConfigsExecution extends Execution
  {
    private int cntDeleted = 0;

    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(apiMethodSelected, "method"));
      addValidation(new SelectionValidation(view.getMethodVS().getSelectedRecordConfigs(), "recordconfig"));

      view.getMethodVS().getSelectedRecordConfigs()
          .forEach(rc -> addValidation(new NotNull(rc.getRecordConfigId(), "id")));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      if (cntDeleted > 0)
      {
        String detail = String.valueOf(cntDeleted) + " " + (cntDeleted > 1
            ? Resources.getLabel("recordconfigs")
            : Resources.getLabel("recordconfig"));

        return new Message(MessageLevel.INFO, "deleted_var", detail);
      }

      return null;
    }

    @Override
    protected void _execute()
      throws Exception
    {
      List<RecordConfig> recordconfigsToDelete = new ArrayList<>(
          view.getMethodVS().getSelectedRecordConfigs());

      for (RecordConfig apiRecordConfig : recordconfigsToDelete)
      {
        invokeApiCall(apiClient -> apiClient.deleteRecordConfig(apiRecordConfig.getRecordConfigId()));

        apiMethodSelected.getRecordConfigs().remove(apiRecordConfig);

        cntDeleted++;

        removeRecordconfigFromView(apiRecordConfig);
      }

      // Update
      updateComponents();

      getView().updateTree();
    }
  }

  private void removeRecordconfigFromView(RecordConfig apiRecordConfig)
  {
    view.getMethodVS().getTblRecordConfigs().remove(apiRecordConfig);
    view.getMethodVS().getSelectedRecordConfigs().remove(apiRecordConfig);

    getTreeDataMethodRecordconfigOverview().getSubEntries()
        .removeIf(td -> apiRecordConfig.equals(td.getEntity()));
  }

  public void handleRecordConfigsSelection()
  {
    updateComponentsRecordConfigOverview();
  }

  private void updateComponentsRecordConfigOverview()
  {
    int cntRecordconfigsSelected = view.getMethodVS().getSelectedRecordConfigs().size();

    view.getMethodVS().setDeleteRecordConfigsDisabled(cntRecordconfigsSelected == 0);
    view.getMethodVS().setEditRecordConfigDisabled(cntRecordconfigsSelected != 1);

    // new recordconfig only allowed if method is saved
    view.getMethodVS().setNewRecordConfigDisabled(
        apiMethodSelected == null || apiMethodSelected.getInterfaceMethodId() == null);

    updateComponentsSaveDeleteInterfaceRendered();
  }

  // --- End Recordconfig overview ----------------------

  // --- Recordconfig -----------------------------------

  private class ShowRecordConfigExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // no validations
    }

    @Override
    protected void _execute()
      throws Exception
    {
      showRecordConfig(this);
    }
  }

  private void showRecordConfig(Execution execution)
  {
    getView().setDataPanel(Resources.SITE_INTERFACE_METHOD_RECORDCONF);

    // get recordConfig selected
    InterfaceMethodVS methodVS = view.getMethodVS();
    RecordConfig apiRecordConfig = Utils.nvl(apiRecordConfigSelected, () -> new RecordConfig());

    // load data
    if (apiRecordConfig.getRecordConfigId() != null)
    {
      RecordConfig apiRecordConfigLoaded = execution
          .invokeApiCall(apiClient -> apiClient.loadRecordConfig(apiRecordConfig.getRecordConfigId()))
          .getRecordConfig();

      apiRecordConfig.setEnabled(apiRecordConfigLoaded.getEnabled());
      apiRecordConfig.setRequestData(apiRecordConfigLoaded.getRequestData());
      apiRecordConfig.setTitle(apiRecordConfigLoaded.getTitle());
    }

    // Transfer Model->View
    methodVS.setRcTitle(apiRecordConfig.getTitle());
    methodVS.setRcEnabled(Boolean.TRUE.equals(apiRecordConfig.getEnabled()));
    methodVS.setRcRequestdata(apiRecordConfig.getRequestData());

    // UpdateComponents
    updateComponentsRecordConfig();
  }

  public void updateComponentsRecordConfig()
  {
    // delete recordConfig disabled if new
    view.getMethodVS().setDeleteRecordConfigDisabled(apiRecordConfigSelected == null);

    updateComponentsSaveDeleteInterfaceRendered();
  }

  public void saveRecordConfig()
  {
    new SaveRecordConfigExecution().execute();
  }

  private class SaveRecordConfigExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      InterfaceMethodVS methodVS = view.getMethodVS();

      addValidation(new NotNull(apiMethodSelected, "method"));

      final String title = methodVS.getRcTitle();

      addValidation(new StringNotEmpty(title, "title") //
          .addSubValidation( //
              new UniqueData<>((apiMethodSelected == null
                  ? new ArrayList<>()
                  : apiMethodSelected.getRecordConfigs()),
                  rc -> apiRecordConfigSelected != rc && title.equalsIgnoreCase(rc.getTitle()))));

      addValidation(new StringNotEmpty(methodVS.getRcRequestdata(), "requestdata"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "saved_var", Resources.getLabel("recordconfig"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      InterfaceMethodVS methodVS = view.getMethodVS();
      String titleNew = methodVS.getRcTitle();

      boolean newRecordConfig = false;
      String titleOldChanged = null;

      if (apiRecordConfigSelected == null)
      {
        apiRecordConfigSelected = new RecordConfig();

        newRecordConfig = true;
      }
      else
      {
        String titleOld = apiRecordConfigSelected.getTitle();

        if (!titleOld.equals(titleNew))
        {
          titleOldChanged = titleOld;
        }
      }

      // update copyModel
      apiRecordConfigSelected.setTitle(titleNew);
      apiRecordConfigSelected.setEnabled(methodVS.isRcEnabled());
      apiRecordConfigSelected.setRequestData(methodVS.getRcRequestdata());

      apiRecordConfigSelected.setInterfaceMethod(new InterfaceMethod());
      apiRecordConfigSelected.getInterfaceMethod()
          .setInterfaceMethodId(apiMethodSelected.getInterfaceMethodId());

      // save
      Integer savedID = invokeApiCall(apiClient -> apiClient.saveRecordConfig(apiRecordConfigSelected))
          .getRecordConfigID();

      apiRecordConfigSelected.setRecordConfigId(savedID);

      // Update tbl/tree
      boolean updateTree = false;

      if (newRecordConfig)
      {
        apiMethodSelected.getRecordConfigs().add(apiRecordConfigSelected);

        methodVS.getTblRecordConfigs().add(apiRecordConfigSelected);

        addTreeDataRecordConfig(getTreeDataMethodRecordconfigOverview(), apiRecordConfigSelected);

        updateTree = true;
      }
      else if (titleOldChanged != null)
      {
        final String titleOldChangedTmp = titleOldChanged;

        TreeData tdRecordConfig = getTreeDataMethodRecordconfigOverview().getSubEntries().stream()
            .filter(td -> titleOldChangedTmp.equals(td.getText())).findAny().orElse(null);

        tdRecordConfig.setText(titleNew);

        updateTree = true;
      }

      // updateComponents
      updateComponentsRecordConfig();

      if (updateTree)
      {
        view.updateTree();
      }
    }
  }

  public void deleteRecordConfig()
  {
    new DeleteRecordConfigExecution().execute();
  }

  private class DeleteRecordConfigExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(apiMethodSelected, "method"));
      addValidation(new NotNull(apiRecordConfigSelected, "recordconfig")
          .addSubValidation(new NotNull(() -> apiRecordConfigSelected.getRecordConfigId(), "id")) //
      );
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel("recordconfig"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      invokeApiCall(apiClient -> apiClient.deleteRecordConfig(apiRecordConfigSelected.getRecordConfigId()));

      apiMethodSelected.getRecordConfigs().remove(apiRecordConfigSelected);

      removeRecordconfigFromView(apiRecordConfigSelected);

      apiRecordConfigSelected = null;

      getView().setDataPanel(Resources.SITE_INTERFACE_METHOD_RECORDCONF_OVERVIEW);

      // update
      updateComponents();

      getView().updateTree();
    }
  }

  // --- End Recordconfig -------------------------------

  //--- MockData overview ----------------------

  public void newMockData()
  {
    apiMockDataSelected = null;

    new ShowMockDataExecution().execute();
  }

  public void editMockData()
  {
    if (view.getMethodVS().getSelectedMockDataList().size() == 1)
    {
      apiMockDataSelected = Utils.getFirstElementOfCollection(view.getMethodVS().getSelectedMockDataList());
    }

    new ShowMockDataExecution().execute();
  }

  public void deleteMockDataFromOverview()
  {
    new DeleteMockDataFromOverviewExecution().execute();
  }

  private class DeleteMockDataFromOverviewExecution extends Execution
  {
    private int cntDeleted = 0;

    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(apiMethodSelected, "method"));
      addValidation(new SelectionValidation(view.getMethodVS().getSelectedMockDataList(), "mockdata"));

      view.getMethodVS().getSelectedMockDataList()
          .forEach(md -> addValidation(new NotNull(md.getMockDataId(), "id")));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      if (cntDeleted > 0)
      {
        String detail = String.valueOf(cntDeleted) + " " + Resources.getLabel("mockdata");

        return new Message(MessageLevel.INFO, "deleted_var", detail);
      }

      return null;
    }

    @Override
    protected void _execute()
      throws Exception
    {
      List<MockData> mockDataToDelete = new ArrayList<>(view.getMethodVS().getSelectedMockDataList());

      for (MockData apiMockData : mockDataToDelete)
      {
        invokeApiCall(apiClient -> apiClient.deleteMockData(apiMockData.getMockDataId()));

        apiMethodSelected.getMockData().remove(apiMockData);

        cntDeleted++;

        removeMockDataFromView(apiMockData);
      }

      // Update
      updateComponents();

      getView().updateTree();
    }
  }

  private void removeMockDataFromView(MockData apiMockData)
  {
    view.getMethodVS().getTblMockData().remove(apiMockData);
    view.getMethodVS().getSelectedMockDataList().remove(apiMockData);

    getTreeDataMethodMockDataOverview().getSubEntries().removeIf(td -> apiMockData.equals(td.getEntity()));
  }

  public void handleMockDataSelection()
  {
    updateComponentsMockDataOverview();
  }

  private void updateComponentsMockDataOverview()
  {
    int cntMockDataSelected = view.getMethodVS().getSelectedMockDataList().size();

    view.getMethodVS().setDeleteMockDataOverviewDisabled(cntMockDataSelected == 0);
    view.getMethodVS().setEditMockDataDisabled(cntMockDataSelected != 1);

    // new mockData only allowed if method is saved
    view.getMethodVS().setNewMockDataDisabled(
        apiMethodSelected == null || apiMethodSelected.getInterfaceMethodId() == null);

    updateComponentsSaveDeleteInterfaceRendered();
  }

  public void uploadMockData()
  {
    new UploadMockDataExecution().execute();
  }

  private class UploadMockDataExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(apiInterface, "interface"))
          .addValidation(new NotNull(() -> apiInterface.getInterfaceId(), "interface"));
      addValidation(new NotNull(apiMethodSelected, "method"))
          .addValidation(new NotNull(() -> apiMethodSelected.getInterfaceMethodId(), "method"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return null;
    }

    @Override
    protected void _execute()
      throws Exception
    {
      Map<String, Object> queryParams = new HashMap<>();
      queryParams.put(UploadMockdataV.VIEW_PARAM_INTERFACE_ID, apiInterface.getInterfaceId());
      queryParams.put(UploadMockdataV.VIEW_PARAM_METHOD_ID, apiMethodSelected.getInterfaceMethodId());

      JsfUtils.redirect(Resources.SITE_UPLOAD_MOCKDATA, queryParams);
    }
  }

  // --- End MockData overview ----------------------

  // --- MockData -----------------------------------

  private class ShowMockDataExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // no validations
    }

    @Override
    protected void _execute()
      throws Exception
    {
      showMockData(this);
    }
  }

  private void showMockData(Execution execution)
  {
    InterfaceMethodVS methodVS = view.getMethodVS();

    // load mocksessions
    loadAndTransferMockSessionsToView(execution, methodVS);

    getView().setDataPanel(Resources.SITE_INTERFACE_METHOD_MOCKDATA);

    // get mockData selected
    MockData apiMockData = Utils.nvl(apiMockDataSelected, () -> new MockData());

    // load data
    if (apiMockData.getMockDataId() != null)
    {
      MockData apiMockDataLoaded = execution
          .invokeApiCall(apiClient -> apiClient.loadMockData(apiMockData.getMockDataId())).getMockData();

      ObjectUtils.copyValues(apiMockDataLoaded, apiMockData, "interfaceMethod", "mockSession");
      apiMockData.setMockSession(getMockSessionFromComboList(apiMockDataLoaded.getMockSession()));
    }

    // Transfer Model->View
    methodVS.setMockSessionSelected(apiMockData.getMockSession());
    methodVS.setMdTitle(apiMockData.getTitle());
    methodVS.setMdActive(Boolean.TRUE.equals(apiMockData.getActive()));
    methodVS.setMdRequest(apiMockData.getRequest());
    methodVS.setMdResponse(apiMockData.getResponse());
    methodVS.setMdCreated(apiMockData.getCreatedAsString());
    methodVS.setMdCountCalls(apiMockData.getCountCalls());

    // UpdateComponents
    updateComponentsMockData();
  }

  private void loadAndTransferMockSessionsToView(AbstractViewController<InterfaceV>.Execution execution,
                                                 InterfaceMethodVS methodVS)
  {
    List<MockSession> mockSessions = execution.invokeApiCall(apiClient -> apiClient.loadMocksessions())
        .getMockSessions();

    methodVS.getMockSessions().clear();
    methodVS.getMockSessions().addAll(mockSessions);

    MockSession mockSessionSelected = methodVS.getMockSessionSelected();
    if (mockSessionSelected != null)
    {
      methodVS.setMockSessionSelected(mockSessions.stream()
          .filter(ms -> mockSessionSelected.getMockSessionID().equals(ms.getMockSessionID())).findAny()
          .orElse(null));
    }
  }

  private MockSession getMockSessionFromComboList(MockSession mockSession)
  {
    if (mockSession != null && mockSession.getMockSessionID() != null)
    {
      Optional<MockSession> optMockSession = view.getMethodVS().getMockSessions().stream()
          .filter(ms -> mockSession.getMockSessionID().equals(ms.getMockSessionID())).findAny();

      if (optMockSession.isPresent())
      {
        return optMockSession.get();
      }
      else
      {
        view.getMethodVS().getMockSessions().add(mockSession);

        return mockSession;
      }
    }

    return null;
  }

  public void updateComponentsMockData()
  {
    // delete mockData disabled if new
    view.getMethodVS().setDeleteMockDataDisabled(apiMockDataSelected == null);

    updateComponentsSaveDeleteInterfaceRendered();
  }

  public void saveMockData()
  {
    new SaveMockDataExecution().execute();
  }

  private class SaveMockDataExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      InterfaceMethodVS methodVS = view.getMethodVS();

      addValidation(new NotNull(apiMethodSelected, "method"));

      final String title = methodVS.getMdTitle();

      addValidation(new StringNotEmpty(title, "title") //
          .addSubValidation( //
              new UniqueData<>((apiMethodSelected == null
                  ? new ArrayList<>()
                  : apiMethodSelected.getMockData()),
                  md -> apiMockDataSelected != md && title.equalsIgnoreCase(md.getTitle()))));

      addValidation(new StringNotEmpty(methodVS.getMdResponse(), "response"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "saved_var", Resources.getLabel("mockdata"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      InterfaceMethodVS methodVS = view.getMethodVS();
      String titleNew = methodVS.getMdTitle();

      boolean newMockData = false;
      String titleOldChanged = null;

      if (apiMockDataSelected == null)
      {
        apiMockDataSelected = new MockData();

        newMockData = true;
      }
      else
      {
        String titleOld = apiMockDataSelected.getTitle();

        if (!titleOld.equals(titleNew))
        {
          titleOldChanged = titleOld;
        }
      }

      // update copyModel
      apiMockDataSelected.setTitle(titleNew);
      apiMockDataSelected.setMockSession(methodVS.getMockSessionSelected());
      apiMockDataSelected.setActive(methodVS.isMdActive());
      apiMockDataSelected.setRequest(methodVS.getMdRequest());
      apiMockDataSelected.setResponse(methodVS.getMdResponse());

      apiMockDataSelected.setInterfaceMethod(new InterfaceMethod());
      apiMockDataSelected.getInterfaceMethod().setInterfaceMethodId(apiMethodSelected.getInterfaceMethodId());

      // save
      MockData apiMockDataSaved = invokeApiCall(apiClient -> apiClient.saveMockData(apiMockDataSelected))
          .getMockData();

      apiMockDataSelected.setMockDataId(apiMockDataSaved.getMockDataId());
      apiMockDataSelected.setCreated(apiMockDataSaved.getCreated());
      apiMockDataSelected.setCountCalls(apiMockDataSaved.getCountCalls());

      // Update tbl/tree
      boolean updateTree = false;

      if (newMockData)
      {
        apiMethodSelected.getMockData().add(apiMockDataSelected);

        methodVS.getTblMockData().add(apiMockDataSelected);

        addTreeDataMockData(getTreeDataMethodMockDataOverview(), apiMockDataSelected);

        updateTree = true;
      }
      else if (titleOldChanged != null)
      {
        final String titleOldChangedTmp = titleOldChanged;

        TreeData tdMockData = getTreeDataMethodMockDataOverview().getSubEntries().stream()
            .filter(td -> titleOldChangedTmp.equals(td.getText())).findAny().orElse(null);

        tdMockData.setText(titleNew);

        updateTree = true;
      }

      methodVS.setMdCreated(apiMockDataSelected.getCreatedAsString());
      methodVS.setMdCountCalls(apiMockDataSelected.getCountCalls());

      // updateComponents
      updateComponentsMockData();

      if (updateTree)
      {
        view.updateTree();
      }
    }
  }

  public void deleteMockData()
  {
    new DeleteMockDataExecution().execute();
  }

  private class DeleteMockDataExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(apiMethodSelected, "method"));
      addValidation(new NotNull(apiMockDataSelected, "mockdata")
          .addSubValidation(new NotNull(() -> apiMockDataSelected.getMockDataId(), "id")) //
      );
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel("mockdata"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      invokeApiCall(apiClient -> apiClient.deleteMockData(apiMockDataSelected.getMockDataId()));

      apiMethodSelected.getMockData().remove(apiMockDataSelected);

      removeMockDataFromView(apiMockDataSelected);

      apiMockDataSelected = null;

      getView().setDataPanel(Resources.SITE_INTERFACE_METHOD_MOCKDATA_OVERVIEW);

      // update
      updateComponents();

      getView().updateTree();
    }
  }

  // --- End MockData -------------------------------
}