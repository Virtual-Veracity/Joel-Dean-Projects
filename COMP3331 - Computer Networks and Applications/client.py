# -------------------------------------------------------------------------------------------------------------------------------------------------------------
#                                                     COMP3331 (Computer Networks and Applications) - Assignment 1
# -------------------------------------------------------------------------------------------------------------------------------------------------------------
#                                                     Written by:       Joel Dean (z5208947)
# -------------------------------------------------------------------------------------------------------------------------------------------------------------

# Python Packages/Libraries
from socket import *
import sys


# ===================================================================== main_client =================================================
# =========== Arguments: ============
# =====   Name - Type - Usage========
# serverIPAddress  - string  - Stores the port number to connect to server welcoming socket
# serverPortNumber - integer - Stores the port at which the server listens out for incoming TCP connections
# clientUDPPortNum - integer - Stores the port number for the socket at which the client listens for incoming 
#                              UDP packets from the server
#
# =========== Function: =============
# Client Application Layer Process
# 
def main_client(serverIPAddress, serverPortNumber, UDPportNumber):

    # Creates the Welcoming socket for the serverside system
    clientSocket = socket(AF_INET, SOCK_STREAM)
    clientSocket.connect((serverIPAddress, serverPortNumber))
    print("Client has connected to server at port " + str(serverPortNumber) + ".")

    # Start communication with server
    command = 'AUTH'
    while command != 'OUT':

        # Authentication Phase
        while command == 'AUTH':
            response = ''
            while ':' not in response:
                data = clientSocket.recv(2048)
                response = data.decode()
                print(response, end='')
            sendMessage = input()
            clientSocket.send(sendMessage.encode())
            
        


        # Command Interface
        waiting = 0
        while waiting == 0:
            # Send data to server
            # Commands or otherwise
            sendMessage = input()
            command = sendMessage.split(" ")[0]

            # Error checking for commands
            if command == 'BCM':
                pass
            elif command == 'ATU':
                pass
            elif command == 'SRS':
                pass
            elif command == 'RDM':
                pass
            elif command == 'OUT':    
                pass
            elif command == 'UPD':    
                pass
            else:
                print("Enter one of the following commands (BCM, ATU, SRB, SRM, RDM, OUT, UDP):")


    
    # Close the client socket (Ends TCP connection)
    clientSocket.close()
    print("Server Closed: NOT listening for incoming connections")


# ============ Run main function upon testing file ==================
if __name__ == "__main__":

    if len(sys.argv) != 4:
        print("\n=========Error==== Usage: python3 client.py serverIP serverportNum UDPPortNum")
        exit(1)

    main_client(sys.argv[1], int(sys.argv[2]), int(sys.argv[3]))