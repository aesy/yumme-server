ALTER TABLE `rating`
    ADD COLUMN `user` int unsigned NOT NULL,
    ADD FOREIGN KEY (`user`) REFERENCES `user`(`id`)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    ADD UNIQUE INDEX (`recipe`, `user`);
