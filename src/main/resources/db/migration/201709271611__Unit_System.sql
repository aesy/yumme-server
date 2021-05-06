CREATE TABLE `unit_usage_area` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `unit_has_unit_usage_area` (
    `unit` int unsigned NOT NULL,
    `unit_usage_area` int unsigned NOT NULL,
    PRIMARY KEY (`unit`, `unit_usage_area`),
    FOREIGN KEY (`unit`) REFERENCES `unit`  (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (`unit_usage_area`) REFERENCES `unit_usage_area` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `unit_usage_area` (`name`)
    VALUES ('WEIGHT'), ('VOLUME'), ('LENGTH'), ('AREA'), ('TEMPERATURE');

CREATE TABLE `unit_system` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `unit_system` (`name`)
    VALUES ('BRITISH IMPERIAL'), ('UNITED STATES CUSTOMARY'), ('INTERNATIONAL SYSTEM');

CREATE TABLE `unit_part_of_unit_system` (
    `unit` int unsigned NOT NULL,
    `unit_system` int unsigned NOT NULL,
    PRIMARY KEY (`unit`, `unit_system`),
    FOREIGN KEY (`unit`) REFERENCES `unit` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (`unit_system`) REFERENCES `unit_system` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
