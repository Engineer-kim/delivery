package com.sparta.delivery.order;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "p_orders")
public class Order {
    @Id
    private UUID id;
}
