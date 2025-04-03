package de.joergdev.mosy.frontend.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.FacesConverter;
import de.joergdev.mosy.api.model.MockProfile;
import de.joergdev.mosy.shared.Utils;

@FacesConverter(value = "MockProfileConverter", forClass = MockProfile.class)
public class MockProfileConverter extends AbstractConverter
{
  protected Object _getAsObject(FacesContext context, UIComponent component, String submittedValue)
  {
    if (submittedValue == null || submittedValue.indexOf("-") < 1)
    {
      return null;
    }

    MockProfile apiMockProfile = new MockProfile();

    int idxSlash = submittedValue.indexOf("-");

    apiMockProfile.setMockProfileID(Utils.asInteger(submittedValue.substring(0, idxSlash).trim()));

    submittedValue = submittedValue.substring(idxSlash + 1);
    idxSlash = submittedValue.indexOf("-");

    apiMockProfile.setName(submittedValue.substring(0, idxSlash).trim());

    return apiMockProfile;
  }
}