package bank;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

public class User {
    private UUID id;
    private double balance;
    private String username;
    private String passwordHash;
    User(UUID id, double balance, String username, String password) throws InvalidInputExeption{
        if (username.matches("^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z0-9]+$")|| password.length()<=8) {
            System.err.println("username bad/ pass bad");
            throw new InvalidInputExeption();
        }
        this.id = id;
        this.balance= balance;
        this.username = username;
        this.passwordHash = getHash(password);
    }
    //for use on from file/ string
    User(String passwordHash ,UUID id, double balance, String username) throws InvalidInputExeption{
        if (username.matches("^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z0-9]+$")) {
            System.err.println("username bad/ pass bad");
            throw new InvalidInputExeption();
        }
        this.id = id;
        this.balance= balance;
        this.username = username;
        this.passwordHash = passwordHash;
    }
    private String getHash (String hash) throws InvalidInputExeption {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(hash.getBytes(StandardCharsets.UTF_8));
             // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, bytes);
 
        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));
 
        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }
        
        return hexString.toString();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new InvalidInputExeption(e.getMessage());
        }
    }
    protected boolean login(String username, String password) {
        try {
            return (username.equals(this.username) && this.passwordHash.equals(getHash(password)));
        } catch (Exception e) {
            return false;
        }
    }
    protected synchronized boolean modBalance(double amount) {
        if (amount <0 && amount > balance) {
            return false;
        }
        balance += amount;
        return true;
    }
    protected double getBalance() {
        return balance;
    }
    byte[] exportToBytes() {
        String val = "USR:" + "UID:" + id.toString() + "BAL:" + balance + "USN:" + "\""+ username + "\"" + "PWD:" + "\"" + passwordHash + '"'+"END;";
        return val.getBytes(StandardCharsets.UTF_8);
    }
    static User importFromBytes(byte[] bytes) throws InvalidInputExeption {
        String val = new String(bytes);
        if (!val.startsWith("USR:")) {
            throw new InvalidInputExeption("Not a User");
        }
        int UID = val.indexOf("UID:");
        int BAL = val.indexOf("BAL:");
        int USN = val.indexOf("USN:");
        int PWD = val.indexOf("PWD:");
        int END = val.indexOf("END;");
        if (UID==-1|| BAL == -1 || USN == -1 || PWD == -1|| END == -1) {
            throw new InvalidInputExeption("does not contain valid info");
        }
        UUID id = UUID.fromString(val.substring(UID+ 4, BAL));
        double balance = Double.parseDouble(val.substring(BAL+ 4, USN));
        String username = val.substring(USN+5, PWD-1);
        String passwordHash = val.substring(PWD+5, END-1);
        return new User(passwordHash ,id, balance, username);
    }
    public UUID getId() {
        return id;
    }
    public static void main(String[] args) {
        try{
        Key key = new Key();
        User user = new User(UUID.randomUUID(), 3000, "CHeeseLives", "cheesecheesecheesecheese");
        System.out.println(new String(user.exportToBytes()));
        byte[] encode = key.Encrypt(user.exportToBytes());
        user = User.importFromBytes(key.Decrypt(encode));
        System.out.println(user.login("CHeeseLives", "cheesecheesecheesecheese"));
        }
        catch (Exception e) {    
            System.err.println(e);
        }
    }
} 