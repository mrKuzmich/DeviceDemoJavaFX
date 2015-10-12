package org.device.demo;

import com.sun.istack.internal.Nullable;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.port.rxtx.RXTXFiscalPort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


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
  final static ObservableList<FiscalPortCommander.TypeSelectItem> PRINTING_OPTIONS = FXCollections.observableArrayList(
      new FiscalPortCommander.TypeSelectItem("P", "Print subtotal"),
      new FiscalPortCommander.TypeSelectItem("X", "No printing"));

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
  private void actionDemoDeviceExit() {
    Platform.exit();
  }

  @FXML
  private void actionPrinterStatus() {
  }

  @FXML
  private void actionOptionAccept() {
    initFiscalPort();
    fiscalPort.statusRequest();
  }

  @FXML
  private void actionSetCustomerData() {
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
          getFieldAsFloat(tfFTestQuantity.getText(), "Quantity"),
          getFieldAsFloat(tfFTestPriceAmount.getText(), "Price/Amount"),
          getFieldAsFloat(tfFTestTax.getText(), "Tax %"),
          cbFTestOperationType.getValue(),
          getFieldAsFloat(tfFTestInternalAmount.getText(), "Internal Tax amount"), null,
          cbFTestQualifierPriceOperation.getValue());
    else if (IT_RECHARGE_DISCOUNT.equals(cbFTestQualifierOperation.getValue()) || IT_BOTTLE_DISCOUNT.equals(cbFTestQualifierOperation.getValue()))
      fiscalPort.returRecharge(tfFTestItemName.getText(),
          getFieldAsFloat(tfFTestPriceAmount.getText(), "Price/Amount"),
          getFieldAsFloat(tfFTestTax.getText(), "Tax %"),
          cbFTestOperationType.getValue(),
          getFieldAsFloat(tfFTestInternalAmount.getText(), "Internal Tax amount"), null,
          cbFTestQualifierPriceOperation.getValue(),
          cbFTestQualifierOperation.getValue());
  }

  @FXML
  private void actionFiscalDocumentSendText() {
  }

  @FXML
  private void actionFiscalDocumentGeneralDiscountRecharge() {
    initFiscalPort();
    fiscalPort.generalDiscountFiscalDocument(tfFTestRechargeDiscountName.getText(),
        getFieldAsFloat(tfFTestRechargeDiscountAmount.getText(), "Amount"), cbFTestGeneralAddOperation.getValue()) ;
  }

  @FXML
  private void actionFiscalDocumentTender() {
  }

  @FXML
  private void actionFiscalDocumentSubtotal() {
    initFiscalPort();
    fiscalPort.subtotalFiscalDocument(cbFTestSubtotalOption.getValue());
  }

  @FXML
  private void actionFiscalDocumentCancel() {
    initFiscalPort();
    fiscalPort.cancelFiscalDocument();
  }

  @FXML
  private void actionFiscalDocumentClose() {
    initFiscalPort();
    fiscalPort.closeFiscalDocument();
  }

  @FXML
  private void actionNonFiscalDocumentOpen() {
  }

  @FXML
  private void actionNonFiscalDocumentSendText() {
  }

  @FXML
  private void actionNonFiscalDocumentCancel() {
  }

  @FXML
  private void actionNonFiscalDocumentClose() {
  }

  @FXML
  private void actionReporteX() {
  }

  @FXML
  private void actionReporteZ() {
  }

  @FXML
  private void actionAuditSend() {
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

    cbFTestSubtotalOption.setItems(PRINTING_OPTIONS);
    cbFTestSubtotalOption.setValue(PRINTING_OPTIONS.get(0));

    cbFTestGeneralAddOperation.setItems(OPERATION_TYPES);
    cbFTestGeneralAddOperation.setValue(OPERATION_TYPES.get(0));

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

  private Float getFieldAsFloat(String value, String fieldName) {
    try {
      return getFloat(value);
    } catch (NumberFormatException e) {
      new UserFormException("Incorrect number format at field \"%s\"", fieldName);
    }
    return null;
  }

}
