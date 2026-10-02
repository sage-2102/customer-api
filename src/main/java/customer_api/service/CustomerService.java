package customer_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import customer_api.entity.Customer;
import customer_api.exception.DuplicateResourceException;
import customer_api.exception.ResourceNotFoundException;
import customer_api.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new DuplicateResourceException(
                    "Customer with email " + customer.getEmail() + " already exists");
        }
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id " + id));
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer updated) {
        Customer existing = getCustomerById(id);

        boolean emailChanged = !existing.getEmail().equals(updated.getEmail());
        if (emailChanged && customerRepository.existsByEmail(updated.getEmail())) {
            throw new DuplicateResourceException(
                    "Customer with email " + updated.getEmail() + " already exists");
        }

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setStatus(updated.getStatus());

        return customerRepository.save(existing);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer existing = getCustomerById(id);
        customerRepository.delete(existing);
    }
}