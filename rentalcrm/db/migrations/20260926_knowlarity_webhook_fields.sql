-- Apply before deploying the updated Knowlarity webhook. Legacy columns remain for existing logs.
ALTER TABLE knowlarity_call_log
    ADD COLUMN call_date VARCHAR(32) NULL,
    ADD COLUMN call_time VARCHAR(32) NULL,
    ADD COLUMN caller_number VARCHAR(32) NULL,
    ADD COLUMN called_number VARCHAR(32) NULL,
    ADD COLUMN call_status VARCHAR(64) NULL,
    ADD COLUMN call_transfer_status VARCHAR(64) NULL,
    ADD COLUMN caller_duration VARCHAR(32) NULL,
    ADD COLUMN recording_url TEXT NULL,
    ADD COLUMN hangup_cause VARCHAR(255) NULL,
    ADD COLUMN menu_extension VARCHAR(64) NULL,
    ADD INDEX idx_knowlarity_call_status (call_uuid, call_status);
