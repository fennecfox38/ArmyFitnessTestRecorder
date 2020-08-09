package army.prt.recorder.acft;

public class Duration implements Comparable{
    private int min, sec;
    Duration(){ min = 0; sec = 0; }
    Duration(int min, int sec){
        this.min=min;
        this.sec=sec;
    }
    public void setTime(int min, int sec){
        this.min=min;
        this.sec=sec;
    }
    public void setMin(int min){ this.min = min; }
    public void setSec(int sec){ this.sec = sec; }
    public int getMin(){ return min; }
    public int getSec(){ return sec;}

    @Override public int compareTo(Object o) {
        Duration duration = (Duration) o;
        int res = min - duration.min;
        if(res != 0) return res;
        else return (sec - duration.sec);
    }
}
