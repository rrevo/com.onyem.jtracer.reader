package com.onyem.jtracer.reader.parser.internal;

import javax.annotation.concurrent.Immutable;

@Immutable
class Property {

  private final String key;
  private final String value;

  public Property(String key, String value) {
    this.key = key;
    this.value = value;
  }

  String getKey() {
    return key;
  }

  String getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Property other = (Property) obj;
    if (key != other.key)
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Property [key=" + key + ", value=" + value + "]";
  }

}
