package org.device.demo;

import com.sun.istack.internal.NotNull;
import com.taliter.fiscal.device.FiscalDevice;
import com.taliter.fiscal.device.FiscalDeviceSource;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.device.hasar.*;
import com.taliter.fiscal.port.rxtx.RXTXFiscalPortSource;
import com.taliter.fiscal.util.LoggerFiscalPortSource;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Date;

/**
 * Created by ������� on 07.10.2015.
 */
public class HasarPortCommander extends PortCommanderImpl implements HasarConstants {

  private static final DecimalFormatSymbols defaultDecimalFormatSymbols = new DecimalFormatSymbols() {{setDecimalSeparator('.');}};
  private static final NumberFormat quantityFormat = new DecimalFormat("###############0.000", defaultDecimalFormatSymbols);
  private static final NumberFormat amountFormat = new DecimalFormat("######0.00", defaultDecimalFormatSymbols);
  private static final NumberFormat taxFormat = new DecimalFormat("0000", defaultDecimalFormatSymbols);

  public HasarPortCommander() {
    super();
  }

  @Override
  protected FiscalDeviceSource getFiscalDeviceSource(String comPort) {
    return new HasarFiscalDeviceSource(new RXTXFiscalPortSource(comPort));
  }

  @Override
  public void statusRequest() {
    open();
    try {
      doCommand(CMD_STATUS_REQUEST);
//      doCommand(CMD_GET_INIT_DATA);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Status Request command.");
    } finally {
      close();
    }
  }

  @Override
  public void openFiscalDocument(TypeSelectItem documentType) {
    open();
    try {
      doCommand(CMD_OPEN_FD, documentType);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Open Fiscal Report command.");
    } finally {
      close();
    }
  }

  @Override
  public void printLineFiscalDocument(String itemName, String itemCode, String extraDescription, Float quantity, Float amount, Float tax,
                                      TypeSelectItem operationType, Float internalTax, TypeSelectItem parameterDisplay,
                                      TypeSelectItem totalPrice, TypeSelectItem qualifierOperation) {
    if (Controller.IT_SALE.equals(qualifierOperation)) {
      open();
      try {
        doCommand(CMD_PRINT_LINE_ITEM,
            itemName != null ? truncate(itemName, 25) : null,
            quantity != null ? quantityFormat.format(quantity) : null,
            amount != null ? amountFormat.format(amount) : null,
            tax != null ? taxFormat.format(tax * 100) : null,
            operationType,
            internalTax != null ? taxFormat.format(internalTax) : null,
            parameterDisplay,
            totalPrice);
      } catch (Exception e) {
        throw new FiscalPortCommandException(e, "Error executing of Print Line Item command.");
      } finally {
        close();
      }
    } else
      returnRecharge(itemName, amount, tax, operationType, internalTax, null, totalPrice, qualifierOperation);
  }

  private void returnRecharge(String description, Float amountDiscount, Float tax, TypeSelectItem operationType,
                             Float internalTax, String displayParameters, TypeSelectItem totalPrice, TypeSelectItem discount) {
    open();
    try {
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
    } finally {
      close();
    }
  }

  @Override
  public void printFiscalText(String text) {
    open();
    try {
      doCommand(CMD_PRINT_FISCAL_TEXT, truncate(text, 30));
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Print Fiscal Text command.");
    } finally {
      close();
    }
  }

  @Override
  public void closeFiscalDocument() {
    open();
    try {
      doCommand(CMD_CLOSE_FD, 1);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Close Fiscal Document command.");
    } finally {
      close();
    }
  }

  @Override
  public void cancelDocument() {
    open();
    try {
      doCommand(CMD_CANCEL_DOCUMENT);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Cancel Fiscal Document command.");
    } finally {
      close();
    }
  }

  @Override
  public void totalTender(String text, Float amount, TypeSelectItem operation) {
    open();
    try {
      doCommand(CMD_TOTAL_TENDER,
          truncate(text, 28),
          amount != null ? amountFormat.format(amount) : null,
          operation); //todo lacks two parameters
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Total Tender command.");
    } finally {
      close();
    }
  }

  @Override
  public void subtotalFiscalDocument(TypeSelectItem printingOptions) {
    open();
    try {
      doCommand(CMD_SUBTOTAL, printingOptions, " ", 0); // 0 it display parameter
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Subtotal Fiscal Document command.");
    } finally {
      close();
    }
  }

  @Override
  public void generalDiscountFiscalDocument(String description, Float amount, TypeSelectItem operationType) {
    open();
    try {
      doCommand(CMD_GENERAL_DISCOUNT,
          truncate(description, 28),
          amountFormat.format(amount),
          operationType);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Cancel Fiscal Document command.");
    } finally {
      close();
    }
  }

  @Override
  public void openNfdTicket() {
    open();
    try {
      doCommand(CMD_OPEN_NFD_TICKET);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Open Non Fiscal Document Ticket command.");
    } finally {
      close();
    }
  }

  @Override
  public void printNonFiscalText(String text) {
    open();
    try {
      doCommand(CMD_PRINT_NON_FISCAL_TEXT, truncate(text, 80));
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Print Non Fiscal Text command.");
    } finally {
      close();
    }
  }

  @Override
  public void closeNfd() {
    open();
    try {
      doCommand(CMD_CLOSE_NFD);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Close Non Fiscal Document command.");
    } finally {
      close();
    }
  }

  @Override
  public void dailyClose(boolean xReport) {
    open();
    try {
      doCommand(CMD_DAILY_CLOSE, xReport ? "X" : "Z");
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Daily Close command.");
    } finally {
      close();
    }
  }

  @Override
  public void dailyCloseByNumber(Integer start, Integer end, TypeSelectItem printingOptions) {
    open();
    try {
      doCommand(CMD_DAILY_CLOSE_BY_NUMBER, start, end, printingOptions);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Daily Close command.");
    } finally {
      close();
    }
  }

  @Override
  public void dailyCloseByDate(Date start, Date end, TypeSelectItem printingOptions) {
    open();
    try {
      doCommand(CMD_DAILY_CLOSE_BY_DATE, start, end, printingOptions);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Daily Close command.");
    } finally {
      close();
    }
  }

  @Override
  public void setCustomerData(@NotNull DocumentOptions documentOptions) {
    open();
    try {
      doCommand(CMD_SET_CUSTOMER_DATA, truncate(documentOptions.getName(), 45), truncate(documentOptions.getNumber(), 11),
              documentOptions.getConditionTax(), documentOptions.getDocument() != null ? documentOptions.getDocument() : null,
              documentOptions.getAddress());
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Set Customer Data command.");
    } finally {
      close();
    }
  }
}
