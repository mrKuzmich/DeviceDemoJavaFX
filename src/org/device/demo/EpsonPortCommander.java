package org.device.demo;

import com.sun.istack.internal.NotNull;
import com.taliter.fiscal.device.FiscalDevice;
import com.taliter.fiscal.device.FiscalDeviceSource;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.device.epson.*;
import com.taliter.fiscal.port.rxtx.RXTXFiscalPortSource;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dmitry on 07.10.2015.
 */
public class EpsonPortCommander extends PortCommanderImpl implements EpsonConstants {

  private static final DecimalFormatSymbols defaultDecimalFormatSymbols = new DecimalFormatSymbols() {{setDecimalSeparator('.');}};
  private static final NumberFormat quantityFormat = new DecimalFormat("###############0", defaultDecimalFormatSymbols);
  private static final NumberFormat amountFormat = new DecimalFormat("######0", defaultDecimalFormatSymbols);
  private static final NumberFormat taxFormat = new DecimalFormat("0000", defaultDecimalFormatSymbols);
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");

  public EpsonPortCommander() {
    super();
  }

  @Override
  protected FiscalDeviceSource getFiscalDeviceSource(String comPort) {
    return new EpsonFiscalDeviceSource(new RXTXFiscalPortSource(comPort));
  }

  @Override
  public void statusRequest() {
    open();
    try {
      doCommand(CMD_STATUS_REQUEST_EXT, new byte[]{0,0});
      doCommand(CMD_GET_INFORMATION_EXT, new byte[]{0,0});
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Status Request command.");
    } finally {
      close();
    }
  }

  @Override
  public void openFiscalDocument(org.device.demo.TypeSelectItem documentType) {
    open();
    try {
      doCommand(CMD_OPEN_FD_EXT, new byte[]{0,0});
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Open Fiscal Report command.");
    } finally {
      close();
    }
  }

  @Override
  public void printLineFiscalDocument(String itemName, String extraDescription, Float quantity, Float amount, Float tax,
                                      TypeSelectItem operationType, Float internalTax, TypeSelectItem parameterDisplay,
                                      TypeSelectItem totalPrice, TypeSelectItem qualifierOperation) {
    byte extCommand = 0x00;
    // Add Amount - 0; Void Amount - 1;
    if (Controller.OPERATION_TYPES.get(1).equals(operationType)) extCommand = (byte) (extCommand | 0x01);
    if (Controller.QUALIFIER_OPERATION.get(1).equals(qualifierOperation)) extCommand = (byte) (extCommand | 0x06);
    else if (Controller.QUALIFIER_OPERATION.get(2).equals(qualifierOperation)) extCommand = (byte) (extCommand | 0x02);

    open();
    try {
      doCommand(CMD_PRINT_LINE_ITEM_EXT, new byte[]{0x00, extCommand}, extraDescription, "", "", "",
              itemName != null ? truncate(itemName, 25) : "",
              quantity != null ? quantityFormat.format(quantity) : null,
              amount != null ? amountFormat.format(amount) : null,
              tax != null ? taxFormat.format(tax*100) : 0,
              internalTax != null ? taxFormat.format(internalTax) : 0,
              0);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Print Line Item command.");
    } finally {
      close();
    }
  }

  @Override
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
      doCommand(CMD_CLOSE_FD_EXT, new byte[]{0x00, 0x03}, 0, "", 0, "", 0, "");
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Close Fiscal Document command.");
    } finally {
      close();
    }
  }

  public void cancelDocument() {
    open();
    try {
      doCommand(CMD_CANCEL_DOCUMENT_EXT, new byte[2]);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Cancel Fiscal Document command.");
    } finally {
      close();
    }
  }

  public void totalTender(String text, Float amount, TypeSelectItem operation) {
    open();
    try {
      doCommand(CMD_TOTAL_TENDER_EXT,
          new byte[]{0x00, 0x00},
          null,
          truncate(text, 28),
          amount != null ? amountFormat.format(amount) : null);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Total Tender command.");
    } finally {
      close();
    }
  }

  public void subtotalFiscalDocument(org.device.demo.TypeSelectItem printingOptions) {
    open();
    try {
      doCommand(CMD_SUBTOTAL_EXT,
          new byte[]{0x00, (byte) (Controller.AUDIT_PRINTING_OPTIONS.get(0).equals(printingOptions) ? 0x00 : 0x01)});
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Subtotal Fiscal Document command.");
    } finally {
      close();
    }
  }

  public void generalDiscountFiscalDocument(String description, Float amount, org.device.demo.TypeSelectItem operationType) {
    open();
    try {
      doCommand(CMD_GENERAL_DISCOUNT_EXT,
          new byte[]{0x00, (byte) (Controller.OPERATION_TYPES.get(0).equals(operationType) ? 0x00 : 0x01)},
          truncate(description, 28) ,
          amountFormat.format(amount));
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Cancel Fiscal Document command.");
    } finally {
      close();
    }
  }

  public void openNfdTicket() {
    open();
    try {
      doCommand(CMD_OPEN_NFD_TICKET_EXT, new byte[2]);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Open Non Fiscal Document Ticket command.");
    } finally {
      close();
    }
  }

  public void printNonFiscalText(String text) {
    open();
    try {
      doCommand(CMD_PRINT_NON_FISCAL_TEXT_EXT, new byte[2], truncate(text, 40));
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Print Non Fiscal Text command.");
    } finally {
      close();
    }
  }

  public void closeNfd() {
    open();
    try {
      doCommand(CMD_CLOSE_NFD_EXT, new byte[2]);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Close Non Fiscal Document command.");
    } finally {
      close();
    }
  }

  public void dailyClose(boolean xReport) {
    open();
    try {
      doCommand(xReport ? CMD_DAILY_CLOSE_X_EXT : CMD_DAILY_CLOSE_Z_EXT, new byte[]{0x00, 0x01});
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Daily Close command.");
    } finally {
      close();
    }
  }

  public void dailyCloseByNumber(Integer start, Integer end, org.device.demo.TypeSelectItem printingOptions) {
    open();
    try {
      doCommand(CMD_DAILY_CLOSE_BY_NUMBER_EXT,
          new byte[] {0x00, (byte)(Controller.AUDIT_PRINTING_OPTIONS.get(0).equals(printingOptions) ? 0x01 : 0x00)},
          start, end);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Daily Close command.");
    } finally {
      close();
    }
  }

  public void dailyCloseByDate(Date start, Date end, org.device.demo.TypeSelectItem printingOptions) {
    open();
    try {
      doCommand(CMD_DAILY_CLOSE_BY_DATE,
          new byte[] {0x00, (byte)(Controller.AUDIT_PRINTING_OPTIONS.get(0).equals(printingOptions) ? 0x01 : 0x00)},
          dateFormat.format(start), dateFormat.format(end));
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Daily Close command.");
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
}
