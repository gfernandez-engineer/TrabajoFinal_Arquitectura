package com.tecsup.lms.shared.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PaymentApprovedEvent extends DomainEvent {

    private Long paymentId;
    private Long enrollmentId;
    private BigDecimal amount;
    private LocalDateTime paidAt;

    public PaymentApprovedEvent(Long paymentId, Long enrollmentId, BigDecimal amount, LocalDateTime paidAt) {
        super("PaymentApprovedEvent");
        this.paymentId = paymentId;
        this.enrollmentId = enrollmentId;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(enrollmentId);
    }
}
