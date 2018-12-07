package ClientField;

import SafeContext.SRP;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

public class ClientField {

    private String salt;
    private long x;
    private long pass_verifier;
    private String pass;
    private String id;
    private int a;
    private long aBig;
    private String key;
    private long bBig;
    private String u;
    private String m;



    ClientField(String id, String pass, BigInteger big1){
        this.pass=pass;
        this.id=id;
        salt=UUID.randomUUID().toString();
        String hash=SRP.getHash((salt+this.pass).getBytes());
        BigInteger big=new BigInteger(hash,16);
        System.out.println(big);
        pass_verifier= SRP.powMod(SRP.getG(),x,SRP.getN());

    }


    void keyComp(){
        BigInteger S;
        BigInteger U=new BigInteger(u,16);
        long ex1=bBig-SRP.getK()*SRP.powMod(SRP.getG(),x,SRP.getN());
        BigInteger ex2=U.multiply(BigInteger.valueOf(x).add(BigInteger.valueOf(a)));
        S=BigInteger.valueOf(ex1).modPow(ex2,BigInteger.valueOf(SRP.getN()));
        key=SRP.getHash(S.toString().getBytes());
    }

    void confirmationHash(){
        BigInteger ex1=new BigInteger(SRP.getHash(Long.valueOf(SRP.getN()).toString().getBytes()));
        BigInteger ex2=new BigInteger(SRP.getHash(Long.valueOf(SRP.getG()).toString().getBytes()));
        String arg1=ex1.xor(ex2).toString();
        byte [] args =(arg1 + SRP.getHash(id.getBytes())+ salt+aBig+bBig+key).getBytes();
        m=SRP.getHash(args);
    }

    void compR(String userId){
        m=SRP.getHash((aBig+m+key).getBytes());
    }

    void compA(){
        a=randomNatural();
        aBig=SRP.powMod(SRP.getG(),a,SRP.getN());
    }

    private void scrambler() {
        long a=aBig;
        long b=bBig;
        String con= a + String.valueOf(b);
        String u=SRP.getHash(con.getBytes());
        this.u=u;
    }

    int randomNatural() {
        Random random = new Random(System.currentTimeMillis());
        int i = random.nextInt(1000000 - 10000) + 10000;
        return i;
    }


    public String getSalt() {
        return salt;
    }

    public long getPass_verifier() {
        return pass_verifier;
    }
    public void setbBig(long bBig) {
        this.bBig = bBig;
    }


    public String getId() {
        return id;
    }
}