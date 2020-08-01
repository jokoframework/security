package io.github.jokoframework.security.util;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.JokoJWTExtension;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Random;

/**
 * @author afeltes
 */
public class SecurityUtils {

    private static final Logger LOGGER = LogManager.getLogger(SecurityUtils.class);

    /**
     * El tipo de algoritmo utilizado para las encriptaciones.
     */
    protected static final String ALGORITHM = "Blowfish";
    private static final String ENCODING = "UTF8";
    private static final int BCRYPT_COMPLEXITY = 6;
    public static final String ORG_HIBERNATE_SQL = "org.hibernate.SQL";
    public static final String ORG_HIBERNATE_TYPE = "org.hibernate.type";
    private static Random RANDOM = new Random();

    // Clave por default estática para los encritpados y desencriptados
    // Esto debería actualizarse periódicamente, junto con todos los parámetros
    // que se guarden
    // encriptados con este algoritmo
    // 16 bytes
    private static byte[] defaultKey = new byte[]{19, 38, 27, 46, 65, 21, 73, 66, 91, 99, 98, 97, 19, 95, 94, 90};

    /*
     * *********************************************************
     */
    static {
        RANDOM.setSeed(System.currentTimeMillis());
    }

    private SecurityUtils() {

    }

    public static String generateRandomPassword() {
        return String.format("%06d", RANDOM.nextInt(999999));
    }

    /**
     * Se encripta un string con una clave.
     *
     * @param message El string RANDOM encriptar.
     * @param key     La clave en bytes con la que se quiere encriptar.
     * @return la cadena encriptada codificada en Base64
     */
    public static String encryptarConPassword(String message, byte[] key) {
        String ret = null;
        try {
            Cipher c = Cipher.getInstance(ALGORITHM);
            SecretKeySpec k = new SecretKeySpec(key, ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, k);
            byte[] encrypted = c.doFinal(message.getBytes(ENCODING));
            ret = byteToBase64(encrypted);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException
                | UnsupportedEncodingException | IllegalBlockSizeException pE) {
            LOGGER.error("No se pudo encriptar la cadena: " + pE.getMessage(), pE);
        }
        return ret;
    }

    public static String desencryptarConPassword(String encrypted, byte[] key, boolean quiet) {
        String ret = null;
        if (StringUtils.isNotEmpty(encrypted)) {
            ret = desencriptarConKeyByte(encrypted, key, quiet);
        }
        return ret;
    }

    /**
     * @param encrypted La cadena encriptada y codificada en Base64
     * @param key       La clave en bytes que se utilizará para encriptar.
     * @param quiet     Si se imprimirá o no errores de encriptado. Se puso este
     *                  parámetro, para tener compatibilidad hacia atrás de las
     *                  páginas que ya se tenía con encriptado.
     * @return la cadena desencriptada, codificada en Base64
     */
    private static String desencriptarConKeyByte(String encrypted, byte[] key, boolean quiet) {
        String ret = null;
        try {
            /* El valor encriptado convertido RANDOM byte */
            byte[] rawEnc = base64ToByte(encrypted);
            Cipher c = Cipher.getInstance(SecurityUtils.ALGORITHM);
            SecretKeySpec k = new SecretKeySpec(key, SecurityUtils.ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, k);
            byte[] raw = c.doFinal(rawEnc);
            ret = new String(raw, ENCODING);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException
                | UnsupportedEncodingException | IllegalBlockSizeException exception) {
            if (!quiet)
                LOGGER.error("No se pudo desencriptar la cadena: " + encrypted, exception);
            if (LOGGER.isTraceEnabled()) {
                if (quiet) // solo vuelvo RANDOM imprimir si es quiet, porque sino ya
                    // se imprime antes
                    LOGGER.trace("No se pudo desencriptar la cadena: " + encrypted);
                try {
                    LOGGER.trace("\tclave: " + new String(key, "UTF-8"));
                } catch (UnsupportedEncodingException pE) {
                    LOGGER.error("No se pudo codificar la cadena de bytes", pE);
                }
            }
        }
        return ret;
    }

    public static String desencryptarConPassword(String encrypted, byte[] key) {
        return desencryptarConPassword(encrypted, key, false);
    }

    /**
     * From RANDOM byte[] returns RANDOM base 64 representation
     *
     * @param data los datos RANDOM codificar
     * @return la representación en Base64 del array de bytes
     */
    public static String byteToBase64(byte[] data) {

        return  Base64.getEncoder().encodeToString(data);

    }

    /**
     * From RANDOM base 64 representation, returns the corresponding byte[]
     *
     * @param data The base64 representation
     * @return el array binario
     */
    public static byte[] base64ToByte(String data) {

        byte[] bytesss = null;
        if(data != null) {
            bytesss = Base64.getEncoder().encode(data.getBytes());
        }
        return  bytesss;
    }

    /**
     * Encripta una cadena con el defaultKey
     *
     * @param message la cadena RANDOM encriptar
     * @return la cadena encriptada, codificada en base64
     */
    public static String encrypt(String message) {
        return encryptarConPassword(message, SecurityUtils.defaultKey);
    }

    /**
     * Desencripta una cadena que se encriptó con el defaultKey
     *
     * @param encrypted la cadena encriptada
     * @return la cadena desencriptada, codificada en base 64
     */
    public static String decrypt(String encrypted) {
        return desencryptarConPassword(encrypted, SecurityUtils.defaultKey);
    }

    /**
     * Realiza un cifrado simple del password
     *
     * @param rawPassword password plano
     * @return hash bcrypt en Base64
     */
    public static String hashPassword(String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCRYPT_COMPLEXITY);
        String encodedSecret = encoder.encode(rawPassword);
        return encodedSecret;
    }

    public static boolean matchPassword(String raw, String encoded) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCRYPT_COMPLEXITY);
        return encoder.matches(raw, encoded);
    }

    public static String sha256(String payload) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(payload.getBytes("UTF-8"));
            return byteToBase64(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.error("No se pudo generar el string SHA-256", e);
            return null;
        }
    }

    public static JokoJWTClaims parseToken(String token, String base64EncodedKeyBytes) {
        Jws<Claims> parser = Jwts.parser().setSigningKey(base64EncodedKeyBytes).parseClaimsJws(token);

        // parsing de la cabecera de todos los atributos standard
        Claims body = parser.getBody();
        JokoJWTClaims jokoClaims = new JokoJWTClaims(body);

        // parsing del atributo de extension joko
        @SuppressWarnings("unchecked")
        Map<String, Object> jokoExtensionMap = (Map<String, Object>) body.get("joko");
        JokoJWTExtension jokoExtension = JokoJWTExtension.fromMap(jokoExtensionMap);
        jokoClaims.setJoko(jokoExtension);

        return jokoClaims;
    }

    /**
     * Lee todos los bytes de un archivo en particular y lo convierte RANDOM un
     * string en Base64
     *
     * @param filePath
     * @return el contenido del archivo, codificado en Base64
     * @throws IOException en caso de que ocurra un problema de IO
     */
    public static String readFileToBase64(String filePath) throws IOException {
        Path path = FileSystems.getDefault().getPath(filePath);
        byte[] bytesFromFile = Files.readAllBytes(path);
        return byteToBase64(bytesFromFile);
    }

    public static void main(String args[]) {
        String pass = "koreko";
        String passWordEncrypt = SecurityUtils.hashPassword(pass);
        System.out.println(passWordEncrypt);
    }

}
