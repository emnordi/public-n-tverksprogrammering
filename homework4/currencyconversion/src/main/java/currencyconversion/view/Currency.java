/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currencyconversion.view;

import currencyconversion.controller.Controller;
import currencyconversion.model.ExchangeRateDTO;
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
    private Double fromAmount = null;
    private Double toAmount = null;
    private String toCurrency;
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
        return fromCurr;
    }
    
    public void setFromCurr(String fromCurr) {
        this.fromCurr = fromCurr;
    }
    //Get the currency the user converted to
    public String getToCurr() {
        return toCurr;
    }
    //Set the currency the user wants to convert to
    public void setToCurr(String toCurr) {
        this.toCurr = toCurr;
    }
    //Get amount user fronverts
    public Double getToAmount() {
        return toAmount;
    }
    //Get currency the user converted to
    public String getToCurrency() {
        return toCurrency;
    }
    //Get the amount user converted from
    public Double getFromAmount() {
        return fromAmount;
    }
    //Sets the amount the users wants to convert
    public void setFromAmount(Double fromAmount) {
        this.fromAmount = fromAmount;
    }
    //Called on when user presses button to convert
    public void convert() {
       startConversation();
       ExchangeRateDTO ans = controller.convert(fromCurr, toCurr, fromAmount);
       toAmount = ans.getRate();
       toCurrency = ans.getCurrency();
    }

}
