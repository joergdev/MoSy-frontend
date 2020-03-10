package com.github.joergdev.mosy.frontend.view.controller.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.NotAuthorizedException;
import org.apache.log4j.Logger;
import com.github.joergdev.mosy.api.client.MosyApiClient;
import com.github.joergdev.mosy.api.response.AbstractResponse;
import com.github.joergdev.mosy.api.response.ResponseMessage;
import com.github.joergdev.mosy.api.response.ResponseMessageLevel;
import com.github.joergdev.mosy.frontend.Message;
import com.github.joergdev.mosy.frontend.MessageLevel;
import com.github.joergdev.mosy.frontend.Resources;
import com.github.joergdev.mosy.frontend.security.TokenHolder;
import com.github.joergdev.mosy.frontend.utils.JsfUtils;
import com.github.joergdev.mosy.frontend.validation.AbstractValidation;
import com.github.joergdev.mosy.frontend.validation.ValidationHandler;
import com.github.joergdev.mosy.frontend.view.LoginV;
import com.github.joergdev.mosy.frontend.view.core.AbstractView;
import com.github.joergdev.mosy.shared.ValueWrapper;

public abstract class AbstractViewController<T extends AbstractView<?>>
{
  private static final Logger LOG = Logger.getLogger(AbstractViewController.class);

  protected T view;

  public void showInfoMessage(String message, String... details)
  {
    showMessage(MessageLevel.INFO, message);
  }

  public void showWarnMessage(String message, String... details)
  {
    showMessage(MessageLevel.WARN, message);
  }

  public void showErrorMessage(String message, String... details)
  {
    showMessage(MessageLevel.ERROR, message);
  }

  public void showFatalErrorMessage(String message, String... details)
  {
    showMessage(MessageLevel.FATAL, message);
  }

  public void showGrowlMessage(MessageLevel level, String message, String... details)
  {
    JsfUtils.showMessage("common_growl", level.getFacesSeverity(), Resources.getLabel(level.getLabel()),
        Resources.getMessage(level, message, details));
  }

  public void showMessage(MessageLevel level, String message, String... details)
  {
    String messagesComp = null;

    if (MessageLevel.INFO.equals(level))
    {
      messagesComp = "info";
    }
    else if (MessageLevel.WARN.equals(level))
    {
      messagesComp = "warn";
    }
    else if (MessageLevel.ERROR.equals(level))
    {
      messagesComp = "error";
    }
    else if (MessageLevel.FATAL.equals(level))
    {
      messagesComp = "fatal";
    }

    JsfUtils.showMessage(messagesComp, level.getFacesSeverity(), Resources.getLabel(level.getLabel()),
        Resources.getMessage(level, message, details));
  }

  public T getView()
  {
    return view;
  }

  public void setView(T view)
  {
    this.view = view;
  }

  protected abstract class Execution
  {
    private ValidationHandler vh;
    private final List<Message> messages = new ArrayList<>();
    private final MosyApiClient apiClient = new MosyApiClient(TokenHolder.getToken());

    public <K> Execution addValidation(AbstractValidation<K> validation)
    {
      if (vh == null)
      {
        vh = new ValidationHandler();
      }

      vh = vh.addValidation(validation);

      return this;
    }

    private boolean validate()
    {
      boolean result = vh.validate();

      messages.addAll(vh.getMessages());

      vh = null;

      return result;
    }

    public void leaveWithBusinessException(String msg, String... msgDetails)
      throws BusinessLevelException
    {
      leaveWithBusinessException(MessageLevel.ERROR, msg, msgDetails);
    }

    public void leaveWithBusinessException(MessageLevel level, String msg, String... msgDetails)
      throws BusinessLevelException
    {
      leaveWithException(level, msg, true, msgDetails);
    }

    public void leaveWithTechnicalException(MessageLevel level, String msg, String... msgDetails)
      throws BusinessLevelException
    {
      leaveWithException(level, msg, false, msgDetails);
    }

    private void leaveWithException(MessageLevel level, String msg, boolean businessLevel,
                                    String... msgDetails)
      throws BusinessLevelException
    {
      Message message = addMessage(level, msg, msgDetails);

      if (businessLevel)
      {
        throw new BusinessLevelException(level, message.getFullMessage());
      }
      else
      {
        throw new IllegalStateException(message.getFullMessage());
      }
    }

    public Message addMessage(MessageLevel level, String msg, String... msgDetails)
    {
      Message message = new Message();
      message.setLevel(level);
      message.setMsg(msg);
      message.setMessageDetails(msgDetails);

      addMessage(message);

      return message;
    }

    public void addMessage(Message msg)
    {
      messages.add(msg);
    }

    public final void execute()
    {
      try
      {
        createPreValidations();

        boolean validationOk = vh == null
            ? true
            : validate();

        if (validationOk)
        {
          _execute();

          showGrowlMessageOnSuccess();
        }
      }
      catch (NotAuthorizedException ex)
      {
        try
        {
          Map<String, Object> queryParams = new HashMap<>();
          queryParams.put(LoginV.VIEW_PARAM_NO_AUTH, true);

          JsfUtils.redirect(Resources.SITE_LOGIN, queryParams);
        }
        catch (Exception ex2)
        {
          handleCommonException(ex2);
        }
      }
      catch (Exception ex)
      {
        handleCommonException(ex);
      }

      showMessages();
    }

    private void handleCommonException(Exception ex)
    {
      MessageLevel exLevel = MessageLevel.ERROR;

      boolean isBusinessLevelException = ex instanceof BusinessLevelException;
      if (isBusinessLevelException)
      {
        exLevel = ((BusinessLevelException) ex).getLevel();
      }

      if (!hasError() && exLevel.rank >= MessageLevel.ERROR.rank)
      {
        addMessage(exLevel, "unexpected_error", ex.getMessage());
      }

      if (!isBusinessLevelException)
      {
        LOG.error(ex.getMessage(), ex);
      }
    }

    private void showGrowlMessageOnSuccess()
    {
      Message growlMsgOnSuccess = getGrowlMessageOnSuccess();
      if (growlMsgOnSuccess != null)
      {
        showGrowlMessage(growlMsgOnSuccess.getLevel(), growlMsgOnSuccess.getMsg(),
            growlMsgOnSuccess.getMessageDetails());
      }
    }

    private void showMessages()
    {
      if (!messages.isEmpty())
      {
        // Group messages by level
        Map<Object, List<Message>> mapMessagesPerLevel = messages.stream()
            .collect(Collectors.groupingBy(msg -> msg.getLevel()));

        // show one message per level
        for (Object lvl : mapMessagesPerLevel.keySet())
        {
          StringBuilder bui = new StringBuilder();

          for (Message message : mapMessagesPerLevel.get(lvl))
          {
            if (bui.length() > 0)
            {
              bui.append("</br>");
            }

            bui.append(message.getFullMessage());
          }

          showMessage((MessageLevel) lvl, "plain_msg", bui.toString());
        }
      }
    }

    public <K extends AbstractResponse> K invokeApiCall(Function<MosyApiClient, K> apiCallFunction)
    {
      K response = apiCallFunction.apply(apiClient);

      // Handle messages
      for (ResponseMessage rspMsg : response.getMessages())
      {
        String msg = rspMsg.getFullMessage();

        // no Debug level in mosy frontend so add as text before msg
        if (ResponseMessageLevel.DEBUG.equals(rspMsg.getResponseCode().level))
        {
          msg = "DEBUG - " + msg;
        }

        addMessage(MessageLevel.getByResponseMessageLevel(rspMsg.getResponseCode().level), "api_call_msg",
            msg);
      }

      return response;
    }

    public boolean hasError()
    {
      return messages.stream()
          .anyMatch(msg -> Arrays.asList(MessageLevel.ERROR, MessageLevel.FATAL).contains(msg.getLevel()));
    }

    protected abstract void createPreValidations();

    protected abstract void _execute()
      throws Exception;

    /** can be overwritten in impl class */
    public Message getGrowlMessageOnSuccess()
    {
      return null;
    }
  }

  /** has to be implemented in child class */
  public void refresh()
  {

  }

  public void logout()
  {
    new LogoutExecution().execute();
  }

  private class LogoutExecution extends Execution
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
      invokeApiCall(apiClient -> apiClient.systemLogout());

      JsfUtils.invalidateSession();

      JsfUtils.redirect(Resources.SITE_GOODBUY);
    }
  }

  public <K extends AbstractResponse> K invokeApiCall(Function<MosyApiClient, K> apiCallFunction)
  {
    ValueWrapper<K> apiCallResponse = new ValueWrapper<>(null);

    new InvokeApiCallExecution<>(apiCallFunction, apiCallResponse).execute();

    return apiCallResponse.getValue();
  }

  private class InvokeApiCallExecution<K extends AbstractResponse> extends Execution
  {
    private final Function<MosyApiClient, K> apiCallFunction;
    private final ValueWrapper<K> apiCallResponse;

    private InvokeApiCallExecution(Function<MosyApiClient, K> apiCallFunction,
                                   ValueWrapper<K> apiCallResponse)
    {
      this.apiCallFunction = Objects.requireNonNull(apiCallFunction, "apiCallFunction");
      this.apiCallResponse = Objects.requireNonNull(apiCallResponse, "apiCallResponse");
    }

    @Override
    protected void createPreValidations()
    {
      // no validation
    }

    @Override
    protected void _execute()
      throws Exception
    {
      K response = invokeApiCall(apiCallFunction);

      apiCallResponse.setValue(response);
    }
  }
}