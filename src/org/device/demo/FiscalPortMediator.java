package org.device.demo;

import com.taliter.fiscal.port.rxtx.RXTXFiscalPort;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by ִלטענטי on 07.10.2015.
 */
public class FiscalPortMediator {
  static ObservableList<String> comPorts = null;
  final ObservableList<Number> baudRates = FXCollections.observableArrayList(
          300L, 1200L, 2400L, 4800L, 9600L, 14400L, 19200L, 28800L, 38400L, 57600L, 115200L, 230400L);

  private StringProperty comPort;
  private IntegerProperty baudRate;
  private BooleanProperty saveLog = new SimpleBooleanProperty(true);

  public FiscalPortMediator() {
  }

  public ObservableList<Integer> getBaudRates() {
    return baudRates;
  }

  public ObservableList<String> getComPorts() {
    if (comPorts == null)
      comPorts = FXCollections.observableArrayList(RXTXFiscalPort.getPortNames());
    return comPorts;
  }

  public boolean getSaveLog() {
    return saveLog.get();
  }

  public BooleanProperty saveLogProperty() {
    return saveLog;
  }

  public String getComPort() {
    return comPort.get();
  }

  public StringProperty comPortProperty() {
    return comPort;
  }

  public int getBaudRate() {
    return baudRate.get();
  }

  public IntegerProperty baudRateProperty() {
    return baudRate;
  }
}
