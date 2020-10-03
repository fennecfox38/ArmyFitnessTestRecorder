import 'package:flutter/material.dart';

class MOSLevel {
  final int index;
  const MOSLevel._internal(this.index);

  static const Moderate = const MOSLevel._internal(0);
  static const Significant = const MOSLevel._internal(1);
  static const Heavy = const MOSLevel._internal(2);
  static const values = const <MOSLevel>[Moderate,Significant,Heavy,];

  static List<String> _names = ['Moderate','Significant','Heavy',];
  toString() => '${_names[index]}';
}

class LevelPF {
  final int index;
  const LevelPF._internal(this.index);

  static const Fail = const LevelPF._internal(0);
  static const Moderate = const LevelPF._internal(1);
  static const Significant = const LevelPF._internal(2);
  static const Heavy = const LevelPF._internal(3);
  static const Pass = const LevelPF._internal(4);
  static const values = const <LevelPF>[Fail,Moderate,Significant,Heavy,Pass];

  static List<String> _names = ['Fail','Moderate','Significant','Heavy','Pass',];
  toString() => '${_names[index]}';
  bool satisfies(MOSLevel _level) => (index>=_level.index+1);
  static LevelPF qualified(List<LevelPF> _list){
    LevelPF _result = LevelPF.Pass;
    _list.forEach((e) { if(_result.index>e.index) _result=e; });
    return _result;
  }
  static LevelPF fromScore(int _score, bool isPF){
    if(isPF!=null && isPF) return (_score>=60? Pass:Fail);
    else{
      if(_score>=70) return Heavy;
      else if(_score>=65) return Significant;
      else if(_score>=60) return Moderate;
      else return Fail;
    }
  }
}


class Sex {
  final int index;
  const Sex._internal(this.index);

  static const Male = const Sex._internal(0);
  static const Female = const Sex._internal(1);
  static const values = const <Sex>[Male,Female,];

  static List<String> _names = ['Male','Female',];
  toString() => '${_names[index]}';
}

class AgeAPFT{
  final int index;
  const AgeAPFT._internal(this.index);

  static const age1721 = const AgeAPFT._internal(0);
  static const age2226 = const AgeAPFT._internal(1);
  static const age2731 = const AgeAPFT._internal(2);
  static const age3236 = const AgeAPFT._internal(3);
  static const age3741 = const AgeAPFT._internal(4);
  static const age4246 = const AgeAPFT._internal(5);
  static const age4751 = const AgeAPFT._internal(6);
  static const age5256 = const AgeAPFT._internal(7);
  static const values = const <AgeAPFT>[age1721, age2226, age2731, age3236,age3741, age4246, age4751, age5256,];

  static List<String> _names = ['17-21', '22-26', '27-31', '32-36','37-41', '42-46', '47-51', '52-56',];
  toString() => '${_names[index]}';
}

class AgeABCP{
  final int index;
  const AgeABCP._internal(this.index);

  static const age1720 = const AgeABCP._internal(0);
  static const age2127 = const AgeABCP._internal(1);
  static const age2839 = const AgeABCP._internal(2);
  static const age40_ = const AgeABCP._internal(3);
  static const values = const <AgeABCP>[age1720, age2127, age2839, age40_,];

  static List<String> _names = ['17-20', '21-27', '28-39', '40+',];
  toString() => '${_names[index]}';
}

class AlterACFT {
  final int index;
  const AlterACFT._internal(this.index);

  static const Run = const AlterACFT._internal(0);
  static const Row = const AlterACFT._internal(1);
  static const Bike = const AlterACFT._internal(2);
  static const Swim = const AlterACFT._internal(3);
  static const values = const <AlterACFT>[Run, Row, Bike, Swim,];

  static List<String> _names = ['2 Mile Run', '5000 M Row', '15000 M Bike', '1000 M Swim',];
  toString() => '${_names[index]}';
}

class AlterAPFT {
  final int index;
  const AlterAPFT._internal(this.index);

  static const Run = const AlterAPFT._internal(0);
  static const Walk = const AlterAPFT._internal(1);
  static const Bike = const AlterAPFT._internal(2);
  static const Swim = const AlterAPFT._internal(3);
  static const values = const <AlterAPFT>[Run, Walk, Bike, Swim,];

  static List<String> _names = ['2 Mile Run', '2.5 Mile Walk', '6.2 Mile Bike', '800 Yard Swim',];
  toString() => '${_names[index]}';
}


class RadioGroup extends StatefulWidget {
  final initialValue; final List values;
  final String title; final Function onChanged;
  RadioGroup({this.title: '', @required this.values, this.initialValue, @required this.onChanged});
  @override
  _RadioGroupState createState() => _RadioGroupState(initialValue==null?values[0]:initialValue);
}

class _RadioGroupState extends State<RadioGroup> {
  var value;
  _RadioGroupState(this.value);

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Text('${widget.title} ', style: TextStyle(fontSize: 20),),
        ...widget.values.map((_value) => Padding(
          padding: const EdgeInsets.all(2.0), child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Radio( value: _value, groupValue: value, onChanged: (e){ setState(()=>value=e); widget.onChanged(e); }, activeColor: Colors.orangeAccent, visualDensity: VisualDensity(horizontal: -4.0),),
              GestureDetector( child: Text(_value.toString()),  onTap: (){ setState(()=>value=_value); widget.onChanged(_value); }, ),
            ],
          ),
        )).toList(), ],
    );
  }
}

class Spinner extends StatefulWidget {
  final List values; final initialValue; final String title;
  final Function onChanged;
  Spinner({@required this.values, this.initialValue, this.title, @required this.onChanged});
  @override
  _SpinnerState createState() => _SpinnerState();
}

class _SpinnerState extends State<Spinner> {
  var value;

  @override void initState(){
    super.initState();
    value = (widget.initialValue==null ? widget.values[0] : widget.initialValue);
  }

  @override Widget build(BuildContext context) {
    DropdownButton spinner = DropdownButton(
      items: widget.values.map((e)=>DropdownMenuItem(value: e, child: Text(e.toString()))).toList(),
      onChanged: (e){ setState(()=>value=e); widget.onChanged(e); },
      value: value,
    );
    if(widget.title==null) return spinner;
    else return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8.0,),
      child: Row( mainAxisSize: MainAxisSize.min,
        children: [Text(widget.title, style: TextStyle(fontSize: 20),), SizedBox(width: 8.0,), spinner,],
      ),
    );
  }
}


class DatePickTile extends StatefulWidget {
  final Function onClicked;
  DatePickTile({@required this.onClicked,});
  @override
  _DatePickTileState createState() => _DatePickTileState();
}

class _DatePickTileState extends State<DatePickTile> {
  DateTime date;
  TextEditingController _textController = TextEditingController();

  @override
  void initState(){
    super.initState();
    date = DateTime.now();
    _textController.value = TextEditingValue(text: string);
  }
  @override void dispose(){
    _textController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        IconButton(icon: Icon(Icons.calendar_today), onPressed: ()=>datePicker(context),),
        SizedBox(width: 80, height:40, child: TextField(controller: _textController, readOnly: true, onTap: ()=>datePicker(context), )),
      ],
    );
  }

  Future<void> datePicker(BuildContext context) async {
    DateTime _dateTime = await showDatePicker(context: context, initialDate: date, firstDate: DateTime(1970,1,1,), lastDate: DateTime.now());
    if(_dateTime!=null) { date = _dateTime; _textController.value = TextEditingValue(text: string); }
    widget.onClicked(string);
  }
  
  twoDigits(n) =>(n>=10 ? "$n":"0$n");
  String get string => ('${date.year}-${twoDigits(date.month)}-${twoDigits(date.day)}');
}
