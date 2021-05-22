ALTER TABLE `collection`
    ADD COLUMN `created_at_ts` timestamp NULL;

UPDATE `collection`
    SET `created_at_ts` = FROM_UNIXTIME(`created_at`);

ALTER TABLE `collection`
    DROP COLUMN `created_at`,
    CHANGE COLUMN `created_at_ts` `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

DROP TRIGGER trig_collection_insert;

ALTER TABLE `collection_has_recipe`
    ADD COLUMN `added_at_ts` timestamp NULL;

UPDATE `collection_has_recipe`
    SET `added_at_ts` = FROM_UNIXTIME(`added_at`);

ALTER TABLE `collection_has_recipe`
    DROP COLUMN `added_at`,
    CHANGE COLUMN `added_at_ts` `added_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

DROP TRIGGER `trig_collection_has_recipe_insert`;

ALTER TABLE `image_upload`
    ADD COLUMN `created_at_ts` timestamp NULL;

UPDATE `image_upload`
    SET `created_at_ts` = FROM_UNIXTIME(`created_at`);

ALTER TABLE `image_upload`
    DROP COLUMN `created_at`,
    CHANGE COLUMN `created_at_ts` `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

ALTER TABLE `image_upload`
    ADD COLUMN `modified_at_ts` timestamp NULL;

UPDATE `image_upload`
    SET `modified_at_ts` = FROM_UNIXTIME(`modified_at`);

ALTER TABLE `image_upload`
    DROP COLUMN `modified_at`,
    CHANGE COLUMN `modified_at_ts` `modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

DROP TRIGGER `trig_image_upload_insert`;

DELIMITER $$
CREATE OR REPLACE TRIGGER `trig_image_upload_update` BEFORE UPDATE ON `image_upload`
FOR EACH ROW
    BEGIN
        SET new.`modified_at` = CURRENT_TIMESTAMP();
    END $$
DELIMITER ;

ALTER TABLE `rating`
    ADD COLUMN `created_at_ts` timestamp NULL;

UPDATE `rating`
    SET `created_at_ts` = FROM_UNIXTIME(`created_at`);

ALTER TABLE `rating`
    DROP COLUMN `created_at`,
    CHANGE COLUMN `created_at_ts` `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

ALTER TABLE `rating`
    ADD COLUMN `modified_at_ts` timestamp NULL;

UPDATE `rating`
    SET `modified_at_ts` = FROM_UNIXTIME(`modified_at`);

ALTER TABLE `rating`
    DROP COLUMN `modified_at`,
    CHANGE COLUMN `modified_at_ts` `modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

DROP TRIGGER `trig_rating_insert`;

DELIMITER $$
CREATE OR REPLACE TRIGGER `trig_rating_update` BEFORE UPDATE ON `rating`
FOR EACH ROW
    BEGIN
        SET new.`modified_at` = CURRENT_TIMESTAMP();
    END $$
DELIMITER ;

ALTER TABLE `recipe`
    ADD COLUMN `created_at_ts` timestamp NULL;

UPDATE `recipe`
    SET `created_at_ts` = FROM_UNIXTIME(`created_at`);

ALTER TABLE `recipe`
    DROP COLUMN `created_at`,
    CHANGE COLUMN `created_at_ts` `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

DROP TRIGGER `trig_recipe_insert`;

ALTER TABLE `refresh_token`
    ADD COLUMN `created_at_ts` timestamp NULL;

UPDATE `refresh_token`
    SET `created_at_ts` = FROM_UNIXTIME(`created_at`);

ALTER TABLE `refresh_token`
    DROP COLUMN `created_at`,
    CHANGE COLUMN `created_at_ts` `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

ALTER TABLE `refresh_token`
    ADD COLUMN `modified_at_ts` timestamp NULL;

UPDATE `refresh_token`
    SET `modified_at_ts` = FROM_UNIXTIME(`modified_at`);

ALTER TABLE `refresh_token`
    DROP COLUMN `modified_at`,
    CHANGE COLUMN `modified_at_ts` `modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

ALTER TABLE `refresh_token`
    ADD COLUMN `revoked_at_ts` timestamp NULL;

UPDATE `refresh_token`
    SET `revoked_at_ts` = FROM_UNIXTIME(`revoked_at`)
    WHERE `revoked_at` IS NOT NULL;

ALTER TABLE `refresh_token`
    DROP COLUMN `revoked_at`,
    CHANGE COLUMN `revoked_at_ts` `revoked_at` timestamp NULL;

ALTER TABLE `refresh_token`
    ADD COLUMN `last_used_at_ts` timestamp NULL;

UPDATE `refresh_token`
    SET `last_used_at_ts` = FROM_UNIXTIME(`last_used_at`)
    WHERE `last_used_at` IS NOT NULL;

ALTER TABLE `refresh_token`
    DROP COLUMN `last_used_at`,
    CHANGE COLUMN `last_used_at_ts` `last_used_at` timestamp NULL;

DROP TRIGGER `trig_refresh_token_insert`;

DELIMITER $$
CREATE OR REPLACE TRIGGER `trig_refresh_token_update` BEFORE UPDATE ON `refresh_token`
FOR EACH ROW
    BEGIN
        SET new.`modified_at` = CURRENT_TIMESTAMP();
    END $$
DELIMITER ;

ALTER TABLE `user`
    ADD COLUMN `created_at_ts` timestamp NULL;

UPDATE `user`
    SET `created_at_ts` = FROM_UNIXTIME(`created_at`);

ALTER TABLE `user`
    DROP COLUMN `created_at`,
    CHANGE COLUMN `created_at_ts` `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

ALTER TABLE `user`
    ADD COLUMN `modified_at_ts` timestamp NULL;

UPDATE `user`
    SET `modified_at_ts` = FROM_UNIXTIME(`modified_at`);

ALTER TABLE `user`
    DROP COLUMN `modified_at`,
    CHANGE COLUMN `modified_at_ts` `modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

ALTER TABLE `user`
    ADD COLUMN `deleted_at_ts` timestamp NULL;

UPDATE `user`
    SET `deleted_at_ts` = FROM_UNIXTIME(`deleted_at`)
    WHERE `deleted_at` IS NOT NULL;

ALTER TABLE `user`
    DROP COLUMN `deleted_at`,
    CHANGE COLUMN `deleted_at_ts` `deleted_at` timestamp NULL;

ALTER TABLE `user`
    ADD COLUMN `suspended_at_ts` timestamp NULL;

UPDATE `user`
    SET `suspended_at_ts` = FROM_UNIXTIME(`suspended_at`)
    WHERE `suspended_at` IS NOT NULL;

ALTER TABLE `user`
    DROP COLUMN `suspended_at`,
    CHANGE COLUMN `suspended_at_ts` `suspended_at` timestamp NULL;

DROP TRIGGER `trig_user_insert`;

DELIMITER $$
CREATE OR REPLACE TRIGGER `trig_user_update` BEFORE UPDATE ON `user`
FOR EACH ROW
    BEGIN
        SET new.`modified_at` = CURRENT_TIMESTAMP();
    END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE `proc_user_delete` (IN `user_id` int unsigned)
BEGIN
    UPDATE `user`
    SET `deleted_at` = CURRENT_TIMESTAMP()
    WHERE `id` = `user_id`;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE `proc_user_suspend` (IN `user_id` int unsigned)
BEGIN
    UPDATE `user`
    SET `suspended_at` = CURRENT_TIMESTAMP()
    WHERE `id` = `user_id`;
END $$
DELIMITER ;

ALTER TABLE `user_has_role`
    ADD COLUMN `assigned_at_ts` timestamp NULL;

UPDATE `user_has_role`
    SET `assigned_at_ts` = FROM_UNIXTIME(`assigned_at`);

ALTER TABLE `user_has_role`
    DROP COLUMN `assigned_at`,
    CHANGE COLUMN `assigned_at_ts` `assigned_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

DROP TRIGGER `trig_user_has_role_insert`;
