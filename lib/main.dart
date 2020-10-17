import 'dart:io';
import 'package:army_fitness_test_recorder/webview_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:device_info/device_info.dart';
import 'package:package_info/package_info.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:army_fitness_test_recorder/acft_page.dart';
import 'package:army_fitness_test_recorder/apft_page.dart';
import 'package:army_fitness_test_recorder/abcp_page.dart';
import 'package:army_fitness_test_recorder/log_page.dart';
import 'package:army_fitness_test_recorder/group.dart';

void main() {
  SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(systemNavigationBarColor: Colors.amber,),);
  runApp(MyApp());
}

class MyApp extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return Listener(
      onPointerDown: (_){
        FocusScopeNode currentFocus = FocusScope.of(context);
        if (!currentFocus.hasPrimaryFocus && currentFocus.focusedChild != null)
          currentFocus.focusedChild.unfocus();
      },
      child: MaterialApp(
        title: 'Army Fitness Test Recorder',
        theme: ThemeData(
          primarySwatch: Colors.amber,
          cardColor: Colors.amberAccent,
          //appBarTheme: AppBarTheme(iconTheme: IconThemeData(color: Colors.white), actionsIconTheme: IconThemeData(color: Colors.white),
          //    textTheme: TextTheme(headline6: TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.w500),)),
          bottomNavigationBarTheme: BottomNavigationBarThemeData(backgroundColor: Colors.amber, selectedItemColor: Colors.black, type: BottomNavigationBarType.fixed, ),
          //sliderTheme: SliderThemeData(thumbColor: Colors.amberAccent, inactiveTrackColor: Colors.black12, activeTrackColor: Colors.amberAccent,),
          visualDensity: VisualDensity.adaptivePlatformDensity,
        ),
        darkTheme: ThemeData.dark().copyWith(
          primaryColor: Colors.amber, primaryColorDark: Colors.amber, accentColor: Colors.amberAccent, indicatorColor: Colors.amber, cursorColor: Colors.amberAccent,
          highlightColor: Colors.amber, focusColor: Colors.amber, splashColor: Colors.amber, toggleableActiveColor: Colors.amberAccent,
          textSelectionTheme: TextSelectionThemeData(cursorColor: Colors.amberAccent, selectionColor: Colors.amberAccent, selectionHandleColor: Colors.amberAccent),
          bottomNavigationBarTheme: BottomNavigationBarThemeData(backgroundColor: Colors.amber, selectedItemColor: Colors.white, type: BottomNavigationBarType.fixed, ),
          snackBarTheme: SnackBarThemeData(contentTextStyle: TextStyle(color: Colors.white), backgroundColor: Colors.black87, actionTextColor: Colors.amberAccent),
          sliderTheme: SliderThemeData(thumbColor: Colors.amberAccent, inactiveTrackColor: Colors.white10, activeTrackColor: Colors.amberAccent,),
          floatingActionButtonTheme: FloatingActionButtonThemeData(backgroundColor: Colors.amber, foregroundColor: Colors.white),
          visualDensity: VisualDensity.adaptivePlatformDensity,
        ),
        home: MainPage(),
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
  final PopupMenuButton _logAction = PopupMenuButton(
    icon: Icon(Icons.more_vert,), initialValue: 0,
    itemBuilder: (context) => <PopupMenuEntry>[
      PopupMenuItem(value: 1, child: Row(children: <Widget>[Icon(Icons.share,), SizedBox(width: 8,), Text('Share as SQL DataBase'),],),),
      PopupMenuItem(value: 2, child: Row(children: <Widget>[Icon(Icons.share,), SizedBox(width: 8,), Text('Share as MS Excel'),],),),
      PopupMenuDivider(),
      PopupMenuItem(value: 3, child: Row(children: <Widget>[Icon(Icons.delete,), SizedBox(width: 8,), Text('Delete current table'),],),),
    ],
    onSelected: (value){ switch(value){
      case 1: _keyLogPage.currentState.shareDB(); break;
      case 2: _keyLogPage.currentState.shareXLSX(); break;
      case 3: _keyLogPage.currentState.deleteDB(); break;
      default: break;
    }},
  );

  @override void initState(){
    super.initState();
    DBHelper.keyLogPage = _keyLogPage;
  }

  @override Widget build(BuildContext context) => Scaffold(
    appBar: AppBar(
      title: _title[_index],
      actions: [
        [ IconButton(icon: Icon(Icons.table_chart,), onPressed: ()=>_routeWebView('ACFT Score Scale Chart', 'assets/ACFTChart.html'),), ],
        [ IconButton(icon: Icon(Icons.table_chart,), onPressed: ()=>_routeWebView('APFT Score Scale Chart', 'assets/APFTChart.html'),), ],
        [ IconButton(icon: Icon(Icons.table_chart,), onPressed: ()=>_routeWebView('ABCP Score Scale Chart', 'assets/ABCPChart.html'),), ],
        [_logAction],
      ][_index],
    ),
    body: IndexedStack(index: _index, sizing: StackFit.expand, children: _pages,),
    bottomNavigationBar: BottomNavigationBar(
      onTap: (_index){ setState(() {this._index = _index;}); },
      currentIndex: _index,
      items: <BottomNavigationBarItem>[
        BottomNavigationBarItem( label: "ACFT", icon: Icon(Icons.fitness_center), ),
        BottomNavigationBarItem( label: "APFT", icon: Icon(Icons.directions_run), ),
        BottomNavigationBarItem( label: "ABCP", icon: Icon(Icons.accessibility), ),
        BottomNavigationBarItem( label: "Log", icon: Icon(Icons.event_note), ),
      ],
    ),
    drawer: Drawer(
      child: ListView(
        padding: EdgeInsets.zero, // Important: Remove any padding from the ListView.
        children: <Widget>[
          DrawerHeader( decoration: BoxDecoration(color: Colors.amber,),
            child: Column( mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [
              SizedBox(width:96, height: 96, child: Image.asset('assets/ic_launcher.png'),),
              Text('Army Fitness Test Recorder'),
            ],),
          ),
          ListTile( leading: Icon(Icons.table_chart), title: Text('ACFT Score Scale Chart'), onTap: ()=>_routeWebView('ACFT Score Scale Chart', 'assets/ACFTChart.html'), ),
          ListTile( leading: Icon(Icons.table_chart), title: Text('APFT Score Scale Chart'), onTap: ()=>_routeWebView('APFT Score Scale Chart', 'assets/APFTChart.html'), ),
          ListTile( leading: Icon(Icons.table_chart), title: Text('ABCP Score Scale Chart'), onTap: ()=>_routeWebView('ABCP Score Scale Chart', 'assets/ABCPChart.html'), ),
          Divider(height: 1, thickness: 2,),
          ListTile( leading: Icon(Icons.mail), title: Text('Feedback'), onTap: _launchFeedbackEmail, ),
          ListTile( leading: Icon(Icons.local_grocery_store), title: Text('Store'), onTap: _launchStore, ),
          ListTile( leading: Icon(Icons.info), title: Text('Information'), onTap: ()=>showDialog(context: context, builder: (context)=>AlertDialog(
            title: Row(children: [ Icon(Icons.info), SizedBox(width: 8,),Text('Information'),],),
            content: Text(infoBody),
            actions: [
              FlatButton(onPressed: () => Navigator.pop(context), child: Text('Dismiss', style: TextStyle(color: Colors.amberAccent),) ),
              FlatButton(
                child: Text('GitHub', style: TextStyle(color: Colors.amberAccent),),
                onPressed: () async { await launch('https://github.com/fennecfox38/ArmyFitnessTestRecorder'); Navigator.pop(context); },
              ),
              FlatButton(
                child: Text('License', style: TextStyle(color: Colors.amberAccent),),
                onPressed: () { Navigator.pop(context); _routeWebView('License', 'assets/License.html'); },
              ),
            ],
          ),),),
        ],
      ),
    ),
  );
  void _routeWebView(String _title, String _url) => Navigator.push(context, MaterialPageRoute(builder: (context)=>WebViewPage(title: _title, url: _url,),));
}

void _launchFeedbackEmail() async {
  PackageInfo _packageInfo = await PackageInfo.fromPlatform();
  String _url ='mailto:fennecfox38@gmail.com?subject=Feedback For ${_packageInfo.appName}&body=';
  _url += '*************************************************\nPackage Name: ${_packageInfo.packageName}\nApp Version: ${_packageInfo.version}\n';
  try {
    if (Platform.isAndroid) {
      AndroidDeviceInfo _build = await DeviceInfoPlugin().androidInfo;
      _url += 'Device Model: ${_build.manufacturer} ${_build.model}\nDevice OS Version: Android ${_build.version.release} (SDK ${_build.version.sdkInt})\n';
    } else if (Platform.isIOS) {
      IosDeviceInfo _build = await DeviceInfoPlugin().iosInfo;
      _url += 'Device Model: Apple ${_build.name}\nDevice OS Version: ${_build.systemName} ${_build.systemVersion}\n';
    }
  } on PlatformException { _url += 'Fail to get device information';}
  finally { _url += '*************************************************\n\n\n'; }

  if (await canLaunch(_url)) await launch(_url);
  else throw 'Could not launch $_url';
}

void _launchStore() async {
  PackageInfo _packageInfo = await PackageInfo.fromPlatform();
  String _url;
  if(Platform.isAndroid) _url = 'http://play.google.com/store/apps/details?id=${_packageInfo.packageName}';
  else if(Platform.isIOS) _url = 'itms-apps://itunes.apple.com/app/apple-store/${_packageInfo.packageName}?mt=8';
  if (await canLaunch(_url)) await launch(_url);
  else throw 'Could not launch $_url';
}

const String infoBody = 'This Application (Army Fitness Test Recorder) has been designed based on US Army Regulation and Field Manual (AR 600–9, FM 7–22) to help soldiers.\n'
    '\nHowever, this application has never been officially authorized. The result of this application cannot be valid without the record in official form. Use for reference only.\n'
    '\nThis application is under Apache License 2.0. The source has been published on GitHub. More information below.';