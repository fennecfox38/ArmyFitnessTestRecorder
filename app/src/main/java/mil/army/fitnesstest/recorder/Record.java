package mil.army.fitnesstest.recorder;

import android.content.ContentValues;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class Record<T> {
    public boolean isPassed=false;
    public int year, month, day;

    public Record(){
        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH)+1;
        day = today.get(Calendar.DAY_OF_MONTH);
    }

    public abstract void updateRecord(ArrayList<T> list);
    public abstract void restoreList(ArrayList<T> list);
    public abstract void invalidate(ArrayList<T> list);
    public abstract ContentValues getContentValues();

    public String dateToString(){
        String str = String.valueOf(year);
        str += "-"; str += make2digit(String.valueOf(month));
        str += "-"; str += make2digit(String.valueOf(day));
        return str;
    }
    public void stringToDate(String str){
        String[] array = str.split("-");
        year = Integer.parseInt(array[0]);
        month = Integer.parseInt(array[1]);
        day = Integer.parseInt(array[2]);
    }
    private static String make2digit(String num){ // make number 2 digit by adding 0 at front.
        return ( (num.length()<2) ? "0"+num : num );
    }

    public static String getPassed(boolean isPassed){ return (isPassed ? "Pass": "Fail"); }

    @NotNull public abstract String toString();
}
