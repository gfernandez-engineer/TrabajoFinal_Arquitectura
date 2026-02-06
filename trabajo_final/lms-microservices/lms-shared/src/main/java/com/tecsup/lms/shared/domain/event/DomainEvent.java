package com.tecsup.lms.shared.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType",
    defaultImpl = DomainEvent.class
)
@JsonSubTypes({
    // Course Events
    @JsonSubTypes.Type(value = CourseCreatedEvent.class, name = "CourseCreatedEvent"),
    @JsonSubTypes.Type(value = CoursePublishedEvent.class, name = "CoursePublishedEvent"),
    // Enrollment Events
    @JsonSubTypes.Type(value = EnrollmentCreatedEvent.class, name = "EnrollmentCreatedEvent"),
    @JsonSubTypes.Type(value = EnrollmentConfirmedEvent.class, name = "EnrollmentConfirmedEvent"),
    @JsonSubTypes.Type(value = EnrollmentCancelledEvent.class, name = "EnrollmentCancelledEvent"),
    // Payment Events
    @JsonSubTypes.Type(value = PaymentApprovedEvent.class, name = "PaymentApprovedEvent"),
    @JsonSubTypes.Type(value = PaymentRejectedEvent.class, name = "PaymentRejectedEvent")
})
public class DomainEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime occurredOn;

    public DomainEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.occurredOn = LocalDateTime.now();
    }

    public String getAggregateId() {
        return eventId;
    }
}
