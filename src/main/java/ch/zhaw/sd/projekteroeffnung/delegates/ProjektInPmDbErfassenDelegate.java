package ch.zhaw.sd.projekteroeffnung.delegates;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Named;

import org.json.JSONObject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * ProjektInPmDbErfassenDelegate
 */
@Named("projektInPmDbErfassen")
public class ProjektInPmDbErfassenDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // RestTemplate instanzieren
        RestTemplate restTemplate = new RestTemplate();
        
        // Prozessvariablen auslesen
        String projektTitel = (String) execution.getVariable("projektName");
        Integer gesamtKosten = (Integer) execution.getVariable("gesamtKosten");
        Date projektStartDatum = (Date) execution.getVariable("projektStartDatum");
        Date projektEndDatum = (Date) execution.getVariable("projektEndDatum");
        String projektLeiter = (String) execution.getVariable("projektleiter").toString();
        String projektMitarbeiter = (String) execution.getVariable("projektMitarbeiter").toString();
        Date mitarbeiterStartDatum = (Date) execution.getVariable("mitarbeiterStartDatum");
        Date mitarbeiterEndDatum = (Date) execution.getVariable("mitarbeiterEndDatum");

        /**
         *  Projekt hinzufügen
         */
        // Projekt-JSON-Objekt erstellen für Post-Request
        JSONObject jsonProjekt = new JSONObject();
        jsonProjekt.put("name", projektTitel);
        jsonProjekt.put("kosten", gesamtKosten);
        jsonProjekt.put("start", new SimpleDateFormat("yyyy-MM-dd").format(projektStartDatum));
        jsonProjekt.put("end", new SimpleDateFormat("yyyy-MM-dd").format(projektEndDatum));

        // Http-Header (JSON-Format) setzen
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");

        // Request zusammensetzen aus serialisiertem Objekt und Header
        HttpEntity<String> request = new HttpEntity<String>(jsonProjekt.toString(), headers);        

        // Request senden und Antwort als ResponseEntity erhalten (im Normalfall mit dem JSON-String)
        ResponseEntity<String> responseProjekt = restTemplate.exchange("http://localhost:8090/api/projekte", HttpMethod.POST, request, String.class);

        // Für später aus der Antwort die URL zum soeben hinzugefügten Projekt erhalten 
        String projektUri = new JSONObject(responseProjekt.getBody()).getJSONObject("_links").getJSONObject("self").getString("href");

        /**
         * Projektleiter-Einsatz erstellen
         */
        // Projektleiter-Einsatz erstellen
        JSONObject jsonEinsatzProjektleiter = new JSONObject();
        jsonEinsatzProjektleiter.put("einsatzStart", new SimpleDateFormat("yyyy-MM-dd").format(projektStartDatum));
        jsonEinsatzProjektleiter.put("einsatzEnde", new SimpleDateFormat("yyyy-MM-dd").format(projektEndDatum));

        // Request zusammensetzen aus serialisiertem Objekt und Header
        request = new HttpEntity<String>(jsonEinsatzProjektleiter.toString(), headers);

        // Request senden und Antwort als ResponseEntity erhalten (im Normalfall mit dem JSON-String)
        ResponseEntity<String> responseEinsatzProjektleiter = restTemplate.exchange("http://localhost:8090/api/einsaetze", HttpMethod.POST, request, String.class);

        // Für später aus der Antwort die URLs zum soeben hinzugefügten Einsatz erhalten
        JSONObject jsonEinsatzResponseLinks = new JSONObject(responseEinsatzProjektleiter.getBody()).getJSONObject("_links");
        String einsatzProjektUri = jsonEinsatzResponseLinks.getJSONObject("projekt").getString("href");
        String einsatzMitarbeiterUri = jsonEinsatzResponseLinks.getJSONObject("mitarbeiter").getString("href");

        // Im Header den Content-Type auf eine URI-List setzen
        headers.set("Content-type", "text/uri-list");

        // Request erstellen mit dem zum Einsatz gehörenden Projekt
        request = new HttpEntity<String>(projektUri, headers);

        // Request senden und Antwort als ResponseEntity verwerfen (im Normalfall nur Status Code 204 -> Fehlerbehandlung)
        restTemplate.exchange(einsatzProjektUri, HttpMethod.PUT, request, String.class);

        // Die URL zum entsprechenden Mitarbeiter (Projektleiter) auslesen
        String projektleiterUri = new JSONObject(projektLeiter).getJSONObject("_links").getJSONObject("self").getString("href");

        // Request erstellen mit dem zum Einsatz gehörenden Mitarbeiter
        request = new HttpEntity<String>(projektleiterUri, headers);

        // Request senden und Antwort als ResponseEntity verwerfen (im Normalfall nur Status Code 204 -> Fehlerbehandlung)
        restTemplate.exchange(einsatzMitarbeiterUri, HttpMethod.PUT, request, String.class);


        /**
         * Projektmitarbeiter-Einsatz erstellen
         */
        // Projektmitarbeiter-Einsatz erstellen
        JSONObject jsonEinsatzProjektmitarbeiter = new JSONObject();
        jsonEinsatzProjektmitarbeiter.put("einsatzStart", new SimpleDateFormat("yyyy-MM-dd").format(mitarbeiterStartDatum));
        jsonEinsatzProjektmitarbeiter.put("einsatzEnde", new SimpleDateFormat("yyyy-MM-dd").format(mitarbeiterEndDatum));

        // Content-Type wieder auf JSON setzen
        headers.set("Content-type", "application/json");

        // Request zusammensetzen aus serialisiertem Objekt und Header
        request = new HttpEntity<String>(jsonEinsatzProjektmitarbeiter.toString(), headers);

        // Request senden und Antwort als ResponseEntity erhalten (im Normalfall mit dem JSON-String)
        ResponseEntity<String> responseEinsatzProjektmitarbeiter = restTemplate.exchange("http://localhost:8090/api/einsaetze", HttpMethod.POST, request, String.class);

        // Für später aus der Antwort die URLs zum soeben hinzugefügten Einsatz erhalten
        jsonEinsatzResponseLinks = new JSONObject(responseEinsatzProjektmitarbeiter.getBody()).getJSONObject("_links");
        einsatzProjektUri = jsonEinsatzResponseLinks.getJSONObject("projekt").getString("href");
        einsatzMitarbeiterUri = jsonEinsatzResponseLinks.getJSONObject("mitarbeiter").getString("href");
        
        // Im Header den Content-Type wieder auf eine URI-List setzen
        headers.set("Content-type", "text/uri-list");

        // Request erstellen mit dem zum Einsatz gehörenden Projekt
        request = new HttpEntity<String>(projektUri, headers);

        // Request senden und Antwort als ResponseEntity verwerfen (im Normalfall nur Status Code 204 -> Fehlerbehandlung)
        restTemplate.exchange(einsatzProjektUri, HttpMethod.PUT, request, String.class);

        // Die URL zum entsprechenden Mitarbeiter (Projektmitarbeiter) auslesen
        String projektmitarbeiterUri = new JSONObject(projektMitarbeiter).getJSONObject("_links").getJSONObject("self").getString("href");

        // Request erstellen mit dem zum Einsatz gehörenden Mitarbeiter
        request = new HttpEntity<String>(projektmitarbeiterUri, headers);

        // Request senden und Antwort als ResponseEntity verwerfen (im Normalfall nur Status Code 204 -> Fehlerbehandlung)
        restTemplate.exchange(einsatzMitarbeiterUri, HttpMethod.PUT, request, String.class);

    }

    
}