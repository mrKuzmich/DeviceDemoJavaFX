package org.device.demo;

import com.taliter.fiscal.device.FiscalDevice;
import com.taliter.fiscal.device.FiscalDeviceSource;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.device.hasar.*;
import com.taliter.fiscal.port.rxtx.RXTXFiscalPortSource;
import com.taliter.fiscal.util.LoggerFiscalPortSource;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 * Created by ������� on 07.10.2015.
 */
public class FiscalPortCommander implements HasarConstants {

  private static final DecimalFormatSymbols defaultDecimalFormatSymbols = new DecimalFormatSymbols() {{setDecimalSeparator('.');}};
  private static final NumberFormat quantityFormat = new DecimalFormat("###############0.000", defaultDecimalFormatSymbols);
  private static final NumberFormat amountFormat = new DecimalFormat("######0.00", defaultDecimalFormatSymbols);
  private static final NumberFormat taxFormat = new DecimalFormat("###0", defaultDecimalFormatSymbols);

  private String comPort;
  private int baudRate = 9600;
  private boolean saveLog = false;
  private FiscalPacketListener requestListener;
  private FiscalPacketListener responseListener;

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

  public void setRequestListener(FiscalPacketListener requestListener) {
    this.requestListener = requestListener;
  }

  public void setResponseListener(FiscalPacketListener responseListener) {
    this.responseListener = responseListener;
  }

  public void open() {
    try {
      if (comPort == null || comPort.isEmpty()) throw new Exception("Port name is not defined.");
      FiscalDeviceSource deviceSource = new HasarFiscalDeviceSource(new RXTXFiscalPortSource(comPort));
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

  public void statusRequest() {
    open();
    try {
      doCommand(CMD_STATUS_REQUEST);
      doCommand(CMD_GET_INIT_DATA);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Status Request command.");
    } finally {
      close();
    }
  }

  public void openFiscalDocument(TypeSelectItem documentType) {
    open();
    try {
      doCommand(CMD_OPEN_FD, documentType != null ? documentType.getType() : "T");
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Open Fiscal Report command.");
    } finally {
      close();
    }
  }

  public void printLineFiscalDocument(String itemName, Float quantity, Float amount, Float tax,
                                      TypeSelectItem operationType, Float internalTax, TypeSelectItem parameterDisplay, Boolean totalPrice) {
    open();
    try {
      doCommand(CMD_PRINT_LINE_ITEM,
              itemName != null ? truncate(itemName, 25) : null,
              quantity != null ? quantityFormat.format(quantity) : null,
              amount != null ? amountFormat.format(amount) : null,
              tax != null ? taxFormat.format(tax * 100) : null,
              operationType != null ? operationType.getType() : null,
              internalTax != null ? taxFormat.format(internalTax) : null,
              parameterDisplay != null ? parameterDisplay.getType() : null,
              totalPrice != null && totalPrice ? "T" : "b"
      );
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Print Line Item command.");
    } finally {
      close();
    }
  }

  public void returRecharge(String description, Float amountDiscount, Float tax, TypeSelectItem operationType,
                            Float internalTax, String displayParameters, Boolean totalPrice, Boolean discount) {
    open();
    try {
      doCommand(CMD_RETURN_RECHARGE,
              description != null ? truncate(description, 23) : null,
              amountDiscount != null ? amountFormat.format(amountDiscount) : null,
              tax != null ? taxFormat.format(tax * 100) : null,
              operationType != null ? operationType.getType() : null,
              internalTax != null ? taxFormat.format(internalTax) : null,
              displayParameters != null ? truncate(displayParameters, 1) : null,
              totalPrice != null && totalPrice ? "T" : "*",
              discount != null && discount ? "B" : "*"
              );
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Return Recharge command.");
    } finally {
      close();
    }
  }

  public void closeFiscalDocument() {
    try {
      open();
      doCommand(CMD_CLOSE_FD, 1);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Close Fiscal Document command.");
    } finally {
      close();
    }
  }

  private void doCommand(int commandId, Object ... params) throws Exception {
    FiscalPacket request = device.createFiscalPacket();
    request.setCommandCode(commandId);
    addPacketParams(request, params);
    if (requestListener != null) requestListener.invoke(request);
      FiscalPacket response = device.execute(request);
    if (responseListener != null) responseListener.invoke(response);
  }

  private void addPacketParams(FiscalPacket packet, Object... params) {
    int paramsIndex = packet.getSize();
    for (Object parameter : params) {
      if (parameter != null) {
        if (parameter instanceof String)
          packet.setString(paramsIndex, (String) parameter);
        else if (parameter instanceof Integer)
          packet.setInt(paramsIndex, (Integer) parameter);
        else throw new FiscalPortCommandException("Parametr of %s type is not supported.",
                  parameter != null ? parameter.getClass().getName() : "null");
      }
      paramsIndex++;
    }

  }

  private String truncate(String value, int length) {
    if (value.length() > length)
      return value.substring(0, length);
    return value;
  }

  public interface FiscalPacketListener {
    void invoke(FiscalPacket packet);
  }


  public static class TypeSelectItem {
    private String type;
    private String typeCaption;

    public TypeSelectItem(String type, String typeCaption) {
      this.type = type;
      this.typeCaption = typeCaption;
    }

    public String getType() {
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
}
