import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import sun.awt.DefaultMouseInfoPeer;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.BoxLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.SwingConstants;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


public class Window {

	private JFrame frame;
	JList<String> list;
	ArrayList<String> modelArrayList;
	DefaultListModel<String> model;
	SerialPortManagement spm;
	boolean isSucceed = false;
	JLabel resultLabel = new JLabel("繋がってない");


	public void main(){
		resultLabel.setFont(new Font("Osaka", Font.BOLD, 24));
		//resultLabel.setBounds(106, 27, 219, 50);
		initialize();
	}

	/**
	 * Create the application.
	 */
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("せんさー");
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		frame.setSize(new Dimension(450,300));
		//frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		modelArrayList = SerialPortManagement.getPortIDs();
		model = new DefaultListModel<String>();
		for(String str : modelArrayList){//こいつをコメントアウトしないとwindowBuilderが起動しない
			System.out.println(str);
			model.add(0,str);
		}
		list = new JList<String>(model);

		JScrollPane scrollPane = new JScrollPane(list);
		//scrollPane.setBounds(50, 112, 350, 100);
		frame.add(scrollPane, BorderLayout.CENTER);

		//resultLabel.setBounds(100, 50, 200, 50);
		frame.add(resultLabel, BorderLayout.NORTH);

		JButton btnNewButton = new JButton("Connect");

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String portID = list.getSelectedValue();
				int index = list.getSelectedIndex();
				if (portID == null){
					return;
				}else{
					try {
						System.out.println(portID);
						if(spm != null){
							spm.close();
						}
						spm = new SerialPortManagement();
						if(spm.openPort(portID, "Window")){
							resultLabel.setText("繋がったヽ(^o^)丿");
							System.out.println("繋がった");
						}else{
							resultLabel.setText("繋がってない");
							System.out.println("繋がってない");
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				list.ensureIndexIsVisible(index + 1);
			}
		});
		//btnNewButton.setBounds(131, 222, 200, 30);
		frame.add(btnNewButton, BorderLayout.SOUTH);
		frame.repaint();



		//		JPanel panel = new JPanel();
		//		scrollPane.setColumnHeaderView(panel);
	}
}
