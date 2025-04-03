package de.joergdev.mosy.frontend.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.FacesConverter;
import de.joergdev.mosy.api.model.RecordSession;
import de.joergdev.mosy.shared.Utils;

@FacesConverter(value = "RecordSessionConverter", forClass = RecordSession.class)
public class RecordSessionConverter extends AbstractConverter
{
  @Override
  protected Object _getAsObject(FacesContext context, UIComponent component, String submittedValue)
  {
    RecordSession rs = new RecordSession();

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

    rs.setRecordSessionID(Utils.asInteger(id));
    rs.setCreated(Utils.parseDate(created, Utils.DATE_FORMAT_DD_MM_YYYY_HH_MM_SS, false));

    return rs;
  }
}