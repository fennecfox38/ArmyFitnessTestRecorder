import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:army_fitness_test_recorder/acft_page.dart';
import 'package:army_fitness_test_recorder/apft_page.dart';
import 'package:army_fitness_test_recorder/abcp_page.dart';

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
  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  int _index = 0;
  final List<Text> _title = [Text('Combat Fitness Test'),Text('Physical Fitness Test'),Text('Body Composition Program'),Text('Log'),];
  final List<Widget> _pages = <Widget>[ACFTPage(),APFTPage(),ABCPPage(),ACFTPage()];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: _title[_index],
        actions: <Widget>[ IconButton(icon: Icon(Icons.settings,), onPressed: () { print('Menu Pressed'); },), ],
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
      body: IndexedStack(index: _index, children: _pages,),
      bottomNavigationBar: BottomNavigationBar(
        onTap: (_index){ setState(() {this._index = _index;}); FocusScope.of(context).unfocus(); }, //{pageController.jumpToPage(_index);},
        currentIndex: _index,
        items: <BottomNavigationBarItem>[
          BottomNavigationBarItem( title: Text("ACFT"), icon: Icon(Icons.fitness_center), ),
          BottomNavigationBarItem( title: Text("APFT"), icon: Icon(Icons.directions_run), ),
          BottomNavigationBarItem( title: Text("ABCP"), icon: Icon(Icons.accessibility), ),
          BottomNavigationBarItem( title: Text("Log"), icon: Icon(Icons.receipt), ),
        ],
      ),
    );
  }
}
