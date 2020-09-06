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
    else if(rawInt>=78) return 69;
    else if(rawInt>=75) return 68;
    else if(rawInt>=71) return 67;
    else if(rawInt>=62) return (64+(rawInt-62)/3);
    else if(rawInt>=54) return (62+(rawInt-54)/4);
    else if(rawInt>=49) return 61;
    else if(rawInt>45) return 60;
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

JNIEXPORT jint JNICALL Java_mil_army_fitnesstest_Standard_00024ACFT_SDCScore(JNIEnv *env, jclass clazz, jint sec){
    if(sec<=93) return 100;
    else if(sec<=99) return (98+(99-sec)/3);
    else if(sec<=105) return (95+(105-sec)/2);
    else if(sec<=130) return (70+(130-sec));
    else if(sec<=150) return (65+(150-sec)/4);
    else if(sec<=170) return (61+(170-sec)/5);
    else if(sec<=180) return 60;
    else if(sec<=190) return (50+(190-sec));
    else if(sec<=214) return ((215-sec)*2);
    else return 0;
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

JNIEXPORT jint JNICALL Java_mil_army_fitnesstest_Standard_00024ACFT_RUNScore(JNIEnv *env, jclass clazz, jint sec) {
    if(sec<=810) return 100;
    else if(sec<=1080) return (70+(1080-sec)/9);
    else if(sec<=1140) return (65+(1140-sec)/12);
    else if(sec<=1260) return (60+(1260-sec)/24);
    else if(sec<=1366)
        switch (sec%9){
            case 1: return(4+(1368-sec)/9*5);
            case 2: case 3: return(3+(1368-sec)/9*5);
            case 4: case 5: return(2+(1368-sec)/9*5);
            case 6: case 7: return(1+(1368-sec)/9*5);
            default: return((1368-sec)/9*5); //case 0: case 8:
        }
    else return 0;
}