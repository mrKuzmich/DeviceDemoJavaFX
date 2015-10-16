package org.device.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Dmitry on 16.10.2015.
 */
public class DocOptionsController {
  final static ObservableList<FiscalPortCommander.TypeSelectItem> TYPES = FXCollections.observableArrayList(
          new FiscalPortCommander.TypeSelectItem("A", "A"),
          new FiscalPortCommander.TypeSelectItem("B", "B/C"));

  final static ObservableList<FiscalPortCommander.TypeSelectItem> CONDITION_TAXES = FXCollections.observableArrayList(
          new FiscalPortCommander.TypeSelectItem("I", "Enrolled Responsible"),
          new FiscalPortCommander.TypeSelectItem("N", "Responsible not registered"),
          new FiscalPortCommander.TypeSelectItem("E", "Exempt"),
          new FiscalPortCommander.TypeSelectItem("A", "Not Responsible"),
          new FiscalPortCommander.TypeSelectItem("B", "Head not registered"),
          new FiscalPortCommander.TypeSelectItem("M", "Responsible monotributo"),
          new FiscalPortCommander.TypeSelectItem("S", "Social Monotribustista"),
          new FiscalPortCommander.TypeSelectItem("T", "Not categorized"));

  final static ObservableList<FiscalPortCommander.TypeSelectItem> DOCUMENTS = FXCollections.observableArrayList(
          new FiscalPortCommander.TypeSelectItem("C", "CUIT"),
          new FiscalPortCommander.TypeSelectItem("L", "CUIL"),
          new FiscalPortCommander.TypeSelectItem("0", "Enrollment"),
          new FiscalPortCommander.TypeSelectItem("1", "Civic"),
          new FiscalPortCommander.TypeSelectItem("2", "National identity"),
          new FiscalPortCommander.TypeSelectItem("3", "Passport"),
          new FiscalPortCommander.TypeSelectItem("4", "Identity card"));


  @FXML
  private CheckBox cbBill;
  @FXML
  private CheckBox cbReceipt;
  @FXML
  private CheckBox cbDebitNote;
  @FXML
  private CheckBox cbCreditNote;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbType;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbConditionTax;
  @FXML
  private ChoiceBox<FiscalPortCommander.TypeSelectItem> cbDocument;
  @FXML
  private TextField tfNumber;
  @FXML
  private TextField tfName;
  @FXML
  private TextField tfAddress;

  private Stage dialogStage;
  private DocumentOptions documentOptions;

  public void setDialogStage(Stage dialogStage) {
    this.dialogStage = dialogStage;
  }

  public DocumentOptions getDocumentOptions() {
    return documentOptions;
  }

  public void init(DocumentOptions documentOptions) {
    cbType.setItems(TYPES);
    cbConditionTax.setItems(CONDITION_TAXES);
    cbDocument.setItems(DOCUMENTS);

    if (documentOptions != null) {
      cbBill.setSelected(documentOptions.getBill());
      cbReceipt.setSelected(documentOptions.getReceipt());
      cbDebitNote.setSelected(documentOptions.getDebitNote());
      cbCreditNote.setSelected(documentOptions.getCreditNote());
      cbType.setValue(documentOptions.getType());
      cbConditionTax.setValue(documentOptions.getConditionTax());
      cbDocument.setValue(documentOptions.getDocument());
      tfNumber.setText(String.valueOf(documentOptions.getNumber()));
      tfName.setText(documentOptions.getName());
      tfAddress.setText(documentOptions.getAddress());
      this.documentOptions = null;
    }
  }

  @FXML
  private void actionSendClient() {
    if (documentOptions == null)
      documentOptions = new DocumentOptions();

    documentOptions.setBill(cbBill.isSelected());
    documentOptions.setReceipt(cbReceipt.isSelected());
    documentOptions.setDebitNote(cbDebitNote.isSelected());
    documentOptions.setCreditNote(cbCreditNote.isSelected());
    documentOptions.setType(cbType.getValue());
    documentOptions.setConditionTax(cbConditionTax.getValue());
    documentOptions.setDocument(cbDocument.getValue());
    documentOptions.setNumber(tfNumber.getText());
    documentOptions.setName(tfName.getText());
    documentOptions.setAddress(tfAddress.getText());

    dialogStage.close();
  }
}
