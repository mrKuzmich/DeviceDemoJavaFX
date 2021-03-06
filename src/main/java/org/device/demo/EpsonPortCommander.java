package org.device.demo;

import com.taliter.fiscal.device.FiscalDeviceSource;
import com.taliter.fiscal.device.epson.EpsonConstants;
import com.taliter.fiscal.device.epson.EpsonFiscalDeviceSource;
import com.taliter.fiscal.port.rxtx.RXTXFiscalPortSource;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Created by Dmitry on 07.10.2015.
 */
public class EpsonPortCommander extends PortCommanderImpl implements EpsonConstants {

  private static final DecimalFormatSymbols defaultDecimalFormatSymbols = new DecimalFormatSymbols() {{setDecimalSeparator('.');}};
  private static final NumberFormat quantityFormat = new DecimalFormat("#############0.000", defaultDecimalFormatSymbols);
  private static final NumberFormat amountFormat = new DecimalFormat("#####0.00", defaultDecimalFormatSymbols);
  private static final NumberFormat taxFormat = new DecimalFormat("#0.00", defaultDecimalFormatSymbols);
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");

  private static final Map<String, String> documentTypeMapping = new HashMap<String, String>() {{
    put("C", "T"); put("L", "L"); put("0", "E"); put("1", "V"); put("2", "D"); put("3", "P"); put("4", "C");
  }};

  private static final Map<String, String> responsibilityMapping = new HashMap<String, String>() {{
    put("T", "U"); put("N", "P"); put("E", "E"); put("A", "N"); put("B", "I"); put("M", "M"); put("S", "T"); put("I", "F");
  }};

  private DocumentOptions documentOptions = null;
  private boolean isTfCommand = false;

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
  public void openFiscalDocument(TypeSelectItem documentType) {
    setIsTfCommand("A".equals(documentType.getType()) || "B".equals(documentType.getType()));
    open();
    try {
      if (isTfCommand()) {
        // Command, Extend command, Name of the buyer Line 1, Name of the buyer line 2,
        // Address of buyer Line 1, Address of buyer Line 2, Address of buyer Line 3
        // Buyer type of document, Number of documents, Responsibility to VAT of the buyer,
        // Lines associated remitos #1, Lines associated remitos #2, Line refund check, for tourist
        doCommand(CMD_OPEN_TF, new byte[2],
                documentOptions != null ? documentOptions.getName() : "", "",
                documentOptions != null ? documentOptions.getAddress() : "", "", "",
                documentOptions != null ? documentTypeMapping.get(documentOptions.getDocument().getType()) : "",
                documentOptions != null ? documentOptions.getNumber() : "",
            responsibilityMapping.get(documentOptions.getConditionTax().getType()), "", "", "");
      } else
        doCommand(CMD_OPEN_FD_EXT, new byte[]{0, 0});
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
    byte extCommand = 0x10;
    // Add Amount - 0; Void Amount - 1;
    if (Controller.OPERATION_TYPES.get(1).equals(operationType)) extCommand = (byte) (extCommand | 0x01);
    if (Controller.QUALIFIER_OPERATION.get(1).equals(qualifierOperation)) extCommand = (byte) (extCommand | 0x06);
    else if (Controller.QUALIFIER_OPERATION.get(2).equals(qualifierOperation)) extCommand = (byte) (extCommand | 0x02);

    open();
    try {
      doCommand(isTfCommand() ? CMD_PRINT_LINE_ITEM_TF : CMD_PRINT_LINE_ITEM_EXT, // Command (0A 02 )
          new byte[]{0x00, extCommand}, // Extended Command
          extraDescription, // Extra Description #1
          "", "", "", // Extra Description #2, 3, 4
          itemName != null ? truncate(itemName, 25) : "", // Item description
          quantity != null ? quantityFormat.format(quantity) : null, // Quantity
          amount != null ? amountFormat.format(amount) : null, // unit Price
          tax != null ? taxFormat.format(tax) : 0, // TAX
          internalTax != null ? taxFormat.format(internalTax) : null, // fixed internal tax
          null, // Excise percentage coefficient
          "", // MTX reference unit
          itemCode, // Item Code MTX (EAN)
          "", // Internal Code
          7, // Code measurement unit
          7// condition code against VAT
          );
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
      doCommand(isTfCommand() ? CMD_CLOSE_TF : CMD_CLOSE_FD_EXT,
          new byte[]{0x00, 0x03}, 0, "", 0, "", 0, "");
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Close Fiscal Document command.");
    } finally {
      close();
    }
  }

  public void cancelDocument() {
    open();
    try {
      doCommand(isTfCommand() ? CMD_CANCEL_DOCUMENT_TF : CMD_CANCEL_DOCUMENT_EXT, new byte[2]);
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Cancel Fiscal Document command.");
    } finally {
      close();
    }
  }

  public void totalTender(String text, Float amount, TypeSelectItem operation) {
    open();
    try {
      doCommand(isTfCommand() ? CMD_TOTAL_TENDER_TF : CMD_TOTAL_TENDER_EXT,
          new byte[]{0x00, 0x00}, // Extended Command
          null, // Extra description of payment #1
          truncate(text, 28), // Extra Description of payment #2
          null, // number of installments
          null, // Detail other payment methods
          null, // Detail coupons
          0, // Payment code
          amount != null ? amountFormat.format(amount) : null// Amount (nnnnnnnn.nn)
          );
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Total Tender command.");
    } finally {
      close();
    }
  }

  public void subtotalFiscalDocument(org.device.demo.TypeSelectItem printingOptions) {
    open();
    try {
      doCommand(isTfCommand() ? CMD_SUBTOTAL_TF : CMD_SUBTOTAL_EXT,
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
      doCommand(isTfCommand() ? CMD_GENERAL_DISCOUNT_TF : CMD_GENERAL_DISCOUNT_EXT,
          new byte[]{0x00, (byte) (Controller.OPERATION_TYPES.get(0).equals(operationType) ? 0x00 : 0x01)},
          truncate(description, 28) ,
          amountFormat.format(amount), // Amount discount or recharge
          "", // tax
          "",// INTERNAL CODE
          ""// CONDITION TAX CODE
          );
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
      doCommand(CMD_DAILY_CLOSE_BY_DATE_EXT,
          new byte[] {0x00, (byte)(Controller.AUDIT_PRINTING_OPTIONS.get(0).equals(printingOptions) ? 0x01 : 0x00)},
          dateFormat.format(start), dateFormat.format(end));
    } catch (Exception e) {
      throw new FiscalPortCommandException(e, "Error executing of Daily Close command.");
    } finally {
      close();
    }
  }

  public void setCustomerData(@Nonnull DocumentOptions documentOptions) {
    this.documentOptions = documentOptions;
  }

  public boolean isTfCommand() {
    return isTfCommand;
  }

  public void setIsTfCommand(boolean isTfCommand) {
    this.isTfCommand = isTfCommand;
  }
}
