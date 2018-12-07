package ServerField;

import SafeContext.SRP;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

public class ServerField {

    private HashMap<String, Account> accountHashMap = new HashMap<String, Account>();


    void registration(String userId, String user_salt, long pass_verifier) {
        accountHashMap.put(userId, new Account(pass_verifier, user_salt));
    }

    void receive(String userId, long aBig) {
        accountHashMap.get(userId).setaBig(aBig);
    }

    void generateB(String userId) {
        int b = randomNatural();
        long passV = accountHashMap.get(userId).getPass_verifier();
        long bBig = SRP.getK() * passV + SRP.powMod(SRP.getG(), b, SRP.getN());
        accountHashMap.get(userId).setbBig(bBig);

    }
    void keyCompute(String userId){
        BigInteger S;
        Account acc=accountHashMap.get(userId);
        BigInteger U = new BigInteger(acc.getU(),16);
        long aBig = acc.getaBig();
        long bBig = acc.getbBig();
        long v = acc.getPass_verifier();
        BigInteger ex1 = BigInteger.valueOf(v).modPow(U, BigInteger.valueOf(SRP.getN()).multiply(BigInteger.valueOf(aBig)));
        S=ex1.modPow(BigInteger.valueOf(bBig),BigInteger.valueOf(SRP.getN()));
        acc.setKey(SRP.getHash(S.toString().getBytes()));
    }

    void confirmationHash(String userId){
        Account acc=accountHashMap.get(userId);
        BigInteger ex1=new BigInteger(SRP.getHash(Long.valueOf(SRP.getN()).toString().getBytes()));
        BigInteger ex2=new BigInteger(SRP.getHash(Long.valueOf(SRP.getG()).toString().getBytes()));
        String arg1=ex1.xor(ex2).toString();
        byte [] args =(arg1 + SRP.getHash(userId.getBytes())+ acc.getUser_salt()+acc.getaBig()+acc.getbBig()+acc.getKey()).getBytes();
        acc.setM(SRP.getHash(args));
    }

    void compR(String userId){
        Account acc=accountHashMap.get(userId);
        acc.setM(SRP.getHash((acc.getaBig()+acc.getM()+acc.getKey()).getBytes()));
    }

    private void scrambler(String userId) {
        long a=accountHashMap.get(userId).getaBig();
        long b=accountHashMap.get(userId).getbBig();
        String con= a + String.valueOf(b);
        String u=SRP.getHash(con.getBytes());
        accountHashMap.get(userId).setU(u);
    }





    private int randomNatural() {
        Random random = new Random(System.currentTimeMillis());
        int i = random.nextInt(1000000 - 10000) + 10000;
        return i;
    }
}

