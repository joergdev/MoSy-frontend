package de.joergdev.mosy.frontend.view.controller;

import java.util.Arrays;
import java.util.List;
import org.primefaces.PrimeFaces;
import de.joergdev.mosy.api.model.MockData;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.api.model.Record;
import de.joergdev.mosy.api.response.record.LoadResponse;
import de.joergdev.mosy.frontend.Message;
import de.joergdev.mosy.frontend.MessageLevel;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.frontend.utils.JsfUtils;
import de.joergdev.mosy.frontend.validation.SelectionValidation;
import de.joergdev.mosy.frontend.validation.StringNotEmpty;
import de.joergdev.mosy.frontend.view.RecordAsMockdataV;
import de.joergdev.mosy.frontend.view.controller.core.AbstractViewController;
import de.joergdev.mosy.shared.Utils;

public class RecordAsMockdataVC extends AbstractViewController<RecordAsMockdataV>
{
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
      JsfUtils.redirect(view.getSource());
    }
  }

  public void loadRefresh()
  {
    new LoadRefreshExecution().execute();
  }

  private class LoadRefreshExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new StringNotEmpty(view.getRecordIds(), "records"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "loaded_var", Resources.getLabel("data"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      List<String> recordIDs = Arrays.asList(view.getRecordIds().split(";"));

      loadRefreshRecords(recordIDs);
    }

    private void loadRefreshRecords(List<String> recordIDs)
    {
      view.getTblRecords().clear();

      for (String recordID : recordIDs)
      {
        LoadResponse response = invokeApiCall(apiClient -> apiClient.loadRecord(Integer.valueOf(recordID)));

        view.getTblRecords().add(response.getRecord());
      }
    }
  }

  @Override
  public void refresh()
  {
    loadRefresh();
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
      addValidation(new SelectionValidation(view.getTblRecords(), "record"));
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
      StringBuilder buiTitle = new StringBuilder();
      buiTitle.append(Utils.nvl(view.getTitlePrefix(), ""));

      if (buiTitle.length() > 0)
      {
        buiTitle.append("_");
      }

      buiTitle.append(System.currentTimeMillis());

      int countRecord = 1;

      for (Record apiRecord : view.getTblRecords())
      {
        MockData apiMockData = new MockData();
        apiMockData.getMockProfiles().addAll(view.getTblMockProfiles());
        apiMockData.setInterfaceMethod(apiRecord.getInterfaceMethod());
        apiMockData.setTitle(buiTitle.toString() + "_" + countRecord);
        apiMockData.setActive(true);
        apiMockData.getPathParams().addAll(apiRecord.getPathParams());
        apiMockData.getUrlArguments().addAll(apiRecord.getUrlArguments());
        apiMockData.setRequest(apiRecord.getRequestData());
        apiMockData.setHttpReturnCode(apiRecord.getHttpReturnCode());
        apiMockData.setResponse(apiRecord.getResponse());

        invokeApiCall(apiclient -> apiclient.saveMockData(apiMockData));

        countRecord++;
      }
    }
  }

  //--- MockProfiles ---

  public void handleMockProfilesSelection()
  {
    updateComponentsMockDataMockProfiles();
  }

  public void addMockProfile()
  {
    List<MockProfile> mockProfiles = invokeApiCall(apiClient -> apiClient.loadMockProfiles())
        .getMockProfiles();

    mockProfiles.removeAll(view.getTblMockProfiles());

    view.setMockProfiles(mockProfiles);

    PrimeFaces.current().executeScript("PF('mockProfileSelectionDlg').show();");
  }

  public void deleteMockProfiles()
  {
    new DeleteMockProfilesExecution().execute();
  }

  private class DeleteMockProfilesExecution extends Execution
  {
    private List<MockProfile> selectedMockProfiles;
    private int countSelected = 0;

    @Override
    protected void createPreValidations()
    {
      selectedMockProfiles = view.getSelectedMockProfiles();
      countSelected = selectedMockProfiles.size();

      addValidation(new SelectionValidation(selectedMockProfiles, "mock_profile"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel(countSelected > 1
          ? "mock_profiles"
          : "mock_profile"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      for (MockProfile mp2del : selectedMockProfiles)
      {
        view.getTblMockProfiles().remove(mp2del);
      }
    }
  }

  private void updateComponentsMockDataMockProfiles()
  {
    view.setDeleteMockProfileDisabled(view.getSelectedMockProfiles().isEmpty());
  }

  public void addSelectedMockProfile()
  {
    MockProfile mpSelected = view.getMdMockProfile();
    if (mpSelected != null)
    {
      view.getTblMockProfiles().add(mpSelected);
    }

    PrimeFaces.current().executeScript("PF('mockProfileSelectionDlg').hide();");
  }

  //--- End MockProfiles ---
}