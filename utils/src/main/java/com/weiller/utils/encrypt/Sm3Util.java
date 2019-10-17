package com.weiller.utils.encrypt;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.security.Security;

public class Sm3Util {

	/**
	 * sm3加密，不使用秘钥
	 * @param srcData
	 * @return
	 */
	private static byte[] hash(byte[] srcData) {
		registerProvider();
		SM3Digest sm3Digest = new SM3Digest();
		sm3Digest.update(srcData, 0, srcData.length);
		byte[] hash = new byte[sm3Digest.getDigestSize()];
		sm3Digest.doFinal(hash, 0);
		return hash;
	}
	
	/**
	 * sm3加密，使用秘钥key
	 * @param key
	 * @param srcData
	 * @return
	 */
	private static byte[] hash(byte[] key,byte[] srcData){
		registerProvider();
		KeyParameter kp = new KeyParameter(key);
		SM3Digest sm3Digest = new SM3Digest();
		HMac hmac = new HMac(sm3Digest);
		hmac.init(kp);
		hmac.update(srcData, 0, srcData.length);
		byte[] hash = new byte[hmac.getMacSize()];
		hmac.doFinal(hash, 0);
		return hash;
	}
	
	/**
	 * 使用Security注册Provider
	 */
	private static void registerProvider() {
		if (!isRegistered()) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	private static boolean isRegistered() {
		return Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) != null;
	}
	
	public static String encodePassword(String rawPass, Object salt) {
		if(salt != null && salt instanceof String){
			return Hex.toHexString(hash(((String)salt).getBytes(),rawPass.getBytes()));
		}
		return Hex.toHexString(hash(rawPass.getBytes()));
	}

	public static boolean isPasswordValid(String encPass, String rawPass, Object salt) {
		return encPass.equals(encodePassword(rawPass, salt));
	}
}
