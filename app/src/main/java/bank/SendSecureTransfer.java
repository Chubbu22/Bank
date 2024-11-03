package bank;
import java.security.PublicKey;
public interface SendSecureTransfer {
    byte[] getEncodedBytes (byte[] toEncode, PublicKey publicKey) throws Exception;
    byte[] getDecodedBytes(byte[] toDecode) throws Exception;
    PublicKey getPublicKey();
}
