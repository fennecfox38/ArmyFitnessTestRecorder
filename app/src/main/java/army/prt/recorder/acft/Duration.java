package army.prt.recorder.acft;

import androidx.annotation.NonNull;

public class Duration implements Comparable{
    private int min, sec, totalInSec;

    Duration(){ min = 0; sec = 0; totalInSec = 0; }
    public Duration(int min, int sec){
        this.min=min;
        this.sec=sec;
        this.totalInSec = min*60 + sec;
    }
    public  Duration(int totalInSec){
        this.totalInSec = totalInSec;
        min = totalInSec/60;
        sec = totalInSec%60;
    }

    public void setTime(int min, int sec){
        this.min=min;
        this.sec=sec;
        totalInSec = min*60 + sec;
    }

    public void setMin(int min){ this.min = min; totalInSec = min*60 + sec;}
    public void setSec(int sec){
        if(sec>=60){ min += sec/60; sec%=60; }
        this.sec = sec; totalInSec = min*60 + sec;
    }
    public void setTotalInSec(int totalInSec){
        this.totalInSec = totalInSec;
        min = totalInSec/60;
        sec = totalInSec%60;
    }

    public int getMin(){ return min; }
    public int getSec(){ return sec;}
    public int getTotalInSec(){ return totalInSec; }

    public static int totalInSec(int min, int sec){ return (min*60 + sec); }

    @Override public int compareTo(Object o) { return (totalInSec - ((Duration)o).totalInSec); }
    public int compareTo(int min, int sec){ return (totalInSec - (min*60 + sec)); }

    @NonNull @Override public String toString() {
        return (make2digit(min) +":"+make2digit(sec));
    }
    private String make2digit(int num){ // make number 2 digit by adding 0 at front.
        return ( (num>=0&&num<=9) ? "0"+num : String.valueOf(num) );
    }
    public void fromString(String str){
        String[] array = str.split(":");
        try{
            min = Integer.parseInt(array[0]);
            sec = Integer.parseInt(array[1]);
        }catch (Exception e){ e.printStackTrace(); }
    }
}
