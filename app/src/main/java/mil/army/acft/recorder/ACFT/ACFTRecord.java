package mil.army.acft.recorder.ACFT;

public class ACFTRecord {
    private int raw_MDL, raw_HPU, raw_LTK, cardioAlter;
    private float raw_SPT;
    private Duration raw_SDC, raw_Cardio;
    private int sco_MDL, sco_SPT, sco_HPU, sco_SDC, sco_LTK, sco_Cardio;

    ACFTRecord(){
        raw_SDC = new Duration();
        raw_Cardio = new Duration();
        cardioAlter = 0;
    }

    public void setRaw_MDL(int raw_MDL) { this.raw_MDL = raw_MDL; }
    public void setRaw_SPT(float raw_SPT) { this.raw_SPT = raw_SPT; }
    public void setRaw_HPU(int raw_HPU) { this.raw_HPU = raw_HPU; }
    public void setRaw_SDC(int min, int sec) { raw_SDC.setTime(min,sec); }
    public void setRaw_SDC(Duration duration) { raw_SDC = duration; }
    public void setRaw_LTK(int raw_LTK) { this.raw_LTK = raw_LTK; }
    public void setRaw_Cardio(int min, int sec) { raw_Cardio.setTime(min,sec); }
    public void setRaw_Cardio(Duration duration) { raw_Cardio = duration; }
    public void setCardioAlter(int cardioAlter) { this.cardioAlter = cardioAlter; }

    public int getRaw_MDL() { return raw_MDL; }
    public float getRaw_SPT() { return raw_SPT; }
    public int getRaw_HPU() { return raw_HPU; }
    public Duration getRaw_SDC() { return raw_SDC; }
    public int getRaw_LTK() { return raw_LTK; }
    public Duration getRaw_Cardio() { return raw_Cardio; }
    public int getCardioAlter() { return cardioAlter; }

}
