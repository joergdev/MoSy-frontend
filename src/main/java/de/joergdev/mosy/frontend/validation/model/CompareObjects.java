package de.joergdev.mosy.frontend.validation.model;

public class CompareObjects
{
  private Object a;
  private Object b;

  public CompareObjects(Object a, Object b)
  {
    setA(a);
    setB(b);
  }

  public Object getA()
  {
    return a;
  }

  public void setA(Object a)
  {
    this.a = a;
  }

  public Object getB()
  {
    return b;
  }

  public void setB(Object b)
  {
    this.b = b;
  }
}
