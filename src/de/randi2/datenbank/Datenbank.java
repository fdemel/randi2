package de.randi2.datenbank;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

import de.randi2.datenbank.exceptions.DatenbankFehlerException;
import de.randi2.model.exceptions.PersonException;
import de.randi2.model.fachklassen.beans.AktivierungBean;
import de.randi2.model.fachklassen.beans.BenutzerkontoBean;
import de.randi2.model.fachklassen.beans.PersonBean;
import de.randi2.model.fachklassen.beans.StudieBean;
import de.randi2.model.fachklassen.beans.ZentrumBean;
import de.randi2.model.fachklassen.beans.PersonBean.Titel;
import de.randi2.utility.NullKonstanten;

/**
 * <p>Datenbankklasse</p>
 * @version $Id$
 * @author Frederik Reifschneider [Reifschneider@stud.uni-heidelberg.de]
 */
public class Datenbank implements DatenbankSchnittstelle{
	//TODO es muss bei allen schreib zugriffen geloggt werden!!
	//TODO Exception Handling.
	
	/**
	 * Logging Objekt
	 */
	private Logger log = null;
	
	/**
	 * Enum Klasse welche die Tabellen der Datenbank auflistet
	 * @author Frederik Reifschneider [Reifschneider@stud.uni-heidelberg.de]
	 */
	private enum Tabellen{	
		ZENTRUM ("Zentrum"),
		PERSON ("Person"),
		BENUTZERKONTO("Benutzerkonto"),
		AKTIVIERUNG("Aktivierung"),
		STUDIENARM("Studiearm"),
		STUDIE("Studie"),
		STATISTIK("Statistik"),
		RANDOMISATION("Randomisation"),
		PATIENT("Patient"),
		STUDIE_ZENTRUM("Studie_has_Zentrum");
		
		private String name = "";
		private Tabellen(String name) {
			this.name=name;
		}
		
		public String toString() {
			return this.name;
		}
	}
	
	/**
	 * Enum Klasse welche die Felder der Tabelle Zentrum repraesntiert.
	 * @author Frederik Reifschneider [Reifschneider@stud.uni-heidelberg.de]
	 *
	 */
	private enum FelderZentrum {
		ID ("zentrumsID"),
		ANSPRECHPARTNERID ("ansprechpartnerID"),
		INSTITUTION ("institution"),
		ABTEILUNGSNAME("abteilungsname"),
		ORT ("ort"),
		PLZ ("plz"),
		STRASSE ("strasse"),
		HAUSNUMMER ("hausnummer"),
		PASSWORT ("passwort"),
		AKTIVIERT ("aktiviert");
		
		private String name = "";
		private FelderZentrum(String name) {
			this.name=name;
		}
		
		public String toString() {
			return this.name;
		}
	}
	
	/**
	 * Enum Klasse welche die Felder der Tabelle Person repraesentiert.
	 * @author Frederik Reifschneider [Reifschneider@stud.uni-heidelberg.de]
	 *
	 */
	private enum FelderPerson {
		
		ID ("personenID"),
		NACHNAME ("nachname"),
		VORNAME ("vorname"),
		TITEL ("titel"),
		GESCHLECHT ("geschlecht"),
		TELEFONNUMMER ("telefonnummer"),
		HANDYNUMMER ("handynummer"),
		FAX ("fax"),
		EMAIL ("email"),
		STELLVERTRETER ("stellvertreterID");
		
		private String name = "";
		private FelderPerson(String name) {
			this.name=name;
		}
		
		public String toString() {
			return this.name;
		}
	}
	
	/**
	 * Enum Klasse welche die Felder der Tabelle Benutzerkonto repraesentiert.
	 * @author Kai Marco Krupka [kai.krupka@urz.uni-heidelberg.de]
	 *
	 */
	private enum BenutzerKontoFelder{
		ID("benutzerkontenID"),
		PERSON("Person_personenID"),
		LOGINNAME("loginname"),
		PASSWORT("passwort"),
		ROLLEACCOUNT("rolle"),
		ERSTERLOGIN("erster_login"),
		LETZTERLOGIN("letzter_login"),
		GESPERRT("gesperrt");
		
		
		private String name = ""; 
		
		private BenutzerKontoFelder(String name){
			this.name = name;
		}
		
		public String toString(){
			return this.name;
		}	
	}
	
	/**
	 * Enum Klasse welche die Felder der Tabelle Aktivierung repraesentiert.
	 * @author Kai Marco Krupka [kai.krupka@urz.uni-heidelberg.de]
	 *
	 */
	private enum AktivierungsFelder{
		ID("aktivierungsID"),
		BENUTZER("Benutzerkonto_benutzerkontenID"),
		LINK("aktivierungslink"),
		VERSANDDATUM("versanddatum");
		
		
		private String name = ""; 
		
		private AktivierungsFelder(String name){
			this.name = name;
		}
		
		public String toString(){
			return this.name;
		}	
	}
	
	/**
	 * Enum Klasse welche die Felder der Tabelle Studie repraesentiert.
	 * @author Kai Marco Krupka [kai.krupka@urz.uni-heidelberg.de]
	 *
	 */
	private enum StudieFelder{
		ID("studienID"),
		BENUTZER("Benutzerkonto_benutzerkontenID"),
		NAME("name"),
		BESCHREIBUNG("beschreibung"),
		STARTDATUM("startdatum"),
		ENDDATUM("enddatum"),
		PROTOKOLL("studienprotokoll"),
		RANDOMISATIONSART("randomisationsart"),
		STATUS("status_studie");
		
		
		private String name = ""; 
		
		private StudieFelder(String name){
			this.name = name;
		}
		
		public String toString(){
			return this.name;
		}	
	}
	
	/**
	 * Konstruktor der Datenbankklasse.
	 */
	public Datenbank() {
        log = Logger.getLogger("Randi2.Datenaenderung");
        try {
			JAXPConfigurator.configure("./src/conf/release/proxool_cfg.xml", false);
		} catch (ProxoolException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dokumentation siehe Schnittstellenbeschreibung
	 * @see de.randi2.datenbank.DatenbankSchnittstelle#loeschenObjekt(de.randi2.datenbank.Filter)
	 */
	public <T extends Filter> void loeschenObjekt(T zuLoeschendesObjekt) throws DatenbankFehlerException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Dokumentation siehe Schnittstellenbeschreibung
	 * @see de.randi2.datenbank.DatenbankSchnittstelle#schreibenObjekt(de.randi2.datenbank.Filter)
	 */
	public <T extends Filter> T schreibenObjekt(T zuSchreibendesObjekt) throws DatenbankFehlerException {
		//ZentrumBean schreiben
		if (zuSchreibendesObjekt instanceof ZentrumBean) {
			ZentrumBean zentrum = (ZentrumBean) zuSchreibendesObjekt;	
			return (T) schreibenZentrum(zentrum);
		}
		//PersonBean schreiben
		else if (zuSchreibendesObjekt instanceof PersonBean) {
			PersonBean person = (PersonBean) zuSchreibendesObjekt;
			return (T) schreibenPerson(person);
		}
		//BenutzerKontoBean schreiben
		else if (zuSchreibendesObjekt instanceof BenutzerkontoBean) {
			BenutzerkontoBean benutzerKonto = (BenutzerkontoBean) zuSchreibendesObjekt;
			return (T) this.schreibenBenutzerKonto(benutzerKonto);
		}
		//AktivierungsBean
		else if (zuSchreibendesObjekt instanceof AktivierungBean) {
			AktivierungBean aktivierung = (AktivierungBean) zuSchreibendesObjekt;
			return (T) this.schreibenAktivierung(aktivierung);
		}
		//StudieBean
		else if (zuSchreibendesObjekt instanceof StudieBean) {
			StudieBean studie = (StudieBean) zuSchreibendesObjekt;
			return (T) this.schreibenStudie(studie);
		}
		
		return null;
	}
	
	
	/**
	 * Schreibt Personbean in die Datenbank
	 * @param person
	 * 			zu schreibendes PersonBean
	 * @return
	 * 			PersonBean mit vergebener ID oder null falls nur ein Update durchgefuehrt wurde
	 * @throws DatenbankFehlerException 
	 */
	private PersonBean schreibenPerson(PersonBean person) throws DatenbankFehlerException {
		//TODO Logging
		Connection con=null; 
		try {
			con = getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatenbankFehlerException(DatenbankFehlerException.CONNECTION_ERR);
		}
		String sql; 
		PreparedStatement pstmt;
		ResultSet rs;
		//neue Person da ID der Nullkonstante entspricht
		if(person.getId()== NullKonstanten.NULL_LONG) {
			long id = Long.MIN_VALUE;
			sql = "INSERT INTO "+Tabellen.PERSON+"("+
			FelderPerson.ID+","+
			FelderPerson.NACHNAME+","+
			FelderPerson.VORNAME+","+
			FelderPerson.GESCHLECHT+","+
			FelderPerson.TITEL+","+
			FelderPerson.EMAIL+","+
			FelderPerson.FAX+","+
			FelderPerson.TELEFONNUMMER+","+
			FelderPerson.STELLVERTRETER+")"+
			"VALUES (NULL,?,?,?,?,?,?,?,?);";
			try {
				pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, person.getNachname());
				pstmt.setString(2, person.getVorname());
				pstmt.setString(3, Character.toString(person.getGeschlecht()));
				pstmt.setString(4, person.getTitel().toString());
				pstmt.setString(5, person.getEmail());
				pstmt.setString(6, person.getFax());
				pstmt.setString(7, person.getTelefonnummer());
				if (person.getStellvertreterID()==NullKonstanten.NULL_LONG) {
					pstmt.setNull(8, Types.NULL);
				}
				else {
					pstmt.setLong(8,person.getStellvertreterID());
				}
				
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				rs.next();
				id = rs.getLong(1);
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatenbankFehlerException(DatenbankFehlerException.SCHREIBEN_ERR);
			}
			person.setId(id);
			return person;						
		}
		//vorhandenes Zentrum wird aktualisiert
		else {
			sql = "UPDATE "+Tabellen.PERSON+
			" SET "+FelderPerson.NACHNAME+"=?,"+
			FelderPerson.VORNAME+"=?,"+
			FelderPerson.GESCHLECHT+"=?,"+
			FelderPerson.TITEL+"=?,"+
			FelderPerson.EMAIL+"=?,"+
			FelderPerson.FAX+"=?,"+
			FelderPerson.TELEFONNUMMER+"=?,"+
			FelderPerson.STELLVERTRETER+"=?,"+
			" WHERE "+FelderPerson.ID+"=?";
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, person.getNachname());
				pstmt.setString(2, person.getVorname());
				pstmt.setString(3,Character.toString(person.getGeschlecht())); 
				pstmt.setString(4, person.getTitel().toString());
				pstmt.setString(5, person.getEmail());
				pstmt.setString(6, person.getFax());
				pstmt.setString(7, person.getTelefonnummer());
				pstmt.setLong(8, person.getStellvertreterID());
				pstmt.setLong(9, person.getId());	
				pstmt.executeUpdate();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatenbankFehlerException(DatenbankFehlerException.SCHREIBEN_ERR);
			}
		}		
		try {
			closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatenbankFehlerException(DatenbankFehlerException.CONNECTION_ERR);
		}
		return null;	
	}

	/**
	 * Speichert bzw. aktualisiert das uebergebene Zentrum in der Datenbank.
	 * @param zentrum
	 * 			zu speicherndes Zentrum
	 * @return
	 * 			das Zentrum mit der vergebenen eindeutigen ID bzw. das aktualisierte Zentrum
	 * @throws DatenbankFehlerException 
	 */
	private ZentrumBean schreibenZentrum(ZentrumBean zentrum) throws DatenbankFehlerException {
		//TODO Logging
		Connection con=null;
		try {
			con = getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatenbankFehlerException(DatenbankFehlerException.CONNECTION_ERR);
		}
		String sql; 
		PreparedStatement pstmt;
		ResultSet rs;
		//neues Zentrum da ID der Nullkonstante entspricht
		if(zentrum.getId()== NullKonstanten.NULL_LONG) {
			long id = Long.MIN_VALUE;
			sql = "INSERT INTO "+Tabellen.ZENTRUM+"("+
			FelderZentrum.ID+","+
			FelderZentrum.INSTITUTION+","+
			FelderZentrum.ABTEILUNGSNAME+","+			
			FelderZentrum.ANSPRECHPARTNERID+","+			
			FelderZentrum.STRASSE+","+
			FelderZentrum.HAUSNUMMER+","+	
			FelderZentrum.PLZ+","+
			FelderZentrum.ORT+","+
			FelderZentrum.PASSWORT+","+				
			FelderZentrum.AKTIVIERT+")"+
			"VALUES (NULL,?,?,?,?,?,?,?,?,?);";
			try {
				pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, zentrum.getInstitution());
				pstmt.setString(2, zentrum.getAbteilung());
				pstmt.setLong(3, zentrum.getAnsprechpartnerId());
				pstmt.setString(4, zentrum.getStrasse());
				pstmt.setString(5, zentrum.getHausnr());
				pstmt.setString(6, zentrum.getPlz());
				pstmt.setString(7, zentrum.getOrt());
				pstmt.setString(8, zentrum.getPasswort());							
				pstmt.setBoolean(9, zentrum.getIstAktiviert());
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				rs.next();
				id = rs.getLong(1);
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatenbankFehlerException(DatenbankFehlerException.SCHREIBEN_ERR);
			}
			zentrum.setId(id);
			return zentrum;						
		}
		//vorhandenes Zentrum wird aktualisiert
		else {
			sql = "UPDATE "+Tabellen.ZENTRUM+			
			" SET "+FelderZentrum.INSTITUTION+"=?,"+
			FelderZentrum.ABTEILUNGSNAME+"=?,"+			
			FelderZentrum.ANSPRECHPARTNERID+"=?,"+			
			FelderZentrum.STRASSE+"=?,"+
			FelderZentrum.HAUSNUMMER+"=?,"+	
			FelderZentrum.PLZ+"=?,"+
			FelderZentrum.ORT+"=?,"+
			FelderZentrum.PASSWORT+"=?,"+				
			FelderZentrum.AKTIVIERT+"=?"+
			" WHERE "+FelderPerson.ID+"=?";
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, zentrum.getInstitution());
				pstmt.setString(2, zentrum.getAbteilung());
				pstmt.setLong(3,zentrum.getAnsprechpartnerId());
				pstmt.setString(4, zentrum.getStrasse());
				pstmt.setString(5, zentrum.getHausnr());
				pstmt.setString(6, zentrum.getPlz());
				pstmt.setString(7, zentrum.getOrt());
				pstmt.setString(8, zentrum.getPasswort());
				pstmt.setLong(9, zentrum.getId());	
				pstmt.executeUpdate();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatenbankFehlerException(DatenbankFehlerException.SCHREIBEN_ERR);
			}
		}		
		try {
			closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatenbankFehlerException(DatenbankFehlerException.CONNECTION_ERR);
		}
		return null;		
	}
	
	/**
	 * Speichert bzw. aktualisiert das übergebene Benutzerkonto.
	 * @param benutzerKonto welches gespeichert (ohne ID) oder aktualisiert (mit ID) werden soll.
	 * @return das gespeicherte Objekt MIT ID, bzw. NULL falls ein Update durchgeführt wurde.
	 * @throws DatenbankFehlerException wirft Datenbankfehler bei Verbindungs- oder Schreibfehlern.
	 */
	private BenutzerkontoBean schreibenBenutzerKonto(BenutzerkontoBean benutzerKonto) throws DatenbankFehlerException{
		Connection con = null;
		String sql = "";
		String calFirst = "";
		String calLast = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			con = this.getConnection();
		}catch(SQLException e){
			e.printStackTrace();
			throw new DatenbankFehlerException(DatenbankFehlerException.CONNECTION_ERR);
		}
		//Neues Benutzerkonto
		if(benutzerKonto.getId() == NullKonstanten.NULL_LONG){
			int i = 1;
			long id = Long.MIN_VALUE;
			sql = "INSERT INTO "+Tabellen.BENUTZERKONTO+" (" +
				BenutzerKontoFelder.ID + ", "+
				BenutzerKontoFelder.PERSON + ", "+
				BenutzerKontoFelder.LOGINNAME + ", "+
				BenutzerKontoFelder.PASSWORT + ", "+
				BenutzerKontoFelder.ROLLEACCOUNT + ", "+
				BenutzerKontoFelder.ERSTERLOGIN + ", "+
				BenutzerKontoFelder.LETZTERLOGIN + ", "+
				BenutzerKontoFelder.GESPERRT + ")"+
				" VALUES (NULL,?,?,?,?,?,?,?)";
			try{
				pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pstmt.setLong(i++, benutzerKonto.getBenutzer().getId());
				pstmt.setString(i++, benutzerKonto.getBenutzername());
				pstmt.setString(i++, benutzerKonto.getPasswort());
				pstmt.setString(i++, benutzerKonto.getRolle().getName());
				if(benutzerKonto.getErsterLogin()!= null){
					calFirst = this.getSqlDateByJavaGregorianCalendar(benutzerKonto.getErsterLogin());
					pstmt.setDate(i++, java.sql.Date.valueOf(calFirst));
				}
				else{
					pstmt.setNull(i++, Types.DATE);
				}
				if(benutzerKonto.getLetzterLogin()!=null){
					calLast = this.getSqlDateByJavaGregorianCalendar(benutzerKonto.getLetzterLogin());
					pstmt.setDate(i++, java.sql.Date.valueOf(calLast));
				}
				else{
					pstmt.setNull(i++, Types.DATE);
				}
				pstmt.setBoolean(i++, benutzerKonto.isGesperrt());
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				rs.next();
				id = rs.getLong(1);
				rs.close();
				pstmt.close();
			}catch(SQLException e){
				e.printStackTrace();
				throw new DatenbankFehlerException(DatenbankFehlerException.SCHREIBEN_ERR);
			}	
			benutzerKonto.setId(id);
			return benutzerKonto;
			
		}else{
			int j= 1;
			sql = "UPDATE " +Tabellen.BENUTZERKONTO+" SET " +
				BenutzerKontoFelder.PERSON + "= ?, " + 
				BenutzerKontoFelder.LOGINNAME + "= ?, " + 
				BenutzerKontoFelder.PASSWORT + "= ?, " + 
				BenutzerKontoFelder.ROLLEACCOUNT + "= ?, " + 
				BenutzerKontoFelder.ERSTERLOGIN + "= ?, " + 
				BenutzerKontoFelder.LETZTERLOGIN + "= ?, " +
				BenutzerKontoFelder.GESPERRT + "= ? " +
						"WHERE " + BenutzerKontoFelder.ID + "= ? ";
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.setLong(j++, benutzerKonto.getBenutzer().getId());
				pstmt.setString(j++, benutzerKonto.getBenutzername());
				pstmt.setString(j++, benutzerKonto.getPasswort());
				pstmt.setString(j++, benutzerKonto.getRolle().getName());
				if(benutzerKonto.getErsterLogin()!= null){
					calFirst = this.getSqlDateByJavaGregorianCalendar(benutzerKonto.getErsterLogin());
					pstmt.setDate(j++, java.sql.Date.valueOf(calFirst));
				}
				else{
					pstmt.setNull(j++, Types.DATE);
				}
				if(benutzerKonto.getLetzterLogin()!=null){
					calLast = this.getSqlDateByJavaGregorianCalendar(benutzerKonto.getLetzterLogin());
					pstmt.setDate(j++, java.sql.Date.valueOf(calLast));
				}
				else{
					pstmt.setNull(j++, Types.DATE);
				}
				pstmt.setBoolean(j++, benutzerKonto.isGesperrt());
				pstmt.setLong(j++, benutzerKonto.getId());
				pstmt.executeUpdate();
				pstmt.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatenbankFehlerException(DatenbankFehlerException.SCHREIBEN_ERR);
			}	
		}
		try{
			this.closeConnection(con);
		}catch(SQLException e){
			e.printStackTrace();
			throw new DatenbankFehlerException(DatenbankFehlerException.CONNECTION_ERR);
		}
		return null;
	}
	
	/**
	 * Speichert bzw. aktualisiert die übergebenen Aktivierungsdaten.
	 * @param aktivierung welche gespeichert (ohne ID) oder aktualisiert (mit ID) werden soll.
	 * @return das gespeicherte Objekt MIT ID, bzw. NULL falls ein Update durchgeführt wurde.
	 * @throws DatenbankFehlerException wirft Datenbankfehler bei Verbindungs- oder Schreibfehlern.
	 */
	private AktivierungBean schreibenAktivierung(AktivierungBean aktivierung) throws DatenbankFehlerException{
		Connection con = null;
		String sql = "";
		String cal = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = this.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatenbankFehlerException(DatenbankFehlerException.CONNECTION_ERR);
		}
		if(aktivierung.getAktivierungsId() == NullKonstanten.NULL_LONG)
		{
			int i = 1;
			long id = Long.MIN_VALUE;
			sql = "INSERT INTO " + Tabellen.AKTIVIERUNG + " (" +
			AktivierungsFelder.ID + ", " +
			AktivierungsFelder.BENUTZER + ", " +
			AktivierungsFelder.LINK + ", " +
			AktivierungsFelder.VERSANDDATUM + ") " +
			" VALUES (NULL,?,?,?)";
			try {
				pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pstmt.setLong(i++, aktivierung.getBenutzerkonto().getId());
				pstmt.setString(i++, aktivierung.getAktivierungsLink());
				cal = this.getSqlDateByJavaGregorianCalendar(aktivierung.getVersanddatum());
				pstmt.setDate(i++, java.sql.Date.valueOf(cal));
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				rs.next();
				id = rs.getLong(1);
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatenbankFehlerException(DatenbankFehlerException.SCHREIBEN_ERR);
			}
			aktivierung.setAktivierungsId(id);
			return aktivierung;
		}
		else{
			int j = 1;
			sql = "UPDATE " + Tabellen.AKTIVIERUNG + " SET "+
				AktivierungsFelder.BENUTZER + "=? , " + 
				AktivierungsFelder.LINK + "=? , " + 
				AktivierungsFelder.VERSANDDATUM + "=?  " +
						"WHERE "+ AktivierungsFelder.ID + "=?";
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.setLong(j++, aktivierung.getBenutzerkonto().getId());
				pstmt.setString(j++, aktivierung.getAktivierungsLink());
				cal = this.getSqlDateByJavaGregorianCalendar(aktivierung.getVersanddatum());
				pstmt.setDate(j++, java.sql.Date.valueOf(cal));
				pstmt.setLong(j++, aktivierung.getAktivierungsId());
				pstmt.executeUpdate();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatenbankFehlerException(DatenbankFehlerException.SCHREIBEN_ERR);
			}
		}
		
		try {
			this.closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatenbankFehlerException(DatenbankFehlerException.CONNECTION_ERR);
		}
		
		return null;
	}
	
	/**
	 * Speichert bzw. aktualisiert die übergebenen Studiendaten.
	 * @param studie welche gespeichert (ohne ID) oder aktualisiert (mit ID) werden soll.
	 * @return das gespeicherte Objekt MIT ID, bzw das Objekt mit den aktualisierten Daten.
	 * @throws DatenbankFehlerException 
	 */
	private StudieBean schreibenStudie(StudieBean studie) throws DatenbankFehlerException{
		Connection con = null;
		String sql = "";
		String calStart = "";
		String calEnde = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Blob protokoll = null;
		try {
			con = this.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatenbankFehlerException(DatenbankFehlerException.CONNECTION_ERR);
		}
		if(studie.getId() == NullKonstanten.NULL_LONG){
			int i = 1;
			long id = Long.MIN_VALUE;
			try {
				sql = "INSERT INTO " + Tabellen.STUDIE + " (" + 
					StudieFelder.ID + ", " + 
					StudieFelder.BENUTZER + ", " +
					StudieFelder.NAME + ", " +
					StudieFelder.BESCHREIBUNG + ", " +
					StudieFelder.STARTDATUM + ", " +
					StudieFelder.ENDDATUM + ", " +
					StudieFelder.PROTOKOLL + ", " +
					StudieFelder.RANDOMISATIONSART + ", " +
					StudieFelder.STATUS + ") " +
					"VALUES (?,?,?,?,?,?,?,?,?)";
				pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pstmt.setLong(i++, studie.getBenutzerkonto().getId());
				pstmt.setString(i++, studie.getName());
				if(studie.getBeschreibung()!=""){
					pstmt.setString(i++, studie.getBeschreibung());
				}
				else{
					//FIXME Es gibt als Typ kein TEXT
					pstmt.setNull(i++, Types.NULL);
				}
				calStart = this.getSqlDateByJavaGregorianCalendar(studie.getStartDatum());
				pstmt.setDate(i++, java.sql.Date.valueOf(calStart));
				calEnde = this.getSqlDateByJavaGregorianCalendar(studie.getEndDatum());
				pstmt.setDate(i++, java.sql.Date.valueOf(calEnde));
				//TODO BLOB/InputStrean Handling!!!
				pstmt.setBlob(i++, protokoll);
				pstmt.setString(i++, studie.getRandomisationseigenschaften().toString());
				pstmt.setInt(i++, studie.getStatus());
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				rs.next();
				id = rs.getLong(1);
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatenbankFehlerException(DatenbankFehlerException.SCHREIBEN_ERR);
			}
			studie.setId(id);
			return studie;
		}
		else{
			int j = 1;
			sql = "UPDATE "+ Tabellen.STUDIE + " SET " +
				StudieFelder.BENUTZER + "=?, " +
				StudieFelder.NAME + "=?, " +
				StudieFelder.BESCHREIBUNG + "=?, " +
				StudieFelder.STARTDATUM + "=?, " +
				StudieFelder.ENDDATUM + "=?, " +
				StudieFelder.PROTOKOLL + "=?, " +
				StudieFelder.RANDOMISATIONSART + "=?, " +
				StudieFelder.STATUS + "=? " +
				"WHERE " + StudieFelder.ID + "=?";
			try {
				pstmt = con.prepareStatement(sql);
			pstmt.setLong(j++, studie.getBenutzerkonto().getId());
			pstmt.setString(j++, studie.getName());
			if(studie.getBeschreibung()!=""){
				pstmt.setString(j++, studie.getBeschreibung());
			}
			else{
				//FIXME Es gibt als Typ kein TEXT
				pstmt.setNull(j++, Types.NULL);
			}
			calStart = this.getSqlDateByJavaGregorianCalendar(studie.getStartDatum());
			pstmt.setDate(j++, java.sql.Date.valueOf(calStart));
			calEnde = this.getSqlDateByJavaGregorianCalendar(studie.getEndDatum());
			pstmt.setDate(j++, java.sql.Date.valueOf(calEnde));
			//TODO BLOB/InputStrean Handling!!!
			pstmt.setBlob(j++, protokoll);
			pstmt.setString(j++, studie.getRandomisationseigenschaften().toString());
			pstmt.setInt(j++, studie.getStatus());
			pstmt.executeUpdate();
			pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DatenbankFehlerException(DatenbankFehlerException.SCHREIBEN_ERR);
			}
		}
		try {
			this.closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatenbankFehlerException(DatenbankFehlerException.CONNECTION_ERR);
		}
		return null;
	}
	
	/**
     * Konvertiert ein Objekt vom Typ GregorianCalendar in einen String im SQL-Date-Format
     * @param calendar ist das Objekt als GregorianCalendar
     * @return ein String im SQL-Date-Format
     */
    private String getSqlDateByJavaGregorianCalendar(GregorianCalendar calendar)
    {
        String cal;
        cal = calendar.get(java.util.Calendar.YEAR) + "-" +
              calendar.get(java.util.Calendar.MONTH) + "-" +
              calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return cal;
    }

	/**
	 * Dokumentation siehe Schnittstellenbeschreibung
	 * @see de.randi2.datenbank.DatenbankSchnittstelle#suchenObjekt(de.randi2.datenbank.Filter)
	 */
	public <T extends Filter> Vector<T> suchenObjekt(T zuSuchendesObjekt) throws DatenbankFehlerException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Dokumentation siehe Schnittstellenbeschreibung
	 * @see de.randi2.datenbank.DatenbankSchnittstelle#suchenObjektID(long, de.randi2.datenbank.Filter)
	 */
	public <T extends Filter> T suchenObjektID(long id, T nullObjekt) throws DatenbankFehlerException {
		if (nullObjekt instanceof PersonBean) {
			PersonBean person = suchenPersonID(id);
			return (T) person;
		}
		return null;
	}
	
	/**
	 * Sucht in der Datenbank nach der Person mit der uebergebenen ID
	 * @param id
	 * 			zu suchende ID
	 * @return
	 * 			Person mit zutreffender ID, null falls keine Person mit entsprechender ID gefunden wurde
	 * @throws DatenbankFehlerException 
	 */
	private PersonBean suchenPersonID(long id) throws DatenbankFehlerException {
		Connection con=null;
		PreparedStatement pstmt;
		ResultSet rs = null;
		PersonBean tmpPerson=null;
		try {
			con = getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql;
		sql = "SELECT * FROM "+Tabellen.PERSON+" WHERE "+FelderPerson.ID+" = ?";		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, id);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				char[] tmp = rs.getString(FelderPerson.GESCHLECHT.toString()).toCharArray();
				try {
					tmpPerson = new PersonBean(rs.getLong(FelderPerson.ID.toString()), rs.getString(FelderPerson.NACHNAME.toString()),rs.getString(FelderPerson.VORNAME.toString())
							, Titel.parseTitel(rs.getString(FelderPerson.TITEL.toString())) , tmp[0],rs.getString(FelderPerson.EMAIL.toString()),
							rs.getString(FelderPerson.TELEFONNUMMER.toString()),rs.getString(FelderPerson.HANDYNUMMER.toString()),rs.getString(FelderPerson.FAX.toString()));
				} catch (PersonException e) {					
					e.printStackTrace();
					throw new DatenbankFehlerException(DatenbankFehlerException.UNGUELTIGE_DATEN);
					//sollte hier lieber die Person Exception weitergeleitet werden? wie sieht es mit logging aus?
				}
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		try {
			closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmpPerson;
		
	}
	
	/**
	 * Dokumentation siehe Schnittstellenbeschreibung
	 * @see de.randi2.datenbank.DatenbankSchnittstelle#suchenMitgliederObjekte(de.randi2.datenbank.Filter, de.randi2.datenbank.Filter)
	 */
	public <T extends Filter, U extends Filter> Vector<T> suchenMitgliederObjekte(U vater, T kind) throws DatenbankFehlerException {
		if (vater instanceof ZentrumBean && kind instanceof PersonBean) {
			ZentrumBean zentrum = (ZentrumBean) vater;
			PersonBean ansprechpartner = suchenAnsprechpartner(zentrum.getAnsprechpartnerId());
			Vector<T> personVec = new Vector();
			personVec.add((T)ansprechpartner);
			return personVec;
		}
		return null;
	}
	
	
	/**
	 * Methode sucht den Ansprechpartner eines Zentrums
	 * @param id
	 * 			Ansprechpartner des Ansprechpartners, welche im ZentrumBean der aufrufenden Methode gespeichert st
	 * @return
	 * 			Ansprechpartner
	 * @throws DatenbankFehlerException
	 * 			Falls ein DB Fehler auftritt
	 */
	private PersonBean suchenAnsprechpartner(long id) throws DatenbankFehlerException {
		return suchenObjektID(id, new PersonBean()); 
	}
	
	//TODO main methode spaeter rausschmeissen
	/**
	 * Nur Testfunktionalitaet
	 * @param args
	 */
	public static void main(String[] args) {
		Datenbank db = new Datenbank();
		Connection con = null;
		try {
			con = db.getConnection();
		} catch (SQLException e) {		
			e.printStackTrace();
		}
		if (con==null) {
			System.out.println("keine Verbindung vorhanden");
		}
		else {
			System.out.println("Verbindung aufgebaut");
		}
		String query = "SELECT * FROM patient";
		Statement stmt;
		ResultSet rs=null;
		try {
			stmt = con.createStatement();
			rs =  stmt.executeQuery(query);
		} catch (SQLException e) {			
			e.printStackTrace();
		}
		
		try {
			db.closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("ENUM TEST ");
		System.out.println("FelderPerson.Nachname: "+FelderPerson.NACHNAME);
		System.out.println("FelderPerson.Nachname.toString(): "+FelderPerson.NACHNAME.toString());
		
	}
	
	/**
     * Baut Verbindung zur Datenbank auf
     * @return
     * 			Connectionobjekt welches Zugriff auf die Datenbank ermoeglicht.
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
    	Connection con = DriverManager.getConnection("proxool.randi2");    	
    	return con;
    }
    //"jdbc:mysql://"+Config.getProperty(Config.Felder.RELEASE_DB_HOST)+":"+Config.getProperty(Config.Felder.RELEASE_DB_PORT)+"/randi2",Config.getProperty(Config.Felder.RELEASE_DB_NUTZERNAME),Config.getProperty(Config.Felder.RELEASE_DB_PASSWORT
    
    /**
     * Trennt Verbindung zur Datenbank.
     * @throws SQLException 
     * @throws DBExceptions
     */
    private void closeConnection(Connection con) throws SQLException {
        	if(con!=null && !con.isClosed()) {
        		con.close();
        	}            
    }

		

}
