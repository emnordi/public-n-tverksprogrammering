/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currencyconversion.model;


/**
* Calculates the result of the conversion and returns it in an object with a double with 2 digits
 */

public class ResultCalculator {
    
    public ExchangeRateDTO calculate(ExchangeRate from, ExchangeRate to, double amount){
        double res = (to.getRate()/from.getRate()) * amount;
        res = Math.floor(res*100)/100;
        ExchangeRate ans = new ExchangeRate(to.getCurrency(), res);
        return ans;
    }
    
}
