package cc.zsakvo.scpreader.bean;

import java.io.Serializable;

public class ScpSeries implements Serializable {
    String sn;
    String name;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
