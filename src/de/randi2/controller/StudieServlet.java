package de.randi2.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.randi2.controller.DispatcherServlet.sessionParameter;
import de.randi2.datenbank.DatenbankFactory;
import de.randi2.datenbank.exceptions.DatenbankExceptions;
import de.randi2.model.exceptions.PatientException;
import de.randi2.model.exceptions.StrataException;
import de.randi2.model.exceptions.StudieException;
import de.randi2.model.exceptions.StudienarmException;
import de.randi2.model.fachklassen.Patient;
import de.randi2.model.fachklassen.Rolle;
import de.randi2.model.fachklassen.Strata;
import de.randi2.model.fachklassen.Studie;
import de.randi2.model.fachklassen.Studienarm;
import de.randi2.model.fachklassen.Zentrum;
import de.randi2.model.fachklassen.beans.BenutzerkontoBean;
import de.randi2.model.fachklassen.beans.PatientBean;

import de.randi2.model.fachklassen.beans.StrataAuspraegungBean;
import de.randi2.model.fachklassen.beans.StrataBean;

import de.randi2.model.fachklassen.beans.StudieBean;
import de.randi2.model.fachklassen.beans.StudienarmBean;
import de.randi2.model.fachklassen.beans.ZentrumBean;
import de.randi2.randomisation.BlockRandomisation;
import de.randi2.randomisation.Randomisation;
import de.randi2.randomisation.StrataBlockRandomisation;
import de.randi2.randomisation.VollstaendigeRandomisation;
import de.randi2.randomisation.Randomisation.Algorithmen;
import de.randi2.utility.Jsp;
import de.randi2.utility.NullKonstanten;
import de.randi2.utility.Parameter;

/**
 * Diese Klasse repraesentiert das STUDIESERVLET, welches Aktionen an die
 * Studie-Fachklasse, die JSPs und an den DISPATCHER weiterleitet.
 * 
 * @version $Id$
 * @author Daniel Haehn [dhaehn@stud.hs-heilbronn.de]
 * 
 */
public class StudieServlet extends javax.servlet.http.HttpServlet {

	/**
	 * Serial Version
	 */
	private static final long serialVersionUID = 7L;

	/**
	 * Meldung, wenn der Aenderungsvorgang einer Studie erfolgreich war.
	 */
	private static final String AENDERUNG_STUDIE_ERFOLGREICH = "Die Studie wurde erfolgreich geändert.";

	/**
	 * Meldung, wenn der Status erfolgreich geaendert wurde.
	 */
	public static final String STATUS_GEAENDERT = "Status der Studie wurde erfolgreich geaendert!";
	
	/**
	 * Meldung, wenn ein Studienarm erfolgreicht beendet wurde.
	 */
	public static final String STUDIENARM_BEENDET = "Der ausgewählte Studienarm wurde beenden!";

	/**
	 * Die Anfrage_id zur Verwendung im Studie Servlet
	 */
	public enum anfrage_id {

		/**
		 * Eine Studie soll vom Benutzer ausgewaehlt werden.
		 */
		AKTION_STUDIE_AUSWAEHLEN,

		/**
		 * "Anlegen einer neuen Studie" auswaehlen
		 */
		AKTION_STUDIE_AUSWAEHLEN_NEUESTUDIE,

		/**
		 * Filtern der Studien.
		 */
		JSP_STUDIE_AUSWAEHLEN_FILTERN,

		/**
		 * Die Studie wurde auf der Seite studie_auswaehlen.jsp ausgewaehlt
		 */
		JSP_STUDIE_AUSWAEHLEN,

		/**
		 * Wenn eine Studie ausgewaehlt wurde und angezeigt werden soll
		 */
		AKTION_STUDIE_AUSGEWAEHLT,

		/**
		 * Eine Studie soll geaendert werden.
		 */
		JSP_STATUS_AENDERN,
		/**
		 * Status der Studie aendern.
		 */
		AKTION_STATUS_AENDERN,

		/**
		 * studie_aendern.jsp wurde gewaehlt.
		 */
		JSP_STUDIE_AENDERN,

		/**
		 * Anfrage "studienarm beenden" von der studienarm_anzeigen.jsp
		 */
		JSP_STUDIENARM_BEENDEN,
		
		/**
		 * Ein Studienarm soll beendet werden
		 */
		AKTION_STUDIENARM_BEENDEN,
		
		/**
		 * Studie soll fortgesetzt werden
		 */
		JSP_STUDIE_FORTSETZEN,

		/**
		 * Prozess bestaetigt
		 */
		JSP_STUDIE_FORTSETZEN_JA,
		
		/**
		 * Studie starten auf der JSP Seite
		 */
		JSP_STUDIE_STARTEN,
		
		/**
		 * Studie soll gestartet werden
		 */
		AKTION_STUDIE_STARTEN,

		/**
		 * Studie soll pausiert werde
		 */
		JSP_STUDIE_PAUSIEREN,

		/**
		 * Prozess bestaetigt
		 */
		JSP_STUDIE_PAUSIEREN_JA,
		
		/**
		 * Prozess bestaetigt
		 */
		JSP_STUDIE_STARTEN_JA,

		/**
		 * Ein ausgewählter Studienarm soll angezeigt werden.
		 */
		JSP_STUDIENARM_ANZEIGEN,

		/**
		 * Ein ausgewählter Studienarm wird angezeigt.
		 */
		AKTION_STUDIENARM_ANZEIGEN,

		/**
		 * zentrum_anzeigen.jsp
		 */
		JSP_ZENTRUM_ANZEIGEN,

		/**
		 * Eine Studie wird pausiert.
		 */
		AKTION_STUDIE_PAUSIEREN,

		/**
		 * Eine pausierte Studie wird fortgesetzt.
		 */
		AKTION_STUDIE_FORTSETZEN,

		/**
		 * Aendert einer bereits vorhandenen Studie.
		 */
		AKTION_STUDIE_AENDERN,

		/**
		 * Neue Studie hinzufuegen
		 */
		AKTION_STUDIE_ANLEGEN,

		/**
		 * Studie ansehen
		 */
		JSP_STUDIE_ANSEHEN,

		/**
		 * Patient hinzufuegen & Randomisieren
		 */
		JSP_PATIENT_HINZUFUEGEN;
	}

	/**
	 * Enhaelt die Parameternamen, die in dem Request gesetzt werden koennen
	 * 
	 */
	public static enum requestParameter {
		/**
		 * Liste der im System gespeicherten Zentren
		 */
		LISTE_DER_ZENTREN("listeZentren"),
		/**
		 * Liste der gefundenen Studien
		 */
		LISTE_DER_STUDIEN("listeStudien"),
		/**
		 * Zur Studie gehoerige Zentren
		 */
		ZUGHOERIGE_ZENTREN("zugehoerigeZentren"),
		/**
		 * Nicht zur Studie gehoerige Zentren
		 */
		NICHT_ZUGEHOERIGE_ZENTREN("nichtZugehoerigeZentren"),
		/**
		 * Ergebnis der Filterung von Zentren
		 */
		GEFILTERTE_ZENTREN("listeZentren"),

		/**
		 * Aktueller Studienarm
		 */
		AKTUELLER_STUDIENARM("aStudienarm");

		/**
		 * String Version des Parameters
		 */
		private String parameter = null;

		/**
		 * Setzt die String Repraesentation des Parameters
		 * 
		 * @param parameter
		 *            String Repraesentation des Parameters
		 */
		private requestParameter(String parameter) {
			this.parameter = parameter;
		}

		/**
		 * Liefert die String Repraesentation des Parameters
		 * 
		 * @return String Repraesentation des Parameters
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return this.parameter;

		}
	}

	/**
	 * Konstruktor.
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public StudieServlet() {
		super();
	}

	/**
	 * Diese Methode nimmt HTTP-GET-Request gemaess HTTP-Servlet Definition
	 * entgegen. Die Anfragen werden direkt an doPost() weitergeleitet.
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);

	}

	/**
	 * Diese Methode nimmt HTTP-POST-Request gemaess HTTP-Servlet Definition
	 * entgegen. Hier werden Anfragen verarbeitet, die Zentren betreffen.
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String id = (String) request.getParameter(Parameter.anfrage_id);
		// idAttribute nicht entfernen, benutzen dies fuer die Weiterleitung aus
		// dem Benutzerservlet --Btheel
		String idAttribute = (String) request
				.getAttribute(Parameter.anfrage_id);

		if (idAttribute != null) {
			id = idAttribute;
			Logger.getLogger(this.getClass()).debug(id);

		} else if (id != null) {
			Logger.getLogger(this.getClass()).debug(id);
			if (id.equals(anfrage_id.JSP_STUDIE_AENDERN.name())) {
				// studieAendern.jsp soll angezeigt werden
				studieAendern(request, response);
			}

		} else {
			// TODO an dieser Stelle würde ich einfach auf index.jsp
			// weiterleiten; gibt's andere Vorschläge (lplotni 17. Jun)
			// request.getRequestDispatcher("DispatcherServlet").forward(request,
			// response);
			System.out.println("Die drei Fragezeichen beim Posten");

		}

		if (id.equals(anfrage_id.AKTION_STUDIE_AUSWAEHLEN.toString())) {
			// Die studie_auswaehlen.jsp soll angezeigt werden.
			studieAuswaehlen(request, response);
		} else if (id.equals(StudieServlet.anfrage_id.JSP_PATIENT_HINZUFUEGEN
				.name())) {
			this.patientHinzufuegen(request, response);
		} else if (id.equals(anfrage_id.AKTION_STUDIE_AUSGEWAEHLT.toString())) {
			// Benutzer hat eine Studie ausgewaehlt
			try {
				// Erstmal alle vorhandenen Studien suchen
				StudieBean aStudie = new StudieBean();
				aStudie.setFilter(true);
				Iterator<StudieBean> iterator = Studie.sucheStudie(aStudie)
						.iterator();
				while (iterator.hasNext()) {
					aStudie = iterator.next();
					String suche = "aStudieId" + aStudie.getId();
					if (request.getParameter(suche) != null) {
						// Ausgewaehlte Studie wird an die Session gebunden
						Logger.getLogger(this.getClass()).debug(
								"Binde Studie an Session");
						request
								.getSession()
								.setAttribute(
										DispatcherServlet.sessionParameter.AKTUELLE_STUDIE
												.toString(), aStudie);
						request.getRequestDispatcher(Jsp.STUDIE_ANSEHEN)
								.forward(request, response);
						break;
					}
				}
			} catch (DatenbankExceptions e) {
				// Fehler zurück!
				request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
						.getMessage());
				request.getRequestDispatcher("/studie_auswaehlen.jsp").forward(
						request, response);
			}
		} else if (id.equals(anfrage_id.AKTION_STUDIE_AUSWAEHLEN_NEUESTUDIE
				.name())) {
			// Formular fuer neue Studie aufrufen
			request.getRequestDispatcher(Jsp.STUDIE_ANLEGEN).forward(request,
					response);
		} else if (id.equals(anfrage_id.AKTION_STUDIE_ANLEGEN.name())) {
			// Neue Studie anlegen
			// StudieBean aStudie = new
			// StudieBean(NullKonstanten.DUMMY_ID,);

			this.classDispatcherservletStudieAnlegen(request, response);

			return;

		} else if (id.equals(anfrage_id.AKTION_STUDIE_AENDERN.name())) {
			// Studie soll geaendert werden
			request.getRequestDispatcher(Jsp.STUDIE_AENDERN).forward(request,
					response);
		} else if (id.equals(anfrage_id.AKTION_STATUS_AENDERN.name())) {
			// Status aendern
			request.getRequestDispatcher(Jsp.STUDIE_PAUSIEREN).forward(request,
					response);

		} else if (id.equals(anfrage_id.AKTION_STUDIE_FORTSETZEN.name())) {
			// Status aendern
			studieStatus(request, response, Studie.Status.AKTIV);
		} else if (id.equals(anfrage_id.AKTION_STUDIE_PAUSIEREN.name())) {
			// Status aendern
			studieStatus(request, response, Studie.Status.PAUSE);
		} else if (id.equals(anfrage_id.AKTION_STUDIE_STARTEN.name())) {
			// Status aendern
			studieStatus(request, response, Studie.Status.AKTIV);
		} else if (id.equals(anfrage_id.JSP_STUDIE_ANSEHEN.name())) {
			// Status aendern
			request.getRequestDispatcher(Jsp.STUDIE_ANSEHEN).forward(request,
					response);
		} else if (id.equals(anfrage_id.JSP_ZENTRUM_ANZEIGEN.name())) {
			request.setAttribute("zugehoerigeZentren", this
					.getZugehoerigeZentren(request, response));
			request.setAttribute("nichtZugehoerigeZentren", this
					.getNichtZugehoerigeZentren(request, response));
			if (((String) request.getParameter("Filtern")) != null) {
				request.getRequestDispatcher("ZentrumServlet").forward(request,
						response);

			} else {
				request.getRequestDispatcher(Jsp.ZENTRUM_ANZEIGEN).forward(
						request, response);
			}

		} else if (id.equals(anfrage_id.AKTION_STUDIENARM_ANZEIGEN.toString())) {
			// Dem Benutzer wird der ausgewählte Studienarm der Studie
			// angezeigt.
			StudienarmBean aStudienarm = (StudienarmBean) Studienarm
					.getStudienarm(Long.parseLong(request
							.getParameter(Parameter.studienarm.ID.toString())));
			request.setAttribute(requestParameter.AKTUELLER_STUDIENARM
					.toString(), aStudienarm);
			request.getRequestDispatcher(Jsp.STUDIENARM_ANZEIGEN).forward(
					request, response);
		}else if(id.equals(anfrage_id.AKTION_STUDIENARM_BEENDEN.toString())){
			//Ein Arm der Studie wird beendet
			StudienarmBean aStudienarm = (StudienarmBean) Studienarm.getStudienarm( Long.parseLong(request.getParameter(Parameter.studienarm.ID.toString())));
			try {
				aStudienarm.setStatus(Studie.Status.BEENDET);
				aStudienarm.setBenutzerkontoLogging((BenutzerkontoBean) request.getSession().getAttribute(DispatcherServlet.sessionParameter.A_Benutzer.toString()));
				DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(aStudienarm);
				request.setAttribute(DispatcherServlet.NACHRICHT_OK,
						STUDIENARM_BEENDET);
				request.getRequestDispatcher(Jsp.STUDIE_ANSEHEN).forward(request,
						response);
			} catch (StudienarmException e) {
				e.printStackTrace();
				request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
						.getMessage());
				request.getRequestDispatcher(Jsp.STUDIE_ANSEHEN).forward(request,
						response);
			}
			
		}
	}

	/**
	 * Diese Methode kuemmert sich um die Logik, die mit dem Prozess "Studie
	 * auswaehlen" verbunden ist. Je nach der Rolle des eingeloggten Benutzers,
	 * werden entsprechende Studien geholt.
	 * 
	 * @param request
	 *            {@link HttpServlet}
	 * @param response
	 *            {@link HttpServlet}
	 * 
	 * @throws IOException
	 *             {@link HttpServlet}
	 * @throws ServletException
	 *             {@link HttpServlet}
	 */
	private void studieAuswaehlen(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		BenutzerkontoBean aBenutzer = (BenutzerkontoBean) request.getSession()
				.getAttribute(
						DispatcherServlet.sessionParameter.A_Benutzer
								.toString());
		Rolle aRolle = aBenutzer.getRolle();

		StudieBean leereStudie = new StudieBean();
		leereStudie.setFilter(true);

		Vector<StudieBean> listeStudien = null;

		if (aRolle == Rolle.getStudienarzt()) {
			// der eingeloggte Benutzer ist ein Studienarzt
			Logger.getLogger(this.getClass()).debug(
					"studieAuswahl - Studienarzt");

			if (request.getParameter(Parameter.filter) != null) {
				try {
					listeStudien = studieFiltern(request
							.getParameter(Parameter.studie.NAME.toString()),
							Studie.Status.parseStatus(request
									.getParameter(Parameter.studie.STATUS
											.toString())), aBenutzer
									.getZentrum());
				} catch (StudieException e) {
					// TODO Dem Benutzer muss eine Meldung angezeigt werden
					// (lplotni
					// 17. Jun)
					e.printStackTrace();
				}
			} else {
				listeStudien = DatenbankFactory.getAktuelleDBInstanz()
						.suchenMitgliederObjekte(aBenutzer.getZentrum(),
								leereStudie);
			}
			request.setAttribute(requestParameter.LISTE_DER_STUDIEN.toString(),
					listeStudien);
			request.getRequestDispatcher(Jsp.STUDIE_AUSWAEHLEN).forward(
					request, response);
		} else if (aRolle == Rolle.getStatistiker()) {
			// der eingeloggte Benutzer ist ein Statistiker - hier muss keine
			// Auswahl erfolgen; die Studie, die er ansehen darf, wird aus der
			// DB geholt und an die Session gebunden; studie_ansehen.jsp wird
			// automatisch aufgerufen.
			Logger.getLogger(this.getClass()).debug(
					"studieAuswahl - Statistiker");
			try {
				leereStudie.setBenutzerkonto(aBenutzer);
			} catch (StudieException e) {
				// TODO Dem Benutzer muss eine Meldung angezeigt werden (lplotni
				// 17. Jun)
				e.printStackTrace();
			}
			listeStudien = Studie.sucheStudie(leereStudie);
			StudieBean aStudie = listeStudien.firstElement();
			request.getSession().setAttribute(
					sessionParameter.AKTUELLE_STUDIE.toString(), aStudie);
			request.getRequestDispatcher(Jsp.STUDIE_ANSEHEN).forward(request,
					response);
		} else if (aRolle == Rolle.getStudienleiter()) {
			// der eingeloggte Benutzer ist ein Studienleiter
			Logger.getLogger(this.getClass()).debug(
					"studieAuswahl - Studienleiter");
			if (request.getParameter(Parameter.filter) != null) {
				try {
					listeStudien = studieFiltern(request
							.getParameter(Parameter.studie.NAME.toString()),
							Studie.Status.parseStatus(request
									.getParameter(Parameter.studie.STATUS
											.toString())), aBenutzer
									.getZentrum());
				} catch (StudieException e) {
					// TODO Dem Benutzer muss eine Meldung angezeigt werden
					// (lplotni
					// 17. Jun)
					e.printStackTrace();
				}
			} else {
				try {
					leereStudie.setBenutzerkonto(aBenutzer);
				} catch (StudieException e) {
					// TODO Dem Benutzer muss eine Meldung angezeigt werden
					// (lplotni
					// 17. Jun)
					e.printStackTrace();
				}
				listeStudien = Studie.sucheStudie(leereStudie);
			}

			request.setAttribute(requestParameter.LISTE_DER_STUDIEN.toString(),
					listeStudien);
			request.getRequestDispatcher(Jsp.STUDIE_AUSWAEHLEN).forward(
					request, response);

		}
	}

	/**
	 * Diese Methode filtert gemaess den Kriterien die Liste der Studien.
	 * 
	 * @param name -
	 *            Name der gesuchten Studie
	 * @param status -
	 *            Status der gesuchten Studie
	 * @param aZentrum -
	 *            das ZentrumBean von dem eingeloggten Benutzer
	 * @return - Vector mit den gefundenen StudienBeans
	 * @throws DatenbankExceptions
	 *             wenn bei dem Vorgang Fehler in der DB auftraten
	 */
	private Vector<StudieBean> studieFiltern(String name, Studie.Status status,
			ZentrumBean aZentrum) throws DatenbankExceptions {

		Logger.getLogger(this.getClass()).debug("studieFiltern");

		StudieBean gStudie = new StudieBean();
		gStudie.setFilter(true);

		try {
			if (name != null) {
				gStudie.setName(name);
			}
			gStudie.setStatus(status);
			return DatenbankFactory.getAktuelleDBInstanz()
					.suchenMitgliederObjekte(aZentrum, gStudie);

		} catch (StudieException e) {
			// TODO Dem Benutzer muss eine Meldung angezeigt werden (lplotni
			// 17. Jun)
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Diese Methode nimmt HTTP-POST-Request gemaess HTTP-Servlet Definition
	 * entgegen. Hier werden Anfragen verarbeitet, die Zentren betreffen.
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @param status
	 *            Status der Studie, der geaendert wird.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 */
	private void studieStatus(HttpServletRequest request,
			HttpServletResponse response, Studie.Status status)
			throws ServletException, IOException {

		StudieBean aStudie = (StudieBean) request.getSession().getAttribute(
				DispatcherServlet.sessionParameter.AKTUELLE_STUDIE.toString());

		try {
			aStudie.setStatus(status);
			aStudie.setBenutzerkontoLogging((BenutzerkontoBean) request
					.getSession().getAttribute(
							DispatcherServlet.sessionParameter.A_Benutzer
									.toString()));
			aStudie = DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(aStudie);
			//Ersetze die alte Studie, die an die Session gebunden ist.
			request.getSession().setAttribute(DispatcherServlet.sessionParameter.AKTUELLE_STUDIE.toString(), aStudie);
			request.setAttribute(DispatcherServlet.NACHRICHT_OK,
					STATUS_GEAENDERT);
			request.getRequestDispatcher(Jsp.STUDIE_ANSEHEN).forward(request,
					response);
		} catch (StudieException e) {
			e.printStackTrace();
			request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
					.getMessage());
			request.getRequestDispatcher(Jsp.STUDIE_ANSEHEN).forward(request,
					response);
		}

	}

	/**
	 * Realisiert das anlegen einer neuen Studie
	 * 
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	private void classDispatcherservletStudieAnlegen(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
		
		String aName = request.getParameter(Parameter.studie.NAME.name());
		String aBeschreibung = request
				.getParameter(Parameter.studie.BESCHREIBUNG.name());
		String aStartdatum = request.getParameter(Parameter.studie.STARTDATUM
				.name());
		String aEnddatum = request.getParameter(Parameter.studie.ENDDATUM
				.name());


		     SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy",Locale.GERMANY);

		     sdf.setCalendar(Calendar.getInstance());

		     Date d_aStartdatum_gc = sdf.parse(aStartdatum);

		     GregorianCalendar aStartdatum_gc = new GregorianCalendar();
		     aStartdatum_gc.setTime(d_aStartdatum_gc);
		     
		     Date d_aEnddatum_gc = sdf.parse(aEnddatum);

		     GregorianCalendar aEnddatum_gc = new GregorianCalendar();
		     aEnddatum_gc.setTime(d_aEnddatum_gc);
		     


		
		
		String aProtokoll = request
				.getParameter(Parameter.studie.STUDIENPROTOKOLL.name());
		BenutzerkontoBean aStudienleiter = ((BenutzerkontoBean) request
				.getSession().getAttribute(
						DispatcherServlet.sessionParameter.A_Benutzer
								.toString()));
		String aStatistikerAnlegen = request
				.getParameter(Parameter.studie.STATISTIKER_BOOL.name());
		int aAnzahl_Arme = (Integer.parseInt(request
				.getParameter(DispatcherServlet.requestParameter.ANZAHL_ARME
						.toString())));
		int aAnzahl_Strata = (Integer.parseInt(request
				.getParameter(DispatcherServlet.requestParameter.ANZAHL_STRATA
						.toString())));
		request.setAttribute(DispatcherServlet.requestParameter.ANZAHL_STRATA
						.toString(), aAnzahl_Strata);
		request.setAttribute(DispatcherServlet.requestParameter.ANZAHL_ARME
				.toString(), aAnzahl_Arme);
		Randomisation.Algorithmen aAlgorithmus = Randomisation.Algorithmen.parseAlgorithmen(request
				.getParameter(Parameter.studie.RANDOMISATIONSALGORITHMUS.name()));
		
		String aBlockgroesse_s = request
		.getParameter(Parameter.studie.BLOCKGROESSE.name());
		int aBlockgroesse = NullKonstanten.NULL_INT;
		
		if (aBlockgroesse_s != null) {
		
			aBlockgroesse = (Integer.parseInt(aBlockgroesse_s));
			
		}
			

		BenutzerkontoBean aBenutzer = (BenutzerkontoBean)request.getSession().getAttribute(DispatcherServlet.sessionParameter.A_Benutzer.toString());
		
		StudieBean aStudie = new StudieBean(NullKonstanten.DUMMY_ID,aBeschreibung,aName,aAlgorithmus,aBenutzer.getId(),aStartdatum_gc,aEnddatum_gc,aProtokoll,Studie.Status.INVORBEREITUNG,aBlockgroesse);
		aStudie.setBenutzerkontoLogging(aBenutzer);
		aStudie = DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(aStudie);
		
		long aStudieId = aStudie.getId();
		
		for (int i=1;i<aAnzahl_Arme+1;i++) {
			
			// Alle Arme holen
			StudienarmBean aArm = null;

			String aArmBezeichnung = request.getParameter(Parameter.studienarm.BEZEICHNUNG.toString()+i);
			String aArmBeschreibung = request.getParameter(Parameter.studienarm.BESCHREIBUNG.toString()+i);
			
			aArm = new StudienarmBean(NullKonstanten.DUMMY_ID,aStudieId,Studie.Status.AKTIV,aArmBezeichnung,aArmBeschreibung);
			aArm.setBenutzerkontoLogging(aBenutzer);
			aArm = DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(aArm);

		}
		
		for (int i=1;i<aAnzahl_Strata+1;i++) {
			
			// Alle Strata holen
			StrataBean aStrata = null;

			String aStrataName = request.getParameter(Parameter.strata.NAME.toString()+i);
			String aStrataBeschreibung = request.getParameter(Parameter.strata.BESCHREIBUNG.toString()+i);
			String aAuspraegungen = request.getParameter(Parameter.strata.AUSPRAEGUNGEN.toString());
			
			
			
			aStrata = new StrataBean(NullKonstanten.DUMMY_ID,aStudieId,aStrataName,aStrataBeschreibung);
			aStrata.setBenutzerkontoLogging(aBenutzer);
			aStrata = DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(aStrata);
			
			long aStrataId = aStrata.getId();
			
			String[] aAuspraegungenArray = aAuspraegungen.split("\n",-2);
			
			for (int j=0;j<aAuspraegungenArray.length;j++) {
				
				// Alle Auspraegungen holen
				StrataAuspraegungBean aAuspraegung = new StrataAuspraegungBean(NullKonstanten.DUMMY_ID,aStrataId,aAuspraegungenArray[j]);
				aAuspraegung.setBenutzerkontoLogging(aBenutzer);
				aAuspraegung = DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(aAuspraegung);
				
			}
			
		}
		
		// TODO Statistiker anlegen!
		
		request.getSession().setAttribute(DispatcherServlet.sessionParameter.AKTUELLE_STUDIE.toString(), aStudie);
		
		request.setAttribute(DispatcherServlet.NACHRICHT_OK,"Die Studie wurde erfolgreich eingerichtet!");
		
		request.getRequestDispatcher(Jsp.STUDIE_ANSEHEN).forward(request, response);
		
		} catch (StrataException e) {
			
			this.weiterleitungBeiFehler(e.getMessage(), request, response);
		
		
		} catch (StudienarmException e) {
			
			this.weiterleitungBeiFehler(e.getMessage(), request, response);
		
		} catch (StudieException e) {
			
			this.weiterleitungBeiFehler(e.getMessage(), request, response);
			
		} catch (ParseException e) {
			e.printStackTrace();
			this.weiterleitungBeiFehler("Systemfehler! ", request, response);
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			this.weiterleitungBeiFehler("Systemfehler!", request, response);
			
		}
	}

	/**
	 * Weiterleitung der Anfrage bei Fehler
	 * <p>
	 * Leitet den Benutzer auf die entsprechende Indexseite weiter, abhaengig
	 * davon, ob das System gesperrt ist oder nicht.
	 * </p>
	 * <p>
	 * Die uebergebene Fehlermeldung wird an den Request als
	 * {@link DispatcherServlet#FEHLERNACHRICHT} angefuegt.
	 * </p>
	 * 
	 * @param fehlermeldungAnBenutzer
	 *            Fehlermeldung, die dem Benutzer angzeigt wird
	 * @param request
	 *            Der Request fuer das Servlet.
	 * @param response
	 *            Der Response Servlet.
	 * @throws IOException
	 *             Falls Fehler in den E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung auftreten.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	private void weiterleitungBeiFehler(String fehlermeldungAnBenutzer,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Fehlermeldung anfuegen
		request.setAttribute(DispatcherServlet.FEHLERNACHRICHT,
				fehlermeldungAnBenutzer);

		request.getRequestDispatcher(Jsp.STUDIE_ANLEGEN).forward(request,
				response);

	}

	/**
	 * Aenderbare Daten einer bereits in der Datenbank bestehenden Studie werden
	 * gesetzt.
	 * 
	 * @param request
	 *            Der request fuer das Servlet.
	 * @param response
	 *            Der Response des Servlets.
	 * @throws IOException
	 *             Falls Fehler in der E/A-Verarbeitung.
	 * @throws ServletException
	 *             Falls Fehler in der HTTP-Verarbeitung.
	 */
	private void studieAendern(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Alle aenderbaren Attribute des request inititalisieren
		String startDatum = request.getParameter((Parameter.studie.STARTDATUM)
				.name());
		String endDatum = request.getParameter((Parameter.studie.ENDDATUM)
				.name());
		String studienarme = request
				.getParameter((Parameter.studie.ARME_STUDIE).name());

		Studie aStudie = (Studie) (request.getSession())
				.getAttribute("aStudie");

		StudieBean aStudieBean = (StudieBean) (request.getSession())
				.getAttribute("aStudie");

		try {
			try {
				Vector<StudienarmBean> studienArme = aStudieBean
						.getStudienarme();
				GregorianCalendar aStartDatum = aStudieBean.getStartDatum();
				GregorianCalendar aEndDatum = aStudieBean.getEndDatum();
				aStudieBean.setStudienZeitraum(aStartDatum, aEndDatum);
				aStudieBean.setBeschreibung(request
						.getParameter((Parameter.studie.BESCHREIBUNG).name()));

				DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(
						aStudieBean);

			} catch (Exception e) {
				request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
						.getMessage());
			}

			request.setAttribute(DispatcherServlet.NACHRICHT_OK,
					this.AENDERUNG_STUDIE_ERFOLGREICH);
			request.getRequestDispatcher("studie_auswaehlen.jsp").forward(

			request, response);
		} catch (DatenbankExceptions e) {
			request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
					.getMessage());
		}

	}

	/**
	 * Diese Methode erstellt einen Vektor mit den Zentren, die der aktuellen
	 * Studie zugeordnet sind
	 * 
	 * @param request
	 * @param response
	 * @return Vektor der Zentren, die der Studie zugeordnet sind
	 */
	private Vector<ZentrumBean> getZugehoerigeZentren(
			HttpServletRequest request, HttpServletResponse response) {
		StudieBean aSession = (StudieBean) request.getSession().getAttribute(
				DispatcherServlet.sessionParameter.AKTUELLE_STUDIE.toString());
		Vector<ZentrumBean> zugehoerigeZentren = null;
		try {
			zugehoerigeZentren = aSession.getZentren();
		} catch (DatenbankExceptions e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return zugehoerigeZentren;

	}

	/**
	 * Diese Methode erstellt einen Vektor mit den Zentren, die der aktuellen
	 * Studie nicht zugeordnet sind
	 * 
	 * @param request
	 * @param response
	 * @return Vektor der Zentren, die der Studie nicht zugeordnet sind
	 * @throws DatenbankExceptions
	 */
	private Vector<ZentrumBean> getNichtZugehoerigeZentren(
			HttpServletRequest request, HttpServletResponse response)
			throws DatenbankExceptions {
		ZentrumBean zb = new ZentrumBean();
		zb.setIstAktiviert(true);
		zb.setFilter(true);

		Vector<ZentrumBean> zentrenliste = null;

		zentrenliste = Zentrum.suchenZentrum(zb);

		Vector<ZentrumBean> zugehoerigeZentren = (Vector<ZentrumBean>) request
				.getAttribute("zugehoerigeZentren");

		// Vector<ZentrumBean> zugehoerigeZentren =
		// getZugehoerigeZentren(request, response);
		for (int y = 0; y < zentrenliste.size(); y++) {
			for (int x = 0; x < zugehoerigeZentren.size(); x++) {
				if (zentrenliste.elementAt(y).equals(
						zugehoerigeZentren.elementAt(x))) {
					zentrenliste.removeElementAt(y);
				}

			}
		}
		return zentrenliste;

	}

	private void patientHinzufuegen(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {

			StudieBean aStudie = ((StudieBean) request.getSession()
					.getAttribute(
							DispatcherServlet.sessionParameter.AKTUELLE_STUDIE
									.toString()));

			PatientBean aPatient = new PatientBean();

			String initialen = request.getParameter(
					Parameter.patient.INITIALEN.toString()).trim();
			GregorianCalendar geburtsdatum = new GregorianCalendar();
			GregorianCalendar datumAufklaerung = new GregorianCalendar();
			char geschlecht = request.getParameter(
					Parameter.patient.GESCHLECHT.toString()).trim().charAt(0);
			geburtsdatum.setTime(DateFormat.getDateInstance(DateFormat.MEDIUM,
					Locale.GERMANY).parse(
					request.getParameter(
							Parameter.patient.GEBURTSDATUM.toString()).trim()));
			datumAufklaerung.setTime(DateFormat.getDateInstance(
					DateFormat.MEDIUM, Locale.GERMANY).parse(
					request.getParameter(
							Parameter.patient.DATUMAUFKLAERUNG.toString())
							.trim()));
			float koerperOberflaeche = 0f;

			koerperOberflaeche = Float.parseFloat(request.getParameter(
					Parameter.patient.KOERPEROBERFLAECHE.toString()).trim());

			int performanceStatus = Integer.parseInt(request.getParameter(
					Parameter.patient.PERFORMANCESTATUS.toString()).trim());

			aPatient.setBenutzerkonto((BenutzerkontoBean) request.getSession()
					.getAttribute("aBenutzer"));
			aPatient.setBenutzerkontoLogging((BenutzerkontoBean) request
					.getSession().getAttribute("aBenutzer"));
			aPatient.setInitialen(initialen);
			aPatient.setGeburtsdatum(geburtsdatum);
			aPatient.setDatumAufklaerung(datumAufklaerung);
			aPatient.setGeschlecht(geschlecht);
			aPatient.setKoerperoberflaeche(koerperOberflaeche);
			aPatient.setPerformanceStatus(performanceStatus);

			Algorithmen randAlg = aStudie.getAlgorithmus();
			if (randAlg == Algorithmen.VOLLSTAENDIGE_RANDOMISATION) {
				new VollstaendigeRandomisation(aStudie)
						.randomisierenPatient(aPatient);
			} else if (randAlg == Algorithmen.BLOCKRANDOMISATION_OHNE_STRATA) {
				new BlockRandomisation(aStudie).randomisierenPatient(aPatient);
			} else if (randAlg == Algorithmen.BLOCKRANDOMISATION_MIT_STRATA) {
				HashMap<Long, Long> strataKombinationen = new HashMap<Long, Long>();
				Vector<StrataBean> sV = new Vector<StrataBean>(Strata
						.getAll(aStudie));

				for (StrataBean s : sV) {
					String sAIdString = request.getParameter(
							Parameter.patient.AUSPRAEGUNG.name() + s.getId());
					long sAId = Integer.parseInt(sAIdString
							.trim());
					strataKombinationen
							.put(new Long(s.getId()), new Long(sAId));
				}

				String strataGruppe = Strata
						.getStratakombinationsString(strataKombinationen);
				aPatient.setStrataGruppe(strataGruppe);

				new StrataBlockRandomisation(aStudie)
						.randomisierenPatient(aPatient);
			}

			Patient.speichern(aPatient);
			Logger.getLogger(this.getClass()).debug(
					"Patient mit ID " + aPatient.getId()
							+ " erfolgreich in Studienarm mit ID "
							+ aPatient.getStudienarmId() + " hinzugefuergt");

			request.setAttribute(DispatcherServlet.NACHRICHT_OK,
					"Ihr Patient <b>" + aPatient.getInitialen()
							+ "</b> wurde in den Studienarm <b>"
							+ aPatient.getStudienarm().getBezeichnung()
							+ "</b> randomisiert.");

			request.getRequestDispatcher("/studie_ansehen.jsp").forward(
					request, response);

		} catch (PatientException e) {
			Logger.getLogger(this.getClass()).debug("Fehler", e);

			request.setAttribute(DispatcherServlet.FEHLERNACHRICHT, e
					.getMessage());
			request.getRequestDispatcher("/patient_hinzufuegen.jsp").forward(
					request, response);
		} catch (ParseException e) {
			Logger.getLogger(this.getClass()).debug("Fehler", e);

			request.setAttribute(DispatcherServlet.FEHLERNACHRICHT,
					"Bitte geben Sie Datumseingaben im Format TT.MM.JJJJ ein.");
			request.getRequestDispatcher("/patient_hinzufuegen.jsp").forward(
					request, response);
		} catch (NumberFormatException e) {
			Logger.getLogger(this.getClass()).debug("Fehler", e);
			request
					.setAttribute(
							DispatcherServlet.FEHLERNACHRICHT,
							"Bitte geben Sie die Koerperoberflaeche als Zahl mit Dezimaltrennzeichen '.' ein.");
			request.getRequestDispatcher("/patient_hinzufuegen.jsp").forward(
					request, response);
		} catch (StrataException e) {
			IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		}

	}
}