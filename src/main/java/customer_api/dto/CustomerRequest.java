package customer_api.dto;

import customer_api.entity.CustomerStatus;

public record CustomerRequest(
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        CustomerStatus status
) {
}