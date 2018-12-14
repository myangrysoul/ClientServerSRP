package ClientField;

import SafeContext.Key;
import SafeContext.SRP;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

public class ClientField {

    private final String salt;
    private final BigInteger x;
    private final long pass_verifier;
    private final String pass;
    private final String id;
    private int a;
    private long aBig;
    String key;
    private long bBig;
    private String u;
    private String m;
    private String r;
    private boolean authPassed;
    private Key closedKey;
    private Key openKey;
    static String destination="";

    public void setClosedKey(Key closedKey) {
        this.closedKey = closedKey;
    }

    public boolean isAuthPassed() {
        return authPassed;
    }

    public void setAuthPassed(boolean authPassed) {
        this.authPassed = authPassed;
    }

    ClientField(String id, String pass) {
        SRP.init();
        this.pass = pass;
        this.id = id;
        salt = UUID.randomUUID().toString();
        String hash = SRP.getHash((salt + this.pass).getBytes());
        x = new BigInteger(hash, 16);
        BigInteger pv = BigInteger.valueOf(SRP.getG()).modPow(x, BigInteger.valueOf(SRP.getN()));
        pass_verifier = Long.valueOf(pv.toString());
        System.out.println("passv " + pass_verifier);

    }


    public String getM() {
        return m;
    }

    void keyComp() {
        BigInteger S;
        BigInteger U = new BigInteger(u, 16);
        long ex1 = bBig - SRP.getK() * pass_verifier;
        BigInteger ex2 = U.multiply(x).add(BigInteger.valueOf(a));
        S = BigInteger.valueOf(ex1).modPow(ex2, BigInteger.valueOf(SRP.getN()));
        key = SRP.getHash(S.toString().getBytes());
        System.out.println("Key: " + key);
    }

    String confirmationHash() {
        BigInteger ex1 = new BigInteger(SRP.getHash(Long.valueOf(SRP.getN()).toString().getBytes()), 16);
        BigInteger ex2 = new BigInteger(SRP.getHash(Long.valueOf(SRP.getG()).toString().getBytes()), 16);
        String arg1 = ex1.xor(ex2).toString();
        byte[] args = (arg1 + SRP.getHash(id.getBytes()) + salt + aBig + bBig + key).getBytes();
        m = SRP.getHash(args);
        return m;
    }

    String compR() {
        r = SRP.getHash((aBig + m + key).getBytes());
        return r;
    }

    public String getR() {
        return r;
    }

    long compA() {
        a = randomNatural();
        a = 84;
        aBig = SRP.powMod(SRP.getG(), a, SRP.getN());
        return aBig;
    }

    String scrambler() {
        long a = aBig;
        long b = bBig;
        String con = a + String.valueOf(b);
        String u = SRP.getHash(con.getBytes());
        this.u = u;
        return u;
    }

    int randomNatural() {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(1000000 - 10000) + 10000;
    }

    public void setOpenKey(Key openKey) {
        this.openKey = openKey;
    }

    BigInteger rsaEncode(String message) {
        BigInteger binary = new BigInteger(message.getBytes());
        System.out.println(binary);
        if (binary.signum() == -1) {
            binary = binary.abs();
            binary = binary.modPow(openKey.getExp(), openKey.getN());
            System.out.println(binary);
            System.out.println("Enconding message...\n" + "Encoded message: " + binary.toString(16));
            binary = binary.negate();
        } else {
            binary = binary.modPow(openKey.getExp(), openKey.getN());
            System.out.println(binary);
            System.out.println("Enconding message...\n" + "Encoded message: " + binary.toString(16));
        }
        return binary;
    }

    String rsaDecode(BigInteger encMessage) {
        BigInteger msg = encMessage;
        if (encMessage.signum() == -1) {
            msg = encMessage.negate();
            msg = msg.modPow(closedKey.getExp(), closedKey.getN());
            msg = msg.negate();
        } else {
            msg = msg.modPow(closedKey.getExp(), closedKey.getN());
        }
        System.out.println("Decoded message: " + msg);
        return new String(msg.toByteArray());
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