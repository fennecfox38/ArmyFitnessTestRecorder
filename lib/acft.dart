import 'package:excel/excel.dart';
import 'package:flutter/material.dart';
import 'package:share/share.dart';
import 'package:army_fitness_test_recorder/duration.dart';
import 'package:army_fitness_test_recorder/group.dart';

class ACFTRecord{
  double valueMDL=0, valueSPT=0, valueHPU=0, valueLTK=0;
  Duration valueSDC=Duration.inSec(300), valueCardio=Duration.inSec(2400);
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

  List<dynamic> get values {
    return [
      date, valueMDL,score[0], valueSPT,score[1], valueHPU,score[2],
      valueSDC.toString(),score[3],valueLTK,score[4],valueCardio.toString(),score[5],
      alter.toString(), qualified.toString(), totalScore, mos.toString(), isPassed.toString(),
    ];
  }
  String print() {
    return "Record Date: " + date + "\nMOS Requirement: " + mos.toString() +
        "\nMDL: ${valueMDL.toInt()} lbs / score: ${score[0]}"+
        "\nSPT: $valueSPT m / score: ${score[1]}"+
        "\nHPU: ${valueHPU.toInt()} reps / score: ${score[2]}"+
        "\nSDC: " + valueSDC.toString() + " / score: ${score[3]}"+
        "\nLTK: ${valueLTK.toInt()} reps / score: ${score[4]}"+
        "\n"+ alter.toString() +": " + valueCardio.toString() + " / score: ${score[5]}"+
        "\nQualified: " + qualified.toString() +
        "\nScore Total: $totalScore" +
        "\nPassed : " + isPassed.toString();
  }
  ACFTLogCard get logCard =>ACFTLogCard(this);

}

class ACFTDBHelper extends DBHelper{
  Future<void> insertRecord(ACFTRecord record, {BuildContext context}) async {
    final db = await database;
    var res = await db.rawInsert(sqlInsert, record.values);
    DBHelper.keyLogPage.currentState.invalidateACFT();
    if(context!=null)
      Scaffold.of(context).showSnackBar(SnackBar(content: Text('Record has been saved to DB.'),
        action: SnackBarAction(label: 'Undo', onPressed: ()=>deleteRecord(record, context: context),),));
    return res;
  }

  Future<void> saveList(List<ACFTRecord> list, {BuildContext context}) async {
    final db = await database;
    list.forEach((e) => db.rawInsert(sqlInsert, e.values));
    DBHelper.keyLogPage.currentState.invalidateACFT();
  }

  Future<List<ACFTRecord>> getRecordList() async {
    final db = await database;
    var res = await db.rawQuery(sqlSelect);
    List<ACFTRecord> list = (res.isNotEmpty ? res.map((e){
      ACFTRecord record = ACFTRecord();
      record.date = e[columnRecordDate];
      record.valueMDL = (e[columnRawMDL] as int).toDouble();
      record.valueSPT = preciseDouble(e[columnRawSPT]);
      record.valueHPU = (e[columnRawHPU] as int).toDouble();
      record.valueSDC.fromString( e[columnRawSDC] );
      record.valueLTK = (e[columnRawLTK] as int).toDouble();
      record.valueCardio.fromString( e[columnRawCardio] );
      record.score[0] = e[columnScoreMDL]; record.score[1] = e[columnScoreSPT];
      record.score[2] = e[columnScoreHPU]; record.score[3] = e[columnScoreSDC];
      record.score[4] = e[columnScoreLTK]; record.score[5] = e[columnScoreCardio];
      record.alter = AlterACFT.fromString(e[columnCardioAlter]);
      record.levelPF[0] = LevelPF.fromScore(record.score[0], false);
      record.levelPF[1] = LevelPF.fromScore(record.score[1], false);
      record.levelPF[2] = LevelPF.fromScore(record.score[2], false);
      record.levelPF[3] = LevelPF.fromScore(record.score[3], false);
      record.levelPF[4] = LevelPF.fromScore(record.score[4], false);
      record.levelPF[5] = LevelPF.fromScore(record.score[5], !record.alter.equal(AlterACFT.Run));
      record.qualified = LevelPF.fromString(e[columnQualifiedLevel]);
      record.mos = MOSLevel.fromString(e[columnMOSRequirement]);
      record.isPassed = (e[columnIsPassed] == true.toString());
      return record;
    }).toList() : []);

    return list;
  }

  Future<void> deleteRecord(ACFTRecord record, {BuildContext context}) async {
    String sqlExec = sqlDeleteWhere + sqlWhereString(columnRecordDate,record.date) + "AND ";
    sqlExec += sqlWhereDouble(columnRawMDL,record.valueMDL) + "AND " + sqlWhereDouble(columnRawSPT,record.valueSPT) + "AND ";
    sqlExec += sqlWhereDouble(columnRawHPU,record.valueHPU) + "AND " + sqlWhereString(columnRawSDC,record.valueSDC.toString()) + "AND ";
    sqlExec += sqlWhereDouble(columnRawLTK,record.valueLTK) + "AND " + sqlWhereString(columnRawCardio,record.valueCardio.toString()) + "AND ";
    sqlExec += sqlWhereInt(columnScoreMDL,record.score[0]) + "AND " + sqlWhereInt(columnScoreSPT,record.score[1]) + "AND ";
    sqlExec += sqlWhereInt(columnScoreHPU,record.score[2]) + "AND " + sqlWhereInt(columnScoreSDC,record.score[3]) + "AND ";
    sqlExec += sqlWhereInt(columnScoreLTK,record.score[4]) + "AND " + sqlWhereInt(columnScoreCardio,record.score[5]) + "AND ";
    sqlExec += sqlWhereString(columnCardioAlter,record.alter.toString()) + "AND ";
    sqlExec += sqlWhereString(columnQualifiedLevel,record.qualified.toString()) + "AND ";
    sqlExec += sqlWhereInt(columnScoreTotal,record.totalScore) + "AND " + sqlWhereString(columnMOSRequirement,record.mos.toString()) + "AND ";
    sqlExec += sqlWhereString(columnIsPassed, record.isPassed.toString());

    final db = await database;
    var res = db.execute(sqlExec);
    DBHelper.keyLogPage.currentState.invalidateACFT();
    if(context!=null)
      Scaffold.of(context).showSnackBar(SnackBar(content: Text('Record has been deleted from DB.'),
        action: SnackBarAction(label: 'Undo', onPressed: ()=>insertRecord(record, context: context),),));
    return res;
  }

  Future<void> deleteAllRecord({BuildContext context}) async {
    List<ACFTRecord> _backup = await getRecordList();
    final db = await database;
    db.rawDelete(sqlDeleteAll);
    DBHelper.keyLogPage.currentState.invalidateACFT();
    if(context!=null)
      Scaffold.of(context).showSnackBar(SnackBar(content: Text('ACFT Records have been deleted from DB.'),
        action: SnackBarAction(label: 'Undo', onPressed: ()=>saveList(_backup)),
      ));
  }

  Future<Excel> exportSheet(Excel excel) async {
    Sheet _sheet = excel['ACFTRecord'];
    List<ACFTRecord> _list = await getRecordList();
    int rowIndex = 0;
    _sheet.insertRowIterables(columnNames, rowIndex++);
    for(ACFTRecord _record in _list)  // will be single record. (single row)
      _sheet.insertRowIterables(_record.values, rowIndex++);
    return excel;
  }


  static const String tableName = "ACFTRecord";
  static const String columnRecordDate = "RecordDate";
  static const String columnRawMDL = "MDLRaw", columnRawSPT = "SPTRaw", columnRawHPU = "HPURaw";
  static const String columnRawSDC = "SDCRaw", columnRawLTK = "LTKRaw", columnRawCardio = "CardioRaw";
  static const String columnScoreMDL = "MDLScore", columnScoreSPT = "SPTScore", columnScoreHPU = "HPUScore";
  static const String columnScoreSDC = "SDCScore", columnScoreLTK = "LTKScore", columnScoreCardio = "CardioScore";
  static const String columnCardioAlter = "CardioAlter", columnScoreTotal = "ScoreTotal", columnQualifiedLevel = "QualifiedLevel";
  static const String columnMOSRequirement = "MOSRequirement", columnIsPassed = "isPassed";

  static const List<String> columnNames = <String>[
    columnRecordDate,columnRawMDL,columnScoreMDL,columnRawSPT,columnScoreSPT,
    columnRawHPU,columnScoreHPU,columnRawSDC,columnScoreSDC,columnRawLTK,columnScoreLTK,
    columnRawCardio,columnScoreCardio,columnCardioAlter,columnQualifiedLevel,
    columnScoreTotal,columnMOSRequirement,columnIsPassed,
  ];

  static const String sqlCreateTable="CREATE TABLE IF NOT EXISTS "+ tableName +" ("+
      columnRecordDate+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
      columnRawMDL+" INTEGER NOT NULL,"+ columnScoreMDL+" INTEGER NOT NULL,"+
      columnRawSPT+" FLOAT NOT NULL,"+ columnScoreSPT+" INTEGER NOT NULL,"+
      columnRawHPU+" INTEGER NOT NULL,"+ columnScoreHPU+" INTEGER NOT NULL,"+
      columnRawSDC+" TEXT NOT NULL,"+ columnScoreSDC+" INTEGER NOT NULL,"+
      columnRawLTK+" INTEGER NOT NULL,"+ columnScoreLTK+" INTEGER NOT NULL,"+
      columnRawCardio+" TEXT NOT NULL,"+ columnScoreCardio+" INTEGER NOT NULL,"+
      columnCardioAlter+" TEXT NOT NULL,"+ columnQualifiedLevel+" TEXT NOT NULL,"+
      columnScoreTotal+" INTEGER NOT NULL,"+columnMOSRequirement+" TEXT NOT NULL,"+
      columnIsPassed+" TEXT NOT NULL)";
  static const String sqlDropTable = "DROP TABLE IF EXISTS "+ tableName;
  static const String sqlSelect = "SELECT * FROM " + tableName;
  static const String sqlInsert = "INSERT OR REPLACE INTO "+tableName+
      "("+columnRecordDate+", "+
      columnRawMDL+", "+columnScoreMDL+", "+ columnRawSPT+", "+columnScoreSPT+", "+
      columnRawHPU+", "+columnScoreHPU+", "+ columnRawSDC+", "+columnScoreSDC+", "+
      columnRawLTK+", "+columnScoreLTK+", "+ columnRawCardio+", "+columnScoreCardio+", "+
      columnCardioAlter+", "+columnQualifiedLevel+", "+columnScoreTotal+", "+
      columnMOSRequirement+", "+columnIsPassed+") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  static const String sqlDeleteWhere = "DELETE FROM " + tableName + " WHERE ";
  static const String sqlDeleteAll = "DELETE FROM " + tableName;
}

String sqlWhereString(String _column, String _arg){ return (_column+"=\""+_arg+"\" "); }
String sqlWhereInt(String _column, int _arg){ return (_column+"=$_arg "); }
String sqlWhereDouble(String _column, double _arg){ return ("abs("+_column+"-$_arg)<0.1 "); }
double preciseDouble(double _obj){ return ((_obj*10).roundToDouble()/10.0); }


class ACFTLogCard extends StatelessWidget {
  final ACFTRecord record;
  ACFTLogCard(this.record);

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
        case 2: ACFTDBHelper().deleteRecord(record, context: context); break;
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
                TableCell(child: (record.isPassed? Text('Pass', textAlign: TextAlign.center,style: TextStyle(color: Colors.green),) : Text('Fail', textAlign: TextAlign.center,style: TextStyle(color: Colors.red),)) ),
                TableCell(child: Text('${record.totalScore} point', textAlign: TextAlign.end,)),
                TableCell(child: Text(record.qualified.toString(), textAlign: TextAlign.center,
                  style: TextStyle(color: (record.isPassed ? Colors.green : Colors.red)),)),
              ]),
              TableRow( children: [
                TableCell(child: Text('MDL')),
                TableCell(child: Text('${record.valueMDL.toInt()} lbs', textAlign: TextAlign.center,),),
                TableCell(child: Text('${record.score[0]} point', textAlign: TextAlign.end,)),
                TableCell(child: Text('${record.levelPF[0].toString()}', textAlign: TextAlign.center,
                  style: TextStyle(color: (record.levelPF[0].satisfies(record.mos)?Colors.green:Colors.red),),),),
              ]),
              TableRow( children: [
                TableCell(child: Text('SPT')),
                TableCell(child: Text('${record.valueSPT} m', textAlign: TextAlign.center,),),
                TableCell(child: Text('${record.score[1]} point', textAlign: TextAlign.end,)),
                TableCell(child: Text('${record.levelPF[1].toString()}', textAlign: TextAlign.center,
                  style: TextStyle(color: (record.levelPF[1].satisfies(record.mos)?Colors.green:Colors.red)),),),
              ]),
              TableRow( children: [
                TableCell(child: Text('HPU')),
                TableCell(child: Text('${record.valueHPU.toInt()} reps', textAlign: TextAlign.center,),),
                TableCell(child: Text('${record.score[2]} point', textAlign: TextAlign.end,)),
                TableCell(child: Text('${record.levelPF[2].toString()}', textAlign: TextAlign.center,
                  style: TextStyle(color: (record.levelPF[2].satisfies(record.mos)?Colors.green:Colors.red)),),),
              ]),
              TableRow( children: [
                TableCell(child: Text('SDC')),
                TableCell(child: Text(record.valueSDC.toString(), textAlign: TextAlign.center,),),
                TableCell(child: Text('${record.score[3]} point', textAlign: TextAlign.end,)),
                TableCell(child: Text('${record.levelPF[3].toString()}', textAlign: TextAlign.center,
                  style: TextStyle(color: (record.levelPF[3].satisfies(record.mos)?Colors.green:Colors.red)),),),
              ]),
              TableRow( children: [
                TableCell(child: Text('LTK')),
                TableCell(child: Text('${record.valueLTK.toInt()} reps', textAlign: TextAlign.center,),),
                TableCell(child: Text('${record.score[4]} point', textAlign: TextAlign.end,)),
                TableCell(child: Text('${record.levelPF[4].toString()}', textAlign: TextAlign.center,
                  style: TextStyle(color: (record.levelPF[4].satisfies(record.mos)?Colors.green:Colors.red)),),),
              ]),
              TableRow( children: [
                TableCell(child: Text(record.alter.toString())),
                TableCell(child: Text(record.valueCardio.toString(), textAlign: TextAlign.center,),),
                TableCell(child: Text('${record.score[5]} point', textAlign: TextAlign.end,)),
                TableCell(child: Text('${record.levelPF[5].toString()}', textAlign: TextAlign.center,
                  style: TextStyle(color: (record.levelPF[5].satisfies(record.mos)?Colors.green:Colors.red)),),),
              ]),
            ],
          ),
        ),
      ),
    );
  }


}


///*****************************************************************************************
