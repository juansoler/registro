from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import padding as sym_padding # For CBC
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes
import os

# The key from the Java example
FIXED_KEY_STRING = "PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4"
# Salt for key derivation - should be constant if we want to decrypt old Java data
# However, for new encryptions, a random salt stored with ciphertext is better.
# For replicating Java's direct use of key string bytes, we might not need KDF if key is right length.
# The Java code likely converts the string to bytes directly.
# A 32-character string (UTF-8) can be 32 bytes, suitable for AES-256.

AES_BLOCK_SIZE = 16 # Bytes (128 bits)

class CryptoException(Exception):
    """Custom exception for cryptography errors."""
    pass

def _derive_key(key_string: str, salt: bytes, key_length: int = 32) -> bytes:
    """
    Derives a key of specified length from the key_string using PBKDF2.
    This is a more secure way to handle string keys than direct byte conversion.
    However, if the Java code directly used the string as bytes, this will differ.
    For this task, we will directly use the UTF-8 bytes of the key string
    as it's 32 chars, matching AES-256 key length.
    """
    # For this specific problem, the key is 32 chars, likely used directly as UTF-8 bytes.
    key_bytes = key_string.encode('utf-8')
    if len(key_bytes) != 32:
        # This would be an issue if the key string wasn't exactly 32 bytes when UTF-8 encoded.
        # For "PdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4", it is 32 bytes.
        raise ValueError(f"Key string must encode to 32 bytes for AES-256, got {len(key_bytes)}")
    return key_bytes
    
    # Example using PBKDF2 if key derivation was needed:
    # kdf = PBKDF2HMAC(
    #     algorithm=hashes.SHA256(),
    #     length=key_length,
    #     salt=salt,
    #     iterations=100000, # Adjust iterations as needed
    #     backend=default_backend()
    # )
    # return kdf.derive(key_string.encode('utf-8'))

def encrypt(key_string: str, input_file_path: str, output_file_path: str):
    """
    Encrypts a file using AES-256-GCM.
    The key_string is used directly as the AES key (must be 32 bytes after UTF-8 encoding).
    A random 12-byte nonce is generated and prepended to the ciphertext.
    """
    try:
        key = _derive_key(key_string, salt=b'') # Salt not used for direct key usage
        
        # Read plaintext
        with open(input_file_path, 'rb') as f:
            plaintext = f.read()

        # Generate a random 96-bit (12-byte) nonce for GCM
        nonce = os.urandom(12) 
        
        # Create AES-GCM cipher
        cipher = Cipher(algorithms.AES(key), modes.GCM(nonce), backend=default_backend())
        encryptor = cipher.encryptor()
        
        # Encrypt plaintext
        ciphertext = encryptor.update(plaintext) + encryptor.finalize()
        
        # Prepend nonce to ciphertext for storage/decryption
        # Also store the GCM tag, which is appended by encryptor.finalize()
        # The tag is typically 16 bytes (128 bits) for GCM.
        # So, ciphertext already includes the tag.
        
        with open(output_file_path, 'wb') as f:
            f.write(nonce) # Write 12-byte nonce
            f.write(encryptor.tag) # Write 16-byte GCM tag
            f.write(ciphertext) # Write ciphertext

    except FileNotFoundError:
        raise CryptoException(f"Input file not found: {input_file_path}")
    except Exception as e:
        raise CryptoException(f"Encryption failed: {e}")

def decrypt(key_string: str, input_file_path: str, output_file_path: str):
    """
    Decrypts a file encrypted with AES-256-GCM.
    Assumes the first 12 bytes are the nonce and the next 16 bytes are the GCM tag.
    """
    try:
        key = _derive_key(key_string, salt=b'') # Salt not used for direct key usage

        with open(input_file_path, 'rb') as f:
            nonce = f.read(12) # Read 12-byte nonce
            if len(nonce) != 12:
                raise CryptoException("Invalid ciphertext: Nonce is not 12 bytes.")
            
            tag = f.read(16) # Read 16-byte GCM tag
            if len(tag) != 16:
                raise CryptoException("Invalid ciphertext: GCM tag is not 16 bytes.")
                
            ciphertext_only = f.read() # Read the rest of the ciphertext
            
        # Create AES-GCM cipher
        cipher = Cipher(algorithms.AES(key), modes.GCM(nonce, tag), backend=default_backend())
        decryptor = cipher.decryptor()
        
        # Decrypt ciphertext
        plaintext = decryptor.update(ciphertext_only) + decryptor.finalize()
        
        with open(output_file_path, 'wb') as f:
            f.write(plaintext)
            
    except FileNotFoundError:
        raise CryptoException(f"Input file not found: {input_file_path}")
    except Exception as e:
        # Specific exceptions like InvalidTag can be caught from cryptography.exceptions
        # For simplicity, catching general Exception.
        raise CryptoException(f"Decryption failed: {e}. Likely incorrect key or corrupted file.")


if __name__ == '__main__':
    print("Running crypto_utils tests...")
    
    KEY = FIXED_KEY_STRING
    
    # Create a dummy test file
    original_content = b"This is some secret test data for AES GCM encryption!"
    test_input_file = "test_plain.txt"
    encrypted_file = "test_encrypted.enc"
    decrypted_file = "test_decrypted.txt"

    with open(test_input_file, 'wb') as f:
        f.write(original_content)

    # Test Encryption
    try:
        print(f"Encrypting {test_input_file} to {encrypted_file}...")
        encrypt(KEY, test_input_file, encrypted_file)
        print("Encryption successful.")
        assert os.path.exists(encrypted_file), "Encrypted file was not created."
        # Check that encrypted file is not same as original (basic check)
        with open(encrypted_file, 'rb') as f:
            encrypted_data = f.read()
        # nonce (12) + tag (16) + ciphertext
        assert len(encrypted_data) > len(original_content), "Encrypted data length issue."
        assert encrypted_data[28:] != original_content, "Ciphertext is same as plaintext."

    except CryptoException as e:
        print(f"Encryption test failed: {e}")
    except Exception as e:
        print(f"An unexpected error occurred during encryption test: {e}")


    # Test Decryption
    try:
        print(f"\nDecrypting {encrypted_file} to {decrypted_file}...")
        decrypt(KEY, encrypted_file, decrypted_file)
        print("Decryption successful.")
        assert os.path.exists(decrypted_file), "Decrypted file was not created."
        with open(decrypted_file, 'rb') as f:
            decrypted_content = f.read()
        assert decrypted_content == original_content, "Decrypted content does not match original."
        print("Decryption content MATCHES original content.")
    except CryptoException as e:
        print(f"Decryption test failed: {e}")
    except Exception as e:
        print(f"An unexpected error occurred during decryption test: {e}")

    # Test Decryption with wrong key (expected to fail)
    wrong_key = "thisisnotthecorrectkeyforaes123!" # Ensure it's 32 bytes
    if len(wrong_key.encode('utf-8')) == 32:
        try:
            print(f"\nAttempting decryption of {encrypted_file} with WRONG key...")
            decrypt(wrong_key, encrypted_file, "test_decrypted_wrong_key.txt")
            print("ERROR: Decryption with wrong key SUCCEEDED unexpectedly!") # Should not happen
        except CryptoException as e:
            print(f"Decryption with wrong key failed as expected: {e}")
            assert "decryption failed" in str(e).lower() or "invalidkey" in str(e).lower() or "invalid tag" in str(e).lower() , "Wrong key error message mismatch"
        except Exception as e:
            print(f"An unexpected error occurred during wrong key decryption test: {e}")
    else:
        print(f"\nSkipping wrong key test as dummy wrong key is not 32 bytes when encoded: {len(wrong_key.encode('utf-8'))} bytes")


    # Clean up test files
    for f_path in [test_input_file, encrypted_file, decrypted_file, "test_decrypted_wrong_key.txt"]:
        if os.path.exists(f_path):
            try:
                os.remove(f_path)
                print(f"Removed test file: {f_path}")
            except OSError as e_os:
                print(f"Error removing test file {f_path}: {e_os}")
    
    print("\nCrypto_utils tests finished.")

```
