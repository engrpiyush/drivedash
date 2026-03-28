package com.drivedash.business.repository;

import com.drivedash.business.entity.FirebasePushNotification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirebasePushNotificationRepository
        extends JpaRepository<FirebasePushNotification, Long> {

    Optional<FirebasePushNotification> findByName(String name);
}