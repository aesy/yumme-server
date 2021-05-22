ALTER TABLE `recipe`
    ADD COLUMN `prep_time_bi` bigint NULL;

UPDATE `recipe`
    SET `prep_time_bi` = `prep_time` * 1000000000; # Seconds to nanos

ALTER TABLE `recipe`
    DROP COLUMN `prep_time`,
    CHANGE COLUMN `prep_time_bi` `prep_time` bigint NOT NULL;

ALTER TABLE `recipe`
    ADD COLUMN `cook_time_bi` bigint NULL;

UPDATE `recipe`
    SET `cook_time_bi` = `cook_time` * 1000000000; # Seconds to nanos

ALTER TABLE `recipe`
    DROP COLUMN `cook_time`,
    CHANGE COLUMN `cook_time_bi` `cook_time` bigint NOT NULL;
