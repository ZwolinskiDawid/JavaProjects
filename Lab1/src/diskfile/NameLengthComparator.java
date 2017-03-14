package diskfile;

import java.io.Serializable;
import java.util.Comparator;

public class NameLengthComparator implements Comparator<DiskFile>, Serializable {
    @Override
    public int compare(DiskFile a, DiskFile b) {
        if(a.kind == b.kind)
        {
            if(a.name.length() == b.name.length())
            {
                return a.name.compareTo(b.name);
            }
            return (int) (a.name.length() - b.name.length());           
        }
        else if(a.kind == DiskFile.FileKind.DIRECTORY)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}
