import java.io.*;
import java.net.*;
import java.io.Serializable;

class UDPClient {
   static DatagramSocket clientSocket;
   static InetAddress IPAddress;
   static int port;
   static byte[] receiveData;
   static boolean quit = false;

   public static void main(String args[]) throws Exception {
      if (args.length == 1){
         UDPClient client = new UDPClient();
         port = Integer.parseInt(args[0]);
         BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
         clientSocket = new DatagramSocket();
         IPAddress = InetAddress.getByName("localhost");

         // byte[] sendData = new byte[1024];
         // byte[] receiveData = new byte[1024];
         // String sentence = inFromUser.readLine();

         // sendData = sentence.getBytes();
         // DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
         // clientSocket.send(sendPacket);

         // DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         // clientSocket.receive(receivePacket);
         // String modifiedSentence = new String(receivePacket.getData());
         // System.out.println("FROM SERVER:" + modifiedSentence);

         receiveData = new byte[1024];

         UDPClient.handshake();

         while(!quit){
            System.out.print("Enter string to send (\"quit\" to exit): ");
            String input = inFromUser.readLine();

            if(input.indexOf("quit") != -1){
               System.out.println("Requested to disconnect.");
               TCPPacket sendData = new TCPPacket();
               byte[] data = sendData.getHeader(input).getBytes();
               DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
               clientSocket.send(sendPacket);
               quit = disconnect();
            } else {
               TCPPacket sendData = new TCPPacket();
               sendData.body = input;
               byte[] data = sendData.getHeader().getBytes();
               DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
               clientSocket.send(sendPacket);

               System.out.println("------------Sent-----------");
               sendData.printContent();
               System.out.println("---------------------------\n");
            }


         }


         clientSocket.close();
      } else {
         System.out.println("Usage: java UDPClient <port>");
      }
   }


   public void sendData(String content) throws Exception {
      TCPPacket sendData = new TCPPacket();

      sendData.synchronization_number = 1;
      sendData.acknowledgement_number = 1;
      sendData.synchronization_bit = 1;
      sendData.acknowledgement_bit = 1;

      byte[] data = sendData.getHeader(content).getBytes();
      DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
      clientSocket.send(sendPacket);
   }

   public static void handshake() throws Exception {
      TCPPacket sendData = new TCPPacket();
      sendData.synchronization_bit = 1;

      byte[] data = sendData.getHeader().getBytes();
      DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
      clientSocket.send(sendPacket);

      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      TCPPacket receivedData = new TCPPacket(new String(receivePacket.getData()));

      if (receivedData.acknowledgement_bit == 1){
         sendData = new TCPPacket();
         sendData.synchronization_number = 1;
         sendData.acknowledgement_number = 1;
         sendData.synchronization_bit = 1;
         sendData.acknowledgement_bit = 1;

         data = sendData.getHeader().getBytes();
         sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
         clientSocket.send(sendPacket);

         System.out.println("Three-way Handshake Complete!");
      }
   }

   public static boolean disconnect() throws Exception {
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      TCPPacket receivedData = new TCPPacket(new String(receivePacket.getData()));

      if (receivedData.finish_bit == 1){
         TCPPacket sendData = new TCPPacket();
         sendData.acknowledgement_bit = 1;
         byte[] data = sendData.getHeader().getBytes();
         DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
         clientSocket.send(sendPacket);

         sendData = new TCPPacket();
         sendData.finish_bit = 1;
         data = sendData.getHeader().getBytes();
         sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
         clientSocket.send(sendPacket);
      }

      receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      receivedData = new TCPPacket(new String(receivePacket.getData()));

      if (receivedData.acknowledgement_bit == 1){
         return true;
      } else {
         return false;
      }
   }
}