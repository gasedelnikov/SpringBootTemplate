create schema if not exists ${defaultSchema};
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE OR REPLACE FUNCTION ${defaultSchema}.audit() RETURNS TRIGGER AS $audit$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        NEW.create_date := now();
        NEW.update_date := current_timestamp;
    ELSIF (TG_OP = 'UPDATE') THEN
        NEW.update_date := current_timestamp;
    END IF;
    RETURN NEW;
END;
$audit$ LANGUAGE plpgsql;


CREATE TABLE ${defaultSchema}.USR(
        id                      UUID            NOT NULL constraint user_pk primary key,
        email                   VARCHAR(255)    NOT NULL,
        phone_number            VARCHAR(255)    NULL,
        name                    VARCHAR(255)    NULL,
        surname                 VARCHAR(255)    NULL,
        password                VARCHAR(255)    NOT NULL,
        description             VARCHAR(1000)   NULL,
        status                  NUMERIC(2)      NOT NULL,
        create_date             timestamp       NULL,
        update_date             timestamp       NULL
);
create unique index if not exists user_name_index on ${defaultSchema}.USR(id);
CREATE TRIGGER audit BEFORE INSERT OR UPDATE ON ${defaultSchema}.USR FOR EACH ROW EXECUTE PROCEDURE ${defaultSchema}.audit();

