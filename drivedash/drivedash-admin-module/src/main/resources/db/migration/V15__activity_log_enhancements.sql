-- =============================================================================
-- V15 – Activity log enhancements
-- • Add `action` column (CREATE / UPDATE / DELETE / LOGIN / LOGOUT / etc.)
-- • Make `edited_by` nullable (session events may not have a DB-tracked actor)
-- • Make `logable_id` nullable (LOGIN / LOGOUT have no specific entity record)
-- =============================================================================

ALTER TABLE activity_logs
    MODIFY COLUMN edited_by  CHAR(36) NULL    COMMENT 'UUID of the acting user (NULL for anonymous/system actions)',
    MODIFY COLUMN logable_id CHAR(36) NULL    COMMENT 'UUID of the modified entity (NULL for session events)',
    ADD    COLUMN action      VARCHAR(30) NULL COMMENT 'Action label: CREATE, UPDATE, DELETE, LOGIN, LOGOUT' AFTER logable_type;

CREATE INDEX idx_al_action ON activity_logs (action);
