package com.github.joergdev.mosy.frontend.view;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import com.github.joergdev.mosy.api.model.MockProfile;
import com.github.joergdev.mosy.api.model.Record;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.utils.ColumnModel;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.utils.WidthUnit;
import com.github.joergdev.mosy.frontend.view.controller.RecordAsMockdataVC;
import com.github.joergdev.mosy.frontend.view.core.AbstractView;

@ManagedBean("recordAsMockdata")
@ViewScoped
public class RecordAsMockdataV extends AbstractView<RecordAsMockdataVC>
{
  public static final String VIEW_PARAM_RECORDS_IDS = "records";

  private String recordIds;
  private String source;

  //--- MockProfiles ---

  //Dialog (choose mockprofile for add)
  private List<MockProfile> mockProfiles = new ArrayList<>();
  private MockProfile mdMockProfile;

  private List<MockProfile> tblMockProfiles = new ArrayList<>();

  private List<MockProfile> selectedMockProfiles;

  private List<ColumnModel> tblMockProfilesColumns = new ArrayList<>();

  private boolean deleteMockProfileDisabled = true;
  // --------------------

  private String titlePrefix;

  private final List<Record> tblRecords = new ArrayList<>();
  private final List<ColumnModel> tblRecordsColumns = new ArrayList<>();

  @PostConstruct
  public void init()
  {
    recordIds = JsfUtils.getViewParameter(VIEW_PARAM_RECORDS_IDS);
    source = JsfUtils.getPreviousPage();

    initTblRecords();
    initTblMockProfiles();

    controller.loadRefresh();
  }

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

  @Override
  protected RecordAsMockdataVC getControllerInstance()
  {
    RecordAsMockdataVC vc = new RecordAsMockdataVC();
    vc.setView(this);

    return vc;
  }

  public void useRecordsAsMockdata()
  {
    controller.useRecordsAsMockdata();
  }

  public void cancel()
  {
    controller.cancel();
  }

  //--- MockProfiles ---

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

  public String getRecordIds()
  {
    return recordIds;
  }

  public void setRecordIds(String recordIds)
  {
    this.recordIds = recordIds;
  }

  public List<Record> getTblRecords()
  {
    return tblRecords;
  }

  public List<ColumnModel> getTblRecordsColumns()
  {
    return tblRecordsColumns;
  }

  public String getTitlePrefix()
  {
    return titlePrefix;
  }

  public void setTitlePrefix(String titlePrefix)
  {
    this.titlePrefix = titlePrefix;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource(String source)
  {
    this.source = source;
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
}