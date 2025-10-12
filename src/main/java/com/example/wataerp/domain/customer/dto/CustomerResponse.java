package com.example.wataerp.domain.customer.dto;

import java.math.BigDecimal;

public class CustomerResponse {

  private String code;

  private String companyName;

  private String billingAddress;

  private String taxCode;

  private BigDecimal creditLimit;

  private Boolean active;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(String billingAddress) {
    this.billingAddress = billingAddress;
  }

  public String getTaxCode() {
    return taxCode;
  }

  public void setTaxCode(String taxCode) {
    this.taxCode = taxCode;
  }

  public BigDecimal getCreditLimit() {
    return creditLimit;
  }

  public void setCreditLimit(BigDecimal creditLimit) {
    if (creditLimit == null) {
      this.creditLimit = null;
    } else {
      this.creditLimit = creditLimit;
    }
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }
}
