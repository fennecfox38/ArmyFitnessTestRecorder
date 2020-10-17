import 'package:flutter/material.dart';
import 'package:army_fitness_test_recorder/acft.dart';
import 'package:army_fitness_test_recorder/count_event.dart';
import 'package:army_fitness_test_recorder/duration_event.dart';
import 'package:army_fitness_test_recorder/group.dart';

class ACFTPage extends StatefulWidget { @override _ACFTPageState createState() => _ACFTPageState(); }

class _ACFTPageState extends State<ACFTPage> with ACFTRecord{
  final List<GlobalKey<CountEventState>> countEventKeys= [GlobalKey<CountEventState>(), GlobalKey<CountEventState>(), GlobalKey<CountEventState>(), GlobalKey<CountEventState>(),];
  final List<GlobalKey<DurationEventState>> durationEventKeys= [GlobalKey<DurationEventState>(), GlobalKey<DurationEventState>(),];

  void _invalidatePage(){
    countEventKeys.forEach((e)=>e.currentState.invalidate());
    durationEventKeys.forEach((e)=>e.currentState.invalidate());
  }
  scoreReturn(int _index)=> {'score': score[_index], 'level': levelPF[_index], 'isPassed': levelPF[_index].satisfies(mos), };

  @override Widget build(BuildContext context) {
    qualified=LevelPF.qualified(levelPF);
    isPassed=qualified.satisfies(mos);
    return Column(
      mainAxisAlignment: MainAxisAlignment.end, mainAxisSize: MainAxisSize.max,
      children: [
        Expanded(
          child: ListView(
            children: [
              CountEvent(key: countEventKeys[0],event: EventCount.MDL, onChanged: (_value){
                setState((){ valueMDL=_value; score[0]=giveMDLScore(_value.toInt()); levelPF[0]=LevelPF.fromScore(score[0], false); });
                return scoreReturn(0);
              },),
              CountEvent(key: countEventKeys[1], event: EventCount.SPT, onChanged: (_value){
                setState((){ valueSPT=_value; score[1]=giveSPTScore((_value*10).toInt()); levelPF[1]=LevelPF.fromScore(score[1], false); });
                return scoreReturn(1);
              },),
              CountEvent(key: countEventKeys[2], event: EventCount.HPU, onChanged: (_value){
                setState((){ valueHPU=_value; score[2]=giveHPUScore(_value.toInt()); levelPF[2]=LevelPF.fromScore(score[2], false); });
                return scoreReturn(2);
              }, ),
              DurationEvent(key: durationEventKeys[0], event: EventDuration.SDC, onChanged: (_duration, _alter){
                setState((){ valueSDC=_duration; score[3]=giveSDCScore(_duration.inSec); levelPF[3]=LevelPF.fromScore(score[3], false); });
                return scoreReturn(3);
              }, ),
              CountEvent(key: countEventKeys[3], event: EventCount.LTK, onChanged: (_value){
                setState((){ valueLTK=_value; score[4]=giveLTKScore(_value.toInt()); levelPF[4]=LevelPF.fromScore(score[4], false); });
                return scoreReturn(4);
              }, ),
              DurationEvent(key: durationEventKeys[1], event: EventDuration.CardioACFT, onChanged: (_duration, _alter){
                setState(() {
                  valueCardio=_duration; alter=_alter;
                  if(alter==AlterACFT.Run){ score[5]=giveRUNScore(_duration.inSec); levelPF[5]=LevelPF.fromScore(score[5], false); }
                  else{ score[5]=(_duration.inSec<=1500 ? 60 : 0); levelPF[5]=LevelPF.fromScore(score[5], true); }
                });
                return scoreReturn(5);
              }, ),
            ],
          ),
        ),
        Padding(
          padding: const EdgeInsets.only(top: 16.0, bottom: 4.0, left: 16.0, right: 16.0),
          child: RadioGroup(title: 'MOS',values: MOSLevel.values, initialValue: mos, onChanged: (_value)=>setState((){mos=_value; _invalidatePage();}), ),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16.0),
          child: Row( mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Text('Score $totalScore satisfies ',style: TextStyle(fontSize: 20),),
              Text(qualified.toString(),style: TextStyle(color: (isPassed?Colors.green:Colors.red),fontSize: 20,),),
            ],
          ),
        ),
        Padding(
          padding: const EdgeInsets.only(top: 4.0, bottom: 16.0, left: 0.0, right: 16.0),
          child: Row( mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              DatePickTile(onClicked: (_date)=> date=_date,),
              (isPassed?Text('Pass',style: TextStyle(color: Colors.green,fontSize: 20),):Text('Fail',style: TextStyle(color: Colors.red,fontSize: 20),)),
              FloatingActionButton( child: Icon(Icons.save, ),  onPressed: ()=>ACFTDBHelper().insertRecord(this, context: context), heroTag: 'SaveACFTRecord',),
            ],
          ),
        ),
      ],
    );
  }

}



/// *******************************************************************************************************************************************
int giveMDLScore(int raw) {
  if(raw>=340) return 100;
  else if(raw>=330) return 97;
  else if(raw>=190) return ((((raw~/10)-19)*2)+68);
  else if(raw>=150) return (((raw~/10)-15)+62);
  else if(raw>=80) return (((raw~/10)-8)*10);
  else return 0;
}

int giveSPTScore(int rawInt) {
  if(rawInt>=125) return 100;
  else if(rawInt>=80) {
    switch (rawInt%3){
      case 0: return ((rawInt-81)*2~/3+70);
      case 1: return ((rawInt-82)*2~/3+71);
      case 2: return ((rawInt-80)*2~/3+70);
      default: return -1;
    }
  }
  else if(rawInt>=78) return 69;
  else if(rawInt>=75) return 68;
  else if(rawInt>=71) return 67;
  else if(rawInt>=62) return (64+(rawInt-62)~/3);
  else if(rawInt>=54) return (62+(rawInt-54)~/4);
  else if(rawInt>=49) return 61;
  else if(rawInt>45) return 60;
  else if(rawInt>33) return ((rawInt-33)*5);
  else return 0;
}

int giveHPUScore(int raw) {
  if(raw>=60) return 100;
  else if(raw>=30) return raw+40;
  else if(raw>=10) return (((raw-10)~/2)+60);
  else if(raw>0) return (raw*5+10);
  else return 0;
}

int giveSDCScore(int sec){
  if(sec<=93) return 100;
  else if(sec<=99) return (98+(99-sec)~/3);
  else if(sec<=105) return (95+(105-sec)~/2);
  else if(sec<=130) return (70+(130-sec));
  else if(sec<=150) return (65+(150-sec)~/4);
  else if(sec<=170) return (61+(170-sec)~/5);
  else if(sec<=180) return 60;
  else if(sec<=190) return (50+(190-sec));
  else if(sec<=214) return ((215-sec)*2);
  else return 0;
}

int giveLTKScore(int raw) {
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

int giveRUNScore(int sec) {
  if(sec<=810) return 100;
  else if(sec<=1080) return (70+(1080-sec)~/9);
  else if(sec<=1140) return (65+(1140-sec)~/12);
  else if(sec<=1260) return (60+(1260-sec)~/24);
  else if(sec<=1366)
    switch (sec%9){
      case 1: return(4+(1368-sec)~/9*5);
      case 2: case 3: return(3+(1368-sec)~/9*5);
      case 4: case 5: return(2+(1368-sec)~/9*5);
      case 6: case 7: return(1+(1368-sec)~/9*5);
      default: return((1368-sec)~/9*5); //case 0: case 8:
    }
  else return 0;
}
/// *******************************************************************************************************************************************
