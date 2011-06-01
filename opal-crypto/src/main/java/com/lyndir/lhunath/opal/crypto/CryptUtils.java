package com.lyndir.lhunath.opal.crypto;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Charsets;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.security.*;
import java.util.Formatter;
import java.util.Random;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Base64;
import org.jetbrains.annotations.Nullable;


/**
 * <i>05 30, 2011</i>
 *
 * @author lhunath
 */
public class CryptUtils {

    private static final Logger logger = Logger.get( CryptUtils.class );
    private static final Random random = new SecureRandom();

    /**
     * Encrypt the given data using the given key with AES at 128 bit in ECB.
     *
     * @param plainData  The data to encrypt.
     * @param key        The encryption key to encrypt the data with.
     * @param usePadding Whether to use PKCS5 padding during the encryption.
     *
     * @return The encrypted version of the plain text data as encrypted by the given key.
     *
     * @throws IllegalBlockSizeException {@code usePadding} was {@code false} but the plain text data was not a multiple of the
     *                                   block size.
     */
    public static byte[] encrypt(byte[] plainData, byte[] key, boolean usePadding)
            throws IllegalBlockSizeException {

        try {
            String cipherTransformation = String.format( "AES/ECB/%s", usePadding? "PKCS5Padding": "NoPadding" );
            return doCrypt( plainData, key, cipherTransformation, 128, Cipher.ENCRYPT_MODE );
        }
        catch (BadPaddingException e) {
            throw logger.bug( e, "Should only occur in decryption mode." );
        }
    }

    /**
     * Decrypt the given data using the given key with AES at 128 bit in ECB.
     *
     * @param encryptedData The data to decrypt.
     * @param key           The encryption key to decrypt the data with.
     * @param usePadding    Whether to use PKCS5 padding during the encryption.
     *
     * @return The decrypted version of the encrypted data as decrypted by the given key.
     *
     * @throws BadPaddingException {@code usePadding} was {@code true} but the encrypted data was not padded.
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] key, boolean usePadding)
            throws BadPaddingException {

        try {
            String cipherTransformation = String.format( "AES/ECB/%s", usePadding? "PKCS5Padding": "NoPadding" );
            return doCrypt( encryptedData, key, cipherTransformation, 128, Cipher.DECRYPT_MODE );
        }
        catch (IllegalBlockSizeException e) {
            throw logger.bug( e, "Should only occur in encryption mode." );
        }
    }

    /**
     * Encrypt or decrypt the given data using the given key using the given cipher.
     *
     * @param data                 The data to decrypt.
     * @param key                  The encryption key to decrypt the data with.
     * @param cipherTransformation The cipher to use for performing the operation.
     * @param blockBitSize         The bit-length of the blocks the cipher should operate on.  The key will be trimmed to this size.
     * @param mode                 {@code Cipher.ENCRYPT_MODE}  or {@code Cipher.DECRYPT_MODE}
     *
     * @return The decrypted version of the encrypted data as decrypted by the given key.
     *
     * @throws IllegalBlockSizeException While encrypting without padding, the plain text data's length is not a multiple of the cipher
     *                                   block size.
     * @throws BadPaddingException       While decrypting with padding, the encrypted data was not padded during encryption.
     */
    public static byte[] doCrypt(byte[] data, byte[] key, String cipherTransformation, int blockBitSize, final int mode)
            throws IllegalBlockSizeException, BadPaddingException {

        // Truncate key to the block size.
        int blockByteSize = blockBitSize / Byte.SIZE;
        byte[] blockSizedKey = key;
        if (blockSizedKey.length != blockByteSize) {
            blockSizedKey = new byte[blockByteSize];
            System.arraycopy( key, 0, blockSizedKey, 0, blockByteSize );
        }

        // Encrypt data with key.
        try {
            Cipher cipher = Cipher.getInstance( cipherTransformation );
            cipher.init( mode, new SecretKeySpec( blockSizedKey, "AES" ) );

            return cipher.doFinal( data );
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Cipher transformation: " + cipherTransformation + ", is not valid or not supported by the provider.", e );
        }
        catch (NoSuchPaddingException e) {
            throw new IllegalStateException( "Cipher transformation: " + cipherTransformation
                                             + ", uses a padding scheme that is not valid or not supported by the provider.", e );
        }
        catch (InvalidKeyException e) {
            throw logger.bug( e, "Key is inappropriate for cipher." );
        }
    }

    /**
     * Calculate the byte-by-byte XOR data set from the two given data sets.
     *
     * @param data1 The first data set to XOR with the second.
     * @param data2 The second data set to XOR with the first.
     *
     * @return A data set of bytes where: data[b] = data1[b] ^ data2[b].
     */
    public static byte[] xor(byte[] data1, byte[] data2) {

        checkArgument( data1.length == data2.length, "Cannot XOR two data sets of different length." );

        byte[] data = new byte[data1.length];
        for (int i = 0; i < data1.length; ++i)
            data[i] = (byte) (data1[i] ^ data2[i]);

        return data;
    }

    /**
     * Create a block of random bytes.
     *
     * @param bitSize The bit-length of the block.  Must be a multiple of {@value Byte#SIZE}.
     *
     * @return A random key of the given byte length.
     */
    public static byte[] getRandomBlock(int bitSize) {

        byte[] block = new byte[bitSize / Byte.SIZE];
        random.nextBytes( block );

        return block;
    }

    /**
     * Base64 encode a string.
     *
     * @param plainData The plain data.
     *
     * @return This convenience method returns an empty string when the byte array is {@code null} or empty.
     */
    @Nullable
    public static String toBase64(@Nullable byte[] plainData) {

        if (plainData == null || plainData.length == 0)
            return null;

        return new String( Base64.encode( plainData ), Charsets.UTF_8 );
    }

    /**
     * Base64 decode a string.
     *
     * @param b64Data The base64 encoded data.
     *
     * @return This convenience method returns an empty byte array when the string is {@code null} or empty.
     */
    public static byte[] fromBase64(@Nullable String b64Data) {

        if (b64Data == null || b64Data.length() == 0)
            return new byte[0];

        return Base64.decode( b64Data.getBytes( Charsets.UTF_8 ) );
    }

    public static String toHex(@Nullable byte[] data) {

        return toHex( data, false );
    }

    public static String toHex(@Nullable byte[] data, boolean pretty) {

        StringBuffer bytes = new StringBuffer();
        Formatter formatter = new Formatter( bytes );
        String format = String.format( "%%02X%s", pretty? ":": "" );

        if (data != null)
            for (byte b : data) {
                formatter.format( format, b );
            }
        if (pretty && bytes.length() > 0)
            bytes.deleteCharAt( bytes.length() - 1 );

        return bytes.toString();
    }

    public static byte[] fromHex(@Nullable String hexString) {

        if (hexString == null)
            return new byte[0];

        byte[] deviceToken = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2)
            deviceToken[i / 2] = Integer.valueOf( hexString.substring( i, i + 2 ), 16 ).byteValue();

        return deviceToken;
    }
}
