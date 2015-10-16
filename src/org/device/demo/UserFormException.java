package org.device.demo;

/**
 * Created by ִלטענטי on 09.10.2015.
 */
public class UserFormException extends RuntimeException {
  public UserFormException(String message) {
    super(message);
  }

  public UserFormException(Throwable cause) {
    super(cause);
  }

  public UserFormException(String message, Object ... params) {
    super(String.format(message, params));
  }

}
