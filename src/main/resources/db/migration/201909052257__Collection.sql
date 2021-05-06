CREATE TABLE `collection` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL,
    `owner` int unsigned NOT NULL,
    `public` tinyint(1) NOT NULL DEFAULT 0,
    `created_at` int unsigned NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`owner`) REFERENCES `user` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `collection`
    ADD INDEX (`public`),
    ADD INDEX (`created_at`);

DELIMITER $$
CREATE TRIGGER `trig_collection_insert` BEFORE INSERT ON `collection`
FOR EACH ROW
    BEGIN
        IF (new.`created_at` = 0) THEN
            SET new.`created_at` = UNIX_TIMESTAMP();
        END IF;
    END $$
DELIMITER ;

CREATE TABLE `collection_has_recipe` (
    `recipe` int unsigned NOT NULL,
    `collection` int unsigned NOT NULL,
    `added_at` int unsigned NOT NULL DEFAULT 0,
    PRIMARY KEY (`recipe`, `collection`),
    FOREIGN KEY (`recipe`) REFERENCES `recipe` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (`collection`) REFERENCES `collection` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELIMITER $$
CREATE TRIGGER `trig_collection_has_recipe_insert` BEFORE INSERT ON `collection_has_recipe`
FOR EACH ROW
    BEGIN
        IF (new.`added_at` = 0) THEN
            SET new.`added_at` = UNIX_TIMESTAMP();
        END IF;
    END $$
DELIMITER ;
