/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currencyconversion.view;

import currencyconversion.integration.ConversionDAO;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;

/**
 *
 * @author Emil
 */
@Named(value = "currency")
public class Currency implements Serializable{
    @EJB
    private ConversionDAO conversionDAO;
    private double fromAmount;
    private double toAmount;
    public String fromCurr;
    public String toCurr;
    
    public void setFromCurr(String fromCurr){
        this.fromCurr = fromCurr;
    }
    public void setToCurr(String toCurr){
        this.toCurr = toCurr;
    }
    
    public double getToAmountr(){
        return conversionDAO.getRes();
    }

    public void setFromAmount(Double fromAmount){
        this.fromAmount = fromAmount; 
    }
    
    public void convert(){
        conversionDAO.convert(fromCurr, toCurr, fromAmount);
}
    
}