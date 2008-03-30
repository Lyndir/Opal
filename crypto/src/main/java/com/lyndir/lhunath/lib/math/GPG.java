/*
 *   Copyright 2008, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.lib.math;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPUtil;

import com.lyndir.lhunath.lib.system.BaseConfig;
import com.lyndir.lhunath.lib.system.Utils;

/**
 * <h2>{@link GPG} - [in short] (TODO).</h2>
 * <p>
 * [description / usage].
 * </p>
 * <p>
 * <i>Jan 8, 2008</i>
 * </p>
 * 
 * @author mbillemo
 */
public class GPG {

    static {
        Security.addProvider( new BouncyCastleProvider() );
    }

    /**
     * Parse a hexadecimal key Id into a wrapped long.
     */
    public static long parseKeyId(String keyId) {

        keyId = keyId.startsWith( "0x" ) ? keyId.substring( 2 ) : keyId;
        long firstChar = Long.decode( "0x" + keyId.substring( 0, 1 ) );
        long otherChars = Long.decode( "0x" + keyId.substring( 1 ) );

        double doubleKeyId = firstChar * Math.pow( 16, keyId.length() - 1 ) + otherChars;
        while (doubleKeyId > Long.MAX_VALUE)
            doubleKeyId -= (double) Long.MAX_VALUE - (double) Long.MIN_VALUE;

        return (long) doubleKeyId;
    }

    /**
     * Load a public key from file.
     */
    public static PGPPublicKey getPublicKey(File publicKeyFile, long publicKeyId) throws FileNotFoundException,
            IOException, PGPException {

        PGPPublicKeyRing publicKeyRing = new PGPPublicKeyRing( new FileInputStream( publicKeyFile ) );
        return publicKeyRing.getPublicKey( publicKeyId );
    }

    /**
     * Load a private key from file.
     */
    public static PGPSecretKey getPrivateKey(File privateKeyFile, long privateKeyId) throws FileNotFoundException,
            IOException, PGPException {

        PGPSecretKeyRingCollection privateKeyRing = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream( new FileInputStream( privateKeyFile ) ) );

        return privateKeyRing.getSecretKey( privateKeyId );
    }

    /**
     * Load a private key required to decrypt the given file from file.
     */
    public static PGPSecretKey getPrivateKeyFor(File encryptedFile, File privateKeyFile) throws IOException,
            PGPException {

        return getPrivateKeyFor( new FileInputStream( encryptedFile ), privateKeyFile );
    }

    /**
     * Load a private key required to decrypt the given string from file.
     */
    public static PGPSecretKey getPrivateKeyFor(String encryptedString, File privateKeyFile) throws IOException,
            PGPException {

        return getPrivateKeyFor( new ByteArrayInputStream( encryptedString.getBytes() ), privateKeyFile );
    }

    /**
     * Load a private key required to decrypt the given stream from file.
     */
    public static PGPSecretKey getPrivateKeyFor(InputStream encryptedStream, File privateKeyFile) throws IOException,
            PGPException {

        /* Open the encrypted file. */
        InputStream encryptedDataStream = PGPUtil.getDecoderStream( encryptedStream );
        PGPObjectFactory encryptedDataFactory = new PGPObjectFactory( encryptedDataStream );

        /* The first object might be a PGP marker packet. */
        Object encryptedDataObjects = encryptedDataFactory.nextObject();
        if (!(encryptedDataObjects instanceof PGPEncryptedDataList))
            encryptedDataObjects = encryptedDataFactory.nextObject();
        @SuppressWarnings("unchecked")
        Iterator<PGPPublicKeyEncryptedData> encryptedDataIterator = ((PGPEncryptedDataList) encryptedDataObjects).getEncryptedDataObjects();

        /* Extract the public key out of the data and find the matching private key required to decrypt the data. */
        PGPSecretKey privateKey = null;
        PGPPublicKeyEncryptedData encryptedData = null;
        while (privateKey == null && encryptedDataIterator.hasNext()) {
            encryptedData = encryptedDataIterator.next();
            privateKey = getPrivateKey( privateKeyFile, encryptedData.getKeyID() );
        }

        return privateKey;
    }

    /**
     * Retrieve all master key IDs available in the given key ring.
     */
    public static List<PrintableKeyWrapper<PGPSecretKey>> getPrivateKeys(File privateKeyFile)
            throws FileNotFoundException, IOException, PGPException {

        /* Open the key ring. */
        List<PrintableKeyWrapper<PGPSecretKey>> keys = new ArrayList<PrintableKeyWrapper<PGPSecretKey>>();
        PGPSecretKeyRingCollection privateKeyRing = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream( new FileInputStream( privateKeyFile ) ) );

        /* Enumerate the IDs. */
        @SuppressWarnings("unchecked")
        Iterator<PGPSecretKeyRing> rings = privateKeyRing.getKeyRings();
        while (rings.hasNext()) {
            @SuppressWarnings("unchecked")
            Iterator<PGPSecretKey> ring = rings.next().getSecretKeys();
            while (ring.hasNext()) {
                PGPSecretKey key = ring.next();
                if (!key.getUserIDs().hasNext())
                    continue;

                keys.add( new PrintableKeyWrapper<PGPSecretKey>( key, key.getKeyID() ) {

                    @Override
                    public String toString() {

                        return getKey().getUserIDs().next().toString();
                    }
                } );
            }
        }

        return keys;
    }
    /**
     * Retrieve all master key IDs available in the given key ring.
     */
    public static List<PrintableKeyWrapper<PGPPublicKey>> getPublicKeys(File publicKeyFile)
            throws FileNotFoundException, IOException, PGPException {

        /* Open the key ring. */
        List<PrintableKeyWrapper<PGPPublicKey>> keys = new ArrayList<PrintableKeyWrapper<PGPPublicKey>>();
        PGPPublicKeyRingCollection privateKeyRing = new PGPPublicKeyRingCollection(
                PGPUtil.getDecoderStream( new FileInputStream( publicKeyFile ) ) );

        /* Enumerate the IDs. */
        @SuppressWarnings("unchecked")
        Iterator<PGPPublicKeyRing> rings = privateKeyRing.getKeyRings();
        while (rings.hasNext()) {
            @SuppressWarnings("unchecked")
            Iterator<PGPPublicKey> ring = rings.next().getPublicKeys();
            while (ring.hasNext()) {
                PGPPublicKey key = ring.next();
                if (!key.getUserIDs().hasNext())
                    continue;

                keys.add( new PrintableKeyWrapper<PGPPublicKey>( key, key.getKeyID() ) {

                    @Override
                    public String toString() {

                        return getKey().getUserIDs().next().toString();
                    }
                } );
            }
        }

        return keys;
    }
    /**
     * PGP Encrypt a file.
     */
    public static void encryptFile(File decryptedFile, File encryptedFile, PGPPublicKey publicKey, boolean armoured)
            throws NoSuchProviderException, IOException, PGPException {

        InputStream encryptedInputStream = encrypt( new FileInputStream( decryptedFile ), publicKey, armoured );
        OutputStream encryptedOutputStream = new FileOutputStream( encryptedFile );

        Utils.pipeStream( encryptedInputStream, encryptedOutputStream );
    }

    /**
     * PGP Encrypt a string.
     */
    public static String encrypt(String decryptedString, PGPPublicKey publicKey, boolean armoured)
            throws NoSuchProviderException, IOException, PGPException {

        return Utils.readStream( encrypt( new ByteArrayInputStream( decryptedString.getBytes() ), publicKey, armoured ) );
    }

    /**
     * PGP Encrypt a stream.
     */
    public static InputStream encrypt(InputStream decryptedData, PGPPublicKey publicKey, boolean armoured)
            throws IOException, NoSuchProviderException, PGPException {

        /* Compress and extract literal data packets that can be encrypted. */
        PGPLiteralDataGenerator literator = new PGPLiteralDataGenerator();
        ByteArrayOutputStream decryptedStream = new ByteArrayOutputStream();
        PGPCompressedDataGenerator compressor = new PGPCompressedDataGenerator( CompressionAlgorithmTags.ZLIB );
        OutputStream literalStream = literator.open( compressor.open( decryptedStream ), PGPLiteralData.BINARY, "",
                new Date(), new byte[BaseConfig.BUFFER_SIZE] );
        Utils.pipeStream( decryptedData, literalStream );
        compressor.close();

        /* Encrypt compressed data. */
        PGPEncryptedDataGenerator encryptor = new PGPEncryptedDataGenerator( SymmetricKeyAlgorithmTags.CAST5,
                new SecureRandom(), BouncyCastleProvider.PROVIDER_NAME );
        encryptor.addMethod( publicKey );

        /* Create the encrypted output stream, armour if necessary. */
        ByteArrayOutputStream encryptedByteStream = new ByteArrayOutputStream();
        OutputStream encryptedStream = encryptedByteStream;
        if (armoured)
            encryptedStream = new ArmoredOutputStream( encryptedStream );

        /* Create and write out the encrypted file. */
        OutputStream encryptionStream = encryptor.open( encryptedStream, new byte[BaseConfig.BUFFER_SIZE] );
        Utils.pipeStream( new ByteArrayInputStream( decryptedStream.toByteArray() ), encryptionStream );
        encryptor.close();

        return new ByteArrayInputStream( encryptedByteStream.toByteArray() );
    }

    /**
     * Decrypt a PGP encrypted file.
     */
    public static void decryptFile(File encryptedFile, File decryptedFile, PGPSecretKey privateKey, String passPhrase)
            throws NoSuchProviderException, IOException, PGPException {

        InputStream decryptedInputStream = decrypt( new FileInputStream( encryptedFile ), privateKey, passPhrase );
        FileOutputStream decryptedOutputStream = new FileOutputStream( decryptedFile );

        Utils.pipeStream( decryptedInputStream, decryptedOutputStream );
    }

    /**
     * Decrypt a PGP encrypted string.
     */
    public static String decrypt(String encryptedString, PGPSecretKey privateKey, String passPhrase)
            throws NoSuchProviderException, IOException, PGPException {

        return Utils.readStream( decrypt( new ByteArrayInputStream( encryptedString.getBytes() ), privateKey,
                passPhrase ) );
    }

    /**
     * Decrypt a PGP encrypted stream.
     */
    public static InputStream decrypt(InputStream encryptedStream, PGPSecretKey privateKey, String passPhrase)
            throws IOException, PGPException, NoSuchProviderException {

        /* Open the encrypted file. */
        InputStream encryptedDataStream = PGPUtil.getDecoderStream( encryptedStream );
        PGPObjectFactory encryptedDataFactory = new PGPObjectFactory( encryptedDataStream );

        /* Find the PGP encrypted data. */
        Object encryptedDataObjects = null;
        do
            encryptedDataObjects = encryptedDataFactory.nextObject();
        while (!(encryptedDataObjects instanceof PGPEncryptedDataList) && encryptedDataObjects != null);
        if (encryptedDataObjects == null)
            throw new PGPException( "No encrypted objects found." );

        @SuppressWarnings("unchecked")
        Iterator<PGPPublicKeyEncryptedData> encryptedDataIterator = ((PGPEncryptedDataList) encryptedDataObjects).getEncryptedDataObjects();

        /* Extract the public key out of the data and find the matching private key required to decrypt the data. */
        PGPPublicKeyEncryptedData encryptedData = null;
        while (encryptedDataIterator.hasNext()) {
            encryptedData = encryptedDataIterator.next();
            if (encryptedData.getKeyID() == privateKey.getKeyID())
                break;
        }
        if (encryptedData == null)
            throw new PGPException( "No encrypted data found." );

        /* Decrypt the data. */
        InputStream unencryptedStream = encryptedData.getDataStream( privateKey.extractPrivateKey(
                passPhrase.toCharArray(), BouncyCastleProvider.PROVIDER_NAME ), BouncyCastleProvider.PROVIDER_NAME );
        PGPObjectFactory pgpFactory = new PGPObjectFactory( unencryptedStream );
        Object unencryptedObject = pgpFactory.nextObject();

        /* Possibly decompress the decrypted data. */
        if (unencryptedObject instanceof PGPCompressedData) {
            PGPCompressedData compressedData = (PGPCompressedData) unencryptedObject;
            pgpFactory = new PGPObjectFactory( compressedData.getDataStream() );
            unencryptedObject = pgpFactory.nextObject();
        }

        /* Verify integrity. */
        if (encryptedData.isIntegrityProtected() && !encryptedData.verify())
            throw new PGPException( "Message integrity check failed." );

        /* Check to see if the data is valid decrypted data. */
        if (unencryptedObject == null)
            throw new PGPException( "No encrypted data found." );
        if (unencryptedObject instanceof PGPOnePassSignatureList)
            throw new PGPException( "Encrypted data is a signature, not an encrypted message." );
        else if (!(unencryptedObject instanceof PGPLiteralData))
            throw new PGPException( "Message type unrecougnized: " + unencryptedObject.getClass() );

        /* Write out decrypted data. */
        PGPLiteralData unencryptedData = (PGPLiteralData) unencryptedObject;
        return unencryptedData.getInputStream();
    }

    /**
     * PGP sign a file.
     */
    public static void signFile(File dataFile, File signedFile, PGPSecretKey privateKey, String passPhrase,
            boolean armoured) throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException,
            FileNotFoundException, PGPException, IOException {

        InputStream decryptedInputStream = sign( new FileInputStream( dataFile ), privateKey, passPhrase, armoured );
        FileOutputStream decryptedOutputStream = new FileOutputStream( signedFile );

        Utils.pipeStream( decryptedInputStream, decryptedOutputStream );
    }

    /**
     * PGP sign a string.
     */
    public static String sign(String data, PGPSecretKey privateKey, String passPhrase, boolean armoured)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException, PGPException {

        return Utils.readStream( sign( new ByteArrayInputStream( data.getBytes() ), privateKey, passPhrase, armoured ) );
    }

    /**
     * PGP sign a stream.
     */
    public static InputStream sign(InputStream data, PGPSecretKey privateKey, String passPhrase, boolean armoured)
            throws NoSuchAlgorithmException, NoSuchProviderException, PGPException, SignatureException, IOException {

        /* Build the signature generator. */
        PGPSignatureGenerator signer = new PGPSignatureGenerator( privateKey.getPublicKey().getAlgorithm(),
                HashAlgorithmTags.SHA1, BouncyCastleProvider.PROVIDER_NAME );
        signer.initSign( PGPSignature.BINARY_DOCUMENT, privateKey.extractPrivateKey( passPhrase.toCharArray(),
                BouncyCastleProvider.PROVIDER_NAME ) );

        /* Write the data into the generator. */
        byte[] buffer = new byte[BaseConfig.BUFFER_SIZE];
        for (int read; (read = data.read( buffer )) >= 0;)
            signer.update( buffer, 0, read );

        /* Create the signature output stream, armour if necessary. */
        ByteArrayOutputStream signatureByteStream = new ByteArrayOutputStream();
        OutputStream signatureStream = signatureByteStream;
        if (armoured)
            signatureStream = new ArmoredOutputStream( signatureStream );

        /* Create and write out the signature. */
        PGPSignature signature = signer.generate();
        signature.encode( signatureStream );

        return new ByteArrayInputStream( signatureByteStream.toByteArray() );
    }

    public static class PrintableKeyWrapper<K> {

        private K    key;
        private Long keyId;

        /**
         * Create a new {@link GPG.PrintableKeyWrapper} instance.
         */
        public PrintableKeyWrapper(K key, Long keyId) {

            this.key = key;
            this.keyId = keyId;
        }

        /**
         * @return The key of this {@link GPG.PrintableKeyWrapper}.
         */
        public K getKey() {

            return key;
        }

        /**
         * @return The keyId of this {@link GPG.PrintableKeyWrapper}.
         */
        public Long getKeyID() {

            return keyId;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            return key.toString();
        }
    }
}
