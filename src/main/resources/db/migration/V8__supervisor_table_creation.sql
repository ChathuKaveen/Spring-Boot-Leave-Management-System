CREATE TABLE `leavesystem`.`supervisors` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `member` BIGINT NOT NULL,
  `primary_supervisor` BIGINT NULL,
  `secondary_supervisor` BIGINT NULL,
  PRIMARY KEY (`id`),
  INDEX `member Foreign_idx` (`member` ASC) VISIBLE,
  CONSTRAINT `member Foreign`
    FOREIGN KEY (`member`)
    REFERENCES `leavesystem`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
