package org.device.demo;

/**
 * Created by Dmitry on 13.11.2015.
 */
public class TypeSelectItem {
  private Object type;
  private String typeCaption;

  public TypeSelectItem(Object type, String typeCaption) {
    this.type = type;
    this.typeCaption = typeCaption;
  }

  public Object getType() {
    return type;
  }

  public String getTypeCaption() {
    return typeCaption;
  }

  @Override
  public String toString() {
    return getTypeCaption();
  }
}
