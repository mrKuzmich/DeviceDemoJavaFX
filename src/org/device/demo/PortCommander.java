package org.device.demo;

import com.sun.istack.internal.NotNull;
import com.taliter.fiscal.device.FiscalPacket;

import java.util.Date;

/**
 * Created by Dmitry on 13.11.2015.
 */
public interface PortCommander {
  public String getComPort();

  public void setComPort(String comPort);

  public int getBaudRate();

  public void setBaudRate(int baudRate);

  public boolean isSaveLog();

  public void setSaveLog(boolean saveLog);

  public FiscalPacket getLastRequest();
  public FiscalPacket getLastResponse();

  public void statusRequest();

  public void openFiscalDocument(TypeSelectItem documentType);

  public void printLineFiscalDocument(String itemName, String itemCode, String extraDescription, Float quantity, Float amount, Float tax,
                                      TypeSelectItem operationType, Float internalTax, TypeSelectItem parameterDisplay,
                                      TypeSelectItem totalPrice, TypeSelectItem qualifierOperation);

  public void printFiscalText(String text);

  public void closeFiscalDocument();

  public void cancelDocument();

  public void totalTender(String text, Float amount, TypeSelectItem operation);

  public void subtotalFiscalDocument(TypeSelectItem printingOptions);

  public void generalDiscountFiscalDocument(String description, Float amount, TypeSelectItem operationType);

  public void openNfdTicket();

  public void printNonFiscalText(String text);

  public void closeNfd();

  public void dailyClose(boolean xReport);

  public void dailyCloseByNumber(Integer start, Integer end, TypeSelectItem printingOptions);

  public void dailyCloseByDate(Date start, Date end, TypeSelectItem printingOptions);

  public void setCustomerData(@NotNull DocumentOptions documentOptions);

  public void setRequestListener(org.device.demo.FiscalPacketListener requestListener);

  public void setResponseListener(org.device.demo.FiscalPacketListener responseListener);
}
