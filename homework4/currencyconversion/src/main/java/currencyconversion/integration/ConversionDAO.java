/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currencyconversion.integration;

import currencyconversion.model.ExchangeRate;
import currencyconversion.model.ExchangeRateDTO;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Emil
 */
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
public class ConversionDAO {
    @PersistenceContext(unitName = "converterPU")
    private EntityManager em;
    
    public ExchangeRate getExrate(String fromCurr){
       return em.find(ExchangeRate.class, fromCurr);
    }
    public void storerate(){
        ExchangeRate curr = new ExchangeRate("GBP", 1);
        em.persist(curr);
        ExchangeRate curr1 = new ExchangeRate("BTC", 11969.01);
        em.persist(curr1);
        ExchangeRate curr2 = new ExchangeRate("EUR", 1.14);
        em.persist(curr2);
        ExchangeRate curr3 = new ExchangeRate("USD", 1.34);
        em.persist(curr3);
        ExchangeRate curr4 = new ExchangeRate("SEK", 11.35);
        em.persist(curr4);
    }
   
}