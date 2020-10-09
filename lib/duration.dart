import 'package:flutter/material.dart';
import 'package:numberpicker/numberpicker.dart';

class Duration{
  int inSec;
  Duration.inSec(this.inSec);
  Duration({min: 0, sec: 0}){
    if(min<0 || sec<0) inSec=0;
    else if(min!=0 && sec>=60) inSec=0;
    else inSec = min*60+sec;
  }
  int get min => (inSec ~/ 60);
  int get sec => (inSec % 60);
  set min(int _min) => inSec = _min*60+sec;
  set sec(int _sec) => inSec = min*60+_sec;

  @override String toString() {
    twoDigits(n) =>(n>=10 ? "$n":"0$n");
    return '${twoDigits(min)}:${twoDigits(sec)}';
  }
  void fromString(String _str){
    List<String> _list = _str.split(":");
    try{
      min = int.parse(_list[0]);
      sec = int.parse(_list[1]);
    }catch (e){ e.printStackTrace(); }
  }

  Duration operator +(Duration other){ return Duration(sec: (inSec+other.inSec)); }
  Duration operator -(Duration other){ return Duration(sec: (inSec-other.inSec)); }

  bool operator ==(other){ return (other is Duration && inSec == other.inSec); }
  bool operator <(Duration other){ return (inSec < other.inSec); }
  bool operator <=(Duration other){ return (inSec <= other.inSec); }
  bool operator >(Duration other){ return (inSec > other.inSec); }
  bool operator >=(Duration other){ return (inSec >= other.inSec); }

  @override int get hashCode => hashValues(min, sec);
}

class DurationPicker extends StatefulWidget {
  final Duration _duration;
  final int min, max;
  DurationPicker(this._duration,{this.min:0, this.max: 59});
  @override
  _DurationPickerState createState() => _DurationPickerState(_duration,);
}

class _DurationPickerState extends State<DurationPicker> {
  Duration _duration;
  _DurationPickerState(this._duration,);
  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text("Pick the event duration"),
      content: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          NumberPicker.integer(initialValue: _duration.min, minValue: widget.min, maxValue: widget.max, onChanged: (_value)=>setState(()=>_duration.min=_value), infiniteLoop: true, textMapper: (str)=>(str.length==1 ? '0': '')+str,),
          Text(' : ', textScaleFactor: 1.5,),
          NumberPicker.integer(initialValue: _duration.sec, minValue: 0, maxValue: 59, onChanged: (_value)=>setState(()=>_duration.sec=_value), infiniteLoop: true, textMapper: (str)=>(str.length==1 ? '0': '')+str,),
        ],
      ),
      actions: [
        FlatButton(onPressed: () { Navigator.pop(context,null); }, child: Text('Cancel', style: TextStyle(color: Colors.orangeAccent),) ),
        FlatButton(onPressed: () { Navigator.pop(context,_duration); }, child: Text('OK', style: TextStyle(color: Colors.orangeAccent),) ),
      ],
    );
  }
}




