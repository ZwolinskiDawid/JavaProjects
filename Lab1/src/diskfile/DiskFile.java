package diskfile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Objects;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class DiskFile implements Comparable<DiskFile>, Serializable{

    public Set<DiskFile> childFiles;
    String name;
    long size;
    FileKind kind;
    String lastModified;
    
    public DiskFile(Path path, Comparator<DiskFile> comp)
    {        
        childFiles = new TreeSet<>(comp);
        
        try
        {
            FileTime date = Files.getLastModifiedTime(path);
            lastModified = (new SimpleDateFormat("[dd.MM.yyyy HH:mm]")).format(date.toMillis());
            name = path.getFileName().toString();
            
            kind = getKind(path);
            
            if(Files.isRegularFile(path))
            {
                size = Files.size(path); 
            }
            else if(Files.isDirectory(path))
            {
                size = size(path);
                
                DirectoryStream<Path> ds = Files.newDirectoryStream(path);
                for (Path file : ds)
                {
                    childFiles.add(new DiskFile(file, comp));
                }               
            }
            
        }
        catch (IOException e) 
        {
            System.err.println(e);
        }
    }
    
    private FileKind getKind(Path path)
    {
        if(Files.isRegularFile(path))
        {
            return FileKind.REGULAR_FILE;
        }
        else
        {
            return FileKind.DIRECTORY;             
        }
    }
    
    private int size(Path path)
    {
        int i=0;
        
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path))
        {
            for (Path file : ds)
            {
                i++;
            }
        } 
        catch (IOException e) 
        {
            System.err.println(e);
        }
        
        return i;
    }
    
    public enum FileKind
    {
        REGULAR_FILE,
        DIRECTORY;
    }
    
    @Override
    public int compareTo(DiskFile file)
    {
        if(kind == file.kind)
        {
            return (int) (size - file.size);
        }
        else if(kind == FileKind.DIRECTORY)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
    
    @Override
    public String toString()
    {
        return this.print("");
    }
    
    private String getValue()
    {
        String type;
        if(kind == FileKind.DIRECTORY)
        {
            type = "K";
        }
        else
        {
            type = "P";
        }
        return name + " " + lastModified + " " + type + " (" + size + ")" + "\n";
    }
    
    private String print(String tab)
    {
        String returnValue;
        returnValue = tab + getValue();
        for(DiskFile file: childFiles)
        {
            returnValue += file.print(tab+"\t");
        }
        return returnValue;
    }
    
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DiskFile other = (DiskFile) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.childFiles, other.childFiles);
    } 
    
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        Path path = Paths.get("C:\\Users\\Dawid\\Desktop\\Projects");
        
        //DiskFile File = new DiskFile(path, null);
        //DiskFile File = new DiskFile(path, new SizeComparator());
        DiskFile File = new DiskFile(path, new NameLengthComparator());
        DiskFile ReadedFile;
        
        //SERIALIZACJA ZAPISYWANIE
        try(FileOutputStream fileOut = new FileOutputStream("plik.txt"); 
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) 
        {
            out.writeObject(File);
        }
        
        //SERIALIZACJA WCZYTYWANIE
        try (FileInputStream fileIn = new FileInputStream("plik.txt"); 
                ObjectInputStream in = new ObjectInputStream(fileIn)) 
        {
            ReadedFile = (DiskFile) in.readObject();
        }
        
        System.out.println(ReadedFile);
    }
}
