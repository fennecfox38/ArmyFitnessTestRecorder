import 'dart:math';
import 'package:flutter/material.dart';
import 'package:army_fitness_test_recorder/count_item.dart';
import 'package:army_fitness_test_recorder/group.dart';
import 'package:army_fitness_test_recorder/record.dart';

class ABCPPage extends StatefulWidget { @override _ABCPPageState createState() => _ABCPPageState(); }

class _ABCPPageState extends State<ABCPPage> with ABCPRecord {

  @override Widget build(BuildContext context) {
    hwPass = isHWPassed(sex.index, age.index, height, weight);
    bodyFatPercent = (sex==Sex.Male ? maleBodyFat(height, neck, abdomen) : femaleBodyFat(height, neck, waist, hips));
    bodyFatPass = isBodyFatPassed(sex.index, age.index, bodyFatPercent);
    isPassed = hwPass || bodyFatPass;
    final List<CountItem> itemHW = [
      CountItem(item: Item.Height, initialValue: height, onChanged: (e){setState(() {height=e;});}, ),
      CountItem(item: Item.Weight, initialValue: weight, onChanged: (e){setState(() {weight=e;});}, ),
    ];
    final List<CountItem> itemMale = [
      CountItem(item: Item.Neck, initialValue: neck, onChanged: (e){setState(() {neck=e;});}, ),
      CountItem(item: Item.Abdomen, initialValue: abdomen, onChanged: (e){setState(() {abdomen=e;});}, ),
    ];
    final List<CountItem> itemFemale = [
      CountItem(item: Item.Neck, onChanged: (e){setState(() {neck=e;});}, ),
      CountItem(item: Item.Waist, onChanged: (e){setState(() {waist=e;});}, ),
      CountItem(item: Item.Hips, onChanged: (e){setState(() {hips=e;});}, ),
    ];
    return Column(
      mainAxisAlignment: MainAxisAlignment.spaceBetween, mainAxisSize: MainAxisSize.max,
      children: [
        Expanded( child: ListView( children: (hwPass?itemHW:itemHW+(sex==Sex.Male?itemMale:itemFemale)),), ),
        Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.only(top: 16.0, bottom: 4.0, left: 16.0, right: 16.0),
              child: Row( mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  RadioGroup(title: 'Sex',values: Sex.values, initialValue: sex, onChanged: (_value)=>setState(()=>sex=_value)),
                  SizedBox(width: 16,),
                  Spinner(values: AgeABCP.values, initialValue: age, title: 'Age' ,onChanged: (_value)=>setState(()=>age=_value), ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0),
              child: Row( mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  Text('HW/Pass ',style: TextStyle(fontSize: 16),),
                  (hwPass?Text('Pass',style: TextStyle(color: Colors.green,fontSize: 20),):Text('Fail',style: TextStyle(color: Colors.red,fontSize: 20),)),
                  SizedBox(width: 16,),
                  Text('BodyFat ',style: TextStyle(fontSize: 16),),
                  (hwPass?Text('N/A',style: TextStyle(fontSize: 20),):Text('$bodyFatPercent%',style: TextStyle(color: (bodyFatPass?Colors.green:Colors.red),fontSize: 20),)),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(top: 4.0, bottom: 16.0, left: 0.0, right: 16.0),
              child: Row( mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  DatePickTile(onClicked: (_date){ setState(()=>date=_date); },),
                  (isPassed?Text('Pass',style: TextStyle(color: Colors.green,fontSize: 20),):Text('Fail',style: TextStyle(color: Colors.red,fontSize: 20),)),
                  FloatingActionButton( child: Icon(Icons.save, ), onPressed: (){
                      Scaffold.of(context).showSnackBar(SnackBar(content: Text('Save Pressed'), action: SnackBarAction(label: 'OK', onPressed: (){},),));
                    },
                  ),
                ],
              ),
            ),
          ],
        ),
      ],
    );
  }

}

/// *******************************************************************************************************************************************

final List<List<List<int>>> HEIGHTWEIGHT = [ //[2][23][6]
  [
    [58,91,0,0,0,0], [59,94,0,0,0,0],
    [60,97,132,136,139,141], [61,100,136,140,144,146], [62,104,141,144,148,150],
    [63,107,145,149,153,155], [64,110,150,154,158,160], [65,114,155,159,163,165],
    [66,117,160,163,168,170], [67,121,165,169,174,176], [68,125,170,174,179,181],
    [69,128,175,179,184,186], [70,132,180,185,189,192], [71,136,185,189,194,197],
    [72,140,190,195,200,203], [73,144,195,200,205,208], [74,148,201,206,211,214],
    [75,152,206,212,217,220], [76,156,212,217,223,226], [77,160,218,223,229,232],
    [78,164,223,229,235,238], [79,168,229,235,241,244], [80,173,234,240,247,250],
  ],
  [
    [58,91,19,121,122,124], [59,94,124,125,126,128],
    [60,97,128,129,131,133], [61,100,132,134,135,137], [62,104,136,138,140,142],
    [63,107,141,143,144,146], [64,110,145,147,149,151], [65,114,150,152,154,156],
    [66,117,155,156,158,161], [67,121,159,161,163,166], [68,125,164,166,168,171],
    [69,128,169,171,173,176], [70,132,174,176,178,181], [71,136,179,181,183,186],
    [72,140,184,186,188,191], [73,144,189,191,194,197], [74,148,194,197,199,202],
    [75,152,200,202,204,208], [76,156,205,207,210,213], [77,160,210,213,215,219],
    [78,164,216,218,221,225], [79,168,221,224,227,230], [80,173,227,230,233,236],
  ],
];


final List<List<int>> BODYFAT = [ [20,22,24,26], [30,32,34,36] ];

bool isHWPassed(int sex, int ageGroup, double height, double weight) {
  int index = (height.ceil())-58;
  List<int> table;
  if(index>=23){
    table = HEIGHTWEIGHT[sex][22];
    int increment = (index-22)*(sex==0 ? 6 : 5);
    return (weight>=(table[1]+increment) && weight <=(table[ageGroup+2]+increment));
  }
  else if(index>=0){
    table = HEIGHTWEIGHT[sex][index];
    return (weight>=table[1] && weight<=table[ageGroup+2]);
  }
  else return false;
}

double maleBodyFat(double height, double neck, double abdomen) {
  double res = ((86.010 * log(abdomen-neck)/ln10) - (70.041 * log(height)/ln10) + 36.76);
  res = (res*10).round()/10;
  return (res>=0 ? res : 0);
}
double femaleBodyFat(double height, double neck, double waist, double hip) {
  double res = ((163.205 * log(waist+hip-neck)/ln10) - (97.684 * log(height)/ln10) - 78.387);
  res = (res*10).round()/10;
  return (res>=0 ? res : 0);
}
bool isBodyFatPassed(int sex, int ageGroup, double percentage) => percentage<=BODYFAT[sex][ageGroup];

/// *******************************************************************************************************************************************


