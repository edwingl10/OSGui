import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class OsGui{
	JFrame frame;
	JPanel mainPanel, controls, osPanel, userPanel, diskPanel, printerPanel, directoryPanel,direct_panel[];
	JLabel user_labels[], disk_labels[], printer_labels[], speed;
	JButton speed_control;
	int offset = 1;
	
	OsGui(int Users, int Disks, int Printers) {
		user_labels = new JLabel[Users];
		disk_labels = new JLabel[Disks];
		printer_labels = new JLabel[Printers];
		direct_panel = new JPanel[Disks];
		
		//creates frame that is going to contain all GUI elements
		frame = new JFrame("OS141");
		frame.setSize(900,700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//creates main panel that will contain OS and directory manager
		mainPanel = new JPanel();
		//mainPanel.setLayout(new GridLayout(0,1));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		//initializes button for speed control
		speed_control = new JButton("Speed");
		speed_control.setPreferredSize(new Dimension(20,20));
		speed_control.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed_button_clicked();
			}
		});
		
		//contains the button for speed control
		controls = new JPanel();
		controls.setLayout(new GridLayout(0,2));
		speed = new JLabel("current speed: Normal");
		controls.add(speed);
		controls.add(speed_control);
		controls.setPreferredSize(new Dimension(10,40));
		mainPanel.add(controls,BorderLayout.PAGE_END);
	
		
		//creates panel that will contain OS GUI elements
		osPanel = new JPanel();
		osPanel.setLayout(new GridLayout(0,3));
		osPanel.setPreferredSize(new Dimension(440,340));
		
		//creates panel that will contain labels that represent users
		userPanel = new JPanel();
		Border border = BorderFactory.createTitledBorder(new LineBorder(Color.black,2),"USERS",TitledBorder.CENTER,
				TitledBorder.TOP);
		userPanel.setBorder(border);
		userPanel.setLayout(new GridLayout(0,1));
		
		//creates panel that will contain labels that represent disks
		diskPanel = new JPanel();
		Border border2 = BorderFactory.createTitledBorder(new LineBorder(Color.black,2),"DISKS", TitledBorder.CENTER,
				TitledBorder.TOP);
		diskPanel.setBorder(border2);
		diskPanel.setLayout(new GridLayout(0,1));
		
		//creates panel that will contain label that represent printers
		printerPanel = new JPanel();
		Border border3 = BorderFactory.createTitledBorder(new LineBorder(Color.black,2),"PRINTERS", TitledBorder.CENTER,
				TitledBorder.TOP);
		printerPanel.setBorder(border3);
		printerPanel.setLayout(new GridLayout(0,1));
		
		//creates panel that will contain directory manager
		directoryPanel = new JPanel();
		Border border4 = BorderFactory.createTitledBorder(new LineBorder(Color.red,5),"Directory Manager", TitledBorder.CENTER,
				TitledBorder.TOP);
		directoryPanel.setBorder(border4);
		directoryPanel.setLayout(new GridLayout(0,2));
		directoryPanel.setPreferredSize(new Dimension(440,340));
		
		//initializes user labels 
		for(int i=0; i<Users; i++) {
			userPanel.add(new JLabel("User"+(i+1)+":"));
			user_labels[i] = new JLabel("IDLE");
			userPanel.add(user_labels[i]);
			user_labels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		//initializes disk labels
		for(int i=0; i<Disks; i++) {
			diskPanel.add(new JLabel("Disk"+(i+1)+":"));
			disk_labels[i] = new JLabel("EMPTY");
			diskPanel.add(disk_labels[i]);
			disk_labels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			//initializes sub panel for disks in directory manager
			direct_panel[i] = new JPanel();
			Border sub_border = BorderFactory.createTitledBorder(new LineBorder(Color.black,2),"Disk"+(i+1)+":", 
					TitledBorder.CENTER, TitledBorder.TOP);
			direct_panel[i].setBorder(sub_border);
			direct_panel[i].setLayout(new GridLayout(0,1));
			JScrollPane scrollPane = new JScrollPane(direct_panel[i], ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			directoryPanel.add(scrollPane);
		}
		
		//initializes printer labels
		for(int i=0; i<Printers; i++) {
			printerPanel.add(new JLabel("Printer"+(i+1)+":"));
			printer_labels[i] = new JLabel("IDLE");
			printerPanel.add(printer_labels[i]);
			printer_labels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		
		
	}
	
	void display_gui(){
		//adds users, disks and printers to os panel
		osPanel.add(userPanel);
		osPanel.add(diskPanel);
		osPanel.add(printerPanel);
		
		//add os panel and directory panel to the main panel
		mainPanel.add(osPanel);
		mainPanel.add(directoryPanel);
		
		frame.add(mainPanel);
		frame.setVisible(true);
	}
	
	synchronized void update_user_label(int index, String message) {
		user_labels[index].setText(message);
	}
	
	synchronized void update_disk_label(int index, String message) {
		disk_labels[index].setText(message);
	}
	
	synchronized void update_printer_label(int index, String message) {
		printer_labels[index].setText(message);
	}
	
	synchronized void add_directory_label(int index, String message) {
		direct_panel[index].add(new JLabel(message));
		frame.validate();
		frame.repaint();
	}
	
	//changes speed status and updates offset 
	void speed_button_clicked() {
		switch(offset) {
		case 1: offset = 2; 
				speed.setText("2x Speed");
				break;
		case 2: offset = 3;
				speed.setText("3x Speed");
				break;
		default: offset = 1;
				speed.setText("Normal Speed");
		}
	}
	
	//returns offset used to calculate how much thread sleeps
	int get_offset() {
		return offset;
	}
	
	
}
