# -------------------------------------------------------------------------------------------------------------------------------------------------------------
#                                                     COMP3331 (Computer Networks and Applications) - Assignment 1
# -------------------------------------------------------------------------------------------------------------------------------------------------------------
#                                                     Written by:       Joel Dean (z5208947)
# -------------------------------------------------------------------------------------------------------------------------------------------------------------

# Python Packages/Libraries
from socket import *
import sys
import re
from threading import Thread
from time import sleep

# Example Of multithreading from COMP3331 Course webpage
"""
    Define multi-thread class for client
    This class would be used to define the instance for each connection from each client
    For example, client-1 makes a connection request to the server, the server will call
    class (ClientThread) to define a thread for client-1, and when client-2 make a connection
    request to the server, the server will call class (ClientThread) again and create a thread
    for client-2. Each client will be runing in a separate therad, which is the multi-threading
"""
class ClientThread(Thread):
    def __init__(self, clientAddress, clientSocket):
        Thread.__init__(self)
        self.clientAddress = clientAddress
        self.clientSocket = clientSocket
        self.clientAlive = False
        
        self.clientSocket.send("Incoming connection accepted.\n".encode())


        clientIP = self.clientAddress[0]
        message = "Welcome USER " + str(clientIP) + " to the SUPER SECRET VIP EXCLUSIVE MessageBoard!!!\n"

        self.clientSocket.send(message.encode())
        self.clientAlive = True
        
    def run(self):
        
        # Authenticate new user
        self.authenticate()

        # Start a communication loop
        message = ''
        while self.clientAlive:
            
             # Receive Command from Client
            self.clientSocket.send("Please Enter Command: ".encode())
            command = self.clientSocket.recv(2048).decode
           
            
            # If no message -> Set Client to offline
            # Cut TCP connection
            if message == '':
                self.clientAlive = False
                print("============= User " + str(self.clientAddress[1]) + "============")
                print("              Disconnected from server")
                break
            
    
    """
        Authenticate Docstring - To do
    """
    def authenticate(self):

        # Authenticate client connection
        maxConsecutiveFailedAttempts = int(sys.argv[2])
        codeNameFound = False
        clientAuthentiated = False
            
        # ===============   CodeName Login ========================
        while codeNameFound == False:
            self.clientSocket.send("Please Enter Your Codename\n".encode())
            self.clientSocket.send("Codename: ".encode())

            # Receive input from client
            codeName = self.clientSocket.recv(2048).decode()
             # Copy 'original' username for later use
            baseCodeName = codeName
            fixedCodeName = re.escape(str(codeName))

            # If any special regex characters exist -> Escape them
            if fixedCodeName != None:
                    codeName = fixedCodeName
            regex = re.compile("^" + codeName + "\s")

            # Check file line by line for a match of username
            for line in open("credentials.txt"):

                # Check for match in line
                # (Duplicated usernames are not tested)
                match = regex.search(line)
                if match != None:
                    # Exit loop once a match is found 
                    codeNameFound = True

            # Repeat if username input doesn't exist in file
            if codeNameFound == False:
                self.clientSocket.send("This agent is not in our database!!!\n".encode())
        



        # ======================== Password Login =====================
        for attemptNumber in range(0, maxConsecutiveFailedAttempts):
            self.clientSocket.send("Super Secret Password: ".encode())

            # Receive password from client
            secretPassword = self.clientSocket.recv(2048).decode()
            fixedSecretPassword = re.escape(str(secretPassword))
            if fixedSecretPassword != None:
                secretPassword = fixedSecretPassword
            # Find a regex expression to match in file
            regex = "^" + codeName + "\s" + secretPassword + "$"
            regex = re.compile(regex)

            # Chekc that password matches associated username
            for line in open("credentials.txt"):
                
                # Check line for password/user match
                match = regex.search(line)
                if match != None:
                    clientAuthentiated = True
            



            # Cases
            # User is blocked
            if blocked[baseCodeName] == True:
                self.clientSocket.send("This account was blocked due to multiple failed login attempts.".encode())
                self.clientSocket.send("Bye Bye".encode())
                self.clientSocket.send("Please Close:".encode())
                self.clientSocket.close()
                break
            # Client is authenticated
            elif clientAuthentiated == True:
                authMessage = "Good Morning Agent.\n" 
                authMessage += "Your order of a Plus-Size Pink "
                authMessage += "Fluffy TeddyBear, has reached HQ.\nIt will "
                authMessage += "be left in your office chair to pick up after "
                authMessage += "the mission\n"
                self.clientSocket.send(authMessage.encode())
                break

            # Max attempt reached -> Block
            elif attemptNumber == maxConsecutiveFailedAttempts - 1:

                # Disconnect the current socket
                self.clientSocket.send("*Sigh* That was your last attempt. Please don't run.\n".encode())
                print("Infiltrator has been blocked")
                self.clientSocket.send("close please:".encode())
                self.clientSocket.close()
                self.clientAlive = False

                # Block CodeName for all threads for 10 seconds
                blocked[str(baseCodeName)] = True
                sleep(10)
                blocked[str(baseCodeName)] = False
                break

            # Invalid password
            else:
                self.clientSocket.send("Incorrect Login Details Detected!!!!\n".encode())
                self.clientSocket.send("Our contract assasination support service will be in contact shorty.\n".encode())
                self.clientSocket.send("If you value your life. Please enter the correct Password\n".encode())



# ===================================================================== main_server =================================================
# =========== Arguments: ============
# =====   Name - Type - Usage========
# serverPortNumber - integer - Stores the port at which the server listens out for incoming TCP connections
# maxConsecutiveFailedAttempts - integer - Allowed number of failed login attempts before blocking user (10 seconds)
#
# =========== Function: =============
# Main server thread
# Creates the TCP welcoming (listening) socket
# 
def main_server(serverPortNumber, maxConsecutiveFailedAttempts):

    #  ==========Invalid Arguments====================
    # Consecutive Failed Attempts 1-5
    if maxConsecutiveFailedAttempts not in range(1,6):
        print("Maximum allowed number of failed login attempts must be between 1 and 5")
        print("Not " + str(maxConsecutiveFailedAttempts) + "Dummy!")
        exit(1)

    # Create dictionary of usernames 
    # Username : Blocked (Boolean)
    global blocked
    blocked = {}
    for line in open("credentials.txt"):
        regex = re.compile('^[^\s]+')
        currentCodeName = regex.search(line)
        if currentCodeName != None:
            blocked[currentCodeName.group(0)] = False


    # Creates the Welcoming socket for the serverside system
    serverWelcomingSocket = socket(AF_INET, SOCK_STREAM)
    serverWelcomingSocket.bind(('', serverPortNumber))
    print("Server is now listening for incoming TCP connections")

    while True:
        # Start listening
        serverWelcomingSocket.listen()

        # Initate TCP connection for incoming client
        # Run in it's own thread
        clientSocket, clientAddress = serverWelcomingSocket.accept()
        clientProcess = ClientThread(clientAddress, clientSocket)
        clientProcess.start()

            

    
    # Close the client TCP connection and the server welcoming socket (Stops listening)
    serverWelcomingSocket.close()
    print("Server Closed: NOT listening for incoming connections")



# ============ Run main function upon testing file ==================
if __name__ == "__main__":

    if len(sys.argv) != 3:
        print("\n===== Error=====  Usage: python3 server.py serverPortNumber maxConsecutiveFailedAttempts")
        exit(1)

    # Runs the main server thread
    # Inputs command line arguments
    # acquire server host and port from command line parameter
    main_server(int(sys.argv[1]), int(sys.argv[2]))