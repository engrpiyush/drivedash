package com.drivedash.admin.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper that loads a JPA entity by its UUID primary key inside its own
 * read-only transaction and immediately serialises it to a plain
 * {@code Map<String, Object>} for use as an audit snapshot.
 *
 * <p>Running in a dedicated transaction is important for two reasons:
 * <ol>
 *   <li>The entity must be read <em>before</em> the mutating service method
 *       starts its own transaction, so the snapshot reflects the state that
 *       is about to change.</li>
 *   <li>Jackson serialisation (which may touch lazy-loaded associations)
 *       must happen while the session is still open.</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BeforeStateLoader {

    @PersistenceContext
    private EntityManager entityManager;

    private final ObjectMapper objectMapper;

    /**
     * Finds {@code entityClass} with the given {@code id} and returns a JSON-like
     * map snapshot, or {@code null} if the entity does not exist.
     *
     * <p>This method is annotated {@link Transactional @Transactional(readOnly=true)};
     * Spring will create (or join) a transaction so that the {@link EntityManager}
     * and any lazy proxies are usable during Jackson serialisation.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> loadSnapshot(Class<?> entityClass, UUID id) {
        try {
            Object entity = entityManager.find(entityClass, id);
            if (entity == null) {
                return null;
            }
            return toMap(entity);
        } catch (Exception ex) {
            log.debug("BeforeStateLoader: could not load snapshot for {} id={}: {}",
                    entityClass.getSimpleName(), id, ex.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(Object entity) {
        try {
            return objectMapper.convertValue(entity, Map.class);
        } catch (Exception ex) {
            return Map.of("value", entity.toString());
        }
    }
}
