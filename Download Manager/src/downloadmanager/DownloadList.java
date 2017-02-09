package downloadmanager;

import java.io.File;
import java.util.*;
import javax.swing.table.*;

class DownloadList extends AbstractTableModel implements Observer {
    
    private static final String[] columnNames = {"FileName","Size","Downloaded","Time Remaining","Transfer Rate","Progress","Status"};
    
    private  ArrayList<Accelarator> downloadList = new ArrayList<Accelarator>();
    
    public void addDownload(Accelarator download) 
    {
        
        download.addObserver(this);
        downloadList.add(download);
        
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }
    
    public Accelarator getDownload(int row)
    {
        return downloadList.get(row);
    }
    public void clearDownload(int row) 
    {
        downloadList.remove(row);
        fireTableRowsDeleted(row, row);
       
    }
    
  
    
    public void clearDownloadDelete(int row) 
    {
        Accelarator download =  downloadList.get(row);
        String filename = download.fileName();
        String path = download.getPath();
        if (path == "") 
        {
            path = System.getProperty("user.home")+"/Downloads/IDM";
        }
        try{
            File file = new File(path+"/"+filename);
            file.delete();
 
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        downloadList.remove(row);  
        fireTableRowsDeleted(row, row);
    }
    @Override
    public int getColumnCount() 
    {
        return columnNames.length;
    }
    @Override
    public String getColumnName(int col) 
    {
        return columnNames[col];
    }   
    
    @Override
    public int getRowCount() 
    {
        return downloadList.size();
    }
    @Override
    public Object getValueAt(int row, int col) 
    {
        Accelarator download = (Accelarator) downloadList.get(row);
        
        switch (col) 
        {
            case 0: // File Name
                return "    "+download.fileName();
            case 1: // Size
                long size = download.getSize();
                if(size == -1) return "     unknown";
                else if(size < (1024)) return  "    "+Double.toString(size)+ " Byte";
                else if (size >= (1024) && size < (1024*1024)) return  "    "+Double.toString(Math.floor((size/1024.0)*100)/100)+ " KB";
                else if(size >= (1024*1024) && size <(1024*1024*1024)) return "    "+Double.toString(Math.floor(((size/1024)/1024.0)*100)/100)+ " MB";
                return "    "+Double.toString(Math.floor((((size/1024)/1024.0)/1024.0)*100)/100)+ " GB";              
            case 2://Downloaded
                int dloaded = download.getDownloaded();
                System.out.println(dloaded);
                if(dloaded < (1024)) return "   "+Double.toString(dloaded)+ " Byte";
                else if(dloaded >= (1024) && dloaded < (1024*1024)) return " "+Double.toString(Math.floor((dloaded/1024.0)*100)/100)+ " KB";
                else if(dloaded >= (1024*1024) && dloaded < (1024*1024*1024)) return "    "+Double.toString(Math.floor(((dloaded/1024)/1024.0)*100)/100)+ " MB";
                return "    "+Double.toString(Math.floor((((dloaded/1024)/1024.0)/1024)*100)/100)+ " B";
            case 3: // time remaining
                if((download.getRemainingTime())>0)
                {
                    return "        "+Double.toString(download.getRemainingTime())+" second";
                }
                else return "      0  ";
            case 4: //transfer rate
                if((download.getSpeed())>0)
                {
                    return "        "+Double.toString(download.getSpeed())+" KB/sec";
                }
                else return "   unknown";
            case 5: // progress in floating point
                if((download.getProgress())>0)
                {
                    if(download.getProgress()<100)return "        "+Double.toString(Math.ceil(Math.floor((download.getProgress())*100)/100))+"%";
                    else if(download.getProgress()>100)return "        "+Double.toString(Math.floor(Math.floor((download.getProgress())*100)/100))+"%";
                }
                else return "       0.0%";
            case 6: // Status
                return "    "+Download.STATUSES[download.getStatus()];
        }
    
        return "";
    }

    @Override
    public void update(Observable o, Object arg) {
        int index = downloadList.indexOf(o);
        fireTableRowsUpdated(index, index);
    }
    
    
}