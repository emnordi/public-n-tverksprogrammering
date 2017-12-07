/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currencyconversion.model;

/**
 *
 * @author Emil
 */
public interface ExchangeRateDTO {
 
    String getCurrency();
    
    double getRate();
}
