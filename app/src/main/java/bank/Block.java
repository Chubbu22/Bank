package bank;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

public class Block {
    private HashMap<UUID, String> Users = new HashMap<UUID, String>();
    private String Keys;
    private Key bankKey;
    
    private UUID BlockID;
    Block(UUID BlockID) {
        this.BlockID = BlockID;
        JSONObject emptyJsonObject = new JSONObject();
        closeKeys(emptyJsonObject);
        
    }
    Block(UUID BlockID,HashMap<UUID, String> Users, Key bankKey, String keys) {
        this.BlockID = BlockID;
        this.Users = Users;
        this.bankKey = bankKey;
        this.Keys = keys;
        
    }
    Boolean addNewUser(String username,String password) {
        User user = null;
        Key key = null;
        JSONObject keyring = null;
        try {
            keyring = KeyRing.getKeys(Keys, bankKey);
            user = new User(UUID.randomUUID(), 0, username, password);
            key = new Key();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        if (user != null && key  != null&& keyring != null) {
            Users.put(user.getId(), handleEncryption(key ,new String(user.exportToBytes())));
            keyring.getJSONObject("keyMap").put(user.getId().toString(), key.toJson());
            closeKeys(keyring);
            return true;
        }
        return false;
    }
    String handleEncryption (Key key, String toEncrypt) {
        String string = null;
        try{
        string = new String(key.Encrypt(toEncrypt.getBytes()));
        } catch (Exception e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return string;
    }
    String handleDecrytpion (Key key, String toDecrypt) {
        String string = null;
        try{
        string = new String(key.Decrypt(toDecrypt.getBytes()));
        } catch (Exception e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return string;
    }
    private void closeKeys (JSONObject keyring) {
        try {keyring = KeyRing.setKey(keyring.getJSONObject("keyMap"));
            bankKey = Key.fromJSON( keyring.getJSONObject("key"));
            Keys = keyring.getString("keyMap"); 
        }
            catch (Exception e) {
                System.exit(1);
            }
    }

}
