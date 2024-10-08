package com.sparta.delivery.product;

import com.sparta.delivery.common.TimeStamped;
import com.sparta.delivery.order.Order;
import com.sparta.delivery.shop.entity.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private boolean isPublic = true;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", referencedColumnName = "shopId")
    private Store store;

    @Builder
    public Product(String productName, String description, int price, Store store) {
        this.store = store;
        this.productName = productName;
        this.description = description;
        this.price = price;
    }
}

