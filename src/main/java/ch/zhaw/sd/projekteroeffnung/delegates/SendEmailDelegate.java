package ch.zhaw.sd.projekteroeffnung.delegates;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * SendEmailDelegate
 */
@Named("sendEmail")
public class SendEmailDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String subject = (String) execution.getVariable("mailSubject");

        System.out.println("Mail versandt mit Betreff '"+subject+"'");
    }

    
}