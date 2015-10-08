package org.device.demo;

import com.sun.org.apache.xpath.internal.operations.Bool;
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
public class FiscalPortCommander implements HasarConstants {

  private String comPort;
  private int baudRate = 9600;
  private boolean saveLog = false;

  private FiscalDevice device;

  public FiscalPortCommander() {
  }

  public String getComPort() {
    return comPort;
  }

  public void setComPort(String comPort) {
    this.comPort = comPort;
  }

  public int getBaudRate() {
    return baudRate;
  }

  public void setBaudRate(int baudRate) {
    this.baudRate = baudRate;
  }

  public boolean isSaveLog() {
    return saveLog;
  }

  public void setSaveLog(boolean saveLog) {
    this.saveLog = saveLog;
  }

  public void open() throws Exception {
    if (comPort == null || comPort.isEmpty()) throw new Exception("Port name is not defined.");
    FiscalDeviceSource deviceSource = new HasarFiscalDeviceSource(new RXTXFiscalPortSource(comPort));
    if (isSaveLog()) deviceSource.setPortSource(new LoggerFiscalPortSource(deviceSource.getPortSource(), System.out));
    device = deviceSource.getFiscalDevice();
    device.open();
  }

  public void statusRequest() throws Exception {
    open();
    FiscalPacket request = device.createFiscalPacket();
    request.setCommandCode(CMD_STATUS_REQUEST);
    FiscalPacket response = device.execute(request);
    request = device.createFiscalPacket();
    request.setCommandCode(CMD_GET_INIT_DATA);
    response = device.execute(request);
    request = device.createFiscalPacket();
    request.setCommandCode(CMD_GET_FISCAL_DEVICE_VERSION);
    response = device.execute(request);
  }
}
