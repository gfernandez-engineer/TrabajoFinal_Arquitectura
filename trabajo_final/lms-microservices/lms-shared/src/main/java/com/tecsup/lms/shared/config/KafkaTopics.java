package com.tecsup.lms.shared.config;

public final class KafkaTopics {

    private KafkaTopics() {}

    // Course events
    public static final String COURSE_EVENTS = "lms.course.events";
    public static final String DLQ_COURSE_EVENTS = "lms.course.events.dlq";

    // Enrollment events
    public static final String ENROLLMENT_EVENTS = "lms.enrollment.events";
    public static final String DLQ_ENROLLMENT_EVENTS = "lms.enrollment.events.dlq";

    // Payment events
    public static final String PAYMENT_EVENTS = "lms.payment.events";
    public static final String DLQ_PAYMENT_EVENTS = "lms.payment.events.dlq";
}
