-- --------------------------------------------------------
-- Host:                         ec2-18-200-90-241.eu-west-1.compute.amazonaws.com
-- Server version:               PostgreSQL 15.5 (Ubuntu 15.5-1.pgdg20.04+1) on x86_64-pc-linux-gnu, compiled by gcc (Ubuntu 9.4.0-1ubuntu1~20.04.2) 9.4.0, 64-bit
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
	"name" VARCHAR(255) NULL DEFAULT NULL,
	"value_string" VARCHAR(255) NULL DEFAULT NULL,
	"value_number" BIGINT NULL DEFAULT NULL,
	"commento" VARCHAR(255) NULL DEFAULT NULL,
	PRIMARY KEY ("id")
);

-- Dumping data for table public.data_gestione_applicazione: 3 rows
/*!40000 ALTER TABLE "data_gestione_applicazione" DISABLE KEYS */;
INSERT INTO "data_gestione_applicazione" ("id", "name", "value_string", "value_number", "commento") VALUES
	(1, 'CSRF_TIKTOK', '539879', 0, 'serve per maggiore sicurezza autenticazione'),
	(3, 'TOKEN_REFRESH_TIKTOK', 'rft.wdjyXL64XZFwK06voffjzppZwdqUqq4brgHhQgTIaBKu6I65LXURjyLIVe9C!5196', 0, 'il token con il quale si rigenera un token permanente'),
	(2, 'TOKEN_TIKTOK', 'act.CWNZWmKZYJIiAEeyM5asMJVG4CNQW8X0scuxJFSii795B4stwZDZWLJNv5oA!5164.e1', 0, 'il token che serve per tutte le operazioni');
/*!40000 ALTER TABLE "data_gestione_applicazione" ENABLE KEYS */;

-- Dumping structure for table public.tutorials
CREATE TABLE IF NOT EXISTS "tutorials" (
	"id" BIGINT NOT NULL,
	"description" VARCHAR(255) NULL DEFAULT NULL,
	"published" BOOLEAN NULL DEFAULT NULL,
	"title" VARCHAR(255) NULL DEFAULT NULL,
	PRIMARY KEY ("id")
);

-- Dumping data for table public.tutorials: 3 rows
/*!40000 ALTER TABLE "tutorials" DISABLE KEYS */;
INSERT INTO "tutorials" ("id", "description", "published", "title") VALUES
	(1, 'mdma', 'false', 'ok'),
	(2, 'cocaina', 'false', 'no'),
	(3, 'fumo', 'false', 'ok');
/*!40000 ALTER TABLE "tutorials" ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
