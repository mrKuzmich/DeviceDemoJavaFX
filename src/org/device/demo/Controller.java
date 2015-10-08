package org.device.demo;

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
  final static ObservableList<OperationType> operationTypes = FXCollections.observableArrayList(
          new OperationType("M", "Add Amount"),
          new OperationType("m", "Void Amount"));


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
  private TextField tfFTestItemName;
  @FXML
  private TextField tfFTestQuantity;
  @FXML
  private TextField tfFTestPrice;
  @FXML
  private TextField tfFTestTax;
  @FXML
  private CheckBox cbFTestTaxable;
  @FXML
  private ChoiceBox<OperationType> cbFTestOperationType;

  @FXML
  private void close() {
    Platform.exit();
  }

  @FXML
  private void accept() {
    try {
      final String comport = cbComPort.getValue();
      final Integer baudRate = cbBaudRates.getValue();
      if (comport != null && !comport.isEmpty() && baudRate != null && baudRate > 0) {
        fiscalPort.setComPort(comport);
        fiscalPort.setBaudRate(baudRate);
        fiscalPort.setSaveLog(cbSaveLog.isSelected());
        fiscalPort.statusRequest();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setFiscalPort(FiscalPortCommander fiscalPort) {
    this.fiscalPort = fiscalPort;
    cbComPort.setItems(getComPorts());
    cbBaudRates.setItems(baudRates);
    cbBaudRates.setValue(fiscalPort.getBaudRate());
    cbFTestOperationType.setItems(operationTypes);
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

  private static class OperationType {
    private String operation;
    private String operationCaption;

    public OperationType(String operation, String operationCaption) {
      this.operation = operation;
      this.operationCaption = operationCaption;
    }

    public String getOperation() {
      return operation;
    }

    public String getOperationCaption() {
      return operationCaption;
    }

    @Override
    public String toString() {
      return getOperationCaption();
    }
  }

}
