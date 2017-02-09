package downloadmanager;

import static downloadmanager.Download.COMPLETE;
import static downloadmanager.Download.tt;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.* ;

 class Accelarator extends Observable implements Runnable {

    private long size;
    
    public Download[] Threads;
    private Download to;
    private URL url;
    private URL url2;
    private Thread thread;
    public int count = 4;
    
    private String[] partFile;
    public double a = 0,b = 0,p1,p2,p3,p4;
    public static BufferedWriter wr;
    private long sizee;
    int dloadede=0,d;
    private int statuse; 
    private double trate=0,timeLeft=0,prog = 0;;
   
    private double p,t,ti;
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JProgressBar progressBar1 = new JProgressBar(0,25);
    private final JProgressBar progressBar2 = new JProgressBar(0, 25);
    private final JProgressBar progressBar3 = new JProgressBar(0, 25);
    private final JProgressBar progressBar4 = new JProgressBar(0, 25);
    
    
    JPanel labelPanel,valuePanel,barPanel,barPanel2;
    private final  JLabel stat = new JLabel("Status");
    private final JLabel fsize = new JLabel("File Size");
    private final JLabel ad = new JLabel("Downloaded");
    private final JLabel rate = new JLabel("Transfer Rate");
    private final JLabel time = new JLabel("Time Left");
    private final JLabel pmade = new JLabel("Progress");
    
    JButton pauseButton = new JButton("Pause");
    JButton resumeButton = new JButton("Resume");
    JButton cancelButton = new JButton("Cancel");
    
    private String filename,Name;
    JFrame frame2;
    JPanel gui ;
    String path,path2;
    
    JLabel sizeLabel,adLabel,rateLabel,timeLabel,statLabel,pm;

    public Accelarator(URL url,String S) 
    {
        
        size = -1;
        path =S;
        if(path =="") path2 = System.getProperty("user.home")+"/Downloads/IDM";
        else path2 = path;
        try
        {
            if(url.toExternalForm().toLowerCase().startsWith("http://"))
            {
                HttpURLConnection connection =  (HttpURLConnection)url.openConnection();
                connection.connect();
                size = connection.getContentLengthLong();
                //System.out.println(size);
            }
            else
            {
                URLConnection connection = url.openConnection();
                connection.connect();
                size = connection.getContentLengthLong();
            }
        }catch (IOException e) {}
        
        this.url = url;
        url2 = url;
        this.size = size;
        Threads = new Download[count];
        thread = new Thread(this);
        filename = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
        frame2 = new JFrame(filename);
        frame2.setVisible(false);
        frame2.setLocation(500,200);
        frame2.setSize(500,400);
        frame2.setResizable(false);
        
        pauseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        pause();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Accelarator.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        });
       
        resumeButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(url2.toExternalForm().toLowerCase().startsWith("http://") && size>1)resume();
                    else restart();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        pause();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Accelarator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    String message = " Do you wish to cancel ? ";
                    String title = "Cancel";
                    int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == 0)
                    {
                        cancel();
                    }
                    else resume();
                }
        });
        
        frame2.addWindowListener(new WindowAdapter(){
        @Override
        public void windowClosing(WindowEvent w)
        {
            try {
                pause();
            } catch (InterruptedException ex) {
                Logger.getLogger(Accelarator.class.getName()).log(Level.SEVERE, null, ex);
            }
           String message = " Do you wish to cancel ? ";
           String title = "Cancel";
           int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
           if (reply == 0)
           {
               cancel();
           }
           else resume();
        }
        });
        
        frame2.setContentPane(createGUI());
        thread.start();
    }
    
    public JPanel createGUI()
    {
        gui = new JPanel();
        gui.setLayout(null);
        
        JLabel urlLabel = new JLabel(url.toString());
        urlLabel.setLocation(15,10);
        urlLabel.setSize(300,15);
        gui.add(urlLabel);
        
        labelPanel = new JPanel();
        labelPanel.setLayout(null);
        labelPanel.setLocation(15,30);
        labelPanel.setSize(120,200);
        gui.add(labelPanel);
        
        fsize.setLocation(15,20);
        fsize.setSize(65,15);
        labelPanel.add(fsize);
        
        ad.setLocation(15,45);
        ad.setSize(85,15);
        labelPanel.add(ad);
        
        rate.setLocation(15,70);
        rate.setSize(85,15);
        labelPanel.add(rate);
        
        time.setLocation(15,95);
        time.setSize(85,15);
        labelPanel.add(time);
        
        stat.setLocation(15,120);
        stat.setSize(85,15);
        labelPanel.add(stat);
        
        pmade.setLocation(15, 145);
        pmade.setSize(85,15);
        labelPanel.add(pmade);
        //here starts the right side value panel
        
        valuePanel = new JPanel();
        valuePanel.setLayout(null);
        valuePanel.setLocation(150,30);
        valuePanel.setSize(250,200);
        gui.add(valuePanel);
        
        sizee = size;
        if(sizee>1){
        if(sizee < (1024)) sizeLabel = new JLabel(Double.toString(sizee)+ " Byte");
        if (sizee < (1024*1024)) sizeLabel = new JLabel(Double.toString(Math.floor((sizee/1024.0)*100)/100)+ " KB");
        if(sizee < (1024*1024*1024)) sizeLabel = new JLabel(Double.toString(Math.floor(((sizee/1024)/1024.0)*100)/100)+ " MB");
        else sizeLabel = new JLabel(Double.toString(Math.floor((((sizee/1024)/1024.0)/1024.0)*100)/100)+ " GB");
        }
        else
        {
            if(sizee < (1024)) sizeLabel = new JLabel("unknown");
        }
        sizeLabel.setLocation(60,20);
        sizeLabel.setSize(70,15);
        valuePanel.add(sizeLabel);
        
        
        dloadede = 0;
        if(dloadede < (1024)) adLabel = new JLabel(Double.toString(dloadede)+ " Byte");
        
        adLabel.setLocation(60,45);
        adLabel.setSize(70,15);
        valuePanel.add(adLabel);
      
        rateLabel = new JLabel("unknown");
        rateLabel.setLocation(60,70);
        rateLabel.setSize(90,15);
        valuePanel.add(rateLabel);
        
        if(timeLeft==0) timeLabel = new JLabel("unknown");
        else timeLabel = new JLabel("0");
        
        timeLabel.setLocation(60,95);
        timeLabel.setSize(90,15);
        valuePanel.add(timeLabel);
        
        
        statLabel = new JLabel("Downloading");
        statLabel.setFont(new Font("Serif",Font.BOLD,16));
        statLabel.setLocation(60,118);
        statLabel.setSize(90,15);
        valuePanel.add(statLabel);
        
        
        pm = new JLabel(" 0.0%");
        pm.setLocation(60, 145);
        pm.setSize(90,15);
        valuePanel.add(pm);
        
        barPanel = new JPanel();
        barPanel.setLayout(null);
        barPanel.setLocation(15,230);
        //barPanel.setBackground(Color.red);
        barPanel.setSize(400,25);
        gui.add(barPanel);
        
        barPanel2 = new JPanel();
        barPanel2.setLayout(null);
        barPanel2.setLocation(15,265);
        barPanel2.setSize(400,30);
        //barPanel2.setBackground(Color.YELLOW);
        gui.add(barPanel2);
        
        progressBar.setLocation(15,3);
        progressBar.setSize(380,20);
        progressBar.setValue(0);
        progressBar.setBackground(Color.white);
        barPanel.add(progressBar);
        
        progressBar1.setLocation(15,3);
        progressBar1.setSize(95,20);
        progressBar1.setValue(0);
        progressBar1.setBackground(Color.white);
        barPanel2.add(progressBar1);
        
        progressBar2.setLocation(110,3);
        progressBar2.setSize(95,20);
        progressBar2.setValue(0);
        progressBar2.setBackground(Color.white);
        barPanel2.add(progressBar2);
        
        progressBar3.setLocation(205,3);
        progressBar3.setSize(95,20);
        progressBar3.setValue(0);
        progressBar3.setBackground(Color.white);
        barPanel2.add(progressBar3);
        
        progressBar4.setLocation(300,3);
        progressBar4.setSize(95,20);
        progressBar4.setValue(0);
        progressBar4.setBackground(Color.white);
        barPanel2.add(progressBar4);
        
        pauseButton.setLocation(170,320);
        pauseButton.setSize(90,25);
        gui.add(pauseButton);
        
        resumeButton.setLocation(270,320);
        resumeButton.setSize(90,25);
        resumeButton.setEnabled(false);
        gui.add(resumeButton);
        
        cancelButton.setLocation(370,320);
        cancelButton.setSize(90,25);
        gui.add(cancelButton);
        
        gui.setOpaque(true);
        return gui;
     }
    
    public long getSize()
    {
        return size;
    }
    
    public int getDownloaded()
    {
         
        return d;
       
    }
    public double getProgress()
    {
        if(size<1) return 0;
        return p;
    }
    
    
    public double getRemainingTime()
    {

        if(size<1)return 0;
        return ti;
    }
    
    public double getSpeed()
    {
        
        return t;
    }
    
    public String fileName()
    {
        Name = url.getFile();
        return Name.substring(Name.lastIndexOf('/') + 1);
    }
    
    public String getPath()
    {
        return path2;
    }
    

    
    public void pause() throws InterruptedException
    {
        if(url.toExternalForm().toLowerCase().startsWith("http://")&&size>1)
        {for(int j=0;j<count;j++)
        {
            Threads[j].pause();
        }
        }
        else if(url.toExternalForm().toLowerCase().startsWith("ftp://") ||(url.toExternalForm().toLowerCase().startsWith("http://") &&size<1))
        {
            to.pause();
        }
         statChanged();
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(true);
        statLabel.setText(" ");
        statLabel = new JLabel("Paused");
        statLabel.setFont(new Font("Serif",Font.BOLD,16));
        statLabel.setLocation(60,120);
        statLabel.setSize(90,15);
        valuePanel.add(statLabel);
        gui.add(valuePanel);
        
        
    }
    
    public void resume() 
    {
        
        statChanged();
        resumeButton.setEnabled(false);
        if(size>1)pauseButton.setEnabled(true);
        statLabel.setText(" ");
        statLabel = new JLabel("Downloading");
        statLabel.setFont(new Font("Serif",Font.BOLD,16));
        statLabel.setLocation(60,120);
        statLabel.setSize(90,18);
        valuePanel.add(statLabel);
        gui.add(valuePanel);
        if(url.toExternalForm().toLowerCase().startsWith("http://")&&size>1)
        {for(int j=0;j<count;j++)
        {
            Threads[j].resume();
        }
        }
        else if(url.toExternalForm().toLowerCase().startsWith("ftp://") ||(url.toExternalForm().toLowerCase().startsWith("http://") &&size<1))
        {
            to.resume();
        }
    }
    
     public void restart() 
    {
        
        statChanged();
        resumeButton.setEnabled(false);
        if(size>1)pauseButton.setEnabled(true);
        statLabel.setText(" ");
        statLabel = new JLabel("Downloading");
        statLabel.setFont(new Font("Serif",Font.BOLD,16));
        statLabel.setLocation(60,120);
        statLabel.setSize(90,18);
        valuePanel.add(statLabel);
        gui.add(valuePanel);
        pauseButton.setEnabled(true);
        to.restart();
        
    }
    
     public void cancel() 
     {
        for (int i = 0; i < count; i++)
                {
                   String path = "c:/Users/"+System.getProperty("user.name")+"/"+"Downloads/IDM";
                   String sub = "";
                   
                   if (i == 0) 
                   {
                    sub = new String("." + 0 + "-" + (size / count));
                    }
                    else if (i == count - 1) 
                    {
                       sub = new String("." + (((size / count)*i) + 1) + "-" + size);
                    } 
                    else
                    {
                        sub =  new String("." +  (((size/count)*i) + 1) + "-" + ((size /count)*(i+1)));
                    }
                    File filee = new File(path + "/" + filename+ sub);
 
                    if(filee.delete())
                    {
    			//System.out.println( " is deleted!");
                    }
        frame2.setVisible(false); 
         statChanged();
       if(url.toExternalForm().toLowerCase().startsWith("http://")&&size>1)
        {for(int j=0;j<count;j++)
        {
            Threads[j].cancel();
        }
        }
        else if(url.toExternalForm().toLowerCase().startsWith("ftp://") ||(url.toExternalForm().toLowerCase().startsWith("http://") &&size<1))
        {
            to.cancel();
        }
        
    }
 }
    public void error() 
    {
        frame2.setVisible(false); 
         statChanged();
        
    }
    
    
    public void openFile() throws IOException
    {
        File userHome = new File(path2+"/"+filename);
        Desktop.getDesktop().open(userHome);
    }
    public void openFolder() throws IOException
    {
        
        File userHome = new File(path2);
        Desktop.getDesktop().open(userHome);
    }
    @SuppressWarnings("unused")
	public int getStatus() 
    {
        if(url.toExternalForm().toLowerCase().startsWith("http://")&&size>1)
        {
            
            for(int i =0;i<count;i++)return Threads[i].status;
        }
        else if(url.toExternalForm().toLowerCase().startsWith("ftp://") ||(url.toExternalForm().toLowerCase().startsWith("http://") &&size<1)) return to.status;
        return 0;
    }
    
    public void run()
    {
        frame2.setVisible(true);
        try
        {
            if(url.toExternalForm().toLowerCase().startsWith("http://") && size>1)
            {
                for (int i = 0; i <count; i++)
            {
                	//System.out.println("afte pause");
                if (i == 0) 
                {
                    Threads[i] = new Download(url,0,size /count,size,path2);
                } 
                else if (i == count - 1) //i=3
                
                {
                    Threads[i] = new Download(url, ((size / count) * i) + 1, size,size,path2);
                } 
                else//i=2 or i=3
                {
                    Threads[i] = new Download(url, ((size / count) * i) + 1, ((size / count) * (i + 1)),size,path2);
                }
            }
            }
            else if(url.toExternalForm().toLowerCase().startsWith("ftp://") ||(url.toExternalForm().toLowerCase().startsWith("http://") &&size<1))
            {
                to = new Download(url,size,0,path2); 
            }
            Thread.sleep(1000);
            for(int i=0;i<count;i++)
            {
                if(url.toExternalForm().toLowerCase().startsWith("http://") && size>1)
                {
                if(Threads[i].getStatus()== 4) 
                {
                    
                    error();
                    break;
                }
                }
                else
                {
                   if(to.getStatus()==4)
                {
                    
                    error();
                }
                }
            }
            
            while(true)
            {
                
                if(url.toExternalForm().toLowerCase().startsWith("http://")&&size>1)
                {
                for(int j = 0 ;j<count;j++)
                {
                    
                    dloadede += Threads[j].getDownloaded();
                }
                adLabel.setText("");
                if(dloadede < (1024)) adLabel = new JLabel(Double.toString(dloadede)+ " Byte");
                else if(dloadede >= (1024) && dloadede < (1024*1024)) adLabel = new JLabel(Double.toString(Math.floor((dloadede/1024.0)*100)/100)+ " KB");
                else if(dloadede >= (1024*1024) && dloadede < (1024*1024*1024)) adLabel = new JLabel(Double.toString(Math.floor(((dloadede/1024)/1024.0)*100)/100)+ " MB");
                else adLabel = new JLabel(Double.toString(Math.floor((((dloadede/1024)/1024.0)/1024)*100)/100)+ " GB");
        
                adLabel.setLocation(60,45);
                adLabel.setSize(70,15);
                valuePanel.add(adLabel);
                d = dloadede;
                
                for(int j = 0 ;j<count;j++)
                {
                    
                	
                    trate += Threads[j].getSpeed();
                }
                
                rateLabel.setText("");
                
                trate = Math.floor(trate*100)/100;
                
                if(trate>0 && trate<(100000000)) rateLabel = new JLabel(Double.toString(trate)+"  KB/sec");
                else rateLabel = new JLabel("unknown");
                
                t = trate;
                trate = 0;
                rateLabel.setLocation(60,70);
                rateLabel.setSize(90,15);
                valuePanel.add(rateLabel);
                
                for(int j = 0 ;j<count;j++)
                {
                    
                    timeLeft += Threads[j].getRemainingTime();
                }
                timeLabel.setText("");
                
                trate = (Math.floor(timeLeft/4*100)/100);
                if(timeLeft>0 && timeLeft<(10000)) timeLabel = new JLabel(Double.toString(timeLeft)+"  second");
                else if(timeLeft == 0)timeLabel = new JLabel("0 second");
                else timeLabel = new JLabel("unknown");
                ti = timeLeft;
                timeLeft = 0;
                timeLabel.setLocation(60,95);
                timeLabel.setSize(90,15);
                valuePanel.add(timeLabel);
                            
                for(int j = 0 ;j<count;j++)
                {
                    b += Threads[j].getProgress();
                }
                p1 = Threads[0].getProgress();
                p2 = Threads[1].getProgress();
                p3 = Threads[2].getProgress();
                p4 = Threads[3].getProgress();
                a = b;
                p = b;
                prog = Math.floor(b*100)/100;
                pm.setText("");
                            
                pm = new JLabel(Double.toString(prog)+" %");
                pm.setLocation(60, 145);
                pm.setSize(90,15);
                valuePanel.add(pm);
                            
                gui.add(valuePanel);
                prog = 0;
                b = Math.ceil(b);
                progressBar.setValue((int)(b));
                p1 = Math.ceil(p1);
                progressBar1.setValue((int)(p1));
                p2 = Math.ceil(p2);
                progressBar2.setValue((int)(p2));
                p3 = Math.ceil(p3);
                progressBar3.setValue((int)(p3));
                p4 = Math.ceil(p4);
                progressBar4.setValue((int)(p4));
                progressBar.setForeground(Color.green);
       
                 progressBar1.setForeground(Color.PINK);
                 progressBar2.setForeground(Color.magenta);
                 progressBar3.setForeground(Color.ORANGE);
                 progressBar4.setForeground(Color.cyan);
                 b = 0;
                 //p1=p2=p3=p4=0;
                 if(dloadede>= size) {break;}
                 dloadede =0;
                 Thread.sleep(300);
                }
                
                else if(url.toExternalForm().toLowerCase().startsWith("ftp://") ||(url.toExternalForm().toLowerCase().startsWith("http://") && size<1))
                {
                    
                    dloadede = to.getDownloaded();
                adLabel.setText("");
                if(dloadede < (1024)) adLabel = new JLabel(Double.toString(dloadede)+ " Byte");
                else if(dloadede >= (1024) && dloadede < (1024*1024)) adLabel = new JLabel(Double.toString(Math.floor((dloadede/1024.0)*100)/100)+ " KB");
                else if(dloadede >= (1024*1024) && dloadede < (1024*1024*1024)) adLabel = new JLabel(Double.toString(Math.floor(((dloadede/1024)/1024.0)*100)/100)+ " MB");
                else adLabel = new JLabel(Double.toString(Math.floor((((dloadede/1024)/1024.0)/1024)*100)/100)+ " GB");
        
                adLabel.setLocation(60,45);
                adLabel.setSize(70,15);
                valuePanel.add(adLabel);
                d = dloadede;
                
                
                    
                    trate = to.getSpeed();
                
                
                rateLabel.setText("");
                
                trate = Math.floor(trate*100)/100;
                
                if(trate>0) rateLabel = new JLabel(Double.toString(trate)+" KB/sec");
                else rateLabel = new JLabel("unknown");
                
                t = trate;
                trate = 0;
                rateLabel.setLocation(60,70);
                rateLabel.setSize(90,15);
                valuePanel.add(rateLabel);
              
                    
                    timeLeft = to.getRemainingTime();
                
                timeLabel.setText("");
                timeLeft = Math.floor(timeLeft*100)/100;
                if(timeLeft>0) timeLabel = new JLabel(Double.toString(timeLeft)+"  second");
                else if(timeLeft == 0)timeLabel = new JLabel("unknown");
                else timeLabel = new JLabel("0 second");
                ti = timeLeft;
                timeLeft = 0;
                timeLabel.setLocation(60,95);
                timeLabel.setSize(90,15);
                valuePanel.add(timeLabel);
                          
                    b = to.getProgress();
                
              
                a = b;
                p = b;
                prog = Math.floor(b*100)/100;
                pm.setText("");
                            
                pm = new JLabel(Double.toString(prog)+" %");
                pm.setLocation(60, 145);
                pm.setSize(90,15);
                valuePanel.add(pm);
                            
                gui.add(valuePanel);
                prog = 0;
                b = Math.ceil(b);
                progressBar.setValue((int)(b));
                progressBar1.setEnabled(false);
                progressBar2.setEnabled(false);
                progressBar3.setEnabled(false);
                progressBar4.setEnabled(false);
                progressBar.setForeground(Color.green);
                 
                 b = 0;
                              
                if(to.status == COMPLETE)
                {
                    break;
                }
                Thread.sleep(400);
                dloadede =0;
                }
                
                
            }
            
            //join
            if(url.toExternalForm().toLowerCase().startsWith("http://")&&size>1)
            {
                progressBar.setValue(0);
               
                for(int i=0;i<count;i++)Threads[i].thread.join();
                
             statLabel.setText(" ");
             statLabel = new JLabel("Appending ");
             statLabel.setFont(new Font("Serif",Font.BOLD,16));
             statLabel.setLocation(60,120);
             statLabel.setSize(90,15);
             valuePanel.add(statLabel);
            
            String[] partFile = new String[count] ;
            for (int i = 0; i < count; i++)
            {
                if (i == 0)
                {
                    partFile[i] = new String("c://Users/"+System.getProperty("user.name")+"/"+"Downloads/IDM/"+ filename +"." + 0 + "-" + (size / count));
                }
                else if (i == count - 1) {
                    partFile[i] =  new String("c://Users/"+System.getProperty("user.name")+"/"+"Downloads/IDM/"+filename + "." + (((size /count)*i) + 1) + "-" + size);
                } 
                else {
                   partFile[i] =   new String("c://Users/"+System.getProperty("user.name")+"/"+"Downloads/IDM/" + filename + "." +  (((size/count)*i) + 1) + "-" + ((size /count)*(i+1)));
                }
            }
            append(filename,partFile,size);
        
            frame2.setVisible(false);
            }
            else if(url.toExternalForm().toLowerCase().startsWith("ftp://") ||(url.toExternalForm().toLowerCase().startsWith("http://") &&size<1))
            {
                frame2.setVisible(false);
                statChanged();
            }
        } catch (Exception ex)
        {
        }
    }

    public void append(String filename, String part[],long siz) throws Exception 
    {
        FileOutputStream fo = new FileOutputStream("c://Users/"+System.getProperty("user.name")+"/"+"Downloads/IDM/"+filename);
        
		for (int j = 0; j < count; j++)
		{
                    InputStream fis1 = new FileInputStream(part[j]);
                    while (true)
                    {
                        byte by[] = new byte[32768];
                        int b = fis1.read(by);
                        if (b == -1)
                        {
                            break;
                        }
                        fo.write(by,0,b);

                    }
                    fis1.close();
		}
                
                for (int i = 0; i < count; i++)
                {
                   String path = "c:/Users/"+System.getProperty("user.name")+"/"+"Downloads/IDM";
                   String sub = "";
                   
                   if (i == 0) 
                   {
                    sub = new String("." + 0 + "-" + (size / count));
                    }
                    else if (i == count - 1) 
                    {
                       sub = new String("." + (((size / count)*i) + 1) + "-" + size);
                    } 
                    else
                    {
                        sub =  new String("." +  (((size/count)*i) + 1) + "-" + ((size /count)*(i+1)));
                    }
                    File filee = new File(path + "/" + filename+ sub);
 
                    if(filee.delete())
                    {
    			
                    }
                }
                fo.close();
                for(int j=0;j<count;j++)
                {
                    Threads[j].status = COMPLETE;
                }
               // System.out.println("check "+filename+ " "+size);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
               // System.out.println(dateFormat.format(date));

                	wr=new BufferedWriter(new FileWriter("Default.txt",true)); 
            
                wr.write(filename+" "+String.valueOf(size)+" "+dateFormat.format(date)+" ");
                wr.close();
               
                statChanged();
    }
    
    private void statChanged() 
    {
        setChanged();
        notifyObservers();
    }
}