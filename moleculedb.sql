-- MySQL dump 10.13  Distrib 8.0.17, for osx10.14 (x86_64)
--
-- Host: localhost    Database: moleculedb
-- ------------------------------------------------------
-- Server version	8.0.17

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

--
-- Table structure for table `atoms`
--

DROP TABLE IF EXISTS `atoms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `atoms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mid` int(11) NOT NULL,
  `atom` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `vertex` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `atoms`
--

LOCK TABLES `atoms` WRITE;
/*!40000 ALTER TABLE `atoms` DISABLE KEYS */;
INSERT INTO `atoms` VALUES (1,1,'C',0),(2,1,'C',1),(3,1,'H',2),(4,1,'H',3),(5,2,'H',0),(6,2,'H',1),(7,2,'O',2),(8,3,'C',0),(9,3,'O',1),(10,3,'O',2),(11,4,'C',0),(12,4,'C',1),(13,4,'C',2),(14,4,'C',3),(15,4,'H',4),(16,4,'H',5),(17,4,'H',6),(18,4,'H',7),(19,4,'H ',8),(20,4,'H',9),(21,4,'H',10),(22,4,'H',11),(23,4,'H',12),(24,4,'H',13),(25,5,'C',0),(26,5,'C',1),(27,5,'C',2),(28,5,'C',3),(29,5,'H',4),(30,5,'H',5),(31,5,'H',6),(32,5,'H',7),(33,5,'H ',8),(34,5,'H',9),(35,5,'H',10),(36,5,'H',11),(37,5,'H',12),(38,5,'H',13);
/*!40000 ALTER TABLE `atoms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `edges`
--

DROP TABLE IF EXISTS `edges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edges` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mid` int(11) NOT NULL,
  `vertex1` int(11) NOT NULL,
  `vertex2` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `edges`
--

LOCK TABLES `edges` WRITE;
/*!40000 ALTER TABLE `edges` DISABLE KEYS */;
INSERT INTO `edges` VALUES (1,1,0,1),(2,1,0,1),(3,1,0,1),(4,1,0,2),(5,1,1,3),(6,2,0,2),(7,2,1,2),(9,3,0,1),(10,3,0,1),(11,3,0,2),(12,3,0,2),(13,4,0,1),(14,4,0,4),(15,4,0,8),(16,4,0,10),(17,4,1,2),(18,4,1,5),(19,4,1,11),(20,4,2,3),(21,4,2,6),(22,4,2,12),(23,4,3,7),(24,4,3,9),(25,4,3,13),(26,5,0,1),(27,5,0,4),(28,5,0,7),(29,5,0,9),(30,5,1,2),(31,5,1,3),(32,5,1,5),(33,5,2,6),(34,5,2,8),(35,5,2,10),(36,5,3,11),(37,5,3,12),(38,5,3,13);
/*!40000 ALTER TABLE `edges` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `molecules`
--

DROP TABLE IF EXISTS `molecules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `molecules` (
  `mid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `num_atoms` int(11) DEFAULT NULL,
  PRIMARY KEY (`mid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `molecules`
--

LOCK TABLES `molecules` WRITE;
/*!40000 ALTER TABLE `molecules` DISABLE KEYS */;
INSERT INTO `molecules` VALUES (1,'acetylene',4),(2,'water',3),(3,'carbon dioxide',3),(4,'butane',14),(5,'iso-butane',14);
/*!40000 ALTER TABLE `molecules` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-03-17 12:30:47
