package org.example.libraffstore.dto.request;

import lombok.Data;

@Data
public class StoreRequest {

    private String name;
    private String address;
    private String phone;
    private Long companyId;
}
