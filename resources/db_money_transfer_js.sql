-- MySQL dump 10.13  Distrib 8.0.20, for Linux (x86_64)
--
-- Host: localhost    Database: db_money_transfer_js
-- ------------------------------------------------------
-- Server version	8.0.20-0ubuntu0.20.04.1

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
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `accountId` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `balance` int NOT NULL,
  PRIMARY KEY (`accountId`),
  KEY `username_idx` (`username`),
  CONSTRAINT `username` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=136 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (128,'albe',100000),(129,'albe',100010),(130,'fra',100000),(131,'fra',99978),(132,'arigalzi',100367),(133,'cristin',100000),(134,'albe',100000),(135,'fra',99645);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contact` (
  `ownerUsername` varchar(45) NOT NULL,
  `contactAccount` int NOT NULL,
  PRIMARY KEY (`ownerUsername`,`contactAccount`),
  UNIQUE KEY `contactAccount_UNIQUE` (`contactAccount`),
  KEY `contactAccount_idx` (`contactAccount`),
  CONSTRAINT `contactAccount` FOREIGN KEY (`contactAccount`) REFERENCES `account` (`accountId`),
  CONSTRAINT `ownerUsername` FOREIGN KEY (`ownerUsername`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact`
--

LOCK TABLES `contact` WRITE;
/*!40000 ALTER TABLE `contact` DISABLE KEYS */;
INSERT INTO `contact` VALUES ('fra',129),('fra',132);
/*!40000 ALTER TABLE `contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `transactionId` int NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `amount` int NOT NULL,
  `originId` int NOT NULL,
  `destinationId` int NOT NULL,
  `description` varchar(45) NOT NULL,
  PRIMARY KEY (`transactionId`),
  KEY `originId_idx` (`originId`),
  KEY `destinationId_idx` (`destinationId`),
  CONSTRAINT `destinationId` FOREIGN KEY (`destinationId`) REFERENCES `account` (`accountId`),
  CONSTRAINT `originId` FOREIGN KEY (`originId`) REFERENCES `account` (`accountId`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (1,'2020-06-21 20:14:08',20000,130,129,'sport car'),(2,'2020-06-21 20:19:52',120,130,131,'hat'),(3,'2020-06-21 20:23:38',25,131,132,'ice cream'),(4,'2020-06-21 20:35:28',20,130,129,'food'),(5,'2020-06-21 20:48:17',15,130,129,'dinner'),(6,'2020-06-21 20:51:35',30000,130,129,'mercedes'),(7,'2020-06-21 20:54:37',5,130,129,'chips'),(8,'2020-06-21 20:58:59',25,130,129,'tshirt'),(9,'2020-06-21 21:01:39',100,130,132,'boots'),(10,'2020-06-21 21:03:16',250,130,129,'groceries'),(11,'2020-06-21 21:25:31',200,130,129,'debugging'),(12,'2020-06-21 21:27:23',130,130,132,'dog food'),(13,'2020-06-21 21:41:25',4999,131,129,'gucci bag'),(14,'2020-06-21 23:22:35',150,131,132,'shoes'),(15,'2020-06-21 23:29:28',130,130,132,'banana'),(16,'2020-06-21 23:37:35',300,131,129,'fridge'),(17,'2020-06-22 12:13:28',20,130,129,'apple'),(18,'2020-06-22 12:13:48',5,130,132,'orange juice'),(19,'2020-06-22 12:16:56',15,130,132,'coconut'),(20,'2020-06-22 12:18:07',15,130,133,'taralli'),(21,'2020-06-22 12:20:36',55,131,133,'headphones'),(22,'2020-06-22 12:26:06',15,131,133,'money'),(23,'2020-06-22 12:35:46',1,131,133,'coffee'),(25,'2020-06-22 15:27:31',20,130,129,'test db'),(26,'2020-06-22 15:28:29',20,130,133,'testdb'),(27,'2020-06-22 16:39:29',800,130,129,'iphone'),(28,'2020-06-22 16:44:28',800,131,129,'bag'),(29,'2020-06-22 16:45:01',2000,131,129,'surgery'),(30,'2020-06-22 17:22:23',500,131,129,'prova'),(31,'2020-06-22 17:25:41',1500,131,129,'prova2'),(32,'2020-06-22 17:33:31',500,130,129,'households'),(33,'2020-06-22 17:35:17',1300,130,129,'prova'),(34,'2020-06-22 18:04:23',150,130,129,'ciao'),(35,'2020-06-22 21:04:02',22,131,132,'things'),(36,'2020-06-22 21:07:35',133,135,132,'test'),(37,'2020-06-22 22:19:17',10,135,132,'test'),(38,'2020-06-22 22:40:27',10,135,129,'test'),(39,'2020-06-22 22:42:08',2,135,132,'test2'),(40,'2020-06-22 22:45:23',200,135,132,'test3');
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  PRIMARY KEY (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('alberto','latino','albpassword','albe','alberto.latino@mail.polimi.it'),('Arianna','Galzerano','password','arigalzi','arigalzi6@gmail.com'),('Cristina','Bombelli','password','cristin','cristin@mail.polimi.it'),('francesco','gonzales','bubblesort','fra','francesco.gonzales@mail.polimi.it');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-06-22 22:58:15
