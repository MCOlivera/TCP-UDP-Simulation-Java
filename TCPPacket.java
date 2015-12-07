public class TCPPacket {
	static final String SEPARATOR = " ";

	int synchronization_number = 0;
	int acknowledgement_number = 0;
	int synchronization_bit = 0;
	int acknowledgement_bit = 0;
	int finish_bit = 0;
	int window_size = 0;
	String body = "";

	public TCPPacket(){}

	public TCPPacket(String contents){
		String components[] = contents.split(TCPPacket.SEPARATOR);

		this.synchronization_number = Integer.parseInt(components[0]);
		this.acknowledgement_number = Integer.parseInt(components[1]);
		this.synchronization_bit = Integer.parseInt(components[2]);
		this.acknowledgement_bit = Integer.parseInt(components[3]);
		this.finish_bit = Integer.parseInt(components[4]);
		this.window_size = Integer.parseInt(components[5].split("[^(0-9)]")[0]);

		if(components.length == 7)
			this.body = components[6];
	}

	public void printContent(){
		System.out.println("Synchronization Number: " + this.synchronization_number);
		System.out.println("Acknowledgement Number: " + this.acknowledgement_number);
		System.out.println("Synchronization Bit: " + this.synchronization_bit);
		System.out.println("Acknowledgement Bit: " + this.acknowledgement_bit);
		System.out.println("Finish Bit: " + this.finish_bit);
		System.out.println("Window Size: " + this.window_size);
		System.out.println("Body: " + this.body);
	}

	public String getHeader(String data){
		return(synchronization_number + TCPPacket.SEPARATOR +
			acknowledgement_number + TCPPacket.SEPARATOR +  
			synchronization_bit + TCPPacket.SEPARATOR + 
			acknowledgement_bit + TCPPacket.SEPARATOR + 
			finish_bit + TCPPacket.SEPARATOR +
			window_size + TCPPacket.SEPARATOR +
			data);
	}

	public String getHeader(){
		return(synchronization_number + TCPPacket.SEPARATOR + 
			acknowledgement_number + TCPPacket.SEPARATOR +  
			synchronization_bit + TCPPacket.SEPARATOR + 
			acknowledgement_bit + TCPPacket.SEPARATOR + 
			finish_bit + TCPPacket.SEPARATOR +
			window_size + TCPPacket.SEPARATOR +
			body);
	}
}