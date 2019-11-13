package ch.zhaw.sd.projekteroeffnung.delegates;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * ProjektInPmDbErfassenDelegate
 */
@Named("projektInPmDbErfassen")
public class ProjektInPmDbErfassenDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String projektTitel = (String) execution.getVariable("projektName");
        System.out.println("Projekt " + projektTitel + " in Datenbank erfasst.");
    }

    
}