package com.drivedash.business.repository;

import com.drivedash.business.entity.SocialLink;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, UUID> {

    List<SocialLink> findAllByActiveTrue();
}