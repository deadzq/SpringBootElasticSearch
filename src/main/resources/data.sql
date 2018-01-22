CREATE TABLE IF NOT EXISTS `runoob_tbl`(
  `runoob_id` INT UNSIGNED AUTO_INCREMENT,
  `runoob_title` VARCHAR(100) NOT NULL,
  `runoob_author` VARCHAR(40) NOT NULL ,
  `submission_date` DATE,
  PRIMARY KEY ( `runoob_id` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `test`(
  `id` INT UNSIGNED AUTO_INCREMENT ,
  `title` VARCHAR(128) NOT NULL ,
  `author` VARCHAR(40) NOT NULL ,
  `data` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
)ENGINE =InnoDB DEFAULT CHARSET = utf8;