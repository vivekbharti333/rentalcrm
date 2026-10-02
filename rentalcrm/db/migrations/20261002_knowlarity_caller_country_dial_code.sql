-- Apply before deploying the explicit caller country-code field.
ALTER TABLE knowlarity_call_log
    ADD COLUMN caller_country_dial_code VARCHAR(8) NULL;

-- Preserve the country codes already saved in the legacy caller field.
UPDATE knowlarity_call_log
SET caller_country_dial_code = country_dial_code
WHERE caller_country_dial_code IS NULL;
