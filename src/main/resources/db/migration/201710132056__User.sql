CREATE TABLE `user` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `user_name` varchar(64) NOT NULL UNIQUE,
    `display_name` varchar(64) NOT NULL,
    `password` char(72) NOT NULL,
    `created_at` int unsigned NOT NULL DEFAULT 0,
    `modified_at` int unsigned NOT NULL DEFAULT 0,
    `deleted_at` int unsigned DEFAULT NULL,
    `suspended_at` int unsigned DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `user`
    ADD INDEX (`user_name`),
    ADD INDEX (`user_name`, `password`);

DELIMITER $$
CREATE TRIGGER `trig_user_insert` BEFORE INSERT ON `user`
FOR EACH ROW
    BEGIN
        IF (new.`created_at` = 0) THEN
            SET new.`created_at` = UNIX_TIMESTAMP();
        END IF;

        IF (new.`modified_at` = 0) THEN
            SET new.`modified_at` = UNIX_TIMESTAMP();
        END IF;
    END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER `trig_user_update` BEFORE UPDATE ON `user`
FOR EACH ROW
    BEGIN
        IF (new.`modified_at` = 0) THEN
            SET new.`modified_at` = UNIX_TIMESTAMP();
        END IF;
    END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE `proc_user_is_deleted` (IN `user_id` int unsigned)
BEGIN
    SELECT
        EXISTS(
            SELECT *
            FROM `user`
            WHERE `id` = `user_id`
              AND `deleted_at` IS NOT NULL
        );
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE `proc_user_is_suspended` (IN `user_id` int unsigned)
BEGIN
    SELECT
        EXISTS(
            SELECT *
            FROM `user`
            WHERE `id` = `user_id`
              AND `suspended_at` IS NOT NULL
        );
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE `proc_user_delete` (IN `user_id` int unsigned)
BEGIN
    UPDATE `user`
    SET `deleted_at` = UNIX_TIMESTAMP()
    WHERE `id` = `user_id`;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE `proc_user_suspend` (IN `user_id` int unsigned)
BEGIN
    UPDATE `user`
    SET `suspended_at` = UNIX_TIMESTAMP()
    WHERE `id` = `user_id`;
END $$
DELIMITER ;

ALTER TABLE `recipe`
    ADD COLUMN `author` int unsigned NOT NULL,
    ADD INDEX (`author`),
    ADD CONSTRAINT
        FOREIGN KEY (`author`) REFERENCES `user` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE;
