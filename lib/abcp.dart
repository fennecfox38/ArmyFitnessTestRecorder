import 'package:excel/excel.dart';
import 'package:flutter/material.dart';
import 'package:share/share.dart';
import 'package:army_fitness_test_recorder/group.dart';

class ABCPRecord{
  double height=58.0, weight=90, neck=10.0, abdomen=20.0, waist=20.0, hips=20.0;
  Sex sex = Sex.Male; AgeABCP age = AgeABCP.age1720;
  bool hwPass = false, bodyFatPass=false, isPassed=false;
  double bodyFatPercent = 0;
  String date = '2020-09-29';

  List<dynamic> get values {
    return [
      date, sex.toString(), age.toString(),
      height, weight, hwPass.toString(), (hwPass? null:neck), (hwPass? null:(sex==Sex.Male ? abdomen : waist)), (hwPass? null:(sex==Sex.Male ? null : hips)),
      (hwPass? null : bodyFatPercent), (hwPass? 'N/A' : bodyFatPass.toString()), isPassed.toString(),
    ];
  }
  String print() {
    return "Record Date: " + date + "\nSex: " + sex.toString() + "\nAge: " + age.toString() +
        "\nHeight: $height inches / Weight: $weight lbs / HeightWeight: ${hwPass? 'Pass':'Fail'}"+
        "\nNeck: $neck inches / ${(sex==Sex.Male ? 'Abdomen' : 'Waist')}: ${(sex==Sex.Male ? abdomen : waist)} inches"+
        (sex==Sex.Male ? '' : ' / Hips: $hips')+
        "\nBodyFat: $bodyFatPercent % / ${bodyFatPass? 'Pass':'Fail'}"+
        "\nPassed : " + isPassed.toString();
  }
  ABCPLogCard get logCard =>ABCPLogCard(this);
}


class ABCPDBHelper extends DBHelper{
  Future<void> insertRecord(ABCPRecord record,{BuildContext context}) async {
    final db = await database;
    var res = await db.rawInsert(sqlInsert, record.values);
    DBHelper.keyLogPage.currentState.invalidateABCP();
    if(context!=null)
      Scaffold.of(context).showSnackBar(SnackBar(content: Text('Record has been saved to DB.'),
        action: SnackBarAction(label: 'Undo', onPressed: ()=>deleteRecord(record, context: context),),));
    return res;
  }

  Future<void> saveList(List<ABCPRecord> list, {BuildContext context}) async {
    final db = await database;
    list.forEach((e) => db.rawInsert(sqlInsert, e.values));
    DBHelper.keyLogPage.currentState.invalidateABCP();
  }

  Future<List<ABCPRecord>> getRecordList() async {
    final db = await database;
    var res = await db.rawQuery(sqlSelect);
    List<ABCPRecord> list = (res.isNotEmpty ? res.map((e){
      ABCPRecord record = ABCPRecord();
      record.date = e[columnRecordDate];
      record.sex = Sex.fromString(e[columnSex]);
      record.age = AgeABCP.fromString(e[columnAge]);
      record.height = e[columnHeight];
      record.weight = (e[columnWeight] as int).toDouble();
      record.neck = e[columnNeck];
      if(record.sex==Sex.Male){
        record.abdomen = e[columnAbdomenWaist];
      } else{
        record.waist = e[columnAbdomenWaist];
        record.hips = e[columnHips];
      }
      record.hwPass = (e[columnHWPass] == true.toString());
      record.bodyFatPass = (e[columnBodyFatPass] == true.toString());
      record.bodyFatPercent = preciseDouble(e[columnBodyFatPercent]);
      record.isPassed = (e[columnIsPassed] == true.toString());
      return record;
    }).toList() : []);

    return list;
  }

  Future<void> deleteRecord(ABCPRecord record, {BuildContext context}) async {
    String sqlExec = sqlDeleteWhere + sqlWhereString(columnRecordDate,record.date) + "AND ";
    sqlExec += sqlWhereString(columnSex,record.sex.toString()) + "AND ";
    sqlExec += sqlWhereString(columnAge,record.age.toString()) + "AND ";
    sqlExec += sqlWhereDouble(columnHeight,record.height) + "AND " + sqlWhereDouble(columnWeight,record.weight) + "AND ";
    if(record.hwPass){
      sqlExec += sqlWhereDouble(columnNeck,record.neck) + "AND " + sqlWhereDouble(columnAbdomenWaist,(record.sex==Sex.Male? record.abdomen:record.waist)) + "AND ";
      if(record.sex==Sex.Female) sqlExec += sqlWhereDouble(columnHips,record.hips) + "AND ";
      sqlExec += sqlWhereDouble(columnBodyFatPercent,record.bodyFatPercent) + "AND ";
    }
    sqlExec += sqlWhereString(columnIsPassed, record.isPassed.toString());

    final db = await database;
    var res = db.execute(sqlExec);
    DBHelper.keyLogPage.currentState.invalidateABCP();
    if(context!=null)
      Scaffold.of(context).showSnackBar(SnackBar(content: Text('ABCP Records have been deleted from DB.'),
        action: SnackBarAction(label: 'Undo', onPressed: ()=>insertRecord(record, context: context),),
      ));
    return res;
  }

  Future<void> deleteAllRecord({BuildContext context}) async {
    List<ABCPRecord> _backup = await getRecordList();
    final db = await database;
    db.rawDelete(sqlDeleteAll);
    DBHelper.keyLogPage.currentState.invalidateABCP();
    if(context!=null)
      Scaffold.of(context).showSnackBar(SnackBar(content: Text('Record has been deleted from DB.'),
        action: SnackBarAction(label: 'Undo', onPressed: ()=>saveList(_backup),),
      ));
  }

  Future<Excel> exportSheet(Excel excel) async {
    Sheet _sheet = excel['ABCPRecord'];
    List<ABCPRecord> _list = await getRecordList();
    int rowIndex = 0;
    _sheet.insertRowIterables(columnNames, rowIndex++);
    for(ABCPRecord _record in _list)  // will be single record. (single row)
      _sheet.insertRowIterables(_record.values, rowIndex++);
    return excel;
  }

  static const String tableName = "ABCPRecord";
  static const String columnRecordDate = "RecordDate", columnSex = "Sex", columnAge = "AgeGroup";
  static const String columnHeight = "Height", columnWeight = "Weight", columnNeck = "Neck";
  static const String columnAbdomenWaist = "AbdomenWaist", columnHips = "Hips", columnHWPass = "HWPass";
  static const String columnBodyFatPass = "BodyFatPass", columnBodyFatPercent = "BodyFatPercent", columnIsPassed = "isPassed";

  static const List<String> columnNames = <String>[
    columnRecordDate,columnSex,columnAge,
    columnHeight,columnWeight, columnHWPass,columnNeck,
    columnAbdomenWaist,columnHips,columnBodyFatPercent,
    columnBodyFatPass,columnIsPassed,
  ];

  static const String sqlCreateTable="CREATE TABLE IF NOT EXISTS "+ tableName +" ("+
      columnRecordDate+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+ columnSex+" TEXT NOT NULL,"+ columnAge+" TEXT NOT NULL,"+
      columnHeight+" FLOAT NOT NULL,"+ columnWeight+" INTEGER NOT NULL,"+ columnHWPass+" TEXT NOT NULL,"+
      columnNeck+" FLOAT,"+ columnAbdomenWaist+" FLOAT,"+ columnHips+" FLOAT,"+
      columnBodyFatPercent+" FLOAT NOT NULL,"+ columnBodyFatPass+" TEXT NOT NULL,"+ columnIsPassed+" TEXT NOT NULL)";
  static const String sqlDropTable = "DROP TABLE IF EXISTS "+ tableName;
  static const String sqlSelect = "SELECT * FROM " + tableName;
  static const String sqlInsert = "INSERT OR REPLACE INTO "+tableName+
      "("+columnRecordDate+", "+columnSex+", "+columnAge+", "+
      columnHeight+", "+columnWeight+", "+ columnHWPass+", "+columnNeck+", "+
      columnAbdomenWaist+", "+columnHips+", "+ columnBodyFatPercent+", "+
      columnBodyFatPass+", "+ columnIsPassed+") VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
  static const String sqlDeleteWhere = "DELETE FROM " + tableName + " WHERE ";
  static const String sqlDeleteAll = "DELETE FROM " + tableName;
}

String sqlWhereString(String _column, String _arg){ return (_column+"=\""+_arg+"\" "); }
String sqlWhereDouble(String _column, double _arg){ return ("abs("+_column+"-$_arg)<0.1 "); }
double preciseDouble(double _obj){ return ((_obj*10).roundToDouble()/10.0); }

class ABCPLogCard extends StatelessWidget {
  final ABCPRecord record;
  ABCPLogCard(this.record);

  @override Widget build(BuildContext context) {
    Offset _tapPosition;
    Future<void> _showPopupMenu() async {
      final RenderBox overlay = Overlay.of(context).context.findRenderObject();
      await showMenu( context: context, elevation: 8.0, initialValue: 0,
        position: RelativeRect.fromRect(_tapPosition & Size(40, 40), Offset.zero & overlay.size,), //(smaller rect(the touch area),  Bigger rect(the entire screen))
        items: [
          PopupMenuItem(value: 1, child: Row(children: <Widget>[Icon(Icons.share), SizedBox(width: 8,), Text('Share'),],),),
          PopupMenuItem(value: 2, child: Row(children: <Widget>[Icon(Icons.delete), SizedBox(width: 8,), Text('Delete'),],),),
        ],
      ).then((value){ switch(value){
        case 1: Share.share(record.print()); break;
        case 2: ABCPDBHelper().deleteRecord(record, context: context); break;
        default: break;
      }});
    }

    Table _table = Table(
      children: [
        TableRow(children: [
          TableCell(child: Text(record.date)),
          TableCell(child: Text(record.sex.toString(), textAlign: TextAlign.center,)),
          TableCell(child: Text(record.age.toString(), textAlign: TextAlign.center,)),
          TableCell(child: getPFText(record.isPassed), ),
        ]),
      ],
    );
    if(record.hwPass) _table.children.add(TableRow( children: [
      TableCell(child: Column(children: [Text('Height'), Text('${record.height} inches')],)),
      TableCell(child: Column(children: [Text('Weight'), Text('${record.weight.toInt()} lbs')],)),
      TableCell(child: Column(children: [Text('H/Weight'), textPass,],)),
      TableCell(child: Column(children: [Text('BodyFat'), Text('N/A'),],)),
    ]));
    else {
      _table.children.add(TableRow( children: [
        TableCell(child: Column(children: [Text('Height'), Text('${record.height} inches')],)),
        TableCell(child: Column(children: [Text('Weight'), Text('${record.weight.toInt()} lbs')],)),
        TableCell(child: Column(children: [Text('H/Weight'), textFail,],)),
        TableCell(child: Column(children: [Text('BodyFat'), getPFText(record.bodyFatPass),],)),
      ]));
      _table.children.add(TableRow( children: [
        TableCell(child: Column(children: [Text('Neck'), Text('${record.neck} inches')],)),
        TableCell(child: Column(children: [Text(record.sex.equal(Sex.Male)?'Abdomen':'Waist'),
          Text('${record.sex.equal(Sex.Male)?record.abdomen:record.waist} inches')],),),
        TableCell(child: Column(children: [Text('Hips'), Text(record.sex.equal(Sex.Male) ? 'N/A' : '${record.hips} inches'),],)),
        TableCell(child: Column(children: [Text('BodyFat %'), Text('${record.bodyFatPercent}%'),],)),
      ]));
    }

    return GestureDetector(
      onTapDown: (TapDownDetails details)=>_tapPosition = details.globalPosition,
      onLongPress: ()=>_showPopupMenu(),
      child: Card(
        margin: const EdgeInsets.all(8.0),
        elevation: 2.0,
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: _table,
        ),
      ),
    );
  }
}


///*****************************************************************************************