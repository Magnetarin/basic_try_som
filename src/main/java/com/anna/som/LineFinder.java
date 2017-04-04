package com.anna.som;//
// Klasse		com.anna.som.LineFinder
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Implementiert Funktionen zum Suchen von zusammenh�ngenden Linienbereichen
// im Fingerabdruck und zum skelettieren der gefundenen Segmente.
//
import java.applet.*;
import java.util.Vector;

public class LineFinder
{
	private final int MINXDIST = 30; // Alles unter dieser Breite ist M�ll
	private final int MINYDIST = 80; // Alles unter dieser H�he ist M�ll
	private final int VERTRATIO = 4;
	// Einige Farben zur Markierung der Bilder:
	private final int COLOR_BACKGROUND       = 0xffffffff;
	private final int COLOR_SHORTLINE        = 0xff00ff00;
	private final int COLOR_LONGLINE         = 0xffff0000;
	private final int COLOR_MARKER			 = 0xffff00ff;
	private final int COLOR_BUFFER_SKELETON  = 0xff0000ff;
	private final int COLOR_BUFFER_REMOVABLE = 0xff00ffff;
	private final int COLOR_BUFFER_MARKER    = 0xffffff00;
	private final int COLOR_IN_LINE		     = 0xff000000; // Farbe des Eingabebildes

	private int SourceData[], BufferData[], OutData[];
	private int w, h, x, y, minx, maxx, miny, maxy;
	private AppletContext ac;
	private boolean dark = false;
	public Vector Nodes;

	/*
	** com.anna.som.LineFinder Constructor
	**
	** Erzeugt ein neues com.anna.som.LineFinder Objekt mit den angegebenen Ein- und
	** Ausgabe-Daten (Pixelarray) und der angegebenen Gr��e.
	*/
	public LineFinder(int _SourceData[], int _OutData[], int _w, int _h)
	{
		int i;

		SourceData = _SourceData;
		OutData = _OutData;
		w = _w;
		h = _h;
		x = 0;
		y = 0;
		BufferData = new int[w*h];
		for(i=0;i<w*h;i++) {
			BufferData[i] = COLOR_BACKGROUND;	// wei� f�llen
		}
		Nodes = new Vector(10,10);
	}

	/*
	** SetAppletContext
	**
	** Setzt den AppletContext des Applets f�r dieses Objekt, damit
	** Debugausgaben in der Statuszeile m�glich sind.
	*/
	public void SetAppletContext(AppletContext _ac)
	{
		ac = _ac;
	}

	/*
	** Fill
	**
	** F�hrt im Quellbild einen F�llalgorithmus aus, wobei an den 
	** angegebenen Koordinaten begonnen wird. Der gef�llte Bereich
	** wird in das Pufferbild kopiert und im Quellbild markiert.
	*/
	private void Fill(int cx, int cy)
	{
		// neue Grenzen abstecken:
		if(cx<minx)	minx=cx;
		if(cx>maxx)	maxx=cx;
		if(cy<miny)	miny=cy;
		if(cy>maxy)	maxy=cy;

		// aktuellen Pixel f�llen:
		BufferData[cy*w+cx] = COLOR_LONGLINE;
		// und in Quelle markieren:
		SourceData[cy*w+cx] = COLOR_MARKER;
		// Alle vier Richtungen absuchen:
		if(cx > 0 && SourceData[cy*w+(cx-1)] == COLOR_IN_LINE) {
			Fill(cx-1,cy);
		}
		if(cx < (w-1) && SourceData[cy*w+(cx+1)] == COLOR_IN_LINE) {
			Fill(cx+1,cy);
		}
		if(cy > 0 && SourceData[(cy-1)*w+cx] == COLOR_IN_LINE) {
			Fill(cx,cy-1);
		}
		if(cy < (h-1) && SourceData[(cy+1)*w+cx] == COLOR_IN_LINE) {
			Fill(cx,cy+1);
		}	
	}

	/*
	** Find
	**
	** Sucht das Quellbild sequenziell nach einem Eingabepixel ab.
	** Wird einer gefunden, wird mit ihm als Ursprung die F�ll-
	** Routine aufgerufen. Die Funktion Find arbeitet bei jedem
	** Aufruf an der Stelle weiter, wo sie beim letzen Aufruf
	** aufgeh�rt hat.
	*/
	public boolean Find()
	{
		minx = w-1;
		maxx = 0;
		miny = h-1;
		maxy = 0;
		
		for(;x<w;x++) {
			for(;y<h;y++) {
				if(SourceData[y*w+x] == COLOR_IN_LINE) {
					// original Linienpixel gefunden
					Fill(x,y);
					return true;
				}
			}
			y = 0;
		}
		return false;
	}

	/*
	** Copy
	**
	** Kopiert ein gefundenes Liniensegment (ausgezeichnet durch das
	** umschreibende Rechteck, das beim F�llen aufgezeichnet wird)
	** vom Pufferbild ins Ausgabebild. Schwarze Bereiche im Ausgabebild
	** werden dabei nicht �berschrieben, so da� Markierungen dort nicht
	** verloren gehen.
	*/
	public void Copy()
	{
		int cx, cy;
		int FillColor;

		if(maxx-minx < MINXDIST && maxy-miny < MINYDIST &&
			(maxx-minx) < VERTRATIO * (maxy-miny))
			FillColor = COLOR_SHORTLINE;
		else
			FillColor = COLOR_LONGLINE;

		try {
			for(cx=minx;cx<=maxx;cx++) {
				for(cy=miny;cy<=maxy;cy++) {
					if(BufferData[cy*w+cx] != COLOR_BACKGROUND) {
						if(OutData[cy*w+cx] != COLOR_BUFFER_MARKER) { // schwarz nicht �berschreiben!
							if(BufferData[cy*w+cx] == COLOR_BUFFER_MARKER) {
								OutData[cy*w+cx] = COLOR_MARKER;
							} else {
								OutData[cy*w+cx] = FillColor;
							}
						}
						BufferData[cy*w+cx] = COLOR_BACKGROUND;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			ac.showStatus("Oops in Copy");
		}
	}

	/*
	** Neighbour
	**
	** Liefert die Position (Offset) im Bild-Array des j-Nachbarn des Punktes x,y
	** Falls der j-Nachbar au�erhalb des Bildes liegen w�rde, wird der
	** Offset des Eingabepunktes (x,y) zur�ckgeliefert.
	*/
	private int Neighbour(int x, int y, int j)
	{
		switch(j) {
			case 0:
			case 1:
			case 7: x++;
		}
		switch(j) {
			case 3:
			case 4:
			case 5:	x--;
		}
		switch(j) {
			case 1:
			case 2:
			case 3: y--;
		}
		switch(j) {
			case 5:
			case 6:
			case 7:	y++;
		}
	
		if(x>=w-1)	x = w-1;
		if(x<0)		x = 0;
		if(y>=h-1)	y = h-1;
		if(y<0)		y = 0;

		return y*w+x;
	}

	/*
	** NeighbourX
	**
	** Liefert die X-Koordinate des j-Nachbarn des Punktes x,y
	** Falls die X-Koordinate �ber den Rand des Bildes hinauslaufen w�rde,
	** wird der Randpunkt (com.anna.som.Eingabe-x) zur�ckgeliefert.
	*/
	private int NeighbourX(int x, int y, int j)
	{
		if(x==0 && (j==3||j==4||j==5)) {
			j=8;
		} else if (x==w && (j==7||j==0||j==1)) {
			j=8;
		}
		if(y==0 && (j==1||j==2||j==3)) {
			j=8;
		} else if (y==h && (j==5||j==7||j==8)) {
			j=8;
		}
		switch(j) {
		case 0: return x+1;
		case 1:	return x+1;
		case 2: return x;
		case 3:	return x-1;
		case 4:	return x-1;
		case 5:	return x-1;
		case 6: return x;
		case 7:	return x+1;
		default: return x;
		}
	}

	/*
	** NeighbourY
	**
	** Liefert die Y-Koordinate des j-Nachbarn des Punktes x,y
	** Falls die Y-Koordinate �ber den Rand des Bildes hinauslaufen w�rde,
	** wird der Randpunkt (com.anna.som.Eingabe-y) zur�ckgeliefert.
	*/
	private int NeighbourY(int x, int y, int j)
	{
		if(x==0 && (j==3||j==4||j==5)) {
			j=8;
		} else if (x==w && (j==7||j==0||j==1)) {
			j=8;
		}
		if(y==0 && (j==1||j==2||j==3)) {
			j=8;
		} else if (y==h && (j==5||j==7||j==8)) {
			j=8;
		}
		switch(j) {
		case 0: return y;
		case 1:	return y-1;
		case 2: return y-1;
		case 3:	return y-1;
		case 4:	return y;
		case 5:	return y+1;
		case 6: return y+1;
		case 7:	return y+1;
		default: return y;
		}
	}
		
	/*
	** MatchPatterns
	**
	** Pr�ft f�r die Skelettierung, ob die umgebenden Pixel 
	** des Punktes x/y ein Muster ergeben, so da� der Punkt x/y
	** ein skelettaler Punkt ist. Liefert true, wenn es ein
	** skelettaler Punkt ist, sonst false.
	*/
	private boolean MatchPatterns(int x, int y)
	{
		if(x>=w-1)	x = w-1;
		if(x<0)		x = 0;
		if(y>=h-1)	y = h-1;
		if(y<0)		y = 0;

		if( BufferData[Neighbour(x,y,0)] == 0xffffffff &&
			BufferData[Neighbour(x,y,4)] == 0xffffffff &&
			(	BufferData[Neighbour(x,y,1)] != 0xffffffff ||		// A A A
				BufferData[Neighbour(x,y,2)] != 0xffffffff ||		// 0 P 0
				BufferData[Neighbour(x,y,3)] != 0xffffffff)&&		// B B B
			(	BufferData[Neighbour(x,y,5)] != 0xffffffff || 
				BufferData[Neighbour(x,y,6)] != 0xffffffff ||
				BufferData[Neighbour(x,y,7)] != 0xffffffff )) {
			return true;
		} else if ( BufferData[Neighbour(x,y,2)] == 0xffffffff &&
					BufferData[Neighbour(x,y,6)] == 0xffffffff &&
					(	BufferData[Neighbour(x,y,7)] != 0xffffffff ||	// B 0 A
						BufferData[Neighbour(x,y,0)] != 0xffffffff ||	// B P A
						BufferData[Neighbour(x,y,1)] != 0xffffffff)&&	// B 0 A
					(	BufferData[Neighbour(x,y,3)] != 0xffffffff || 
						BufferData[Neighbour(x,y,4)] != 0xffffffff || 
						BufferData[Neighbour(x,y,5)] != 0xffffffff )) {
			return true;
		} else if ( BufferData[Neighbour(x,y,7)] == COLOR_BUFFER_SKELETON &&
					BufferData[Neighbour(x,y,0)] == 0xffffffff &&
					BufferData[Neighbour(x,y,6)] == 0xffffffff &&
					(	BufferData[Neighbour(x,y,1)] != 0xffffffff ||	// A A A
						BufferData[Neighbour(x,y,2)] != 0xffffffff ||	// A P 0
						BufferData[Neighbour(x,y,3)] != 0xffffffff ||	// A 0 2
						BufferData[Neighbour(x,y,4)] != 0xffffffff ||
						BufferData[Neighbour(x,y,5)] != 0xffffffff )) {
			return true;
		} else if ( BufferData[Neighbour(x,y,5)] == COLOR_BUFFER_SKELETON &&
					BufferData[Neighbour(x,y,4)] == 0xffffffff &&
					BufferData[Neighbour(x,y,6)] == 0xffffffff &&
					(	BufferData[Neighbour(x,y,7)] != 0xffffffff ||	// A A A
						BufferData[Neighbour(x,y,0)] != 0xffffffff ||	// 0 P A
						BufferData[Neighbour(x,y,1)] != 0xffffffff ||	// 2 0 A
						BufferData[Neighbour(x,y,2)] != 0xffffffff ||
						BufferData[Neighbour(x,y,3)] != 0xffffffff )) {
			return true;		
		} else if ( BufferData[Neighbour(x,y,3)] == COLOR_BUFFER_SKELETON &&
					BufferData[Neighbour(x,y,2)] == 0xffffffff &&
					BufferData[Neighbour(x,y,4)] == 0xffffffff &&
					(	BufferData[Neighbour(x,y,5)] != 0xffffffff ||	// 2 0 A
						BufferData[Neighbour(x,y,6)] != 0xffffffff ||	// 0 P A
						BufferData[Neighbour(x,y,7)] != 0xffffffff ||	// A A A
						BufferData[Neighbour(x,y,0)] != 0xffffffff ||
						BufferData[Neighbour(x,y,1)] != 0xffffffff )) {
			return true;		
		} else if ( BufferData[Neighbour(x,y,1)] == COLOR_BUFFER_SKELETON &&
					BufferData[Neighbour(x,y,0)] == 0xffffffff &&
					BufferData[Neighbour(x,y,2)] == 0xffffffff &&
					(	BufferData[Neighbour(x,y,3)] != 0xffffffff ||	// A 0 2
						BufferData[Neighbour(x,y,4)] != 0xffffffff ||	// A P 0
						BufferData[Neighbour(x,y,5)] != 0xffffffff ||	// A A A
						BufferData[Neighbour(x,y,6)] != 0xffffffff ||
						BufferData[Neighbour(x,y,7)] != 0xffffffff )) {
			return true;		
		} else {
			return false;
		}
	}

	/*
	** Thin
	**
	** F�hrt den Skelettierungs-Algorithmus durch. Es wird jeweils das
	** aktuelle Liniensegment skelettiert, dessen Grenzen durch das
	** umschreibende Rechteck gegeben sind. Die komplette Verarbeitung
	** l�uft im Puffer-Bild.
	*/
	public void Thin()
	{
		boolean Remain, Skel;
		int j;
		int x,y;

		// Eingabebild in Puffer kopieren, weil's da gesucht wird 
		// (zwecks Verkn�pfung der einzelnen Schritte!

		Remain = true;
		while (Remain) {
			Remain = false;
			for(j=0;j<=6;j+=2) { // j = 0, 2, 4, 6
				for(x=minx;x<=maxx;x++) {	
					for(y=miny;y<=maxy;y++) {
						if(BufferData[y*w+x] == COLOR_LONGLINE &&
							(BufferData[Neighbour(x,y,j)] == COLOR_BACKGROUND)) {

							if(MatchPatterns(x,y)) {
								BufferData[y*w+x] = COLOR_BUFFER_SKELETON;	// skelettales Pixel
							} else {
								BufferData[y*w+x] = COLOR_BUFFER_REMOVABLE; // entfernbares Pixel
								Remain = true;
							}
						}
					}
				}
				// Alle entfernbaren Pixel l�schen:
				for(x=minx;x<=maxx;x++) {
					for(y=miny;y<=maxy;y++) {
						if(BufferData[y*w+x] == COLOR_BUFFER_REMOVABLE) {
							BufferData[y*w+x] = COLOR_BACKGROUND;
						}
					}
				}
			}
		}

	}

	/*
	** CountNeighbours
	**
	** Z�hlt die Anzahl der i-Nachbarn um den Punkt x/y, die nicht
	** die Hintergrundfarbe haben.
	*/
	private int CountNeighbours(int x, int y)
	{
		int i, nNeighbours;

		nNeighbours = 0;
		for(i=0;i<=7;i++) {
			if(BufferData[Neighbour(x,y,i)] != COLOR_BACKGROUND) {
				nNeighbours++;
			}
		}
		return nNeighbours;
	}

	/*
	** FindNodes
	**
	** Sucht im aktuellen Liniensegment nach Knotenpunkten mit
	** entweder nur einem oder mindestens drei Nachbarpunkten.
	** Die gefundenen Punkte werden in den Nodes-Vektor eingetragen.
	*/
	public void FindNodes()
	{
		int cx, cy;

		// Anfangspunkt suchen:
		if(!(maxx-minx < MINXDIST && maxy-miny < MINYDIST &&
			(maxx-minx) < VERTRATIO * (maxy-miny))) {

			for(cy=miny;cy<=maxy;cy++) {
				for(cx=minx;cx<=maxx;cx++) {
					if(BufferData[cy*w+cx] == COLOR_BUFFER_SKELETON) {
						if(CountNeighbours(cx,cy) == 1) {	// Linien-Endpunkt
							Nodes.addElement(new Node(cx,cy,1));
						} else if(CountNeighbours(cx,cy) > 2) { // Abzweigung
							Nodes.addElement(new Node(cx,cy,3));
						}
					}
				}
			}
		}
	}

	/*
	** DrawCenterLine
	**
	** Zeichnet im aktuellen Liniensegment eine senkrechte Linie
	** in der Mitte des Segments. Ist nur f�r optisches Debugging
	** gedacht.
	*/
	public void DrawCenterLine()
	{
		int x, y;

		if(!(maxx-minx < MINXDIST && maxy-miny < MINYDIST &&
			(maxx-minx) < VERTRATIO * (maxy-miny))) {

			if((maxx-minx)*2 < (maxy-miny)) {
				x = minx + (maxx-minx)/2;
				for(y=miny;y<=maxy;y++) {
					OutData[y*w+x] = COLOR_MARKER;
				}
			}
		}
	}
}
