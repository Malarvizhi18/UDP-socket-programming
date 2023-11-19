import socket

# Create a UDP socket
client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Server address and port
server_host = '127.0.0.1'  # Use the server's IP address or hostname
server_port = 12345  # Use the same port number as the server

while True:
    print("Choose an option:")
    print("1. View available blood types and quantities")
    print("2. Register as a donor")
    print("3. Request blood")
    print("4. Exit")
    choice = input("Enter your choice (1/2/3/4): ")

    if choice == '1':
        # View available blood types and quantities
        client_socket.sendto("VIEW".encode('utf-8'), (server_host, server_port))
    elif choice == '2':
        # Register as a donor
        donor_name = input("Enter donor name: ")
        blood_type = input("Enter blood type: ")
        quantity = int(input("Enter donated quantity (ml): "))
        request=f"REGISTER,{donor_name},{blood_type},{quantity}"
        client_socket.sendto(request.encode('utf-8'), (server_host, server_port))
    elif choice == '3':
        # Request blood
        blood_type = input("Enter blood type: ")
        quantity = input("Enter requested quantity (ml): ")
        request = f"REQUEST,{blood_type},{quantity}"
        client_socket.sendto(request.encode('utf-8'), (server_host, server_port))
    elif choice == '4':
        client_socket.sendto("TERMINATE".encode('utf-8'), (server_host, server_port))
        break
    else:
        print("Invalid choice.")
        continue

    # Receive and display the server's response
    response, _ = client_socket.recvfrom(1024)
    print("Server Response:\n", response.decode('utf-8'))




