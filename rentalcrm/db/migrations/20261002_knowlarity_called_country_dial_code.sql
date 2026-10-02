-- Apply before deploying the Knowlarity called-number split.
ALTER TABLE knowlarity_call_log
    ADD COLUMN called_country_dial_code VARCHAR(8) NULL;
