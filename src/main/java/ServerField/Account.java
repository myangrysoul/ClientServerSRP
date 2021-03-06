package ServerField;

import SafeContext.Key;


public class Account {
    private final String user_salt;
    private final long pass_verifier;
    private long bBig;
    private long aBig;
    private long b;
    private Key openKey;
    private String destination;

    public Key getOpenKey() {
        return openKey;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setVerrified(boolean verrified) {
        this.verrified = verrified;
    }

    private boolean verrified;

    public boolean isVerrified() {
        return verrified;
    }

    public void setOpenKey(Key openKey) {
        this.openKey = openKey;
    }


    public void setB(long b) {
        this.b = b;
    }

    public long getB() {
        return b;
    }

    public String getM() {
        return m;
    }

    private String m;
    private String r;
    private String u;
    public String key;

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public Account(long pass_verifier, String salt) {
        this.pass_verifier = pass_verifier;
        user_salt = salt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getUser_salt() {
        return user_salt;
    }

    public long getPass_verifier() {
        return pass_verifier;
    }

    public long getbBig() {
        return bBig;
    }

    public long getaBig() {
        return aBig;
    }


    public void setbBig(long bBig) {
        this.bBig = bBig;
    }

    public void setaBig(long aBig) {
        this.aBig = aBig;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getU() {
        return u;
    }

}
