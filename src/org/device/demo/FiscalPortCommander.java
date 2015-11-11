package org.device.demo;

import com.sun.istack.internal.NotNull;
import com.taliter.fiscal.device.FiscalDevice;
import com.taliter.fiscal.device.FiscalDeviceSource;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.device.epson.EpsonFiscalDeviceSource;
import com.taliter.fiscal.device.epson.*;
import com.taliter.fiscal.port.rxtx.RXTXFiscalPortSource;
import com.taliter.fiscal.util.LoggerFiscalPortSource;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Date;

/**
 * Created by ������� on 07.10.2015.
 */
public class FiscalPortCommander implements EpsonConstants {

  private static final DecimalFormatSymbols defaultDecimalFormatSymbols = new DecimalFormatSymbols() {{setDecimalSeparator('.');}};
  private static final NumberFormat quantityFormat = new DecimalFormat("###############0.000", defaultDecimalFormatSymbols);
  private static final NumberFormat amountFormat = new DecimalFormat("######0.00", defaultDecimalFormatSymbols);
  private static final NumberFormat taxFormat = new DecimalFormat("0000", defaultDecimalFormatSymbols);

  private String comPort;
  private int baudRate = 9600;
  private boolean saveLog = false;
  private FiscalPacketListener requestListener;
  private FiscalPacketListener responseListener;

  private FiscalPacket lastRequest;
  private FiscalPacket lastResponse;

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
      FiscalDeviceSource deviceSource = new EpsonFiscalDeviceSource(new RXTXFiscalPortSource(comPort));
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

  public void statusRequest() {
    open();
    try {
      doCommand(CMD_STATUS_REQUEST, new byte[]{0,0});
      doCommand(CMD_GET_INFORMATION, new byte[]{0,0});
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Status Request command.");
    } finally {
      close();
    }
  }

  public void openFiscalDocument(TypeSelectItem documentType) {
    open();
    try {
      doCommand(CMD_GET_INFORMATION, new byte[]{0,0});
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Open Fiscal Report command.");
    } finally {
      close();
    }
  }

  public void printLineFiscalDocument(String itemName, Float quantity, Float amount, Float tax,
                                      TypeSelectItem operationType, Float internalTax, TypeSelectItem parameterDisplay, TypeSelectItem totalPrice) {
    open();
    try {
/*
      doCommand(CMD_PRINT_LINE_ITEM,
              itemName != null ? truncate(itemName, 25) : null,
              quantity != null ? quantityFormat.format(quantity) : null,
              amount != null ? amountFormat.format(amount) : null,
              tax != null ? taxFormat.format(tax*100) : null,
              operationType,
              internalTax != null ? taxFormat.format(internalTax) : null,
              parameterDisplay,
              totalPrice);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Print Line Item command.");
*/
    } finally {
      close();
    }
  }

  public void returnRecharge(String description, Float amountDiscount, Float tax, TypeSelectItem operationType,
                             Float internalTax, String displayParameters, TypeSelectItem totalPrice, TypeSelectItem discount) {
    open();
    try {
/*
      doCommand(CMD_RETURN_RECHARGE,
              description != null ? truncate(description, 23) : null,
              amountDiscount != null ? amountFormat.format(amountDiscount) : null,
              tax != null ? taxFormat.format(tax * 100) : null,
              operationType,
              internalTax != null ? taxFormat.format(internalTax) : null,
              displayParameters != null ? truncate(displayParameters, 1) : null,
              totalPrice,
              discount);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Return Recharge command.");
*/
    } finally {
      close();
    }
  }

  public void printFiscalText(String text) {
    open();
    try {
/*
      doCommand(CMD_PRINT_FISCAL_TEXT, truncate(text, 30));
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Print Fiscal Text command.");
*/
    } finally {
      close();
    }
  }

  public void closeFiscalDocument() {
    open();
    try {
/*
      doCommand(CMD_CLOSE_FD, 1);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Close Fiscal Document command.");
*/
    } finally {
      close();
    }
  }

  public void cancelDocument() {
    open();
    try {
/*
      doCommand(CMD_CANCEL_DOCUMENT);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Cancel Fiscal Document command.");
*/
    } finally {
      close();
    }
  }

  public void totalTender(String text, Float amount, TypeSelectItem operation) {
    open();
    try {
/*
      doCommand(CMD_TOTAL_TENDER,
          truncate(text, 28),
          amount != null ? amountFormat.format(amount) : null,
          operation); //todo lacks two parameters
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Total Tender command.");
*/
    } finally {
      close();
    }
  }

  public void subtotalFiscalDocument(TypeSelectItem printingOptions) {
    open();
    try {
/*
      doCommand(CMD_SUBTOTAL, printingOptions, " ", 0); // 0 it display parameter
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Subtotal Fiscal Document command.");
*/
    } finally {
      close();
    }
  }

  public void generalDiscountFiscalDocument(String description, Float amount, TypeSelectItem operationType) {
    open();
    try {
/*
      doCommand(CMD_GENERAL_DISCOUNT,
          truncate(description, 28),
          amountFormat.format(amount),
          operationType);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Cancel Fiscal Document command.");
*/
    } finally {
      close();
    }
  }

  public void openNfdTicket() {
    open();
    try {
/*
      doCommand(CMD_OPEN_NFD_TICKET);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Open Non Fiscal Document Ticket command.");
*/
    } finally {
      close();
    }
  }

  public void printNonFiscalText(String text) {
    open();
    try {
/*
      doCommand(CMD_PRINT_NON_FISCAL_TEXT, truncate(text, 80));
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Print Non Fiscal Text command.");
*/
    } finally {
      close();
    }
  }

  public void closeNfd() {
    open();
    try {
/*
      doCommand(CMD_CLOSE_NFD);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Close Non Fiscal Document command.");
*/
    } finally {
      close();
    }
  }

  public void dailyClose(boolean xReport) {
    open();
    try {
/*
      doCommand(CMD_DAILY_CLOSE, xReport ? "X" : "Z");
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Daily Close command.");
*/
    } finally {
      close();
    }
  }

  public void dailyCloseByNumber(Integer start, Integer end, TypeSelectItem printingOptions) {
    open();
    try {
/*
      doCommand(CMD_DAILY_CLOSE_BY_NUMBER, start, end, printingOptions);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Daily Close command.");
*/
    } finally {
      close();
    }
  }

  public void dailyCloseByDate(Date start, Date end, TypeSelectItem printingOptions) {
    open();
    try {
/*
      doCommand(CMD_DAILY_CLOSE_BY_DATE, start, end, printingOptions);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Daily Close command.");
*/
    } finally {
      close();
    }
  }

  public void setCustomerData(@NotNull DocumentOptions documentOptions) {
    open();
    try {
/*
      doCommand(CMD_SET_CUSTOMER_DATA, truncate(documentOptions.getName(), 45), truncate(documentOptions.getNumber(), 11),
              documentOptions.getConditionTax(), documentOptions.getDocument() != null ? documentOptions.getDocument() : null,
              documentOptions.getAddress());
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Set Customer Data command.");
*/
    } finally {
      close();
    }
  }

  private void doCommand(int commandId, Object... params) throws Exception {
    FiscalPacket request = device.createFiscalPacket();
    request.setCommandCode(commandId);
    addPacketParams(request, params);
    if (requestListener != null) requestListener.invoke(request);
    lastRequest = request;
    lastResponse = device.execute(request);
    if (responseListener != null) responseListener.invoke(lastResponse);
  }

  private void addPacketParams(FiscalPacket packet, Object... params) {
    int paramsIndex = packet.getSize();
    for (Object parameter : params) {
      if (parameter != null) {
        if (parameter instanceof String)
          packet.setString(paramsIndex, (String) parameter);
        else if (parameter instanceof Integer)
          packet.setInt(paramsIndex, (Integer) parameter);
        else if (parameter instanceof Date)
          packet.setDate(paramsIndex, (Date) parameter);
        else if (parameter instanceof TypeSelectItem)
          packet.setString(paramsIndex, ((TypeSelectItem) parameter).getType());
        else if (parameter instanceof byte[])
          packet.set(paramsIndex, (byte[])parameter);
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
