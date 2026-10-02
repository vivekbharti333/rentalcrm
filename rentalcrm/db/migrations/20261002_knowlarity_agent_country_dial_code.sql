-- Apply before deploying the Knowlarity agent-number split.
ALTER TABLE knowlarity_call_log
    ADD COLUMN agent_country_dial_code VARCHAR(8) NULL;
