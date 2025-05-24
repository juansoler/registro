const crypto = require('crypto');

// Constants from the Java class
const ITERATIONS = 20 * 1000;
const SALT_LEN = 32; // bytes
const DESIRED_KEY_LEN = 256 / 8; // 256 bits = 32 bytes
const HASH_ALGORITHM = 'sha1'; // PBKDF2WithHmacSHA1 uses SHA1

/**
 * Computes a salted PBKDF2 hash of a given plaintext password.
 * @param {string} password - The plaintext password.
 * @returns {Promise<string>} A promise that resolves with the stored password string "salt$hash".
 */
function getSaltedHash(password) {
    return new Promise((resolve, reject) => {
        if (password === null || password.length === 0) {
            return reject(new Error("Empty passwords are not supported."));
        }

        crypto.randomBytes(SALT_LEN, (err, salt) => {
            if (err) {
                return reject(err);
            }

            crypto.pbkdf2(password, salt, ITERATIONS, DESIRED_KEY_LEN, HASH_ALGORITHM, (err, derivedKey) => {
                if (err) {
                    return reject(err);
                }
                // Store the salt with the password, matching the Java format
                const saltString = salt.toString('base64');
                const hashString = derivedKey.toString('base64');
                resolve(`${saltString}$${hashString}`);
            });
        });
    });
}

/**
 * Checks whether a given plaintext password corresponds to a stored salted hash.
 * @param {string} password - The plaintext password to check.
 * @param {string} stored - The stored password string in "salt$hash" format.
 * @returns {Promise<boolean>} A promise that resolves with true if the password matches, false otherwise.
 */
function check(password, stored) {
    return new Promise((resolve, reject) => {
        if (password === null || password.length === 0) {
            return reject(new Error("Empty passwords are not supported for checking."));
        }
        
        const parts = stored.split('$');
        if (parts.length !== 2) {
            return reject(new Error("The stored password must have the form 'salt$hash'"));
        }

        const salt = Buffer.from(parts[0], 'base64');
        const storedHash = parts[1];

        crypto.pbkdf2(password, salt, ITERATIONS, DESIRED_KEY_LEN, HASH_ALGORITHM, (err, derivedKey) => {
            if (err) {
                return reject(err);
            }

            resolve(derivedKey.toString('base64') === storedHash);
        });
    });
}

module.exports = { getSaltedHash, check };

/*
Notes on porting from Java's Password.java:
1.  `iterations = 20*1000`, `saltLen = 32`, `desiredKeyLen = 256`: These are directly translated.
    `desiredKeyLen` in `crypto.pbkdf2` is in bytes, so 256 bits / 8 = 32 bytes.
2.  `SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen)`: Node's `crypto.randomBytes` is suitable for generating cryptographically strong random data for the salt.
3.  `SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")`: Node's `crypto.pbkdf2` allows specifying the digest algorithm, which is 'sha1' in this case to match HmacSHA1.
4.  `Base64.encodeBase64String` and `Base64.decodeBase64`: Node's `Buffer` handles Base64 encoding/decoding (`toString('base64')` and `Buffer.from(string, 'base64')`).
5.  The structure `salt$hash` is maintained.
6.  Functions are asynchronous (return Promises) as cryptographic operations can be intensive.
*/
