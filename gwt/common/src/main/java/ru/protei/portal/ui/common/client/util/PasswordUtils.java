package ru.protei.portal.ui.common.client.util;

import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.client.TripleDesCipher;

public class PasswordUtils {
    public static String encrypt(String pwd) {
        try {
            TripleDesCipher cipher = new TripleDesCipher();
            cipher.setKey(CIPHER_KEY);
            return cipher.encrypt(pwd);
        } catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
            return null;
        }
    }

    public static String decrypt(String pwd) {
        try {
            TripleDesCipher cipher = new TripleDesCipher();
            cipher.setKey(CIPHER_KEY);
            return cipher.decrypt(pwd);
        } catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
            return null;
        }
    }

    private static final byte[] CIPHER_KEY = new byte[]{5, 4, 4, 3, 5, 4, 8, 3, 2, 7, 5, 9, 3, 1, 3, 2, 3, 6, 3, 1};
}
