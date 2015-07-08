import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.awt.*;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.DefaultListModel;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;


public class SerialPortManagement implements Runnable ,SerialPortEventListener{

	private CommPortIdentifier portID = null;
	private SerialPort port = null;
	private InputStream is = null;
	private OutputStream os = null;
	private static final int WAIT = 5000;
	private long prevTime = Calendar.getInstance().getTimeInMillis();
	private long prevTime1 = Calendar.getInstance().getTimeInMillis();
	private int sonicTemp = 0;
	private int sonicTemp1 = 0;
	private int irTemp = 0;
	private int soundCnt = 0;
	private int variableVal = 0;
	private int diffMax = 0;//デバック用
	private boolean flg = false;//デバック用
	private int cnt = 0;//デバック用

	public SerialPortManagement() throws Exception{

	}

	public void run(){
		while(true){
			try {
				//calc(this.serialInputInt());
			} catch (Exception e) {
			}
		}
	}



	public void calc(int values[]){
		int distance = 0;
		int diff;
		for(int i = 0; i < values.length; i++){
			//			System.out.println("value["+i+"] = " + values[i]);
		}

		try{
			if(values[0] == 0){
				Toolkit.getDefaultToolkit().beep();
			}

			//			System.out.println("values : "+duration);
			if(values[0] > 0){
				//				System.out.println("In");
				distance = values[0]/2;
				distance = distance*340*100/1000000;
				diff = Math.abs(sonicTemp - values[0]);
				//				System.out.println("diff is : " +diff);
				System.out.println("values :" + values[0]);
				System.out.println("distance : " + distance);
				if( diff> 200 &&  diff < 300) {
					long currentTime = Calendar.getInstance().getTimeInMillis();
					//					System.out.println("timediff is : "+(currentTime - prevTime));

					if(currentTime - prevTime > 10) {
						System.out.println("BEEP!!!!!");
						Toolkit.getDefaultToolkit().beep();
						//						SerialPortManagement spm2 = new SerialPortManagement("/dev/cu.usbserial-A501KBM8", "SerialPortManagement");
						//						byte[] bytes = "1234".getBytes();
						//						spm2.os.write(bytes);	// 出力ストリームにバイト列を書き込む
						//						spm2.os.flush();		// 出力ストリームをフラッシュ
					}
					prevTime = currentTime;
					sonicTemp = values[0];
				}
			}
		}catch(Exception e){

		}
	}


	public boolean openPort(String portID, String fileName){
		try{
			this.prevTime = Calendar.getInstance().getTimeInMillis();
			this.portID = CommPortIdentifier.getPortIdentifier(portID);
			this.port = (SerialPort)this.portID.open(fileName, WAIT);		
			this.port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			this.port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			this.is = this.port.getInputStream();
			this.os = this.port.getOutputStream();
			this.port.addEventListener(this);
			port.notifyOnDataAvailable(true);

			//			SerialPortManagement spm2 = new SerialPortManagement("/dev/cu.usbserial-A501KBM8", "SerialPortManagement");
			//			byte[] bytes = "1234".getBytes();
			//			spm2.os.write(bytes);	// 出力ストリームにバイト列を書き込む
			//			spm2.os.flush();		// 出力ストリームをフラッシュ


		}catch(Exception e){
			return false;
		}
		return true;
	}


	public void test() throws Exception{
		int c;
		while((c = this.is.read()) != -1){
			System.out.println(c);
		}
	}

	public void close() throws Exception{
		port.close();
		is.close();
		os.close();
		System.out.println("close");
	}

	public void read() throws Exception{
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String str;
		System.out.println("始まり");
		while((str = br.readLine()) != null){
			//System.out.println("str "+str);
			ArrayList<Integer> values = new ArrayList<>();
			String c = "/";
			values = getIntValues(str, c);
			//			System.out.println(values);
			//			System.out.println("read values are" + values);
			compareSonicwave(values.get(0));
			compareSonicwave1(values.get(1));
		}
		//		System.out.println("おわり");
		is.close();
		close();
	}

	public boolean compareIr(int irVal) throws LineUnavailableException{
		int ndiff;
		int diff = Math.abs(irTemp - irVal);
		if(diff > diffMax && cnt > 10){diffMax = diff;cnt++;}
		//		System.out.println("Ir diff : "+irTemp+" - "+irVal+" = "+diff);
		if(irVal > 380){
			return false; 
		}
		if(diff > 60 && soundCnt == 0){
			soundCnt = 1;
			variableVal = irVal;
		}else if(soundCnt == 1){
			//			System.out.println("変わらない値1  "+variableVal);
			soundCnt = 0;
			ndiff = Math.abs(variableVal - irVal);
			if(ndiff < 30){
				soundCnt = 2;
			}else{
				soundCnt = 0;
			}
		}else if(soundCnt == 2){
			//			System.out.println("変わらない値2  "+variableVal);
			ndiff = Math.abs(variableVal - irVal);
			if(ndiff < 30){
				soundCnt = 3;
			}else{
				soundCnt = 0;
			}
		}else if(soundCnt == 3){
			//			System.out.println("変わらない値3  "+variableVal);
			ndiff = Math.abs(variableVal - irVal);
			if(ndiff < 30){
				Sound.ring("G");
			}
			soundCnt = 0;
		}else{
			soundCnt = 0;
		}
		irTemp = irVal;
		return true;
	}

	public boolean compareSonicwave(int duration) throws LineUnavailableException{
		if(duration > 0){
			int distance;
			int diff;
			distance = duration/2;
			distance = distance*340*100/1000000;
			diff = Math.abs(sonicTemp - distance);
			if(distance < 5) return false;
			//			System.out.println("diff is : " +diff);
			//			System.out.println("duration :" + duration);
			//			System.out.println("distance : " + distance);
			//			System.out.println("Sonic diff : " +sonicTemp+" - "+distance+" = "+diff);
			if( diff> 30 &&  diff < 300) {
				long currentTime = Calendar.getInstance().getTimeInMillis();

				//				System.out.println("timediff is : "+(currentTime - prevTime));

				if(currentTime - prevTime > 1000) {
					System.out.println("BEEP!!!!!");
					System.out.println("distance: "+distance);
					System.out.println("sonicTemp: "+sonicTemp);
					Sound.ring("C");
				}
				prevTime = currentTime;
			}else if(distance == 0){
				Toolkit.getDefaultToolkit().beep();
			}
			sonicTemp = distance;
		}
		return true;
	}

	public boolean compareSonicwave1(int duration) throws LineUnavailableException{
		if(duration > 0){
			int distance;
			int diff;
			distance = duration/2;
			distance = distance*340*100/1000000;
			diff = Math.abs(sonicTemp1 - distance);
			if(distance < 5) return false;
			//			System.out.println("diff is : " +diff);
			//			System.out.println("duration :" + duration);
			//			System.out.println("distance : " + distance);
			//			System.out.println("Sonic diff : " +sonicTemp1+" - "+distance+" = "+diff);
			if( diff> 30 &&  diff < 300) {
				long currentTime = Calendar.getInstance().getTimeInMillis();
				//				System.out.println("timediff is : "+(currentTime - prevTime));

				if(currentTime - prevTime1 > 1000) {
					System.out.println("distance: "+distance);
					System.out.println("sonicTemp1: "+sonicTemp1);
					System.out.println("BEEP!!!!!");
					Sound.ring("C");
				}
				prevTime1 = currentTime;
			}else if(distance < 5){
				Toolkit.getDefaultToolkit().beep();
			}
			sonicTemp1 = distance;
		}
		return true;
	}

	public ArrayList<Integer> getIntValues(String str, String c) throws NumberFormatException{
		ArrayList<Integer> values = new ArrayList<>();
		str = str.trim();
		String [] data = str.split(c);
		if(data.length != 2) {
			values.add(139);
			values.add(94);
		}
		try{
			for(int i = 0; i < data.length; i++){
				values.add(Integer.parseInt(data[i]));
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			System.out.println("data.length: "+data.length);
		}
		return values;
	}

	//シリアル通信が始まったら呼び出される。
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				read();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<String> getPortIDs(){
		ArrayList<String> modelArray = new ArrayList<>();
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();

		CommPortIdentifier portID;
		while(portList.hasMoreElements()){
			// リストからポートを取り出す
			portID = (CommPortIdentifier)portList.nextElement();

			if(!portID.isCurrentlyOwned()){
				if(portID.getPortType() == CommPortIdentifier.PORT_SERIAL){
					String str = portID.getName();
					//					System.out.println(portID.getName());
					//					System.out.println(str);
					if(str.startsWith("/dev/tty")){// /dev/cu.*は表示させない
						modelArray.add(str);
						//						System.out.println(str);
					}
				}
			}
		}
		System.out.println("model return");
		return modelArray;
	}


	//使ってない。arduinoからのInputをstringで扱えてなかった時のやつ
	public int[] serialInputInt() throws Exception{
		int c;
		int values[] = new int[2];
		while((c = this.is.read()) != -1){
			if((char)c != '/'){
				c -= 48;
				values[0] *= 10;
				values[0] += c;
			}else{

				if((char)c != '\n' ){
					c -= 48;
					values[1] *= 10;
					values[1] += c;
				}else{
					return values;
				}
			}
		}
		return null;
	}
}