import socket

# Create a UDP socket
server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind the socket to a specific host and port
server_host = '127.0.0.1'  # Use your server's IP address or hostname
server_port = 12345  # Use a specific port number
server_socket.bind((server_host, server_port))

print("Blood Bank Management System Server is running...")

# Define a dictionary to store client information (IP and port)
active_clients = {}

# Define your database or data storage mechanism here
# For simplicity, we'll use a dictionary as a sample data store
blood_bank_data = {
    "A+": 1000,
    "B+": 800,
    "AB+": 1200,
    "O+": 2000,
    "B-":500,
    "O-":300,
    "AB-":350,
    "A-":450,
}

while True:
    # Receive data from clients
    data, client_address =server_socket.recvfrom(1024)

    # Add the client to the active clients list if not already added
    if client_address not in active_clients:
        active_clients[client_address] = True
        print(f"Client connected: {client_address}")

    # Decode the received data (assuming it's in JSON format)
    request = data.decode('utf-8')

    # Process the request
    if request == "VIEW":
        # Send available blood types and quantities to the client
        print(f"Requested to show available details")
        response = "\n".join([f"{blood_type}: {quantity} ml" for blood_type, quantity in blood_bank_data.items()])
    elif request.startswith("REGISTER"):
        # Register a new donor
        print(f"donor need to donate the blood")
        _,donor_name,blood_type,quantity=request.split(',')
        if blood_type in blood_bank_data:
            blood_bank_data[blood_type] =int(blood_bank_data[blood_type])+ int(quantity)
        else:
            blood_bank_data[blood_type] = int(quantity)

        response = "Donor registered successfully."
    elif request.startswith("REQUEST"):
        # Handle blood request
        print(f"requesting for blood")
        _, requested_blood_type, requested_quantity = request.split(',')
        requested_quantity = int(requested_quantity)

        if requested_blood_type in blood_bank_data and blood_bank_data[requested_blood_type] >= requested_quantity:
            blood_bank_data[requested_blood_type] -= requested_quantity
            response = f"Request approved. Remaining {requested_blood_type}: {blood_bank_data[requested_blood_type]} ml"
        else:
            response = "Request denied. Insufficient blood."
    elif request == "TERMINATE":
        # Remove the client from the active clients list when they terminate
        active_clients.pop(client_address, None)
        print(f"Client terminated: {client_address}")
        response = "Terminated successfully."
        
    else:
        response = "Invalid request."

    # Send the response back to the client
    server_socket.sendto(response.encode('utf-8'), client_address)
