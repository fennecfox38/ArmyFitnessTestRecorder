#include <jni.h>
#include <math.h>

JNIEXPORT jint JNICALL Java_mil_army_fitnesstest_Standard_00024ACFT_MDLScore(JNIEnv *env, jclass clazz, jint raw) {
    if(raw>=340) return 100;
    else if(raw>=330) return 97;
    else if(raw>=190) return ((((raw/10)-19)*2)+68);
    else if(raw>=150) return (((raw/10)-15)+62);
    else if(raw>=80) return (((raw/10)-8)*10);
    else return 0;
}

JNIEXPORT jint JNICALL Java_mil_army_fitnesstest_Standard_00024ACFT_SPTScore(JNIEnv *env, jclass clazz, jint rawInt) {
    if(rawInt>=125) return 100;
    else if(rawInt>=80) {
        switch (rawInt%3){
            case 0: return ((rawInt-81)*2/3+70);
            case 1: return ((rawInt-82)*2/3+71);
            case 2: return ((rawInt-80)*2/3+70);
            default: return -1;
        }
    }
    else if(rawInt>45){
        if(rawInt>=78) return 69;
        else if(rawInt>=75) return 68;
        else if(rawInt>=71) return 67;
        else if(rawInt>=62) return (64+(rawInt-62)/3);
        else if(rawInt>=54) return (62+(rawInt-54)/4);
        else if(rawInt>=49) return 61;
        else return 60;
    }
    else if(rawInt>33) return ((rawInt-33)*5);
    else return 0;
}

JNIEXPORT jint JNICALL Java_mil_army_fitnesstest_Standard_00024ACFT_HPUScore(JNIEnv *env, jclass clazz, jint raw) {
    if(raw>=60) return 100;
    else if(raw>=30) return raw+40;
    else if(raw>=10) return (((raw-10)/2)+60);
    else if(raw>0) return (raw*5+10);
    else return 0;
}

JNIEXPORT jint JNICALL Java_mil_army_fitnesstest_Standard_00024ACFT_SDCScore(JNIEnv *env, jclass clazz, jint min, jint sec){
    switch (min){
        case 0: return 100;
        case 1:
            if(sec<=33) return 100;
            else if(sec<=39) return (98+(39-sec)/3);
            else if(sec<=45) return (95+(45-sec)/2);
            else return (80+(60-sec));
        case 2:
            if(sec<=10) return (70+(70-sec));
            else if(sec<=30) return (65+(30-sec)/4);
            else if(sec<=50) return (61+(50-sec)/5);
            else return 60;
        case 3:
            if(sec==0) return 60;
            else if(sec<=10) return (50+(10-sec));
            else if(sec<=34) return ((35-sec)*2);
            else return 0;
        default: return 0;
    }
}

JNIEXPORT jint JNICALL Java_mil_army_fitnesstest_Standard_00024ACFT_LTKScore(JNIEnv *env, jclass clazz, jint raw) {
    if(raw>=20) return 100;
    else if(raw>=5) return ((raw-5)*2+70);
    switch (raw){
        case 4: return 67;
        case 3: return 65;
        case 2: return 63;
        case 1: return 60;
        default: return 0;
    }
}

JNIEXPORT jint JNICALL Java_mil_army_fitnesstest_Standard_00024ACFT_RUNScore(JNIEnv *env, jclass clazz, jint min, jint sec) {
    if(min<13) return 100;
    switch (min){
        case 13:                if(sec<=30) return 100; // after 13:30 follows next statements.
        case 14: case 15: case 16: case 17: return (70+((18-min)*60-sec)/9);
        case 18:                            return (65+(60-sec)/12);
        case 19: case 20:                   return (60+((21-min)*60-sec)/24);
        case 21:
            switch (sec%9){
                case 0:                     return (60-(sec/9)*5);
                case 1:                     return (59-(sec/9)*5);
                case 2: case 3:             return (58-(sec/9)*5);
                case 4: case 5:             return (57-(sec/9)*5);
                case 6: case 7:             return (56-(sec/9)*5);
                case 8:                     return (55-(sec/9)*5);
                default:                    return -1;
            }
        case 22:
            if(sec>46)                      return 0;
            switch (sec%9){
                case 0: case 1:             return (26-(sec/9)*5);
                case 2: case 3:             return (25-(sec/9)*5);
                case 4:                     return (24-(sec/9)*5);
                case 5: case 6:             return (23-(sec/9)*5);
                case 7: case 8:             return (22-(sec/9)*5);
                default:                    return -1;
            }
        default:                            return 0;
    }
}

JNIEXPORT jint JNICALL Java_mil_army_fitnesstest_Standard_00024ACFT_AlterScore(JNIEnv *env, jclass clazz, jint min, jint sec) {
    return (min<25||(min==25&&sec==0) ? 60 : 0);
}