import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:army_fitness_test_recorder/duration.dart';
import 'package:army_fitness_test_recorder/group.dart';

class EventDuration {
  final index;
  const EventDuration._internal(this.index);

  static const SDC = const EventDuration._internal(0); static const CardioACFT = const EventDuration._internal(1);
  static const CardioAPFT = const EventDuration._internal(2);
  static const values = const <EventDuration> [SDC,CardioACFT,CardioAPFT];

  static List<String> _titles = ['Sprint-Drag-Carry', 'Cardio', 'Cardio',];
  static List<int> _mins = [0,0,0,]; static List<int> _maxs = [5,40,40,];

  toString() => _titles[index]; String get title => _titles[index];
  int get min => _mins[index]; int get max => _maxs[index];
}

// ignore: must_be_immutable
class DurationEvent extends StatefulWidget {
  final GlobalKey key;
  final EventDuration event;
  final Function onChanged;

  DurationEvent({@required this.key,@required this.event, @required this.onChanged});
  @override DurationEventState createState() => DurationEventState(key: key);
}

class DurationEventState extends State<DurationEvent> {
  final GlobalKey key;
  TextEditingController _controller = TextEditingController();
  Duration duration; var alter; int score; LevelPF levelPF; bool isPassed;

  DurationEventState({@required this.key});

  @override void initState() {
    super.initState();
    switch(widget.event){
      case EventDuration.SDC: alter = null; break;
      case EventDuration.CardioACFT: alter = AlterACFT.Run; break;
      case EventDuration.CardioAPFT: alter = AlterAPFT.Run; break;
    }
    //setValue(Duration(min: widget.event.min, sec: 0));
    duration=Duration(min: widget.event.max, sec: 0);
    score=0; levelPF=LevelPF.Fail; isPassed=false;
  }

  @override void dispose(){
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    _controller.value = TextEditingValue(text: duration.toString());
    return Card(
      margin: const EdgeInsets.all(8.0),
      elevation: 2.0,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween, mainAxisSize: MainAxisSize.max,
              children: [Text(widget.event.title, style: TextStyle(fontSize: 20),), Text(levelPF.toString(), style: TextStyle(color: (isPassed? Colors.green:Colors.red)),),],
            ),
            SizedBox(height: 8,),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween, mainAxisSize: MainAxisSize.max,
              children: [
                (alter!=null ? Spinner(values: (widget.event==EventDuration.CardioACFT ? AlterACFT.values : AlterAPFT.values), onChanged: (e)=>setState((){alter=e; _setValue(duration);}),) : SizedBox() ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.end, mainAxisSize: MainAxisSize.min,
                  children: [
                    Container( width: 70, height: 30, child: TextField( readOnly: true,
                      controller: _controller, textAlign: TextAlign.center,
                      decoration: const InputDecoration(border: OutlineInputBorder(),),
                      onTap: () async => await showDialog(
                          context: context, builder: (context)=>DurationPicker(duration, min: widget.event.min, max: widget.event.max,)
                      ).then( (_value) { if(_value!=null) _setValue(_value); } ),
                    ),),
                    SizedBox(width: 8,), Text('$score points', style: TextStyle(fontSize: 16),),
                  ],
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void _setValue(Duration _duration) => setState( (){
    duration=_duration;
    Map _result = widget.onChanged(duration,alter);
    score = _result['score'];
    levelPF = _result['level'];
    isPassed =_result['isPassed'];
  });
  void invalidate()=>_setValue(duration);

}
