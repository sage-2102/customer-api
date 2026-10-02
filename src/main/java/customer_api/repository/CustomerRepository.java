package customer_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import customer_api.entity.Customer;
import customer_api.entity.CustomerStatus;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    List<Customer> findByStatus(CustomerStatus status);
}