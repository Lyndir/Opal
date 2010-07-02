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
package com.lyndir.lhunath.lib.security.gpg;

import com.lyndir.lhunath.lib.system.BaseConfig;
import com.lyndir.lhunath.lib.system.util.Utils;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <h2>{@link GPG} - [in short] (TODO).</h2> <p> [description / usage]. </p> <p> <i>Jan 8, 2008</i> </p>
 *
 * @author mbillemo
 */
public class GPG {

    private static final Logger logger = LoggerFactory.getLogger( GPG.class );

    static {
        Security.addProvider( new BouncyCastleProvider() );
    }

    /**
     * Parse a hexadecimal key Id into a wrapped long.
     *
     * @param keyId The ID to convert.
     *
     * @return The long that represents the key ID.
     */
    public static long parseKeyId(final String keyId) {

        String trimmedKeyId = keyId.startsWith( "0x" )? keyId.substring( 2 ): keyId;
        long firstChar = Long.decode( "0x" + trimmedKeyId.substring( 0, 1 ) );
        long otherChars = Long.decode( "0x" + trimmedKeyId.substring( 1 ) );

        double doubleKeyId = firstChar * Math.pow( 16, trimmedKeyId.length() - 1 ) + otherChars;
        while (doubleKeyId > Long.MAX_VALUE)
            doubleKeyId -= (double) Long.MAX_VALUE - (double) Long.MIN_VALUE;

        return (long) doubleKeyId;
    }

    /**
     * @param publicKeyFile The file that contains the public key.
     * @param publicKeyId   The ID of the key to retrieve from the file.
     *
     * @return a public key from file.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws PGPException
     */
    public static PGPPublicKey getPublicKey(final File publicKeyFile, final long publicKeyId)
            throws IOException, PGPException {

        PGPPublicKeyRing publicKeyRing = new PGPPublicKeyRing( new FileInputStream( publicKeyFile ) );
        return publicKeyRing.getPublicKey( publicKeyId );
    }

    /**
     * @param privateKeyFile The file that contains the private key.
     * @param privateKeyId   The ID of the key to retrieve from the file.
     *
     * @return a private key from file.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws PGPException
     */
    public static PGPSecretKey getPrivateKey(final File privateKeyFile, final long privateKeyId)
            throws IOException, PGPException {

        PGPSecretKeyRingCollection privateKeyRing = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream( new FileInputStream( privateKeyFile ) ) );

        return privateKeyRing.getSecretKey( privateKeyId );
    }

    /**
     * @param encryptedFile  The file to decrypt.
     * @param privateKeyFile The file that contains the private key that can decrypt the file.
     *
     * @return a private key required to decrypt the given file from file.
     *
     * @throws IOException
     * @throws PGPException
     */
    public static PGPSecretKey getPrivateKeyFor(final File encryptedFile, final File privateKeyFile)
            throws IOException, PGPException {

        return getPrivateKeyFor( new FileInputStream( encryptedFile ), privateKeyFile );
    }

    /**
     * @param encryptedString The string that can be decrypted with the private key.
     * @param privateKeyFile  The file that contains the private key that can decrypt the string.
     *
     * @return a private key required to decrypt the given string from file.
     *
     * @throws IOException
     * @throws PGPException
     */
    public static PGPSecretKey getPrivateKeyFor(final String encryptedString, final File privateKeyFile)
            throws IOException, PGPException {

        return getPrivateKeyFor( new ByteArrayInputStream( encryptedString.getBytes() ), privateKeyFile );
    }

    /**
     * @param encryptedStream The stream of data that can be decrypted with the private key.
     * @param privateKeyFile  The file that contains the private key that can decrypt the stream data.
     *
     * @return a private key required to decrypt the given stream from file.
     *
     * @throws IOException
     * @throws PGPException
     */
    public static PGPSecretKey getPrivateKeyFor(final InputStream encryptedStream, final File privateKeyFile)
            throws IOException, PGPException {

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
        while (privateKey == null && encryptedDataIterator.hasNext()) {
            PGPPublicKeyEncryptedData encryptedData = encryptedDataIterator.next();
            privateKey = getPrivateKey( privateKeyFile, encryptedData.getKeyID() );
        }

        return privateKey;
    }

    /**
     * @param privateKeyFile The file that contains the private keys.
     *
     * @return all master key IDs available in the given key ring.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws PGPException
     */
    public static List<PrintableKeyWrapper<PGPSecretKey>> getPrivateKeys(final File privateKeyFile)
            throws IOException, PGPException {

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
     * @param publicKeyFile The file that contains the public keys.
     *
     * @return all master key IDs available in the given key ring.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws PGPException
     */
    public static List<PrintableKeyWrapper<PGPPublicKey>> getPublicKeys(final File publicKeyFile)
            throws IOException, PGPException {

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
     *
     * @param plainFile     The file that contains the plain-text data.
     * @param encryptedFile The file to write encrypted data into.
     * @param publicKey     The public key to use for encryption.
     * @param armoured      <code>true</code>: ASCII armor the encrypted data.
     *
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws PGPException
     */
    public static void encryptFile(File plainFile, final File encryptedFile, final PGPPublicKey publicKey, boolean armoured)
            throws NoSuchProviderException, IOException, PGPException {

        InputStream encryptedInputStream = encrypt( new FileInputStream( plainFile ), publicKey, armoured );
        OutputStream encryptedOutputStream = new FileOutputStream( encryptedFile );

        Utils.pipeStream( encryptedInputStream, encryptedOutputStream );
    }

    /**
     * PGP Encrypt a string.
     *
     * @param plainTextData The plain-text data.
     * @param publicKey     The public key to use for encryption.
     * @param armoured      <code>true</code>: ASCII armor the encrypted data.
     *
     * @return The encrypted string data.
     *
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws PGPException
     */
    public static byte[] encrypt(final byte[] plainTextData, final PGPPublicKey publicKey, final boolean armoured)
            throws NoSuchProviderException, IOException, PGPException {

        return Utils.readStream( encrypt( new ByteArrayInputStream( plainTextData ), publicKey, armoured ) );
    }

    /**
     * PGP Encrypt a stream.
     *
     * @param plainTextStream The stream that contains the plain-text data.
     * @param publicKey       The public key to use for encryption.
     * @param armoured        <code>true</code>: ASCII armor the encrypted data.
     *
     * @return The encrypted data stream.
     *
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws PGPException
     */
    public static InputStream encrypt(InputStream plainTextStream, final PGPPublicKey publicKey, boolean armoured)
            throws IOException, NoSuchProviderException, PGPException {

        /* Compress and extract literal data packets that can be encrypted. */
        PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
        ByteArrayOutputStream decryptedStream = new ByteArrayOutputStream();
        PGPCompressedDataGenerator compressor = new PGPCompressedDataGenerator( CompressionAlgorithmTags.ZLIB );
        OutputStream literalStream = literalDataGenerator.open( compressor.open( decryptedStream ), PGPLiteralData.BINARY, "", new Date(),
                                                                new byte[BaseConfig.BUFFER_SIZE] );
        Utils.pipeStream( plainTextStream, literalStream );
        compressor.close();

        /* Encrypt compressed data. */
        PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator( SymmetricKeyAlgorithmTags.CAST5,
                                                                                          new SecureRandom(),
                                                                                          BouncyCastleProvider.PROVIDER_NAME );
        encryptedDataGenerator.addMethod( publicKey );

        /* Create the encrypted output stream, armour if necessary. */
        ByteArrayOutputStream encryptedByteStream = new ByteArrayOutputStream();
        OutputStream encryptedStream = encryptedByteStream;
        if (armoured)
            encryptedStream = new ArmoredOutputStream( encryptedStream );

        /* Create and write out the encrypted file. */
        OutputStream encryptionStream = encryptedDataGenerator.open( encryptedStream, new byte[BaseConfig.BUFFER_SIZE] );
        Utils.pipeStream( new ByteArrayInputStream( decryptedStream.toByteArray() ), encryptionStream );
        encryptedDataGenerator.close();

        return new ByteArrayInputStream( encryptedByteStream.toByteArray() );
    }

    /**
     * Decrypt a PGP encrypted file.
     *
     * @param encryptedFile The file that contains the encrypted data.
     * @param plainTextFile The file to write the plain-text data into.
     * @param privateKey    The private key to use for decrypting the data.
     * @param passPhrase    The passphrase the private key is encrypted with.
     *
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws PGPException
     */
    public static void decryptFile(File encryptedFile, final File plainTextFile, final PGPSecretKey privateKey, String passPhrase)
            throws NoSuchProviderException, IOException, PGPException {

        InputStream decryptedInputStream = decrypt( new FileInputStream( encryptedFile ), privateKey, passPhrase );
        FileOutputStream decryptedOutputStream = new FileOutputStream( plainTextFile );

        Utils.pipeStream( decryptedInputStream, decryptedOutputStream );
    }

    /**
     * Decrypt a PGP encrypted string.
     *
     * @param encryptedData The string that contains the encrypted data.
     * @param privateKey    The private key to use for decrypting the data.
     * @param passPhrase    The passphrase the private key is encrypted with.
     *
     * @return The plain-text string.
     *
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws PGPException
     */
    public static byte[] decrypt(final byte[] encryptedData, final PGPSecretKey privateKey, final String passPhrase)
            throws NoSuchProviderException, IOException, PGPException {

        return Utils.readStream( decrypt( new ByteArrayInputStream( encryptedData ), privateKey, passPhrase ) );
    }

    /**
     * Decrypt a PGP encrypted stream.
     *
     * @param encryptedStream The stream that contains the encrypted data.
     * @param privateKey      The private key to use for decrypting the data.
     * @param passPhrase      The passphrase the private key is encrypted with.
     *
     * @return The plain-text stream.
     *
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws PGPException
     */
    public static InputStream decrypt(InputStream encryptedStream, final PGPSecretKey privateKey, String passPhrase)
            throws IOException, PGPException, NoSuchProviderException {

        /* Open the encrypted file. */
        InputStream encryptedDataStream = PGPUtil.getDecoderStream( encryptedStream );
        PGPObjectFactory encryptedDataFactory = new PGPObjectFactory( encryptedDataStream );

        /* Find the PGP encrypted data. */
        Object encryptedDataObjects = null;
        do
            try {
                encryptedDataObjects = encryptedDataFactory.nextObject();
            }
            catch (IOException e) {
                logger.warn( e.getMessage() );
            }
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
        InputStream unencryptedStream = encryptedData.getDataStream(
                privateKey.extractPrivateKey( passPhrase.toCharArray(), BouncyCastleProvider.PROVIDER_NAME ),
                BouncyCastleProvider.PROVIDER_NAME );
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
            throw new PGPException( "Message type unrecognized: " + unencryptedObject.getClass() );

        /* Write out decrypted data. */
        PGPLiteralData unencryptedData = (PGPLiteralData) unencryptedObject;
        return unencryptedData.getInputStream();
    }

    /**
     * PGP sign a file.
     *
     * @param dataFile   The file that contains the data to sign.
     * @param signedFile The file to write the signature into.
     * @param privateKey The private key to use for signing.
     * @param passPhrase The passphrase that the private key is locked with.
     * @param armoured   <code>true</code>: ASCII armor the signature.
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws FileNotFoundException
     * @throws PGPException
     * @throws IOException
     */
    public static void signFile(File dataFile, final File signedFile, final PGPSecretKey privateKey, String passPhrase,
                                final boolean armoured)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, PGPException, IOException {

        InputStream decryptedInputStream = sign( new FileInputStream( dataFile ), privateKey, passPhrase, armoured );
        FileOutputStream decryptedOutputStream = new FileOutputStream( signedFile );

        Utils.pipeStream( decryptedInputStream, decryptedOutputStream );
    }

    /**
     * PGP sign some data.
     *
     * @param data       The string that contains the data to sign.
     * @param privateKey The private key to use for signing.
     * @param passPhrase The passphrase that the private key is locked with.
     * @param armoured   <code>true</code>: ASCII armor the signature.
     *
     * @return The signature.
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws FileNotFoundException
     * @throws PGPException
     * @throws IOException
     */
    public static byte[] sign(byte[] data, final PGPSecretKey privateKey, final String passPhrase, boolean armoured)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException, PGPException {

        return Utils.readStream( sign( new ByteArrayInputStream( data ), privateKey, passPhrase, armoured ) );
    }

    /**
     * PGP sign a stream.
     *
     * @param data       The stream that contains the data to sign.
     * @param privateKey The private key to use for signing.
     * @param passPhrase The passphrase that the private key is locked with.
     * @param armoured   <code>true</code>: ASCII armor the signature.
     *
     * @return The signature.
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws FileNotFoundException
     * @throws PGPException
     * @throws IOException
     */
    public static InputStream sign(InputStream data, final PGPSecretKey privateKey, final String passPhrase, boolean armoured)
            throws NoSuchAlgorithmException, NoSuchProviderException, PGPException, SignatureException, IOException {

        /* Build the signature generator. */
        PGPSignatureGenerator signer = new PGPSignatureGenerator( privateKey.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1,
                                                                  BouncyCastleProvider.PROVIDER_NAME );
        signer.initSign( PGPSignature.BINARY_DOCUMENT,
                         privateKey.extractPrivateKey( passPhrase.toCharArray(), BouncyCastleProvider.PROVIDER_NAME ) );

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

    /**
     * <h2>{@link PrintableKeyWrapper}<br> <sub>A wrapper for wrapping a key id with a printable representation of it.</sub></h2>
     *
     * @author mbillemo
     * @param <K> The type of object to use for representing the key id.
     *
     * <p> <i>Apr 9, 2008</i> </p>
     */
    private static class PrintableKeyWrapper<K> {

        private final K key;
        private final Long keyId;

        /**
         * Create a new {@link PrintableKeyWrapper} instance.
         *
         * @param key   The object to use for representing the key id.
         * @param keyId The key id to wrap.
         */
        PrintableKeyWrapper(final K key, final Long keyId) {

            this.key = key;
            this.keyId = keyId;
        }

        /**
         * @return The key of this {@link PrintableKeyWrapper}.
         */
        public K getKey() {

            return key;
        }

        /**
         * @return The keyId of this {@link PrintableKeyWrapper}.
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
