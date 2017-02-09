package downloadmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.event.*;
import java.util.Scanner;
import java.io.FileNotFoundException;


public class DownloadGUI extends JFrame implements Observer{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private URL z;
    private final DownloadList tableModel;
     
    private final JTable table;
   
    private Accelarator selectedDownload;
    private Accelarator dl;
    JMenuItem pause,resume,cancel,clear;
    String str,s1,s2,s3;
    int flag,flagg=0,i1,i2,i3,fl;
    
    private final JTextField addTextField = new JTextField(30);
    private final JTextField t1 = new JTextField(5);
    private final JTextField t2 = new JTextField(5);
    private final JTextField t3 = new JTextField(5);
    JTextField addT = new JTextField(35);
    JTextField browser = new JTextField(35);
    
    JButton addButton    = new JButton("Start Download");
    JButton saveButton   = new JButton("Save As");
    JButton o   = new JButton("OK");
    
    JPopupMenu popupMenu ;
    JMenuItem menuItemPause;
    JMenuItem menuItemResume;
    JMenuItem menuItemCancel;
    JMenuItem menuItemClear;
    JMenuItem menuItemClearDelete;
    JMenuItem open;
    JMenuItem openFolder;
    JMenuItem fileSaveItem ;
    JPanel textPanel, panelForTextField;
    JPanel text, panel;
    JPanel texttPanel;
    JLabel address = new JLabel("Address");
    JLabel url = new JLabel("URL: ");
    JLabel url1 = new JLabel("LINK: ");
    JLabel h = new JLabel("HH");
    JLabel m = new JLabel("MM");
    JLabel s = new JLabel("SS");
    JLabel am = new JLabel("AM/PM");
    
    private boolean clearing;
    
    public DownloadGUI() 
    {
    	
        
    	super("Download Manager");
    	
    	JMenuItem SubMenuNames,SubMenuSize,SubDefaultItem;
        setSize(640, 480);
        setLocationRelativeTo(null);//opens it in the centre of the screen...not at the top-left corner.
        JMenuBar menuBar = new JMenuBar();
        
        JMenu taskMenu = new JMenu("Tasks");
        JMenu fileMenu = new JMenu("File");
        
        //addButton.
        str = "";
        taskMenu.setMnemonic(KeyEvent.VK_T);
        
        JMenuItem fileAddItem = new JMenuItem("Add new download");
        JMenuItem s = new JMenuItem("Scheduler");
        
        fileAddItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        s.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        
        JMenuItem fileExitMenuItem = new JMenuItem("Exit");
        fileExitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        
        JMenuItem addFromBrowser = new JMenuItem("Add Browser Link");
        
        taskMenu.addSeparator();
        
        JMenuItem historyMenuItem= new JMenu("History");
        historyMenuItem.setMnemonic(KeyEvent.VK_A);
        //1st submenu
        SubMenuNames = new JMenuItem("by names");
        SubMenuNames.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_3, ActionEvent.ALT_MASK));
        historyMenuItem.add(SubMenuNames);
        //2nd submenu
        SubMenuSize = new JMenuItem("by size");
        SubMenuSize.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_4, ActionEvent.ALT_MASK));
        historyMenuItem.add(SubMenuSize);
        //3rd submenu
        SubDefaultItem = new JMenuItem("default");
        SubDefaultItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_5, ActionEvent.ALT_MASK));
        historyMenuItem.add(SubDefaultItem);
       
        
        
        
        fileMenu.setMnemonic(KeyEvent.VK_F);
        pause = new JMenuItem("Pause");
        resume = new JMenuItem("Resume");
        cancel = new JMenuItem("Cancel");
        clear = new JMenuItem("Clear");
        
        pause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        resume.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        cancel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        clear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
        
        
        pause.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actionPause();
                } catch (InterruptedException ex) {
                    Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        pause.setEnabled(false);
        resume.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                 actionResume();
            }
        });
        resume.setEnabled(false);
        cancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                 actionCancel();
            }
        });
        cancel.setEnabled(false);
        clear.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                 actionClear();
            }
        });
        clear.setEnabled(false);
        fileAddItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final JFrame frame = new JFrame("Enter new address to download");
                frame.setLocation(500,300);
                frame.setVisible(true);
                frame.setSize(550,145);
                frame.setResizable(false);
                frame.setContentPane(createContentPane());
               // JFrame.setDefaultLookAndFeelDecorated(true);
                addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        actionAdd();
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    frame.setVisible(false);
            }
        });
                saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionSaveTo();
            }
        });     
            }
        });
        
        
        s.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final JFrame f = new JFrame("Scheduler");
                f.setLocation(500,300);
                f.setVisible(true);
                f.setSize(550,175);
                f.setResizable(false);
                f.setContentPane(ContentPane());
               // JFrame.setDefaultLookAndFeelDecorated(true);
                o.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    flagg =1;
                    s1=t1.getText();
                    s2=t2.getText();
                    s3=t3.getText();
                   
                    i1=Integer.parseInt(s1);
                    i2=Integer.parseInt(s2);
                    i3=Integer.parseInt(s3);
                    try {
                        actionAdd();
                    } catch (IOException ex) {
                        Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    f.setVisible(false);
            }
        });
                
            }
        });
        
        SubDefaultItem.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e)
            {
				JFrame frame = new JFrame("History");
                String columns[] = {"Filename","Size","Date","Time"};
                frame.setBounds(0,0,500,500);
                
		        BufferedReader reader=null;
				try {
					reader = new BufferedReader(new FileReader("Default.txt"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String []parts = null;
		        String line=null;
		        try {
					while ((line = reader.readLine()) != null)
					{
						// System.out.println(line);
						 parts = line.split(" ");						
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        int check=0,j=-1,cnt=0;
		        
		        for (String part : parts) {
		        	cnt++;
		        }
		        Object data[][]= new Object[cnt/4][4];
		        
		        for (String part : parts) {
		        	if(check==0)
		        	{
		        		data[++j][0]=part;
		      	
		        		check=1;
		        	}
		        	else if(check==1)
		        	{
		        		data[j][1]=part;
		        		check=2;
		        	}
		        	else if(check==2)
		        	{
		        		data[j][2]=part;
		        		check=3;
		        	}
		        	else
		        	{
		        		data[j][3]=part;
		        		check=0;
		        	}
		        	
		        	//System.out.println(part);
		            
		        }
		        
                JTable table = new JTable(data,columns);                
                table.setGridColor(Color.GREEN);
                
               frame.setVisible(true);
                
               frame.add(table.getTableHeader(),BorderLayout.CENTER);
                
               frame.add(table);
             }
        });
        
        
        
        
        
        SubMenuNames.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e)
            {
				JFrame frame = new JFrame("By names");
                String columns[] = {"Filename","Size","Date","Time"};
                frame.setBounds(0,0,500,500);
                
		        BufferedReader reader=null;
				try {
					reader = new BufferedReader(new FileReader("Default.txt"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String []parts = null;
		        String line=null;
		        try {
					while ((line = reader.readLine()) != null)
					{
						// System.out.println(line);
						 parts = line.split(" ");						
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        int check=0,j=-1,cnt=0,ct=0;
		        
		        ArrayList <String> sizee = new ArrayList <String>();
		        for (String part : parts) {
		        	
		        	if(cnt%4==0)
		        	{
		        		sizee.add(part);
		        	}
		        	cnt++;
		        }
		        
		       
		        Collections.sort(sizee, new Comparator<String>() {
		            @Override
		            public int compare(String  fruite1, String  fruite2)
		            {
		            	String upper1 = fruite1.toUpperCase(); 
		            	String upper2=fruite2.toUpperCase();
		                return  upper1.compareTo(upper2);
		            }
		        });
		        BufferedWriter ws = null;
		        try {
				      ws=new BufferedWriter(new FileWriter("Names.txt"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        
		       for(String a: sizee)
		       {
		    	  // System.out.println(a);
		    	   
		    	   for(int i=0;i<parts.length;i++)
		    	   {
		    		   if(a.compareTo(parts[i])==0)
		    		   {
		    			try {
							ws.write(parts[i]+" "+parts[i+1]+" "+parts[i+2]+" "+parts[i+3]+" ");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		    			
                         break;
		    		   }
		    	   }
		    	   
		    	   
		    	   
		    	   
		       }
		       try {
				ws.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		        
		        BufferedReader reader1=null;
				try {
					reader1 = new BufferedReader(new FileReader("Names.txt"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String []parts1 = null;
		        String line1=null;
		        try {
					while ((line1 = reader1.readLine()) != null)
					{
						// System.out.println(line1);
						 parts1 = line1.split(" ");						
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        int check1=0,cnt1=0,j1=-1;
		        
		        for (String part1 : parts1) {
		        	cnt1++;
		        }
		       
		       
		       
		        Object data[][]= new Object[cnt1/4][4];
		        
		        for (String part1 : parts1) {
		        	if(check1==0)
		        	{
		        		data[++j1][0]=part1;
		      	
		        		check1=1;
		        	}
		        	else if(check1==1)
		        	{
		        		data[j1][1]=part1;
		        		check1=2;
		        	}
		        	else if(check1==2)
		        	{
		        		data[j1][2]=part1;
		        		check1=3;
		        	}
		        	else
		        	{
		        		data[j1][3]=part1;
		        		check1=0;
		        	}
		        	
		        	//System.out.println(part1);
		            
		        }
		        
                JTable table = new JTable(data,columns);                
                table.setGridColor(Color.GREEN);
                
               frame.setVisible(true);
                
               frame.add(table.getTableHeader(),BorderLayout.CENTER);
                
               frame.add(table);
             }
        });
        
        
        
        SubMenuSize.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e)
            {
				JFrame frame = new JFrame("By size");
                String columns[] = {"Filename","Size","Date","Time"};
                frame.setBounds(0,0,500,500);
                
		        BufferedReader reader=null;
				try {
					reader = new BufferedReader(new FileReader("Default.txt"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String []parts = null;
		        String line=null;
		        try {
					while ((line = reader.readLine()) != null)
					{
						// System.out.println(line);
						 parts = line.split(" ");						
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        int check=0,j=-1,cnt=0,ct=0;
		        
		        ArrayList <Integer> sizee = new ArrayList <Integer>();
		        for (String part : parts) {
		        	
		        	if(cnt%4==1)
		        	{
		        		sizee.add(Integer.parseInt(part));
		        		
		        	}
		        	cnt++;
		        }
		        
		       
		        Collections.sort(sizee, new Comparator<Integer>() {
		            @Override
		            public int compare(Integer  o1, Integer  o2)
		            {
		            	
		                return  (o1>o2 ? -1 : (o1==o2 ? 0 : 1));
		            }
		        });
		        
		        BufferedWriter ws = null;
		        try {
				      ws=new BufferedWriter(new FileWriter("Size.txt"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        
		       for(int a: sizee)
		       {
		    	  // System.out.println(a);
		    	   
		    	   for(int i=0;i<parts.length;i++)
		    	   {
		    		  
		    		   if(String.valueOf(a).compareTo(parts[i])==0)
		    		   {
		    			try {
							ws.write(parts[i-1]+" "+parts[i]+" "+parts[i+1]+" "+parts[i+2]+" ");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		    			
                         break;
		    		   }
		    	   }
		    	   
		    	   
		    	   
		    	   
		       }
		       try {
				ws.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		        
		        BufferedReader reader1=null;
				try {
					reader1 = new BufferedReader(new FileReader("Size.txt"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String []parts1 = null;
		        String line1=null;
		        try {
					while ((line1 = reader1.readLine()) != null)
					{
						// System.out.println(line1);
						 parts1 = line1.split(" ");						
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        int check1=0,cnt1=0,j1=-1;
		        
		        for (String part1 : parts1) {
		        	cnt1++;
		        }
		       
		       
		       
		        Object data[][]= new Object[cnt1/4][4];
		        
		        for (String part1 : parts1) {
		        	if(check1==0)
		        	{
		        		data[++j1][0]=part1;
		      	
		        		check1=1;
		        	}
		        	else if(check1==1)
		        	{
		        		data[j1][1]=part1;
		        		check1=2;
		        	}
		        	else if(check1==2)
		        	{
		        		data[j1][2]=part1;
		        		check1=3;
		        	}
		        	else
		        	{
		        		data[j1][3]=part1;
		        		check1=0;
		        	}
		        	
		        	//System.out.println(part1);
		            
		        }
		        
                JTable table = new JTable(data,columns);                
                table.setGridColor(Color.GREEN);
                
               frame.setVisible(true);
                
               frame.add(table.getTableHeader(),BorderLayout.CENTER);
                
               frame.add(table);
             }
        });
        
        addFromBrowser.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
            	JFrame frame = new JFrame("Browser Link");
            frame.setBounds(0,0,500,500);
 
                JPanel addPanell = new JPanel();
                
                url1.setFont(new Font("Serif",Font.BOLD,14));
                addPanell.add(url1);
                
                browser.setToolTipText("Enter the full URL here ");
                browser.setFont(new Font("Serif",Font.PLAIN,14));
                
                JButton addB = new JButton("Start Download");        
                
                addPanell.add(browser);
                addPanell.add(addB);                
                 
                
            browser.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                browser.setText(" ");
                if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) 
                {
                    //addT.setText(" ");
                    try {
                        fl=1;
                        //actionAdd();
                        actionBrowse();
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
                
             addB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //actionAdd();
                	actionBrowse();
                } catch (MalformedURLException ex) {
                    Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
                
                setLayout(new BorderLayout());
                frame.add(addPanell, BorderLayout.CENTER);
            	frame.setVisible(true);
            }
        });
        
       fileExitMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                actionExit();
            }
        });
        
        taskMenu.add(fileAddItem);
        taskMenu.add(s);
        taskMenu.add(addFromBrowser);
        taskMenu.add(historyMenuItem);
        
        taskMenu.add(fileExitMenuItem);
        fileMenu.add(pause);
        fileMenu.add(resume);
        fileMenu.add(clear);
        fileMenu.add(cancel);
        menuBar.add(taskMenu);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        //pop-up menu for each cell
        popupMenu = new JPopupMenu();
        menuItemPause = new JMenuItem("Pause");
        menuItemResume = new JMenuItem("Resume");
        menuItemCancel = new JMenuItem("Cancel");
        menuItemClear = new JMenuItem("Clear");
        menuItemClearDelete = new JMenuItem ("Clear And Delete");
        open = new JMenuItem ("Open");
        openFolder = new JMenuItem ("Open Folder");

        menuItemPause.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actionPause();
                } catch (InterruptedException ex) {
                    Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menuItemResume.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                 actionResume();
            }
        });
        menuItemCancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                 actionCancel();
            }
        });
        menuItemClear.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                 actionClear();
            }
        });
        
        menuItemClearDelete.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                 actionClearDelete();
            }
        });
         open.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actionOpen();
                } catch (IOException ex) {
                    Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
          openFolder.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actionOpenFolder();
                } catch (IOException ex) {
                    Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        popupMenu.add(menuItemPause);
        popupMenu.add(menuItemResume);
        popupMenu.add(menuItemCancel);
        popupMenu.add(menuItemClear);
        popupMenu.add(menuItemClearDelete);
        popupMenu.add(open);
        popupMenu.add(openFolder);
        
        // Initialization of the download table
        tableModel = new DownloadList();
        table = new JTable(tableModel);
        table.setComponentPopupMenu(popupMenu);
        table.setAutoCreateRowSorter(true);
        table.setGridColor(Color.GREEN);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
      
            @Override
            public void valueChanged(ListSelectionEvent e){
                RowChanged();
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel addPanell = new JPanel();
        
        url.setFont(new Font("Serif",Font.BOLD,14));
        addPanell.add(url);
        
        addT.setToolTipText("Enter the full URL here ");
        addT.setFont(new Font("Serif",Font.PLAIN,14));
        
        JButton addB = new JButton("Start Download");  
        addB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actionAdd();
                } catch (MalformedURLException ex) {
                    Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        addT.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                addT.setText(" ");
                if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) 
                {
                    //addT.setText(" ");
                    try {
                        fl=1;
                        actionAdd();
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        addPanell.add(addT);
        addPanell.add(addB);
        
        JPanel downloadsPanel = new JPanel();
        downloadsPanel.setLayout(new BorderLayout());
        downloadsPanel.add(new JScrollPane(table),BorderLayout.CENTER);
       
        setLayout(new BorderLayout());
        add(addPanell, BorderLayout.NORTH);
        add(downloadsPanel);
        
    }//end of constructor
    
    

    
    
    public void actionSaveTo()
    {
        JFileChooser jfchooser = new JFileChooser();
        jfchooser.setApproveButtonText("OK");
        jfchooser.setDialogTitle("Save As");
        jfchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//it shows the directories only
        
        //show opendialog method returns an int based on if anything is selected or not
        int result = jfchooser.showOpenDialog(this);// this is an instance of a Component such as JFrame,JPanelwhich is parent of the dialog.this refers to the parent component for the dialog. The parent component affects the position of the dialog and the frame that the dialog depends on.
        if (result == JFileChooser.APPROVE_OPTION)
        {
            str = jfchooser.getSelectedFile().getAbsolutePath();//To get the chosen file getSelectedFile method is used..This method returns an instance of File.file instance holds the full path
        }
    }
    private void actionExit() {

        System.exit(0);
    }
    
    private void RowChanged() 
    {
        if (selectedDownload != null) selectedDownload.deleteObserver(this);
        if (!clearing &&table.getSelectedRow()>-1)
        {
            selectedDownload = tableModel.getDownload(table.getSelectedRow());
            selectedDownload.addObserver(this);
            ChangeState();
        }
    }
    public void actionPause() throws InterruptedException
    {
        selectedDownload.pause();
        ChangeState();
    }
    public void actionResume() 
    {
        selectedDownload.resume();
        ChangeState();
    }
    public void actionCancel()
    {
        selectedDownload.cancel();
        ChangeState();
    }
    public void actionClear()
    {
        clearing = true;
        tableModel.clearDownload(table.getSelectedRow());
        clearing = false;
        selectedDownload = null;
        ChangeState();
    }
    public void actionClearDelete()
    {
        clearing = true;
        tableModel.clearDownloadDelete(table.getSelectedRow());
        clearing = false;
        selectedDownload = null;
        ChangeState();
    }
    public void actionOpen() throws IOException 
    {
        selectedDownload.openFile();
        ChangeState();
    }
    public void actionOpenFolder() throws IOException 
    {
        selectedDownload.openFolder();
        ChangeState();
    }
    public void ChangeState() 
    {
        if (selectedDownload != null) 
        {
            int statuses = selectedDownload.getStatus();
            switch (statuses)
            {
                case Download.DOWNLOADING:
                    pause.setEnabled(true);
                    resume.setEnabled(false);
                    cancel.setEnabled(true);
                    clear.setEnabled(false);
                    
                    menuItemPause.setEnabled(true);
                    menuItemResume.setEnabled(false);
                    menuItemCancel.setEnabled(true);
                    menuItemClear.setEnabled(false);
                    menuItemClearDelete.setEnabled(false);
                    open.setEnabled(false);
                    openFolder.setEnabled(false);
                    break;
                case Download.PAUSED:
                    pause.setEnabled(false);
                    resume.setEnabled(true);
                    cancel.setEnabled(true);
                    clear.setEnabled(false);
                    menuItemPause.setEnabled(false);
                    menuItemResume.setEnabled(true);
                    menuItemCancel.setEnabled(true);
                    menuItemClear.setEnabled(false);
                    menuItemClearDelete.setEnabled(false);
                    open.setEnabled(false);
                    openFolder.setEnabled(false);
                    break;
                case Download.ERROR:
                    pause.setEnabled(false);
                    resume.setEnabled(true);
                    cancel.setEnabled(false);
                    clear.setEnabled(true);
                    menuItemPause.setEnabled(false);
                    menuItemResume.setEnabled(true);
                    menuItemCancel.setEnabled(false);
                    menuItemClear.setEnabled(true);
                    menuItemClearDelete.setEnabled(true);
                    open.setEnabled(false);
                    openFolder.setEnabled(false);
                    break;
                case Download.COMPLETE: // COMPLETE or CANCELLED
                    pause.setEnabled(false);
                    resume.setEnabled(false);
                    cancel.setEnabled(false);
                    clear.setEnabled(true);
                    menuItemPause.setEnabled(false);
                    menuItemResume.setEnabled(false);
                    menuItemCancel.setEnabled(false);
                    menuItemClear.setEnabled(true);
                    menuItemClearDelete.setEnabled(true);
                    open.setEnabled(true);
                    openFolder.setEnabled(true);
                    break;
                    
                case Download.CANCELLED: // COMPLETE or CANCELLED
                    pause.setEnabled(false);
                    resume.setEnabled(false);
                    cancel.setEnabled(false);
                    clear.setEnabled(true);
                    menuItemPause.setEnabled(false);
                    menuItemResume.setEnabled(false);
                    menuItemCancel.setEnabled(false);
                    menuItemClear.setEnabled(true);
                    menuItemClearDelete.setEnabled(false);
                    open.setEnabled(false);
                    openFolder.setEnabled(false);
                    break;
            }
        }
        else
        {
            // No download is selected in table.
            pause.setEnabled(false);
            resume.setEnabled(false);
            cancel.setEnabled(false);
            clear.setEnabled(false);
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if (selectedDownload != null && selectedDownload.equals(o))
            ChangeState();
    }
    
    public static void main(String[] args) {
        DownloadGUI manager = new DownloadGUI();
        manager.setVisible(true);
        manager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public JPanel createContentPane ()
    {
        JPanel totalGUI = new JPanel();
        totalGUI.setLayout(null);
        totalGUI.setBackground(Color.white);
        
        textPanel = new JPanel();
        textPanel.setLayout(null);
        textPanel.setLocation(3,5);
        textPanel.setSize(80,35);
        textPanel.setBackground(Color.white);
        totalGUI.add(textPanel);
        
        address.setFont(new Font("Serif",Font.BOLD,16));
        address.setLocation(10,10);
        address.setSize(75,35);
        textPanel.add(address);
        
        panelForTextField = new JPanel();
        panelForTextField.setLayout(null);
        panelForTextField.setLocation(75,10);
        panelForTextField.setSize(360,46);
        panelForTextField.setBackground(Color.white);
        totalGUI.add(panelForTextField);

        addTextField.setToolTipText("Enter the full URL here ");
        addTextField.setFont(new Font("Serif",Font.PLAIN,14));
        addTextField.setLocation(10,10);
        addTextField.setSize(350,25);
        panelForTextField.add(addTextField);
        
        addButton.setLocation(190,70);
        addButton.setSize(125, 30);
        totalGUI.add(addButton);
        
        saveButton.setLocation(445,15);
        saveButton.setSize(85, 30);
        totalGUI.add(saveButton);
        
        totalGUI.setOpaque(true);    
        return totalGUI;
     }  
    
    public JPanel ContentPane ()
    {
        JPanel total = new JPanel();
        total.setLayout(null);
        total.setBackground(Color.white);
        
        text = new JPanel();
        text.setLayout(null);
        text.setLocation(35,60);
        text.setSize(420,35);
        text.setBackground(Color.white);
        total.add(text);
        
        h.setFont(new Font("Serif",Font.BOLD,20));
        h.setLocation(10,3);
        h.setSize(45,35);
        text.add(h);
        
        m.setFont(new Font("Serif",Font.BOLD,20));
        m.setLocation(125,3);
        m.setSize(45,35);
        text.add(m);
        
        s.setFont(new Font("Serif",Font.BOLD,20));
        s.setLocation(255,3);
        s.setSize(75,35);
        text.add(s);

        
        panel = new JPanel();
        panel.setLayout(null);
        panel.setLocation(35,10);
        panel.setSize(420,40);
        panel.setBackground(Color.white);
        total.add(panel);

        
        t1.setFont(new Font("Serif",Font.PLAIN,14));
        t1.setLocation(5,7);
        t1.setSize(60,25);
        panel.add(t1);
        
        t2.setFont(new Font("Serif",Font.PLAIN,14));
        t2.setLocation(125,7);
        t2.setSize(60,25);
        panel.add(t2);
        
        t3.setFont(new Font("Serif",Font.PLAIN,14));
        t3.setLocation(245,7);
        t3.setSize(60,25);
        panel.add(t3);
        
   
        
        o.setLocation(190,110);
        o.setSize(90, 25);
        total.add(o);
        
        
        
        total.setOpaque(true);    
        return total;
     }   

private void actionBrowse() throws MalformedURLException, IOException
{
	Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
    if(flagg==0 || fl==1)
   {
	    String s = null;
	
	    try 
	    {
	        s = (String) c.getData(DataFlavor.stringFlavor);
	    } 
	    catch (UnsupportedFlavorException | IOException ex)
	    {
	        Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
	    try
        {
            URL java = new URL(s);
            BufferedReader in = new BufferedReader( new InputStreamReader(java.openStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter("outputfile.txt"));
            
            String inputLine;
            int index=0;
            JFrame list = new JFrame("Download links");
            String columns[] = {"Links"};
            ArrayList <String> array = new ArrayList <String>();
            
            list.setBounds(0,0,500,500);
            
            while ((inputLine = in.readLine()) != null){
              //    System.out.println(inputLine);
                int j=0;
               // System.out.println(inputLine);
               
               while(true)
               {
            	   
                   int k=inputLine.indexOf("href=\"", j);
                   if(k==-1)
                       break;
                   int kk=inputLine.indexOf("\"", k+6);
                   if(kk==-1)
                       break;
                   if(k+6<inputLine.length())
                        {
                	   String aaa= inputLine.substring(k+6, kk);
                	   //System.out.println(aaa);
                	  // System.out.println(++index+" "+inputLine.substring(k+6, kk-1));
                	   String[]check ={".mp3",".mp4",".wmv",".png",".jpg",".jpeg",".flv",".wma",".pdf",".bmp"};
                	   for(String a: check)
                	   {
                	   if(aaa.endsWith(a))
                	   {
                		//   ac[++index][0]=aaa;
                		   index++;
                		   String ccc=aaa.replaceAll(" ", "%20");
                		   array.add(ccc);
                		  // System.out.println(index+" "+ccc);
                           break;                          
   
                	   }
                	   }
                        }
                   else 
                       break;
                   j=kk+1;
                   
                   
               }


               writer.write(inputLine);
            }
            Object ac [][] = new Object[index][1];
            index=-1;
            for(String a:array)
            {
            	ac[++index][0]=a;
            	
            }
            
            JTable table = new JTable(ac,columns);                
            table.setGridColor(Color.GREEN);
            list.add(table.getTableHeader(),BorderLayout.CENTER);
            
            list.add(table);
            
            list.setVisible(true);
            
            in.close();
            writer.close();
        }
	    catch (IOException ex) {
            Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

 private void actionAdd() throws MalformedURLException, IOException 
    {   
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
             if(flagg==0 || fl==1)
            {
             String s = null;

             try {
                 s = (String) c.getData(DataFlavor.stringFlavor);
             } catch (UnsupportedFlavorException | IOException ex) {
                 Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
             }

             try
             {
                 URL u = new URL(s);
                 if(u.toExternalForm().toLowerCase().startsWith("http://") || u.toExternalForm().toLowerCase().startsWith("ftp://"))
                 { //System.out.println("in add");
                    fl=0;    
                 dl = new Accelarator(u,str);

                     tableModel.addDownload(dl);
                     addTextField.setText(""); // reset add text field
                     addT.setText(" "); // reset add text field
                     str ="";
                     
                 }
                 else
                 {
                     JOptionPane.showMessageDialog(null,"Sorry..Only http and ftp protocol is allowed", "Error",JOptionPane.ERROR_MESSAGE);
                 }
             }
             catch(MalformedURLException e)
             {
                 JOptionPane.showMessageDialog(null,"Download Not Possible..Inavlid URL", "Error",JOptionPane.ERROR_MESSAGE);
             } 
            }
         else if(flagg==1)
         {
           Toolkit toolkit  = Toolkit.getDefaultToolkit();
            Timer timer = new Timer();
           timer.schedule(new scheduleDailyTask(),0,1000);
         
        
          }
    }
 
    class scheduleDailyTask extends TimerTask 
           {
            @Override
            public void run() 
                {
                     Date date = new Date();
                     Calendar c = Calendar.getInstance();
                     //System.out.println("check "+i1+" "+i2+" "+i3+" "+c.get(Calendar.HOUR)+" "+c.get(Calendar.MINUTE)+" "+c.get(Calendar.SECOND));
                     if(c.get(Calendar.HOUR)==i1 && c.get(Calendar.MINUTE)== i2 && c.get(Calendar.SECOND)==i3)
                        { //if (i1<=12 && i2<=59 && i3 <=59)
                    	// System.out.println("starting");
                         String m;
                          Scanner x=null;
                         try{
                               x = new Scanner(new File("link.txt"));
                           }catch(FileNotFoundException e){
                                 System.out.println("File Not Found");
                                 }
                
                            while(x.hasNext())
                             {
                                  m=x.next();
                          //      URL u;
                            try {
                               z = new URL(m);
                            } catch (MalformedURLException ex) {
                                ex.printStackTrace();
                            }
                            
                                 if(z.toExternalForm().toLowerCase().startsWith("http://") || z.toExternalForm().toLowerCase().startsWith("ftp://"))
                                 { 
                                     dl = new Accelarator(z,str);
                
                                tableModel.addDownload(dl);
                                addTextField.setText(""); // reset add text field
                                 addT.setText(" "); // reset add text field
                                  str ="";
                                  }
                                    else
                                    {
                                        JOptionPane.showMessageDialog(null,"Sorry..Only http and ftp protocol is allowed", "Error",JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            flagg=0;
                                        
                            }
                     else if(fl==1)
                            {//System.out.println("in adddddd");
                                String s = null;
                                Clipboard r = Toolkit.getDefaultToolkit().getSystemClipboard();
                         try {
                             s = (String) r.getData(DataFlavor.stringFlavor);
                         } catch (                 UnsupportedFlavorException | IOException ex) {
                             Logger.getLogger(DownloadGUI.class.getName()).log(Level.SEVERE, null, ex);
                         }

                                try
                                {
                                    URL u = new URL(s);
                                    if(u.toExternalForm().toLowerCase().startsWith("http://") || u.toExternalForm().toLowerCase().startsWith("ftp://"))
                                    { 
                                        dl = new Accelarator(u,str);

                                        tableModel.addDownload(dl);
                                        addTextField.setText(""); // reset add text field
                                        addT.setText(" "); // reset add text field
                                        str ="";
                                    }
                                    else
                                    {
                                        JOptionPane.showMessageDialog(null,"Sorry..Only http and ftp protocol is allowed", "Error",JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                catch(MalformedURLException e)
                                {
                                    JOptionPane.showMessageDialog(null,"Download Not Possible..Inavlid URL", "Error",JOptionPane.ERROR_MESSAGE);
                                }
                            
                            
                            }
                             
                         }
                }

}