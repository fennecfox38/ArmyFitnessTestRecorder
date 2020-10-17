import 'package:flutter/material.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:share/share.dart';
import 'package:army_fitness_test_recorder/acft.dart';
import 'package:army_fitness_test_recorder/apft.dart';
import 'package:army_fitness_test_recorder/abcp.dart';
import 'package:army_fitness_test_recorder/group.dart';

const Widget _beforeLoded = Center(child: SizedBox( width: 100, height: 100, child: CircularProgressIndicator(), ),);

class LogPage extends StatefulWidget {
  const LogPage({@required key}) : super(key: key);
  @override LogPageState createState() => LogPageState();
}

class LogPageState extends State<LogPage> with SingleTickerProviderStateMixin {
  final List<Tab> _tabs = <Tab>[
    Tab(child: Row( mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [ Icon(Icons.fitness_center), Text('ACFT'),],),),
    Tab(child: Row( mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [ Icon(Icons.directions_run), Text('APFT'),],),),
    Tab(child: Row( mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [ Icon(Icons.accessibility), Text('ABCP'),],),),
  ];
  Widget _widgetACFT = _beforeLoded; Widget _widgetAPFT = _beforeLoded; Widget _widgetABCP = _beforeLoded;
  TabController _tabController;

  @override void initState(){
    super.initState();
    _tabController=new TabController(vsync: this, length: _tabs.length, initialIndex: 0);
    invalidateACFT();
    invalidateAPFT();
    invalidateABCP();
  }

  @override Widget build(BuildContext context) {
    return Scaffold(
      appBar: TabBar( tabs: _tabs, controller: _tabController, ), //indicatorColor: (MediaQuery.of(context).platformBrightness==Brightness.dark ? Colors.white : Colors.black),
      body: TabBarView( controller: _tabController, children: <Widget>[ _widgetACFT, _widgetAPFT, _widgetABCP, ], ),
      floatingActionButton: SpeedDial(
        child: Icon(Icons.more_vert), heroTag: 'LogMoreOption',
        overlayColor: (MediaQuery.of(context).platformBrightness==Brightness.dark ? Colors.black : Colors.white),
        children: [
          SpeedDialChild(label: 'Delete current table', labelBackgroundColor: Colors.transparent, child: Icon(Icons.delete), onTap: deleteDB,),
          SpeedDialChild(label: 'Share as MS Excel', labelBackgroundColor: Colors.transparent, child: Icon(Icons.share), onTap: shareXLSX,),
          SpeedDialChild(label: 'Share as SQL DataBase', labelBackgroundColor: Colors.transparent, child: Icon(Icons.share), onTap: shareDB,),
        ],
      ),
    );
  }

  Future<void> invalidateACFT() async =>
    ACFTDBHelper().getRecordList().then((_list) => setState(()=> _widgetACFT = ListView(children: _list.map((e) => e.logCard).toList() ,)));
  Future<void> invalidateAPFT() async =>
    APFTDBHelper().getRecordList().then((_list) => setState(()=> _widgetAPFT = ListView(children: _list.map((e) => e.logCard).toList() ,)));
  Future<void> invalidateABCP() async =>
    ABCPDBHelper().getRecordList().then((_list)=> setState(()=> _widgetABCP = ListView(children: _list.map((e) => e.logCard).toList() ,)));

  void shareDB() async {
    List<String> _paths = [await DBHelper().path,];
    if (_paths.isNotEmpty)
      await Share.shareFiles(_paths,);
  }
  void shareXLSX() async {
    List<String> _paths = [await DBHelper().exportExcel(),];
    if (_paths.isNotEmpty)
      await Share.shareFiles(_paths, /*text: 'Share MS Excel File'*/);
  }

  void deleteDB(){
    switch(_tabController.index){
      case 0: ACFTDBHelper().deleteAllRecord(); break;
      case 1: APFTDBHelper().deleteAllRecord(); break;
      case 2: ABCPDBHelper().deleteAllRecord(); break;
      default: break;
    }
  }

}
