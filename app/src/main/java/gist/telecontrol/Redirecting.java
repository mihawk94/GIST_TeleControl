import java.io.*;
import java.net.*;

public class Redirecting{
	
	public static void main(String [] args){
		while(true){
			byte [] message = new byte[100];
			DatagramPacket packet = new DatagramPacket(message, message.length);
			try{
				DatagramSocket socket = new DatagramSocket(48186);	
				try{
					socket.receive(packet);
				}
				catch(IOException e){
					e.printStackTrace();
				}
				socket.close();
			}
			catch(SocketException e){
				e.printStackTrace();
			}
			packet.setAddress(args[0]);
			packet.setPort(48182);
			try{
                DatagramSocket socket = new DatagramSocket(48181);
                socket.connect(args[0], 48182);
                socket.send(packet);
                socket.close();
            }
            catch(SocketException se){
            	e.printStackTrace();
            }
            catch(IOException ioe){
            	e.printStackTrace();
            }
		}

	}

}