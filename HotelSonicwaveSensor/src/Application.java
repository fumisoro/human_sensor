import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.TooManyListenersException;

public class Application implements SerialPortEventListener {

	protected SerialPort port;
	protected InputStream is;
	protected OutputStream os;

	Application() throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, TooManyListenersException, IOException{
		CommPortIdentifier id = CommPortIdentifier.getPortIdentifier("/dev/cu.usbserial-A501KBM8");
		port = (SerialPort)id.open("ApplicationName", 1000);

		port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

		port.addEventListener(this);
		port.notifyOnDataAvailable(true);//シリアルポートがデータを受信した時に教えてくれるように

		is = port.getInputStream();
		os = port.getOutputStream();
	}

	public void close(){
		port.close();
		System.out.println("close");
	}

	public void read() throws IOException{
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String str;
		while((str = br.readLine()) != null){
			System.out.println(str);
		}
		is.close();
		close();
	}

	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}