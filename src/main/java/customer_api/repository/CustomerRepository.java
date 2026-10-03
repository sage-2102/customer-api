package customer_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import customer_api.entity.Customer;
import customer_api.entity.CustomerStatus;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    List<Customer> findByStatus(CustomerStatus status);

    @Query("""
            select c from Customer c
            where c.status in :statuses
              and (lower(c.firstName) like lower(concat('%', :name, '%'))
                   or lower(c.lastName) like lower(concat('%', :name, '%')))
            """)
    List<Customer> search(@Param("statuses") List<CustomerStatus> statuses,
                          @Param("name") String name);
}