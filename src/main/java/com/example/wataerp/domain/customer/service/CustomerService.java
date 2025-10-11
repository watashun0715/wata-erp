package com.example.wataerp.domain.customer.service;

import com.example.wataerp.domain.customer.dto.CustomerRequest;
import com.example.wataerp.domain.customer.dto.CustomerResponse;
import com.example.wataerp.domain.customer.entity.Customer;
import com.example.wataerp.domain.customer.mapper.CustomerMapper;
import com.example.wataerp.domain.customer.repository.CustomerRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @Transactional
  public CustomerResponse create(CustomerRequest request) {
    if (customerRepository.existsByCode(request.getCode())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "顧客コードが重複しています: " + request.getCode());
    }

    Customer entity = CustomerMapper.toNewEntity(request);
    entity.setId(UUID.randomUUID());

    Customer savedCustomer = customerRepository.save(entity);

    return CustomerMapper.toResponse(savedCustomer);
  }

  @Transactional(readOnly = true)
  public List<CustomerResponse> getCustomers(List<String> codes) {
    List<Customer> customers;

    if (codes == null || codes.isEmpty()) {
      // 全件取得
      customers = customerRepository.findAll();
    } else {
      // 指定コード取得
      customers = customerRepository.findByCodeIn(codes);

      // 存在チェック（不足コード検出）
      if (customers.size() != codes.size()) {
        // Setにすることによって、Listよりも数十万行処理する場合などに性能有利
        Set<String> found = customers.stream().map(Customer::getCode).collect(Collectors.toSet());
        List<String> missing = codes.stream().filter(code -> !found.contains(code)).toList();
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "以下の顧客コードが見つかりません: " + String.join(", ", missing));
      }
    }
    return CustomerMapper.toResponseList(customers);
  }

  @Transactional(readOnly = true)
  public CustomerResponse getCustomer(String code) {
    return getCustomers(List.of(code)).get(0);
  }
}
