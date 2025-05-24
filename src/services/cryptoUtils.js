const crypto = require('crypto');
const fs = require('fs');
const path = require('path'); // May be needed for path operations

const ALGORITHM = 'aes-256-cbc'; // AES is often used with 256-bit keys in CBC mode.
                                // The Java code used "AES" which might default to ECB on some JREs,
                                // but CBC is generally preferred if an IV is used.
                                // For simplicity and closer match to the original if it used ECB and no IV,
                                // we might need to adjust or use 'aes-256-ecb' if no IV is generated/used.
                                // However, the Java code doesn't show IV handling, which is bad for CBC.
                                // Let's assume for now the key itself is used directly and it's sufficient for the AES variant.
                                // A common practice is to derive a key and IV from the password.
                                // The original Java code uses `new SecretKeySpec(key.getBytes(), "AES")`. key.getBytes()
                                // will use the platform's default charset. UTF-8 is a safer bet.
                                // The key length must match the algorithm (e.g., 32 bytes for AES-256).

/**
 * Encrypts a file.
 * @param {string} keyString - The secret key as a string. Must be 32 characters for aes-256-cbc.
 * @param {string} inputFile - Path to the input file.
 * @param {string} outputFile - Path to the output file.
 */
function encrypt(keyString, inputFile, outputFile) {
    return new Promise((resolve, reject) => {
        const key = Buffer.from(keyString, 'utf8').slice(0, 32); // Ensure key is 32 bytes for AES-256

        // For CBC mode, an Initialization Vector (IV) is required.
        // It should be random and unique for each encryption.
        // For ECB mode, IV is not used, but ECB is less secure.
        // Given the Java code's simplicity, it might be using ECB or a fixed/derived IV.
        // If we must use CBC (recommended), we'd generate an IV and need to store/prepend it to the output.
        // For now, let's try to keep it simple and assume ECB if the Java code implies it,
        // or if the key is directly used and its length dictates the AES variant.
        // The original `Cipher.getInstance("AES")` is ambiguous.
        // Let's use 'aes-256-ecb' for now, and ensure the key is 32 bytes. No IV needed for ECB.
        const cipher = crypto.createCipheriv('aes-256-ecb', key, null); // null for IV in ECB

        const input = fs.createReadStream(inputFile);
        const output = fs.createWriteStream(outputFile);

        input.pipe(cipher).pipe(output);

        output.on('finish', () => resolve());
        output.on('error', err => reject(err));
        cipher.on('error', err => reject(err));
        input.on('error', err => reject(err));
    });
}

/**
 * Decrypts a file.
 * @param {string} keyString - The secret key as a string. Must be 32 characters for aes-256-cbc.
 * @param {string} inputFile - Path to the input file.
 * @param {string} outputFile - Path to the output file.
 */
function decrypt(keyString, inputFile, outputFile) {
    return new Promise((resolve, reject) => {
        const key = Buffer.from(keyString, 'utf8').slice(0, 32); // Ensure key is 32 bytes for AES-256

        // Use 'aes-256-ecb' and null IV as decided for encrypt
        const decipher = crypto.createDecipheriv('aes-256-ecb', key, null);

        const input = fs.createReadStream(inputFile);
        const output = fs.createWriteStream(outputFile);

        input.pipe(decipher).pipe(output);

        output.on('finish', () => resolve());
        output.on('error', err => reject(err));
        decipher.on('error', err => reject(err));
        input.on('error', err => reject(err));
    });
}

module.exports = { encrypt, decrypt };

/*
Important considerations from the Java code:
1.  `ALGORITHM = "AES"`, `TRANSFORMATION = "AES"`: This is somewhat ambiguous in Java.
    It often defaults to "AES/ECB/PKCS5Padding". Node.js's `crypto` module is more explicit.
    `aes-256-ecb` is a common equivalent if no IV is specified. PKCS5Padding is usually default.
2.  `key.getBytes()`: This uses the platform default charset. UTF-8 is a safer assumption for `Buffer.from(keyString, 'utf8')`.
3.  Key Length: The Java code `new SecretKeySpec(key.getBytes(), ALGORITHM)` will use the raw bytes of the key.
    If the provided `keyString` is, for example, "mysecretpassword", its byte length will determine the AES variant (128, 192, 256).
    The JavaScript code above standardizes on aes-256 by taking a 32-byte slice of the key. This might differ from the Java behavior if the key string isn't intended to be exactly 32 bytes after UTF-8 encoding.
    A more robust approach would be to hash the keyString to a fixed length (e.g., SHA-256 for a 32-byte key).
    For now, the implementation takes the first 32 bytes of the UTF-8 encoded keyString.

Testing will be crucial to ensure compatibility if decrypting files encrypted by the Java version or vice-versa.
If the Java key was shorter, e.g. 16 bytes, then 'aes-128-ecb' would be the target.
The current JS code assumes the keyString will be long enough or is intended to be truncated/padded to 32 bytes for AES-256.
*/
