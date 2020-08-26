package com.github.joergdev.mosy.frontend.utils;

import java.util.ArrayList;
import java.util.List;
import com.github.joergdev.mosy.shared.Utils;

public class TreeData
{
  private String text;
  private String viewPage;
  private Object entity;
  private boolean defaultSelection;
  private final List<TreeData> subEntries = new ArrayList<>();

  public TreeData(String text, String viewPage)
  {
    this(text, viewPage, null, false);
  }

  public TreeData(String text, String viewPage, Object entity)
  {
    this(text, viewPage, entity, false);
  }

  public TreeData(String text, String viewPage, boolean defaultSelection)
  {
    this(text, viewPage, null, defaultSelection);
  }

  public TreeData(String text, String viewPage, Object entity, boolean defaultSelection)
  {
    this.text = text;
    this.viewPage = viewPage;
    this.entity = entity;
    this.defaultSelection = defaultSelection;
  }

  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  public String getViewPage()
  {
    return viewPage;
  }

  public void setViewPage(String viewPage)
  {
    this.viewPage = viewPage;
  }

  public List<TreeData> getSubEntries()
  {
    return subEntries;
  }

  public boolean isDefaultSelection()
  {
    return defaultSelection;
  }

  public void setDefaultSelection(boolean defaultSelection)
  {
    this.defaultSelection = defaultSelection;
  }

  @Override
  public String toString()
  {
    return text;
  }

  public boolean isEqual(TreeData other)
  {
    if (other != null)
    {
      if (Utils.isEqual(text, other.text) && Utils.isEqual(viewPage, other.viewPage)
          && Utils.isEqual(entity, other.entity))
      {
        return true;
      }
    }

    return false;
  }

  @SuppressWarnings("unchecked")
  public <T> T getEntity(Class<T> entityClass)
  {
    return (T) entity;
  }

  @SuppressWarnings("unchecked")
  public <T> T getEntity()
  {
    return (T) entity;
  }

  public void setEntity(Object entity)
  {
    this.entity = entity;
  }
}