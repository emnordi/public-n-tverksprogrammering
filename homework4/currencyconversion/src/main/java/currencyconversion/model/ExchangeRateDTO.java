/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currencyconversion.model;

/**
 * A DTO for the ExchangeRate object
 */
public interface ExchangeRateDTO {
 
    String getCurrency();
    
    double getRate();
}
