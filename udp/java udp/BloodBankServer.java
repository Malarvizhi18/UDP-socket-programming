import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class BloodBankServer {
    public static void main(String[] args) {
        DatagramSocket serverSocket = null;
        Map<InetAddress, Integer> activeClients = new HashMap<>();
        Map<String, Integer> bloodBankData = new HashMap<>();

        try {
            serverSocket = new DatagramSocket(12345); // Use a specific port number

            System.out.println("Blood Bank Management System Server is running...");

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                serverSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                if (!activeClients.containsKey(clientAddress)) {
                    activeClients.put(clientAddress, clientPort);
                    System.out.println("Client connected: " + clientAddress + ":" + clientPort);
                }

                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());

                String response = "";
                if (request.equals("VIEW")) {
                    // Send available blood types and quantities to the client
                    System.out.println("Requested to show available details");
                    for (Map.Entry<String, Integer> entry : bloodBankData.entrySet()) {
                        response += entry.getKey() + ": " + entry.getValue() + " ml\n";
                    }
                } else if (request.startsWith("REGISTER")) {
                    // Register a new donor
                    System.out.println("Donor needs to donate blood");
                    String[] parts = request.split(",");
                    String donorName = parts[1];
                    String bloodType = parts[2];
                    int quantity = Integer.parseInt(parts[3]);

                    bloodBankData.put(bloodType, bloodBankData.getOrDefault(bloodType, 0) + quantity);
                    response = "Donor registered successfully.";
                } else if (request.startsWith("REQUEST")) {
                    // Handle blood request
                    System.out.println("Requesting for blood");
                    String[] parts = request.split(",");
                    String requestedBloodType = parts[1];
                    int requestedQuantity = Integer.parseInt(parts[2]);

                    if (bloodBankData.containsKey(requestedBloodType) &&
                            bloodBankData.get(requestedBloodType) >= requestedQuantity) {
                        bloodBankData.put(requestedBloodType, bloodBankData.get(requestedBloodType) - requestedQuantity);
                        response = "Request approved. Remaining " + requestedBloodType + ": " +
                                bloodBankData.get(requestedBloodType) + " ml";
                    } else {
                        response = "Request denied. Insufficient blood.";
                    }
                } else if (request.equals("TERMINATE")) {
                    // Remove the client from the active clients list when they terminate
                    activeClients.remove(clientAddress);
                    System.out.println("Client terminated: " + clientAddress + ":" + clientPort);
                    response = "Terminated successfully.";
                } else {
                    response = "Invalid request.";
                }

                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
    }
}
