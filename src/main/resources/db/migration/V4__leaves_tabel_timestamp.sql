ALTER TABLE `leavesystem`.`leaves`
CHANGE COLUMN `updated_on` `updated_on` TIMESTAMP NOT NULL ;

ALTER TABLE `leavesystem`.`leaves`
CHANGE COLUMN `updated_on` `updated_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE `leavesystem`.`user`
CHANGE COLUMN `password` `password` VARCHAR(100) NOT NULL ;