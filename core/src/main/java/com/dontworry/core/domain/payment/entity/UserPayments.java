package com.dontworry.core.domain.payment.entity;

import com.dontworry.core.domain.user.entity.Users;
import com.dontworry.core.domain.payment.enums.PGProvider;
import com.dontworry.core.domain.payment.enums.PaymentMethod;
import com.dontworry.core.domain.payment.enums.PaymentStatus;
import com.dontworry.core.domain.payment.enums.UsageType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_payments")
@EntityListeners(AuditingEntityListener.class)
public class  UserPayments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private Users user;

    @Column
    private String customerKey;

    @Column(nullable = false)
    private String orderId;

    @Column
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    @Column
    private PGProvider pgProvider;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UsageType usageType;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private String failReason;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime refundedAt;

    public static UserPayments build(Users user, String orderId, String customerKey, UsageType usageType, Long amount, PaymentMethod paymentMethod) {
        return UserPayments.builder()
                .user(user)
                .customerKey(customerKey)
                .orderId(orderId)
                .usageType(usageType)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.READY)
                .build();
    }

    public void failed(String failReason) {
        this.paymentStatus = PaymentStatus.ABORTED;
        this.failReason = failReason;
    }

}
