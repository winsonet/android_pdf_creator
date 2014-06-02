/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package com.pablog178.pdfcreator.android;

import java.io.OutputStream;
import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.webkit.WebView;

@Kroll.module(name="Pdfcreator", id="com.pablog178.pdfcreator.android")
public class PdfcreatorModule extends KrollModule
{

	// Standard Debugging variables
	private static final String MODULE_NAME = "PdfcreatorModule";
	private static final String PROXY_NAME = "PDF_PROXY";

	// Private members
	private static TiApplication app;
	private TiUIView 	view 	= null;
	private String 		fileName = "default_name.pdf";
	private float 		shrinking = 1f;

	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;
	
	public PdfcreatorModule(){
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication myApp)
	{
		Log.d(MODULE_NAME, "inside onAppCreate");
		app = myApp;
		// put module init code that needs to run when the application is created
	}

	// Methods

	/**
	 * Generates a new PDF based on the given view, withe the given fileName on the app directory
	 */
	@Kroll.method(runOnUiThread=true)
	public void generatePDF(final HashMap args){
		Log.i(PROXY_NAME, "generatePDF()");

		if(TiApplication.isUIThread()){
			generatePDFfunction(args);
		} else {
			app.getCurrentActivity().runOnUiThread(new Runnable(){
				public void run(){
					generatePDFfunction(args);
				}
			});
		}
	}

	@Kroll.method(runOnUiThread=true)
	public void generateImage(final HashMap args){
		Log.i(PROXY_NAME, "generateImage()");

		if(TiApplication.isUIThread()){
			generateImageFunction(args);
		} else {
			app.getCurrentActivity().runOnUiThread(new Runnable(){
				public void run(){
					generateImageFunction(args);
				}
			});
		}
	}

	private void generatePDFfunction(HashMap args){
		if(args.containsKey("fileName")){
			Object fileName = args.get("fileName");
			if(fileName instanceof String){
				this.fileName = (String) fileName;
				Log.i(PROXY_NAME, "fileName: " + this.fileName);
			}
		} else return;

		if(args.containsKey("view")){
			Object viewObject = args.get("view");
			if(viewObject instanceof TiViewProxy){
				TiViewProxy viewProxy = (TiViewProxy) viewObject;
				this.view = viewProxy.getOrCreateView();
				if(this.view == null){
					Log.e(PROXY_NAME, "NO VIEW was created!!");
					return;
				}
				Log.i(PROXY_NAME, "view: " + this.view.toString());
			}
		} else return;

		if(args.containsKey("shrinking")){
			this.shrinking = TiConvert.toFloat(args.get("shrinking"));
		}



		TiBaseFile file = TiFileFactory.createTitaniumFile(this.fileName, true);
		Log.i(PROXY_NAME, "file full path: " + file.nativePath());
		try {
			Resources 		appResources 	= app.getResources();
			OutputStream 	outputStream 	= file.getOutputStream();
			final int 		PDF_WIDTH 		= 612;
			final int 		PDF_HEIGHT 		= 792;
			int viewWidth = 1600;
			int viewHeight = 1;
			
			PdfDocument 	pdfDocument 	= new PdfDocument();
			PageInfo 		pageInfo 		= new PageInfo.Builder(PDF_WIDTH, PDF_HEIGHT, 1).create();
			Page 			page 			= pdfDocument.startPage(pageInfo);


			WebView 		view 			= (WebView) this.view.getNativeView();

			if (TiApplication.isUIThread()) {

				viewWidth 		= view.capturePicture().getWidth();
				viewHeight 		= view.capturePicture().getHeight();
			} else {
				Log.e(PROXY_NAME, "NO UI THREAD");				
			}

			Log.i(PROXY_NAME, "viewWidth: " + viewWidth);
			Log.i(PROXY_NAME, "viewHeight: " + viewHeight);

			Bitmap 			viewBitmap 		= Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
			float 			density 		= appResources.getDisplayMetrics().density;

			Canvas 			canvas 			= new Canvas(viewBitmap);
			Matrix 			matrix 			= new Matrix();

			Drawable bgDrawable = view.getBackground();
	        if (bgDrawable != null){
				bgDrawable.draw(canvas);
			} else {
				canvas.drawColor(Color.WHITE);
			}
			view.draw(canvas);

			float scaleFactorWidth 	= 1 / ((float)viewWidth  / (float)PDF_WIDTH);
			float scaleFactorHeight = 1 / ((float)viewHeight / (float)PDF_HEIGHT);

			Log.i(PROXY_NAME, "scaleFactorWidth: " + scaleFactorWidth);
			Log.i(PROXY_NAME, "scaleFactorHeight: " + scaleFactorHeight);

			matrix.setScale(scaleFactorWidth * this.shrinking, scaleFactorWidth * this.shrinking);

			Bitmap shrinkedBitmap = Bitmap.createBitmap((int)(viewWidth * scaleFactorWidth * this.shrinking),
														(int)(viewHeight * scaleFactorWidth * this.shrinking),
														Bitmap.Config.ARGB_8888);

			Canvas shrinkedCanvas = new Canvas(shrinkedBitmap);

			shrinkedCanvas.drawBitmap(viewBitmap, matrix, null);

			Matrix newMatrix = new Matrix();
			newMatrix.setScale(1 / this.shrinking, 1 / this.shrinking);


			Canvas pdfCanvas = page.getCanvas();
			pdfCanvas.drawBitmap(shrinkedBitmap, newMatrix, null);
			// pdfCanvas.drawBitmap(viewBitmap, matrix, null);

			pdfDocument.finishPage(page);
			pdfDocument.writeTo(outputStream);
			pdfDocument.close();

			sendCompleteEvent();

		} catch (Exception exception){
			Log.e(PROXY_NAME, "Error: " + exception.toString());
			sendErrorEvent(exception.toString());
		}
	}

	private void generateImageFunction(HashMap args){
		if(args.containsKey("fileName")){
			Object fileName = args.get("fileName");
			if(fileName instanceof String){
				this.fileName = (String) fileName;
				Log.i(PROXY_NAME, "fileName: " + this.fileName);
			}
		} else return;

		if(args.containsKey("view")){
			Object viewObject = args.get("view");
			if(viewObject instanceof TiViewProxy){
				TiViewProxy viewProxy = (TiViewProxy) viewObject;
				this.view = viewProxy.getOrCreateView();
				if(this.view == null){
					Log.e(PROXY_NAME, "NO VIEW was created!!");
					return;
				}
				Log.i(PROXY_NAME, "view: " + this.view.toString());
			}
		} else return;

		if(args.containsKey("shrinking")){
			this.shrinking = TiConvert.toFloat(args.get("shrinking"));
		}



		TiBaseFile file = TiFileFactory.createTitaniumFile(this.fileName, true);
		Log.i(PROXY_NAME, "file full path: " + file.nativePath());
		try {
			final int 		PDF_WIDTH 		= 612;
			final int 		PDF_HEIGHT 		= 792;
			Resources 		appResources 	= app.getResources();
			OutputStream 	outputStream 	= file.getOutputStream();
			int viewWidth = 1600;
			int viewHeight = 1;
			
			WebView 		view 			= (WebView) this.view.getNativeView();

			if (TiApplication.isUIThread()) {

				viewWidth 		= view.capturePicture().getWidth();
				viewHeight 		= view.capturePicture().getHeight();
			} else {
				Log.e(PROXY_NAME, "NO UI THREAD");				
			}

			Log.i(PROXY_NAME, "viewWidth: " + viewWidth);
			Log.i(PROXY_NAME, "viewHeight: " + viewHeight);

			Bitmap 			viewBitmap 		= Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
			float 			density 		= appResources.getDisplayMetrics().density;

			Canvas 			canvas 			= new Canvas(viewBitmap);
			Matrix 			matrix 			= new Matrix();

			Drawable bgDrawable = view.getBackground();
	        if (bgDrawable != null){
				bgDrawable.draw(canvas);
			} else {
				canvas.drawColor(Color.WHITE);
			}
			view.draw(canvas);

			float scaleFactorWidth 	= 1 / ((float)viewWidth  / (float)PDF_WIDTH);
			float scaleFactorHeight = 1 / ((float)viewHeight / (float)PDF_HEIGHT);

			Log.i(PROXY_NAME, "scaleFactorWidth: " + scaleFactorWidth);
			Log.i(PROXY_NAME, "scaleFactorHeight: " + scaleFactorHeight);

			// matrix.setScale(scaleFactorWidth * this.shrinking, scaleFactorWidth * this.shrinking);
			matrix.setScale(scaleFactorWidth, scaleFactorWidth);

			Bitmap imageBitmap = Bitmap.createBitmap(PDF_WIDTH, PDF_HEIGHT, Bitmap.Config.ARGB_8888);
			Canvas imageCanvas = new Canvas(imageBitmap);
			imageCanvas.drawBitmap(viewBitmap, matrix, null);
			imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

			// Bitmap shrinkedBitmap = Bitmap.createBitmap((int)(viewWidth * scaleFactorWidth * this.shrinking),
			// 											(int)(viewHeight * scaleFactorWidth * this.shrinking),
			// 											Bitmap.Config.ARGB_8888);

			// Canvas shrinkedCanvas = new Canvas(shrinkedBitmap);

			// shrinkedCanvas.drawBitmap(viewBitmap, matrix, null);

			// Matrix newMatrix = new Matrix();
			// newMatrix.setScale(1 / this.shrinking, 1 / this.shrinking);


			// Canvas pdfCanvas = page.getCanvas();
			// pdfCanvas.drawBitmap(shrinkedBitmap, newMatrix, null);
			// pdfCanvas.drawBitmap(viewBitmap, matrix, null);

			// pdfDocument.finishPage(page);
			// pdfDocument.writeTo(outputStream);
			// pdfDocument.close();

			sendCompleteEvent();

		} catch (Exception exception){
			Log.e(PROXY_NAME, "Error: " + exception.toString());
			sendErrorEvent(exception.toString());
		}
	}

	// method to invoke success callback
	private void sendCompleteEvent() {
	    if (this.hasListeners("complete")) {
	        KrollDict props = new KrollDict();
	        props.put("fileName", this.fileName);
	        this.fireEvent("complete", props);
	    }
	}

	// method to invoke error callback
	private void sendErrorEvent(String message) {
	    if (this.hasListeners("error")) {
	        KrollDict props = new KrollDict();
	        props.put("message", message);
	        this.fireEvent("error", props);
	    }
	}
}

