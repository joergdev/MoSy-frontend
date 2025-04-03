package de.joergdev.mosy.frontend.view;

import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.event.FileUploadEvent;
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.InterfaceMethod;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.frontend.Resources;
import de.joergdev.mosy.frontend.utils.ColumnModel;
import de.joergdev.mosy.frontend.utils.JsfUtils;
import de.joergdev.mosy.frontend.utils.WidthUnit;
import de.joergdev.mosy.frontend.view.controller.UploadMockdataVC;
import de.joergdev.mosy.frontend.view.core.AbstractView;

@Named("uploadMockdata")
@ViewScoped
public class UploadMockdataV extends AbstractView<UploadMockdataVC>
{
  public static final String VIEW_PARAM_INTERFACE_ID = "interface_id";
  public static final String VIEW_PARAM_METHOD_ID = "method_id";

  private String interfaceID;
  private String methodID;
  private String source;

  // --- MockProfiles ---
  // Dialog (choose mockprofile for add)
  private List<MockProfile> mockProfiles = new ArrayList<>();
  private MockProfile mdMockProfile;

  private List<MockProfile> tblMockProfiles = new ArrayList<>();

  private List<MockProfile> selectedMockProfiles;

  private List<ColumnModel> tblMockProfilesColumns = new ArrayList<>();

  private boolean deleteMockProfileDisabled = true;
  // --------------------

  private String titlePrefix = "Uploaded_";
  private boolean titlePostfixTimestamp = true;

  private final List<Interface> interfaces = new ArrayList<>();
  private Interface interfaceSelected;

  private final List<InterfaceMethod> methods = new ArrayList<>();
  private InterfaceMethod methodSelected;

  private boolean hintNoInterfaceMethodSelectedVisible;

  @PostConstruct
  public void init()
  {
    interfaceID = JsfUtils.getViewParameter(VIEW_PARAM_INTERFACE_ID);
    methodID = JsfUtils.getViewParameter(VIEW_PARAM_METHOD_ID);
    source = JsfUtils.getPreviousPage();

    initTblMockProfiles();

    controller.loadRefresh();
  }

  @Override
  protected UploadMockdataVC getControllerInstance()
  {
    UploadMockdataVC vc = new UploadMockdataVC();
    vc.setView(this);

    return vc;
  }

  public void cancel()
  {
    controller.cancel();
  }

  public void handleInterfaceSelection()
  {
    controller.handleInterfaceSelection();
  }

  public void uploadMockData(FileUploadEvent event)
  {
    controller.uploadMockData(event);
  }

  public void useUploadedMockData()
  {
    controller.useUploadedMockData();
  }

  // --- MockProfiles ---

  private void initTblMockProfiles()
  {
    ColumnModel colName = new ColumnModel(Resources.getLabel("name"), "name");
    colName.setWidth(67, WidthUnit.PERCENT);
    tblMockProfilesColumns.add(colName);

    ColumnModel colPersistent = new ColumnModel(Resources.getLabel("persistent"), "persistent");
    colPersistent.setWidth(33, WidthUnit.PERCENT);
    tblMockProfilesColumns.add(colPersistent);
  }

  public void onMockProfilesRowSelect()
  {
    controller.handleMockProfilesSelection();
  }

  public void onMockProfilesRowUnselect()
  {
    controller.handleMockProfilesSelection();
  }

  public void addMockProfile()
  {
    controller.addMockProfile();
  }

  public void addSelectedMockProfile()
  {
    controller.addSelectedMockProfile();
  }

  public void deleteMockProfiles()
  {
    controller.deleteMockProfiles();
  }

  // --------------------

  public String getInterfaceID()
  {
    return interfaceID;
  }

  public void setInterfaceID(String interfaceID)
  {
    this.interfaceID = interfaceID;
  }

  public String getMethodID()
  {
    return methodID;
  }

  public void setMethodID(String methodID)
  {
    this.methodID = methodID;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource(String source)
  {
    this.source = source;
  }

  public String getTitlePrefix()
  {
    return titlePrefix;
  }

  public void setTitlePrefix(String titlePrefix)
  {
    this.titlePrefix = titlePrefix;
  }

  public boolean isTitlePostfixTimestamp()
  {
    return titlePostfixTimestamp;
  }

  public void setTitlePostfixTimestamp(boolean titlePostfixTimestamp)
  {
    this.titlePostfixTimestamp = titlePostfixTimestamp;
  }

  public boolean isHintNoInterfaceMethodSelectedVisible()
  {
    return hintNoInterfaceMethodSelectedVisible;
  }

  public void setHintNoInterfaceMethodSelectedVisible(boolean hintNoInterfaceMethodSelectedVisible)
  {
    this.hintNoInterfaceMethodSelectedVisible = hintNoInterfaceMethodSelectedVisible;
  }

  public Interface getInterfaceSelected()
  {
    return interfaceSelected;
  }

  public void setInterfaceSelected(Interface interfaceSelected)
  {
    this.interfaceSelected = interfaceSelected;
  }

  public InterfaceMethod getMethodSelected()
  {
    return methodSelected;
  }

  public void setMethodSelected(InterfaceMethod methodSelected)
  {
    this.methodSelected = methodSelected;
  }

  public List<Interface> getInterfaces()
  {
    return interfaces;
  }

  public List<InterfaceMethod> getMethods()
  {
    return methods;
  }

  /**
   * @return the tblMockProfiles
   */
  public List<MockProfile> getTblMockProfiles()
  {
    return tblMockProfiles;
  }

  /**
   * @param tblMockProfiles the tblMockProfiles to set
   */
  public void setTblMockProfiles(List<MockProfile> tblMockProfiles)
  {
    this.tblMockProfiles = tblMockProfiles;
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

  /**
   * @return the mockProfiles
   */
  public List<MockProfile> getMockProfiles()
  {
    return mockProfiles;
  }

  /**
   * @param mockProfiles the mockProfiles to set
   */
  public void setMockProfiles(List<MockProfile> mockProfiles)
  {
    this.mockProfiles = mockProfiles;
  }

  /**
   * @return the mdMockProfile
   */
  public MockProfile getMdMockProfile()
  {
    return mdMockProfile;
  }

  /**
   * @param mdMockProfile the mdMockProfile to set
   */
  public void setMdMockProfile(MockProfile mdMockProfile)
  {
    this.mdMockProfile = mdMockProfile;
  }
}