// src/main/java/com/prjt2cs/project/repository/NotificationRepository.java
package com.prjt2cs.project.repository;

import com.prjt2cs.project.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByPuit_PuitId(String puitId);
    List<Notification> findByIsReadFalse();
    List<Notification> findByType(Notification.Type type);
    List<Notification> findByOrderByTimestampDesc();
    long countByIsReadFalse();
    boolean existsByTitleAndPuit_PuitId(String title, String puitId);
}