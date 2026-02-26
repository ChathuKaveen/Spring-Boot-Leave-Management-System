CREATE TABLE `leave_date_type` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`));

  CREATE TABLE `leave_type` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `type` VARCHAR(45) NOT NULL,
    `description` VARCHAR(100) NOT NULL,
    `paid` INT NOT NULL,
    `halfday_allowed` INT NOT NULL,
    PRIMARY KEY (`id`));


    ALTER TABLE `leaves`
    ADD INDEX `from_date_foreign_idx` (`from_date_type` ASC) VISIBLE,
    ADD INDEX `to_date_foreign_idx` (`to_date_type` ASC) VISIBLE;

    ALTER TABLE `leaves`
    ADD CONSTRAINT `from_date_foreign`
      FOREIGN KEY (`from_date_type`)
      REFERENCES `leave_date_type` (`id`)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION,
    ADD CONSTRAINT `to_date_foreign`
      FOREIGN KEY (`to_date_type`)
      REFERENCES `leave_date_type` (`id`)
      ON DELETE NO ACTION
      ON UPDATE NO ACTION;

ALTER TABLE `leaves`
ADD INDEX `leave_type_id_idx` (`leave_type_id` ASC) VISIBLE;

ALTER TABLE `leaves`
ADD CONSTRAINT `leave_type_id`
  FOREIGN KEY (`leave_type_id`)
  REFERENCES `leave_type` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
