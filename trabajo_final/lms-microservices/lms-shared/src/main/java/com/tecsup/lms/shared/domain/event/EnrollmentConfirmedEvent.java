package com.tecsup.lms.shared.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EnrollmentConfirmedEvent extends DomainEvent {

    private Long enrollmentId;
    private Long userId;
    private Long courseId;

    public EnrollmentConfirmedEvent(Long enrollmentId, Long userId, Long courseId) {
        super("EnrollmentConfirmedEvent");
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(enrollmentId);
    }
}
