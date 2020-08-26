package com.github.joergdev.mosy.frontend.view.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import com.github.joergdev.mosy.api.model.BaseData;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.InterfaceType;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.api.model.Record;
import com.github.joergdev.mosy.api.response.mocksession.LoadSessionsResponse;
import com.github.joergdev.mosy.frontend.Message;
import com.github.joergdev.mosy.frontend.MessageLevel;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.model.YesNoGlobalOrInterfaceIndividuallyType;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.utils.TreeData;
import com.github.joergdev.mosy.frontend.validation.NotNull;
import com.github.joergdev.mosy.frontend.validation.SelectionValidation;
import com.github.joergdev.mosy.frontend.validation.UniqueSelectionValidation;
import com.github.joergdev.mosy.frontend.view.InterfaceV;
import com.github.joergdev.mosy.frontend.view.MainV;
import com.github.joergdev.mosy.frontend.view.RecordAsMockdataV;
import com.github.joergdev.mosy.frontend.view.controller.core.AbstractViewController;
import com.github.joergdev.mosy.shared.ObjectUtils;
import com.github.joergdev.mosy.shared.Utils;
import com.github.joergdev.mosy.shared.ValueWrapper;

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

      if (Utils.isEmpty(dataPanel)
          || Arrays.asList(Resources.SITE_MAIN_BASEDATA, Resources.SITE_MAIN_INTERFACES).contains(dataPanel))
      {
        loadedMessageDetail = Resources.getLabel("basedata") + " / " + Resources.getLabel("interfaces");

        loadRefreshBasedata();
      }
      else if (Resources.SITE_MAIN_RECORDS.equals(dataPanel))
      {
        loadedMessageDetail = Resources.getLabel("records");

        loadRefreshRecords();
      }
      else if (Resources.SITE_MAIN_MOCK_SESSIONS.equals(dataPanel))
      {
        loadedMessageDetail = Resources.getLabel("mock_sessions");

        loadRefreshMockSessions();
      }
    }

    private void loadRefreshMockSessions()
    {
      LoadSessionsResponse response = invokeApiCall(apiClient -> apiClient.loadMocksessions());

      view.getTblMockSessions().clear();
      view.getTblMockSessions().addAll(response.getMockSessions());
    }

    private void loadRefreshRecords()
    {
      view.getTblRecords().reset();
    }

    private void loadRefreshBasedata()
    {
      basedata = invokeApiCall(apiClient -> apiClient.systemLoadBasedata()).getBaseData();

      buildTreeData();

      view.setMockActive(YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getMockActive()));
      view.setMockActiveOnStartup(
          YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getMockActiveOnStartup()));
      view.setRoutingOnNoMockData(
          YesNoGlobalOrInterfaceIndividuallyType.fromBoolean(basedata.getRoutingOnNoMockData()));
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

      // MockSessions
      if (Utils.isPositive(basedata.getCountMockSessions()))
      {
        treeDataList
            .add(new TreeData(Resources.getLabel("mock_sessions"), Resources.SITE_MAIN_MOCK_SESSIONS));
      }

      // Records
      if (Utils.isPositive(basedata.getCountRecords()))
      {
        treeDataList.add(new TreeData(Resources.getLabel("records"), Resources.SITE_MAIN_RECORDS));
      }

      view.setTreeDataList(treeDataList);
    }

    private void buildTreeDataInterfaces(BaseData baseData, List<TreeData> treeDataList)
    {
      TreeData treeDataInterfaces = new TreeData(Resources.getLabel("interfaces"),
          Resources.SITE_MAIN_INTERFACES);
      treeDataList.add(treeDataInterfaces);

      TreeData treeDataInterfacesSoap = new TreeData(Resources.getLabel("soap"),
          Resources.SITE_MAIN_INTERFACES);
      TreeData treeDataInterfacesRest = new TreeData(Resources.getLabel("rest"),
          Resources.SITE_MAIN_INTERFACES);
      TreeData treeDataInterfacesCustom = new TreeData(Resources.getLabel("custom"),
          Resources.SITE_MAIN_INTERFACES);

      for (Interface apiInterface : baseData.getInterfaces())
      {
        TreeData treeDataInterface = new TreeData(apiInterface.getName(), Resources.SITE_INTERFACE,
            apiInterface);

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
      if (Arrays.asList(Resources.SITE_MAIN_BASEDATA, Resources.SITE_MAIN_INTERFACES,
          Resources.SITE_MAIN_MOCK_SESSIONS, Resources.SITE_MAIN_RECORDS).contains(viewPage))
      {
        view.setDataPanel(viewPage);

        if ((Resources.SITE_MAIN_MOCK_SESSIONS.equals(viewPage))
            || (Resources.SITE_MAIN_RECORDS.equals(viewPage)))
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
          queryParams.put(InterfaceV.VIEW_PARAM_INTERFACE_ID,
              ((Interface) treeData.getEntity()).getInterfaceId());
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
      YesNoGlobalOrInterfaceIndividuallyType savedMockActive = YesNoGlobalOrInterfaceIndividuallyType
          .fromBoolean(basedata.getMockActive());
      YesNoGlobalOrInterfaceIndividuallyType savedMockActiveOnStartup = YesNoGlobalOrInterfaceIndividuallyType
          .fromBoolean(basedata.getMockActiveOnStartup());
      YesNoGlobalOrInterfaceIndividuallyType savedRoutingOnNoMockdata = YesNoGlobalOrInterfaceIndividuallyType
          .fromBoolean(basedata.getRoutingOnNoMockData());
      YesNoGlobalOrInterfaceIndividuallyType savedRecord = YesNoGlobalOrInterfaceIndividuallyType
          .fromBoolean(basedata.getRecord());

      YesNoGlobalOrInterfaceIndividuallyType viewMockActive = view.getMockActive();
      YesNoGlobalOrInterfaceIndividuallyType viewMockActiveOnStartup = view.getMockActiveOnStartup();
      YesNoGlobalOrInterfaceIndividuallyType viewRoutingOnNoMockData = view.getRoutingOnNoMockData();
      YesNoGlobalOrInterfaceIndividuallyType viewRecord = view.getRecord();

      // Check for changes
      if (savedMockActive == viewMockActive && savedMockActiveOnStartup == viewMockActiveOnStartup
          && savedRoutingOnNoMockdata == viewRoutingOnNoMockData && savedRecord == viewRecord)
      {
        leaveWithBusinessException(MessageLevel.INFO, "no_changes");
      }

      // updateComponents enabled / disabled state
      boolean enableRoutingOnNoMockdata = !YesNoGlobalOrInterfaceIndividuallyType.NO.equals(viewMockActive)
                                          || !YesNoGlobalOrInterfaceIndividuallyType.NO
                                              .equals(viewMockActiveOnStartup);
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
      basedataSave
          .setMockActiveOnStartup(YesNoGlobalOrInterfaceIndividuallyType.toBoolean(viewMockActiveOnStartup));
      basedataSave
          .setRoutingOnNoMockData(YesNoGlobalOrInterfaceIndividuallyType.toBoolean(viewRoutingOnNoMockData));
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
      addValidation(
          new UniqueSelectionValidation(view.getSelectedInterfaces(), Resources.getLabel("interface")));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      Map<String, Object> queryParams = new HashMap<>();
      queryParams.put(InterfaceV.VIEW_PARAM_INTERFACE_ID,
          Utils.getFirstElementOfCollection(view.getSelectedInterfaces()).getInterfaceId());

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
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel(idsDeleted.size() > 1
          ? "interfaces"
          : "interface"));
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
    return view.getTreeDataList().stream()
        .filter(td -> Resources.SITE_MAIN_INTERFACES.equals(td.getViewPage())).findAny().orElse(null);
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
    return treeDataInterfaces.getSubEntries().stream()
        .filter(td -> Resources.getLabel(label).equals(td.getText())).findAny().orElse(null);
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

  //--- MockSessions --------------------------

  public void handleMockSessionsSelection()
  {
    updateComponentsMockSessions();
  }

  private void updateComponentsMockSessions()
  {
    int cntSelected = view.getSelectedMockSessions().size();

    view.setDeleteMockSessionDisabled(cntSelected == 0);
  }

  public void newMockSession()
  {
    new NewMockSessionExecution().execute();
  }

  private class NewMockSessionExecution extends Execution
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
      MockSession apiMockSession = invokeApiCall(apiClient -> apiClient.createMocksession()).getMockSession();

      view.getTblMockSessions().add(apiMockSession);
    }
  }

  public void deleteMockSessions()
  {
    new DeleteMockSessionsExecution().execute();
  }

  private class DeleteMockSessionsExecution extends Execution
  {
    private final List<Integer> idsDeleted = new ArrayList<>();

    @Override
    protected void createPreValidations()
    {
      addValidation(new SelectionValidation(view.getSelectedMockSessions(), "mock_session"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel(idsDeleted.size() > 1
          ? "mock_sessions"
          : "mock_session"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      List<MockSession> mockSessionsToDelete = new ArrayList<>(view.getSelectedMockSessions());

      for (MockSession apiMockSession : mockSessionsToDelete)
      {
        invokeApiCall(apiClient -> apiClient.deleteMocksession(apiMockSession.getMockSessionID()));

        idsDeleted.add(apiMockSession.getMockSessionID());

        view.getTblMockSessions().remove(apiMockSession);
        view.getSelectedMockSessions().remove(apiMockSession);
      }

      updateComponentsMockSessions();
    }
  }

  //--- End MockSessions ----------------------

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
      Record apiRecordLoaded = invokeApiCall(
          apiClient -> apiClient.loadRecord(apiRecordSelected.getRecordId())).getRecord();

      getView().setDataPanel(Resources.SITE_MAIN_RECORD);

      // Transfer Model->View
      view.setRecInterface(apiRecordLoaded.getInterfaceName());
      view.setRecMethod(apiRecordLoaded.getMethodName());
      view.setRecCreated(apiRecordLoaded.getCreatedAsString());
      view.setRecRequest(apiRecordLoaded.getRequestData());
      view.setRecResponse(apiRecordLoaded.getResponse());
    }
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
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel(idsDeleted.size() > 1
          ? "records"
          : "record"));
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

  private void removeRecordFromView(Record apiRecord)
  {
    view.getTblRecords().removeRecord(apiRecord);
    view.getSelectedRecords().remove(apiRecord);
  }

  public void handleRecordsSelection()
  {
    updateComponentsRecords();
  }

  private void updateComponentsRecords()
  {
    int cntSelected = view.getSelectedRecords().size();

    view.setDownloadRecordsDisabled(cntSelected == 0);
    view.setDeleteRecordsDisabled(cntSelected == 0);
    view.setShowRecordsDisabled(cntSelected != 1);
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
          Record apiRecordLoaded = invokeApiCall(apiClient -> apiClient.loadRecord(apiRecord.getRecordId()))
              .getRecord();

          apiRecord.setRequestData(apiRecordLoaded.getRequestData());
          apiRecord.setResponse(apiRecordLoaded.getResponse());
        }
      }

      // single record selected
      if (view.getSelectedRecords().size() == 1)
      {
        Record apiRecord = view.getSelectedRecords().get(0);

        String contentType = getContentTypeRecordDownload(apiRecord);

        streamedContent.setValue(new DefaultStreamedContent(
            new ByteArrayInputStream(getFileContentRecordDownload(apiRecord).getBytes("UTF-8")), contentType,
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
            String filenameForZipping = getFilenameRecordDownload(apiRecord,
                getContentTypeRecordDownload(apiRecord));

            Integer countFilename = mapFilenameCount.get(filenameForZipping);

            if (countFilename == null)
            {
              mapFilenameCount.put(filenameForZipping, 1);
            }
            else
            {
              mapFilenameCount.put(filenameForZipping, countFilename + 1);

              filenameForZipping = new StringBuilder(filenameForZipping)
                  .insert(filenameForZipping.lastIndexOf("."), "_" + countFilename).toString();
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
      StringBuilder bui = new StringBuilder(
          apiRecord.getRequestData().length() + apiRecord.getResponse().length() + 30);

      bui.append(Resources.PREFIX_MOCKDATA_IN_EXPORT_REQUEST).append("\n");
      bui.append(apiRecord.getRequestData());
      bui.append("\n");

      bui.append(Resources.PREFIX_MOCKDATA_IN_EXPORT_RESPONSE).append("\n");
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
      addValidation(new NotNull(apiRecordSelected, "record")
          .addSubValidation(new NotNull(() -> apiRecordSelected.getRecordId(), "id")) //
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
}