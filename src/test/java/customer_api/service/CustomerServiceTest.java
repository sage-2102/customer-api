package customer_api.service;

import customer_api.dto.CustomerRequest;
import customer_api.dto.CustomerResponse;
import customer_api.entity.Customer;
import customer_api.entity.CustomerStatus;
import customer_api.exception.DuplicateResourceException;
import customer_api.exception.ResourceNotFoundException;
import customer_api.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer(Long id, String email) {
        Customer c = new Customer();
        c.setId(id);
        c.setFirstName("Asha");
        c.setLastName("Verma");
        c.setEmail(email);
        return c;
    }

    @Test
    void createCustomer_savesAndReturnsResponse() {
        CustomerRequest request = new CustomerRequest(
                "Asha", "Verma", "asha@example.com", "9876543210", "Pune", null);
        when(customerRepository.existsByEmail("asha@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        CustomerResponse response = customerService.createCustomer(request);

        assertEquals(1L, response.id());
        assertEquals("Asha", response.firstName());
        assertEquals(CustomerStatus.ACTIVE, response.status());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_duplicateEmail_throwsAndDoesNotSave() {
        CustomerRequest request = new CustomerRequest(
                "Asha", "Verma", "asha@example.com", null, null, null);
        when(customerRepository.existsByEmail("asha@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> customerService.createCustomer(request));

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerById_notFound_throws() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.getCustomerById(99L));
    }

    @Test
    void updateCustomer_emailBelongsToSomeoneElse_throws() {
        Customer existing = customer(1L, "old@example.com");
        CustomerRequest request = new CustomerRequest(
                "Asha", "Verma", "taken@example.com", null, null, null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> customerService.updateCustomer(1L, request));

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_existing_deletesIt() {
        Customer existing = customer(1L, "asha@example.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));

        customerService.deleteCustomer(1L);

        verify(customerRepository).delete(existing);
    }
}