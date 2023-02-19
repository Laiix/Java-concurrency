package java_concurrency_in_practice._05_basicbuildingblocks;

import java.math.BigInteger;

public class ExpensiveFunction implements Computable<String, BigInteger> {
    @Override
    public BigInteger compute(String arg) {
        //在经过长时间的就算后
        return new BigInteger(arg);
    }
}
