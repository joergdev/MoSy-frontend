package de.joergdev.mosy.frontend.view.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import de.joergdev.mosy.api.model.BaseData;
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.InterfaceType;
import de.joergdev.mosy.api.model.MockData;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.api.model.PathParam;
import de.joergdev.mosy.api.model.Record;
import de.joergdev.mosy.api.model.RecordSession;
import de.joergdev.mosy.api.model.UrlArgument;
import de.joergdev.mosy.api.response.mockprofile.LoadProfilesResponse;
import de.joergdev.mosy.api.response.record.session.LoadSessionsResponse;
import de.joergdev.mosy.frontend.Message;
import de.joergdev.mosy.frontend.MessageLevel;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.frontend.model.YesNoGlobalOrInterfaceIndividuallyType;
import de.joergdev.mosy.frontend.utils.JsfUtils;
import de.joergdev.mosy.frontend.utils.TreeData;
import de.joergdev.mosy.frontend.validation.NotNull;
import de.joergdev.mosy.frontend.validation.SelectionValidation;
import de.joergdev.mosy.frontend.validation.UniqueSelectionValidation;
import de.joergdev.mosy.frontend.view.InterfaceV;
import de.joergdev.mosy.frontend.view.MainV;
import de.joergdev.mosy.frontend.view.MockProfileV;
import de.joergdev.mosy.frontend.view.RecordAsMockdataV;
import de.joergdev.mosy.frontend.view.controller.core.AbstractViewController;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;
import de.joergdev.mosy.shared.ValueWrapper;

public class MainVC extends AbstractViewController<MainV>
{
  private BaseData basedata;

  private Record apiRecordSelected;

  public void loadRefresh()
  {
    new LoadRefreshExecution().execute();
  }

  private class LoadRefreshExecution extends Execution
  {
    private String loadedMessageDetail;

    @Override
    protected void createPreValidations()
    {
      // no validations
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      Message msg = new Message();
      msg.setLevel(MessageLevel.INFO);
      msg.setMsg("loaded_var");
      msg.setMessageDetails(loadedMessageDetail);

      return msg;
    }

    @Override
    protected void _execute()
    {
      String dataPanel = view.getDataPanel();

      if (Utils.isEmpty(dataPanel) || Arrays.asList(Resources.SITE_MAIN_BASEDATA, Resources.SITE_MAIN_INTERFACES).contains(dataPanel))
      {
        loadedMessageDetail = Resources.getLabel("basedata") + " / " + Resources.getLabel("interfaces");

        loadRefreshBasedata();
      }
      else if (Resources.SITE_MAIN_RECORDS.equals(dataPanel))
      {
        loadedMessageDetail = Resources.getLabel("records");

        loadRefreshRecords();
        loadAndTransferRecordSessionsToView();
      }
      else if (Resources.SITE_MAIN_MOCK_PROFILES.equals(dataPanel))
      {
        loadedMessageDetail = Resources.getLabel("mock_profiles");

        loadRefreshMockProfiles();
      }
      else if (Resources.SITE_MAIN_RECORDSESSIONS.equals(dataPanel))
      {
        loadedMessageDetail = Resources.getLabel("record_sessions");

        loadRefreshRecordSessions();
      }
    }

    private void loadRefreshRecordSessions()
    {
      LoadSessionsResponse response = invokeApiCall(apiClient -> apiClient.loadRecordSessions());

      view.getTblRecordSessions().clear();
      view.getTblRecordSessions().addAll(response.getRecordSessions());
    }

    private void loadRefreshMockProfiles()
    {
      LoadProfilesResponse response = invokeApiCall(apiClient -> apiClient.loadMockProfiles());

      view.getTblMockProfiles().clear();
      view.getTblMockProfiles().addAll(response.getMockProfiles());
    }

    private void loadRefreshRecords()
    {
      view.getTblRecords().reset();
    }

    private void loadAndTransferRecordSessionsToView()
    {
      List<RecordSession> recordSessions = invokeApiCall(apiClient -> apiClient.loadRecordSessions()).getRecordSessions();

      view.getRecordSessions().clear();
      view.getRecordSessions().addAll(recordSessions);

      RecordSession recordSessionSelected = view.getRecordSessionOvSelected();
      if (recordSessionSelected != null)
      {
        view.setRecordSessionOvSelected(
            recordSessions.stream().filter(rs -> recordSessionSelected.getRecordSessionID().equals(rs.getRecordSessionID())).findAny().orElse(null));
      }
    }

    private void loadRefreshBasedata()
    {
      basedata = invokeApiCall(apiClient -> apiClient.systemLoadBasedata()).getBaseData();

      buildTreeData();

      view.setMockActive(YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getMockActive()));
      view.setMockActiveOnStartup(YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getMockActiveOnStartup()));
      view.setRoutingOnNoMockData(YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getRoutingOnNoMockData()));
      view.setRecord(YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getRecord()));

      view.getTblInterfaces().clear();
      view.getTblInterfaces().addAll(basedata.getInterfaces());

      view.updateTree();
    }

    private void buildTreeData()
    {
      List<TreeData> treeDataList = new ArrayList<>();

      // baseData
      treeDataList.add(new TreeData(Resources.getLabel("basedata"), Resources.SITE_MAIN_BASEDATA, true));

      // interfaces
      buildTreeDataInterfaces(basedata, treeDataList);

      // MockProfiles
      if (Utils.isPositive(basedata.getCountMockProfiles()))
      {
        treeDataList.add(new TreeData(Resources.getLabel("mock_profiles"), Resources.SITE_MAIN_MOCK_PROFILES));
      }

      // Records
      if (Utils.isPositive(basedata.getCountRecords()))
      {
        treeDataList.add(new TreeData(Resources.getLabel("records"), Resources.SITE_MAIN_RECORDS));
      }

      // RecordSessions
      treeDataList.add(new TreeData(Resources.getLabel("record_sessions"), Resources.SITE_MAIN_RECORDSESSIONS));

      view.setTreeDataList(treeDataList);
    }

    private void buildTreeDataInterfaces(BaseData baseData, List<TreeData> treeDataList)
    {
      TreeData treeDataInterfaces = new TreeData(Resources.getLabel("interfaces"), Resources.SITE_MAIN_INTERFACES);
      treeDataList.add(treeDataInterfaces);

      TreeData treeDataInterfacesSoap = new TreeData(Resources.getLabel("soap"), Resources.SITE_MAIN_INTERFACES);
      TreeData treeDataInterfacesRest = new TreeData(Resources.getLabel("rest"), Resources.SITE_MAIN_INTERFACES);
      TreeData treeDataInterfacesCustom = new TreeData(Resources.getLabel("custom"), Resources.SITE_MAIN_INTERFACES);

      for (Interface apiInterface : baseData.getInterfaces())
      {
        TreeData treeDataInterface = new TreeData(apiInterface.getName(), Resources.SITE_INTERFACE, apiInterface);

        if (InterfaceType.SOAP.equals(apiInterface.getType()))
        {
          treeDataInterfacesSoap.getSubEntries().add(treeDataInterface);
        }
        else if (InterfaceType.REST.equals(apiInterface.getType()))
        {
          treeDataInterfacesRest.getSubEntries().add(treeDataInterface);
        }
        else
        {
          treeDataInterfacesCustom.getSubEntries().add(treeDataInterface);
        }
      }

      if (!treeDataInterfacesSoap.getSubEntries().isEmpty())
      {
        treeDataInterfaces.getSubEntries().add(treeDataInterfacesSoap);
      }

      if (!treeDataInterfacesRest.getSubEntries().isEmpty())
      {
        treeDataInterfaces.getSubEntries().add(treeDataInterfacesRest);
      }

      if (!treeDataInterfacesCustom.getSubEntries().isEmpty())
      {
        treeDataInterfaces.getSubEntries().add(treeDataInterfacesCustom);
      }
    }
  }

  @Override
  public void refresh()
  {
    loadRefresh();
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

      // page in main view
      if (Arrays.asList(Resources.SITE_MAIN_BASEDATA, Resources.SITE_MAIN_INTERFACES, Resources.SITE_MAIN_MOCK_PROFILES, Resources.SITE_MAIN_RECORDSESSIONS,
          Resources.SITE_MAIN_RECORDS).contains(viewPage))
      {
        view.setDataPanel(viewPage);

        if (Resources.SITE_MAIN_MOCK_PROFILES.equals(viewPage) || Resources.SITE_MAIN_RECORDSESSIONS.equals(viewPage)
            || Resources.SITE_MAIN_RECORDS.equals(viewPage))
        {
          loadRefresh();
        }
      }
      // redirect
      else
      {
        Map<String, Object> queryParams = new HashMap<>();

        if (Resources.SITE_INTERFACE.equals(viewPage))
        {
          queryParams.put(InterfaceV.VIEW_PARAM_INTERFACE_ID, ((Interface) treeData.getEntity()).getInterfaceId());
        }

        JsfUtils.redirect(viewPage, queryParams);
      }
    }
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
      // no validation
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
      JsfUtils.redirect(Resources.SITE_UPLOAD_MOCKDATA);
    }
  }

  public void baseDataChanged()
  {
    new BaseDataChangedExecution().execute();
  }

  private class BaseDataChangedExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(basedata, "basedata"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "saved_var", Resources.getLabel("basedata"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      YesNoGlobalOrInterfaceIndividuallyType savedMockActive = YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getMockActive());
      YesNoGlobalOrInterfaceIndividuallyType savedMockActiveOnStartup = YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getMockActiveOnStartup());
      YesNoGlobalOrInterfaceIndividuallyType savedRoutingOnNoMockdata = YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getRoutingOnNoMockData());
      YesNoGlobalOrInterfaceIndividuallyType savedRecord = YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getRecord());

      YesNoGlobalOrInterfaceIndividuallyType viewMockActive = view.getMockActive();
      YesNoGlobalOrInterfaceIndividuallyType viewMockActiveOnStartup = view.getMockActiveOnStartup();
      YesNoGlobalOrInterfaceIndividuallyType viewRoutingOnNoMockData = view.getRoutingOnNoMockData();
      YesNoGlobalOrInterfaceIndividuallyType viewRecord = view.getRecord();

      // Check for changes
      if (savedMockActive == viewMockActive && savedMockActiveOnStartup == viewMockActiveOnStartup && savedRoutingOnNoMockdata == viewRoutingOnNoMockData
          && savedRecord == viewRecord)
      {
        leaveWithBusinessException(MessageLevel.INFO, "no_changes");
      }

      // updateComponents enabled / disabled state
      boolean enableRoutingOnNoMockdata = !YesNoGlobalOrInterfaceIndividuallyType.NO.equals(viewMockActive)
                                          || !YesNoGlobalOrInterfaceIndividuallyType.NO.equals(viewMockActiveOnStartup);
      boolean enableRecord = !YesNoGlobalOrInterfaceIndividuallyType.YES.equals(viewMockActive)
                             || !YesNoGlobalOrInterfaceIndividuallyType.YES.equals(viewMockActiveOnStartup)
                             || !YesNoGlobalOrInterfaceIndividuallyType.NO.equals(viewRoutingOnNoMockData);

      view.setRoutingOnNoMockDataDisabled(!enableRoutingOnNoMockdata);
      view.setRecordDisabled(!enableRecord);

      if (!enableRoutingOnNoMockdata)
      {
        viewRoutingOnNoMockData = YesNoGlobalOrInterfaceIndividuallyType.NO;
        view.setRoutingOnNoMockData(viewRoutingOnNoMockData);
      }

      if (!enableRecord)
      {
        viewRecord = YesNoGlobalOrInterfaceIndividuallyType.NO;
        view.setRecord(viewRecord);
      }

      // save
      BaseData basedataSave = new BaseData();
      basedataSave.setMockActive(YesNoGlobalOrInterfaceIndividuallyType.toBoolean(viewMockActive));
      basedataSave.setMockActiveOnStartup(YesNoGlobalOrInterfaceIndividuallyType.toBoolean(viewMockActiveOnStartup));
      basedataSave.setRoutingOnNoMockData(YesNoGlobalOrInterfaceIndividuallyType.toBoolean(viewRoutingOnNoMockData));
      basedataSave.setRecord(YesNoGlobalOrInterfaceIndividuallyType.toBoolean(viewRecord));

      invokeApiCall(apiClient -> apiClient.globalConfigSave(basedataSave));

      ObjectUtils.copyValues(basedataSave, basedata, "interfaces", "countMockSessions", "countRecords");
    }
  }

  public void boot()
  {
    new BootExecution()._execute();
  }

  private class BootExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // no validations
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "system_reseted");
    }

    @Override
    protected void _execute()
    {
      invokeApiCall(apiClient -> apiClient.systemBoot());

      loadRefresh();
    }
  }

  public void editTenantData()
  {
    new EditTenantExecution().execute();
  }

  private class EditTenantExecution extends Execution
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
      JsfUtils.redirect(Resources.SITE_TENANT);
    }
  }

  //--- Interfaces ----------------------

  public void newInterface()
  {
    new NewInterfaceExecution().execute();
  }

  private class NewInterfaceExecution extends Execution
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
      JsfUtils.redirect(Resources.SITE_INTERFACE);
    }
  }

  public void editInterface()
  {
    new EditInterfaceExecution().execute();
  }

  private class EditInterfaceExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new UniqueSelectionValidation(view.getSelectedInterfaces(), Resources.getLabel("interface")));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      Map<String, Object> queryParams = new HashMap<>();
      queryParams.put(InterfaceV.VIEW_PARAM_INTERFACE_ID, Utils.getFirstElementOfCollection(view.getSelectedInterfaces()).getInterfaceId());

      JsfUtils.redirect(Resources.SITE_INTERFACE, queryParams);
    }
  }

  public void deleteInterfaces()
  {
    new DeleteInterfacesExecution().execute();
  }

  private class DeleteInterfacesExecution extends Execution
  {
    private final List<Integer> idsDeleted = new ArrayList<>();

    @Override
    protected void createPreValidations()
    {
      addValidation(new SelectionValidation(view.getSelectedInterfaces(), "interface"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel(idsDeleted.size() > 1 ? "interfaces" : "interface"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      List<Interface> interfacesToDelete = new ArrayList<>(view.getSelectedInterfaces());

      for (Interface apiInterface : interfacesToDelete)
      {
        invokeApiCall(apiClient -> apiClient.deleteInterface(apiInterface.getInterfaceId()));

        idsDeleted.add(apiInterface.getInterfaceId());

        view.getTblInterfaces().remove(apiInterface);
        view.getSelectedInterfaces().remove(apiInterface);

        // Delete from tree
        for (TreeData tdIfcGroup : getTreeDataInterfaceGroups())
        {
          if (tdIfcGroup.getSubEntries().removeIf(td -> apiInterface.equals(td.getEntity())))
          {
            if (tdIfcGroup.getSubEntries().isEmpty())
            {
              getTreeDataInterfaces().getSubEntries().remove(tdIfcGroup);
            }

            break;
          }
        }
      }

      updateComponentsInterfaceOverview();

      if (!idsDeleted.isEmpty())
      {
        view.updateTree();
      }
    }
  }

  private TreeData getTreeDataInterfaces()
  {
    return view.getTreeDataList().stream().filter(td -> Resources.SITE_MAIN_INTERFACES.equals(td.getViewPage())).findAny().orElse(null);
  }

  private List<TreeData> getTreeDataInterfaceGroups()
  {
    TreeData treeDataInterfaces = getTreeDataInterfaces();

    List<TreeData> groups = new ArrayList<>();

    Utils.addToCollectionIfNotNull(groups, getTreeDataInterfaceGroup(treeDataInterfaces, "soap"));
    Utils.addToCollectionIfNotNull(groups, getTreeDataInterfaceGroup(treeDataInterfaces, "rest"));
    Utils.addToCollectionIfNotNull(groups, getTreeDataInterfaceGroup(treeDataInterfaces, "custom"));

    return groups;
  }

  private TreeData getTreeDataInterfaceGroup(TreeData treeDataInterfaces, String label)
  {
    return treeDataInterfaces.getSubEntries().stream().filter(td -> Resources.getLabel(label).equals(td.getText())).findAny().orElse(null);
  }

  public void handleInterfacesSelection()
  {
    updateComponentsInterfaceOverview();
  }

  private void updateComponentsInterfaceOverview()
  {
    int cntSelected = view.getSelectedInterfaces().size();

    view.setDeleteInterfaceDisabled(cntSelected == 0);
    view.setEditInterfaceDisabled(cntSelected != 1);
  }

  // --- End Interfaces ----------------------

  //--- MockProfiles --------------------------

  public void handleMockProfilesSelection()
  {
    updateComponentsMockProfiles();
  }

  private void updateComponentsMockProfiles()
  {
    int cntSelected = view.getSelectedMockProfiles().size();

    view.setEditMockProfileDisabled(cntSelected != 1);
    view.setDeleteMockProfileDisabled(cntSelected == 0);
  }

  public void newMockProfile()
  {
    new NewMockProfileExecution().execute();
  }

  private class NewMockProfileExecution extends Execution
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
      JsfUtils.redirect(Resources.SITE_MOCK_PROFILE);
    }
  }

  public void deleteMockProfiles()
  {
    new DeleteMockProfilesExecution().execute();
  }

  private class DeleteMockProfilesExecution extends Execution
  {
    private final List<Integer> idsDeleted = new ArrayList<>();

    @Override
    protected void createPreValidations()
    {
      addValidation(new SelectionValidation(view.getSelectedMockProfiles(), "mock_profile"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel(idsDeleted.size() > 1 ? "mock_profiles" : "mock_profile"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      List<MockProfile> mockProfilesToDelete = new ArrayList<>(view.getSelectedMockProfiles());

      for (MockProfile apiMockProfile : mockProfilesToDelete)
      {
        invokeApiCall(apiClient -> apiClient.deleteMockProfile(apiMockProfile.getMockProfileID()));

        idsDeleted.add(apiMockProfile.getMockProfileID());

        view.getTblMockProfiles().remove(apiMockProfile);
        view.getSelectedMockProfiles().remove(apiMockProfile);
      }

      updateComponentsMockProfiles();
    }
  }

  public void editMockProfile()
  {
    new EditMockProfileExecution().execute();
  }

  private class EditMockProfileExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new UniqueSelectionValidation(view.getSelectedMockProfiles(), Resources.getLabel("mock_profile")));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      Map<String, Object> queryParams = new HashMap<>();
      queryParams.put(MockProfileV.VIEW_PARAM_MOCK_PROFILE_ID, Utils.getFirstElementOfCollection(view.getSelectedMockProfiles()).getMockProfileID());

      JsfUtils.redirect(Resources.SITE_MOCK_PROFILE, queryParams);
    }
  }

  //--- End MockProfiles ----------------------

  // --- Records -------------------------------

  public void showRecord()
  {
    new ShowRecordExecution().execute();
  }

  private class ShowRecordExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new UniqueSelectionValidation(view.getSelectedRecords(), "record"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      apiRecordSelected = view.getSelectedRecords().get(0);

      // load data
      Record apiRecordLoaded = invokeApiCall(apiClient -> apiClient.loadRecord(apiRecordSelected.getRecordId())).getRecord();

      getView().setDataPanel(Resources.SITE_MAIN_RECORD);

      // Transfer Model->View
      view.setRecRecordSession(apiRecordLoaded.getRecordSession() == null ? null : apiRecordLoaded.getRecordSession().toString());
      view.setRecInterface(apiRecordLoaded.getInterfaceName());
      view.setRecMethod(apiRecordLoaded.getMethodName());
      view.setRecCreated(apiRecordLoaded.getCreatedAsString());
      view.setTblRecordPathParams(new ArrayList<>(apiRecordLoaded.getPathParams()));
      view.setTblRecordUrlArguments(new ArrayList<>(apiRecordLoaded.getUrlArguments()));
      view.setRecRequest(apiRecordLoaded.getRequestData());
      view.setRecHttpResponseCode(apiRecordLoaded.getHttpReturnCode());
      view.setRecResponse(apiRecordLoaded.getResponse());

      updateComponentsRecord();
    }
  }

  private void updateComponentsRecord()
  {
    view.setRecPathParamsRendered(!view.getTblRecordPathParams().isEmpty());
    view.setRecUrlArgumentsRendered(!view.getTblRecordUrlArguments().isEmpty());
    view.setRecHttpReturnCodeRendered(view.getRecHttpResponseCode() != null);
  }

  public void deleteRecords()
  {
    new DeleteRecordsExecution().execute();
  }

  private class DeleteRecordsExecution extends Execution
  {
    private final List<Integer> idsDeleted = new ArrayList<>();

    @Override
    protected void createPreValidations()
    {
      addValidation(new SelectionValidation(view.getSelectedRecords(), "record"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel(idsDeleted.size() > 1 ? "records" : "record"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      List<Record> recordsToDelete = new ArrayList<>(view.getSelectedRecords());

      for (Record apiRecord : recordsToDelete)
      {
        invokeApiCall(apiClient -> apiClient.deleteRecord(apiRecord.getRecordId()));

        idsDeleted.add(apiRecord.getRecordId());

        removeRecordFromView(apiRecord);
      }

      updateComponentsRecords();
    }
  }

  public void deleteAllRecords()
  {
    new DeleteAllRecordsExecution().execute();
  }

  private class DeleteAllRecordsExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // no validation, option only avalilable if entries loaded
      // in worst case we fire a delete for no entries resulting in doing nothing.
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", "records");
    }

    @Override
    protected void _execute()
      throws Exception
    {
      invokeApiCall(apiClient -> apiClient.deleteRecords());

      view.getTblRecords().reset();
      view.getSelectedRecords().clear();

      updateComponentsRecords();
    }
  }

  private void removeRecordFromView(Record apiRecord)
  {
    view.getTblRecords().removeRecord(apiRecord);
    view.getSelectedRecords().remove(apiRecord);
  }

  public void handleRecordsSelection()
  {
    updateComponentsRecords();
  }

  public void updateComponentsRecords()
  {
    int cntSelected = view.getSelectedRecords().size();

    view.setDownloadRecordsDisabled(cntSelected == 0);
    view.setDeleteRecordsDisabled(cntSelected == 0);
    view.setShowRecordsDisabled(cntSelected != 1);
    view.setDeleteAllRecordsDisabled(view.getTblRecords().getRowCount() == 0);
  }

  public StreamedContent getFileRecords()
  {
    ValueWrapper<StreamedContent> streamedContent = new ValueWrapper<>(null);

    new GetFileRecordsExecution(streamedContent).execute();

    return streamedContent.getValue();
  }

  private class GetFileRecordsExecution extends Execution
  {
    private ValueWrapper<StreamedContent> streamedContent;

    private GetFileRecordsExecution(ValueWrapper<StreamedContent> streamedContent)
    {
      this.streamedContent = streamedContent;
    }

    @Override
    protected void createPreValidations()
    {
      addValidation(new SelectionValidation(view.getSelectedRecords(), "record"));
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
      // load request/response data for selected record(s)
      for (Record apiRecord : view.getSelectedRecords())
      {
        if (apiRecord.getRequestData() == null)
        {
          Record apiRecordLoaded = invokeApiCall(apiClient -> apiClient.loadRecord(apiRecord.getRecordId())).getRecord();

          apiRecord.getPathParams().clear();
          apiRecord.getPathParams().addAll(apiRecordLoaded.getPathParams());

          apiRecord.getUrlArguments().clear();
          apiRecord.getUrlArguments().addAll(apiRecordLoaded.getUrlArguments());

          apiRecord.setRequestData(apiRecordLoaded.getRequestData());
          apiRecord.setHttpReturnCode(apiRecordLoaded.getHttpReturnCode());
          apiRecord.setResponse(apiRecordLoaded.getResponse());
        }
      }

      // single record selected
      if (view.getSelectedRecords().size() == 1)
      {
        Record apiRecord = view.getSelectedRecords().get(0);

        String contentType = getContentTypeRecordDownload(apiRecord);

        streamedContent.setValue(new DefaultStreamedContent(new ByteArrayInputStream(getFileContentRecordDownload(apiRecord).getBytes("UTF-8")), contentType,
            getFilenameRecordDownload(apiRecord, contentType)));
      }
      // mapRecordFiles.size() > 1 => ZIP
      else
      {
        ByteArrayOutputStream baos = null;
        ZipOutputStream zos = null;

        Map<String, Integer> mapFilenameCount = new HashMap<>();

        try
        {
          baos = new ByteArrayOutputStream();
          zos = new ZipOutputStream(baos);

          for (Record apiRecord : view.getSelectedRecords())
          {
            String filenameForZipping = getFilenameRecordDownload(apiRecord, getContentTypeRecordDownload(apiRecord));

            Integer countFilename = mapFilenameCount.get(filenameForZipping);

            if (countFilename == null)
            {
              mapFilenameCount.put(filenameForZipping, 1);
            }
            else
            {
              mapFilenameCount.put(filenameForZipping, countFilename + 1);

              filenameForZipping = new StringBuilder(filenameForZipping).insert(filenameForZipping.lastIndexOf("."), "_" + countFilename).toString();
            }

            ZipEntry entry = new ZipEntry(filenameForZipping);

            zos.putNextEntry(entry);
            zos.write(getFileContentRecordDownload(apiRecord).getBytes("UTF-8"));
            zos.closeEntry();
          }

          zos.close();

          byte[] bytes = baos.toByteArray();

          streamedContent.setValue(new DefaultStreamedContent(new ByteArrayInputStream(bytes), null,
              "records_" + Utils.localDateTimeToString(LocalDateTime.now(), "dd_MM_yyyy_HH_mm_ss") + ".zip"));
        }
        catch (Exception ex)
        {
          throw new IllegalStateException(ex);
        }
        finally
        {
          Utils.safeClose(zos, baos);
        }
      }
    }

    private String getFilenameRecordDownload(Record apiRecord, String filetype)
    {
      StringBuilder bui = new StringBuilder(35);

      bui.append(apiRecord.getInterfaceName()).append("_").append(apiRecord.getMethodName()).append("_")
          .append(apiRecord.getCreatedAsString().replace(".", "_").replace(" ", "_").replace(":", "_"));

      if ("zip".equals(filetype))
      {
        bui.append(".zip");
      }
      else if (filetype.contains("xml"))
      {
        bui.append(".xml");
      }
      else
      {
        bui.append(".txt");
      }

      return bui.toString();
    }

    private String getContentTypeRecordDownload(Record apiRecord)
    {
      InterfaceType ifcType = apiRecord.getInterfaceMethod().getMockInterface().getType();

      if (Arrays.asList(InterfaceType.SOAP, InterfaceType.CUSTOM_XML).contains(ifcType))
      {
        return "text/xml";
      }
      else if (Arrays.asList(InterfaceType.CUSTOM_JSON).contains(ifcType))
      {
        return "json";
      }
      else
      {
        return "text/plain";
      }
    }

    private String getFileContentRecordDownload(Record apiRecord)
    {
      StringBuilder bui = new StringBuilder(apiRecord.getRequestData().length() + apiRecord.getResponse().length() + 30);

      Collection<PathParam> pathParams = apiRecord.getPathParams();
      if (!pathParams.isEmpty())
      {
        bui.append(MockData.PREFIX_MOCKDATA_IN_EXPORT_REQUEST_PATH_PARAMS).append("\n");

        for (PathParam pathParam : pathParams)
        {
          bui.append(pathParam.getKey()).append(":").append(pathParam.getValue()).append("\n");
        }
      }

      Collection<UrlArgument> urlArguments = apiRecord.getUrlArguments();
      if (!urlArguments.isEmpty())
      {
        bui.append(MockData.PREFIX_MOCKDATA_IN_EXPORT_REQUEST_URL_ARGUMENTS).append("\n");

        for (UrlArgument urlArg : urlArguments)
        {
          bui.append(urlArg.getKey()).append(":").append(urlArg.getValue()).append("\n");
        }
      }

      bui.append(MockData.PREFIX_MOCKDATA_IN_EXPORT_REQUEST).append("\n");
      bui.append(apiRecord.getRequestData());
      bui.append("\n");

      Integer httpReturnCode = apiRecord.getHttpReturnCode();
      if (httpReturnCode != null)
      {
        bui.append(MockData.PREFIX_MOCKDATA_IN_EXPORT_RESPONSE_HTTP_CODE).append("\n");
        bui.append(httpReturnCode);
        bui.append("\n");
      }

      bui.append(MockData.PREFIX_MOCKDATA_IN_EXPORT_RESPONSE).append("\n");
      bui.append(apiRecord.getResponse());

      return bui.toString();
    }
  }

  public void useRecordsAsMockdata()
  {
    new UseRecordsAsMockdataExecution().execute();
  }

  private class UseRecordsAsMockdataExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new SelectionValidation(view.getSelectedRecords(), "record"));
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
      StringBuilder bui = new StringBuilder();

      // load request/response data for selected record(s)
      for (Record apiRecord : view.getSelectedRecords())
      {
        if (bui.length() > 0)
        {
          bui.append(";");
        }

        bui.append(apiRecord.getRecordId());
      }

      Map<String, Object> queryParams = new HashMap<>();
      queryParams.put(RecordAsMockdataV.VIEW_PARAM_RECORDS_IDS, bui.toString());

      JsfUtils.redirect(Resources.SITE_RECORD_AS_MOCKDATA, queryParams);
    }
  }

  public void handleRecordOverviewSessionSelected()
  {
    view.getTblRecords().reset();

    view.getTblRecords().setFilterSession(view.getRecordSessionOvSelected());
  }

  // --- End Records ---------------------------

  //--- Record --------------------------------- 

  public void deleteRecord()
  {
    new DeleteRecordExecution().execute();
  }

  private class DeleteRecordExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(apiRecordSelected, "record").addSubValidation(new NotNull(() -> apiRecordSelected.getRecordId(), "id")) //
      );
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel("record"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      invokeApiCall(apiClient -> apiClient.deleteRecord(apiRecordSelected.getRecordId()));

      removeRecordFromView(apiRecordSelected);

      apiRecordSelected = null;

      getView().setDataPanel(Resources.SITE_MAIN_RECORDS);

      updateComponentsRecords();
    }
  }

  // --- End Record ----------------------------

  //--- RecordSessions -------------------------------

  public void handleRecordSessionsSelection()
  {
    updateComponentsRecordSessions();
  }

  private void updateComponentsRecordSessions()
  {
    int cntSelected = view.getSelectedRecordSessions().size();

    view.setDeleteRecordSessionDisabled(cntSelected == 0);
  }

  public void newRecordSession()
  {
    new NewRecordSessionExecution().execute();
  }

  private class NewRecordSessionExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // no validation
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "saved_var", Resources.getLabel("record_session"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      RecordSession apiRecordSession = invokeApiCall(apiClient -> apiClient.createRecordSession()).getRecordSession();

      view.getTblRecordSessions().add(apiRecordSession);
    }
  }

  public void deleteRecordSessions()
  {
    new DeleteRecordSessionsExecution().execute();
  }

  private class DeleteRecordSessionsExecution extends Execution
  {
    private final List<Integer> idsDeleted = new ArrayList<>();

    @Override
    protected void createPreValidations()
    {
      addValidation(new SelectionValidation(view.getSelectedRecordSessions(), "record_session"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel(idsDeleted.size() > 1 ? "record_sessions" : "record_session"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      List<RecordSession> recordSessionsToDelete = new ArrayList<>(view.getSelectedRecordSessions());

      for (RecordSession apiRecordSession : recordSessionsToDelete)
      {
        invokeApiCall(apiClient -> apiClient.deleteRecordSession(apiRecordSession.getRecordSessionID()));

        idsDeleted.add(apiRecordSession.getRecordSessionID());

        view.getTblRecordSessions().remove(apiRecordSession);
        view.getSelectedRecordSessions().remove(apiRecordSession);
      }

      updateComponentsRecordSessions();
    }
  }

  //--- End RecordSessions -------------------------------
}
