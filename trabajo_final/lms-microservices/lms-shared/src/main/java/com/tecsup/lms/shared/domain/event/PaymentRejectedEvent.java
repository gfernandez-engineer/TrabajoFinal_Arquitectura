package com.tecsup.lms.shared.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRejectedEvent extends DomainEvent {

    private Long paymentId;
    private Long enrollmentId;
    private String reason;

    public PaymentRejectedEvent(Long paymentId, Long enrollmentId, String reason) {
        super("PaymentRejectedEvent");
        this.paymentId = paymentId;
        this.enrollmentId = enrollmentId;
        this.reason = reason;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(enrollmentId);
    }
}
