package com.tecsup.lms.shared.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CourseCreatedEvent extends DomainEvent {

    private Long courseId;
    private String title;
    private String instructor;

    public CourseCreatedEvent(Long courseId, String title, String instructor) {
        super("CourseCreatedEvent");
        this.courseId = courseId;
        this.title = title;
        this.instructor = instructor;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(courseId);
    }
}
