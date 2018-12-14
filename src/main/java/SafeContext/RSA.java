package SafeContext;

import ClientField.ClientField;
import java.math.BigInteger;
import java.util.Random;

public final class RSA {

    private static final long e = 257;//fermat number;
    private static BigInteger fi;
    private RSA(){

    }

   /*public static Key compute(Account acc){
        BigInteger Q=randomPrime();
        BigInteger P=randomPrime();
        BigInteger n=Q.multiply(P);
        fi=Q.subtract(BigInteger.valueOf(1)).multiply(P.subtract(BigInteger.valueOf(1)));
        System.out.println("N="+n+" fi="+fi);
        BigInteger d=inverse();
        acc.setClosedKey(new Key(n,d));
        return new Key(n,BigInteger.valueOf(e));

    }*/

    public static Key compute(ClientField clientField) {
        BigInteger Q = randomPrime();
        BigInteger P = randomPrime();
        BigInteger n = Q.multiply(P);
        fi = Q.subtract(BigInteger.valueOf(1)).multiply(P.subtract(BigInteger.valueOf(1)));
        System.out.println("N=" + n + " fi=" + fi);
        BigInteger d = inverse();
        clientField.setClosedKey(new Key(n, d));
        return new Key(n, BigInteger.valueOf(e));
    }


    /*static long [] euclidAlgorithm(long mod,long b){
        long q,r=b,b1,x=0,y,x1=0,x2=1,y1=1,y2=0;
        b=mod;
        while(r>0){
            q=b/r;
            b1=r;
            r=b-q*r;
            x=x2-q*x1;
            y=y2-q*y1;
            b=b1;
            x2=x1;
            x1=x;
            y2=y1;
            y1=y;
        /*
        a->b
        b->r
        x2->x1
        x1->x
        y2->y1
        y1->y


        }
        System.out.println(Arrays.toString(new long[]{b,x2,y2}));
        return new long[] { b, x2, y2 };
    }*/
    private static BigInteger inverse() {
        return BigInteger.valueOf(e).modInverse(fi);
    }

    private static BigInteger randomPrime() {
        BigInteger rand = new BigInteger(512, 32, new Random(System.currentTimeMillis()));
        System.out.println(rand);
        return rand;
    }

}
