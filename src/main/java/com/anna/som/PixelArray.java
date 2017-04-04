package com.anna.som;//
// Klasse		com.anna.som.PixelArray
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Implementiert ein Array, in dem Bildpixel gespeichert werden k�nnen.
// Die Klasse com.anna.som.PixelArray bietet Funktionen, mit denen die Bildpixel
// direkt ausgelesen oder manipuliert werden k�nnen (�ber Angabe deren
// Offset oder Koordinaten). Au�erdem sind rudiment�re Zeichenfunktionen 
// vorhanden. Das eigentliche Pixel-Array ist public, so da� es direkt
// modifiziert und an andere Funktionen �bergeben werden kann.
//

class PixelArray
{
	public int Pixels[];	// Speicher f�r Pixel
	private int w, h;		// Gr��e des Bildes

	/*
	** com.anna.som.PixelArray Constructor
	**
	** Erzeugt einen neuen Bildspeicher mit der �ber Breite und
	** H�he angegebenen Gr��e.
	*/
	public PixelArray(int _w, int _h)
	{
		w = _w;
		h = _h;
		Pixels = new int[w*h];
	}

	/*
	** Clear
	**
	** F�llt das gesamte Pixel-Array wei�.
	*/
	public void Clear()
	{
		int i;

		for(i=0;i<h*w;i++) {
			Pixels[i] = 0xffffffff;
		}
	}

	/*
	** getWidth
	**
	** Liefert die Breite des Bildspeichers zur�ck.
	*/
	public int getWidth()
	{
		return w;
	}

	/*
	** getHeight
	**
	** Liefert die H�he des Bildspeichers zur�ck.
	*/
	public int getHeight()
	{
		return h;
	}

	/*
	** setPixel
	**
	** Setzt den Wert des Pixels am angegebenen Offset auf die
	** angegebene Farbe.
	*/
	public void setPixel(int offset, int color)
	{
		Pixels[offset] = color;
	}

	/*
	** setPixel
	**
	** Setzt den Wert des Pixels an der angegebenen x/y-Position
	** auf die angegebene Farbe
	*/
	public void setPixel(int x, int y, int color)
	{
		if(x<0||y<0||x>=w||y>=h) {
			return; // Punkt liegt au�erhalb des Bildes
		}

		Pixels[y*w+x] = color;
	}

	/*
	** setGrayPixel
	**
	** Setzt den Wert des Pixels an der angegebenen x/y-Position
	** auf den angegebenen Grau-Wert (0-255)
	*/
	public void setGrayPixel(int x, int y, int gray)
	{
		if(x<0||y<0||x>=w||y>=h) {
			return; // Punkt liegt au�erhalb des Bildes
		}
		
		if(gray<0)		gray = 0;
		if(gray>255)	gray = 255;

		Pixels[y*w+x] = 0xff000000 | gray | gray << 8 | gray << 16;
	}


	/*
	** getPixel
	**
	** Liefert den Farbwert des Pixels am angegebenen Offset
	** zur�ck.
	*/
	public int getPixel(int offset)
	{
		return Pixels[offset];
	}

	/*
	** getPixel
	**
	** Liefert den Farbwert des Pixels an der angegebenen
	** x/y-Position zur�ck.
	*/
	public int getPixel(int x, int y)
	{
		// Koordinaten auf Bildbereich zwingen:
		if(x<0)		x=0;
		if(y<0)		y=0;
		if(x>=w)	x=w-1;
		if(y>=h)	y=h-1;

		return Pixels[y*w+x];
	}

	/*
	** getGrayPixel
	**
	** Liefert den Grauwert dex Pixels an der angegebenen
	** x/y-Position zur�ck. Es wird dabei keine 
	** Intensit�tskorrektur der RGB-Werte vorgenommen.
	*/
	public int getGrayPixel(int x, int y)
	{
		// Koordinaten auf Bildbereich zwingen:
		if(x<0)		x=0;
		if(y<0)		y=0;
		if(x>=w)	x=w-1;
		if(y>=h)	y=h-1;

		return   (Pixels[y*w+x]        & 0x000000ff) / 3 +
				((Pixels[y*w+x] >>  8) & 0x000000ff) / 3 +
				((Pixels[y*w+x] >> 16) & 0x000000ff) / 3;
	}

	/*
	** drawCircle
	**
	** Zeichnet im Pixelspeicher einen Kreis mit dem Mittelpunkt
	** im Punkt x/y, dem Radius r und der Farbe color.
	** Der Winkel der Ausgabe wird in 5� Schritten erh�ht, was bei
	** gro�en Kreisen zu Unterbrechnungen f�hren kann.
	*/
	public void drawCircle(int x, int y, int r, int color)
	{
		int i;
		for (i=0;i<360;i+=5) {
			setPixel((int)Math.round(x+Math.sin(i)*r), 
				(int)Math.round(y+Math.cos(i)*r),color);
		}
	}

	/*
	** drawBox
	**
	** Zeichnet im Pixelspeicher ein Quadrat mit der Kantenl�nge
	** r um den Mittelpunkt x/y in der Farbe color.
	*/
	public void drawBox(int x, int y, int r, int color)
	{
		int i;
		for(i=x-(r/2);i<x+(r/2);i++) {
			setPixel(i, y-(r/2), color);
			setPixel(i, y+(r/2), color);
		}
		for(i=y-(r/2);i<y+(r/2);i++) {
			setPixel(x-(r/2), i, color);
			setPixel(x+(r/2), i, color);
		}
	}

	/*
	** Clone
	**
	** Erzeugt eine Kopie des aktuellen Pixelspeichers.
	** Der Bildinhalt wird dabei ebenfalls kopiert.
	** ACHTUNG: Dies ist nicht die �berladene Funktion "clone",
	** da diese ein Object zur�ckliefern sollte, es aber keinen
	** Sinn macht, in der Funktion auf Object zu downcasten und
	** anschlie�end wieder auf com.anna.som.PixelArray upcasten.
	*/
	public PixelArray Clone()
	{
		int i;
		PixelArray c;

		// Neues Array erzeugen:
		c = new PixelArray(w,h);

		// Und Bild kopieren:
		for(i=0;i<w*h;i++) {
			c.Pixels[i] = Pixels[i];
		}

		return c;
	}

}