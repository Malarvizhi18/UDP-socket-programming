import java.net.*;
import java.util.Scanner;

public class BloodBankClient {
    public static void main(String[] args) {
        DatagramSocket clientSocket = null;
        Scanner scanner = new Scanner(System.in);

        try {
            clientSocket = new DatagramSocket();

            InetAddress serverAddress = InetAddress.getByName("127.0.0.1"); // Use the server's IP address or hostname
            int serverPort = 12345; // Use the same port number as the server

            while (true) {
                System.out.println("Choose an option:");
                System.out.println("1. View available blood types and quantities");
                System.out.println("2. Register as a donor");
                System.out.println("3. Request blood");
                System.out.println("4. Exit");
                System.out.print("Enter your choice (1/2/3/4): ");

                String choice = scanner.nextLine();
                String request = "";

                if (choice.equals("1")) {
                    // View available blood types and quantities
                    request = "VIEW";
                } else if (choice.equals("2")) {
                    // Register as a donor
                    System.out.print("Enter donor name: ");
                    String donorName = scanner.nextLine();
                    System.out.print("Enter blood type: ");
                    String bloodType = scanner.nextLine();
                    System.out.print("Enter donated quantity (ml): ");
                    int quantity = Integer.parseInt(scanner.nextLine());

                    request = "REGISTER," + donorName + "," + bloodType + "," + quantity;
                } else if (choice.equals("3")) {
                    // Request blood
                    System.out.print("Enter blood type: ");
                    String bloodType = scanner.nextLine();
                    System.out.print("Enter requested quantity (ml): ");
                    int quantity = Integer.parseInt(scanner.nextLine());

                    request = "REQUEST," + bloodType + "," + quantity;
                } else if (choice.equals("4")) {
                    request = "TERMINATE";
                    DatagramPacket terminatePacket = new DatagramPacket(request.getBytes(), request.length(), serverAddress, serverPort);
                    clientSocket.send(terminatePacket);
                    break;
                } else {
                    System.out.println("Invalid choice.");
                    continue;
                }

                DatagramPacket sendPacket = new DatagramPacket(request.getBytes(), request.length(), serverAddress, serverPort);
                clientSocket.send(sendPacket);

                // Receive and display the server's response
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Server Response: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            scanner.close();
        }
    }
}
