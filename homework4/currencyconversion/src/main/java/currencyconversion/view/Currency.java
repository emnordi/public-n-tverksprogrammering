/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currencyconversion.view;

import currencyconversion.controller.Controller;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Emil
 */
@Named("currency")
@ConversationScoped
public class Currency implements Serializable {

    @EJB
    private Controller controller;
    private double fromAmount;
    private double toAmount;
    private String fromCurr;
    private String toCurr;

    @Inject
    private Conversation conv;
    private void startConversation(){
        if(conv.isTransient()){
            conv.begin();
        }
    }
    private void stopConversation(){
        if(!conv.isTransient()){
            conv.end();
        }
    }
    public String getFromCurr() {
        return null;
    }
    
    public void setFromCurr(String fromCurr) {
        this.fromCurr = fromCurr;
    }

    public String getToCurr() {
        return null;
    }
    
    public void setToCurr(String toCurr) {
        this.toCurr = toCurr;
    }

    public double getToAmount() {
        return toAmount;
    }

    public double getFromAmount() {
        return 0;
    }
    public void setFromAmount(double fromAmount) {
        this.fromAmount = fromAmount;
    }

    public void convert() {
       startConversation();
       toAmount = controller.convert(fromCurr, toCurr, fromAmount).getRate();
    }

}
