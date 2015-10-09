package org.device.demo;

/**
 * Created by ִלטענטי on 09.10.2015.
 */
public class FiscalPortCommandException extends RuntimeException {
  public FiscalPortCommandException(String message) {
    super(message);
  }

  public FiscalPortCommandException(String message, Throwable cause) {
    super(message, cause);
  }

  public FiscalPortCommandException(String messageTpl, Object ... values) {
    this(String.format(messageTpl, values));
  }

  public FiscalPortCommandException(Throwable cause, String messageTpl, Object ... values) {
    this(String.format(messageTpl, values), cause);
  }
}
