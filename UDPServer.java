import java.io.*;
import java.net.*;

class UDPServer {
   static DatagramSocket serverSocket;
   static InetAddress IPAddress;
   static int port;
   static byte[] receiveData;

   public static void main(String args[]) throws Exception {
      if (args.length == 1){
         port = Integer.parseInt(args[0]);

         serverSocket = new DatagramSocket(port);
         receiveData = new byte[1024];

         UDPServer.handshake();

         while(true){
            receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            TCPPacket receivedData = new TCPPacket(new String(receivePacket.getData()));

            System.out.println("----------Received---------");
            receivedData.printContent();
            System.out.println("---------------------------\n");

            if(receivedData.body.indexOf("quit") != -1){
               System.out.println("Request to disconnect.");
               disconnect();
               break;
            }
         }

         serverSocket.close();

         // while(true){
         //    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         //    serverSocket.receive(receivePacket);
         //    String sentence = new String(receivePacket.getData());
         //    System.out.println("RECEIVED: " + sentence);
         //    IPAddress = receivePacket.getAddress();
         //    port = receivePacket.getPort();

         //    String capitalizedSentence = sentence.toUpperCase();
         //    sendData = capitalizedSentence.getBytes();
         //    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
         //    serverSocket.send(sendPacket);
         // }
      } else {
         System.out.println("Usage: java UDPServer <port>");
      }
   }

   public static void handshake() throws Exception {
      TCPPacket sendData = new TCPPacket();
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);
      IPAddress = receivePacket.getAddress();
      port = receivePacket.getPort();
      TCPPacket receivedData = new TCPPacket(new String(receivePacket.getData()));

      if (receivedData.synchronization_bit == 1){
         sendData = new TCPPacket();
         sendData.acknowledgement_number = 1;
         sendData.synchronization_bit = 1;
         sendData.acknowledgement_bit = 1;

         byte[] data = sendData.getHeader().getBytes();
         DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
         serverSocket.send(sendPacket);

         receivePacket = new DatagramPacket(receiveData, receiveData.length);
         serverSocket.receive(receivePacket);
         receivedData = new TCPPacket(new String(receivePacket.getData()));

         if (receivedData.synchronization_number == 1)
            System.out.println("Three-way Handshake Complete!");
      }
   }

   public static void disconnect() throws Exception {
      TCPPacket sendData = new TCPPacket();
      sendData.finish_bit = 1;

      byte[] data = sendData.getHeader().getBytes();
      DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
      serverSocket.send(sendPacket);

      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);
      TCPPacket receivedData = new TCPPacket(new String(receivePacket.getData()));

      if (receivedData.acknowledgement_bit == 1){
         receivePacket = new DatagramPacket(receiveData, receiveData.length);
         serverSocket.receive(receivePacket);
         receivedData = new TCPPacket(new String(receivePacket.getData()));

         if (receivedData.finish_bit == 1){
            sendData = new TCPPacket();
            sendData.acknowledgement_bit = 1;

            data = sendData.getHeader().getBytes();
            sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
            serverSocket.send(sendPacket);
         }
      }
   }
}