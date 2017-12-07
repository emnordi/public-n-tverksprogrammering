/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currencyconversion.controller;

import currencyconversion.integration.ConversionDAO;
import currencyconversion.model.ExchangeRate;
import currencyconversion.model.ExchangeRateDTO;
import currencyconversion.model.ResultCalculator;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Emil
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class Controller {
    
    @EJB
    ConversionDAO cdao;
    @EJB
    ResultCalculator res;
    public ExchangeRateDTO convert(String fromCurr, String toCurr, double fromAmount) {
        ExchangeRate from = cdao.getExrate(fromCurr);
        ExchangeRate to = cdao.getExrate(toCurr);
        ExchangeRateDTO result = res.calculate(from, to, fromAmount);
        return result;
    }

}
