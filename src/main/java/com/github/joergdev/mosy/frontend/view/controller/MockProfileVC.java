package com.github.joergdev.mosy.frontend.view.controller;

import com.github.joergdev.mosy.api.model.MockProfile;
import com.github.joergdev.mosy.frontend.Message;
import com.github.joergdev.mosy.frontend.MessageLevel;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.validation.NotNull;
import com.github.joergdev.mosy.frontend.validation.StringNotEmpty;
import com.github.joergdev.mosy.frontend.view.MockProfileV;
import com.github.joergdev.mosy.frontend.view.controller.core.AbstractViewController;
import com.github.joergdev.mosy.shared.Utils;

public class MockProfileVC extends AbstractViewController<MockProfileV>
{
  private MockProfile apiMockProfile;
  private MockProfile apiMockProfileClone;

  public void showLoadRefresh()
  {
    new ShowLoadRefreshExecution().execute();
  }

  @Override
  public void refresh()
  {
    showLoadRefresh();
  }

  private class ShowLoadRefreshExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      // no validation
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      if (view.getMockProfileID() != null)
      {
        return new Message(MessageLevel.INFO, "loaded_var", Resources.getLabel("mock_profile"));
      }

      return null;
    }

    @Override
    protected void _execute()
      throws Exception
    {
      if (view.getMockProfileID() != null)
      {
        apiMockProfile = invokeApiCall(
            apiClient -> apiClient.loadMockProfile(Utils.asInteger(view.getMockProfileID())))
                .getMockProfile();
      }
      else
      {
        apiMockProfile = new MockProfile();
      }

      apiMockProfileClone = apiMockProfile.clone();

      transferDataToView();

      updateComponents();
    }

    private void transferDataToView()
    {
      view.setName(apiMockProfileClone.getName());
      view.setPersistent(apiMockProfileClone.isPersistent());
      view.setUseCommonMocks(apiMockProfileClone.isUseCommonMocks());
      view.setDescription(apiMockProfileClone.getDescription());
      view.setCreated(apiMockProfileClone.getCreatedAsString());
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
      JsfUtils.redirect(Utils.nvl(view.getSource(), Resources.SITE_MAIN));
    }
  }

  private void updateComponents()
  {
    view.setDeleteDisabled(apiMockProfileClone.getMockProfileID() == null);
  }

  public void save()
  {
    new SaveMockProfileExecution().execute();
  }

  private class SaveMockProfileExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new StringNotEmpty(view.getName(), "name"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "saved_var", Resources.getLabel("mock_profile"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      // update copyModel
      apiMockProfileClone.setName(view.getName());
      apiMockProfileClone.setPersistent(view.isPersistent());
      apiMockProfileClone.setUseCommonMocks(view.isUseCommonMocks());
      apiMockProfileClone.setDescription(view.getDescription());

      // save
      MockProfile apiMockProfileSaved = invokeApiCall(
          apiClient -> apiClient.saveMockProfile(apiMockProfileClone)).getMockProfile();

      // update model (copyModel=mode, +id's aus save uebertragen)
      apiMockProfileClone.setMockProfileID(apiMockProfileSaved.getMockProfileID());
      apiMockProfileClone.setCreated(apiMockProfileSaved.getCreated());

      apiMockProfile = apiMockProfileClone.clone();

      view.setMockProfileID(Utils.asString(apiMockProfileClone.getMockProfileID()));
      view.setCreated(apiMockProfileClone.getCreatedAsString());

      // updateComponents
      updateComponents();
    }
  }

  public void delete()
  {
    new DeleteMockProfileExecution().execute();
  }

  private class DeleteMockProfileExecution extends Execution
  {
    @Override
    protected void createPreValidations()
    {
      addValidation(new NotNull(view.getMockProfileID(), "id"));
    }

    @Override
    public Message getGrowlMessageOnSuccess()
    {
      return new Message(MessageLevel.INFO, "deleted_var", Resources.getLabel("mock_profile"));
    }

    @Override
    protected void _execute()
      throws Exception
    {
      invokeApiCall(apiClient -> apiClient.deleteMockProfile(Utils.asInteger(view.getMockProfileID())));

      JsfUtils.redirect(Utils.nvl(view.getSource(), Resources.SITE_MAIN));
    }
  }
}