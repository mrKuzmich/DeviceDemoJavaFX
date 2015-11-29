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

import java.sql.Date;
import java.time.LocalDate;


public class Controller {
  static ObservableList<String> comPorts = null;
  final static ObservableList<Integer> baudRates = FXCollections.observableArrayList(
          300, 1200, 2400, 4800, 9600, 14400, 19200, 28800, 38400, 57600, 115200, 230400);
  // TODO This and other items controller must get from concrete PortCommander
  public final static ObservableList<TypeSelectItem> MODELS = FXCollections.observableArrayList(
      new TypeSelectItem(EpsonPortCommander.class, "Epson"),
      new TypeSelectItem(HasarPortCommander.class, "Hasar"));
  public final static ObservableList<TypeSelectItem> OPERATION_TYPES = FXCollections.observableArrayList(
          new TypeSelectItem("M", "Add Amount"),
          new TypeSelectItem("m", "Void Amount"));
  public final static ObservableList<TypeSelectItem> DOCUMENT_TYPES = FXCollections.observableArrayList(
          new TypeSelectItem("T", "Ticket"),
          new TypeSelectItem("A", "Ticket Bill \"A\""),
          new TypeSelectItem("B", "Ticket Bill \"B/C\""),
          new TypeSelectItem("D", "Debit note \"A\""),
          new TypeSelectItem("E", "Debit note \"B/C\""));
  public final static ObservableList<TypeSelectItem> DISPLAY_PARAMETERS = FXCollections.observableArrayList(
          new TypeSelectItem("0", "No changes"),
          new TypeSelectItem("1", "Write display"),
          new TypeSelectItem("2", "Increase repetitions subfield"));
  public final static ObservableList<TypeSelectItem> QUALIFIER_PRICE_OPERATION = FXCollections.observableArrayList(
          new TypeSelectItem("T", "Total price"),
          new TypeSelectItem("X", "Net price"));
  public final static ObservableList<TypeSelectItem> SUBTOTAL_PRINTING_OPTIONS = FXCollections.observableArrayList(
          new TypeSelectItem("P", "Print subtotal"),
          new TypeSelectItem("X", "No printing"));
  public final static ObservableList<TypeSelectItem> TENDER_OPERATION = FXCollections.observableArrayList(
          new TypeSelectItem("C", "Cancel"),
          new TypeSelectItem("P", "Payment Total/Partial"),
          new TypeSelectItem("R", "Pay Back"));
  public final static ObservableList<TypeSelectItem> AUDIT_PRINTING_OPTIONS = FXCollections.observableArrayList(
          new TypeSelectItem("D", "Print subtotal"),
          new TypeSelectItem("T", "No printing"));

  final static TypeSelectItem IT_SALE =
          new TypeSelectItem("X", "Normal Sale");
  final static TypeSelectItem IT_RECHARGE_DISCOUNT =
          new TypeSelectItem("B", "Recharge/Discount");
  final static TypeSelectItem IT_BOTTLE_DISCOUNT =
          new TypeSelectItem("X", "Bottle");
  public final static ObservableList<TypeSelectItem> QUALIFIER_OPERATION = FXCollections.observableArrayList(
          IT_SALE, IT_RECHARGE_DISCOUNT, IT_BOTTLE_DISCOUNT);

  @FXML
  private ChoiceBox<String> cbComPort;
  @FXML
  private ChoiceBox<Integer> cbBaudRates;
  @FXML
  private ChoiceBox<TypeSelectItem> cbModels;
  @FXML
  private CheckBox cbSaveLog;
  @FXML
  private TextArea tfPrinterRequest;
  @FXML
  private TextArea tfPrinterStatus;

  //FiscalTestFields
  @FXML
  private ChoiceBox<TypeSelectItem> cbFTestDocumentType;
  @FXML
  private TextField tfFTestItemName;
  @FXML
  private TextField tfFTestItemCode;
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
  private ChoiceBox<TypeSelectItem> cbFTestOperationType;
  @FXML
  private ChoiceBox<TypeSelectItem> cbFTestDisplayParameter;
  @FXML
  private ChoiceBox<TypeSelectItem> cbFTestQualifierPriceOperation;
  @FXML
  private ChoiceBox<TypeSelectItem> cbFTestQualifierOperation;
  @FXML
  private ChoiceBox<TypeSelectItem> cbFTestSubtotalOption;
  @FXML
  private ChoiceBox<TypeSelectItem> cbFTestGeneralAddOperation;
  @FXML
  private ChoiceBox<TypeSelectItem> cbFTestTenderOperation;
  @FXML
  private ChoiceBox<TypeSelectItem> cbReportsPrintingOptions;

  private Main mainApplication;

  private DocumentOptions documentOptions;

  @FXML
  private void actionDemoDeviceExit() {
    Platform.exit();
  }

  @FXML
  private void actionPrinterStatus() {
    getPortCommander().statusRequest();
  }

  @FXML
  private void actionOptionAccept() {
    getPortCommander().statusRequest();
  }

  @FXML
  private void actionSetCustomerData() {
    try {
      final DocumentOptions newDocumentOptions = mainApplication.showDocOptionsDialog(this.documentOptions);
      if (newDocumentOptions != null) {
        this.documentOptions = newDocumentOptions;
        getPortCommander().setCustomerData(this.documentOptions);
      }
    } catch (Exception e) {
      throw new UserFormException(e);
    }
  }

  @FXML
  private void actionFiscalDocumentOpen() {
    getPortCommander().openFiscalDocument(cbFTestDocumentType.getValue());
  }

  @FXML
  private void actionFiscalDocumentSendItem() {
    getPortCommander().printLineFiscalDocument(tfFTestItemName.getText(),
        tfFTestItemCode.getText(),
        tfFTestExtraText.getText(),
        getFieldAsFloat(tfFTestQuantity.getText(), "Quantity"),
        getFieldAsFloat(tfFTestPriceAmount.getText(), "Price/Amount"),
        getFieldAsFloat(tfFTestTax.getText(), "Tax %"),
        cbFTestOperationType.getValue(),
        getFieldAsFloat(tfFTestInternalAmount.getText(), "Internal Tax amount"), null,
        cbFTestQualifierPriceOperation.getValue(),
        cbFTestQualifierOperation.getValue());
  }

  @FXML
  private void actionFiscalDocumentSendText() {
    getPortCommander().printFiscalText(tfFTestExtraText.getText());
  }

  @FXML
  private void actionFiscalDocumentGeneralDiscountRecharge() {
    getPortCommander().generalDiscountFiscalDocument(tfFTestRechargeDiscountName.getText(),
        getFieldAsFloat(tfFTestRechargeDiscountAmount.getText(), "Amount"), cbFTestGeneralAddOperation.getValue());
  }

  @FXML
  private void actionFiscalDocumentTender() {
    getPortCommander().totalTender(tfFTestPaymentName.getText(),
        getFieldAsFloat(tfFTestPaymentAmount.getText(), "Payment Amount"), cbFTestTenderOperation.getValue());
  }

  @FXML
  private void actionFiscalDocumentSubtotal() {
    final PortCommander portCommander = getPortCommander();
    portCommander.subtotalFiscalDocument(cbFTestSubtotalOption.getValue());
    try {
      final FiscalPacket response = portCommander.getLastResponse();

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
    getPortCommander().cancelDocument();
  }

  @FXML
  private void actionFiscalDocumentClose() {
    getPortCommander().closeFiscalDocument();
  }

  @FXML
  private void actionNonFiscalDocumentOpen() {
    getPortCommander().openNfdTicket();
  }

  @FXML
  private void actionNonFiscalDocumentSendText() {
    getPortCommander().printNonFiscalText(tfNonFiscalText.getText());
  }

  @FXML
  private void actionNonFiscalDocumentCancel() {
    getPortCommander().cancelDocument();
  }

  @FXML
  private void actionNonFiscalDocumentClose() {
    getPortCommander().closeNfd();
  }

  @FXML
  private void actionReporteX() {
    getPortCommander().dailyClose(true);
  }

  @FXML
  private void actionReporteZ() {
    getPortCommander().dailyClose(false);
  }

  @FXML
  private void actionAuditSend() {
    if (rbFiscalAuditByZ.isSelected())
      getPortCommander().dailyCloseByNumber(
          getFieldAsInteger(tfFiscalAuditNumStart.getText(), "start #"),
          getFieldAsInteger(tfFiscalAuditNumEnd.getText(), "end #"),
          cbReportsPrintingOptions.getValue());
    else
      getPortCommander().dailyCloseByDate(
          getFieldAsDate(dpFiscalAuditDateStart, "start"),
          getFieldAsDate(dpFiscalAuditDateEnd, "end"),
          cbReportsPrintingOptions.getValue());
  }

  private PortCommander getPortCommander() {
      final String comport = cbComPort.getValue();
      final Integer baudRate = cbBaudRates.getValue();
      final Class<PortCommander> portCommanderClass = (Class) (cbModels.getValue().getType());
      if (comport != null && !comport.isEmpty() && baudRate != null && baudRate > 0) {
        final PortCommander portCommander;
        try {
          portCommander = mainApplication.getPortCommander(portCommanderClass);
        } catch (Exception e) {
          throw new UserFormException("Error create port commander: %s", e.getMessage());
        }
        portCommander.setComPort(comport);
        portCommander.setBaudRate(baudRate);
        portCommander.setSaveLog(cbSaveLog.isSelected());

        portCommander.setRequestListener(new FiscalPacketListener() {
          @Override
          public void invoke(FiscalPacket packet) {
            tfPrinterRequest.appendText("\n");
            tfPrinterRequest.appendText(packet.toString());
          }
        });

        portCommander.setResponseListener(new FiscalPacketListener() {
          @Override
          public void invoke(FiscalPacket packet) {
            tfPrinterStatus.appendText("\n");
            tfPrinterStatus.appendText(packet.toString());
          }
        });

        return portCommander;
      } else
        throw new UserFormException("Port configuration error");
  }

  public void init(Main mainApplication) {
    this.mainApplication = mainApplication;

    cbComPort.setItems(getComPorts());
    cbBaudRates.setItems(baudRates);
    cbBaudRates.setValue(baudRates.get(4));
    cbModels.setItems(MODELS);
    cbModels.setValue(MODELS.get(0));
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
