package org.device.demo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;


public class Controller {
  private FiscalPortMediator fiscalPort;
  @FXML
  private ChoiceBox<String> cbComPort;
  @FXML
  private ChoiceBox<Number> cbBaudRates;
  @FXML
  private CheckBox cbSaveLog;
  @FXML
  private TextField tfPrinterRequest;


  @FXML
  private void close() {
    Platform.exit();
  }
  @FXML
  private void accept() {
    try {
      fiscalPort.accept();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setFiscalPort(FiscalPortMediator fiscalPort) {
    this.fiscalPort = fiscalPort;
    cbComPort.setItems(fiscalPort.getComPorts());
    cbComPort.valueProperty().bindBidirectional(fiscalPort.comPortProperty());
    cbBaudRates.setItems(fiscalPort.getBaudRates());
    cbBaudRates.valueProperty().bindBidirectional(fiscalPort.baudRateProperty());
    cbSaveLog.selectedProperty().bindBidirectional(fiscalPort.saveLogProperty());
    tfPrinterRequest.textProperty().bindBidirectional(fiscalPort.printerRequestProperty());


  }
}
