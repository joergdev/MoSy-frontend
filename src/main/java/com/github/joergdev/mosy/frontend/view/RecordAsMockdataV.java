package com.github.joergdev.mosy.frontend.view;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import com.github.joergdev.mosy.api.model.MockSession;
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

  private final List<MockSession> mockSessions = new ArrayList<>();
  private MockSession mockSessionSelected;

  private String titlePrefix;

  private final List<Record> tblRecords = new ArrayList<>();
  private final List<ColumnModel> tblRecordsColumns = new ArrayList<>();

  @PostConstruct
  public void init()
  {
    recordIds = JsfUtils.getViewParameter(VIEW_PARAM_RECORDS_IDS);
    source = JsfUtils.getPreviousPage();

    initTblRecords();

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

  public String getRecordIds()
  {
    return recordIds;
  }

  public void setRecordIds(String recordIds)
  {
    this.recordIds = recordIds;
  }

  public MockSession getMockSessionSelected()
  {
    return mockSessionSelected;
  }

  public void setMockSessionSelected(MockSession mockSessionSelected)
  {
    this.mockSessionSelected = mockSessionSelected;
  }

  public List<MockSession> getMockSessions()
  {
    return mockSessions;
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
}