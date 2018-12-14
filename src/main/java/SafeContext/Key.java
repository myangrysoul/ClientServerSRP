package SafeContext;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.math.BigInteger;

public class Key implements Serializable {
    private static final long serialVersionUID = 5419289665882915216L;
    private final BigInteger n;
    private final BigInteger exp;

    public Key(BigInteger n, BigInteger exp) {
        this.n = n;
        this.exp = exp;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getExp() {
        return exp;
    }
}
