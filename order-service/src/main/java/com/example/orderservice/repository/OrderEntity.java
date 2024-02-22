package com.example.orderservice.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String productId;
    @Column(nullable = false, length = 120)
    private Integer qty;
    @Column(nullable = false, length = 120)
    private Integer unitPrice;
    @Column(nullable = false, length = 120)
    private Integer totalPrice;

    @Column(nullable = false, length = 120)
    private String orderId;
    @Column(nullable = false, length = 120)
    private String userId;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Date createdAt;
}
