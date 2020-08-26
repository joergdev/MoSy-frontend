package com.github.joergdev.mosy.frontend.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import com.github.joergdev.mosy.api.model.MockSession;
import com.github.joergdev.mosy.shared.Utils;

@FacesConverter(value = "MockSessionConverter", forClass = MockSession.class)
public class MockSessionConverter extends AbstractConverter
{
  @Override
  protected Object _getAsObject(FacesContext context, UIComponent component, String submittedValue)
  {
    MockSession ms = new MockSession();

    int idxSep = submittedValue.indexOf("-");

    String id = null;
    String created = null;

    if (idxSep > 0)
    {
      id = submittedValue.substring(0, idxSep - 1);
      created = submittedValue.substring(idxSep + 2);
    }
    else
    {
      id = submittedValue;
    }

    ms.setMockSessionID(Utils.asInteger(id));
    ms.setCreated(Utils.parseDate(created, Utils.DATE_FORMAT_DD_MM_YYYY_HH_MM_SS, false));

    return ms;
  }
}