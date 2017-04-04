package com.anna.som;//
// Klasse		com.anna.som.PictureCanvas
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Implementiert eine von Canvas abgeleitete Klasse, die spezielle
// Funktionen zur Verf�gung stellt um ein Image in diesem Canvas auszugeben.
// Das Image kann dabei entweder ein einzelnes Image oder drei getrennte,
// kleinere Bilder, die untereinander mit Beschriftung dargestellt werden, sein.
// Zum Setzen der Pixel des einzelnen Bildes gibt es die M�glichkeit, direkt
// ein Image zu �bergeben, oder die Pixel aus einem com.anna.som.PixelArray zu setzen.
// Beim Setzen des Bildes wird dieses automatisch sofort ausgegeben.
//
import java.awt.*;
import java.awt.image.*;


class PictureCanvas extends Canvas
{
	Image img;			// einzelnes Bild der Bildbearbeitung
	Image img3[];		// 3-Bild-Ansicht der Netz-Struktur
	boolean useimg3;	// true, wenn 3-Bild-Ansicht benutzt werden soll

	/*
	** com.anna.som.PictureCanvas Constructor
	**
	** Erzeugt das Array f�r die Bilder der 3-Bild-Ansicht der
	** Netz-Struktur.
	*/
	public PictureCanvas()
	{
		img3 = new Image[3];
	}

	/*
	** Set3Image
	**
	** Setzt ein Bild f�r die 3-Bild-Ansicht der Netz-Struktur.
	** img gibt das Bild an, i den Index (1 - 3) des Bildes,
	** 1 = Zentren, 2 = Muster, 3 = Ausgabe Map.
	*/
	public void Set3Image(Image img, int i)
	{
		img3[i-1] = img;
		useimg3 = true;	// 3-Bild-Ansicht benutzen
	}
	
	/*
	** update3Image
	**
	** Zeichnet die drei Bilder der Netz-Struktur im Panel.
	** Falls die Bilder nicht verwendet werden (bei Ausgabe der Bildverarbeitung),
	** wird die Funktion sofort verlassen.
	*/
	public void update3Image()
	{
		Graphics gr;

		if(useimg3) {
			gr = getGraphics();
			gr.setColor(Color.black);
			gr.fillRect(0,0,500,800);
			if(gr != null) {
				// Beschriftungen ausgeben:
				gr.setColor(Color.white);
				gr.drawString("Zentren:", 100, 40);
				gr.drawString("Muster:", 100, 260);
				gr.drawString("Ausgabe Map:", 100,480);

				gr.setColor(Color.cyan.brighter());
				gr.drawString("Christian", 200, 700);
				gr.setColor(Color.green.brighter());
				gr.drawString("Peter", 200, 720);
				gr.setColor(Color.red);
				gr.drawString("Liane", 200, 740);
				gr.setColor(Color.yellow);
				gr.drawString("Andrea", 200, 760);

				// Bilder ausgeben:
				gr.drawImage(img3[0], 200, 30, null);
				gr.drawImage(img3[1], 200, 250, null);
				gr.drawImage(img3[2], 200, 470, null);

				// Rahmen um die Bilder zeichnen:
				gr.setColor(Color.white);
				gr.drawRect(199,29,img3[0].getWidth(null)+1,img3[0].getHeight(null)+1);
				gr.drawRect(199,249,img3[1].getWidth(null)+1,img3[1].getHeight(null)+1);
				gr.drawRect(199,469,img3[2].getWidth(null)+1,img3[2].getHeight(null)+1);
			}
		}
	}

	/*
	** SetImage
	**
	** Setzt das angegebene Image in den com.anna.som.PictureCanvas und zeichnet es.
	*/
	public void SetImage(Image i)
	{
		Graphics gr;

		useimg3 = false;

		img = i;
		gr = getGraphics();
		if(gr != null) {
			gr.drawImage(img, 0, 0, null);
		}
	}

	/*
	** paint
	**
	** Wird aufgerufen, wenn das Bild aktualisiert werden mu�.
	** -- Keine Ahnung, ob paint oder update das Richtige sind.
	**    Beide schaden aber offensichtlich auch nicht...
	*/
	public void paint(Graphics  g)
	{
		if(useimg3) {
			update3Image();
		} else {
			g.drawImage(img, 0, 0, null);
		}
	}

	/*
	** update
	**
	** Wird aufgerufen, wenn das Bild aktualisiert werden mu�.
	** -- Keine Ahnung, ob paint oder update das Richtige sind.
	**    Beide schaden aber offensichtlich auch nicht...
	*/
	public void update(Graphics g)
	{
		if(useimg3) {
			update3Image();
		} else {
			g.drawImage(img, 0, 0, null);
		}
	}

	/*
	** setPixels
	**
	** Erzeugt ein neues Bild, das im com.anna.som.PictureCanvas angezeigt wird
	** und initialisiert es mit den Bilddaten aus dem angegebenen
	** com.anna.som.PixelArray. Falls der aktuelle com.anna.som.PictureCanvas kleiner ist als
	** das com.anna.som.PixelArray, wird das Bild um den Faktor 2 in beiden Richtungen
	** verkleinert.
	** Das Bild wird sofort neu gezeichnet.
	*/
	public void setPixels(PixelArray pix)
	{
		int BufferData[] = new int[pix.getHeight()/2*pix.getWidth()/2];
		int x,y;
		Graphics gr;

		useimg3 = false;

		// ACHTUNG: com.anna.som.PixelArray mu� entweder genauso gro� sein wie img, oder doppelt so gro�!!!
		if(img.getHeight(null)<pix.getHeight() ||
			img.getWidth(null)<pix.getWidth()) {
			// Bildgr��e halbieren!
			for(x=0;x<pix.getWidth()/2;x++) {
				for(y=0;y<pix.getHeight()/2;y++) {
					BufferData[y*pix.getWidth()/2+x] = pix.getPixel(x*2,y*2);
				}
			}
			img = createImage(new MemoryImageSource(pix.getWidth()/2, pix.getHeight()/2, BufferData, 0, pix.getWidth()/2));
		} else {
			img = createImage(new MemoryImageSource(pix.getWidth(), pix.getHeight(), pix.Pixels, 0, pix.getWidth()));
		}
		
		// Neu zeichnen. Auf update warten hilft nichts!
		gr = getGraphics();
		if(gr!=null) {
			gr.drawImage(img, 0, 0, null);
		}
	}
}

