

--
-- Table structure for table `molecules`
--
DROP TABLE IF EXISTS `atoms`;
DROP TABLE IF EXISTS `edges`;

DROP TABLE IF EXISTS `molecules`;

CREATE TABLE `molecules` (
  `mid` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(255) NOT NULL,
  `num_atoms` INTEGER NOT NULL);
create INDEX mid on molecules(mid);

INSERT INTO `molecules` VALUES (1,'acetylene',4),(2,'water',3),(3,'carbon dioxide',3),(4,'butane',14),(5,'iso-butane',14),(6,'1-aminopropan-2-ol',14);

--
-- Table structure for table `atoms`
--


CREATE TABLE `atoms` (
  `id` BIGINT NOT NULL AUTO_INCREMENT, -- PRIMARY KEY ,
  `mid` BIGINT NOT NULL,
  `atom` varchar(10) NOT NULL,
  `vertex` INTEGER NOT NULL,
   PRIMARY KEY (mid, atom, vertex),
   foreign key (mid) references molecules(mid));

--create INDEX atom_mid on atoms(mid, atom);

INSERT INTO `atoms` VALUES (1,1,'C',0),(2,1,'C',1),(3,1,'H',2),(4,1,'H',3),(5,2,'H',0),(6,2,'H',1),(7,2,'O',2),(8,3,'C',0),(9,3,'O',1),(10,3,'O',2),(11,4,'C',0),(12,4,'C',1),(13,4,'C',2),(14,4,'C',3),(15,4,'H',4),(16,4,'H',5),(17,4,'H',6),(18,4,'H',7),(19,4,'H',8),(20,4,'H',9),(21,4,'H',10),(22,4,'H',11),(23,4,'H',12),(24,4,'H',13),(25,5,'C',0),(26,5,'C',1),(27,5,'C',2),(28,5,'C',3),(29,5,'H',4),(30,5,'H',5),(31,5,'H',6),(32,5,'H',7),(33,5,'H',8),(34,5,'H',9),(35,5,'H',10),(36,5,'H',11),(37,5,'H',12),(38,5,'H',13),(39,6,'O',0),(40,6,'N',1),(41,6,'C',2),(42,6,'C',3),(43,6,'C',4),(44,6,'H',5),(45,6,'H',6),(46,6,'H',7),(47,6,'H',8),(48,6,'H',9),(49,6,'H',10),(50,6,'H',11),(51,6,'H',12),(52,6,'H',13);

--
-- Table structure for table `edges`
--


CREATE TABLE `edges` (
  `id` BIGINT NOT NULL AUTO_INCREMENT, -- PRIMARY KEY,
  `mid` BIGINT NOT NULL,
  `vertex1` INTEGER NOT NULL,
  `vertex2` INTEGER NOT NULL,
   foreign key (mid) references molecules(mid),
  PRIMARY KEY (id, mid)
  );

--create INDEX edge_mid on edges(mid, vertex1, vertex2);

INSERT INTO `edges` VALUES (1,1,0,1),(2,1,0,1),(3,1,0,1),(4,1,0,2),(5,1,1,3),(6,2,0,2),(7,2,1,2),(9,3,0,1),(10,3,0,1),(11,3,0,2),(12,3,0,2),(13,4,0,1),(14,4,0,4),(15,4,0,8),(16,4,0,10),(17,4,1,2),(18,4,1,5),(19,4,1,11),(20,4,2,3),(21,4,2,6),(22,4,2,12),(23,4,3,7),(24,4,3,9),(25,4,3,13),(26,5,0,1),(27,5,0,4),(28,5,0,7),(29,5,0,9),(30,5,1,2),(31,5,1,3),(32,5,1,5),(33,5,2,6),(34,5,2,8),(35,5,2,10),(36,5,3,11),(37,5,3,12),(38,5,3,13),(39,6,0,2),(40,6,0,13),(41,6,1,3),(42,6,1,11),(43,6,1,12),(44,6,2,3),(45,6,2,4),(46,6,2,5),(47,6,3,6),(48,6,3,7),(49,6,4,8),(50,6,4,9),(51,6,4,10);



-- Dump completed on 2020-03-19 19:04:34;

