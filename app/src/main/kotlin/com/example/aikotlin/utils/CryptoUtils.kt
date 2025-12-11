package com.example.aikotlin.utils

import android.util.Base64
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

object CryptoUtils {

    private const val DES_ALGORITHM = "DES/ECB/PKCS5Padding"
    private const val KEY_ALGORITHM = "DES"
    private const val RSA_ALGORITHM = "RSA/ECB/PKCS1Padding"

    private var rsaPublicKey: PublicKey? = null
    private var rsaPrivateKey: PrivateKey? = null

    /**
     * Sets the RSA public key from a byte array (X.509 encoded).
     */
    fun setRsaPublicKey(publicKeyBytes: ByteArray) {
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        rsaPublicKey = keyFactory.generatePublic(keySpec)
    }

    /**
     * Sets the RSA private key from a byte array (PKCS#8 encoded).
     */
    fun setRsaPrivateKey(privateKeyBytes: ByteArray) {
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        rsaPrivateKey = keyFactory.generatePrivate(keySpec)
    }

    /**
     * Generates an RSA key pair.
     * @param keySize The size of the key to generate.
     * @return A Pair containing the encoded public key (X.509) and private key (PKCS#8).
     */
    fun generateRsaKeyPair(keySize: Int = 1024): Pair<ByteArray, ByteArray> {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(keySize)
        val keyPair = generator.generateKeyPair()
        return Pair(keyPair.public.encoded, keyPair.private.encoded)
    }

    // IMPORTANT: DES key must be 8 bytes long.
    private val ENCRYPTED_SECRET_KEY by lazy {
        encryptRSA("12345678".toByteArray(Charsets.UTF_8))
    }

    fun encrypt(data: String): String {
        return try {
            val secretKey = getSecretKey()
            val cipher = Cipher.getInstance(DES_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            data // On failure, return original data
        }
    }

    fun decrypt(data: String): String {
        return try {
            val secretKey = getSecretKey()
            val cipher = Cipher.getInstance(DES_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decodedBytes = Base64.decode(data, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            data // On failure, return original data
        }
    }

    private fun getSecretKey(): SecretKey {
        val decryptedKey = decryptRSA(ENCRYPTED_SECRET_KEY)
        val keySpec = DESKeySpec(decryptedKey)
        val keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        return keyFactory.generateSecret(keySpec)
    }

    private fun encryptRSA(data: ByteArray): ByteArray {
        if(rsaPublicKey == null){
            val pair = generateRsaKeyPair()
            setRsaPublicKey(pair.first)
            setRsaPrivateKey(pair.second)
        }
        val cipher = Cipher.getInstance(RSA_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey)
        return cipher.doFinal(data)
    }

    private fun decryptRSA(data: ByteArray): ByteArray {
        if(rsaPublicKey == null){
            val pair = generateRsaKeyPair()
            setRsaPublicKey(pair.first)
            setRsaPrivateKey(pair.second)
        }
        val cipher = Cipher.getInstance(RSA_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey)
        return cipher.doFinal(data)
    }
}
