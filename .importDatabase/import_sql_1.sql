-- --------------------------------------------------------
-- Host:                         ec2-18-200-90-241.eu-west-1.compute.amazonaws.com
-- Server version:               PostgreSQL 15.7 (Ubuntu 15.7-1.pgdg20.04+1) on x86_64-pc-linux-gnu, compiled by gcc (Ubuntu 9.4.0-1ubuntu1~20.04.2) 9.4.0, 64-bit
-- Server OS:                    
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES  */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Dumping structure for table public.data_gestione_applicazione
CREATE TABLE IF NOT EXISTS "data_gestione_applicazione" (
	"id" BIGINT NOT NULL,
	"value_number" BIGINT NULL DEFAULT NULL,
	"commento" VARCHAR(255) NULL DEFAULT NULL,
	"name" VARCHAR(255) NULL DEFAULT NULL,
	"value_string" VARCHAR(255) NULL DEFAULT NULL,
	PRIMARY KEY ("id"),
	UNIQUE INDEX "data_gestione_applicazione_name_key" ("name")
);

-- Dumping data for table public.data_gestione_applicazione: -1 rows
/*!40000 ALTER TABLE "data_gestione_applicazione" DISABLE KEYS */;
INSERT INTO "data_gestione_applicazione" ("id", "value_number", "commento", "name", "value_string") VALUES
	(2, 0, 'il token che serve per tutte le operazioni', 'TOKEN_TIKTOK', 'act.3irLCr9IONPGWmlUK42NCypuq6ld9uTuKbcqLFiXlI7k1Q2WbnbGMRxnEZAp!5209.e1'),
	(3, 0, 'il token con il quale si rigenera un token permanente', 'TOKEN_REFRESH_TIKTOK', 'rft.urBgLFG80DGAgUJpQydQ7TqyYrrNd1alfyofUDTw1SGMexTxs2XcPxL7NWP7!5168.e1'),
	(1, 0, 'serve per maggiore sicurezza autenticazione', 'CSRF_TIKTOK', '159159');
/*!40000 ALTER TABLE "data_gestione_applicazione" ENABLE KEYS */;

-- Dumping structure for table public.oroscopo_giornaliero
CREATE TABLE IF NOT EXISTS "oroscopo_giornaliero" (
	"num_segno" INTEGER NOT NULL,
	"data_oroscopo" TIMESTAMP NOT NULL,
	"id" BIGINT NOT NULL,
	"nome_file_video" VARCHAR(255) NULL DEFAULT NULL,
	"testo_oroscopo" TEXT NULL DEFAULT NULL,
	"video" INTEGER NULL DEFAULT NULL,
	UNIQUE INDEX "uk3a3x9u7pdohhwtv2nmxc0seeo" ("num_segno", "data_oroscopo"),
	PRIMARY KEY ("id")
);

-- Dumping data for table public.oroscopo_giornaliero: 12 rows
/*!40000 ALTER TABLE "oroscopo_giornaliero" DISABLE KEYS */;
INSERT INTO "oroscopo_giornaliero" ("num_segno", "data_oroscopo", "id", "nome_file_video", "testo_oroscopo", "video") VALUES
	(8, '2024-06-19 12:00:00', 14021, '2024-06-19_8.mp4', '18549585', 18549586),
	(4, '2024-06-20 12:00:00', 14056, '2024-06-20_4.mp4', '18564416', 18564417),
	(9, '2024-06-19 12:00:00', 14022, '2024-06-19_9.mp4', '18549588', 18549589),
	(5, '2024-06-20 12:00:00', 14057, '2024-06-20_5.mp4', '18564418', 18564419),
	(0, '2024-06-19 12:00:00', 14014, '2024-06-19_0.mp4', '18549558', 18549559),
	(10, '2024-06-19 12:00:00', 14023, '2024-06-19_10.mp4', '18549591', 18549592),
	(6, '2024-06-20 12:00:00', 14058, '2024-06-20_6.mp4', '18564420', 18564421),
	(0, '2024-06-18 12:00:00', 13002, '2024-06-18_0.mp4', '18534165', 18534166),
	(1, '2024-06-18 12:00:00', 13003, '2024-06-18_1.mp4', '18534169', 18534170),
	(2, '2024-06-18 12:00:00', 13004, '2024-06-18_2.mp4', '18534173', 18534174),
	(3, '2024-06-18 12:00:00', 13005, '2024-06-18_3.mp4', '18534177', 18534178),
	(1, '2024-06-19 12:00:00', 14015, '2024-06-19_1.mp4', '18549560', 18549561),
	(11, '2024-06-19 12:00:00', 14024, '2024-06-19_11.mp4', '18549594', 18549595),
	(7, '2024-06-20 12:00:00', 14059, '2024-06-20_7.mp4', '18564422', 18564423),
	(4, '2024-06-18 12:00:00', 12453, '2024-06-18_4.mp4', '18534181', 18534182),
	(2, '2024-06-19 12:00:00', 14016, '2024-06-19_2.mp4', '18549563', 18549564),
	(0, '2024-06-20 12:00:00', 14052, '2024-06-20_0.mp4', '18564414', 18564415),
	(8, '2024-06-20 12:00:00', 14060, '2024-06-20_8.mp4', '18564424', 18564425),
	(8, '2024-06-18 12:00:00', 12454, '2024-06-18_8.mp4', '18534197', 18534198),
	(4, '2024-06-19 12:00:00', 14017, '2024-06-19_4.mp4', '18549573', 18549574),
	(9, '2024-06-20 12:00:00', 14061, '2024-06-20_9.mp4', '18564426', 18564427),
	(9, '2024-06-18 12:00:00', 12455, '2024-06-18_9.mp4', '18534201', 18534202),
	(5, '2024-06-19 12:00:00', 14018, '2024-06-19_5.mp4', '18549576', 18549577),
	(1, '2024-06-20 12:00:00', 14053, '2024-06-20_1.mp4', '18564401', 18564402),
	(10, '2024-06-20 12:00:00', 14062, '2024-06-20_10.mp4', '18564428', 18564429),
	(5, '2024-06-18 12:00:00', 13052, '2024-06-18_5.mp4', '18534185', 18534186),
	(6, '2024-06-18 12:00:00', 13053, '2024-06-18_6.mp4', '18534189', 18534190),
	(6, '2024-06-19 12:00:00', 14019, '2024-06-19_6.mp4', '18549580', 18549581),
	(2, '2024-06-20 12:00:00', 14054, '2024-06-20_2.mp4', '18564410', 18564411),
	(11, '2024-06-20 12:00:00', 14063, '2024-06-20_11.mp4', '18564431', 18564432),
	(7, '2024-06-18 12:00:00', 13103, '2024-06-18_7.mp4', '18534193', 18534194),
	(10, '2024-06-18 12:00:00', 13104, '2024-06-18_10.mp4', '18534205', 18534206),
	(11, '2024-06-18 12:00:00', 13105, '2024-06-18_11.mp4', '18534209', 18534210),
	(7, '2024-06-19 12:00:00', 14020, '2024-06-19_7.mp4', '18549582', 18549583),
	(3, '2024-06-20 12:00:00', 14055, '2024-06-20_3.mp4', '18564412', 18564413);
/*!40000 ALTER TABLE "oroscopo_giornaliero" ENABLE KEYS */;

-- Dumping structure for table public.tutorials
CREATE TABLE IF NOT EXISTS "tutorials" (
	"id" BIGINT NOT NULL,
	"description" VARCHAR(255) NULL DEFAULT 'NULL::character varying',
	"published" BOOLEAN NULL DEFAULT NULL,
	"title" VARCHAR(255) NULL DEFAULT 'NULL::character varying',
	PRIMARY KEY ("id")
);

-- Dumping data for table public.tutorials: 3 rows
/*!40000 ALTER TABLE "tutorials" DISABLE KEYS */;
INSERT INTO "tutorials" ("id", "description", "published", "title") VALUES
	(1, 'se vis pacem', 'false', 'ok'),
	(2, 'para', 'false', 'no'),
	(3, 'bellum', 'false', 'ok');
/*!40000 ALTER TABLE "tutorials" ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
