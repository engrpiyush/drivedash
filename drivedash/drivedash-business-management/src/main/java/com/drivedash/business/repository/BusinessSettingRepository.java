package com.drivedash.business.repository;

import com.drivedash.business.entity.BusinessSetting;
import com.drivedash.business.entity.SettingsType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access for {@link BusinessSetting}.
 * The primary query pattern is always by ({@code keyName}, {@code settingsType}) –
 * mirroring Laravel's {@code findOneBy(['key_name' => ..., 'settings_type' => ...])}.
 */
@Repository
public interface BusinessSettingRepository extends JpaRepository<BusinessSetting, UUID> {

    Optional<BusinessSetting> findByKeyNameAndSettingsType(String keyName, SettingsType settingsType);

    List<BusinessSetting> findAllBySettingsType(SettingsType settingsType);

    boolean existsByKeyNameAndSettingsType(String keyName, SettingsType settingsType);
}