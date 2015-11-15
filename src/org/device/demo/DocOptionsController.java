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
  final static ObservableList<TypeSelectItem> TYPES = FXCollections.observableArrayList(
          new TypeSelectItem("A", "A"),
          new TypeSelectItem("B", "B/C"));

  final static ObservableList<TypeSelectItem> CONDITION_TAXES = FXCollections.observableArrayList(
          new TypeSelectItem("I", "Enrolled Responsible"),
          new TypeSelectItem("N", "Responsible not registered"),
          new TypeSelectItem("E", "Exempt"),
          new TypeSelectItem("A", "Not Responsible"),
          new TypeSelectItem("B", "Head not registered"),
          new TypeSelectItem("M", "Responsible monotributo"),
          new TypeSelectItem("S", "Social Monotribustista"),
          new TypeSelectItem("T", "Not categorized"));

  final static ObservableList<TypeSelectItem> DOCUMENTS = FXCollections.observableArrayList(
          new TypeSelectItem("C", "CUIT"),
          new TypeSelectItem("L", "CUIL"),
          new TypeSelectItem("0", "Enrollment"),
          new TypeSelectItem("1", "Civic"),
          new TypeSelectItem("2", "National identity"),
          new TypeSelectItem("3", "Passport"),
          new TypeSelectItem("4", "Identity card"));


  @FXML
  private CheckBox cbBill;
  @FXML
  private CheckBox cbReceipt;
  @FXML
  private CheckBox cbDebitNote;
  @FXML
  private CheckBox cbCreditNote;
  @FXML
  private ChoiceBox<TypeSelectItem> cbType;
  @FXML
  private ChoiceBox<TypeSelectItem> cbConditionTax;
  @FXML
  private ChoiceBox<TypeSelectItem> cbDocument;
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
