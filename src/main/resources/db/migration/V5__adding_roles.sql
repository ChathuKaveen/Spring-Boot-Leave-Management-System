ALTER TABLE `leavesystem`.`user`
ADD COLUMN `role` VARCHAR(45) NULL DEFAULT 'USER' AFTER `password`;