package com.example.wataerp.domain.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Table(
    name = "customer_master",
    uniqueConstraints = {@UniqueConstraint(name = "uk_customers_code", columnNames = "code")},
    indexes = {@Index(name = "idx_customers_code", columnList = "code")})
public class Customer {

  @Id
  // 生成はアプリ側で行う想定（Controller/Serviceで UUID.randomUUID()）
  // DB側で生成したい場合は @GeneratedValue を使う選択もあるが、今回は手動採番で進める
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
  private UUID id;

  @Column(name = "code", nullable = false, length = 32)
  private String code;

  @Column(name = "last_name", length = 64)
  private String lastName;

  @Column(name = "first_name", length = 64)
  private String firstName;

  @Column(name = "company_name", length = 128)
  private String companyName;

  @Column(name = "billing_address", length = 256)
  private String billingAddress;

  @Column(name = "tax_code", length = 16)
  private String taxCode;

  @Column(name = "credit_limit", precision = 14, scale = 2, nullable = false)
  private BigDecimal creditLimit;

  @Column(name = "active", nullable = false)
  private Boolean active = true;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
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
    // creditLimitは一応nullを許容しないが、テストなどでnullなど何らかの経緯で来た場合ヌルポになるのでその場合は0
    BigDecimal creditLimitNullSafe = (creditLimit == null) ? BigDecimal.ZERO : creditLimit;
    this.creditLimit = creditLimitNullSafe.setScale(2, RoundingMode.HALF_UP);
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    // creditLimitと同様nullが来た時にヌルポになるので、その場合はtrue
    this.active = (active == null) ? Boolean.TRUE : active;
  }
}
