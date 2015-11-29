package org.device.demo;

import com.taliter.fiscal.device.FiscalDevice;
import com.taliter.fiscal.device.FiscalDeviceSource;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.device.epson.EpsonFiscalDeviceSource;
import com.taliter.fiscal.port.rxtx.RXTXFiscalPortSource;
import com.taliter.fiscal.util.LoggerFiscalPortSource;

import java.util.Date;

/**
 * Created by Dmitry on 13.11.2015.
 */
public abstract class PortCommanderImpl implements PortCommander {

  private String comPort;
  private int baudRate = 9600;
  private boolean saveLog = false;
  private FiscalPacketListener requestListener;
  private FiscalPacketListener responseListener;

  private FiscalPacket lastRequest;
  private FiscalPacket lastResponse;

  private FiscalDevice device;

  public PortCommanderImpl() {
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

  public void setRequestListener(org.device.demo.FiscalPacketListener requestListener) {
    this.requestListener = requestListener;
  }

  public void setResponseListener(org.device.demo.FiscalPacketListener responseListener) {
    this.responseListener = responseListener;
  }

  protected abstract FiscalDeviceSource getFiscalDeviceSource(String comPort);

  public void open() {
    try {
      if (comPort == null || comPort.isEmpty()) throw new Exception("Port name is not defined.");
      FiscalDeviceSource deviceSource = getFiscalDeviceSource(getComPort());
      if (isSaveLog()) deviceSource.setPortSource(new LoggerFiscalPortSource(deviceSource.getPortSource(), System.out));
      device = deviceSource.getFiscalDevice();
      device.open();
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error opening Port");
    }
  }

  public void close() {
    try {
      device.close();
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error closing Port.");
    }
  }

  public FiscalPacket getLastRequest() {
    return lastRequest;
  }

  public FiscalPacket getLastResponse() {
    return lastResponse;
  }

  protected void doCommand(int commandId, Object... params) throws Exception {
    FiscalPacket request = device.createFiscalPacket();
    request.setCommandCode(commandId);
    addPacketParams(request, params);
    if (requestListener != null) requestListener.invoke(request);
    lastRequest = request;
    lastResponse = device.execute(request);
    if (responseListener != null) responseListener.invoke(lastResponse);
  }

  protected void addPacketParams(FiscalPacket packet, Object... params) {
    int paramsIndex = packet.getSize();
    for (Object parameter : params) {
      if (parameter != null) {
        if (parameter instanceof String)
          packet.setString(paramsIndex, (String) parameter);
        else if (parameter instanceof Integer)
          packet.setInt(paramsIndex, (Integer) parameter);
        else if (parameter instanceof Date)
          packet.setDate(paramsIndex, (Date) parameter);
        else if (parameter instanceof org.device.demo.TypeSelectItem)
          packet.setString(paramsIndex, ((TypeSelectItem) parameter).getType() != null ?
              ((TypeSelectItem) parameter).getType().toString() : "");
        else if (parameter instanceof byte[])
          packet.set(paramsIndex, (byte[])parameter);
        else throw new FiscalPortCommandException("Parametr of %s type is not supported.",
              parameter != null ? parameter.getClass().getName() : "null");
      }
      paramsIndex++;
    }

  }

  protected String truncate(String value, int length) {
    if (value.length() > length)
      return value.substring(0, length);
    return value;
  }
}
