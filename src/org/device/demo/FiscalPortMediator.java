package org.device.demo;

import com.taliter.fiscal.device.FiscalDevice;
import com.taliter.fiscal.device.FiscalDeviceSource;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.device.hasar.*;
import com.taliter.fiscal.port.rxtx.RXTXFiscalPort;
import com.taliter.fiscal.port.rxtx.RXTXFiscalPortSource;
import com.taliter.fiscal.util.LoggerFiscalDeviceEventHandler;
import com.taliter.fiscal.util.LoggerFiscalPortSource;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by ִלטענטי on 07.10.2015.
 */
public class FiscalPortMediator implements HasarConstants {
  static ObservableList<String> comPorts = null;
  final static ObservableList<Number> baudRates = FXCollections.observableArrayList(
          (Number) 300, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 38400, 57600, 115200, 230400);

  private StringProperty comPort = new SimpleStringProperty();
  private IntegerProperty baudRate = new SimpleIntegerProperty(9600);
  private BooleanProperty saveLog = new SimpleBooleanProperty(true);
  private StringProperty printerRequest = new SimpleStringProperty();
  private StringProperty printerStatus = new SimpleStringProperty();

  private FiscalDevice device;

  public FiscalPortMediator() {
  }

  public ObservableList<Number> getBaudRates() {
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

  public String getPrinterRequest() {
    return printerRequest.get();
  }

  public StringProperty printerRequestProperty() {
    return printerRequest;
  }

  public String getPrinterStatus() {
    return printerStatus.get();
  }

  public StringProperty printerStatusProperty() {
    return printerStatus;
  }

  public void init() throws Exception {
    FiscalDeviceSource deviceSource = new HasarFiscalDeviceSource(new RXTXFiscalPortSource(comPort.getValue()));
    deviceSource.setPortSource(new LoggerFiscalPortSource(deviceSource.getPortSource(), System.out));
    device = deviceSource.getFiscalDevice();
    device.setEventHandler(new LoggerFiscalDeviceEventHandler(System.out));
    device.open();
  }

  public void accept() throws Exception {
    init();
    FiscalPacket request = device.createFiscalPacket();
    request.setCommandCode(CMD_STATUS_REQUEST);
    printerRequest.setValue(request.toHexString());
    FiscalPacket response = device.execute(request);

  }
}
