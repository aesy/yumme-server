CREATE TABLE `refresh_token` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `value` varchar(64) NOT NULL UNIQUE,
    `user` int unsigned NOT NULL,
    `created_at` int unsigned NOT NULL DEFAULT 0,
    `modified_at` int unsigned NOT NULL DEFAULT 0,
    `revoked_at` int unsigned DEFAULT NULL,
    `last_used_at` int unsigned DEFAULT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user`) REFERENCES `user` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELIMITER $$
CREATE TRIGGER `trig_refresh_token_insert` BEFORE INSERT ON `refresh_token`
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
CREATE TRIGGER `trig_refresh_token_update` BEFORE UPDATE ON `refresh_token`
FOR EACH ROW
    BEGIN
        IF (new.`modified_at` = 0) THEN
            SET new.`modified_at` = UNIX_TIMESTAMP();
        END IF;
    END $$
DELIMITER ;
