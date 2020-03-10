package com.github.joergdev.mosy.frontend.view.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.api.model.MockData;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.frontend.Message;
import com.github.joergdev.mosy.frontend.MessageLevel;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.validation.NotNull;
import com.github.joergdev.mosy.frontend.validation.NumberValidator;
import com.github.joergdev.mosy.frontend.validation.SelectionValidation;
import com.github.joergdev.mosy.frontend.view.UploadMockdataV;
import com.github.joergdev.mosy.frontend.view.controller.core.AbstractViewController;
import com.github.joergdev.mosy.frontend.view.controller.core.BusinessLevelException;
import com.github.joergdev.mosy.shared.Utils;

public class UploadMockdataVC extends AbstractViewController<UploadMockdataV>
{
  @Override
  public void refresh()
  {
    loadRefresh();
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
      addValidation(new NumberValidator(view.getInterfaceID(), "interface", true, 1, null));
      addValidation(new NumberValidator(view.getMethodID(), "method", true, 1, null));

      if (!Utils.isEmpty(view.getMethodID()))
      {
        addValidation(new NotNull(view.getInterfaceID(), "interface"));
      }
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
      loadAndTransferInterfacesToView();

      updateComponents();
    }

    private void loadAndTransferInterfacesToView()
    {
      List<Interface> interfaces = invokeApiCall(apiClient -> apiClient.systemLoadBasedata()).getBaseData()
          .getInterfaces();

      view.getInterfaces().clear();
      view.getInterfaces().addAll(interfaces);

      Interface interfaceSelected = view.getInterfaceSelected();
      Integer interfaceID = null;

      if (interfaceSelected != null)
      {
        interfaceID = interfaceSelected.getInterfaceId();
      }
      else if (!Utils.isEmpty(view.getInterfaceID()))
      {
        interfaceID = Integer.valueOf(view.getInterfaceID());
      }

      if (interfaceID != null)
      {
        Integer ifcID = interfaceID;

        view.setInterfaceSelected(
            interfaces.stream().filter(ifc -> ifcID.equals(ifc.getInterfaceId())).findAny().orElse(null));

        handleInterfaceSelection(false);
      }
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
      JsfUtils.redirect(view.getSource());
    }
  }

  private void updateComponents()
  {
    view.setHintNoInterfaceMethodSelectedVisible(view.getInterfaceSelected() == null);
  }

  public void handleInterfaceSelection()
  {
    new HandleInterfaceSelectionExecution().execute();
  }

  private class HandleInterfaceSelectionExecution extends Execution
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
      handleInterfaceSelection(true);
    }
  }

  private void handleInterfaceSelection(boolean removeMethodSelected)
  {
    Interface ifc = view.getInterfaceSelected();

    view.getMethods().clear();

    if (removeMethodSelected)
    {
      view.setMethodSelected(null);
    }

    if (ifc == null)
    {
      view.setMethodSelected(null);
    }
    else
    {
      if (ifc.getMethods().isEmpty())
      {
        // load methods
        List<InterfaceMethod> methods = invokeApiCall(
            apiClient -> apiClient.loadInterface(ifc.getInterfaceId())).getInterface().getMethods();

        ifc.getMethods().addAll(methods);
      }

      List<InterfaceMethod> methods = ifc.getMethods();

      view.getMethods().addAll(methods);

      if (methods.size() == 1)
      {
        view.setMethodSelected(methods.get(0));
      }
      else if (methods.size() > 1)
      {
        InterfaceMethod methodSelected = view.getMethodSelected();
        Integer methodID = null;

        if (methodSelected != null)
        {
          methodID = methodSelected.getInterfaceMethodId();
        }
        else if (!Utils.isEmpty(view.getMethodID())
                 && Integer.valueOf(view.getInterfaceID()).equals(ifc.getInterfaceId()))
        {
          methodID = Integer.valueOf(view.getMethodID());
        }

        if (methodID != null)
        {
          Integer mID = methodID;

          view.setMethodSelected(
              methods.stream().filter(m -> mID.equals(m.getInterfaceMethodId())).findAny().orElse(null));
        }
      }
    }

    updateComponents();
  }

  public void useUploadedMockData()
  {
    new UseUploadedMockDataExecution().execute();
  }

  private class UseUploadedMockDataExecution extends Execution
  {
    private List<FileUploadEvent> mockDataUploadedEvents = view.getMockDataUploadedEvents();

    @Override
    protected void createPreValidations()
    {
      addValidation(new SelectionValidation(mockDataUploadedEvents, "mockdata"));

      if (view.getInterfaceSelected() != null)
      {
        addValidation(new NotNull(view.getMethodSelected(), "method"));
      }
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      String detail = String.valueOf(mockDataUploadedEvents.size()) + " " + Resources.getLabel("mockdata");

      return new Message(MessageLevel.INFO, "uploaded_var", detail);
    }

    @Override
    protected void _execute()
      throws Exception
    {
      for (FileUploadEvent e : mockDataUploadedEvents)
      {
        UploadedFile upFile = e.getFile();
        String filename = upFile.getFileName();
        String content = new String(upFile.getContents(), "UTF-8");

        InterfaceMethod methodSelected = view.getMethodSelected();

        boolean interfaceMethodByFileName = methodSelected == null;
        String filenameNew = filename;

        // Get interfaceMethod by FileName
        if (interfaceMethodByFileName)
        {
          int idxEndInterfaceName = filename.indexOf("_");
          if (idxEndInterfaceName <= 0 || idxEndInterfaceName == filename.length() - 1)
          {
            leaveWithBusinessException("mockdata_file_invalid", filename,
                Resources.getErrorMessage("mockdata_file_invalid_no_interface_name"));
          }

          int idxEndMethodName = filename.indexOf("_", idxEndInterfaceName + 1);
          if (idxEndMethodName < 0)
          {
            leaveWithBusinessException("mockdata_file_invalid", filename,
                Resources.getErrorMessage("mockdata_file_invalid_no_interface_method_name"));
          }

          methodSelected = new InterfaceMethod();
          methodSelected.setName(filename.substring(idxEndInterfaceName + 1, idxEndMethodName));

          methodSelected.setMockInterface(new Interface());
          methodSelected.getMockInterface().setName(filename.substring(0, idxEndInterfaceName));

          filenameNew = filename.substring(idxEndMethodName + 1);
        }

        MockData apiMockData = new MockData();
        apiMockData.setMockSession(view.getMockSessionSelected());
        apiMockData.setInterfaceMethod(methodSelected);
        apiMockData.setActive(true);

        // Title
        setMockDataTitle(filenameNew, apiMockData);

        // Request / Response
        getRequestResponseByFileContent(content, filename, apiMockData);

        // Save
        invokeApiCall(apiClient -> apiClient.saveMockData(apiMockData));

        mockDataUploadedEvents.remove(e);
      }
    }

    private void setMockDataTitle(String filenameNew, MockData apiMockData)
    {
      StringBuilder buiTitle = new StringBuilder();
      buiTitle.append(Utils.nvl(view.getTitlePrefix(), ""));
      buiTitle.append(filenameNew);

      if (view.isTitlePostfixTimestamp())
      {
        buiTitle.append("_").append(Utils.localDateTimeToString(LocalDateTime.now(),
            Utils.DATE_FORMAT_DD_MM_YYYY_HH_MM_SS_UNDERSCORES));
      }

      apiMockData.setTitle(buiTitle.toString());
    }

    private void getRequestResponseByFileContent(String fileContent, String filename, MockData apiMockdata)
      throws BusinessLevelException
    {
      // Get Request/Response from file
      int idxStartRequest = getFileIndexPrefixRequestResponse(fileContent, filename,
          Resources.PREFIX_MOCKDATA_IN_EXPORT_REQUEST, "mockdata_file_invalid_no_prefix_request");
      int idxStartResponse = getFileIndexPrefixRequestResponse(fileContent, filename,
          Resources.PREFIX_MOCKDATA_IN_EXPORT_RESPONSE, "mockdata_file_invalid_no_prefix_response");

      String request = getRequestResponseFromFileContent(fileContent, filename,
          Resources.PREFIX_MOCKDATA_IN_EXPORT_REQUEST, idxStartRequest, idxStartResponse);
      String response = getRequestResponseFromFileContent(fileContent, filename,
          Resources.PREFIX_MOCKDATA_IN_EXPORT_RESPONSE, idxStartResponse, fileContent.length());

      apiMockdata.setRequest(request);
      apiMockdata.setResponse(response);
    }

    private String getRequestResponseFromFileContent(String fileContent, String filename, String prefix,
                                                     int idxPrefix, int idxEnd)
      throws BusinessLevelException
    {
      try
      {
        String request = fileContent
            .substring(idxPrefix + Resources.PREFIX_MOCKDATA_IN_EXPORT_REQUEST.length(), idxEnd).trim();

        if (request.startsWith("\n"))
        {
          request = request.substring(1);
        }

        if (request.endsWith("\n"))
        {
          request = request.substring(0, request.length() - 1);
        }

        return request;
      }
      catch (IndexOutOfBoundsException ex)
      {
        leaveWithBusinessException("mockdata_file_invalid", filename,
            Resources.getErrorMessage("mockdata_file_invalid_no_request_response"));

        // never called
        return null;
      }
    }

    private int getFileIndexPrefixRequestResponse(String fileContent, String filename, String prefix,
                                                  String errorMsgDetail)
      throws BusinessLevelException
    {
      int idxStartRequest = fileContent.indexOf(prefix);

      if (idxStartRequest < 0)
      {
        leaveWithBusinessException("mockdata_file_invalid", filename,
            Resources.getErrorMessage(errorMsgDetail));
      }

      return idxStartRequest;
    }
  }
}