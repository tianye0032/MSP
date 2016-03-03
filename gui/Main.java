package MSP.gui;


import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.TextField;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import MSP.file.mapping.*;
import MSP.server.central.CentralServer;
import MSP.server.central.Configure;

public class Main extends Applet {
	Configure config = new Configure();
	List<MappingMethod> methods = config.getImplementedMethods();
	TextField t = new TextField(30);
	Choice c = new Choice();
	int count = 0;
	TextField center, ds1, ds2, ds3;
	Button confirm = new Button("Confirm");
	public void init() {
		t.setEditable(false);
		for(MappingMethod instance: methods)
			c.addItem(instance.getName());
		add(t);
		add(c);

	}
	public boolean action (Event evt, Object arg) {
		if(evt.target.equals(c)){
			t.setText("index: " +  c.getSelectedIndex() + (String)arg);
			if(c.getSelectedIndex()==1){
				config.setMethod(methods.get(c.getSelectedIndex()));
				drawDuplicate();
			}
		}else if(evt.target.equals(confirm)){
			config.setCentralPath(center.getText());
			String[] dis = new String[3];
			dis[0]=ds1.getText();
			dis[1]=ds2.getText();
			dis[2]=ds3.getText();
			config.setDistributedPath(dis);
			CentralServer cs = new CentralServer(config);
			cs.start();
			
		}
	
		return true;
	}
	public void drawDuplicate(){
		
		// a blank text field
		center = new TextField("Center",30);
		// blank field of 20 columns
		ds1 = new TextField("1 Distributed:", 30);
		// predefined text displayed
		ds2 = new TextField("2 Distributed:!",30);
		// predefined text in 30 columns
		ds3 = new TextField("3 Distributed: !", 30);
		this.add(center);
		this.add(ds1);
		this.add(ds2);
		this.add(ds3);
		
		this.add(confirm);
//		confirm.addMouseListener(this);
		this.repaint();
	}
}