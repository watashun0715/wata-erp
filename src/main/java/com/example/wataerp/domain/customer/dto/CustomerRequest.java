package com.example.wataerp.domain.customer.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerRequest {

    @NotBlank
    @Size(max = 32, message = "最大文字数は{max}文字です")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "書式が違います")
    private String code;

    @NotBlank
    @Size(max = 128, message = "最大文字数は{max}文字です")
    private String name;

    @Size(max = 256, message = "最大文字数は{max}文字です")
    private String billingAddress;

    @Size(max = 16, message = "最大文字数は{max}文字です")
    private String taxCode;

    @DecimalMin("0")
    private BigDecimal creditLimit;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        this.creditLimit = creditLimit;
    }

}