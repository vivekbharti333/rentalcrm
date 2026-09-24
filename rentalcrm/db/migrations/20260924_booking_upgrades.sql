-- Apply once before deploying the backend. Take the normal production backup first.
-- Existing rows remain unchanged; no data backfill or report replacement is needed.
ALTER TABLE lead_details
    ADD COLUMN upgrade_root_id BIGINT NULL,
    ADD COLUMN upgrade_previous_id BIGINT NULL,
    ADD COLUMN upgrade_request_id VARCHAR(36) NULL,
    ADD COLUMN upgrade_old_total BIGINT NULL,
    ADD COLUMN upgrade_new_total BIGINT NULL,
    ADD COLUMN upgrade_received_margin BIGINT NULL,
    ADD COLUMN upgrade_snapshot LONGTEXT NULL,
    ADD INDEX idx_lead_upgrade_root (upgrade_root_id),
    ADD UNIQUE INDEX uq_lead_upgrade_previous (upgrade_previous_id),
    ADD UNIQUE INDEX uq_lead_upgrade_request (upgrade_request_id);
