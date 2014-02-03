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
package com.lyndir.lhunath.opal.crypto.gpg;

import com.google.common.io.ByteStreams;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.*;
import java.security.*;
import java.util.*;
import javax.annotation.Nullable;
import org.bouncycastle.bcpg.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <h2>{@link GPG} - [in short] (TODO).</h2> <p> [description / usage]. </p> <p> <i>Jan 8, 2008</i> </p>
 *
 * @author mbillemo
 */
public abstract class GPG {

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
    @SuppressWarnings({ "StringConcatenationMissingWhitespace", "NumericOverflow" })
    public static long parseKeyId(final String keyId) {

        String trimmedKeyId = keyId.startsWith( "0x" )? keyId.substring( 2 ): keyId;
        long firstChar = Long.decode( "0x" + trimmedKeyId.substring( 0, 1 ) );
        long otherChars = Long.decode( "0x" + trimmedKeyId.substring( 1 ) );

        double doubleKeyId = firstChar * Math.pow( 16, trimmedKeyId.length() - 1 ) + otherChars;
        while (doubleKeyId > Long.MAX_VALUE)
            doubleKeyId -= Long.MAX_VALUE - Long.MIN_VALUE;

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
    @SuppressFBWarnings({ "RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE" })
    public static PGPPublicKey getPublicKey(final File publicKeyFile, final long publicKeyId)
            throws IOException, PGPException {

        try (FileInputStream publicKeyInputStream = new FileInputStream( publicKeyFile )) {
            return new PGPPublicKeyRing( publicKeyInputStream ).getPublicKey( publicKeyId );
        }
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
    @SuppressFBWarnings({ "RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE" })
    public static PGPSecretKey getPrivateKey(final File privateKeyFile, final long privateKeyId)
            throws IOException, PGPException {

        try (FileInputStream privateKeyInputStream = new FileInputStream( privateKeyFile )) {
            PGPSecretKeyRingCollection privateKeyRing = new PGPSecretKeyRingCollection( PGPUtil.getDecoderStream( privateKeyInputStream ) );
            return privateKeyRing.getSecretKey( privateKeyId );
        }
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
    @Nullable
    public static PGPSecretKey getPrivateKeyFor(final File encryptedFile, final File privateKeyFile)
            throws IOException, PGPException {

        try (FileInputStream encryptedFileStream = new FileInputStream( encryptedFile )) {
            return getPrivateKeyFor( encryptedFileStream, privateKeyFile );
        }
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
    @Nullable
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
        try (FileInputStream privateKeyInputStream = new FileInputStream( privateKeyFile )) {
            List<PrintableKeyWrapper<PGPSecretKey>> keys = new ArrayList<>();
            PGPSecretKeyRingCollection privateKeyRing = new PGPSecretKeyRingCollection( PGPUtil.getDecoderStream( privateKeyInputStream ) );

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
        try (FileInputStream publicKeyInputStream = new FileInputStream( publicKeyFile )) {
            List<PrintableKeyWrapper<PGPPublicKey>> keys = new ArrayList<>();
            PGPPublicKeyRingCollection privateKeyRing = new PGPPublicKeyRingCollection( PGPUtil.getDecoderStream( publicKeyInputStream ) );

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
    }

    /**
     * PGP Encrypt a file.
     *
     * @param plainFile     The file that contains the plain-text data.
     * @param encryptedFile The file to write encrypted data into.
     * @param publicKey     The public key to use for encryption.
     * @param armoured      {@code true}: ASCII armor the encrypted data.
     *
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws PGPException
     */
    public static void encryptFile(final File plainFile, final File encryptedFile, final PGPPublicKey publicKey, final boolean armoured)
            throws NoSuchProviderException, IOException, PGPException {

        try (OutputStream encryptedOutputStream = new FileOutputStream( encryptedFile ); InputStream plainInputStream = new FileInputStream(
                plainFile ); InputStream encryptedInputStream = encrypt( plainInputStream, publicKey, armoured )) {
            ByteStreams.copy( encryptedInputStream, encryptedOutputStream );
        }
    }

    /**
     * PGP Encrypt a string.
     *
     * @param plainTextData The plain-text data.
     * @param publicKey     The public key to use for encryption.
     * @param armoured      {@code true}: ASCII armor the encrypted data.
     *
     * @return The encrypted string data.
     *
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws PGPException
     */
    public static byte[] encrypt(final byte[] plainTextData, final PGPPublicKey publicKey, final boolean armoured)
            throws NoSuchProviderException, IOException, PGPException {

        return ByteStreams.toByteArray( encrypt( new ByteArrayInputStream( plainTextData ), publicKey, armoured ) );
    }

    /**
     * PGP Encrypt a stream.
     *
     * @param plainTextStream The stream that contains the plain-text data.
     * @param publicKey       The public key to use for encryption.
     * @param armoured        {@code true}: ASCII armor the encrypted data.
     *
     * @return The encrypted data stream.
     *
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws PGPException
     */
    public static InputStream encrypt(final InputStream plainTextStream, final PGPPublicKey publicKey, final boolean armoured)
            throws IOException, NoSuchProviderException, PGPException {

        /* Compress and extract literal data packets that can be encrypted. */
        PGPEncryptedDataGenerator encryptedDataGenerator = null;
        try (ByteArrayOutputStream decryptedStream = new ByteArrayOutputStream(); ByteArrayOutputStream encryptedByteStream = new ByteArrayOutputStream()) {
            PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
            PGPCompressedDataGenerator compressor = new PGPCompressedDataGenerator( CompressionAlgorithmTags.ZLIB );
            OutputStream literalStream = literalDataGenerator.open( compressor.open( decryptedStream ), PGPLiteralData.BINARY, "",
                                                                    new Date(), new byte[4096] );
            ByteStreams.copy( plainTextStream, literalStream );
            compressor.close();

        /* Encrypt compressed data. */
            encryptedDataGenerator = new PGPEncryptedDataGenerator( SymmetricKeyAlgorithmTags.CAST5, new SecureRandom(),
                                                                    BouncyCastleProvider.PROVIDER_NAME );
            encryptedDataGenerator.addMethod( publicKey );

        /* Create the encrypted output stream, armour if necessary. */
            OutputStream encryptedStream = encryptedByteStream;
            if (armoured)
                encryptedStream = new ArmoredOutputStream( encryptedStream );

        /* Create and write out the encrypted file. */
            OutputStream encryptionStream = encryptedDataGenerator.open( encryptedStream, new byte[4096] );
            ByteStreams.copy( new ByteArrayInputStream( decryptedStream.toByteArray() ), encryptionStream );

            return new ByteArrayInputStream( encryptedByteStream.toByteArray() );
        }
        finally {
            if (encryptedDataGenerator != null)
                encryptedDataGenerator.close();
        }
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
    public static void decryptFile(final File encryptedFile, final File plainTextFile, final PGPSecretKey privateKey,
                                   final String passPhrase)
            throws NoSuchProviderException, IOException, PGPException {

        try (InputStream encryptedInputStream = new FileInputStream( encryptedFile ); InputStream decryptedInputStream = decrypt(
                encryptedInputStream, privateKey, passPhrase ); OutputStream decryptedOutputStream = new FileOutputStream(
                plainTextFile )) {
            ByteStreams.copy( decryptedInputStream, decryptedOutputStream );
        }
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

        return ByteStreams.toByteArray( decrypt( new ByteArrayInputStream( encryptedData ), privateKey, passPhrase ) );
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
    public static InputStream decrypt(final InputStream encryptedStream, final PGPSecretKey privateKey, final String passPhrase)
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
            catch (final IOException e) {
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
        if (!(unencryptedObject instanceof PGPLiteralData))
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
     * @param armoured   {@code true}: ASCII armor the signature.
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws FileNotFoundException
     * @throws PGPException
     * @throws IOException
     */
    public static void signFile(final File dataFile, final File signedFile, final PGPSecretKey privateKey, final String passPhrase,
                                final boolean armoured)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, PGPException, IOException {

        try (InputStream dataInputStream = new FileInputStream( dataFile )) {
            try (InputStream signedInputStream = sign( dataInputStream, privateKey, passPhrase,
                                                       armoured ); OutputStream signedOutputStream = new FileOutputStream( signedFile )) {
                ByteStreams.copy( signedInputStream, signedOutputStream );
            }
        }
    }

    /**
     * PGP sign some data.
     *
     * @param data       The string that contains the data to sign.
     * @param privateKey The private key to use for signing.
     * @param passPhrase The passphrase that the private key is locked with.
     * @param armoured   {@code true}: ASCII armor the signature.
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
    public static byte[] sign(final byte[] data, final PGPSecretKey privateKey, final String passPhrase, final boolean armoured)
            throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IOException, PGPException {

        return ByteStreams.toByteArray( sign( new ByteArrayInputStream( data ), privateKey, passPhrase, armoured ) );
    }

    /**
     * PGP sign a stream.
     *
     * @param data       The stream that contains the data to sign.
     * @param privateKey The private key to use for signing.
     * @param passPhrase The passphrase that the private key is locked with.
     * @param armoured   {@code true}: ASCII armor the signature.
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
    public static InputStream sign(final InputStream data, final PGPSecretKey privateKey, final String passPhrase, final boolean armoured)
            throws NoSuchAlgorithmException, NoSuchProviderException, PGPException, SignatureException, IOException {

        /* Build the signature generator. */
        PGPSignatureGenerator signer = new PGPSignatureGenerator( privateKey.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1,
                                                                  BouncyCastleProvider.PROVIDER_NAME );
        signer.initSign( PGPSignature.BINARY_DOCUMENT,
                         privateKey.extractPrivateKey( passPhrase.toCharArray(), BouncyCastleProvider.PROVIDER_NAME ) );

        /* Write the data into the generator. */
        byte[] buffer = new byte[4096];
        for (int read; (read = data.read( buffer )) >= 0; )
            signer.update( buffer, 0, read );

        /* Create the signature output stream, armour if necessary. */
        try (ByteArrayOutputStream signatureByteStream = new ByteArrayOutputStream(); OutputStream signatureStream = armoured
                ? new ArmoredOutputStream( signatureByteStream ): signatureByteStream) {

            /* Create and write out the signature. */
            PGPSignature signature = signer.generate();
            signature.encode( signatureStream );

            return new ByteArrayInputStream( signatureByteStream.toByteArray() );
        }
    }

    /**
     * <h2>{@link PrintableKeyWrapper}<br> <sub>A wrapper for wrapping a key id with a printable representation of it.</sub></h2>
     *
     * @param <K> The type of object to use for representing the key id.
     *
     *            <p> <i>Apr 9, 2008</i> </p>
     *
     * @author mbillemo
     */
    private static class PrintableKeyWrapper<K> {

        private final K    key;
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

        @Override
        public String toString() {

            return key.toString();
        }
    }
}
