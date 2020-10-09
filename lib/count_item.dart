import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';

class Item {
  final index;
  const Item._internal(this.index);

  static const Height = const Item._internal(0); static const Weight = const Item._internal(1);
  static const Neck = const Item._internal(2); static const Abdomen = const Item._internal(3);
  static const Waist = const Item._internal(4); static const Hips = const Item._internal(5);
  static const values = const <Item> [Height,Weight,Neck,Abdomen,Waist,Hips];

  static List<String> _titles = ['Height', 'Weight', 'Neck', 'Abdomen', 'Waist', 'Hips',];
  static List<String> _units = ['inches', 'lbs', 'inches', 'inches', 'inches', 'inches',];
  static List<double> _mins = [58.0, 90, 10.0, 20.0, 20.0, 20.0,];
  static List<double> _maxs = [97.0, 310, 40.0, 60.0, 60.0, 60.0,];
  static List<double> _increments = [0.5, 1, 0.5, 0.5, 0.5, 0.5,];

  toString() => _titles[index];
  String get title => _titles[index]; String get unit => _units[index];
  double get increment => _increments[index];
  double get min => _mins[index]; double get max => _maxs[index];
}

class CountItem extends StatefulWidget {
  final Item item;
  final double initialValue;
  final Function onChanged;

  CountItem({@required this.item, @required this.onChanged, this.initialValue}); //

  @override
  _CountItemState createState() => _CountItemState();
}

class _CountItemState extends State<CountItem> {
  double value;
  FocusNode _textFieldFocus = new FocusNode();
  TextEditingController _controller = TextEditingController();

  @override void initState() {
    super.initState();
    _textFieldFocus.addListener((){
      if(!_textFieldFocus.hasFocus)
        try{ _setValue(double.parse(_controller.text)); }
        catch(e){ e.printStackTrace(); }
    });
    value = (widget.initialValue!=null ? widget.initialValue : widget.item.min);
  }

  @override void dispose(){
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    _controller.value = TextEditingValue( text: value.toStringAsFixed(widget.item.increment<1?1:0),);// selection: TextSelection.fromPosition(TextPosition(offset: '$value'.length)),);
    return Card(
      margin: const EdgeInsets.all(8.0),
      elevation: 2.0,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Padding(
            padding: const EdgeInsets.only(top: 16, bottom: 8, left: 16, right: 16,),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween, mainAxisSize: MainAxisSize.max,
              children: [
                Text(widget.item.title, style: TextStyle(fontSize: 20),),
                Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container( width: 60, height: 30, child: TextField(
                      controller: _controller, focusNode: _textFieldFocus,
                      decoration: const InputDecoration(counterText: '',border: OutlineInputBorder(),),
                      keyboardType: TextInputType.number, textAlign: TextAlign.center, maxLength: 5,
                    ), ),
                    SizedBox(width: 8,), Text(widget.item.unit),
                  ],
                ),
              ],
            ),
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly, mainAxisSize: MainAxisSize.max,
            children: [
              IconButton(icon: Icon(Icons.remove), onPressed: ()=>_setValue(value-=widget.item.increment),),
              Expanded(child: Slider(value: value, min: widget.item.min, max: widget.item.max, onChanged: (_value)=>_setValue(_value),)),
              IconButton(icon: Icon(Icons.add), onPressed: ()=>_setValue(value+=widget.item.increment),),
            ],
          ),
        ],
      ),
    );
  }
  void _setValue(double _value){
    if(_value<=widget.item.min) _value=widget.item.min;
    else if(_value>=widget.item.max) _value=widget.item.max;
    else _value=(_value/widget.item.increment).round()*widget.item.increment;
    setState(()=> value=_value );
    widget.onChanged(value);
  }
}
