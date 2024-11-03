package bank;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.json.JSONObject;

import com.google.common.base.Utf8;

import javax.crypto.Cipher;
import java.util.Base64;
import java.util.List;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import netscape.javascript.JSObject;

public class Key {
    private SecretKey key;
    private byte[] iv;
    private String method;
    private String algorithm;
    Key() throws Exception{
        this("AES", "AES/CBC/PKCS5Padding");
        
    }
    Key(SecretKey key, byte[] iv, String method, String algorithm) {
        this.key = key; 
        this.iv = iv;
        this.method = method;
        this.algorithm = algorithm;
    }
    Key(String method,String algorithm)  throws Exception {
        KeyGenerator gen = KeyGenerator.getInstance(method);
        gen.init(256);
        key = gen.generateKey();
        iv = GenerateNewIV();
        this.method = method;
        this.algorithm = algorithm;

    }
    static byte[] GenerateNewIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    byte[] Encrypt(byte[] toEncrypt) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(toEncrypt);
    }
    byte[] Decrypt(byte[] Encryted) throws Exception{
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(Encryted);
    }   
    static Key fromJSON( JSONObject JSONkey) {
        System.out.println("iv");
        String ivString =  JSONkey.getString("iv");
        byte[] iv = Base64.getDecoder().decode(ivString);
        
        System.out.println("method");
        String method = JSONkey.getString("method");
        String algorithm = JSONkey.getString("algorithm");
        System.out.println("key");
        SecretKey key = (SecretKey) new  SecretKeySpec(Base64.getDecoder().decode(JSONkey.getString("key")), method);
        return new Key(key, iv, method,algorithm);
    }
    public JSONObject toJson() {
        JSONObject res = new JSONObject();
        res.put("key", Base64.getEncoder().encodeToString(key.getEncoded()));
        res.put("iv", Base64.getEncoder().encodeToString(iv));
        res.put("method", method);
        res.put("algorithm", algorithm);
        return res;
    }
    // public static void main(String[] args) {
    //     try {
    //         byte[] message = "hello world".getBytes();
    //         Key key = new Key();
    //         System.out.println(new String(message));
    //         message = key.Encrypt(message);
    //         System.out.println(new String(message));
    //         key = fromJSON(new JSONObject(key.toJson().toString()));
    //         message = key.Decrypt(message);
    //         System.out.println(new String(message));
    //     } catch (Exception e) {
    //         System.err.println(e.getMessage());
    //     }
    // }
}
