CREATE TABLE `role` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `role` (`name`)
    VALUES ('ADMIN'), ('USER');

CREATE TABLE `permission` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_has_role` (
    `user` int unsigned NOT NULL,
    `role` int unsigned NOT NULL,
    `assigned_at` int unsigned NOT NULL DEFAULT 0,
    PRIMARY KEY (`user`, `role`),
    FOREIGN KEY (`user`) REFERENCES `user` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (`role`) REFERENCES `role` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELIMITER $$
CREATE TRIGGER `trig_user_has_role_insert` BEFORE INSERT ON `user_has_role`
FOR EACH ROW
    BEGIN
        IF (new.`assigned_at` = 0) THEN
            SET new.`assigned_at` = UNIX_TIMESTAMP();
        END IF;
    END $$
DELIMITER ;

CREATE TABLE `role_grants_permission` (
    `role` int unsigned NOT NULL,
    `permission` int unsigned NOT NULL,
    `assigned_at` int unsigned NOT NULL DEFAULT 0,
    PRIMARY KEY (`role`, `permission`),
    FOREIGN KEY (`role`) REFERENCES `role` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (`permission`) REFERENCES `permission` (`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELIMITER $$
CREATE TRIGGER `trig_role_grants_permission_insert` BEFORE INSERT ON `role_grants_permission`
FOR EACH ROW
    BEGIN
        IF (new.`assigned_at` = 0) THEN
            SET new.`assigned_at` = UNIX_TIMESTAMP();
        END IF;
    END $$
DELIMITER ;
