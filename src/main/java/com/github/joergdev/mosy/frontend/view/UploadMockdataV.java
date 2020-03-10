package com.github.joergdev.mosy.frontend.view;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.view.controller.UploadMockdataVC;
import com.github.joergdev.mosy.frontend.view.core.AbstractView;

@ManagedBean("uploadMockdata")
@ViewScoped
public class UploadMockdataV extends AbstractView<UploadMockdataVC>
{
  public static final String VIEW_PARAM_INTERFACE_ID = "interface_id";
  public static final String VIEW_PARAM_METHOD_ID = "method_id";

  private String interfaceID;
  private String methodID;
  private String source;

  private final List<MockSession> mockSessions = new ArrayList<>();
  private MockSession mockSessionSelected;

  private String titlePrefix = "Uploaded_";
  private boolean titlePostfixTimestamp = true;

  private final List<Interface> interfaces = new ArrayList<>();
  private Interface interfaceSelected;

  private final List<InterfaceMethod> methods = new ArrayList<>();
  private InterfaceMethod methodSelected;

  private boolean hintNoInterfaceMethodSelectedVisible;

  private final List<FileUploadEvent> mockDataUploadedEvents = new ArrayList<>();

  @PostConstruct
  public void init()
  {
    interfaceID = JsfUtils.getViewParameter(VIEW_PARAM_INTERFACE_ID);
    methodID = JsfUtils.getViewParameter(VIEW_PARAM_METHOD_ID);
    source = JsfUtils.getPreviousPage();

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
    mockDataUploadedEvents.add(event);
  }

  public void useUploadedMockData()
  {
    controller.useUploadedMockData();
  }

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

  public MockSession getMockSessionSelected()
  {
    return mockSessionSelected;
  }

  public void setMockSessionSelected(MockSession mockSessionSelected)
  {
    this.mockSessionSelected = mockSessionSelected;
  }

  public String getTitlePrefix()
  {
    return titlePrefix;
  }

  public void setTitlePrefix(String titlePrefix)
  {
    this.titlePrefix = titlePrefix;
  }

  public List<MockSession> getMockSessions()
  {
    return mockSessions;
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

  public List<FileUploadEvent> getMockDataUploadedEvents()
  {
    return mockDataUploadedEvents;
  }
}