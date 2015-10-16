package org.device.demo;

import javafx.beans.property.*;

/**
 * Created by Dmitry on 16.10.2015.
 */
public class DocumentOptions {
  private BooleanProperty bill;
  private BooleanProperty receipt;
  private BooleanProperty debitNote;
  private BooleanProperty creditNote;

  private SimpleObjectProperty<FiscalPortCommander.TypeSelectItem> type;
  private SimpleObjectProperty<FiscalPortCommander.TypeSelectItem> conditionTax;
  private SimpleObjectProperty<FiscalPortCommander.TypeSelectItem> document;

  private LongProperty number;
  private StringProperty name;
  private StringProperty address;

  public DocumentOptions() {
  }

  public boolean getBill() {
    return bill.get();
  }

  public BooleanProperty billProperty() {
    return bill;
  }

  public void setBill(boolean bill) {
    this.bill.set(bill);
  }

  public boolean getReceipt() {
    return receipt.get();
  }

  public BooleanProperty receiptProperty() {
    return receipt;
  }

  public void setReceipt(boolean receipt) {
    this.receipt.set(receipt);
  }

  public boolean getDebitNote() {
    return debitNote.get();
  }

  public BooleanProperty debitNoteProperty() {
    return debitNote;
  }

  public void setDebitNote(boolean debitNote) {
    this.debitNote.set(debitNote);
  }

  public boolean getCreditNote() {
    return creditNote.get();
  }

  public BooleanProperty creditNoteProperty() {
    return creditNote;
  }

  public void setCreditNote(boolean creditNote) {
    this.creditNote.set(creditNote);
  }

  public FiscalPortCommander.TypeSelectItem getType() {
    return type.get();
  }

  public SimpleObjectProperty<FiscalPortCommander.TypeSelectItem> typeProperty() {
    return type;
  }

  public void setType(FiscalPortCommander.TypeSelectItem type) {
    this.type.set(type);
  }

  public FiscalPortCommander.TypeSelectItem getConditionTax() {
    return conditionTax.get();
  }

  public SimpleObjectProperty<FiscalPortCommander.TypeSelectItem> conditionTaxProperty() {
    return conditionTax;
  }

  public void setConditionTax(FiscalPortCommander.TypeSelectItem conditionTax) {
    this.conditionTax.set(conditionTax);
  }

  public FiscalPortCommander.TypeSelectItem getDocument() {
    return document.get();
  }

  public SimpleObjectProperty<FiscalPortCommander.TypeSelectItem> documentProperty() {
    return document;
  }

  public void setDocument(FiscalPortCommander.TypeSelectItem document) {
    this.document.set(document);
  }

  public long getNumber() {
    return number.get();
  }

  public LongProperty numberProperty() {
    return number;
  }

  public void setNumber(long number) {
    this.number.set(number);
  }

  public String getName() {
    return name.get();
  }

  public StringProperty nameProperty() {
    return name;
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public String getAddress() {
    return address.get();
  }

  public StringProperty addressProperty() {
    return address;
  }

  public void setAddress(String address) {
    this.address.set(address);
  }
}
