package com.lemon.Solution;

import com.lemon.Exceptions.ProblemFileException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IndexCalculatorImpl implements IndexCalculator {

    String src;
    HashMap<YearMonth, BigDecimal> indexes = new HashMap<YearMonth, BigDecimal>();
    public String[] enumBasePer;
    public String[] enumCalcPer;
    public BigDecimal minzp = new BigDecimal("1378");                                     //the minimum wage by default
    private YearMonth startIndexesPeriod = YearMonth.of(1999, 10);
    private YearMonth endIndexesPeriod;
    private static final YearMonth startCalc = YearMonth.of(2016,1);
    BigDecimal limit = new BigDecimal("1.03");
    private Pattern pattern;
    private Matcher matcher;
    String delimiter = "[\t\\s]+";
    String entryFormat = "(\\d\\.{0,1})*"+ delimiter + ".*";


    public IndexCalculatorImpl(String src){
        this.src = src;

    }

    public void initialization() throws ProblemFileException {

        fillIndex(src);
        fillBasePeriod();
        fillCalcPeriod();
    }

    //fill indexes
    public void fillIndex(String src) throws ProblemFileException {

        File file = new File(src);
        pattern = Pattern.compile(entryFormat);
        if (!file.exists() && !file.isFile()) {
            throw new ProblemFileException("File: \"" + src + "\" not found!");
        }
        try {

            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String currentIndex;
            while ((currentIndex = br.readLine()) != null) {
                matcher = pattern.matcher(currentIndex);
                if (!currentIndex.equals("") && matcher.matches()) {
                    YearMonth yearMonth = YearMonth.parse(currentIndex.split(delimiter)[0].split("\\.")[2]
                            + "-" + currentIndex.split(delimiter)[0].split("\\.")[1]);
                    indexes.put(yearMonth, new BigDecimal(currentIndex.split(delimiter)[1].replaceAll(",", ".")));
                }
            }

        } catch (IOException e) {
            throw new ProblemFileException("Problem reading file!");
        }
    }

    //fill avalible base period

    public void fillBasePeriod()
    {
        enumBasePer = new String[indexes.keySet().size()];
        YearMonth tmpPeriod = getStartIndexesPeriod();
        for (int i = 0; i < enumBasePer.length; i++) {
            enumBasePer[i] = tmpPeriod.toString();
            tmpPeriod = tmpPeriod.plusMonths(1);
        }
        setEndIndexesPeriod(tmpPeriod.minusMonths(1));
    }

    // fill avalible calculation period

    public void fillCalcPeriod() throws ProblemFileException {
        YearMonth tmpPeriod;
        YearMonth maxAvalible = getEndIndexesPeriod().plusMonths(2);
        if (maxAvalible.compareTo(getStartCalc())<0) {
            throw new ProblemFileException("Not all indexes are loaded!");
        }
        tmpPeriod = getStartCalc();
        ArrayList<String> list = new ArrayList<String>();
        while(tmpPeriod.compareTo(maxAvalible) <= 0) {
            list.add(tmpPeriod.toString());
            tmpPeriod = tmpPeriod.plusMonths(1);
        }
        enumCalcPer = new String[list.size()];
        for(int i=0; i< list.size(); i++){
            enumCalcPer[i] = list.get(i);
        }

    }

    /**
     * @param basePer base period such as"2007-12", not null
     * @param calcPeriod pay period such as"2007-12", not null
     * @return indexation coefficient, not null
     */

     public BigDecimal solve (String basePer, String calcPeriod){
        if(basePer == null || calcPeriod == null) {
            throw new IllegalArgumentException("Parameters are incorrect");
        }
        BigDecimal coefficient = BigDecimal.ONE;
        BigDecimal bound = BigDecimal.ZERO;
        YearMonth base = YearMonth.parse(basePer);
        YearMonth calc = YearMonth.parse(calcPeriod);
        ArrayList<BigDecimal> excessLimit = new ArrayList<BigDecimal>();

        if (base.compareTo(calc.minusMonths(2))>0) {
            return BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
        }

        for(YearMonth i = base.plusMonths(1); i.compareTo(calc.minusMonths(2))< 0; i = i.plusMonths(1) ){

            if(indexes.get(i)==null){
                return new BigDecimal("-1");
            }

            if(bound.compareTo(BigDecimal.ZERO)!= 0) {
                bound = bound.multiply(indexes.get(i));
            }
            else bound = indexes.get(i);


            if(bound.compareTo(limit)>=0) {
                excessLimit.add(bound.setScale(3, RoundingMode.HALF_UP));
                bound = BigDecimal.ZERO;
            }
        }

         for(BigDecimal count: excessLimit) {
             coefficient = coefficient.multiply(count);
         }

        coefficient = coefficient.subtract(BigDecimal.ONE);
        if (coefficient.compareTo(BigDecimal.ZERO) < 0) {
            coefficient = BigDecimal.ZERO ;
        }
        coefficient = coefficient.setScale(3, RoundingMode.HALF_UP);
        return coefficient;
    }


    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public  String[] getEnumCalcPer() {
        return enumCalcPer;
    }

    public  String[] getEnumBasePer() {
        return enumBasePer;
    }

    public static YearMonth getStartCalc() {
        return startCalc;
    }

    public YearMonth getStartIndexesPeriod() {
        return startIndexesPeriod;
    }

    public YearMonth getEndIndexesPeriod() {
        return endIndexesPeriod;
    }

    public void setEndIndexesPeriod(YearMonth endIndexesPeriod) {
        this.endIndexesPeriod = endIndexesPeriod;
    }

    public  BigDecimal getMinzp() {
        return minzp;
    }

    public  void setMinzp(BigDecimal minzp) {
        this.minzp = minzp;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

}
