import 'package:flutter/material.dart';
import 'package:army_fitness_test_recorder/apft.dart';
import 'package:army_fitness_test_recorder/count_event.dart';
import 'package:army_fitness_test_recorder/duration_event.dart';
import 'package:army_fitness_test_recorder/group.dart';

class APFTPage extends StatefulWidget { @override _APFTPageState createState() => _APFTPageState(); }

class _APFTPageState extends State<APFTPage> with APFTRecord{
  final keyPU = GlobalKey<CountEventState>();
  final keySU = GlobalKey<CountEventState>();
  final keyCardio = GlobalKey<DurationEventState>();

  void _invalidatePage(){
    keyPU.currentState.invalidate();
    keySU.currentState.invalidate();
    keyCardio.currentState.invalidate();
  }

  scoreReturn(int _index)=> {'score': score[_index], 'level': levelPF[_index], 'isPassed': (levelPF[_index]!=LevelPF.Fail), };

  @override Widget build(BuildContext context) {
    isPassed=true;
    levelPF.forEach((e) { if(e==LevelPF.Fail) isPassed=false; });

    return Column(
      mainAxisAlignment: MainAxisAlignment.end, mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          child: ListView(
            children: [
              CountEvent(key: keyPU, event: EventCount.PU, onChanged: (_value){
                setState((){ valuePU=_value; score[0]=givePUScore(sex: sex.index, ageGroup: age.index, raw: _value.toInt(),); levelPF[0]=LevelPF.fromScore(score[0], true); });
                return scoreReturn(0);
              }, ),
              CountEvent(key: keySU, event: EventCount.SU, onChanged: (_value){
                setState((){ valueSU=_value; score[1]=giveSUScore(ageGroup: age.index, raw: _value.toInt(),); levelPF[1]=LevelPF.fromScore(score[1], true); });
                return scoreReturn(1);
              }, ),
              DurationEvent(key: keyCardio, event: EventDuration.CardioAPFT, onChanged: (_duration, _alter){
                setState(() {
                  valueCardio=_duration; this.alter=_alter;
                  switch(_alter){
                    case AlterAPFT.Run: score[2]= giveRUNScoreAPFT(sex: sex.index, ageGroup: age.index, sec: _duration.inSec); break;
                    case AlterAPFT.Walk: score[2]= giveWalkScore(sex: sex.index, ageGroup: age.index, sec: _duration.inSec); break;
                    case AlterAPFT.Bike: score[2]= giveBikeScore(sex: sex.index, ageGroup: age.index, sec: _duration.inSec); break;
                    case AlterAPFT.Swim: score[2]= giveSwimScore(sex: sex.index, ageGroup: age.index, sec: _duration.inSec); break;
                  }
                  levelPF[2]=LevelPF.fromScore(score[2], true);
                });
                return scoreReturn(2);
              }, ),
            ],
          ),
        ),
        Padding(
          padding: const EdgeInsets.only(top: 16.0, bottom: 0.0, left: 16.0, right: 16.0),
          child: FittedBox( fit: BoxFit.cover,
            child: Row( mainAxisAlignment: MainAxisAlignment.start, children: [
              RadioGroup(title: 'Sex',values: Sex.values, initialValue: sex, onChanged: (_value)=>setState((){sex=_value; _invalidatePage();}), ),
              SizedBox(width: 16,),
              Spinner(values: AgeAPFT.values, initialValue: age, title: 'Age' ,onChanged: (_value)=>setState((){age=_value; _invalidatePage();}), ),
            ],),
          ),
        ),
        Padding(
          padding: const EdgeInsets.only(top: 0.0, bottom: 16.0, left: 0.0, right: 16.0),
          child: Row( mainAxisAlignment: MainAxisAlignment.spaceBetween, mainAxisSize: MainAxisSize.max,
            children: [
              ConstrainedBox( constraints: BoxConstraints(maxWidth: MediaQuery.of(context).size.width*0.75, ), child: FittedBox( fit: BoxFit.cover, child: Row( children: [
                DatePickTile(onClicked: (_date)=> date=_date,), SizedBox(width: 16,),
                Text('Score $totalScore ',style: TextStyle(fontSize: 20),), SizedBox(width: 16,),
                (isPassed?Text('Pass',style: TextStyle(color: Colors.green,fontSize: 20),):Text('Fail',style: TextStyle(color: Colors.red,fontSize: 20),)),
                SizedBox(width: 16,),
              ],),),),
              FloatingActionButton( child: Icon(Icons.save, ), onPressed: ()=>APFTDBHelper().insertRecord(this, context: context), heroTag: 'SaveAPFTRecord',),
            ],
          ),
        ),
      ],
    );
  }

}

/// *******************************************************************************************************************************************
final List<List<List<int>>> pu =[ //PUM[8][78] PUF[8][51]
  [
    [0,3,5,6,8,9,10,12,13,14,16,17,19,20,21,23,24,26,27,28,30,31,32,34,35,37,38,39,41,42,43,45,46,48,49,50,52,53,54,56,57,59,60,61,63,64,66,67,68,70,71,72,74,75,77,78,79,81,82,83,85,86,88,89,90,92,93,94,96,97,99,100,100,100,100,100,100,100],
    [0,15,17,18,19,20,21,22,23,25,26,27,28,29,30,31,33,34,35,36,37,38,39,41,42,43,44,45,46,47,49,50,51,52,53,54,55,57,58,59,60,61,62,63,65,66,67,68,69,70,71,73,74,75,76,77,78,79,81,82,83,84,85,86,87,89,90,91,92,93,94,95,97,98,99,100,100,100],
    [0,20,21,22,23,24,25,26,27,28,29,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,91,92,93,94,95,96,97,98,99,100],
    [0,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,100,100],
    [0,26,27,28,29,30,31,32,33,34,35,36,37,38,39,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,100,100,100,100],
    [0,0,0,0,0,32,33,34,36,37,38,39,40,41,42,43,44,46,47,48,49,50,51,52,53,54,56,57,58,59,60,61,62,63,64,66,67,68,69,70,71,72,73,74,76,77,78,79,80,81,82,83,84,86,87,88,89,90,91,92,93,94,96,97,98,99,100,100,100,100,100,100,100,100,100,100,100,100],
    [0,0,0,0,0,36,38,39,40,41,42,45,46,47,48,49,50,51,52,53,54,55,56,58,59,60,61,62,64,65,66,67,68,69,71,72,73,74,75,76,78,79,80,81,82,85,86,87,88,89,90,91,92,93,94,95,96,98,99,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100],
    [0,0,0,0,0,43,44,46,47,48,49,50,51,52,53,54,56,57,58,59,60,61,62,63,64,66,67,68,69,70,71,72,73,74,76,77,78,79,80,81,82,83,84,86,87,88,89,90,91,92,94,95,96,97,98,99,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100],
  ],
  [
    [0,29,30,32,34,36,37,39,41,43,44,46,58,50,51,53,55,57,58,60,62,63,65,67,69,70,72,74,76,77,79,81,83,84,86,88,90,91,93,95,97,98,100,100,100,100,100,100,100,100,100],
    [0,38,39,41,42,43,45,45,48,49,49,50,52,54,56,57,59,60,61,63,64,66,67,68,70,71,72,74,75,77,78,79,81,82,83,85,86,88,89,90,92,93,94,96,97,99,100,100,100,100,100],
    [0,41,42,43,44,45,47,48,49,49,50,52,54,55,56,58,59,60,61,62,64,65,66,67,68,70,71,72,73,75,76,77,78,79,81,82,83,84,85,87,88,89,90,92,93,94,95,96,98,99,100],
    [0,41,43,44,45,47,48,49,49,50,52,54,58,58,59,60,61,63,64,65,67,68,69,71,72,73,75,76,77,79,80,81,83,84,85,87,88,89,91,92,93,95,96,100,100,100,100,100,100,100,100],
    [0,42,44,45,47,48,50,51,53,54,56,57,59,60,61,63,64,66,67,69,70,72,73,75,76,78,79,81,82,84,85,87,88,90,91,93,94,96,97,99,100,100,100,100,100,100,100,100,100,100,100],
    [0,0,0,0,0,49,50,52,54,55,57,58,50,62,63,65,66,68,70,71,73,64,76,78,79,81,82,84,86,87,89,90,92,94,95,97,98,100,100,100,100,100,100,100,100,100,100,100,100,100,100],
    [0,0,0,0,0,52,53,55,57,58,60,62,63,65,67,68,70,72,73,75,77,78,80,82,83,85,87,88,90,92,93,95,97,98,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100],
    [0,0,0,0,0,53,55,56,58,60,62,64,65,67,69,71,73,75,76,78,80,82,84,85,87,89,91,93,95,96,89,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100],
  ],
];

final List<List<int>> su=[ //[][62]
  [9,10,12,14,15,17,18,20,22,23,25,26,28,30,31,33,34,36,38,39,41,42,44,45,47,49,50,52,54,55,57,58,60,62,63,65,66,68,70,71,73,74,76,78,79,81,82,84,87,88,89,90,92,94,95,97,98,100,100,100,100,100],
  [21,23,24,25,27,28,29,31,32,33,35,36,37,39,40,41,43,44,45,47,48,49,50,52,53,55,56,57,59,60,61,63,64,65,67,68,69,71,72,73,75,76,77,79,80,81,83,84,85,87,88,89,91,92,93,95,96,97,99,100,100,100],
  [34,35,36,37,38,39,41,42,43,44,45,46,47,48,49,50,51,52,54,55,56,57,58,59,60,61,62,63,64,65,66,68,69,70,71,72,73,74,75,76,77,78,79,81,82,83,84,85,86,87,88,89,90,91,92,94,95,96,97,98,99,100],
  [35,36,38,39,40,41,42,44,45,46,47,48,49,50,52,53,54,55,56,58,59,60,61,62,64,65,66,67,68,69,71,72,73,74,75,76,78,79,80,81,82,84,85,86,87,88,89,91,92,93,94,95,96,98,99,100,100,100,100,100,100,100],
  [42,43,44,45,46,47,48,49,50,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,91,92,93,94,95,96,97,98,99,100,100,100,100,100,100,100],
  [49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,100,100,100,100,100,100,100,100,100,100],
  [50,51,52,53,54,56,57,58,59,60,61,62,63,64,66,67,68,69,70,71,72,73,74,76,77,78,79,80,81,82,83,84,86,87,88,89,90,91,92,93,94,96,97,98,99,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100],
  [53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,91,92,93,94,95,96,97,98,99,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100]
];

int givePUScore({@required int sex, @required int ageGroup,@required int raw}) {
  if((sex==0&&raw>77)||(sex==1&&raw>50)) return 100;
  else return pu[sex][ageGroup][raw];
}
int giveSUScore({@required int ageGroup,@required int raw}) {
  if(raw<21) return 0;
  else if(raw>82) return 100;
  else return su[ageGroup][raw-21];
}

final List<List<List<int>>> run = [
  [
    [0,1,2,3,5,6,8,9,10,12,13,14,17,18,19,20,21,23,24,26,27,28,30,31,32,34,35,37,38,39,41,42,43,45,46,48,49,50,52,53,54,56,57,59,60,61,63,64,66,67,68,70,71,72,74,75,77,78,79,81,82,83,85,86,88,89,90,92,93,94,96,97,99,100],
    [0,1,2,3,4,6,7,8,9,10,11,12,13,14,16,17,18,19,20,21,22,23,24,26,27,28,29,30,31,32,33,34,36,37,38,39,40,41,42,43,44,46,47,48,49,50,51,52,53,54,56,57,58,59,60,61,62,63,64,66,67,68,69,70,71,72,73,74,76,77,78,79,80,81,82,83,84,86,87,88,89,90,91,92,93,94,96,97,98,99,100],
    [0,1,2,3,4,5,6,7,8,9,10,11,12,14,15,16,17,18,19,20,21,22,23,24,25,28,29,30,31,32,33,34,35,36,37,38,39,41,42,43,44,45,46,47,48,49,50,51,52,54,55,56,57,58,59,60,61,62,63,64,65,66,68,69,70,71,72,73,74,75,76,77,78,79,81,82,83,84,85,86,87,88,89,90,91,92,94,95,96,97,98,99,100],
    [0,1,2,3,4,5,5,6,7,8,9,10,11,12,13,14,15,15,16,17,18,19,20,21,22,23,24,25,25,26,27,28,29,30,31,32,33,34,35,35,36,37,38,39,40,41,42,43,44,45,45,46,47,48,49,50,51,52,53,54,55,55,56,57,58,59,60,61,62,63,64,65,65,66,67,68,69,70,71,72,73,74,75,75,76,77,78,79,80,81,82,83,84,85,85,86,87,88,89,90,91,92,93,94,95,95,96,97,98,99,100],
    [0,1,2,3,4,5,6,6,7,8,9,10,11,11,12,13,14,15,16,17,17,18,19,20,21,22,23,23,24,25,26,27,28,29,29,30,31,32,33,34,34,35,36,37,38,39,40,40,41,42,43,44,45,46,46,47,48,49,50,51,51,52,53,54,55,56,57,57,58,59,60,61,62,63,63,64,65,66,67,68,69,69,70,71,72,73,74,74,75,76,77,78,79,80,80,81,82,83,84,85,86,86,87,88,89,90,91,91,92,93,94,95,96,97,97,98,99,100],
    [0,1,2,3,3,4,5,6,7,8,9,10,10,11,12,13,14,15,16,17,17,18,19,20,21,22,23,23,24,25,26,27,28,29,30,30,31,32,33,34,35,36,37,37,38,39,40,41,42,43,43,44,45,46,47,48,49,50,50,51,52,53,54,55,56,57,57,58,59,60,61,62,63,63,64,65,66,67,68,69,70,70,71,72,73,74,75,76,77,77,78,79,80,81,82,83,83,84,85,86,87,88,89,89,90,91,92,93,94,95,96,97,97,98,99,100],
    [5,6,7,7,8,9,10,11,11,12,13,14,15,15,16,17,18,18,19,20,21,22,22,23,24,25,25,26,27,28,29,29,30,31,32,33,33,34,35,36,36,37,38,39,40,40,41,42,43,44,44,45,46,47,47,48,49,50,51,51,52,53,54,55,55,56,57,58,58,59,60,61,62,62,63,64,65,65,66,67,68,69,69,70,71,72,73,73,74,75,76,76,77,78,79,80,80,81,82,83,84,84,85,86,87,87,88,89,90,91,91,92,93,94,95,95,96,97,98,98,99,100],
    [7,8,9,10,11,11,21,13,14,15,15,16,17,18,18,19,20,21,22,22,23,24,25,25,26,27,28,29,29,30,31,32,33,33,34,35,36,36,37,38,39,40,40,41,42,43,44,44,45,46,47,47,48,49,50,51,51,52,53,54,55,55,56,57,58,58,59,60,61,62,62,63,64,65,65,66,67,68,69,69,70,71,72,73,73,74,75,76,76,77,78,79,80,80,81,82,83,84,84,85,86,87,87,88,89,90,91,91,92,93,94,95,95,96,97,98,98,99,100],
  ],
  [
    [0,1,2,3,4,5,7,8,9,10,12,13,14,15,16,18,19,20,21,22,24,25,26,27,28,30,31,32,33,35,36,37,38,39,41,42,43,44,45,47,48,49,50,52,53,54,55,56,58,59,60,61,62,64,65,66,67,68,70,71,72,73,75,76,77,78,79,81,82,83,84,85,87,88,89,90,92,93,94,95,96,98,99,100],
    [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100],
    [9,10,11,11,12,13,14,15,16,17,17,18,19,20,21,22,23,23,24,25,26,27,28,29,29,30,31,32,33,34,34,35,36,37,38,39,40,40,41,42,43,44,45,46,46,47,48,49,50,51,51,52,53,54,55,56,57,57,58,59,60,61,62,63,63,64,65,66,67,68,69,69,70,71,72,73,74,74,75,76,77,78,79,80,80,81,82,83,84,85,86,86,87,88,89,90,91,91,92,93,94,95,96,97,97,98,99,100],
    [27,28,28,29,30,30,31,32,32,33,34,34,35,36,37,37,38,39,39,40,41,41,42,43,43,44,45,46,46,47,48,48,49,49,50,51,52,52,53,54,54,55,56,57,57,58,59,59,60,61,61,62,63,63,64,65,66,66,67,68,68,69,70,70,71,72,72,73,74,74,75,76,77,77,78,79,79,80,81,81,82,83,83,84,85,86,86,87,88,88,89,90,90,91,92,92,93,94,94,95,96,97,97,98,99,99,100],
    [33,34,35,35,36,37,38,38,39,40,40,41,42,42,43,44,45,45,46,47,47,48,49,49,50,51,52,52,53,54,54,55,56,56,57,58,59,59,60,61,62,63,64,64,65,66,66,67,68,68,69,70,71,71,72,73,73,74,75,75,76,77,78,78,79,80,80,81,82,82,83,84,85,85,86,87,87,88,89,89,90,91,92,92,93,94,94,95,96,96,97,98,99,99,100],
    [42,43,43,44,45,45,46,47,47,48,49,49,50,50,51,52,52,53,54,54,55,56,56,57,57,58,59,59,60,61,61,62,63,63,64,64,65,66,66,67,68,68,69,70,70,71,71,72,73,73,74,75,75,76,77,77,78,78,79,80,80,81,82,82,83,83,84,85,85,86,87,87,88,89,89,90,90,91,92,92,93,94,94,95,96,96,97,97,98,99,99,100],
    [44,45,46,46,47,47,48,49,49,50,51,51,52,52,53,54,54,55,56,56,57,57,58,59,59,60,61,61,62,62,63,64,64,65,66,66,67,67,68,69,69,70,71,71,72,72,73,74,74,75,76,76,77,77,78,79,79,80,81,81,82,82,83,84,84,85,86,86,87,87,88,89,89,90,91,91,92,92,93,94,94,95,96,96,97,97,98,99,99,100],
    [44,45,46,47,47,48,49,50,50,51,52,53,53,54,55,56,56,57,58,59,59,60,61,61,62,63,64,64,65,66,67,67,68,69,70,70,71,72,73,73,74,75,76,76,77,78,79,79,80,81,81,82,83,84,84,85,86,87,87,88,89,90,90,91,92,93,93,94,95,96,96,97,98,99,99,100],
  ],
];
final List<List<List<int>>> runBoundary=[ //[2][8][2]
  [[1218,780],[1320,780],[1356,798],[1458,798],[1518,816],[1536,846],[1590,864],[1590,882]],
  [[1434,936],[1536,936],[1590,948],[1590,954],[1590,1020],[1590,1044],[1590,1056],[1590,1140]],
];

int giveRUNScoreAPFT({@required int sex, @required int ageGroup, @required int sec}) {
  if(sec>runBoundary[sex][ageGroup][0]) return 0;
  else if(sec<=runBoundary[sex][ageGroup][1]) return 100;
  else return run[sex][ageGroup][(runBoundary[sex][ageGroup][0]-sec)~/6];
}

final List<List<int>> walk = [ [2040,2070,2100,2130,2160,2190,2220,2250], [2220,2250,2280,2310,2340,2370,2400,2430], ];
final List<List<int>> bike = [ [1440,1470,1500,1530,1560,1620,1680,1800], [1500,1530,1560,1590,1620,1680,1800,1920], ];
final List<List<int>> swim = [ [1200,1230,1260,1290,1320,1350,1380,1440], [1260,1290,1320,1350,1380,1410,1440,1500], ];
int giveWalkScore({@required int sex, @required int ageGroup, @required int sec}){ return ( ( sec < walk[sex][ageGroup] ) ? 60: 0 ); }
int giveBikeScore({@required int sex, @required int ageGroup, @required int sec}){ return ( ( sec < bike[sex][ageGroup] ) ? 60: 0 ); }
int giveSwimScore({@required int sex, @required int ageGroup, @required int sec}){ return ( ( sec < swim[sex][ageGroup] ) ? 60: 0 ); }

/// *******************************************************************************************************************************************



