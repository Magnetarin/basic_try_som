package com.anna.som;//
// Klasse		com.anna.som.Filter
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Implementiert die Funktionen zur Kantendetektion eines
// Fingerabdruck-Bildes. Es sind drei verschiedene
// Kantendetektionsverfahren implementiert, die �ber den
// Parameter in doFilter ausgew�hlt werden k�nnen.
//

class Filter
{
	private PixelArray Source, Destination;	// Bildspeicher f�r Quell- und Zielbild
	private int Filtersize;					// Gr��e der Filtermatrix f�r Sobel
	private int FilterArray[][];			// Filtermatrix f�r Sobel

	/*
	** com.anna.som.Filter Constructor
	**
	** Erzeugt ein neues com.anna.som.Filter-Objekt mit dem angegebenen
	** Bildspeicher als Eingabebild. Initialisiert die Filtermatrix
	** mit einem 5*5 Sobel-Operator
	*/
	public Filter(PixelArray _Source)
	{
		int i;

		Source = _Source;
		Destination = null;

		// TODO: Einstellungen noch fest verdrahtet; Parametrisierbar machen!
		Filtersize = 5;
		FilterArray = new int[Filtersize][];

		// Filtermatrix initialisieren:
		for(i=0;i<Filtersize;i++) {
			FilterArray[i]=new int[Filtersize];
		}

		FilterArray[0][0] = 20;
		FilterArray[0][1] = 20;
		FilterArray[0][2] = 20;
		FilterArray[0][3] = 20;
		FilterArray[0][4] = 20;
		FilterArray[1][0] = 17;
		FilterArray[1][1] = 17;
		FilterArray[1][2] = 17;
		FilterArray[1][3] = 17;
		FilterArray[1][4] = 17;
		FilterArray[2][0] = 0;
		FilterArray[2][1] = 0;
		FilterArray[2][2] = 0;
		FilterArray[2][3] = 0;
		FilterArray[2][4] = 0;
		FilterArray[3][0] = -17;
		FilterArray[3][1] = -17;
		FilterArray[3][2] = -17;
		FilterArray[3][3] = -17;
		FilterArray[3][4] = -17;
		FilterArray[4][0] = -20;
		FilterArray[4][1] = -20;
		FilterArray[4][2] = -20;
		FilterArray[4][3] = -20;
		FilterArray[4][4] = -20;
	}

	/*
	** getResult
	**
	** Liefert nach Berechnung der Kantendetektion das Ziel-com.anna.som.PixelArray zu�ck.
	** Der Aufruf dieser Funktion darf nur nach Berechnung des Filters
	** (�ber doFilter) erfolgen. Vorher ist das Ergebnis null.
	*/
	public PixelArray getResult()
	{
		return Destination;
	}

	/*
	** doFilter
	**
	** F�hrt die Berechnung der Kantendetektion durch. Der Parameter Mode gibt an,
	** mit welchem Verfahren die Kanten erkannt werden sollen: 1 = Schwellwert,
	** 2 = Sobel, 3 = Logarithmisch.
	*/
	public void doFilter(int Mode)
	{
		int x, y, sum;
/*		int b, c;*/

		// Neues Ziel-com.anna.som.PixelArray anlegen und initialisieren:
		Destination = new PixelArray(Source.getWidth(), Source.getHeight());
		for(x=0;x<Destination.getWidth();x++) {
			for(y=0;y<Destination.getHeight();y++) {
				Destination.setGrayPixel(x,y,255);
			}
		}

		// Alle Punkte des Quell-Bildes durchlaufen:
		for(x=0;x<Source.getWidth();x++) {
			for(y=0;y<Source.getHeight();y++) {

				if(Mode == 1) {
					sum = EdgeTest(x,y);	// Schwellwert
				} else if(Mode ==2) {
					sum = FilterMatrix(x,y);	// Sobel
				} else {
					sum = (int)Math.round(	// Logarithmisch (keine Funktion, machen wir direkt hier)
						Math.log((1.0/(double)(Source.getGrayPixel(x,y)+20))*70.0)  /
						Math.log(2)  * -255.0 ); 
					if(sum<250) sum = 0;
					else sum = 255;
				}
				Destination.setGrayPixel(x,y,sum); // Ausgabepixel setzen
			}
		}
	}

	/*
	** FilterMatrix
	**
	** Berechnet die Filtermatrix-Summe im Punkt x/y des Quell-PixelArrays.
	** Liegt ein Matrix-Punkt au�erhalb des Bildes, wird auf den n�chsten Punkt,
	** der innerhalb des Bildes liegt (= Bildrand) zugegriffen. Die Matrix
	** selbst wird dabei nicht gestaucht.
	** Die Gr��e der Matrix mu� immer ungeradzahlig sein!
	*/
	private int FilterMatrix(int x, int y)
	{
		int Filterradius = Filtersize / 2;
		int Sum, mx, my;

		Sum = 0;
		
		for(mx=-Filterradius;mx<=Filterradius;mx++) {
			for(my=-Filterradius;my<=Filterradius;my++) {
				Sum += Source.getGrayPixel(x+mx, y+my) * FilterArray[mx+Filterradius][my+Filterradius];
			}
		}

		if(Sum<0) Sum = 0;
		if(Sum>255) Sum = 255;

		return 255-Sum;	// invertieren
	}

	/*
	** EdgeTest
	**
	** F�hrt eine nichtmathematische Detektion von dunklen Linien und Kanten durch.
	** Es wird gepr�ft, ob der Punkt x/y �ber einem gewissen Grauwert oder unter einem
	** anderen Grauwert liegt, so da� er direkt der Ausgabefarbe wei� oder schwarz
	** zugeordnet werden kann.
	** Falls der Punkt nicht eindeutig schwarz oder wei� ist, wird in der Umgebung des
	** Punktes gepr�ft, ob eine bestimmt Anzahl Nachbarpunkte um einen bestimmten
	** Grauwert abweichen. Falls ja, geh�rt der Punkt zu einer Kante.
	** Der R�ckgabewert ist der Grauwert f�r Schwarz oder Wei�.
	*/
	private int EdgeTest(int x, int y)
	{

		int THRESBLACK = 100;			// Schwellwert des Grauwertes, unter dem alles 
										//		schwarz ausgegeben wird.
		int THRESWHITE = 160;			// Schwellwert des Grauwertes, �ber dem alles
										//		wei� ausgegeben wird.
		int WHITENEIGHBOURS = 1;		// Anzahl der Pixel, die in i-Nachbarschaft
										//		wei� sein m�ssen, um den aktuellen
										//		Pixel auch wei� auszugeben.

		int pix, b, c, Out, sum;

		
		pix = Source.getGrayPixel(x,y);

		if(pix < THRESBLACK) {
			Out = 0;	// Schwarzer Pixel
		} else if (pix > THRESWHITE) {
			Out = 255;	// Wei�er Pixel
		} else {
			// Bei nicht eindeutigen Logarithmus anwenden:
			sum = (int)Math.round( Math.log((1.0/(double)(Source.getGrayPixel(x,y)))*70.0)  /
					Math.log(2)  * -255.0 ); 		
			if(sum > 200) {
				Out = 255;
			} else {

				// Umgebungspixel in der 1. Reihe untersuchen:

				c = 0;
				if(Source.getGrayPixel(x-1,y-1) > THRESWHITE)
					c++;
				if(Source.getGrayPixel(x  ,y-1) > THRESWHITE)
					c++;
				if(Source.getGrayPixel(x+1,y-1) > THRESWHITE)
					c++;
				if(Source.getGrayPixel(x-1,y  ) > THRESWHITE)
					c++;
				if(Source.getGrayPixel(x+1,y  ) > THRESWHITE)
					c++;
				if(Source.getGrayPixel(x-1,y+1) > THRESWHITE)
					c++;
				if(Source.getGrayPixel(x  ,y+1) > THRESWHITE)
					c++;
				if(Source.getGrayPixel(x+1,y+1) > THRESWHITE)
					c++;

				if(c >= WHITENEIGHBOURS) {
					Out = 255;
				} else {
					Out = 0;
				}
			}
		} 
		return Out;
	}
}
