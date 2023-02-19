package java_concurrency_in_practice._03_sharingofobjects;

import net.jcip.annotations.Immutable;

import java.math.BigInteger;
import java.util.Arrays;

@Immutable
public class OneValueCache {
    private final BigInteger lastNumber;
    private final BigInteger[] lastFactors;


    public OneValueCache(BigInteger i, BigInteger[] factors) {
        this.lastNumber = i;
        this.lastFactors = factors;
    }

    public BigInteger[] getFactors(BigInteger i) {
        if(lastNumber==null || !lastNumber.equals(i)) {
            return null;
        } else
            return Arrays.copyOf(lastFactors, lastFactors.length);
    }
}
