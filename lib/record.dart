import 'package:army_fitness_test_recorder/duration.dart';
import 'package:army_fitness_test_recorder/group.dart';

class ACFTRecord{
  double valueMDL=0, valueSPT=0, valueHPU=0, valueLTK=0;
  Duration valueSDC=Duration.inSec(0), valueCardio=Duration.inSec(0);
  AlterACFT alter=AlterACFT.Run;
  List<int> score = <int>[0,0,0,0,0,0,];
  int get totalScore {
    int _result=0;
    score.forEach((e)=>_result+=e);
    return _result;
  }
  List<LevelPF> levelPF = <LevelPF>[LevelPF.Fail,LevelPF.Fail,LevelPF.Fail,LevelPF.Fail,LevelPF.Fail,LevelPF.Fail,];
  MOSLevel mos = MOSLevel.Moderate;
  LevelPF qualified = LevelPF.Fail; bool isPassed=false;
  String date = '2020-09-29';
}

class APFTRecord{
  double valuePU, valueSU; Duration valueCardio; AlterAPFT alter;
  List<int> score = <int>[0,0,0,];
  int get totalScore {
    int _result=0;
    score.forEach((e)=>_result+=e);
    return _result;
  }
  List<LevelPF> levelPF = <LevelPF>[LevelPF.Fail,LevelPF.Fail,LevelPF.Fail,];
  bool isPassed=false;
  Sex sex = Sex.Male; AgeAPFT age = AgeAPFT.age1721;
  String date = '2020-09-29';
}

class ABCPRecord{
  double height=58.0, weight=90, neck=10.0, abdomen=20.0, waist=20.0, hips=20.0;
  Sex sex = Sex.Male; AgeABCP age = AgeABCP.age1720;
  bool hwPass = false, bodyFatPass=false, isPassed=false;
  double bodyFatPercent = 0;
  String date = '2020-09-29';

}
