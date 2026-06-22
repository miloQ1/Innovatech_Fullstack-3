
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `clientes_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `clientes_db`;
DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clients` (
  `client_id` bigint NOT NULL AUTO_INCREMENT,
  `contact_email` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_name` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `industry` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('ACTIVE','INACTIVE') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
INSERT INTO `clients` VALUES (1,'maria.gonzalez@techcorp.cl','María González','2026-06-21 04:40:43.090315','seed','Tecnología','TechCorp Solutions','ACTIVE'),(2,'carlos.perez@bancoandino.cl','Carlos Pérez','2026-06-21 04:40:43.090315','seed','Banca','Banco Andino','ACTIVE');
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `asignaciones_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `asignaciones_db`;
DROP TABLE IF EXISTS `assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignments` (
  `assignment_id` bigint NOT NULL AUTO_INCREMENT,
  `allocation_pct` int DEFAULT NULL,
  `assignment_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `planned_hours` int DEFAULT NULL,
  `project_id` bigint DEFAULT NULL,
  `project_role` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `resource_id` bigint DEFAULT NULL,
  PRIMARY KEY (`assignment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `assignments` WRITE;
/*!40000 ALTER TABLE `assignments` DISABLE KEYS */;
INSERT INTO `assignments` VALUES (1,80,'ACTIVE','2026-06-21 04:40:57.021131','2026-09-30',320,1,'Frontend Lead','2026-03-01','2026-06-21 04:40:57.021131',1),(2,60,'ACTIVE','2026-06-21 04:40:57.021131','2026-09-30',240,1,'Backend Developer','2026-03-01','2026-06-21 04:40:57.021131',2),(3,50,'ACTIVE','2026-06-21 04:40:57.021131','2027-03-31',200,2,'Project Manager','2026-06-01','2026-06-21 04:40:57.021131',3);
/*!40000 ALTER TABLE `assignments` ENABLE KEYS */;
UNLOCK TABLES;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `proyectos_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `proyectos_db`;
DROP TABLE IF EXISTS `board_columns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `board_columns` (
  `column_id` bigint NOT NULL AUTO_INCREMENT,
  `color` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mapped_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sequence_order` int NOT NULL,
  `phase_id` bigint NOT NULL,
  PRIMARY KEY (`column_id`),
  KEY `FK6hs0ykj0g1ia49p29gfbdgytx` (`phase_id`),
  CONSTRAINT `FK6hs0ykj0g1ia49p29gfbdgytx` FOREIGN KEY (`phase_id`) REFERENCES `phases` (`phase_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `board_columns` WRITE;
/*!40000 ALTER TABLE `board_columns` DISABLE KEYS */;
/*!40000 ALTER TABLE `board_columns` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `phases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phases` (
  `phase_id` bigint NOT NULL AUTO_INCREMENT,
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `planned_end` date DEFAULT NULL,
  `planned_start` date DEFAULT NULL,
  `sequence_order` int NOT NULL,
  `status` enum('CANCELLED','COMPLETED','IN_PROGRESS','PENDING') COLLATE utf8mb4_unicode_ci NOT NULL,
  `project_id` bigint NOT NULL,
  PRIMARY KEY (`phase_id`),
  KEY `FKp0vs31guyj5itugskrstnrm4y` (`project_id`),
  CONSTRAINT `FKp0vs31guyj5itugskrstnrm4y` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `phases` WRITE;
/*!40000 ALTER TABLE `phases` DISABLE KEYS */;
INSERT INTO `phases` VALUES (1,'seed','Diseño UX','2026-04-15','2026-03-01',1,'COMPLETED',1),(2,'seed','Desarrollo Frontend','2026-07-31','2026-04-16',2,'IN_PROGRESS',1);
/*!40000 ALTER TABLE `phases` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `project_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `joined_at` datetime(6) NOT NULL,
  `last_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `project_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKaydweb1re2g5786xaugww4u0` (`project_id`,`user_id`),
  CONSTRAINT `FKdki1sp2homqsdcvqm9yrix31g` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `project_members` WRITE;
/*!40000 ALTER TABLE `project_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_members` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projects` (
  `project_id` bigint NOT NULL AUTO_INCREMENT,
  `budget` decimal(15,2) DEFAULT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `end_date` date DEFAULT NULL,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `progress_pct` decimal(5,2) DEFAULT NULL,
  `project_manager_id` bigint DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','IN_PROGRESS','ON_HOLD','PLANNING') COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `client_id` bigint NOT NULL,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `UKclujw4wu21d33ssgde022aymk` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `projects` WRITE;
/*!40000 ALTER TABLE `projects` DISABLE KEYS */;
INSERT INTO `projects` VALUES (1,45000000.00,'TC-001','2026-06-21 04:40:43.233431','seed','Renovación del portal de autoatención para clientes de TechCorp.','2026-09-30','Portal de Clientes TechCorp',62.00,NULL,'2026-03-01','IN_PROGRESS',NULL,1),(2,120000000.00,'BA-001','2026-06-21 04:40:43.233431','seed','Migración del sistema core bancario a la nueva plataforma.','2027-03-31','Migración Core Bancario',15.00,NULL,'2026-06-01','PLANNING',NULL,2);
/*!40000 ALTER TABLE `projects` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `task_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_status_history` (
  `history_id` bigint NOT NULL AUTO_INCREMENT,
  `changed_at` datetime(6) NOT NULL,
  `changed_by_resource_id` bigint DEFAULT NULL,
  `new_status` enum('CANCELLED','DONE','IN_PROGRESS','IN_REVIEW','TODO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `previous_status` enum('CANCELLED','DONE','IN_PROGRESS','IN_REVIEW','TODO') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `task_id` bigint NOT NULL,
  PRIMARY KEY (`history_id`),
  KEY `FKlbsvojobaaf8bkuddoe7ig25s` (`task_id`),
  CONSTRAINT `FKlbsvojobaaf8bkuddoe7ig25s` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `task_status_history` WRITE;
/*!40000 ALTER TABLE `task_status_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_status_history` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tasks` (
  `task_id` bigint NOT NULL AUTO_INCREMENT,
  `actual_hours` decimal(7,2) DEFAULT NULL,
  `assigned_resource_id` bigint DEFAULT NULL,
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `due_date` date DEFAULT NULL,
  `estimated_hours` decimal(7,2) DEFAULT NULL,
  `priority` enum('CRITICAL','HIGH','LOW','MEDIUM') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `status` enum('CANCELLED','DONE','IN_PROGRESS','IN_REVIEW','TODO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `task_code` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_task_id` bigint DEFAULT NULL,
  `phase_id` bigint DEFAULT NULL,
  `project_id` bigint NOT NULL,
  PRIMARY KEY (`task_id`),
  KEY `FK76tiq4q248au3u79a8nkexoth` (`parent_task_id`),
  KEY `FKk9sa90vks2eryt7tre1gvtl03` (`phase_id`),
  KEY `FKsfhn82y57i3k9uxww1s007acc` (`project_id`),
  CONSTRAINT `FK76tiq4q248au3u79a8nkexoth` FOREIGN KEY (`parent_task_id`) REFERENCES `tasks` (`task_id`),
  CONSTRAINT `FKk9sa90vks2eryt7tre1gvtl03` FOREIGN KEY (`phase_id`) REFERENCES `phases` (`phase_id`),
  CONSTRAINT `FKsfhn82y57i3k9uxww1s007acc` FOREIGN KEY (`project_id`) REFERENCES `projects` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `tasks` WRITE;
/*!40000 ALTER TABLE `tasks` DISABLE KEYS */;
INSERT INTO `tasks` VALUES (1,NULL,NULL,'seed',NULL,'2026-04-10',16.00,'HIGH',NULL,'DONE','TC-001-T1','Definir wireframes',NULL,NULL,1,1),(2,NULL,NULL,'seed',NULL,'2026-07-10',40.00,'HIGH',NULL,'IN_PROGRESS','TC-001-T2','Maquetar dashboard de cliente',NULL,NULL,2,1),(3,NULL,NULL,'seed',NULL,'2026-07-25',24.00,'MEDIUM',NULL,'TODO','TC-001-T3','Integrar login con BFF',NULL,NULL,2,1),(4,NULL,NULL,'seed',NULL,'2026-07-01',60.00,'CRITICAL',NULL,'IN_PROGRESS','BA-001-T1','Levantamiento de requerimientos',NULL,NULL,NULL,2);
/*!40000 ALTER TABLE `tasks` ENABLE KEYS */;
UNLOCK TABLES;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `recursos_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `recursos_db`;
DROP TABLE IF EXISTS `absences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `absences` (
  `absence_id` bigint NOT NULL AUTO_INCREMENT,
  `absence_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `hours_affected` int DEFAULT NULL,
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `resource_id` bigint DEFAULT NULL,
  PRIMARY KEY (`absence_id`),
  KEY `FKpqg5y6y5ng0kuleuuqkg36y4o` (`resource_id`),
  CONSTRAINT `FKpqg5y6y5ng0kuleuuqkg36y4o` FOREIGN KEY (`resource_id`) REFERENCES `professionals` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `absences` WRITE;
/*!40000 ALTER TABLE `absences` DISABLE KEYS */;
/*!40000 ALTER TABLE `absences` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `availability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `availability` (
  `availability_id` bigint NOT NULL AUTO_INCREMENT,
  `availability_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `available_hours` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `date_from` date DEFAULT NULL,
  `date_to` date DEFAULT NULL,
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `resource_id` bigint DEFAULT NULL,
  PRIMARY KEY (`availability_id`),
  KEY `FK4y03wmk6xmmq0p8ofdor5qpr9` (`resource_id`),
  CONSTRAINT `FK4y03wmk6xmmq0p8ofdor5qpr9` FOREIGN KEY (`resource_id`) REFERENCES `professionals` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `availability` WRITE;
/*!40000 ALTER TABLE `availability` DISABLE KEYS */;
/*!40000 ALTER TABLE `availability` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `professionals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professionals` (
  `resource_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `employee_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `first_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `seniority` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `time_zone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `weekly_capacity_hours` int DEFAULT NULL,
  PRIMARY KEY (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `professionals` WRITE;
/*!40000 ALTER TABLE `professionals` DISABLE KEYS */;
INSERT INTO `professionals` VALUES (1,'2026-06-21 04:40:56.904409','ana.silva@innovatech.cl','EMP-001','Ana','Silva','Santiago','Frontend Developer','SENIOR','ACTIVE','America/Santiago','2026-06-21 04:40:56.904409',40),(2,'2026-06-21 04:40:56.904409','jorge.munoz@innovatech.cl','EMP-002','Jorge','Muñoz','Santiago','Backend Developer','MID','ACTIVE','America/Santiago','2026-06-21 04:40:56.904409',40),(3,'2026-06-21 04:40:56.904409','camila.rojas@innovatech.cl','EMP-003','Camila','Rojas','Valparaíso','Project Manager','LEAD','ACTIVE','America/Santiago','2026-06-21 04:40:56.904409',45);
/*!40000 ALTER TABLE `professionals` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `resource_skills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resource_skills` (
  `resource_skill_id` bigint NOT NULL AUTO_INCREMENT,
  `certified` bit(1) DEFAULT NULL,
  `last_updated` datetime(6) DEFAULT NULL,
  `proficiency_level` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `years_experience` int DEFAULT NULL,
  `resource_id` bigint DEFAULT NULL,
  `skill_id` bigint DEFAULT NULL,
  PRIMARY KEY (`resource_skill_id`),
  KEY `FKehs69shssqjquu4sfoqf047e7` (`resource_id`),
  KEY `FKbxuqrfw7qu2tm41sbnqksy8y1` (`skill_id`),
  CONSTRAINT `FKbxuqrfw7qu2tm41sbnqksy8y1` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`skill_id`),
  CONSTRAINT `FKehs69shssqjquu4sfoqf047e7` FOREIGN KEY (`resource_id`) REFERENCES `professionals` (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `resource_skills` WRITE;
/*!40000 ALTER TABLE `resource_skills` DISABLE KEYS */;
INSERT INTO `resource_skills` VALUES (1,_binary '','2026-06-21 04:40:56.921866','ADVANCED',5,1,1),(2,_binary '','2026-06-21 04:40:56.921866','ADVANCED',4,2,2),(3,_binary '\0','2026-06-21 04:40:56.921866','INTERMEDIATE',3,2,4),(4,_binary '','2026-06-21 04:40:56.921866','ADVANCED',7,3,3);
/*!40000 ALTER TABLE `resource_skills` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `skills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skills` (
  `skill_id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`skill_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `skills` WRITE;
/*!40000 ALTER TABLE `skills` DISABLE KEYS */;
INSERT INTO `skills` VALUES (1,'Frontend','2026-06-21 04:40:56.913125','Desarrollo de interfaces con React','React','ACTIVE','2026-06-21 04:40:56.913125'),(2,'Backend','2026-06-21 04:40:56.913125','Desarrollo de microservicios con Spring Boot','Spring Boot','ACTIVE','2026-06-21 04:40:56.913125'),(3,'Management','2026-06-21 04:40:56.913125','Planificación y seguimiento de proyectos','Gestión de proyectos','ACTIVE','2026-06-21 04:40:56.913125'),(4,'Backend','2026-06-21 04:40:56.913125','Modelado y administración de bases de datos','MySQL','ACTIVE','2026-06-21 04:40:56.913125');
/*!40000 ALTER TABLE `skills` ENABLE KEYS */;
UNLOCK TABLES;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `colaboracion_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `colaboracion_db`;
DROP TABLE IF EXISTS `activity_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_logs` (
  `activity_id` bigint NOT NULL AUTO_INCREMENT,
  `action_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `actor_resource_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `project_id` bigint NOT NULL,
  `task_id` bigint DEFAULT NULL,
  PRIMARY KEY (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `activity_logs` WRITE;
/*!40000 ALTER TABLE `activity_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `activity_logs` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `attachments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attachments` (
  `attachment_id` bigint NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mime_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `size_bytes` bigint NOT NULL,
  `uploaded_at` datetime(6) NOT NULL,
  `comment_id` bigint NOT NULL,
  PRIMARY KEY (`attachment_id`),
  KEY `FKbydtpaj4hcd1ahpdu9091hlpp` (`comment_id`),
  CONSTRAINT `FKbydtpaj4hcd1ahpdu9091hlpp` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `attachments` WRITE;
/*!40000 ALTER TABLE `attachments` DISABLE KEYS */;
/*!40000 ALTER TABLE `attachments` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `author_resource_id` bigint NOT NULL,
  `content` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `is_edited` bit(1) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `thread_id` bigint NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FKowxb1mrw8j0kq7y01c68jc097` (`thread_id`),
  CONSTRAINT `FKowxb1mrw8j0kq7y01c68jc097` FOREIGN KEY (`thread_id`) REFERENCES `threads` (`thread_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (1,1,'¿Usamos el mismo esquema de colores del login para el dashboard?','2026-06-21 04:41:02.899441',NULL,_binary '\0','2026-06-21 04:41:02.899441',1),(2,3,'Sí, mantengamos la misma paleta para consistencia visual.','2026-06-21 04:41:02.899441',NULL,_binary '\0','2026-06-21 04:41:02.899441',1);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `mentions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mentions` (
  `mention_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `mention_status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mentioned_resource_id` bigint NOT NULL,
  `comment_id` bigint NOT NULL,
  PRIMARY KEY (`mention_id`),
  KEY `FK8kbxvaojdu8o8r3exxg00cufw` (`comment_id`),
  CONSTRAINT `FK8kbxvaojdu8o8r3exxg00cufw` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `mentions` WRITE;
/*!40000 ALTER TABLE `mentions` DISABLE KEYS */;
/*!40000 ALTER TABLE `mentions` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `threads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `threads` (
  `thread_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by_resource_id` bigint NOT NULL,
  `project_id` bigint NOT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `task_id` bigint DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`thread_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `threads` WRITE;
/*!40000 ALTER TABLE `threads` DISABLE KEYS */;
INSERT INTO `threads` VALUES (1,'2026-06-21 04:41:02.890640',1,1,'OPEN',2,'Dudas sobre diseño del dashboard','2026-06-21 04:41:02.890640');
/*!40000 ALTER TABLE `threads` ENABLE KEYS */;
UNLOCK TABLES;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `analitica_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `analitica_db`;
DROP TABLE IF EXISTS `alert_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alert_rules` (
  `rule_id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) NOT NULL,
  `notification_channel` enum('EMAIL','IN_APP','WEBHOOK') COLLATE utf8mb4_unicode_ci NOT NULL,
  `operator` enum('EQUALS','GREATER_THAN','LESS_THAN') COLLATE utf8mb4_unicode_ci NOT NULL,
  `severity` enum('CRITICAL','HIGH','LOW','MEDIUM') COLLATE utf8mb4_unicode_ci NOT NULL,
  `threshold_value` decimal(15,2) NOT NULL,
  `kpi_id` bigint NOT NULL,
  PRIMARY KEY (`rule_id`),
  KEY `FKsf9kfulox1cufhubdf3c1dpe` (`kpi_id`),
  CONSTRAINT `FKsf9kfulox1cufhubdf3c1dpe` FOREIGN KEY (`kpi_id`) REFERENCES `kpi_definitions` (`kpi_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `alert_rules` WRITE;
/*!40000 ALTER TABLE `alert_rules` DISABLE KEYS */;
/*!40000 ALTER TABLE `alert_rules` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `dashboard_layouts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dashboard_layouts` (
  `layout_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `is_default` bit(1) NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `owner_role` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`layout_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `dashboard_layouts` WRITE;
/*!40000 ALTER TABLE `dashboard_layouts` DISABLE KEYS */;
/*!40000 ALTER TABLE `dashboard_layouts` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `kpi_definitions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kpi_definitions` (
  `kpi_id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `formula_type` enum('DELAY_RISK','PROGRESS','UTILIZATION') COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refresh_frequency` enum('DAILY','MONTHLY','WEEKLY') COLLATE utf8mb4_unicode_ci NOT NULL,
  `unit` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`kpi_id`),
  UNIQUE KEY `UK5jyusnjad6aipu29x2syelhhy` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `kpi_definitions` WRITE;
/*!40000 ALTER TABLE `kpi_definitions` DISABLE KEYS */;
INSERT INTO `kpi_definitions` VALUES (1,'Proyect_Progress','PROGRESS',_binary '','Avance','DAILY','%'),(2,'RESOURCE_UTILIZATION','UTILIZATION',_binary '','Utilización de recursos','WEEKLY','%'),(3,'DELAY_RISK_SCORE','DELAY_RISK',_binary '','Riesgo de atraso','MONTHLY','pts');
/*!40000 ALTER TABLE `kpi_definitions` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `kpi_snapshots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kpi_snapshots` (
  `snapshot_id` bigint NOT NULL AUTO_INCREMENT,
  `generated_at` datetime(6) NOT NULL,
  `numeric_value` decimal(15,2) DEFAULT NULL,
  `period_end` date NOT NULL,
  `period_start` date NOT NULL,
  `scope_id` bigint DEFAULT NULL,
  `scope_type` enum('GLOBAL','PROJECT','RESOURCE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `source_trace_json` text COLLATE utf8mb4_unicode_ci,
  `text_value` text COLLATE utf8mb4_unicode_ci,
  `kpi_id` bigint NOT NULL,
  PRIMARY KEY (`snapshot_id`),
  KEY `FKphph3xmb6yxkot2epd3kn5hag` (`kpi_id`),
  CONSTRAINT `FKphph3xmb6yxkot2epd3kn5hag` FOREIGN KEY (`kpi_id`) REFERENCES `kpi_definitions` (`kpi_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `kpi_snapshots` WRITE;
/*!40000 ALTER TABLE `kpi_snapshots` DISABLE KEYS */;
INSERT INTO `kpi_snapshots` VALUES (1,'2026-06-21 00:37:57.577016',0.00,'2026-06-21','2026-06-21',NULL,'GLOBAL',NULL,NULL,1),(2,'2026-06-21 04:41:23.594150',45.00,'2026-05-03','2026-04-27',1,'PROJECT',NULL,NULL,1),(3,'2026-06-21 04:41:23.594150',52.00,'2026-05-10','2026-05-04',1,'PROJECT',NULL,NULL,1),(4,'2026-06-21 04:41:23.594150',58.00,'2026-05-17','2026-05-11',1,'PROJECT',NULL,NULL,1),(5,'2026-06-21 04:41:23.594150',63.00,'2026-05-24','2026-05-18',1,'PROJECT',NULL,NULL,1),(6,'2026-06-21 04:41:23.594150',70.00,'2026-05-31','2026-05-25',1,'PROJECT',NULL,NULL,1),(7,'2026-06-21 04:41:23.594150',75.00,'2026-06-07','2026-06-01',1,'PROJECT',NULL,NULL,1),(8,'2026-06-21 04:41:23.612462',60.00,'2026-05-03','2026-04-27',NULL,'GLOBAL',NULL,NULL,2),(9,'2026-06-21 04:41:23.612462',65.00,'2026-05-10','2026-05-04',NULL,'GLOBAL',NULL,NULL,2),(10,'2026-06-21 04:41:23.612462',72.00,'2026-05-17','2026-05-11',NULL,'GLOBAL',NULL,NULL,2),(11,'2026-06-21 04:41:23.612462',68.00,'2026-05-24','2026-05-18',NULL,'GLOBAL',NULL,NULL,2),(12,'2026-06-21 04:41:23.612462',76.00,'2026-05-31','2026-05-25',NULL,'GLOBAL',NULL,NULL,2),(15,'2026-06-21 04:41:23.718615',20.00,'2026-03-31','2026-03-01',NULL,'GLOBAL',NULL,NULL,3),(16,'2026-06-21 04:41:23.718615',18.00,'2026-04-30','2026-04-01',NULL,'GLOBAL',NULL,NULL,3),(17,'2026-06-21 04:41:23.718615',12.00,'2026-05-31','2026-05-01',NULL,'GLOBAL',NULL,NULL,3),(18,'2026-06-21 04:41:23.718615',8.00,'2026-06-21','2026-06-01',NULL,'GLOBAL',NULL,NULL,3);
/*!40000 ALTER TABLE `kpi_snapshots` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `layout_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `layout_items` (
  `layout_item_id` bigint NOT NULL AUTO_INCREMENT,
  `display_order` int NOT NULL,
  `height` int NOT NULL,
  `position_x` int NOT NULL,
  `position_y` int NOT NULL,
  `width` int NOT NULL,
  `layout_id` bigint NOT NULL,
  `widget_id` bigint NOT NULL,
  PRIMARY KEY (`layout_item_id`),
  KEY `FKr29f5cupod6y97v0sl32hgyd9` (`layout_id`),
  KEY `FK9ls2idrjcpn5ermjt12cyrb1c` (`widget_id`),
  CONSTRAINT `FK9ls2idrjcpn5ermjt12cyrb1c` FOREIGN KEY (`widget_id`) REFERENCES `widgets` (`widget_id`),
  CONSTRAINT `FKr29f5cupod6y97v0sl32hgyd9` FOREIGN KEY (`layout_id`) REFERENCES `dashboard_layouts` (`layout_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `layout_items` WRITE;
/*!40000 ALTER TABLE `layout_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `layout_items` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `widgets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `widgets` (
  `widget_id` bigint NOT NULL AUTO_INCREMENT,
  `configuration_json` text COLLATE utf8mb4_unicode_ci,
  `is_active` bit(1) NOT NULL,
  `source_kpi_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `widget_type` enum('BAR_CHART','CARD','LINE_CHART','PIE_CHART','TABLE') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`widget_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `widgets` WRITE;
/*!40000 ALTER TABLE `widgets` DISABLE KEYS */;
INSERT INTO `widgets` VALUES (1,NULL,_binary '','Proyect_Progress','Avance','LINE_CHART'),(2,NULL,_binary '','Proyect_Progress','Avance actual','CARD'),(3,NULL,_binary '','RESOURCE_UTILIZATION','Utilización semanal','BAR_CHART'),(4,NULL,_binary '','RESOURCE_UTILIZATION','Historial de utilización','TABLE'),(5,NULL,_binary '','DELAY_RISK_SCORE','Distribución de riesgo','PIE_CHART');
/*!40000 ALTER TABLE `widgets` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

