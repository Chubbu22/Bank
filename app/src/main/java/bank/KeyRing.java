package bank;

import java.util.HashMap;
import java.util.UUID;
import java.util.Base64;
import org.json.JSONObject;

public class KeyRing {
    private KeyRing() {

    }
    public static JSONObject getKeys (String keys, Key key ) throws Exception {
        String str = new String(key.Decrypt(Base64.getDecoder().decode(keys)));
        JSONObject dcr = new JSONObject(str);
        JSONObject keyMap = dcr.getJSONObject("keys");
        JSONObject res = new JSONObject();
        res.put("keyMap", keyMap); 
        res.put("key", key);
        return res;
    }
    
    public static JSONObject setKey (JSONObject keys) throws Exception {
        Key key = new Key();
        JSONObject ecr = new JSONObject();
        ecr.put("keys", keys);
        JSONObject res = new JSONObject();
        res.put("keyMap" ,Base64.getEncoder().encodeToString(key.Encrypt(ecr.toString().getBytes())));
        res.put("key", key.toJson());
        return res;
    }
}
