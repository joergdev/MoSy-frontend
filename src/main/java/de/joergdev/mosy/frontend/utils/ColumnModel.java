package de.joergdev.mosy.frontend.utils;

public class ColumnModel
{
  private String header;
  private String property;
  private Integer width;
  private WidthUnit widthUnit;

  public ColumnModel(String header, String property)
  {
    this.header = header;
    this.property = property;
  }

  public String getHeader()
  {
    return header;
  }

  public String getProperty()
  {
    return property;
  }

  public void setWidth(Integer width, WidthUnit unit)
  {
    this.width = width;
    this.widthUnit = unit;
  }

  public Integer getWidth()
  {
    return width;
  }

  public void setWidth(Integer width)
  {
    this.width = width;
  }

  public WidthUnit getWidthUnit()
  {
    return widthUnit;
  }

  public void setWidthUnit(WidthUnit widthUnit)
  {
    this.widthUnit = widthUnit;
  }

  public String getWidthFull()
  {
    if (width != null && widthUnit != null)
    {
      return String.valueOf(width) + widthUnit.postfix;
    }

    return null;
  }
}