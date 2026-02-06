package com.tecsup.lms.shared.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EnrollmentCreatedEvent extends DomainEvent {

    private Long enrollmentId;
    private Long userId;
    private Long courseId;
    private String courseTitle;
    private String userEmail;

    public EnrollmentCreatedEvent(Long enrollmentId, Long userId, Long courseId,
                                   String courseTitle, String userEmail) {
        super("EnrollmentCreatedEvent");
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.userEmail = userEmail;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(enrollmentId);
    }
}
