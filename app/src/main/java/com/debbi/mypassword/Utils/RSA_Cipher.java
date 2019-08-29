package com.debbi.mypassword.Utils;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import com.debbi.mypassword.CommonApplication;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

public class RSA_Cipher {

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";


    private static KeyStore.Entry createKeys(Context context) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException, UnrecoverableEntryException {

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        boolean containsAlias = keyStore.containsAlias("com.your.package.name");

        if (!containsAlias) {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            Calendar start = Calendar.getInstance(Locale.ENGLISH);
            Calendar end = Calendar.getInstance(Locale.ENGLISH);
            end.add(Calendar.YEAR, 1);
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias("com.your.package.name")
                    .setSubject(new X500Principal("CN=" + CommonApplication.PackageName))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
            kpg.initialize(spec);
            kpg.generateKeyPair();
        }
        return keyStore.getEntry(CommonApplication.PackageName, null);

    }

    private static byte[] encryptUsingKey(PublicKey publicKey, byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        inCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        Log.d("aaa", "encryptUsingKey = " + inCipher.doFinal(bytes).toString() );
        return inCipher.doFinal(bytes);
    }

    private static byte[] decryptUsingKey(PrivateKey privateKey, byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        inCipher.init(Cipher.DECRYPT_MODE, privateKey);
        Log.d("aaa", "decryptUsingKey = " + inCipher.doFinal(bytes).toString() );
        return inCipher.doFinal(bytes);
    }

    public static String encrypt(Context context, String plainText) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException, NoSuchProviderException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {

        KeyStore.Entry entry = createKeys(context);

        if (entry instanceof KeyStore.PrivateKeyEntry) {

            Log.d("aaa", "encrypt =  instanceof KeyStore.PrivateKeyEntry " );

            Certificate certificate = ((KeyStore.PrivateKeyEntry) entry).getCertificate();
            PublicKey publicKey = certificate.getPublicKey();

            byte[] bytes = plainText.getBytes("UTF-8");
            byte[] encryptedBytes = encryptUsingKey(publicKey, bytes);
            byte[] base64encryptedBytes = Base64.encode(encryptedBytes, Base64.DEFAULT);
            return new String(base64encryptedBytes);
        }
        return null;
    }

    public static String decrypt(String cipherText) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);

        KeyStore.Entry entry = keyStore.getEntry(CommonApplication.PackageName, null);
        if (entry instanceof KeyStore.PrivateKeyEntry) {

            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();

            byte[] bytes = cipherText.getBytes("UTF-8");

            byte[] base64encryptedBytes = Base64.decode(bytes, Base64.DEFAULT);

            byte[] decryptedBytes = decryptUsingKey(privateKey, base64encryptedBytes);

            return new String(decryptedBytes);
        }
        return null;
    }



}
