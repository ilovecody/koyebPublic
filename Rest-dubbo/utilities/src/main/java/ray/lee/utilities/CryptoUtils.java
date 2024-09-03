package ray.lee.utilities;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoUtils {
    private static final String key = "myResiApi";
    
    public static String encrypt(String input) {
        return encrypt(input, key);
    }

    public static String encrypt(String input, String key) {  
    	String output = null;  
        try{
        	//create a Rijndael cipher with 256 bit block size, this is not AES
        	//use a padding method that only works on data that cannot end with zero valued bytes
        	//use ECB mode encryption, which should never be used
        	PaddedBufferedBlockCipher pbbc = new PaddedBufferedBlockCipher(new RijndaelEngine(256), new ZeroBytePadding());
            // initialize the cipher using the key (no need for an IV, this is ECB)
            pbbc.init(true, generateKey(key));
            
            byte[] plaintext = input.getBytes(Charset.forName("UTF8"));
            byte[] ciphertext = new byte[pbbc.getOutputSize(plaintext.length)];
            int offset = 0;
            offset += pbbc.processBytes(plaintext, 0, plaintext.length, ciphertext, offset);
            offset += pbbc.doFinal(ciphertext, offset);
            output =  new String(Base64Utils.encode(ciphertext));  
        }catch(Exception e){  
        	log.debug("CryptoUtils.encrypt() error -- " + e, e);
        }
        return replaceChars(output, "+/=", "-_.");
    }

    public static String decrypt(String input) {
        return decrypt(input, key);
    }
    public static String decrypt(String input, String key){
    	if(!StringUtils.hasText(input)) {
    		log.debug("CryptoUtils.decrypt() input is empty !"); 
    		return "";
    	}
        String output = null;  
        try {
        	byte[] plaintext = Base64Utils.decode(replaceChars(input, "-_.", "+/=").getBytes());
        	//create a Rijndael cipher with 256 bit block size, this is not AES
        	//use a padding method that only works on data that cannot end with zero valued bytes
        	//use ECB mode encryption, which should never be used
        	PaddedBufferedBlockCipher pbbc = new PaddedBufferedBlockCipher(new RijndaelEngine(256), new ZeroBytePadding());
            // initialize the cipher using the key (no need for an IV, this is ECB)
            pbbc.init(false, generateKey(key));
            byte[] decrypted = new byte[plaintext.length];
            int offset = 0;
            offset += pbbc.processBytes(plaintext, 0, plaintext.length, decrypted, offset);
            offset += pbbc.doFinal(decrypted, offset);
            output = new String(decrypted, StandardCharsets.UTF_8.name()).trim();
        }catch(Exception e){
            	log.debug("CryptoUtils.decrypt() error -- " + e, e); 
        }  
        return output;  
    }
    
    private static KeyParameter generateKey(String key) {
    	byte[] givenKey = key.getBytes(Charset.forName("ASCII"));
        final int keysize;
        if (givenKey.length <= 128 / Byte.SIZE) {
            keysize = 128;
        } else if (givenKey.length <= 192 / Byte.SIZE) {
            keysize = 192;
        } else {
            keysize = 256;
        }
        
        // create a 256 bit key by adding zero bytes to the decoded key
        byte[] keyData = new byte[keysize / Byte.SIZE];
        System.arraycopy(givenKey, 0, keyData, 0, Math.min(givenKey.length, keyData.length));
        return new KeyParameter(keyData);
    }
    
    public static String encrypt_MD5(String input) {  
    	String output = null;
    	try {
    		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    		messageDigest.update(input.getBytes("UTF-8"));
    		
    		byte[] bytes = messageDigest.digest();
    		StringBuffer sb = new StringBuffer();
            for(int i=0; i<bytes.length; i++) {
                sb.append(Integer.toHexString(0xFF & bytes[i]));
            }
            output = sb.toString();
    	} catch(Exception e) {
    		log.debug("CryptoUtils.encrypt() error -- " + e, e);
    	}
    	return output;
    }
    
    private static String replaceChars(String str, String searchChars, String replaceChars) {
        if (!StringUtils.hasText(str) || !StringUtils.hasText(searchChars)) {
            return "";
        }
        if (replaceChars == null) {
            replaceChars = "";
        }
        boolean modified = false;
        int replaceCharsLength = replaceChars.length();
        int strLength = str.length();
        StringBuffer buf = new StringBuffer(strLength);
        for (int i = 0; i < strLength; i++) {
            char ch = str.charAt(i);
            int index = searchChars.indexOf(ch);
            if (index >= 0) {
                modified = true;
                if (index < replaceCharsLength) {
                    buf.append(replaceChars.charAt(index));
                }
            } else {
                buf.append(ch);
            }
        }
        if (modified) {
            return buf.toString();
        }
        return str;
    }    
}
