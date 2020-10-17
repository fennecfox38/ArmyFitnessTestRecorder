import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:army_fitness_test_recorder/group.dart';

class EventCount {
  final index;
  const EventCount._internal(this.index);

  static const MDL = const EventCount._internal(0); static const SPT = const EventCount._internal(1);
  static const HPU = const EventCount._internal(2); static const LTK = const EventCount._internal(3);
  static const PU = const EventCount._internal(4); static const SU = const EventCount._internal(5);
  static const values = const <EventCount> [MDL,SPT,HPU,LTK,PU,SU];

  static List<String> _titles = ['Maximum Dead Lift', 'Standing Power Throw', 'Hand-Release Push-Up', 'Leg Tucks', 'Push-Up', 'Sit-Up',];
  static List<String> _units = ['lbs', 'm', 'reps', 'reps', 'reps', 'reps', ]; static List<double> _increments = [1,0.1,1,1,1,1,];
  static List<double> _mins = [0,0,0,0,0,0,]; static List<double> _maxs = [340,15.0,100,40,100,100,];

  toString() => _titles[index];
  String get title => _titles[index]; String get unit => _units[index];
  double get increment => _increments[index];
  double get min => _mins[index]; double get max => _maxs[index];
}

// ignore: must_be_immutable
class CountEvent extends StatefulWidget {
  //final double initialValue;
  final GlobalKey key;
  final EventCount event;
  final Function onChanged;

  CountEvent({@required this.key, @required this.event, @required this.onChanged,});
  @override CountEventState createState() => CountEventState(key: key);
}

class CountEventState extends State<CountEvent> {
  final GlobalKey key;
  double value; int score; LevelPF levelPF; bool isPassed;
  FocusNode _textFieldFocus = new FocusNode();
  TextEditingController _controller = TextEditingController();

  CountEventState({@required this.key});

  @override void initState() {
    super.initState();
    _textFieldFocus.addListener((){
      if(!_textFieldFocus.hasFocus)
        try{ _setValue(double.parse(_controller.text)); }
      catch(e) { e.printStackTrace(); }
    });
    //setValue(widget.event.min);
    value=widget.event.min; score=0; levelPF=LevelPF.Fail; isPassed=false;
  }

  @override void dispose(){
    _controller.dispose();
    super.dispose();
  }

  @override Widget build(BuildContext context) {
    _controller.value = TextEditingValue( text: value.toStringAsFixed(widget.event.increment<1?1:0),);
    return Card( margin: const EdgeInsets.all(8.0), elevation: 2.0, child: Column( mainAxisSize: MainAxisSize.min, children: [
      Padding(
        padding: const EdgeInsets.only(top: 16.0, bottom: 4.0, left: 16.0, right: 16.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween, mainAxisSize: MainAxisSize.max,
          children: [
            Flexible(child: Text(widget.event.title, style: TextStyle(fontSize: 20),)),
            Text(levelPF.toString(), style: TextStyle(color: (isPassed? Colors.green:Colors.red)),),
          ],
        ),
      ),
      Padding(
        padding: const EdgeInsets.only(top: 4.0, bottom: 0.0, left: 16.0, right: 16.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.end, mainAxisSize: MainAxisSize.max,
          children: [
            IntrinsicWidth(child: TextField(
              controller: _controller, focusNode: _textFieldFocus,
              decoration: const InputDecoration(counterText: '',),
              keyboardType: TextInputType.number, textAlign: TextAlign.center, maxLength: 5,
            ),),
            SizedBox(width: 8,), Text(widget.event.unit), SizedBox(width: 8,),
            Text('$score', style: TextStyle(fontSize: 16),), Text(' points')
          ],
        ),
      ),
      Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween, mainAxisSize: MainAxisSize.max,
        children: [
          IconButton(icon: Icon(Icons.remove), onPressed: ()=>_setValue(value-=widget.event.increment), ),
          Expanded(child: Slider(value: value, min: widget.event.min, max: widget.event.max, onChanged: (_value)=>_setValue(_value),)),
          IconButton(icon: Icon(Icons.add), onPressed: ()=>_setValue(value+=widget.event.increment), ),
        ],
      ),
    ],),);
  }
  void _setValue(double _value){
    if(_value<=widget.event.min) _value=widget.event.min;
    else if(_value>=widget.event.max) _value=widget.event.max;
    else _value=(_value/widget.event.increment).round()*widget.event.increment;
    setState((){
      value=_value;
      Map _result = widget.onChanged(value);
      score = _result['score'];
      levelPF = _result['level'];
      isPassed =_result['isPassed'];
    });
  }
  void invalidate()=>_setValue(value);

}
