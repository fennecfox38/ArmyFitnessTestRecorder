import 'package:excel/excel.dart';
import 'package:flutter/material.dart';
import 'package:share/share.dart';
import 'package:army_fitness_test_recorder/duration.dart';
import 'package:army_fitness_test_recorder/group.dart';

class APFTRecord{
  double valuePU=0, valueSU=0; Duration valueCardio=Duration.inSec(2400); AlterAPFT alter=AlterAPFT.Run;
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

  List<dynamic> get values {
    return [
      date, sex.toString(), age.toString(),
      valuePU,score[0], valueSU,score[1], valueCardio.toString(),score[2],
      alter.toString(), totalScore, isPassed.toString(),
    ];
  }
  String print() {
    return "Record Date: " + date + "\nSex: " + sex.toString() + "\nAge: " + age.toString() +
        "\nPush-Up: ${valuePU.toInt()} reps / score: ${score[0]}"+
        "\nSit-Up: ${valueSU.toInt()} reps / score: ${score[1]}"+
        "\n"+ alter.toString() +": " + valueCardio.toString() + " / score: ${score[2]}"+
        "\nScore Total: $totalScore" +
        "\nPassed : " + isPassed.toString();
  }
  APFTLogCard get logCard =>APFTLogCard(this);
}

class APFTDBHelper extends DBHelper{
  Future<void> insertRecord(APFTRecord record, {BuildContext context}) async {
    final db = await database;
    var res = await db.rawInsert(sqlInsert, record.values);
    DBHelper.keyLogPage.currentState.invalidateAPFT();
    if(context!=null)
      Scaffold.of(context).showSnackBar(SnackBar(content: Text('Record has been saved to DB.'),
        action: SnackBarAction(label: 'Undo', onPressed: ()=>deleteRecord(record, context: context),), ));
    return res;
  }

  Future<void> saveList(List<APFTRecord> list, {BuildContext context}) async {
    final db = await database;
    list.forEach((e) => db.rawInsert(sqlInsert, e.values));
    DBHelper.keyLogPage.currentState.invalidateAPFT();
  }

  Future<List<APFTRecord>> getRecordList() async {
    final db = await database;
    var res = await db.rawQuery(sqlSelect);
    List<APFTRecord> list = (res.isNotEmpty ? res.map((e){
      APFTRecord record = APFTRecord();
      record.date = e[columnRecordDate];
      record.sex = Sex.fromString(e[columnSex]);
      record.age = AgeAPFT.fromString(e[columnAge]);
      record.valuePU = (e[columnRawPU] as int).toDouble();
      record.valueSU = (e[columnRawSU] as int).toDouble();
      record.valueCardio.fromString( e[columnRawCardio] );
      record.score[0] = e[columnScorePU]; record.score[1] = e[columnScoreSU];
      record.score[2] = e[columnScoreCardio];
      record.levelPF[0] = LevelPF.fromScore(record.score[0], true);
      record.levelPF[1] = LevelPF.fromScore(record.score[1], true);
      record.levelPF[2] = LevelPF.fromScore(record.score[2], true);
      record.alter = AlterAPFT.fromString(e[columnCardioAlter]);
      record.isPassed = (e[columnIsPassed] == true.toString());
      return record;
    }).toList() : []);

    return list;
  }

  Future<void> deleteRecord(APFTRecord record, {BuildContext context}) async {
    String sqlExec = sqlDeleteWhere + sqlWhereString(columnRecordDate,record.date) + "AND ";
    sqlExec += sqlWhereString(columnSex,record.sex.toString()) + "AND ";
    sqlExec += sqlWhereString(columnAge,record.age.toString()) + "AND ";
    sqlExec += sqlWhereDouble(columnRawPU,record.valuePU) + "AND " + sqlWhereDouble(columnRawSU,record.valueSU) + "AND ";
    sqlExec += sqlWhereString(columnRawCardio,record.valueCardio.toString()) + "AND ";
    sqlExec += sqlWhereInt(columnScorePU,record.score[0]) + "AND " + sqlWhereInt(columnScoreSU,record.score[1]) + "AND ";
    sqlExec += sqlWhereInt(columnScoreCardio,record.score[2]) + "AND ";
    sqlExec += sqlWhereString(columnCardioAlter,record.alter.toString()) + "AND ";
    sqlExec += sqlWhereInt(columnScoreTotal,record.totalScore) + "AND ";
    sqlExec += sqlWhereString(columnIsPassed, record.isPassed.toString());

    final db = await database;
    var res = db.execute(sqlExec);
    DBHelper.keyLogPage.currentState.invalidateAPFT();
    if(context!=null)
      Scaffold.of(context).showSnackBar(SnackBar(content: Text('APFT Records have been deleted from DB.'),
        action: SnackBarAction(label: 'Undo', onPressed: ()=>insertRecord(record, context: context),),
      ));
    return res;
  }

  Future<void> deleteAllRecord({BuildContext context}) async {
    List<APFTRecord> _backup = await getRecordList();
    final db = await database;
    db.rawDelete(sqlDeleteAll);
    DBHelper.keyLogPage.currentState.invalidateAPFT();
    if(context!=null)
      Scaffold.of(context).showSnackBar(SnackBar(content: Text('Record has been deleted from DB.'),
        action: SnackBarAction(label: 'Undo', onPressed: ()=>saveList(_backup)),
      ));
  }

  Future<Excel> exportSheet(Excel excel) async {
    Sheet _sheet = excel['APFTRecord'];
    List<APFTRecord> _list = await getRecordList();
    int rowIndex = 0;
    _sheet.insertRowIterables(columnNames, rowIndex++);
    for(APFTRecord _record in _list)  // will be single record. (single row)
      _sheet.insertRowIterables(_record.values, rowIndex++);
    return excel;
  }

  static const String tableName = "APFTRecord";
  static const String columnRecordDate = "RecordDate", columnSex = "Sex", columnAge = "AgeGroup";
  static const String columnRawPU = "PURaw", columnRawSU = "SURaw", columnRawCardio = "CardioRaw";
  static const String columnScorePU = "PUScore", columnScoreSU = "SUScore", columnScoreCardio = "CardioScore";
  static const String columnCardioAlter = "CardioAlter", columnScoreTotal = "ScoreTotal", columnIsPassed = "isPassed";

  static const List<String> columnNames = <String>[
    columnRecordDate,columnSex,columnAge,
    columnRawPU,columnScorePU, columnRawSU,columnScoreSU,
    columnRawCardio,columnScoreCardio,columnCardioAlter,
    columnScoreTotal,columnIsPassed,
  ];

  static const String sqlCreateTable="CREATE TABLE IF NOT EXISTS "+ tableName +" ("+
      columnRecordDate+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+ columnSex+" TEXT NOT NULL,"+ columnAge+" TEXT NOT NULL,"+
      columnRawPU+" INTEGER NOT NULL,"+ columnScorePU+" INTEGER NOT NULL,"+
      columnRawSU+" INTEGER NOT NULL,"+ columnScoreSU+" INTEGER NOT NULL,"+
      columnRawCardio+" TEXT NOT NULL,"+ columnScoreCardio+" INTEGER NOT NULL,"+
      columnCardioAlter+" TEXT NOT NULL,"+ columnScoreTotal+" INTEGER NOT NULL,"+
      columnIsPassed+" TEXT NOT NULL)";
  static const String sqlDropTable = "DROP TABLE IF EXISTS "+ tableName;
  static const String sqlSelect = "SELECT * FROM " + tableName;
  static const String sqlInsert = "INSERT OR REPLACE INTO "+tableName+
      "("+columnRecordDate+", "+columnSex+", "+columnAge+", "+
      columnRawPU+", "+columnScorePU+", "+ columnRawSU+", "+columnScoreSU+", "+
      columnRawCardio+", "+columnScoreCardio+", "+ columnCardioAlter+", "+
      columnScoreTotal+", "+ columnIsPassed+") VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
  static const String sqlDeleteWhere = "DELETE FROM " + tableName + " WHERE ";
  static const String sqlDeleteAll = "DELETE FROM " + tableName;
}

String sqlWhereString(String _column, String _arg){ return (_column+"=\""+_arg+"\" "); }
String sqlWhereInt(String _column, int _arg){ return (_column+"=$_arg "); }
String sqlWhereDouble(String _column, double _arg){ return ("abs("+_column+"-$_arg)<0.1 "); }
double preciseDouble(double _obj){ return ((_obj*10).roundToDouble()/10.0); }

class APFTLogCard extends StatelessWidget {
  final APFTRecord record;
  APFTLogCard(this.record);

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
        case 2: APFTDBHelper().deleteRecord(record, context: context); break;
        default: break;
      }});
    }

    return GestureDetector(
      onTapDown: (TapDownDetails details)=>_tapPosition = details.globalPosition,
      onLongPress: ()=>_showPopupMenu(),
      child: Card(
        margin: const EdgeInsets.all(8.0),
        elevation: 2.0,
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Table(
            children: [
              TableRow(children: [
                TableCell(child: Text(record.date)),
                TableCell(child: Text(record.sex.toString(), textAlign: TextAlign.center,)),
                TableCell(child: Text(record.age.toString(), textAlign: TextAlign.center,)),
                TableCell(child: Text('${record.totalScore} point', textAlign: TextAlign.end,
                  style: TextStyle(color: (record.isPassed ? Colors.green : Colors.red) ),)),
              ]),
              TableRow( children: [
                TableCell(child: Text('Push-Up')),
                TableCell(child: Text('${record.valuePU.toInt()} reps', textAlign: TextAlign.center,),),
                TableCell(child: Text('${record.score[0]} point', textAlign: TextAlign.end,)),
                TableCell(child: Text('${record.levelPF[0].toString()}', textAlign: TextAlign.center,
                  style: TextStyle(color: (record.levelPF[0]==LevelPF.Pass ? Colors.green : Colors.red) ),),),
              ]),
              TableRow( children: [
                TableCell(child: Text('Sit-Up')),
                TableCell(child: Text('${record.valueSU.toInt()} reps', textAlign: TextAlign.center,),),
                TableCell(child: Text('${record.score[1]} point', textAlign: TextAlign.end,)),
                TableCell(child: Text('${record.levelPF[1].toString()}', textAlign: TextAlign.center,
                  style: TextStyle(color: (record.levelPF[0]==LevelPF.Pass ? Colors.green : Colors.red) ),),),
              ]),
              TableRow( children: [
                TableCell(child: Text(record.alter.toString())),
                TableCell(child: Text(record.valueCardio.toString(), textAlign: TextAlign.center,),),
                TableCell(child: Text('${record.score[2]} point', textAlign: TextAlign.end,)),
                TableCell(child: Text('${record.levelPF[2].toString()}', textAlign: TextAlign.center,
                  style: TextStyle(color: (record.levelPF[0]==LevelPF.Pass ? Colors.green : Colors.red) ),),),
              ]),
            ],
          ),
        ),
      ),
    );
  }
}

///*****************************************************************************************
