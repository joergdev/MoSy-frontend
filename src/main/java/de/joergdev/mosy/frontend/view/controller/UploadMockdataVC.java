package de.joergdev.mosy.frontend.view.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.InterfaceMethod;
import de.joergdev.mosy.api.model.MockData;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.frontend.Message;
import de.joergdev.mosy.frontend.MessageLevel;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.frontend.utils.JsfUtils;
import de.joergdev.mosy.frontend.validation.NotNull;
import de.joergdev.mosy.frontend.validation.NumberValidator;
import de.joergdev.mosy.frontend.validation.SelectionValidation;
import de.joergdev.mosy.frontend.view.UploadMockdataV;
import de.joergdev.mosy.frontend.view.controller.core.AbstractViewController;
import de.joergdev.mosy.shared.Utils;

public class UploadMockdataVC extends AbstractViewController<UploadMockdataV>
{
  private final List<FileUploadEvent> mockDataUploadedEvents = new ArrayList<>();

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

    updateComponentsMockDataMockProfiles();
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
      String ifcName = ifc.getName();

      ifc = view.getInterfaces().stream().filter(ifcList -> Utils.isEqual(ifcList.getName(), ifcName))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException("no interface found with name " + ifcName));

      if (ifc.getMethods().isEmpty())
      {
        Integer ifcID = ifc.getInterfaceId();

        // load methods
        List<InterfaceMethod> methods = invokeApiCall(apiClient -> apiClient.loadInterface(ifcID))
            .getInterface().getMethods();

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

        if (methodSelected != null)
        {
          String methodName = methodSelected.getName();

          view.setMethodSelected(
              methods.stream().filter(m -> methodName.equals(m.getName())).findAny().orElse(null));
        }
        else if (!Utils.isEmpty(view.getMethodID())
                 && Integer.valueOf(view.getInterfaceID()).equals(ifc.getInterfaceId()))
        {
          Integer methodID = Integer.valueOf(view.getMethodID());

          view.setMethodSelected(
              methods.stream().filter(m -> methodID.equals(m.getInterfaceMethodId())).findAny().orElse(null));

        }
      }
    }

    updateComponents();
  }

  public void uploadMockData(FileUploadEvent event)
  {
    mockDataUploadedEvents.add(event);

    useUploadedMockData(true, event);
  }

  public void useUploadedMockData()
  {
    useUploadedMockData(false, null);
  }

  private void useUploadedMockData(boolean singlemode, FileUploadEvent event)
  {
    boolean singlemodeEnabled = Boolean.TRUE
        .equals(Boolean.valueOf(Resources.getProperty(Resources.PROPERTY_UPLOAD_MOCKDATA_SINGLEMODE)));

    if (singlemode == singlemodeEnabled)
    {
      new UseUploadedMockDataExecution(singlemode, event).execute();
    }

    // Called when upload finished, if singlemode then show sucess message
    // because message in UseUploadedMockDataExecution ist not showing in this case.
    if (!singlemode && singlemodeEnabled)
    {
      showGrowlMessage(new Message(MessageLevel.INFO, "uploaded_var",
          mockDataUploadedEvents.size() + " " + Resources.getLabel("mockdata")));

      mockDataUploadedEvents.clear();
    }
  }

  private class UseUploadedMockDataExecution extends Execution
  {
    private final boolean singlemode;
    private final FileUploadEvent event;

    private int countUploaded = 0;

    public UseUploadedMockDataExecution(boolean singlemode, FileUploadEvent event)
    {
      this.singlemode = singlemode;
      this.event = event;
    }

    @Override
    protected void createPreValidations()
    {
      if (singlemode)
      {
        addValidation(new NotNull(event, "mockdata"));
      }
      else
      {
        addValidation(new SelectionValidation(mockDataUploadedEvents, "mockdata"));
      }

      if (view.getInterfaceSelected() != null)
      {
        addValidation(new NotNull(view.getMethodSelected(), "method"));
      }
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      if (!singlemode)
      {
        String detail = String.valueOf(countUploaded) + " " + Resources.getLabel("mockdata");

        return new Message(MessageLevel.INFO, "uploaded_var", detail);
      }

      return null;
    }

    @Override
    protected void _execute()
      throws Exception
    {
      Iterator<FileUploadEvent> itFiles = singlemode
          ? Arrays.asList(event).iterator()
          : mockDataUploadedEvents.iterator();

      while (itFiles.hasNext())
      {
        FileUploadEvent e = itFiles.next();
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
        apiMockData.getMockProfiles().addAll(view.getTblMockProfiles());
        apiMockData.setInterfaceMethod(methodSelected);
        apiMockData.setActive(true);

        // Title
        setMockDataTitle(filenameNew, apiMockData);

        // Request / Response
        try
        {
          apiMockData.setRequestResponseByFileContent(content);
        }
        catch (IndexOutOfBoundsException ex)
        {
          leaveWithBusinessException("mockdata_file_invalid", filename,
              Resources.getErrorMessage("mockdata_file_invalid_no_request_response"));
        }

        // Save
        invokeApiCall(apiClient -> apiClient.saveMockData(apiMockData));

        countUploaded++;

        itFiles.remove();
      }
    }

    private void setMockDataTitle(String filenameNew, MockData apiMockData)
    {
      int idxLastPoint = filenameNew.lastIndexOf(".");
      if (idxLastPoint > 0)
      {
        filenameNew = filenameNew.substring(0, idxLastPoint);
      }

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
    view.setDeleteMockProfileDisabled(Utils.nvlCollection(view.getSelectedMockProfiles()).isEmpty());
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