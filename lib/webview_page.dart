import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';

class WebViewPage extends StatefulWidget {
  final String title, url;
  WebViewPage({@required this.title, @required this.url});
  @override _WebViewPageState createState() => _WebViewPageState();
}

class _WebViewPageState extends State<WebViewPage> {
  @override void initState() {
    super.initState();
    if (Platform.isAndroid) WebView.platform = SurfaceAndroidWebView();
  }

  @override Widget build(BuildContext context) => Scaffold(
    appBar: AppBar(title: Text(widget.title), elevation: 0.0,),
    body: SafeArea(
      child: WebView(
        initialUrl: 'about:blank',
        javascriptMode: JavascriptMode.unrestricted,
        onWebViewCreated: (WebViewController webViewController) {
          if(widget.url.contains('html'))
            _loadHtml(widget.url).then((_url) => webViewController.loadUrl(_url));
          else webViewController.loadUrl(widget.url);
        },
      ),
    ),
  );

  Future<String> _loadHtml(String _str) async => Uri.dataFromString( await rootBundle.loadString(_str), mimeType: 'text/html', encoding: Encoding.getByName('utf-8'), ).toString();
}
