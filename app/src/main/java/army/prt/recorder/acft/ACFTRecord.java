package army.prt.recorder.acft;

public class ACFTRecord{
    public int raw_MDL=0, raw_HPU=0, raw_LTK=0;
    public float raw_SPT=0.0f;
    public Duration duration_SDC, duration_Cardio;
    public int sco_MDL=0, sco_SPT=0, sco_HPU=0, sco_SDC=0, sco_LTK=0, sco_Cardio=0, sco_total=0;
    ACFTRecord(){
        duration_SDC = new Duration(0,0);
        duration_Cardio = new Duration(0,0);
    }
    public int getScoreTotal(){
        sco_total = sco_MDL + sco_SPT + sco_HPU + sco_SDC + sco_LTK + sco_Cardio;
        return sco_total;
    }
}
