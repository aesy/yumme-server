DROP TRIGGER `trig_role_grants_permission_insert`;
DROP TABLE `role_grants_permission`;
DROP TABLE `permission`;

ALTER TABLE `role`
    DROP CONSTRAINT `role`.`PRIMARY`,
    ADD CONSTRAINT PRIMARY KEY (`id`, `name`);
