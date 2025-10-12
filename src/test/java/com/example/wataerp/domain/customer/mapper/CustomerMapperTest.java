package com.example.wataerp.domain.customer.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.wataerp.domain.customer.dto.CustomerRequest;
import com.example.wataerp.domain.customer.dto.CustomerResponse;
import com.example.wataerp.domain.customer.entity.Customer;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

  // ------------------------
  // merge / overwrite
  // ------------------------

  @Test
  @DisplayName("merge: リクエストで指定された項目のみ上書きし、未指定は元の値を保持する")
  void merge_updatesOnlyProvidedFields() {
    // given: 既存エンティティ
    Customer entity = new Customer();
    entity.setId(UUID.randomUUID());
    entity.setCode("C001");
    entity.setCompanyName("Old Co");
    entity.setBillingAddress("Old Addr");
    entity.setTaxCode("T-OLD");
    entity.setCreditLimit(new BigDecimal("10.00"));

    // and: 一部のみ指定されたリクエスト（name と creditLimit は更新、空白のみ/未指定は無視）
    CustomerRequest req = new CustomerRequest();
    req.setCode("  C001  "); // 通常 merge では code は触らない想定だが trim は通る
    req.setCompanyName("  New Co  "); // 上書き対象
    req.setTaxCode("T-NEW"); // 上書き対象
    req.setCreditLimit(new BigDecimal("20.50")); // 上書き対象

    // when
    CustomerMapper.merge(entity, req);

    // then
    assertThat(entity.getCode()).isEqualTo("C001"); // 変化なし（trim 済みでも値同じ）
    assertThat(entity.getCompanyName()).isEqualTo("New Co"); // 上書き
    assertThat(entity.getBillingAddress()).isEqualTo("Old Addr"); // 未指定 → そのまま
    assertThat(entity.getTaxCode()).isEqualTo("T-NEW"); // 未指定(null) → そのまま
    assertThat(entity.getCreditLimit()).isEqualByComparingTo("20.50"); // 上書き
  }

  @Test
  @DisplayName("merge: リクエストが全て null の場合は元の値を保持する")
  void merge_requestFieldsAreNull() {
    // given: 既存エンティティ
    Customer entity = new Customer();
    entity.setId(UUID.randomUUID());
    entity.setCode("C001");
    entity.setCompanyName("Old Co");
    entity.setBillingAddress("Old Addr");
    entity.setTaxCode("T-OLD");
    entity.setCreditLimit(new BigDecimal("10.00"));

    // and: 一部のみ指定されたリクエスト（name と creditLimit は更新、空白のみ/未指定は無視）
    CustomerRequest req = new CustomerRequest();
    req.setCode(null); // null → 無視想定（元の値保持）
    req.setCompanyName(null); // null → 無視想定（元の値保持）
    req.setBillingAddress(null); // null → 無視想定（元の値保持）
    req.setTaxCode(null); // null → 無視想定（元の値保持）
    req.setCreditLimit(null); // null → 無視想定（元の値保持）

    // when
    CustomerMapper.merge(entity, req);

    // then
    assertThat(entity.getCode()).isEqualTo("C001"); // 変化なし（trim 済みでも値同じ）
    assertThat(entity.getCompanyName()).isEqualTo("Old Co"); // 上書き
    assertThat(entity.getBillingAddress()).isEqualTo("Old Addr"); // 未指定 → そのまま
    assertThat(entity.getTaxCode()).isEqualTo("T-OLD"); // 未指定(null) → そのまま
    assertThat(entity.getCreditLimit()).isEqualByComparingTo("10.00"); // 上書き
  }

  @Test
  @DisplayName("overwrite: すべての項目を上書きする（trimOrNullの規則が適用される）")
  void overwrite_overwritesAllFields() {
    // given: 既存エンティティ
    Customer entity = new Customer();
    entity.setId(UUID.randomUUID());
    entity.setCode("C001");
    entity.setCompanyName("Old Co");
    entity.setBillingAddress("Old Addr");
    entity.setTaxCode("T-OLD");
    entity.setCreditLimit(new BigDecimal("10.00"));

    // and: 全フィールド指定のリクエスト（空白のみは null 化される想定）
    CustomerRequest req = new CustomerRequest();
    req.setCode("  C999  ");
    req.setCompanyName("  New Co  ");
    req.setBillingAddress("   "); // 空白のみ → null に
    req.setTaxCode("  T-NEW  ");
    req.setCreditLimit(new BigDecimal("30.00"));

    // when
    CustomerMapper.overwrite(entity, req);

    // then
    assertThat(entity.getCode()).isEqualTo("C999");
    assertThat(entity.getCompanyName()).isEqualTo("New Co");
    assertThat(entity.getBillingAddress()).isNull(); // 空白のみ → null
    assertThat(entity.getTaxCode()).isEqualTo("T-NEW");
    assertThat(entity.getCreditLimit()).isEqualByComparingTo("30.00");
  }

  @Test
  @DisplayName("overwrite: すべての項目を上書きする（trimOrNullの規則が適用される）")
  void overwrite_creditLimitIsNull() {
    // given: 既存エンティティ
    Customer entity = new Customer();
    entity.setId(UUID.randomUUID());
    entity.setCode("C001");
    entity.setCompanyName("Old Co");
    entity.setBillingAddress("Old Addr");
    entity.setTaxCode("T-OLD");
    entity.setCreditLimit(new BigDecimal("10.00"));

    // and: 全フィールド指定のリクエスト（空白のみは null 化される想定）
    CustomerRequest req = new CustomerRequest();
    req.setCode("  C999  ");
    req.setCompanyName("  New Co  ");
    req.setBillingAddress("   "); // 空白のみ → null に
    req.setTaxCode("  T-NEW  ");
    req.setCreditLimit(null);

    // when
    CustomerMapper.overwrite(entity, req);

    // then
    assertThat(entity.getCode()).isEqualTo("C999");
    assertThat(entity.getCompanyName()).isEqualTo("New Co");
    assertThat(entity.getBillingAddress()).isNull(); // 空白のみ → null
    assertThat(entity.getTaxCode()).isEqualTo("T-NEW");
    assertThat(entity.getCreditLimit()).isEqualByComparingTo(BigDecimal.ZERO); // null → 0
  }

  // ------------------------
  // 既存メソッドの押さえ（差分があれば拾う）
  // ------------------------

  @Test
  @DisplayName("toNewEntity: すべてのフィールドが正しくマッピングされ trimOrNull 規則が適用される")
  void toNewEntity_mapsAndTrims() {
    CustomerRequest req = new CustomerRequest();
    req.setCode("  C777 ");
    req.setCompanyName("  Seven  ");
    req.setBillingAddress("   "); // 空白のみ → null 期待
    req.setTaxCode("  T-777 ");
    req.setCreditLimit(new BigDecimal("999.99"));

    Customer entity = CustomerMapper.toNewEntity(req);

    assertThat(entity.getCode()).isEqualTo("C777");
    assertThat(entity.getCompanyName()).isEqualTo("Seven");
    assertThat(entity.getBillingAddress()).isNull();
    assertThat(entity.getTaxCode()).isEqualTo("T-777");
    assertThat(entity.getCreditLimit()).isEqualByComparingTo("999.99");
  }

  @Test
  @DisplayName("toNewEntity:  creditLimit が null の場合は 0 に初期化される")
  void toNewEntity_creditLimitIsNull() {
    CustomerRequest req = new CustomerRequest();
    req.setCode("  C777 ");
    req.setCompanyName("  Seven  ");
    req.setBillingAddress("New Co");
    req.setTaxCode("  T-777 ");
    req.setCreditLimit(null);

    Customer entity = CustomerMapper.toNewEntity(req);

    assertThat(entity.getCode()).isEqualTo("C777");
    assertThat(entity.getCompanyName()).isEqualTo("Seven");
    assertThat(entity.getBillingAddress()).isEqualTo("New Co");
    assertThat(entity.getTaxCode()).isEqualTo("T-777");
    assertThat(entity.getCreditLimit()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("toResponse / toResponseList: Entity が Response に正しく写像される")
  void toResponse_and_toResponseList() {
    Customer entity = new Customer();
    entity.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
    entity.setCode("C123");
    entity.setCompanyName("Acme");
    entity.setBillingAddress("Tokyo");
    entity.setTaxCode("T-123");
    entity.setCreditLimit(new BigDecimal("100.00"));

    CustomerResponse r = CustomerMapper.toResponse(entity);
    assertThat(r.getCode()).isEqualTo("C123");
    assertThat(r.getCompanyName()).isEqualTo("Acme");
    assertThat(r.getBillingAddress()).isEqualTo("Tokyo");
    assertThat(r.getTaxCode()).isEqualTo("T-123");
    assertThat(r.getCreditLimit()).isEqualByComparingTo("100.00");

    List<CustomerResponse> list = CustomerMapper.toResponseList(List.of(entity));
    assertThat(list).hasSize(1);
    assertThat(list.get(0).getCode()).isEqualTo("C123");
  }
}
