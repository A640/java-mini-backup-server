package common;

import java.io.Serializable;

public class BFile implements Serializable {
    public int fileId;
    public String name;
    public Long size;
//    private static final long serialVersionUID = 71337L;

    public BFile(int fileId, String name, Long size) {
        this.fileId = fileId;
        this.name = name;
        this.size = size;
    }


    public String toString(){

        int sizeDivider = -1;
        String sizeUnit;

        if(this.size < 1048576){
            sizeUnit = "kB";
            sizeDivider = 1024;
        } else if(this.size < 1073741824){
            sizeUnit = "MB";
            sizeDivider = 1048576;
        } else{
            sizeUnit = "GB";
            sizeDivider = 1073741824;
        }

        return this.name + "  -  " + (this.size / sizeDivider) + " " + sizeUnit;
    }
}