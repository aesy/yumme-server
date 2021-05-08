CREATE TABLE `image_upload` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `file_name` varchar(128) NOT NULL UNIQUE,
    `hash` char(32) NOT NULL,
    `width` int unsigned NOT NULL,
    `height` int unsigned NOT NULL,
    `created_at` int unsigned NOT NULL DEFAULT 0,
    `modified_at` int unsigned NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELIMITER $$
CREATE TRIGGER `trig_image_upload_insert` BEFORE INSERT ON `image_upload`
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
CREATE TRIGGER `trig_image_upload_update` BEFORE UPDATE ON `image_upload`
FOR EACH ROW
    BEGIN
        IF (new.`modified_at` = 0) THEN
            SET new.`modified_at` = UNIX_TIMESTAMP();
        END IF;
    END $$
DELIMITER ;

CREATE TABLE `recipe_has_image_upload` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL,
    `type` varchar(64) NOT NULL,
    `recipe` int unsigned NOT NULL,
    `image_upload` int unsigned NOT NULL UNIQUE,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`recipe`) REFERENCES `recipe` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (`image_upload`) REFERENCES `image_upload` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `recipe_has_image_upload`
    ADD UNIQUE INDEX (`type`, `recipe`, `name`);

DELIMITER $$
CREATE PROCEDURE `proc_unprocessed_originals_by_type` (IN `type` varchar(64))
BEGIN
    SELECT *
    FROM `recipe_has_image_upload` `original`
    WHERE `original`.`type` = 'ORIGINAL'
      AND NOT EXISTS(
            SELECT `id`
            FROM `recipe_has_image_upload` `thumbnail`
            WHERE `thumbnail`.`name` = `original`.`name`
              AND `thumbnail`.`recipe` = `original`.`recipe`
              AND `thumbnail`.`type` = `type`
        );
END $$
DELIMITER ;
