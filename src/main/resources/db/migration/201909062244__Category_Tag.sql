CREATE TABLE `category` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `recipe_belong_to_category` (
    `recipe` int unsigned NOT NULL,
    `category` int unsigned NOT NULL,
    PRIMARY KEY (`recipe`, `category`),
    FOREIGN KEY (`recipe`) REFERENCES `recipe` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (`category`) REFERENCES `unit_usage_area` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tag` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL,
    `recipe` int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`recipe`) REFERENCES `recipe` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
