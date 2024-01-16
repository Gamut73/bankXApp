package com.x.bank.bankXapp.mapper;

import com.x.bank.bankXapp.dto.CustomerDto;
import com.x.bank.bankXapp.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer mapToEntity(CustomerDto customerDto);

    CustomerDto mapToDto(Customer customer);
}
