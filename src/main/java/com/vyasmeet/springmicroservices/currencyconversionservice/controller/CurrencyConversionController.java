package com.vyasmeet.springmicroservices.currencyconversionservice.controller;

import com.vyasmeet.springmicroservices.currencyconversionservice.model.CurrencyConversion;
import com.vyasmeet.springmicroservices.currencyconversionservice.proxy.CurrencyExchangeProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy proxy;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(@PathVariable String from,
                                                          @PathVariable String to,
                                                          @PathVariable BigDecimal quantity) {
        HashMap<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from",from);
        uriVariables.put("to",to);

        ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversion.class, uriVariables);
        CurrencyConversion conversion = responseEntity.getBody();

        BigDecimal conversionMultiple = conversion.getConversionMultiple();
        BigDecimal totalAmount = quantity.multiply(conversion.getConversionMultiple());
        String env = conversion.getEnvironment() + " " + "REST Template";

         return new CurrencyConversion(conversion.getId(), from, to, quantity, conversionMultiple, totalAmount, env);
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from,
                                                          @PathVariable String to,
                                                          @PathVariable BigDecimal quantity) {

        CurrencyConversion conversion = proxy.getExchangeValue(from, to);
        BigDecimal conversionMultiple = conversion.getConversionMultiple();
        BigDecimal totalAmount = quantity.multiply(conversion.getConversionMultiple());
        String env = conversion.getEnvironment() + " " + "feign";

        return new CurrencyConversion(conversion.getId(), from, to, quantity, conversionMultiple, totalAmount, env);
    }
}
