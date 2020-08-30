package army.prt.recorder.abcp;

public class Item {
    public static final int HEIGHT=0, WEIGHT=1, NECK=2, ABDOMEN_WAIST =3, HIPS=4;
    public String title, unit;
    public int itemType, min, max, raw=0;
    Item(int itemType, String title, String unit, int min, int max){
        this.itemType = itemType;
        this.title = title;
        this.unit = unit;
        this.min = min;
        this.max = max;
    }
}
