package MSP.gui;

import javax.swing.*;

import MSP.file.mapping.MappingMethod;
import MSP.server.central.CentralServer;
import MSP.server.central.Configure;
import MSP.utils.Writer;

import java.awt.Choice;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;

public class Main implements  ActionListener{
	static Configure config;
	static JPanel base;
	static JButton next;
	static Choice methodChoise;
	static JFrame frame;
	static int step,servers;
//	static List<MappingMethod> methods;
	static Choice serverNum;
	static TextField[] serverLocations;
	static TextField centralLocation;	
	public Main(){
		
	}
	public static void init(){
//		config = new Configure();
		step = 0;
	}

    public JPanel createMethodSelectionPanel (){
        // We create a bottom JPanel to place everything on.
        JPanel methodGUI = new JPanel();
        methodGUI.setLayout(null);

//        methods = Configure.getImplementedMethods();
        methodChoise = new Choice();
//    	for(MappingMethod instance: methods)
//    		methodChoise.addItem(instance.getName());
        methodChoise.addItem("DuplicateTwiceMapping");
        methodChoise.addItem("DuplicateMapping");
        methodChoise.addItem("MergeSplitMapping");
        methodChoise.addItem("HammingCodeMapping");
        methodChoise.addItem("ParityMapping");
    	methodGUI.add(methodChoise);
    	methodChoise.setSize(300, 30);
    	methodChoise.setLocation(30, 40);
    	next = new JButton("Next");
    	next.setSize(120, 30);
    	next.addActionListener(this);    	
    	methodGUI.add(next);
    	next.setLocation(220, 200);
    	methodGUI.setOpaque(true);
        return methodGUI;
    }
    public JPanel createServerNumPanel (){

        // We create a bottom JPanel to place everything on.
        JPanel numGUI = new JPanel();
        numGUI.setLayout(null);
        
        serverNum = new Choice();
    	for(int i=3;i<8;i++)
    		serverNum.addItem(i+"");
    	serverNum.setSize(300, 30);
    	serverNum.setLocation(30, 40);
    	numGUI.add(serverNum);
    	int selected = methodChoise.getSelectedIndex();
    	switch (selected){
    		case 0:
    			serverNum.select(0);
    			serverNum.setEnabled(false);
    			break;
    		case 1://Do nothing
    			break;
    		case 2:
    			System.out.println("MergeSplit Method");
    			break;
    		case 3: 
    			serverNum.select(4);
    			serverNum.setEnabled(false);
    			break;
    		case 4:
    			serverNum.select(4);
    			serverNum.setEnabled(false);
    			break;
    		default:System.out.println("DuplicateTwice Method Selected1");
    	}
    	
    	
    	
    	
    	next = new JButton("Next");   	
    	next.setSize(120, 30);
    	next.addActionListener(this);
    	numGUI.add(next);
    	next.setLocation(220, 200);
    	numGUI.setOpaque(true);
        return numGUI;
    }
    
    public JPanel createServerSelectionPanel (){

        // We create a bottom JPanel to place everything on.
        JPanel serverGUI = new JPanel();
        serverGUI.setLayout(null);
        Label lblCentral = new Label("Central : "); 
        lblCentral.setSize(100,25);       
        serverGUI.add(lblCentral);
        lblCentral.setLocation(5, 25);
        centralLocation = new TextField();
        centralLocation.setSize(180,25);
        centralLocation.addActionListener(this);
        serverGUI.add(centralLocation);
        centralLocation.setLocation(110, 20);
        
        Label lblDis = new Label("Distributed :"); 
        lblDis.setSize(100,25);       
        serverGUI.add(lblDis);
        lblDis.setLocation(5, 55);
//        servers = 7;
        serverLocations = new TextField[servers];
        for(int i=0;i<servers;i++){
        	serverLocations[i]=new TextField();
        	serverLocations[i].setSize(160, 25);
        	serverLocations[i].addActionListener(this);
        	serverGUI.add(serverLocations[i]);
        	serverLocations[i].setLocation(110, i*27+55);
        }
    	
    	
    	
    	
    	
    	next = new JButton("Confirm");   	
    	next.setSize(90, 30);
    	next.addActionListener(this);
    	serverGUI.add(next);
    	next.setLocation(280, 220);
    	serverGUI.setOpaque(true);
        return serverGUI;
    }
  public JPanel createSummaryPanel (){

      // We create a bottom JPanel to place everything on.
      JPanel summaryGUI = new JPanel();
      summaryGUI.setLayout(null);

      return summaryGUI;
  }

    // This is the new ActionPerformed Method.
    // It catches any events with an ActionListener attached.
    // Using an if statement, we can determine which button was pressed
    // and change the appropriate values in our GUI.
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == next)
        {
        	
        	base.setVisible(false);
        	System.out.println(step);
        	if(step==0)//Select a method
        	{
	        	int selected = methodChoise.getSelectedIndex();
//	        	config.setMethod(methods.get(methodChoise.getSelectedIndex()));
	        	switch (selected){
	        		case 0:
	        			System.out.println("DuplicateTwice Method Selected!");
	        			break;
	        		case 1:
	        			System.out.println("Duplicate Method Selected!");
	        			break;
	        		case 2:
	        			System.out.println("Merge and Split Method Selected!");
	        			break;
	        		case 3: 
	        			System.out.println("HammingCode Method Selected!");
	        			break;
	        		case 4:
	        			System.out.println("QPC Method Selected1");
	        			break;
	        		default:System.out.println("DuplicateTwice Method Selected1");
	        	}
	        	base=this.createServerNumPanel();
	        	base.setVisible(true);
	        	frame.setContentPane(base);
	        	
        	}else if(step==1){//Determine the server numbers
        		servers = Integer.parseInt(serverNum.getSelectedItem());
        		System.out.println(servers+" servers would be used!");
        		base=this.createServerSelectionPanel();
	        	base.setVisible(true);
	        	frame.setContentPane(base);
        	}else if(step==2){
//        		config.setCentralPath(centralLocation.getText());
    			String[] dis = new String[servers];
    			for(int i=0;i<servers;i++)
    				dis[i]=serverLocations[i].getText();
    			
//    			config.setDistributedPath(dis);  
    			
    			//Write to file
    			Writer writer = new Writer(Configure.CONFIGPATH);
    			int selected = methodChoise.getSelectedIndex();
	        	
	        	switch (selected){
	        		case 0:
	        			writer.writeline("MappingMethod	DuplicateTwiceMapping");
	        			break;
	        		case 1:
	        			writer.writeline("MappingMethod	DuplicateMapping");
	        			break;
	        		case 2:
	        			writer.writeline("MappingMethod	MergeSplitMapping");
	        			break;
	        		case 3: 
	        			writer.writeline("MappingMethod	HammingCodeMapping");
	        			break;
	        		case 4:
	        			writer.writeline("MappingMethod	ParityMapping");
	        			break;
	        		default:writer.writeline("MappingMethod	ParityMapping");
	        	}
	        	writer.writeline("CentralPath	"+centralLocation.getText());
	        	String disPath = dis[0];
	        	for(int i=1;i<servers;i++)
	        		disPath=disPath+","+dis[i];
	        	writer.writeline("DistributedPath	"+disPath);
    			
	        	writer.writeline("numDataServers	3");
	        	writer.writeline("numParityServers	4");
	        	writer.writeline("coefficent1	0.18,0.32,0.5");
	        	writer.writeline("coefficent2	0.2,0.3,0.5");
	        	writer.writeline("coefficent3	0.5,0.4,0.1");
	        	writer.writeline("coefficent4	0.35,0.45,0.2");
	        	writer.writeline("extend	100");
	        	
	        	config = new Configure(Configure.CONFIGPATH);
	        	Configure.config = new Configure(Configure.CONFIGPATH);
	        	base=this.createSummaryPanel();
	        	base.setVisible(true);
	        	frame.setContentPane(base);
	        	CentralServer cs = new CentralServer(config);
    			cs.start();
        	}else{
        		
        	}
        				
        	step++;
        }
       
    }

    private static void createAndShowGUI() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("Configuration Panel");

        //Create and set up the content pane.
        Main demo = new Main();
        base=demo.createMethodSelectionPanel();
    	
       
        frame.setContentPane(base);
//        frame.setContentPane(demo.createContentPane());
       
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(380, 290);
        frame.setVisible(true);
    }
    
    

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
    	Main.init();
    	
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
