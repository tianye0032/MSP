package MSP.data.gui;

import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import MSP.data.DataOwner;
import MSP.data.LocalServer;

public class Main implements  ActionListener{

	static JPanel base;
	static JButton next;
	static JFrame frame;
	static int step;
	static TextField query;
	static TextField serverOutputs;
	static TextField dataownerOutput;	
	static JTextArea coefficients;
	static int serverNum;
	static DataOwner owner;
	static LocalServer[] locals;
	public Main(){
		
	}
	public static void init(){
		serverNum = 5;
		owner  = new DataOwner("data/OriginalData");
		locals = new LocalServer[serverNum];
		
		//Initialize the local servers
		for(int i=0;i<serverNum;i++)
			locals[i]= new LocalServer("data/LocalData1");
	}

    public JPanel creatPanel (){
        // We create a bottom JPanel to place everything on.
        JPanel GUI = new JPanel();
        GUI.setLayout(null);

//        methods = Configure.getImplementedMethods();
        query = new TextField();
//    	for(MappingMethod instance: methods)
//    		methodChoise.addItem(instance.getName());
        Label lblquery = new Label("Input Query : ");
        GUI.add(lblquery);
        lblquery.setSize(90,60);
        lblquery.setLocation(30,40);
    	GUI.add(query);
    	query.setSize(80, 30);
    	query.setLocation(130, 40);
    	
    	Label lbltrue = new Label("True Results : ");
        GUI.add(lbltrue);
        lbltrue.setSize(90,60);
        lbltrue.setLocation(30,80);
    	dataownerOutput = new TextField();
    	GUI.add(dataownerOutput);
    	dataownerOutput.setSize(200,30);
    	dataownerOutput.setLocation(130,80);
    	
    	Label lblservers = new Label("Servers Output : ");
        GUI.add(lblservers);
        lblservers.setSize(90,60);
        lblservers.setLocation(30,120);
        
        serverOutputs = new TextField();
        GUI.add(serverOutputs);
        serverOutputs.setSize(200,80);
        serverOutputs.setLocation(130,120);
    	
    	next = new JButton("Submit");
    	next.setSize(120, 30);
    	next.addActionListener(this);    	
    	GUI.add(next);
    	next.setLocation(260, 220);
    	GUI.setOpaque(true);
        return GUI;
    }
    public void actionPerformed(ActionEvent e) {
      if(e.getSource() == next)
      {
    	  String queryText = query.getText();
    	  System.out.println(queryText);
    	  String[] segs = queryText.split(",");
    	  try{
    		  if(segs.length!=2)throw new Exception("Wrong arguments number");
    		  int lower = Integer.parseInt(segs[0]);
    		  int upper = Integer.parseInt(segs[1]);
    		  List<Integer> result = owner.processQuery(lower, upper);
    		  //process local servers
    		  
    		  //String 
    		  serverOutputs.setText("");
    		  
    		  ///////////////////////////////////////////////////////
    		  
    		  
    		  
    		  String ret = "";
    		  for(int x:result)ret = ret+x+" ";
    		  dataownerOutput.setText(ret);
    		  System.out.println("Query Results : "+ret);
    	  }catch(Exception ee){
    		  System.out.println(ee.getMessage());
    	  }
    	  query.setText("lower,upper");
    	  System.out.println(0);
      }
    }
    private static void createAndShowGUI() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("Configuration Panel");

        //Create and set up the content pane.
        Main demo = new Main();
        base=demo.creatPanel();
    	
       
        frame.setContentPane(base);
//        frame.setContentPane(demo.createContentPane());
       
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(380, 290);
        frame.setVisible(true);
    }
//    public JPanel createServerNumPanel (){
//
//        // We create a bottom JPanel to place everything on.
//        JPanel numGUI = new JPanel();
//        numGUI.setLayout(null);
//        
//        
//        Label lblSV = new Label("Server Numbers : "); 
//        lblSV.setSize(100,25);       
//        numGUI.add(lblSV);
//        lblSV.setLocation(30, 15);
//        serverNum = new Choice();
//    	for(int i=3;i<8;i++)
//    		serverNum.addItem(i+"");
//    	
//    	serverNum.setSize(100, 30);
//    	serverNum.setLocation(110, 40);
//    	numGUI.add(serverNum);
//    	
//    	Label lblEN = new Label("Maximal Errors : "); 
//        lblEN.setSize(100,25);       
//        numGUI.add(lblEN);
//        lblEN.setLocation(30, 60);
//         
//        errorNum = new Choice();
//     	for(int i=1;i<4;i++)
//     		errorNum.addItem(i+"");
//     	errorNum.setSize(100, 30);
//     	errorNum.setLocation(110, 85);
//     	numGUI.add(errorNum);
//    	
//    	
//    	int selected = methodChoise.getSelectedIndex();
//    	switch (selected){
//    		case 0:
//    			serverNum.select(0);
//    			serverNum.setEnabled(false);
//    			errorNum.setEnabled(false);
//    			break;
//    		case 1://Do nothing
//    			errorNum.setEnabled(false);
//    			break;
//    		case 2:
//    			System.out.println("MergeSplit Method");
//    			break;
//    		case 3: 
//    			serverNum.select(4);
//    			serverNum.setEnabled(false);
//    			break;
//    		case 4:
//    			serverNum.select(4);
////    			serverNum.setEnabled(false);
//    			errorNum.select(1);
//    			break;
//    		default:System.out.println("DuplicateTwice Method Selected!");
//    	} 
//    	next = new JButton("Next");   	
//    	next.setSize(120, 30);
//    	next.addActionListener(this);
//    	numGUI.add(next);
//    	next.setLocation(220, 200);
//    	pre = new JButton("Previous");   	
//    	pre.setSize(120, 30);
//    	pre.addActionListener(this);
//    	numGUI.add(pre);
//    	pre.setLocation(220, 235);
//    	numGUI.setOpaque(true);
//        return numGUI;
//    }
//    
//    public JPanel createServerSelectionPanel (){
//
//        // We create a bottom JPanel to place everything on.
//        JPanel serverGUI = new JPanel();
//        serverGUI.setLayout(null);
//        Label lblCentral = new Label("Central : "); 
//        lblCentral.setSize(100,25);       
//        serverGUI.add(lblCentral);
//        lblCentral.setLocation(5, 25);
//        centralLocation = new TextField();
//        centralLocation.setSize(180,25);
//        centralLocation.addActionListener(this);
//        serverGUI.add(centralLocation);
//        centralLocation.setLocation(110, 20);
//        
//        Label lblDis = new Label("Distributed :"); 
//        lblDis.setSize(100,25);       
//        serverGUI.add(lblDis);
//        lblDis.setLocation(5, 55);
////        servers = 7;
//        serverLocations = new TextField[servers];
//        for(int i=0;i<servers;i++){
//        	serverLocations[i]=new TextField();
//        	serverLocations[i].setSize(160, 25);
//        	serverLocations[i].addActionListener(this);
//        	serverGUI.add(serverLocations[i]);
//        	serverLocations[i].setLocation(110, i*27+55);
//        }
//    	next = new JButton("Next");   	
//    	next.setSize(90, 30);
//    	next.addActionListener(this);
//    	serverGUI.add(next);
//    	next.setLocation(280, 200);
//    	
//    	pre = new JButton("Previous");   	
//    	pre.setSize(90, 30);
//    	pre.addActionListener(this);
//    	serverGUI.add(pre);
//    	pre.setLocation(280, 235);
//    	serverGUI.setOpaque(true);
//        return serverGUI;
//    }
//    public JPanel createAdditionalPanel (){
//
//        // To select how many malicious servers are tolerated,
//    	// and coefficients
//        JPanel additionGUI = new JPanel();
//        additionGUI.setLayout(null);
//        
////        Label lblEN = new Label("Maximal Errors : "); 
////        lblEN.setSize(100,25);       
////        additionGUI.add(lblEN);
////        lblEN.setLocation(30, 20);
////        
////        errorNum = new Choice();
////    	for(int i=1;i<(servers+1)/2;i++)
////    		errorNum.addItem(i+"");
////    	errorNum.setSize(100, 30);
////    	errorNum.setLocation(110, 45);
////    	additionGUI.add(errorNum);
//        
//        Label lblCoef = new Label("Coefficients : (use ',' and ';' to seperate)"); 
//        lblCoef.setSize(250,25);       
//        additionGUI.add(lblCoef);
//        lblCoef.setLocation(5, 60);
//        coefficients = new JTextArea();
//        coefficients.setSize(180,75);
////        coefficients.
////        coefficients.addActionListener(this);
//        additionGUI.add(coefficients);
//        coefficients.setLocation(110, 90);
//        
//        int p = errors*2;
//		int d = servers-p;
//		
//		double [][] coe=null;
//		if(d==2){
//			coe = ParityConfig.coe2;				
//		}else if(d==3){
//			coe = ParityConfig.coe3;
//		}else if(d ==4 ){
//			coe = ParityConfig.coe4;
//		}
//		String text = "";
//		for(int i=0;i<p;i++){
//			String line = "coefficent"+(i+1)+" ";
//			for(int j=0;j<d-1;j++){
//				line =line + coe[i][j]+",";
//			}
//			line = line + coe[i][d-1];
//			text = text + line + "\n";
//		}
//		coefficients.setText(text);
//		
//    	next = new JButton("Confirm");   	
//    	next.setSize(90, 30);
//    	next.addActionListener(this);
//    	additionGUI.add(next);
//    	next.setLocation(280, 200);
//    	
//    	pre = new JButton("Previous");   	
//    	pre.setSize(90, 30);
//    	pre.addActionListener(this);
//    	additionGUI.add(pre);
//    	pre.setLocation(280, 235);
//    	additionGUI.setOpaque(true);
//        return additionGUI;
//    }
//  public JPanel createSummaryPanel (){
//
//      // We create a bottom JPanel to place everything on.
//      JPanel summaryGUI = new JPanel();
//      summaryGUI.setLayout(null);
//
//      return summaryGUI;
//  }
//
//    // This is the new ActionPerformed Method.
//    // It catches any events with an ActionListener attached.
//    // Using an if statement, we can determine which button was pressed
//    // and change the appropriate values in our GUI.
//    public void actionPerformed(ActionEvent e) {
//        if(e.getSource() == next)
//        {
//        	base.setVisible(false);
//        	System.out.println(step);
//        	if(step==0)//Select a method
//        	{
//	        	int selected = methodChoise.getSelectedIndex();
////	        	config.setMethod(methods.get(methodChoise.getSelectedIndex()));
//	        	switch (selected){
//	        		case 0:
//	        			System.out.println("DuplicateTwice Method Selected!");
//	        			break;
//	        		case 1:
//	        			System.out.println("Duplicate Method Selected!");
//	        			break;
//	        		case 2:
//	        			System.out.println("Merge and Split Method Selected!");
//	        			break;
//	        		case 3: 
//	        			System.out.println("HammingCode Method Selected!");
//	        			break;
//	        		case 4:
//	        			System.out.println("QPC Method Selected!");
//	        			break;
//	        		default:System.out.println("DuplicateTwice Method Selected!");
//	        	}
//	        	base=this.createServerNumPanel();
//	        	base.setVisible(true);
//	        	frame.setContentPane(base);
//	        	
//        	}else if(step==1){//Determine the server numbers
//        		servers = Integer.parseInt(serverNum.getSelectedItem());
//        		errors = Integer.parseInt(errorNum.getSelectedItem());
//        		System.out.println(servers+" servers would be used!"+errors +" errors are allowed!");
//        		if(errors*2>=servers){
//        			System.out.println("Error Number is Too Large!");
//        			step = 0;
//        			base=this.createServerNumPanel();
//    	        	base.setVisible(true);
//    	        	frame.setContentPane(base);
//        		}else{
//	        		base=this.createServerSelectionPanel();
//		        	base.setVisible(true);
//		        	frame.setContentPane(base);
//        		}
//        	}else if(step==2){
////        		errors = Integer.parseInt(errorNum.getSelectedItem());
////        		System.out.println(errors+" malicious servers can be tolerated!");
//        		base=this.createAdditionalPanel();
//	        	base.setVisible(true);
//	        	frame.setContentPane(base);
//        	}else if(step==3){//Validating all parameters
//        		
//        		System.out.println(errors+" malicious servers can be tolerated!");
//        		int p = errors*2;
//        		int d = servers-p;
//        		
//        		double[][] coefts = new double[p][d];
//        		String coefText = coefficients.getText();
//        		
//        		try{
//        			String[] lines = coefText.split("\n");
//        			for(int i=0;i<p;i++){
//        				String line = lines[i].split(" ")[1];
//        				String[] nums = line.split(",");
//        				for(int j=0;j<d;j++){
//        					coefts[i][j]=Double.parseDouble(nums[j]);
//        				}
//        			}
////        			ParityConfig pc = new ParityConfig();
////        			ParityConfig.isValid(coefts);
//        		}catch(Exception er){
//        			step = 2;
//        			System.out.println("Coefficients Wrong!");
//            		base=this.createAdditionalPanel();
//    	        	base.setVisible(true);
//    	        	frame.setContentPane(base);
//        		}
//        		
//        	
////        		config.setCentralPath(centralLocation.getText());
//    			String[] dis = new String[servers];
//    			for(int i=0;i<servers;i++)
//    				dis[i]=serverLocations[i].getText();
//    			
////    			config.setDistributedPath(dis);  
//    			
//    			//Write to file
//    			Writer writer = new Writer(Configure.CONFIGPATH);
//    			int selected = methodChoise.getSelectedIndex();
//	        	
//	        	switch (selected){
//	        		case 0:
//	        			writer.writeline("MappingMethod	DuplicateTwiceMapping");
//	        			break;
//	        		case 1:
//	        			writer.writeline("MappingMethod	DuplicateMapping");
//	        			break;
//	        		case 2:
//	        			writer.writeline("MappingMethod	MergeSplitMapping");
//	        			break;
//	        		case 3: 
//	        			writer.writeline("MappingMethod	HammingCodeMapping");
//	        			break;
//	        		case 4:
//	        			writer.writeline("MappingMethod	ParityMapping");
//	        			break;
//	        		default:writer.writeline("MappingMethod	ParityMapping");
//	        	}
//	        	writer.writeline("CentralPath	"+centralLocation.getText());
//	        	String disPath = dis[0];
//	        	for(int i=1;i<servers;i++)
//	        		disPath=disPath+","+dis[i];
//	        	writer.writeline("DistributedPath	"+disPath);
//    			
//	        
//	        	writer.writeline("numDataServers	"+d);
//	        	writer.writeline("numParityServers	"+p);
//	        	
////	        	writer.writeline("coefficent1	0.18,0.32,0.5");
////	        	writer.writeline("coefficent2	0.2,0.3,0.5");
////	        	writer.writeline("coefficent3	0.5,0.4,0.1");
////	        	writer.writeline("coefficent4	0.35,0.45,0.2");
//	        	for(int i=0;i<p;i++){
//	    			String line = "coefficent"+(i+1)+"	";
//	    			for(int j=0;j<d-1;j++){
//	    				line =line + coefts[i][j]+",";
//	    			}
//	    			line = line + coefts[i][d-1];
//	    			writer.writeline(line);
//	    		}
//	        	writer.writeline("extend	100");
//	        	
//	        	config = new Configure(Configure.CONFIGPATH);
//	        	Configure.config = new Configure(Configure.CONFIGPATH);
//	        	base=this.createSummaryPanel();
//	        	base.setVisible(true);
//	        	frame.setContentPane(base);
//	        	CentralServer cs = new CentralServer(config);
//    			cs.start();
//        	}
//        				
//        	step++;
//        }
//        else if(e.getSource() == pre){        	
//        	base.setVisible(false);
//        	System.out.println("Go back!!");
//        	if(step==1){        		
//	        	base=this.createMethodSelectionPanel();
//	        	base.setVisible(true);
//	        	frame.setContentPane(base);	        	
//        	}else if(step ==2){
//        		base=this.createServerNumPanel();
//	        	base.setVisible(true);
//	        	frame.setContentPane(base);	  
//        	}else if(step == 3){
//        		base=this.createServerSelectionPanel();
//	        	base.setVisible(true);
//	        	frame.setContentPane(base);	 
//        	}
//        	step--;
//        }
//       
//    }
//
//    private static void createAndShowGUI() {
//
//        JFrame.setDefaultLookAndFeelDecorated(true);
//        frame = new JFrame("Configuration Panel");
//
//        //Create and set up the content pane.
//        Main demo = new Main();
//        base=demo.createMethodSelectionPanel();
//    	
//       
//        frame.setContentPane(base);
////        frame.setContentPane(demo.createContentPane());
//       
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(380, 290);
//        frame.setVisible(true);
//    }
//    
    

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
    	Main.init();
    	
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	Main.createAndShowGUI();
            }
        });
    }
}
