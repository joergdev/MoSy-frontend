package com.github.joergdev.mosy.frontend.view.controller;

import java.util.Arrays;
import java.util.List;
import com.github.joergdev.mosy.api.model.MockData;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.api.model.Record;
import com.github.joergdev.mosy.api.response.record.LoadResponse;
import com.github.joergdev.mosy.frontend.Message;
import com.github.joergdev.mosy.frontend.MessageLevel;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.validation.SelectionValidation;
import com.github.joergdev.mosy.frontend.validation.StringNotEmpty;
import com.github.joergdev.mosy.frontend.view.RecordAsMockdataV;
import com.github.joergdev.mosy.frontend.view.controller.core.AbstractViewController;
import com.github.joergdev.mosy.shared.Utils;

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
      loadAndTransferMockSessionsToView();

      List<String> recordIDs = Arrays.asList(view.getRecordIds().split(";"));

      loadRefreshRecords(recordIDs);
    }

    private void loadAndTransferMockSessionsToView()
    {
      List<MockSession> mockSessions = invokeApiCall(apiClient -> apiClient.loadMocksessions())
          .getMockSessions();

      view.getMockSessions().clear();
      view.getMockSessions().addAll(mockSessions);

      MockSession mockSessionSelected = view.getMockSessionSelected();
      if (mockSessionSelected != null)
      {
        view.setMockSessionSelected(mockSessions.stream()
            .filter(ms -> mockSessionSelected.getMockSessionID().equals(ms.getMockSessionID())).findAny()
            .orElse(null));
      }
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
      MockSession apiMockSession = view.getMockSessionSelected();

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
        apiMockData.setMockSession(apiMockSession);
        apiMockData.setInterfaceMethod(apiRecord.getInterfaceMethod());
        apiMockData.setTitle(buiTitle.toString() + "_" + countRecord);
        apiMockData.setActive(true);
        apiMockData.setRequest(apiRecord.getRequestData());
        apiMockData.setResponse(apiRecord.getResponse());

        invokeApiCall(apiclient -> apiclient.saveMockData(apiMockData));

        countRecord++;
      }
    }
  }
}