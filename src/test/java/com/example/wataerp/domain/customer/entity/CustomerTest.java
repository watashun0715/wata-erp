package com.example.wataerp.domain.customer.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerTest {

  // --- setCreditLimit ---

  @Test
  @DisplayName("setCreditLimit: 非nullはスケール=2でHALF_UPに丸めて保持")
  void setCreditLimit_roundsHalfUpToScale2() {
    Customer c = new Customer();

    c.setCreditLimit(new BigDecimal("1.004"));
    assertThat(c.getCreditLimit()).isEqualByComparingTo("1.00");

    c.setCreditLimit(new BigDecimal("1.005")); // HALF_UP の境界
    assertThat(c.getCreditLimit()).isEqualByComparingTo("1.01");

    c.setCreditLimit(new BigDecimal("123")); // スケールなし → 2桁化
    assertThat(c.getCreditLimit()).isEqualByComparingTo("123.00");
  }

  @Test
  @DisplayName("setCreditLimit: nullは0に正規化してスケール=2で保持")
  void setCreditLimit_null_becomesZeroWithScale2() {
    Customer c = new Customer();
    c.setCreditLimit(null);
    assertThat(c.getCreditLimit()).isEqualByComparingTo("0.00");
  }

  // --- setActive ---

  @Test
  @DisplayName("setActive: true/falseを素直に反映する")
  void setActive_trueFalse() {
    Customer c = new Customer();

    c.setActive(Boolean.TRUE);
    assertThat(c.getActive()).isTrue();

    c.setActive(Boolean.FALSE);
    assertThat(c.getActive()).isFalse();
  }

  @Test
  @DisplayName("setActive: nullはtrueに正規化される")
  void setActive_null_becomesTrue() {
    Customer c = new Customer();
    c.setActive(null);
    assertThat(c.getActive()).isTrue();
  }

  // --- その他のgetter/setter も軽く通しておく（行カバレッジ加点用） ---

  @Test
  @DisplayName("その他のプロパティ: getter/setterが素直に動く")
  void otherProperties_getterSetter() {
    Customer c = new Customer();
    UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    c.setId(id);
    c.setCode("C001");
    c.setCompanyName("Acme");
    c.setBillingAddress("Tokyo");
    c.setTaxCode("T-123");
    c.setCreditLimit(new BigDecimal("10.50")); // ついでにもう一回

    assertThat(c.getId()).isEqualTo(id);
    assertThat(c.getCode()).isEqualTo("C001");
    assertThat(c.getCompanyName()).isEqualTo("Acme");
    assertThat(c.getBillingAddress()).isEqualTo("Tokyo");
    assertThat(c.getTaxCode()).isEqualTo("T-123");
    assertThat(c.getCreditLimit()).isEqualByComparingTo("10.50");
  }

  @Test
  @DisplayName("初期状態: active は true（フィールド初期値）")
  void defaultActive_isTrue() {
    Customer c = new Customer();
    assertThat(c.getActive()).isTrue();
  }
}
