package com.drivemond.business.service;

import com.drivemond.business.entity.SocialLink;
import com.drivemond.business.repository.SocialLinkRepository;
import com.drivemond.core.exception.DrivemondException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages landing-page social media links.
 * Mirrors Laravel's {@code SocialLinkService}.
 */
@Service
@RequiredArgsConstructor
public class SocialLinkService {

    private final SocialLinkRepository repository;

    @Transactional(readOnly = true)
    public List<SocialLink> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SocialLink> findAllActive() {
        return repository.findAllByActiveTrue();
    }

    @Transactional
    public SocialLink create(String name, String link) {
        return repository.save(SocialLink.builder()
                .name(name)
                .link(link)
                .active(true)
                .build());
    }

    @Transactional
    public SocialLink update(UUID id, String name, String link) {
        SocialLink socialLink = findOrThrow(id);
        socialLink.setName(name);
        socialLink.setLink(link);
        return repository.save(socialLink);
    }

    @Transactional
    public void updateStatus(UUID id, boolean active) {
        SocialLink socialLink = findOrThrow(id);
        socialLink.setActive(active);
        repository.save(socialLink);
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(findOrThrow(id));
    }

    private SocialLink findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> DrivemondException.notFound("Social link not found"));
    }
}
