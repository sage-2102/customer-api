package customer_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import customer_api.dto.CustomerRequest;
import customer_api.dto.CustomerResponse;
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
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Customer with email " + request.email() + " already exists");
        }
        Customer customer = new Customer();
        applyRequest(customer, request);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerResponse::from)
                .toList();
    }

    public CustomerResponse getCustomerById(Long id) {
        return CustomerResponse.from(findCustomerOrThrow(id));
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer existing = findCustomerOrThrow(id);

        boolean emailChanged = !existing.getEmail().equals(request.email());
        if (emailChanged && customerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Customer with email " + request.email() + " already exists");
        }

        applyRequest(existing, request);
        return CustomerResponse.from(customerRepository.save(existing));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.delete(findCustomerOrThrow(id));
    }

    private Customer findCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id " + id));
    }

    private void applyRequest(Customer customer, CustomerRequest request) {
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
        if (request.status() != null) {
            customer.setStatus(request.status());
        }
    }
}