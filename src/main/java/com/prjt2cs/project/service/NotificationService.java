// src/main/java/com/prjt2cs/project/service/NotificationService.java
package com.prjt2cs.project.service;

import com.prjt2cs.project.model.*;
import com.prjt2cs.project.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final PuitRepository puitRepository;

    public NotificationService(NotificationRepository notificationRepository, 
                             PuitRepository puitRepository) {
        this.notificationRepository = notificationRepository;
        this.puitRepository = puitRepository;
    }

    @Transactional
public Notification createNotification(
        Notification.Type type,
        Notification.Category category,
        String title,
        String description,
        String puitId,
        Long reportId // 👈 ajout du reportId
) {
    Notification notification = new Notification();
    notification.setType(type);
    notification.setCategory(category);
    notification.setTitle(title);
    notification.setDescription(description);
    notification.setSeverity(determineSeverity(type));
    notification.setTimestamp(LocalDateTime.now());
    notification.setRead(false);

    // Associer le puits si fourni
    if (puitId != null) {
        Puit puit = puitRepository.findById(puitId)
            .orElseThrow(() -> new RuntimeException("Puit non trouvé"));
        notification.setPuit(puit);
    }

    // Associer l'ID du rapport si fourni
    if (reportId != null) {
        notification.setReportId(reportId);
    }

    return notificationRepository.save(notification);
}


    private Notification.Severity determineSeverity(Notification.Type type) {
        return switch (type) {
            case CRITICAL -> Notification.Severity.CRITICAL;
            case WARNING -> Notification.Severity.HIGH;
            case ACTION -> Notification.Severity.MEDIUM;
            default -> Notification.Severity.LOW;
        };
    }

    @Transactional
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void markAllAsRead() {
        notificationRepository.findAll().forEach(notification -> {
            if (!notification.isRead()) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        });
    }

    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalse();
    }

    public long getUnreadCount() {
        return notificationRepository.countByIsReadFalse();
    }

public List<Notification> getAllNotifications() {
    return notificationRepository.findByOrderByTimestampDesc();
}
}