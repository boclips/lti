#!/usr/bin/env bash

# Generate a keypair to private.pem file
openssl genrsa -out private.pem 2048 > /dev/null 2>&1

# Export public key to a separate file
openssl rsa -in private.pem -outform PEM -pubout -out public.pem > /dev/null 2>&1

# Export the private key to a private_unencrypted.pem file
openssl pkcs8 -topk8 -nocrypt -in private.pem -out private_unencrypted.pem  > /dev/null 2>&1

echo "BOCLIPS_LTI_V1P3_SIGNINGKEYS_0_GENERATIONTIMESTAMP = $(date +%s)"
echo "BOCLIPS_LTI_V1P3_SIGNINGKEYS_0_PRIVATEKEY = $(cat private_unencrypted.pem | tr -d '\n')"
echo "BOCLIPS_LTI_V1P3_SIGNINGKEYS_0_PUBLICKEY = $(cat public.pem | tr -d '\n')"

rm private.pem
rm private_unencrypted.pem
rm public.pem

