import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:army_fitness_test_recorder/acft.dart';
import 'package:army_fitness_test_recorder/apft.dart';
import 'package:army_fitness_test_recorder/abcp.dart';
import 'package:army_fitness_test_recorder/acft_page.dart';
import 'package:army_fitness_test_recorder/apft_page.dart';
import 'package:army_fitness_test_recorder/abcp_page.dart';
import 'package:army_fitness_test_recorder/log_page.dart';

void main() {
  SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(systemNavigationBarColor: Colors.orange,),);
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  final _focusNode = FocusScopeNode();

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: (){_focusNode.unfocus();},
      child: FocusScope(
        node: _focusNode,
        child: MaterialApp(
          title: 'Army Fitness Test Recorder',
          theme: ThemeData(
            //primaryColor: Colors.orange, primaryColorDark: Colors.orange, accentColor: Colors.orangeAccent,
            primarySwatch: Colors.orange,
            //appBarTheme: AppBarTheme(iconTheme: IconThemeData(color: Colors.white), actionsIconTheme: IconThemeData(color: Colors.white),
            //    textTheme: TextTheme(headline6: TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.w500),)),
            bottomNavigationBarTheme: BottomNavigationBarThemeData(backgroundColor: Colors.orange, selectedItemColor: Colors.black, type: BottomNavigationBarType.fixed, ),
            //sliderTheme: SliderThemeData(thumbColor: Colors.orangeAccent, inactiveTrackColor: Colors.black12, activeTrackColor: Colors.orangeAccent,),
            visualDensity: VisualDensity.adaptivePlatformDensity,
          ),
          darkTheme: ThemeData.dark().copyWith(
            primaryColor: Colors.orange, primaryColorDark: Colors.orange, accentColor: Colors.orangeAccent,
            bottomNavigationBarTheme: BottomNavigationBarThemeData(backgroundColor: Colors.orange, selectedItemColor: Colors.white, type: BottomNavigationBarType.fixed, ),
            snackBarTheme: SnackBarThemeData(contentTextStyle: TextStyle(color: Colors.white), backgroundColor: Colors.black87, actionTextColor: Colors.orangeAccent),
            sliderTheme: SliderThemeData(thumbColor: Colors.orangeAccent, inactiveTrackColor: Colors.white10, activeTrackColor: Colors.orangeAccent,),
            floatingActionButtonTheme: FloatingActionButtonThemeData(backgroundColor: Colors.orange, foregroundColor: Colors.white),
            visualDensity: VisualDensity.adaptivePlatformDensity,
          ),
          home: MainPage(),
        ),
      ),
    );
  }
}

class MainPage extends StatefulWidget {
  @override _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  int _index = 0;
  static GlobalKey<LogPageState> _keyLogPage = GlobalKey<LogPageState>();
  final List<Text> _title = [Text('Combat Fitness Test'),Text('Physical Fitness Test'),Text('Body Composition Program'),Text('Log'),];
  final List<StatefulWidget> _pages = <StatefulWidget>[ACFTPage(),APFTPage(), ABCPPage(),LogPage(key: _keyLogPage),];
  final List<List<Widget>> _appbarActions = [
    [ IconButton(icon: Icon(Icons.table_chart,), onPressed: () {  },), ],
    [ IconButton(icon: Icon(Icons.table_chart,), onPressed: () {  },), ],
    [ IconButton(icon: Icon(Icons.table_chart,), onPressed: () {  },), ],
    [ IconButton(icon: Icon(Icons.share,), onPressed: ()=>_keyLogPage.currentState.shareDB(),), IconButton(icon: Icon(Icons.delete,), onPressed: ()=>_keyLogPage.currentState.deleteDB(),), ],
  ];

  @override void initState(){
    super.initState();
    ACFTDBHelper.keyLogPage = _keyLogPage;
    APFTDBHelper.keyLogPage = _keyLogPage;
    ABCPDBHelper.keyLogPage = _keyLogPage;
  }

  @override Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: _title[_index],
        actions: _appbarActions[_index],
      ),
      drawer: Drawer(
        child: ListView(
          padding: EdgeInsets.zero, // Important: Remove any padding from the ListView.
          children: <Widget>[
            DrawerHeader( child: Text('Drawer Header'), decoration: BoxDecoration(color: Colors.orange,),),
            ListTile( title: Text('Item 1'), onTap: () { }, ),
            ListTile( title: Text('Item 2'), onTap: () { }, ),
          ],
        ),
      ),
      body: IndexedStack(index: _index, sizing: StackFit.expand, children: _pages,),
      bottomNavigationBar: BottomNavigationBar(
        onTap: (_index){ setState(() {this._index = _index;}); FocusScope.of(context).unfocus(); }, //{pageController.jumpToPage(_index);},
        currentIndex: _index,
        items: <BottomNavigationBarItem>[
          BottomNavigationBarItem( label: "ACFT", icon: Icon(Icons.fitness_center), ),
          BottomNavigationBarItem( label: "APFT", icon: Icon(Icons.directions_run), ),
          BottomNavigationBarItem( label: "ABCP", icon: Icon(Icons.accessibility), ),
          BottomNavigationBarItem( label: "Log", icon: Icon(Icons.receipt), ),
        ],
      ),
    );
  }

  void routeToLogPage() => setState(() => this._index = 3);
}
