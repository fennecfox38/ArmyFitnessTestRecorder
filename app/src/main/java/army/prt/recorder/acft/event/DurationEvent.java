package army.prt.recorder.acft.event;

import army.prt.recorder.acft.Duration;

public class DurationEvent extends Event{
    public static final int RUN=0,ROW=1,BIKE=2,SWIM=3;
    public Duration duration;
    public int cardioAlter = 0;

    public DurationEvent(int eventType, String title, int max){
        super(eventType, title, max);
        duration = new Duration(0,0);
        if(eventType==CARDIO) cardioAlter = 0;
    }

    public void giveScore(){
        if(eventType == SDC) sco = SDCScore(duration);
        else if(cardioAlter==RUN) sco = RUNScore(duration);
        else sco = AlterScore(duration);
    }

    public static int SDCScore(Duration duration){
        if(duration.compareTo(1,33)<=0) return 100;
        else if(duration.compareTo(1,39)<=0) return (98+((Duration.totalInSec(1,39)-duration.getTotalInSec())/3));
        else if(duration.compareTo(1,45)<=0) return (95+((Duration.totalInSec(1,45)-duration.getTotalInSec())/2));
        else if(duration.compareTo(2,10)<=0) return (70+(Duration.totalInSec(2,10)-duration.getTotalInSec()));
        else if(duration.compareTo(2,30)<=0) return (65+((Duration.totalInSec(2,30)-duration.getTotalInSec())/4));
        else if(duration.compareTo(2,50)<=0) return (61+((Duration.totalInSec(2,50)-duration.getTotalInSec())/5));
        else if(duration.compareTo(3,0)<=0) return 60;
        else if(duration.compareTo(3,10)<=0) return (50+(Duration.totalInSec(3,10)-duration.getTotalInSec()));
        else if(duration.compareTo(3,34)<=0) return ((Duration.totalInSec(3,35)-duration.getTotalInSec())*2);
        else return 0;
    }

    public static int RUNScore(Duration duration){
        if(duration.compareTo(13,30)<=0) return 100;
        else if(duration.compareTo(18,0)<=0) return (70+((Duration.totalInSec(18,0)-duration.getTotalInSec())/9));
        else if(duration.compareTo(19,0)<=0) return (65+((Duration.totalInSec(19,0)-duration.getTotalInSec())/12));
        else if(duration.compareTo(21,0)<=0) return (60+((Duration.totalInSec(21,0)-duration.getTotalInSec())/24));
        else if(duration.compareTo(21,9)<=0) return (55+((Duration.totalInSec(21,9)-duration.getTotalInSec())/2));
        else if(duration.compareTo(21,18)<=0) return (50+((Duration.totalInSec(21,18)-duration.getTotalInSec())/2));
        else if(duration.compareTo(21,27)<=0) return (45+((Duration.totalInSec(21,27)-duration.getTotalInSec())/2));
        else if(duration.compareTo(21,36)<=0) return (40+((Duration.totalInSec(21,36)-duration.getTotalInSec())/2));
        else if(duration.compareTo(21,45)<=0) return (35+((Duration.totalInSec(21,45)-duration.getTotalInSec())/2));
        else if(duration.compareTo(21,54)<=0) return (30+((Duration.totalInSec(21,54)-duration.getTotalInSec())/2));
        else if(duration.compareTo(22,3)<=0) return (25+((Duration.totalInSec(22,3)-duration.getTotalInSec())/2));
        else if(duration.compareTo(22,12)<=0) return (20+((Duration.totalInSec(22,12)-duration.getTotalInSec())/2));
        else if(duration.compareTo(22,21)<=0) return (15+((Duration.totalInSec(22,21)-duration.getTotalInSec())/2));
        else if(duration.compareTo(22,30)<=0) return (10+((Duration.totalInSec(22,30)-duration.getTotalInSec())/2));
        else if(duration.compareTo(22,39)<=0) return (5+((Duration.totalInSec(22,39)-duration.getTotalInSec())/2));
        else if(duration.compareTo(22,46)<=0) return ((Duration.totalInSec(22,48)-duration.getTotalInSec())/2);
        else return 0;
    }

    public static int AlterScore(Duration duration) { return (duration.compareTo(25,00)<=0 ? 60 : 0); }

}