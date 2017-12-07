/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currencyconversion.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Emil
 */
@Entity
public class ExchangeRate implements ExchangeRateDTO, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String currency;
    private double rate;

    //Create instance of exchangerate
    public ExchangeRate(){
    }
    public ExchangeRate(String currency, double rate){
        this.currency = currency;
        this.rate = rate;
    }
    @Override
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    @Override
    public double getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.currency = rate;
    }

    
}
