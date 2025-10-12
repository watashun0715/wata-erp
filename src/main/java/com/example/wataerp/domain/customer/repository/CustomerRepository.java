package com.example.wataerp.domain.customer.repository;

import com.example.wataerp.domain.customer.entity.Customer;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  // 複数顧客情報取得
  List<Customer> findByCodeIn(List<String> codes);

  // 指定したコードの顧客がいるかの判定
  boolean existsByCode(String code);
}
