ALTER TABLE `leavesystem`.`user`
ADD COLUMN `password` VARCHAR(45) NOT NULL AFTER `email`;

ALTER TABLE `leavesystem`.`leave_type`
CHANGE COLUMN `paid` `paid` TINYINT(1) NOT NULL ,
CHANGE COLUMN `halfday_allowed` `halfday_allowed` TINYINT(1) NOT NULL ;