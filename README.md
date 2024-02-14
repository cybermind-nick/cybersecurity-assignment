# Foundations of Cybersecurity assignment

## Objective

Create a Client-Sever application that allows for the sending and receiving of ___signed___ encrypted messages


### Tasks
- Create an application that generates public-private key pairs for any user. This will act as an automatic sign up for us in this case

- Create the client application
    -- A program users to send messages securely using encryption and authentication.
    -- Sends the hashed userid of the agent to the server upon connecting.
    -- Receives the number of saved messages from the server and retrieves them.
    -- Verifies the server's signature, decrypts the message, and displays it on the screen.
    -- Prompts the user to enter the recipient userid, message, and sends them to the server.
    -- Generates a signature based on the message, and sends the encrypted content, timestamp, signature, and unhashed sender userid to the server.
    -- Prints the date, message, and prompt for the user to enter a new message.
- Create the server
    -- Storage for messages sent by user that are not yet read by their recipients.
    -- Listens for incoming connections from clients at the specified port.
    -- Handles one client connection at a time.
    -- Has its own private key and the public keys of all users.
    -- Uses RSA/ECB/PKCS1Padding for encryption and SHA256withRSA for signature generation.
    -- Stores messages in memory in encrypted form, ready to be sent to the recipient.
    -- Sends saved messages to the client, deletes them from the server, and verifies the signature.
    -- Computes the hashed recipient userid for storing and locating saved messages.
    -- Re-encrypts the message with the recipient's public key and saves it in memory.
    -- Prints the hashed userid of the client, sender and recipient userid, timestamp, and plaintext message contents for marking purposes.
