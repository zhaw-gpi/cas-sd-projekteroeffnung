package ch.zhaw.sd.projekteroeffnung.delegates;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import ch.zhaw.sd.projekteroeffnung.services.EmailService;

/**
 * SendEmailDelegate
 */
@Named("sendEmail")
public class SendEmailDelegate implements JavaDelegate {

    @Autowired
    private EmailService emailService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String mailType = (String) execution.getVariable("mailType");
        String projektName = (String) execution.getVariable("projektName");
        String subject = "";
        String body = "";
        String to = "";

        switch (mailType) {
            case "ErinnerungGL":
                to = "gl@a.ch";
                subject = "Erinnerung für '" + projektName + "'";
                body = "Liebe GL-Mitglieder\n\nDer Antrag wurde vor zwei Wochen eingereicht, aber noch nicht geprüft. Bitte erledigt dies so schnell als möglich.\n\nLiebe Grüsse\nIhre Prozessapplikation";
                break;
            case "ErinnerungController":
                to = "gl@a.ch";
                subject = "Erinnerung für '" + projektName + "'";
                body = "Liebe/r ControllerIn\n\nDer Antrag wurde vor einer Woche eingereicht, aber noch nicht geprüft. Bitte erledigen Sie dies so schnell als möglich.\n\nLiebe Grüsse\nIhre Prozessapplikation";
                break;
            case "Ablehnung":
                String begruendung = (String) execution.getVariable("begruendung");
                to = "antragsteller@a.ch";
                subject = "Projektantrag abgelehnt für '" + projektName + "'";
                body = "Liebe/r AntragstellerIn\n\nIhr Projektantrag wurde abgelehnt aus folgendem Grund: " + begruendung + "\n\nLieber Gruss\nIhre Prozessapplikation";
                break;
            case "Erfolgreich":
                to = "antragsteller@a.ch";
                subject = "Projektantrag bewilligt für '" + projektName + "'";
                body = "Liebe/r AntragstellerIn\n\nIhr Projektantrag wurde bewilligt.\n\nLieber Gruss\nIhre Prozessapplikation";
        }

        emailService.sendSimpleMail(to, subject, body);
    }

}