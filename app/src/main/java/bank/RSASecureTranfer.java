package bank;

import java.security.spec.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.*;
import javax.crypto.*;

import org.json.JSONObject;
public class RSASecureTranfer implements SendSecureTransfer{
    private PublicKey publicKey;
    private PrivateKey privateKey;
    RSASecureTranfer() throws NoSuchAlgorithmException {
        KeyPairGenerator generator =  KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
    }
    RSASecureTranfer(byte[] publicKeyBytes, byte[] privateKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec publicKeySpec = new X509EncodedKeySpec(privateKeyBytes);
        KeySpec privateKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        publicKey = keyFactory.generatePublic(publicKeySpec);
        privateKey = keyFactory.generatePrivate(privateKeySpec);
    }
    public static RSASecureTranfer fromJSON(JSONObject keyObject) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return new RSASecureTranfer(Base64.getDecoder().decode(keyObject.getString("publicKey")), Base64.getDecoder().decode(keyObject.getString("privateKey")));
    }
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("privateKey", Base64.getEncoder().encode(privateKey.getEncoded()));
        json.put("publicKey", Base64.getEncoder().encode(publicKey.getEncoded()));
        return json;
    }
    @Override
    public byte[] getDecodedBytes(byte[] toDecode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(toDecode);
    }
    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }
    @Override
    public byte[] getEncodedBytes (byte[] toEncode, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(toEncode);
    }
    public static void main(String[] args) {
        try {
            File f = new File("test.txt");
            byte[] message = "Hello world".getBytes(StandardCharsets.UTF_8);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();

            RSASecureTranfer tranfer = new RSASecureTranfer();
            RSASecureTranfer reciever = new RSASecureTranfer();
            System.out.println(message);
            FileOutputStream fout = new FileOutputStream(f);
            message = tranfer.getEncodedBytes(message, reciever.getPublicKey());
            fout.write(message);
            System.out.println(message);
            System.out.println(message.length);
            fout.close();
            FileInputStream fin = new FileInputStream(f);
            message = fin.readAllBytes();
            System.out.println(message);
            System.out.println(message.length);
            message = reciever.getDecodedBytes(message);
            System.out.println(new String(message, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }

    }
}
