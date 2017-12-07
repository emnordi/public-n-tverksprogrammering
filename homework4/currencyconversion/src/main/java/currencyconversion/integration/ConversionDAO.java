/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currencyconversion.integration;

import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Emil
 */
@Stateless
public class ConversionDAO {
    private double res;
    @PersistenceContext(unitName = "converterPU")
   public void convert(String fromCurr, String toCurr, double fromAmount){
        res = fromAmount * 10;
    }
   
   public Double getRes(){
       return res;
   }
}