import hashlib
import os
import base64

def hash_password(password: str) -> str:
    """Hash a password using PBKDF2 with HMAC-SHA256"""
    salt = os.urandom(16)
    key = hashlib.pbkdf2_hmac(
        'sha256',
        password.encode('utf-8'),
        salt,
        100000
    )
    return base64.b64encode(salt + key).decode('ascii')

def verify_password(stored_password: str, provided_password: str) -> bool:
    """Verify a password against a stored hash"""
    try:
        decoded = base64.b64decode(stored_password.encode('ascii'))
        salt = decoded[:16]
        stored_key = decoded[16:]
        new_key = hashlib.pbkdf2_hmac(
            'sha256',
            provided_password.encode('utf-8'),
            salt,
            100000
        )
        return stored_key == new_key
    except Exception:
        return False
