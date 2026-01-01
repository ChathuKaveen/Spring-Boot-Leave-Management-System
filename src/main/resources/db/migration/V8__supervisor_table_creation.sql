CREATE TABLE `leavesystem`.`user_supervisor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `supervisor_id` BIGINT NULL,
  `type` VARCHAR(100) NULL,
  PRIMARY KEY (`id`),
  INDEX `user foreignkey_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `user foreignkey`
    FOREIGN KEY (`user_id`)
    REFERENCES `leavesystem`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);