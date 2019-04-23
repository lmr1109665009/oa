package com.suneee.eas.gateway.security;

import com.suneee.eas.common.encrypt.EncryptUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义密码编码器
 * @user 子华
 * @created 2018/9/12
 */
@Component
public class CustomPwdEncoder implements PasswordEncoder {

	private static ThreadLocal<Boolean> ingorePwd=new ThreadLocal<Boolean>();
	
	public static void setIngore(boolean ingore){
		ingorePwd.set(ingore);
	}

	/**
     * Encode the raw password.
     * Generally, a good encoding algorithm applies a SHA-1 or greater hash combined with an 8-byte or greater randomly
     * generated salt.
     */
    public String encode(CharSequence rawPassword){
    	String pwd=rawPassword.toString();
		try {
			return EncryptUtil.encrypt32MD5(pwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
		
	}

    /**
     * Verify the encoded password obtained from storage matches the submitted raw password after it too is encoded.
     * Returns true if the passwords match, false if they do not.
     * The stored password itself is never decoded.
     *
     * @param rawPassword the raw password to encode and match
     * @param encodedPassword the encoded password from storage to compare with
     * @return true if the raw password, after encoding, matches the encoded password from storage
     */
    public  boolean matches(CharSequence rawPassword, String encodedPassword){
    	if(ingorePwd.get()==null || ingorePwd.get()==false){
    		String enc=this.encode(rawPassword);
        	return enc.equals(encodedPassword);
    	}
    	return true;
    }

	
	

}
