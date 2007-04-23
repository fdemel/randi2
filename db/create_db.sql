﻿CREATE TABLE Aktivierung (
  aktivierungslink VARCHAR(16) NOT NULL,
  Benutzerkonto_benutzerkontenID INT UNSIGNED NOT NULL,
  versanddatum DATE NOT NULL,
  PRIMARY KEY(aktivierungslink),
  INDEX Aktivierung_FKIndex1(Benutzerkonto_benutzerkontenID)
)
TYPE=InnoDB;

CREATE TABLE Benutzerkonto (
  benutzerkontenID INT UNSIGNED NOT NULL AUTO_INCREMENT,
  Person_personenID INT UNSIGNED NOT NULL,
  loginname VARCHAR(14) NOT NULL,
  passwort CHAR(64) NOT NULL,
  rolle ENUM('Studienarzt', 'Studienleiter', 'System Operator', 'Administrator') NOT NULL,
  erster_login DATETIME NULL,
  letzter_login DATETIME NULL,
  gesperrt BOOL NOT NULL,
  PRIMARY KEY(benutzerkontenID),
  INDEX Benutzerkonto_FKIndex1(Person_personenID)
)
TYPE=InnoDB;

CREATE TABLE Patient (
  patientenID INT UNSIGNED NOT NULL AUTO_INCREMENT,
  Benutzerkonto_benutzerkontenID INT UNSIGNED NOT NULL,
  Studienarm_studienarmID INT UNSIGNED NOT NULL,
  initialen VARCHAR(4) NOT NULL,
  geburtsdatum DATE NOT NULL,
  geschlecht ENUM('weiblich', 'maennlich') NOT NULL,
  aufklaerungsdatum DATE NOT NULL,
  koerperoberflaeche FLOAT NOT NULL,
  performancestatus ENUM('0', '1', '2', '3', '4') NOT NULL,
  PRIMARY KEY(patientenID),
  INDEX Patient_FKIndex1(Studienarm_studienarmID),
  INDEX Patient_FKIndex2(Benutzerkonto_benutzerkontenID)
)
TYPE=InnoDB;

CREATE TABLE Person (
  personenID INT UNSIGNED NOT NULL AUTO_INCREMENT,
  Person_personenID INT UNSIGNED NOT NULL,
  nachname VARCHAR(50) NOT NULL,
  vorname VARCHAR(50) NOT NULL,
  titel ENUM('Prof.', 'Dr.', 'Prof. Dr.') NULL,
  geschlecht ENUM('weiblich', 'maennlich') NOT NULL,
  telefonnummer VARCHAR(26) NOT NULL,
  handynummer VARCHAR(26) NULL,
  fax VARCHAR(26) NULL,
  email VARCHAR(26) NOT NULL,
  PRIMARY KEY(personenID),
  INDEX Person_FKIndex1(Person_personenID)
)
TYPE=InnoDB;

CREATE TABLE Strata_Typen (
  strata_TypenID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  Studie_studienID INT UNSIGNED NOT NULL,
  name VARCHAR(40) NOT NULL,
  beschreibung TEXT NULL,
  PRIMARY KEY(strata_TypenID),
  INDEX Strata_Typen_FKIndex1(Studie_studienID)
)
TYPE=InnoDB;

CREATE TABLE Strata_Werte (
  strata_WerteID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  Strata_Typen_strata_TypenID INTEGER UNSIGNED NOT NULL,
  wert VARCHAR(100) NOT NULL,
  PRIMARY KEY(strata_WerteID),
  INDEX Strata_Werte_FKIndex1(Strata_Typen_strata_TypenID)
)
TYPE=InnoDB;

CREATE TABLE Strata_Werte_has_Patient (
  Strata_Werte_strata_WerteID INTEGER UNSIGNED NOT NULL,
  Patient_patientenID INT UNSIGNED NOT NULL,
  PRIMARY KEY(Strata_Werte_strata_WerteID, Patient_patientenID),
  INDEX Strata_Werte_has_Patient_FKIndex1(Strata_Werte_strata_WerteID),
  INDEX Strata_Werte_has_Patient_FKIndex2(Patient_patientenID)
)
TYPE=InnoDB;

CREATE TABLE Studie (
  studienID INT UNSIGNED NOT NULL AUTO_INCREMENT,
  Benutzerkonto_benutzerkontenID INT UNSIGNED NOT NULL,
  name VARCHAR(50) NOT NULL,
  beschreibung TEXT NULL,
  startdatum DATE NOT NULL,
  enddatum DATE NOT NULL,
  studienprotokoll LONGBLOB NOT NULL,
  randomisationArt ENUM('Vollstaendige', 'Block', 'Block mit Strata', 'Minimisation') NOT NULL,
  PRIMARY KEY(studienID),
  INDEX Studie_FKIndex1(Benutzerkonto_benutzerkontenID)
)
TYPE=InnoDB;

CREATE TABLE Studienarm (
  studienarmID INT UNSIGNED NOT NULL AUTO_INCREMENT,
  Studie_studienID INT UNSIGNED NOT NULL,
  status_aktivitaet TINYINT UNSIGNED NOT NULL,
  bezeichnung VARCHAR(50) NOT NULL,
  beschreibung TEXT NULL,
  PRIMARY KEY(studienarmID),
  INDEX Studienarm_FKIndex1(Studie_studienID)
)
TYPE=InnoDB;

CREATE TABLE Studie_has_Zentrum (
  Studie_studienID INT UNSIGNED NOT NULL,
  Zentrum_zentrumsID INT UNSIGNED NOT NULL,
  PRIMARY KEY(Studie_studienID, Zentrum_zentrumsID),
  INDEX Studie_has_Zentrum_FKIndex1(Studie_studienID),
  INDEX Studie_has_Zentrum_FKIndex2(Zentrum_zentrumsID)
)
TYPE=InnoDB;

CREATE TABLE Zentrum (
  zentrumsID INT UNSIGNED NOT NULL AUTO_INCREMENT,
  Person_personenID INT UNSIGNED NOT NULL,
  institution VARCHAR(70) NOT NULL,
  abteilungsname VARCHAR(70) NOT NULL,
  ort VARCHAR(50) NOT NULL,
  plz CHAR(5) NOT NULL,
  strasse VARCHAR(50) NOT NULL,
  hausnummer VARCHAR(6) NOT NULL,
  passwort CHAR(12) NOT NULL,
  aktiviert BOOL NOT NULL,
  PRIMARY KEY(zentrumsID),
  INDEX Zentrum_FKIndex1(Person_personenID)
)
TYPE=InnoDB;


