package downloadmanager;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import java.net.Proxy;
import java.net.URL;

class Download  implements Runnable {
    
    private static final int SIZE =4*1024;//16*1024
    public static final String STATUSES[] = {"Downloading","Paused", "Complete", "Cancelled", "Error","Appending all files"};
    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;
    public static final int APPENDING = 5;
    
    public long start,end;
    public int tempo;
    public int flagg =0;
    public static int tt=0;
    private URL url; 
    private long size ;
    private int downloaded;
    public int status; 
    private float speedInBytes;
    private float tmpSpeed;
    private String Name;
    
    int flag = 0;
    long temp =0;
    int checker = 0;
    int read;
    int j = 0;
    public  Thread thread;
    String path2;
    
    RandomAccessFile file = null;
    
    BufferedInputStream stream = null;
        InputStream s = null;
    
    // Constructor for Download.
    public Download(URL url, long start, long  end,long si,String S)
    {
        this.url = url;
        size = si;
        downloaded = 0;
        path2 = S;
        this.start = start;
        tempo = (int) start; 
        this.end = end;
        status = DOWNLOADING;
        download();
    }
    
    public Download(URL url,long si,int end,String S)
    {
        this.url = url;
        size = si;
        downloaded = 0;
        this.end = end;
        path2 = S;
        status = DOWNLOADING;
        download();
    }
    
    
    
    public String getUrl()
    {
        return url.toString();
    }
    
    public String getPath()
    {
        return path2;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public int getDownloaded() {
        return downloaded;
    }
    public double getProgress()
    {
        if(size<1) return 0;
        return Math.floor((((double)downloaded/size) * 100)*100)/100;
    }
    
    public int getStatus() 
    {
        return status;
    }
    
    public double getRemainingTime()
    {
        if(size<1)return 0;
        if(url.toExternalForm().toLowerCase().startsWith("http://")) 
        {
           return Math.floor((size/4-downloaded)/speedInBytes*10)/10;
        }
        else return Math.floor((size-downloaded)/speedInBytes*10)/10;
    }
    
    public double getSpeed()
    {
        double v = Math.floor(tmpSpeed*100)/100;
        return v;
    }
    
    
    public void openFile() throws IOException
    {
        File userHome = new File(path2+"/"+fileName());
        Desktop.getDesktop().open(userHome);
    }
    public void openFolder() throws IOException
    {
        File userHome = new File(path2);
        Desktop.getDesktop().open(userHome);
    }
    public String fileName()
    {
        Name = url.getFile();
        return Name.substring(Name.lastIndexOf('/') + 1);
    }
    public void pause() throws InterruptedException
    {
        status = PAUSED;
        System.out.println("before "+downloaded);
           flag = 1;
        flagg =1;
        
    }
    
    public void resume() 
    {
    	
        status = DOWNLOADING;
        tempo = downloaded;
    }
    
     public void restart() 
    {
        status = DOWNLOADING;
        downloaded = 0;
         String filename =fileName();
        String path = path2;
        
        try{
            File file = new File(path2+"/"+filename);
            file.delete();
 
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        download();
        
    }
    
    
    
    public void cancel() 
    {
        status = CANCELLED;
        
    }
    
    public void error() 
    {
        status = ERROR;
        
        
    }
    
    private void download() 
    {
        thread = new Thread(this);
        thread.start();
    }
    /*private void dowmload()
    {
    	
        this.start();
    }*/
     
    public String getFileName(URL url)
    {
        String fileName = "";
        fileName = url.getFile();
        return  fileName.substring(fileName.lastIndexOf("/") + 1,fileName.length());
    }
    @Override
    public void run() 
    {
    	
       
        HttpURLConnection connection = null ;
        URLConnection conn = null;
        try 
        {
        	if(status==DOWNLOADING)
        	{
             // Open connection to URL with HTTP protocol...http is called scheme
            if(url.toExternalForm().toLowerCase().startsWith("http://"))
            {
                checker = 1;
                connection = (HttpURLConnection) url.openConnection();
         
                if(end!=0)
                {
                	//System.out.println("abc");
                    connection.setRequestProperty("Range","bytes=" + tempo + "-" + end);
                }
                else if(end==0)
                {
                	//System.out.println("abcd");
                    connection.setRequestProperty("Range","bytes=" + downloaded + "-");
                }
                connection.connect();
                if (connection.getResponseCode() / 100 != 2) 
                {
                    
                    error();
                    
                }
                    File f=new File(System.getProperty("user.home")+"/Downloads/IDM");
                    if(f.mkdir()) 
                    { 
                    //System.out.println("Directory Created");
                    }
                    
                    if(size>1)file = new RandomAccessFile(path2 + "/" + getFileName(url)+"." + start + "-" + end, "rw");
                    else file = new RandomAccessFile(path2 + "/" + getFileName(url), "rw");
               
                file.seek(downloaded);
                stream = new BufferedInputStream(connection.getInputStream());
            }
            
            else if(url.toExternalForm().toLowerCase().startsWith("ftp://"))
            {
                checker = 2;
                conn = url.openConnection();
                conn.connect();
                
                    File f=new File(System.getProperty("user.home")+"/Downloads/IDM");
                    if(f.mkdir()) 
                    { 
                    }
                    file = new RandomAccessFile(path2 + "/" + getFileName(url), "rw");
                
                
                s = conn.getInputStream();
                file.seek(downloaded);
            }
            long downloadStartTime;
            if (flag == 1)
            {
                downloadStartTime = temp;
                flag = 0;
            }
            else
            {
                downloadStartTime = System.currentTimeMillis();
            }
            
           // long remainingTime = 0; 
            int cc=0;
            while(true)
            {
	            while (status == DOWNLOADING)
	            {
	                byte buff[] ;
	                buff = new byte[SIZE];
	               
	                
	                if(checker == 1) 
	                {
	                    read = stream.read(buff);
	                }
	                else
	                {
	                    read = s.read(buff);
	                }
	                
	                if(read==-1)
	                	{cc++;break;}
	               
	                if(url.toExternalForm().toLowerCase().startsWith("http://") && size>1)
	                {
	                    if(downloaded>=(end-start))break;
	                }
	                file.write(buff, 0, read);
	                downloaded += read;
	                
	                
	                long elapsedTime = System.currentTimeMillis() - downloadStartTime; 
	                temp = System.currentTimeMillis();
	                speedInBytes =  1024f*downloaded / elapsedTime;
	                tmpSpeed = speedInBytes;
	                
	                if (tmpSpeed > 1024) 
	                {
	                    tmpSpeed = tmpSpeed / 1024;
	                }
	                if (tmpSpeed > 1024 * 1024)
	                {
	                    tmpSpeed = tmpSpeed / (1024 * 1024);
	                }
	               
	            }
	            if(cc==1)
	            	break;
	            }
        }
            // System.out.println(read);
             
            if (status == DOWNLOADING) 
            {
                if(url.toExternalForm().toLowerCase().startsWith("http://") && size>1)status = APPENDING;
                else if(url.toExternalForm().toLowerCase().startsWith("ftp://") ||(url.toExternalForm().toLowerCase().startsWith("http://") &&size<1))status = COMPLETE;
            }
        }catch (UnknownHostException e) 
        {
            error();
            JOptionPane.showMessageDialog(null,"Connection has tmed out", "TimeOut",JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e) 
        {
            error();
        }
        finally 
        {
            if (file != null) 
            {
                try
                {
                    file.close();
                } 
                catch (IOException e) {}
            }
            if (stream != null) 
            {
                try
                {
                    stream.close();
                } 
                catch (IOException e) {}
            }
        }
        //System.out.println("yooho");
    }
    
}