import 'package:flutter/material.dart';
import 'package:army_fitness_test_recorder/acft.dart';
import 'package:army_fitness_test_recorder/apft.dart';
import 'package:army_fitness_test_recorder/abcp.dart';
import 'package:share/share.dart';

class LogPage extends StatefulWidget {
  const LogPage({key}) : super(key: key);
  @override LogPageState createState() => LogPageState();
}

class LogPageState extends State<LogPage> {
  List<ACFTRecord> _acftList;
  List<APFTRecord> _apftList;
  List<ABCPRecord> _abcpList;
  Widget _widgetACFT = Center(child: SizedBox( width: 100, height: 100, child: CircularProgressIndicator(), ),);
  Widget _widgetAPFT = Center(child: SizedBox( width: 100, height: 100, child: CircularProgressIndicator(), ),);
  Widget _widgetABCP = Center(child: SizedBox( width: 100, height: 100, child: CircularProgressIndicator(), ),);

  @override void initState(){
    super.initState();
    invalidateACFT();
    invalidateAPFT();
    invalidateABCP();
  }
  @override Widget build(BuildContext context) {

    return DefaultTabController(
      length: 3,
      child: Column(
        children: [
          Container(
            color: Colors.orange,
            child: TabBar(
              indicatorColor: (MediaQuery.of(context).platformBrightness==Brightness.dark ? Colors.white : Colors.black),
              tabs: [
                Tab(child: Row( mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [ Icon(Icons.fitness_center), Text('ACFT'),],),),
                Tab(child: Row( mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [ Icon(Icons.directions_run), Text('APFT'),],),),
                Tab(child: Row( mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [ Icon(Icons.accessibility), Text('ABCP'),],),),
              ],
            ),
          ),
          Expanded(child: TabBarView( children: [ _widgetACFT, _widgetAPFT, _widgetABCP, ], )),
        ],
      ),
    );
  }

  Future<void> invalidateACFT() async {
    _acftList = await ACFTDBHelper().getRecordList();
    setState(() =>
      _widgetACFT = Expanded(child: ListView(children: _acftList.map((e) => e.logCard).toList() ,),)
    );
  }
  Future<void> invalidateAPFT() async {
    _apftList = await APFTDBHelper().getRecordList();
    setState(() =>
      _widgetAPFT = Expanded(child: ListView(children: _apftList.map((e) => e.logCard).toList() ,),)
    );
  }
  Future<void> invalidateABCP() async {
    _abcpList = await ABCPDBHelper().getRecordList();
    setState(() =>
      _widgetABCP = Expanded(child: ListView(children: _abcpList.map((e) => e.logCard).toList() ,),)
    );
  }

  void shareDB(){
    /*if (imagePaths.isNotEmpty) {
      await Share.shareFiles(imagePaths,
          text: text,
          subject: subject,
          sharePositionOrigin: box.localToGlobal(Offset.zero) & box.size);
    }*/
  }
  void deleteDB(){

  }

}
