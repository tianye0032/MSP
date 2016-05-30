package MSP.gui;

import javax.swing.*;

import MSP.file.mapping.MappingMethod;
import MSP.server.central.CentralServer;
import MSP.server.central.Configure;


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
	static List<MappingMethod> methods;
	static Choice serverNum;
	static TextField[] serverLocations;
	static TextField centralLocation;	
	public Main(){
		
	}
	public static void init(){
		config = new Configure();
		step = 0;
	}

    public JPanel createMethodSelectionPanel (){
        // We create a bottom JPanel to place everything on.
        JPanel methodGUI = new JPanel();
        methodGUI.setLayout(null);

        methods = config.getImplementedMethods();
        methodChoise = new Choice();
    	for(MappingMethod instance: methods)
    		methodChoise.addItem(instance.getName());
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
    			System.out.println("QPC Method Selected1");
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
	        	config.setMethod(methods.get(methodChoise.getSelectedIndex()));
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
        		config.setCentralPath(centralLocation.getText());
    			String[] dis = new String[servers];
    			for(int i=0;i<servers;i++)
    				dis[i]=serverLocations[i].getText();
    			
    			config.setDistributedPath(dis);    			
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
/*public class Main implements  ActionListener{
   

    public JPanel createContentPane (){

        // We create a bottom JPanel to place everything on.
        JPanel totalGUI = new JPanel();
        totalGUI.setLayout(null);

        // Creation of a Panel to contain the title labels
        titlePanel = new JPanel();
        titlePanel.setLayout(null);
        titlePanel.setLocation(10, 0);
        titlePanel.setSize(250, 30);
        totalGUI.add(titlePanel);

        redLabel = new JLabel("Red Team");
        redLabel.setLocation(0, 0);
        redLabel.setSize(120, 30);
        redLabel.setHorizontalAlignment(0);
        redLabel.setForeground(Color.red);
        titlePanel.add(redLabel);

        blueLabel = new JLabel("Blue Team");
        blueLabel.setLocation(130, 0);
        blueLabel.setSize(120, 30);
        blueLabel.setHorizontalAlignment(0);
        blueLabel.setForeground(Color.blue);
        titlePanel.add(blueLabel);

        // Creation of a Panel to contain the score labels.
        scorePanel = new JPanel();
        scorePanel.setLayout(null);
        scorePanel.setLocation(10, 40);
        scorePanel.setSize(260, 30);
        totalGUI.add(scorePanel);

        redScore = new JLabel(""+redScoreAmount);
        redScore.setLocation(0, 0);
        redScore.setSize(120, 30);
        redScore.setHorizontalAlignment(0);
        scorePanel.add(redScore);

        blueScore = new JLabel(""+blueScoreAmount);
        blueScore.setLocation(130, 0);
        blueScore.setSize(120, 30);
        blueScore.setHorizontalAlignment(0);
        scorePanel.add(blueScore);

        // Creation of a Panel to contain all the JButtons.
        buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setLocation(10, 80);
        buttonPanel.setSize(260, 70);
        totalGUI.add(buttonPanel);

        // We create a button and manipulate it using the syntax we have
        // used before. Now each button has an ActionListener which posts 
        // its action out when the button is pressed.
        redButton = new JButton("Red Score!");
        redButton.setLocation(0, 0);
        redButton.setSize(120, 30);
        redButton.addActionListener(this);
        buttonPanel.add(redButton);

        blueButton = new JButton("Blue Score!");
        blueButton.setLocation(130, 0);
        blueButton.setSize(120, 30);
        blueButton.addActionListener(this);
        buttonPanel.add(blueButton);

        resetButton = new JButton("Reset Score");
        resetButton.setLocation(0, 40);
        resetButton.setSize(250, 30);
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);
        
        totalGUI.setOpaque(true);
        return totalGUI;
    }

    // This is the new ActionPerformed Method.
    // It catches any events with an ActionListener attached.
    // Using an if statement, we can determine which button was pressed
    // and change the appropriate values in our GUI.
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == redButton)
        {
            redScoreAmount = redScoreAmount + 1;
            redScore.setText(""+redScoreAmount);
        }
        else if(e.getSource() == blueButton)
        {
            blueScoreAmount = blueScoreAmount + 1;
            blueScore.setText(""+blueScoreAmount);
        }
        else if(e.getSource() == resetButton)
        {
            redScoreAmount = 0;
            blueScoreAmount = 0;
            redScore.setText(""+redScoreAmount);
            blueScore.setText(""+blueScoreAmount);
        }
    }

    private static void createAndShowGUI() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("[=] JButton Scores! [=]");

        //Create and set up the content pane.
        Main demo = new Main();
        frame.setContentPane(demo.createContentPane());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(280, 190);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
*/