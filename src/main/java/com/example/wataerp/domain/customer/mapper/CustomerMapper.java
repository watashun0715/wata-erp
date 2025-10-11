package com.example.wataerp.domain.customer.mapper;

import com.example.wataerp.domain.customer.dto.CustomerRequest;
import com.example.wataerp.domain.customer.dto.CustomerResponse;
import com.example.wataerp.domain.customer.entity.Customer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public final class CustomerMapper {

  private CustomerMapper() {}

  public static Customer toNewEntity(CustomerRequest req) {
    Customer customer = new Customer();

    customer.setCode((trim(req.getCode())));
    // 最初は法人顧客のみを想定、あとから個人の場合に改修する
    // カスタムメソッドでtrimしてあげることでヌルポになるリスクをなくす
    customer.setCompanyName(trim(req.getName()));
    customer.setBillingAddress(trimOrNull(req.getBillingAddress()));
    customer.setTaxCode(trimOrNull(req.getTaxCode()));
    customer.setCreditLimit(req.getCreditLimit() != null ? req.getCreditLimit() : BigDecimal.ZERO);
    return customer;
  }

  public static void overwrite(Customer customer, CustomerRequest req) {
    customer.setCode((trim(req.getCode())));
    customer.setCompanyName(trim(req.getName()));
    customer.setBillingAddress(trimOrNull(req.getBillingAddress()));
    customer.setTaxCode(trimOrNull(req.getTaxCode()));
    // PUT想定: creditLimitがnullなら0に初期化
    customer.setCreditLimit(req.getCreditLimit() != null ? req.getCreditLimit() : BigDecimal.ZERO);
  }

  public static void merge(Customer customer, CustomerRequest req) {
    if (req.getCode() != null) customer.setCode((trim(req.getCode())));
    if (req.getName() != null) customer.setCompanyName(trim(req.getName()));
    if (req.getBillingAddress() != null)
      customer.setBillingAddress(trimOrNull(req.getBillingAddress()));
    if (req.getTaxCode() != null) customer.setTaxCode(trimOrNull(req.getTaxCode()));
    if (req.getCreditLimit() != null) customer.setCreditLimit(req.getCreditLimit());
  }

  public static CustomerResponse toResponse(Customer customer) {
    CustomerResponse res = new CustomerResponse();

    // Responseに返すときはerpシステムなどはnullで来たらnullで返すでよさそう。
    // UIにこだわっているシステムなどはここで""に変換してあげるなどしても良い。
    res.setCode(trim(customer.getCode()));
    res.setName(trim(customer.getCompanyName()));
    res.setBillingAddress(trimOrNull(customer.getBillingAddress()));
    res.setTaxCode(trimOrNull(customer.getTaxCode()));
    res.setCreditLimit(customer.getCreditLimit());
    res.setActive(customer.getActive());
    return res;
  }

  public static List<CustomerResponse> toResponseList(Collection<Customer> customers) {
    return customers.stream().map(CustomerMapper::toResponse).toList();
  }

  // ---- helpers ----
  private static String trim(String s) {
    return (s == null) ? null : s.trim();
  }

  /** 空文字はnullに正規化 */
  private static String trimOrNull(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }
}
