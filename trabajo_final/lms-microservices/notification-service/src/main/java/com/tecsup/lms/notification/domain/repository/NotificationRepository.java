package com.tecsup.lms.notification.domain.repository;

import com.tecsup.lms.notification.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findBySentFalse();

    List<Notification> findByUserIdAndSentFalse(Long userId);
}
