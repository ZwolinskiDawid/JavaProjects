package diskfile;

import java.io.Serializable;
import java.util.Comparator;

public class SizeComparator implements Comparator<DiskFile>, Serializable {
    @Override
    public int compare(DiskFile a, DiskFile b) {
        if(a.kind == b.kind)
        {
            if(a.size == b.size)
            {
                return a.name.compareTo(b.name);
            }
            return (int) (a.size - b.size);           
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
