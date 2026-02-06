package com.tecsup.lms.shared.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EnrollmentCancelledEvent extends DomainEvent {

    private Long enrollmentId;
    private Long userId;
    private Long courseId;
    private String reason;

    public EnrollmentCancelledEvent(Long enrollmentId, Long userId, Long courseId, String reason) {
        super("EnrollmentCancelledEvent");
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
        this.reason = reason;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(enrollmentId);
    }
}
