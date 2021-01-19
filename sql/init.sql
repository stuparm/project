CREATE DATABASE `project`;
USE `project`;


-- gate table
DROP TABLE IF EXISTS `gate`;
CREATE TABLE `gate` (
  `gate_id` int NOT NULL AUTO_INCREMENT,
  `gate_number` varchar(45) NOT NULL,
  PRIMARY KEY (`gate_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


LOCK TABLES `gate` WRITE;
INSERT INTO `gate` VALUES (1,'A1'),(2,'A2'),(3,'A3'),(4,'A4'),(5,'B1'),(6,'B2'),(7,'B3'),(8,'B4');
UNLOCK TABLES;




-- schedule table
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule` (
  `schedule_id` int NOT NULL AUTO_INCREMENT,
  `utc_from` timestamp(3) NOT NULL,
  `utc_to` timestamp(3) NOT NULL,
  `status` varchar(45) NOT NULL,
  `utc_change` timestamp(3) NOT NULL,
  `changed_by` varchar(45) NOT NULL,
  `gate_id` int NOT NULL,
  PRIMARY KEY (`schedule_id`),
  KEY `schedule_gate_fk_idx` (`gate_id`),
  CONSTRAINT `schedule_gate_fk` FOREIGN KEY (`gate_id`) REFERENCES `gate` (`gate_id`)
) ENGINE=InnoDB AUTO_INCREMENT=446 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


LOCK TABLES `schedule` WRITE;
INSERT INTO `schedule` VALUES (1,'1970-01-17 00:00:01.000','2038-01-19 03:14:00.000','free','2021-01-17 13:00:01.000','system',1),(2,'1970-01-17 00:00:01.100','2038-01-19 03:14:01.000','free','2021-01-17 13:00:02.000','system',2),(3,'1970-01-17 00:00:01.200','2038-01-19 03:14:02.000','free','2021-01-17 13:00:03.000','system',3),(4,'1970-01-17 00:00:01.300','2038-01-19 03:14:03.000','free','2021-01-17 13:00:04.000','system',4),(5,'1970-01-17 00:00:01.400','2038-01-19 03:14:04.000','free','2021-01-17 13:00:05.000','system',5),(6,'1970-01-17 00:00:01.500','2038-01-19 03:14:05.000','free','2021-01-17 13:00:06.000','system',6),(7,'1970-01-17 00:00:01.600','2038-01-19 03:14:06.000','free','2021-01-17 13:00:07.000','system',7),(8,'1970-01-17 00:00:01.700','2038-01-19 03:14:07.000','free','2021-01-17 13:00:08.000','system',8);   
UNLOCK TABLES;

