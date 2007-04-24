package de.randi2.model.fachklassen.beans;

import java.util.HashMap;

import de.randi2.utility.CollectionUtil;
import de.randi2.utility.NullKonstanten;

/**
 * Die Klasse StrataBean kapselt die Eigenschaft eines Stratas. Sie kann - wenn
 * benoetigt - auch die aktuelle Auspraegung eines Stratas repraentieren.
 * 
 * @author Johannes Thoenes [jthoenes@stud.hs-heilbronn.de]
 * @author Daniel Haehn [dhaehn@stud.hs-heilbronn.de]
 * @version $Id$
 * 
 */
public class StrataBean {

	/**
	 * Die Datenbank-Id des Strata-Typs.
	 */
	long id = NullKonstanten.NULL_LONG;
	
	/**
	 * Die moeglichen Auspraegungen eines Stratas als HashMap. Der Key-Parameter
	 * stellt dabei die id des Stratas auf der Datenbank als {@link Long} dar.
	 * Der value-Paramater den geschriebenen Wert des Stratas als {@link String}.
	 */
	private HashMap<Long, String> moeglicheAuspraegungen = null;

	/**
	 * Die id des Stratas als long, falls ein aktuelles Strata gewaehlt ist.
	 */
	private long aAuspraegungId = NullKonstanten.NULL_LONG;

	/**
	 * Default-Konstruktor. Erzeugt ein Null-Objekt.
	 * 
	 */
	public StrataBean() {
	}

	/**
	 * Erzeugt ein StrataBean, ohne eine aktuelle Auspraegung mit anzugeben.
	 * 
	 * @param moeglicheAuspraegungen
	 *            Die moeglichen Auspraegungen eines Stratas als HashMap. Der
	 *            Key-Parameter stellt dabei die id des Stratas auf der
	 *            Datenbank als {@link Long} dar. Der value-Paramater den
	 *            geschriebenen Wert des Stratas als {@link String}.
	 */
	public StrataBean(long id, HashMap<Long, String> moeglicheAuspraegungen) {
		this.id = id;
		this.moeglicheAuspraegungen = moeglicheAuspraegungen;
	}

	/**
	 * ERzeugt ein Strata, mit einer aktuellen Auspraegung.
	 * 
	 * @param moeglicheAuspraegungen
	 *            Die moeglichen Auspraegungen eines Stratas als HashMap. Der
	 *            Key-Parameter stellt dabei die id des Stratas auf der
	 *            Datenbank als {@link Long} dar. Der value-Paramater den
	 *            geschriebenen Wert des Stratas als {@link String}.
	 * @param aAuspragungId
	 *            Die id des Stratas als long.
	 */
	public StrataBean(long id, HashMap<Long, String> moeglicheAuspraegungen,
			long aAuspragungId) {
		this(id, moeglicheAuspraegungen);
		this.aAuspraegungId = aAuspragungId;
	}

	/**
	 * Gibt die aktuelle Auspraegung des Stratas zurueck.
	 * 
	 * @return Die id des Stratas als long, falls ein aktuelles Strata gewaehlt
	 *         ist.
	 */
	public long getAuspraegungId() {
		return aAuspraegungId;
	}

	/**
	 * Setzt die aktuelle Auspraegungs-Id.
	 * 
	 * @param auspraegungId
	 *            Die id des Stratas als long.
	 */
	public void setAuspraegungId(long auspraegungId) {
		aAuspraegungId = auspraegungId;
	}

	/**
	 * Setzt die aktuelle Auspraegungs-Id.
	 * 
	 * @param auspraegungId
	 *            Das aktuelle Strata.
	 */
	public void setAuspraegungId(String auspraegung) {
		Long aAuspraegungIdObjekt = CollectionUtil.getKeyFromHashMap(
				moeglicheAuspraegungen, auspraegung);
		if (aAuspraegungIdObjekt == null) {
			// TODO Fehler!!!
		} else {
			this.aAuspraegungId = aAuspraegungIdObjekt.longValue();
		}
	}

	/**
	 * Gibt die moeglichen Strata zurueck.
	 * 
	 * @return Die moeglichen Auspraegungen eines Stratas als HashMap. Der
	 *         Key-Parameter stellt dabei die id des Stratas auf der Datenbank
	 *         als {@link Long} dar. Der value-Paramater den geschriebenen Wert
	 *         des Stratas als {@link String}.
	 */
	public HashMap<Long, String> getMoeglicheAuspraegungen() {
		return moeglicheAuspraegungen;
	}

}