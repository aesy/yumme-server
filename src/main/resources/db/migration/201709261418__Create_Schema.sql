CREATE TABLE `recipe` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `title` varchar(64) NOT NULL,
    `description` text NOT NULL,
    `directions` text NOT NULL,
    `prep_time` int unsigned NOT NULL,
    `cook_time` int unsigned NOT NULL,
    `yield` int unsigned NOT NULL,
    `public` tinyint(1) NOT NULL DEFAULT 0,
    `created_at` int unsigned NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `recipe`
    ADD INDEX (`public`),
    ADD INDEX (`created_at`);

DELIMITER $$
CREATE TRIGGER `trig_recipe_insert` BEFORE INSERT ON `recipe`
FOR EACH ROW
    BEGIN
        IF (new.`created_at` = 0) THEN
            SET new.`created_at` = UNIX_TIMESTAMP();
        END IF;
    END $$
DELIMITER ;

CREATE TABLE `rating` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `score` int unsigned NOT NULL DEFAULT 0,
    `recipe` int unsigned NOT NULL,
    `created_at` int unsigned NOT NULL DEFAULT 0,
    `modified_at` int unsigned NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`recipe`) REFERENCES `recipe` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `rating`
    ADD INDEX (`score`);

DELIMITER $$
CREATE TRIGGER `trig_rating_insert` BEFORE INSERT ON `rating`
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
CREATE TRIGGER `trig_rating_update` BEFORE UPDATE ON `rating`
FOR EACH ROW
    BEGIN
        IF (new.`modified_at` = 0) THEN
            SET new.`modified_at` = UNIX_TIMESTAMP();
        END IF;
    END $$
DELIMITER ;

CREATE TABLE `unit` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `unit` (`name`)
    VALUES ('FLUID OUNCE'), ('PINT'), ('QUART'), ('GALLON'),
           ('POUND'), ('OUNCE'), ('INCH'), ('FOOT'),
           ('GILL'), ('TEASPOON'), ('TABLESPOON'), ('CUP'),
           ('MILLILITRE'), ('CENTILITRE'), ('DECILITRE'), ('LITRE'),
           ('MILLIGRAM'), ('GRAM'), ('HECTOGRAM'), ('KILOGRAM'),
           ('MILLIMETER'), ('CENTIMETER'), ('DECIMETER'), ('METER');

CREATE TABLE `ingredient` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `recipe_has_ingredient` (
    `recipe` int unsigned NOT NULL,
    `ingredient` int unsigned NOT NULL,
    `amount` int unsigned NOT NULL,
    `unit` int unsigned NOT NULL,
    PRIMARY KEY (`recipe`, `ingredient`),
    FOREIGN KEY (`recipe`) REFERENCES `recipe` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (`ingredient`) REFERENCES `ingredient` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (`unit`) REFERENCES `unit` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
