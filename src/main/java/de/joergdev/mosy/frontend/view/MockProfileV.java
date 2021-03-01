package de.joergdev.mosy.frontend.view;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import de.joergdev.mosy.frontend.utils.JsfUtils;
import de.joergdev.mosy.frontend.view.controller.MockProfileVC;
import de.joergdev.mosy.frontend.view.core.AbstractView;

@ManagedBean("mockprofile")
@ViewScoped
public class MockProfileV extends AbstractView<MockProfileVC>
{
  public static final String VIEW_PARAM_MOCK_PROFILE_ID = "mock_profile_id";

  private String mockProfileID;
  private String source;

  // ----- Controls ---------------

  private String name;
  private boolean persistent;
  private boolean useCommonMocks;
  private String description;
  private String created;

  private boolean deleteDisabled;
  // ------------------------------

  @PostConstruct
  public void init()
  {
    mockProfileID = JsfUtils.getViewParameter(VIEW_PARAM_MOCK_PROFILE_ID);
    source = JsfUtils.getPreviousPage();

    controller.showLoadRefresh();
  }

  @Override
  protected MockProfileVC getControllerInstance()
  {
    MockProfileVC vc = new MockProfileVC();
    vc.setView(this);

    return vc;
  }

  public void cancel()
  {
    controller.cancel();
  }

  public void save()
  {
    controller.save();
  }

  public void delete()
  {
    controller.delete();
  }

  /**
   * @return the mockProfileID
   */
  public String getMockProfileID()
  {
    return mockProfileID;
  }

  /**
   * @param mockProfileID the mockProfileID to set
   */
  public void setMockProfileID(String mockProfileID)
  {
    this.mockProfileID = mockProfileID;
  }

  /**
   * @return the source
   */
  public String getSource()
  {
    return source;
  }

  /**
   * @param source the source to set
   */
  public void setSource(String source)
  {
    this.source = source;
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * @return the persistent
   */
  public boolean isPersistent()
  {
    return persistent;
  }

  /**
   * @param persistent the persistent to set
   */
  public void setPersistent(boolean persistent)
  {
    this.persistent = persistent;
  }

  /**
   * @return the useCommonMocks
   */
  public boolean isUseCommonMocks()
  {
    return useCommonMocks;
  }

  /**
   * @param useCommonMocks the useCommonMocks to set
   */
  public void setUseCommonMocks(boolean useCommonMocks)
  {
    this.useCommonMocks = useCommonMocks;
  }

  /**
   * @return the description
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * @return the created
   */
  public String getCreated()
  {
    return created;
  }

  /**
   * @param created the created to set
   */
  public void setCreated(String created)
  {
    this.created = created;
  }

  public boolean isDeleteDisabled()
  {
    return deleteDisabled;
  }

  public void setDeleteDisabled(boolean deleteDisabled)
  {
    this.deleteDisabled = deleteDisabled;
  }
}