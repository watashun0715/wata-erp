package com.example.wataerp.domain.customer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.wataerp.domain.customer.dto.CustomerRequest;
import com.example.wataerp.domain.customer.dto.CustomerResponse;
import com.example.wataerp.domain.customer.entity.Customer;
import com.example.wataerp.domain.customer.repository.CustomerRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;

class CustomerServiceTest {

  private CustomerRepository repo;
  private CustomerService service;

  @BeforeEach
  void setup() {
    repo = mock(CustomerRepository.class);
    service = new CustomerService(repo);
  }

  // ======================================================
  // 正常系
  // ======================================================

  @Test
  @DisplayName("create - 新規顧客登録が正常に行われる")
  void create_success() {
    // given
    CustomerRequest req = new CustomerRequest();
    req.setCode("C001");
    req.setName("Acme");
    req.setBillingAddress("Tokyo");
    req.setTaxCode("T123");
    req.setCreditLimit(new BigDecimal("100.00"));

    when(repo.existsByCode("C001")).thenReturn(false);
    when(repo.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

    // when
    CustomerResponse res = service.create(req);

    // then
    ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
    verify(repo).save(captor.capture());
    Customer saved = captor.getValue();

    assertThat(saved.getCode()).isEqualTo("C001");
    assertThat(saved.getId()).isNotNull();
    assertThat(res.getName()).isEqualTo("Acme");
  }

  @Test
  @DisplayName("getCustomers - 引数nullなら全件取得が呼ばれる")
  void getCustomers_all_success() {
    Customer customer = new Customer();
    customer.setId(UUID.randomUUID());
    customer.setCode("C001");
    customer.setCompanyName("Acme");
    customer.setBillingAddress("Tokyo");
    customer.setTaxCode("T123");
    customer.setCreditLimit(new BigDecimal("100"));
    when(repo.findAll()).thenReturn(List.of(customer));

    List<CustomerResponse> result = service.getCustomers(null);

    assertThat(result).hasSize(1);
    verify(repo, times(1)).findAll();
  }

  @Test
  @DisplayName("getCustomers - コード指定で正常に取得")
  void getCustomers_byCodes_success() {
    Customer customer1 = new Customer();
    customer1.setId(UUID.randomUUID());
    customer1.setCode("C001");
    customer1.setCompanyName("Acme");
    customer1.setBillingAddress("Tokyo");
    customer1.setTaxCode("T123");
    customer1.setCreditLimit(new BigDecimal("100"));

    Customer customer2 = new Customer();
    customer2.setId(UUID.randomUUID());
    customer2.setCode("C001");
    customer2.setCompanyName("Acme");
    customer2.setBillingAddress("Tokyo");
    customer2.setTaxCode("T123");
    customer2.setCreditLimit(new BigDecimal("100"));
    List<Customer> list = List.of(customer1, customer2);
    when(repo.findByCodeIn(List.of("C001", "C002"))).thenReturn(list);

    List<CustomerResponse> res = service.getCustomers(List.of("C001", "C002"));

    assertThat(res).hasSize(2);
    verify(repo).findByCodeIn(List.of("C001", "C002"));
  }

  // ======================================================
  // 異常系
  // ======================================================

  @Test
  @DisplayName("create - 重複コードの場合はCONFLICTをスロー")
  void create_conflict_throwsException() {
    CustomerRequest req = new CustomerRequest();
    req.setCode("C001");
    req.setName("Acme");

    when(repo.existsByCode("C001")).thenReturn(true);

    assertThatThrownBy(() -> service.create(req))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("顧客コードが重複しています");
  }

  @Test
  @DisplayName("getCustomers - 指定コードの一部が存在しない場合はNOT_FOUNDをスロー")
  void getCustomers_missingCode_throwsException() {
    List<String> codes = List.of("C001", "C002");
    Customer customer = new Customer();
    customer.setId(UUID.randomUUID());
    customer.setCode("C001");
    customer.setCompanyName("Acme");
    customer.setBillingAddress("Tokyo");
    customer.setTaxCode("T123");
    customer.setCreditLimit(new BigDecimal("100"));
    when(repo.findByCodeIn(codes)).thenReturn(List.of(customer));

    assertThatThrownBy(() -> service.getCustomers(codes))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("見つかりません");
  }
}
