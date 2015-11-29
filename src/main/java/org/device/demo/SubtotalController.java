package org.device.demo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by Dmitry on 17.10.2015.
 */
public class SubtotalController {

  @FXML
  private TextField tfNumberOfItems;
  @FXML
  private TextField tfSalesAmount;
  @FXML
  private TextField tfTaxAmount;
  @FXML
  private TextField tfAmountPaid;
  @FXML
  private TextField tfOtherTaxAmount;

  public void init(Float numberOfItems, Float salesAmount, Float taxAmount, Float amountPaid, Float otherTaxAmount) {
    tfNumberOfItems.setText(numberOfItems != null ? numberOfItems.toString() : null);
    tfNumberOfItems.setEditable(false);
    tfSalesAmount.setText(salesAmount != null ? salesAmount.toString() : null);
    tfSalesAmount.setEditable(false);
    tfTaxAmount.setText(taxAmount != null ? taxAmount.toString() : null);
    tfTaxAmount.setEditable(false);
    tfAmountPaid.setText(amountPaid != null ? amountPaid.toString() : null);
    tfAmountPaid.setEditable(false);
    tfOtherTaxAmount.setText(otherTaxAmount != null ? otherTaxAmount.toString() : null);
    tfOtherTaxAmount.setEditable(false);
  }

}
