package Solution;

import java.math.BigDecimal;

/**
 * Created by admin on 04.02.2016.
 */
public interface Calculator {

    public BigDecimal solve (String basePer, String calcPeriod);
    public  String[] getEnumBasePer();
    public  String[] getEnumCalcPer();
    public  BigDecimal getMinzp();

}