package org.device.demo;

import com.sun.istack.internal.Nullable;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.port.rxtx.RXTXFiscalPort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import javax.xml.soap.Text;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class Controller {
  static ObservableList<String> comPorts = null;
  final static ObservableList<Integer> baudRates = FXCollections.observableArrayList(
          300, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 38400, 57600, 115200, 230400);
  final static ObservableList<FiscalPortCommander.TypeSelectItem> OPERATION_TYPES = FXCollections.observableArrayList(
          new FiscalPortCommander.TypeSelectItem("M", "Add Amount"),
          new FiscalPortCommander.TypeSelectItem("m", "Void Amount"));
  final static ObservableList<FiscalPortCommander.TypeSelectItem> DOCUMENT_TYPES = FXCollections.observableArrayList(
          new FiscalPortCommander.TypeSelectItem("T", "Ticket"),
          new FiscalPortCommander.TypeSelectItem("A", "Ticket Bill \"A\""),
          new FiscalPortCommander.TypeSelectItem("B", "Ticket Bill \"B/C\""),
          new FiscalPortCommander.TypeSelectItem("D", "Debit note \"A\""),
          new FiscalPortCommander.TypeSelectItem("E", "Debit note \"B/C\""));
  final static ObservableList<FiscalPortCommander.TypeSelectItem> DISPLAY_PARAMETERS = FXCollections.observableArrayList(
          new FiscalPortCommander.TypeSelectItem("0", "No changes"),
          new FiscalPortCommander.TypeSelectItem("1", "Write display"),
          new FiscalPortCommander.TypeSelectItem("2", "Increase repetitions subfield"));
  final static ObservableList<FiscalPortCommander.TypeSelectItem> QUALIFIER_PRICE_OPERATION = FXCollections.observableArrayList(
          new FiscalPortCommander.TypeSelectItem("T", "Total price"),
          new FiscalPortCommander.TypeSelectItem("X", "Net price"));
  final static ObservableList<FiscalPortCommander.TypeSelectItem> SUBTOTAL_PRINTING_OPTIONS = FXCollections.observableArrayList(
          new FiscalPortCommander.TypeSelectItem("P", "Print subtotal"),
          new FiscalPortCommander.TypeSelectItem("X", "No printing"));
  final static ObservableList<FiscalPortCommander.TypeSelectItem> TENDER_OPERATION = FXCollections.observableArrayList(
          new FiscalPortCommander.TypeSelectItem("C", "Cancel"),
          new FiscalPortCommander.TypeSelectItem("P", "Payment Total/Partial"),
          new FiscalPortCommander.TypeSelectItem("R", "Pay Back"));
  final static ObservableList<FiscalPortCommander.TypeSelectItem> AUDIT_PRINTING_OPTIONS = FXCollections.observableArrayList(
          new FiscalPortCommander.TypeSelectItem("D", "Print subtotal"),
          new FiscalPortCommander.TypeSelectItem("T", "No printing"));

  final static FiscalPortCommander.TypeSelectItem IT_SALE =
          new FiscalPortCommander.TypeSelectItem("X", "Normal Sale");
  final static FiscalPortCommander.TypeSelectItem IT_RECHARGE_DISCOUNT =
          new FiscalPortCommander.TypeSelectItem("B", "Recharge/Discount");
  final static FiscalPortCommander.TypeSelectItem IT_BOTTLE_DISCOUNT =
          new FiscalPortCommander.TypeSelectItem("X", "Bottle");
  final static ObservableList<FiscalPortCommander.TypeSelectItem> QUALIFIER_OPERATION = FXCollections.observableArrayList(
          IT_SALE, IT_RECHARGE_DISCOUNT, IT_BOTTLE_DISCOUNT);

  private FiscalPortCommander fiscalPort;
  @FXML
  private ChoiceBox<String> cbComPort;
  @FXML
  private ChoiceBox<Integer> cbBaudRates;
  @FXML
  private CheckBox cbSaveLog;
  @FXML
  private TextArea tfPrinterRequest;
  @FXML
  private TextArea tfPrinterStatus;

  //FiscalTestFields
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbFTestDocumentType;
  @FXML
  private TextField tfFTestItemName;
  @FXML
  private TextField tfFTestQuantity;
  @FXML
  private TextField tfFTestPriceAmount;
  @FXML
  private TextField tfFTestTax;
  @FXML
  private TextField tfFTestInternalAmount;
  @FXML
  private TextField tfFTestRechargeDiscountName;
  @FXML
  private TextField tfFTestRechargeDiscountAmount;
  @FXML
  private TextField tfFTestExtraText;
  @FXML
  private TextField tfFTestPaymentName;
  @FXML
  private TextField tfFTestPaymentAmount;
  @FXML
  private TextArea tfNonFiscalText;
  @FXML
  private RadioButton rbFiscalAuditByZ;
  @FXML
  private RadioButton rbFiscalAuditByDate;
  @FXML
  private TextField tfFiscalAuditNumStart;
  @FXML
  private TextField tfFiscalAuditNumEnd;
  @FXML
  private DatePicker dpFiscalAuditDateStart;
  @FXML
  private DatePicker dpFiscalAuditDateEnd;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbFTestOperationType;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbFTestDisplayParameter;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbFTestQualifierPriceOperation;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbFTestQualifierOperation;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbFTestSubtotalOption;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbFTestGeneralAddOperation;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbFTestTenderOperation;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbReportsPrintingOptions;

  private Main mainApplication;

  private DocumentOptions documentOptions;

  public void setMainApplication(Main mainApplication) {
    this.mainApplication = mainApplication;
  }

  @FXML
  private void actionDemoDeviceExit() {
    Platform.exit();
  }

  @FXML
  private void actionPrinterStatus() {
    initFiscalPort();
    fiscalPort.statusRequest();
  }

  @FXML
  private void actionOptionAccept() {
    initFiscalPort();
    fiscalPort.statusRequest();
  }

  @FXML
  private void actionSetCustomerData() {
    try {
      final DocumentOptions newDocumentOptions = mainApplication.showDocOptionsDialog(this.documentOptions);
      if (newDocumentOptions != null) {
        this.documentOptions = newDocumentOptions;
        initFiscalPort();
        fiscalPort.setCustomerData(this.documentOptions);
      }
    } catch (Exception e) {
      throw new UserFormException(e);
    }
  }

  @FXML
  private void actionFiscalDocumentOpen() {
    initFiscalPort();
    fiscalPort.openFiscalDocument(cbFTestDocumentType.getValue());
  }

  @FXML
  private void actionFiscalDocumentSendItem() {
    initFiscalPort();
    if (IT_SALE.equals(cbFTestQualifierOperation.getValue()))
      fiscalPort.printLineFiscalDocument(tfFTestItemName.getText(),
              tfFTestExtraText.getText(),
              getFieldAsFloat(tfFTestQuantity.getText(), "Quantity"),
              getFieldAsFloat(tfFTestPriceAmount.getText(), "Price/Amount"),
              getFieldAsFloat(tfFTestTax.getText(), "Tax %"),
              cbFTestOperationType.getValue(),
              getFieldAsFloat(tfFTestInternalAmount.getText(), "Internal Tax amount"), null,
              cbFTestQualifierPriceOperation.getValue());
    else if (IT_RECHARGE_DISCOUNT.equals(cbFTestQualifierOperation.getValue()) || IT_BOTTLE_DISCOUNT.equals(cbFTestQualifierOperation.getValue()))
      fiscalPort.returnRecharge(tfFTestItemName.getText(),
              getFieldAsFloat(tfFTestPriceAmount.getText(), "Price/Amount"),
              getFieldAsFloat(tfFTestTax.getText(), "Tax %"),
              cbFTestOperationType.getValue(),
              getFieldAsFloat(tfFTestInternalAmount.getText(), "Internal Tax amount"), null,
              cbFTestQualifierPriceOperation.getValue(),
              cbFTestQualifierOperation.getValue());
  }

  @FXML
  private void actionFiscalDocumentSendText() {
    initFiscalPort();
    fiscalPort.printFiscalText(tfFTestExtraText.getText());
  }

  @FXML
  private void actionFiscalDocumentGeneralDiscountRecharge() {
    initFiscalPort();
    fiscalPort.generalDiscountFiscalDocument(tfFTestRechargeDiscountName.getText(),
            getFieldAsFloat(tfFTestRechargeDiscountAmount.getText(), "Amount"), cbFTestGeneralAddOperation.getValue());
  }

  @FXML
  private void actionFiscalDocumentTender() {
    initFiscalPort();
    fiscalPort.totalTender(tfFTestPaymentName.getText(),
            getFieldAsFloat(tfFTestPaymentAmount.getText(), "Payment Amount"), cbFTestTenderOperation.getValue());
  }

  @FXML
  private void actionFiscalDocumentSubtotal() {
    initFiscalPort();
    fiscalPort.subtotalFiscalDocument(cbFTestSubtotalOption.getValue());
    try {
      final FiscalPacket response = fiscalPort.getLastResponse();

      if (response != null)
        mainApplication.showSubtotalDialog(
                getResponseItemAsMoney(response, 4),
                getResponseItemAsMoney(response, 5),
                getResponseItemAsMoney(response, 6),
                getResponseItemAsMoney(response, 7),
                getResponseItemAsMoney(response, 10));
    } catch (Exception e) {
      throw new UserFormException(e);
    }
  }

  @FXML
  private void actionFiscalDocumentCancel() {
    initFiscalPort();
    fiscalPort.cancelDocument();
  }

  @FXML
  private void actionFiscalDocumentClose() {
    initFiscalPort();
    fiscalPort.closeFiscalDocument();
  }

  @FXML
  private void actionNonFiscalDocumentOpen() {
    initFiscalPort();
    fiscalPort.openNfdTicket();
  }

  @FXML
  private void actionNonFiscalDocumentSendText() {
    initFiscalPort();
    fiscalPort.printNonFiscalText(tfNonFiscalText.getText());
  }

  @FXML
  private void actionNonFiscalDocumentCancel() {
    initFiscalPort();
    fiscalPort.cancelDocument();
  }

  @FXML
  private void actionNonFiscalDocumentClose() {
    initFiscalPort();
    fiscalPort.closeNfd();
  }

  @FXML
  private void actionReporteX() {
    initFiscalPort();
    fiscalPort.dailyClose(true);
  }

  @FXML
  private void actionReporteZ() {
    initFiscalPort();
    fiscalPort.dailyClose(false);
  }

  @FXML
  private void actionAuditSend() {
    initFiscalPort();
    if (rbFiscalAuditByZ.isSelected())
      fiscalPort.dailyCloseByNumber(
              getFieldAsInteger(tfFiscalAuditNumStart.getText(), "start #"),
              getFieldAsInteger(tfFiscalAuditNumEnd.getText(), "end #"),
              cbReportsPrintingOptions.getValue());
    else
      fiscalPort.dailyCloseByDate(
              getFieldAsDate(dpFiscalAuditDateStart, "start"),
              getFieldAsDate(dpFiscalAuditDateEnd, "end"),
              cbReportsPrintingOptions.getValue());
  }

  public void initFiscalPort() {
    final String comport = cbComPort.getValue();
    final Integer baudRate = cbBaudRates.getValue();
    if (comport != null && !comport.isEmpty() && baudRate != null && baudRate > 0) {
      fiscalPort.setComPort(comport);
      fiscalPort.setBaudRate(baudRate);
      fiscalPort.setSaveLog(cbSaveLog.isSelected());
    } else
      throw new UserFormException("Port configuration error");
  }

  public void setFiscalPort(FiscalPortCommander fiscalPort) {
    this.fiscalPort = fiscalPort;
    cbComPort.setItems(getComPorts());
    cbBaudRates.setItems(baudRates);
    cbBaudRates.setValue(fiscalPort.getBaudRate());
    cbFTestDocumentType.setItems(DOCUMENT_TYPES);
    cbFTestDocumentType.setValue(DOCUMENT_TYPES.get(0));
    cbFTestOperationType.setItems(OPERATION_TYPES);
    cbFTestOperationType.setValue(OPERATION_TYPES.get(0));
/* not implemented in this version
    cbFTestDisplayParameter.setItems(DISPLAY_PARAMETERS);
    cbFTestDisplayParameter.setValue(DISPLAY_PARAMETERS.get(0));
*/
    cbFTestQualifierPriceOperation.setItems(QUALIFIER_PRICE_OPERATION);
    cbFTestQualifierPriceOperation.setValue(QUALIFIER_PRICE_OPERATION.get(0));
    cbFTestQualifierOperation.setItems(QUALIFIER_OPERATION);
    cbFTestQualifierOperation.setValue(QUALIFIER_OPERATION.get(0));

    cbFTestSubtotalOption.setItems(SUBTOTAL_PRINTING_OPTIONS);
    cbFTestSubtotalOption.setValue(SUBTOTAL_PRINTING_OPTIONS.get(0));

    cbFTestGeneralAddOperation.setItems(OPERATION_TYPES);
    cbFTestGeneralAddOperation.setValue(OPERATION_TYPES.get(0));

    cbFTestTenderOperation.setItems(TENDER_OPERATION);
    cbFTestTenderOperation.setValue(TENDER_OPERATION.get(0));

    cbReportsPrintingOptions.setItems(AUDIT_PRINTING_OPTIONS);
    cbReportsPrintingOptions.setValue(AUDIT_PRINTING_OPTIONS.get(0));

    dpFiscalAuditDateStart.disableProperty().bind(rbFiscalAuditByZ.selectedProperty());
    dpFiscalAuditDateEnd.disableProperty().bind(rbFiscalAuditByZ.selectedProperty());
    tfFiscalAuditNumStart.disableProperty().bind(rbFiscalAuditByDate.selectedProperty());
    tfFiscalAuditNumEnd.disableProperty().bind(rbFiscalAuditByDate.selectedProperty());

    fiscalPort.setRequestListener(new FiscalPortCommander.FiscalPacketListener() {
      @Override
      public void invoke(FiscalPacket packet) {
        tfPrinterRequest.appendText("\n");
        tfPrinterRequest.appendText(packet.toString());
      }
    });

    fiscalPort.setResponseListener(new FiscalPortCommander.FiscalPacketListener() {
      @Override
      public void invoke(FiscalPacket packet) {
        tfPrinterStatus.appendText("\n");
        tfPrinterStatus.appendText(packet.toString());
      }
    });
  }

  private ObservableList<String> getComPorts() {
    if (comPorts == null)
      comPorts = FXCollections.observableArrayList(RXTXFiscalPort.getPortNames());
    return comPorts;
  }

  private Float getFloat(@Nullable String value) {
    return value != null && !value.isEmpty() ? Float.valueOf(value) : null;
  }

  private Integer getFieldAsInteger(String value, String fieldName) {
    try {
      return value != null && !value.isEmpty() ? Integer.valueOf(value) : null;
    } catch (NumberFormatException e) {
      throw new UserFormException("Incorrect number format at field \"%s\"", fieldName);
    }
  }

  private Float getFieldAsFloat(String value, String fieldName) {
    try {
      return getFloat(value);
    } catch (NumberFormatException e) {
      throw new UserFormException("Incorrect number format at field \"%s\"", fieldName);
    }
  }

  private Date getFieldAsDate(DatePicker value, String fieldName) {
    try {
      final StringConverter<LocalDate> converter = value.getConverter();
      return Date.valueOf(converter.fromString(value.getEditor().getText()));
    } catch (Exception e) {
      throw new UserFormException("Incorrect Date format at field \"%s\"", fieldName);
    }
  }

  private Float getResponseItemAsMoney(FiscalPacket packet, int itemNumber) {
    try {
      return packet.getFloat(itemNumber) / 100;
    } catch (Exception e) {
      return null;
    }
  }
}
