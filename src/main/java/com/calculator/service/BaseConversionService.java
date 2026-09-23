package com.calculator.service;

import com.calculator.calculator.CalculatorException;
import com.calculator.model.BaseConversionResult;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Set;

/** 二、八、十、十六进制整数互转。 */
@Service
public class BaseConversionService {

    private static final Set<Integer> SUPPORTED_BASES = Set.of(2, 8, 10, 16);

    public BaseConversionResult convert(String value, int fromBase, int toBase) {
        if (!SUPPORTED_BASES.contains(fromBase) || !SUPPORTED_BASES.contains(toBase)) {
            throw new CalculatorException("Unsupported base");
        }
        String normalized = value.trim();
        try {
            String result = new BigInteger(normalized, fromBase)
                    .toString(toBase)
                    .toUpperCase();
            return new BaseConversionResult(normalized.toUpperCase(), fromBase, toBase, result);
        } catch (NumberFormatException e) {
            throw new CalculatorException("Invalid number for source base");
        }
    }
}
