// This is a test harness for your module
// You should do something interesting in this harness 
// to test out the module and to provide instructions 
// to users on how to use it by example.


// open a single window
var pdfcreator = require('com.pablog178.pdfcreator.android');
var win = Ti.UI.createWindow({
	backgroundColor:'red'
});

var webView = Ti.UI.createWebView({
	width : 0,
	height : 0,
	scalesPageToFit : true,
	url : 'http://na.leagueoflegends.com/'
});

win.add(webView);
win.open();

pdfcreator.addEventListener('complete', completeEvent);
pdfcreator.addEventListener('error', errorEvent);
webView.addEventListener('load', generatePDF);
// webView.addEventListener('load', generateImage);


function generatePDF () {
	var fileName = 'myPDF.pdf';
	
	pdfcreator.generatePDF({
		view : webView,
		fileName : fileName,
		shrinking : 1.0
	});

}

function generateImage () {
	var fileName = 'myImage.png';

	pdfcreator.generateImage({
		view : webView,
		fileName : fileName
	});
}

function completeEvent(evt) {
	Ti.API.info("COMPLETED!");
	alert('PDf file created!');
	// var f = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, fileName);
    // Ti.Android.currentActivity.startActivity(Ti.Android.createIntent({
    //     action: Ti.Android.ACTION_VIEW,
    //     type: 'application/pdf',
    //     data: f.getNativePath()
    // }));
	
}

function errorEvent (evt) {
	alert('An error ocurred!: ' + JSON.stringify(evt, null, ' '));
}