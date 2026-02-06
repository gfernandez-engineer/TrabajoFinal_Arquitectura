package com.tecsup.lms.shared.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CoursePublishedEvent extends DomainEvent {

    private Long courseId;
    private String title;

    public CoursePublishedEvent(Long courseId, String title) {
        super("CoursePublishedEvent");
        this.courseId = courseId;
        this.title = title;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(courseId);
    }
}
