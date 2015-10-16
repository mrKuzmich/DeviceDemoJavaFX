package org.device.demo;

import javafx.beans.property.*;

/**
 * Created by Dmitry on 16.10.2015.
 */
public class DocumentOptions {
  private Boolean bill = false;
  private Boolean receipt = false;
  private Boolean debitNote = false;
  private Boolean creditNote = false;

  private FiscalPortCommander.TypeSelectItem type;
  private FiscalPortCommander.TypeSelectItem conditionTax;
  private FiscalPortCommander.TypeSelectItem document;

  private String number;
  private String name;
  private String address;

  public DocumentOptions() {
  }

  public Boolean getBill() {
    return bill;
  }

  public void setBill(Boolean bill) {
    this.bill = bill;
  }

  public Boolean getReceipt() {
    return receipt;
  }

  public void setReceipt(Boolean receipt) {
    this.receipt = receipt;
  }

  public Boolean getDebitNote() {
    return debitNote;
  }

  public void setDebitNote(Boolean debitNote) {
    this.debitNote = debitNote;
  }

  public Boolean getCreditNote() {
    return creditNote;
  }

  public void setCreditNote(Boolean creditNote) {
    this.creditNote = creditNote;
  }

  public FiscalPortCommander.TypeSelectItem getType() {
    return type;
  }

  public void setType(FiscalPortCommander.TypeSelectItem type) {
    this.type = type;
  }

  public FiscalPortCommander.TypeSelectItem getConditionTax() {
    return conditionTax;
  }

  public void setConditionTax(FiscalPortCommander.TypeSelectItem conditionTax) {
    this.conditionTax = conditionTax;
  }

  public FiscalPortCommander.TypeSelectItem getDocument() {
    return document;
  }

  public void setDocument(FiscalPortCommander.TypeSelectItem document) {
    this.document = document;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
