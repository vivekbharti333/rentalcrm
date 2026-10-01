-- Apply before deploying the Knowlarity caller number split.
ALTER TABLE knowlarity_call_log
    ADD COLUMN country_dial_code VARCHAR(8) NULL;
