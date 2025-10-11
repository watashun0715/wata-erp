package com.example.wataerp.interfaces.api.v1;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.wataerp.domain.customer.dto.CustomerRequest;
import com.example.wataerp.domain.customer.dto.CustomerResponse;
import com.example.wataerp.domain.customer.service.CustomerService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(CustomerApiController.class)
@WithMockUser
class CustomerApiControllerTest {

  @Autowired private MockMvc mvc;

  @MockitoBean private CustomerService service;

  // ======================================================
  // 正常系
  // ======================================================

  @Test
  @DisplayName("POST /api/v1/customers - 顧客登録 正常系")
  void createCustomer_success() throws Exception {
    CustomerResponse response = new CustomerResponse();
    response.setCode("C001");
    response.setName("Acme");
    response.setBillingAddress("Tokyo");
    response.setTaxCode("T1234567890123");
    response.setCreditLimit(new BigDecimal("100.00"));

    Mockito.when(service.create(any(CustomerRequest.class))).thenReturn(response);

    mvc.perform(
            post("/api/v1/customers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "code": "C001",
                        "name": "Acme",
                        "billingAddress": "Tokyo",
                        "taxCode": "T1234567890123",
                        "creditLimit": 100.00
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(
            header()
                .string("Location", org.hamcrest.Matchers.containsString("/api/v1/customers/C001")))
        .andExpect(jsonPath("$.code").value("C001"))
        .andExpect(jsonPath("$.name").value("Acme"));
  }

  @Test
  @DisplayName("GET /api/v1/customers/all - 全件取得 正常系")
  void getAllCustomers_success() throws Exception {
    CustomerResponse response1 = new CustomerResponse();
    response1.setCode("C001");
    response1.setName("Acme");
    response1.setBillingAddress("Tokyo");
    response1.setTaxCode("T123");
    response1.setCreditLimit(new BigDecimal("100.00"));

    CustomerResponse response2 = new CustomerResponse();
    response2.setCode("C002");
    response2.setName("Beta");
    response2.setBillingAddress("Osaka");
    response2.setTaxCode("T456");
    response2.setCreditLimit(new BigDecimal("200.00"));

    List<CustomerResponse> list = List.of(response1, response2);
    Mockito.when(service.getCustomers(null)).thenReturn(list);

    mvc.perform(get("/api/v1/customers/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].code").value("C001"))
        .andExpect(jsonPath("$[1].code").value("C002"));
  }

  @Test
  @DisplayName("GET /api/v1/customers/{code} - 単一取得 正常系")
  void getCustomerByCode_success() throws Exception {
    CustomerResponse response = new CustomerResponse();
    response.setCode("C001");
    response.setName("Acme");
    response.setBillingAddress("Tokyo");
    response.setTaxCode("T1234567890123");
    response.setCreditLimit(new BigDecimal("100.00"));
    Mockito.when(service.getCustomer("C001")).thenReturn(response);

    mvc.perform(get("/api/v1/customers/C001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("C001"))
        .andExpect(jsonPath("$.name").value("Acme"));
  }

  @Test
  @DisplayName("GET /api/v1/customers/by-codes - 複数取得 正常系")
  void getCustomersByCodes_success() throws Exception {
    CustomerResponse response1 = new CustomerResponse();
    response1.setCode("C001");
    response1.setName("Acme");
    response1.setBillingAddress("Tokyo");
    response1.setTaxCode("T123");
    response1.setCreditLimit(new BigDecimal("100.00"));

    CustomerResponse response2 = new CustomerResponse();
    response2.setCode("C002");
    response2.setName("Beta");
    response2.setBillingAddress("Osaka");
    response2.setTaxCode("T456");
    response2.setCreditLimit(new BigDecimal("200.00"));
    List<CustomerResponse> list = List.of(response1, response2);
    Mockito.when(service.getCustomers(List.of("C001", "C002"))).thenReturn(list);

    mvc.perform(get("/api/v1/customers/by-codes").param("codes", "C001", "C002"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].code").value("C001"))
        .andExpect(jsonPath("$[1].code").value("C002"));
  }

  // ======================================================
  // 異常系
  // ======================================================

  @Test
  @DisplayName("POST /api/v1/customers - 重複登録時 409エラー")
  void createCustomer_conflict() throws Exception {
    Mockito.when(service.create(any(CustomerRequest.class)))
        .thenThrow(
            new ResponseStatusException(
                org.springframework.http.HttpStatus.CONFLICT, "顧客コードが重複しています: C001"));

    mvc.perform(
            post("/api/v1/customers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "code": "C001",
                        "name": "Acme",
                        "billingAddress": "Tokyo",
                        "taxCode": "T1234567890123",
                        "creditLimit": 100.00
                    }
                    """))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.message").value(org.hamcrest.Matchers.containsString("顧客コードが重複しています")));
  }

  @Test
  @DisplayName("GET /api/v1/customers/{code} - 存在しない顧客 404エラー")
  void getCustomerByCode_notFound() throws Exception {
    Mockito.when(service.getCustomer("C999"))
        .thenThrow(
            new ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Customer not found"));

    mvc.perform(get("/api/v1/customers/C999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Customer not found"));
  }

  @Test
  @DisplayName("GET /api/v1/customers/by-codes - パラメータ不足 400エラー")
  void getCustomersByCodes_missingParam() throws Exception {
    mvc.perform(get("/api/v1/customers/by-codes")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /api/v1/customers - バリデーションエラー（空のJSON）")
  void createCustomer_validationError() throws Exception {
    mvc.perform(
            post("/api/v1/customers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest());
  }
}
