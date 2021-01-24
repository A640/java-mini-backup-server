package common;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Backup implements Serializable {
    public int backupId;
    public String name;
    public String description;
    public Date created;
    public List<BFile> files;
//    private static final long serialVersionUID = 71337L;

    public Backup(int backupId, String name, String description, Date created, List<BFile> files) {
        this.backupId = backupId;
        this.name = name;
        this.description = description;
        this.created = created;
        this.files = files;
    }
}
