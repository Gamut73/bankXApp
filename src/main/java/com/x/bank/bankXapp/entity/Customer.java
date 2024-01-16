package com.x.bank.bankXapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    private String email;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "accountOwner")
    private List<Account> accounts;
}
