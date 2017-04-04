package com.anna.som;//
// Klasse		com.anna.som.KohonenNetz
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Die com.anna.som.KohonenNetz - Klasse ist der Kern allen Lernens - das Ged�chtnis.
// Sie definiert die Feature- und Output - Map, sowie alle notwendigen 
// Initialisierungs-, Lern- und Suchfunktionen. Eine com.anna.som.KohonenNetz - Klasse
// wird f�r eine bestimmte Eingangs- und Ausgangsdimension von Vektoren 
// erstellt und kann dann nur mit solchen Vektoren verwendet werden.
// Zus�tzlich besitzt die Klasse zwei Zeichenmethoden, die zur 
// Visualisierung der Feature - Map dienen.  

import java.awt.*;

public class KohonenNetz 
{
	private NeuroVektor m_FeatureMap[][];	// Die Feature - Map
	private NeuroVektor m_OutputMap[][];	// Die Output - Map
	private float m_eps, m_sig;				// Aktuelle Varianz und Lernrate
	private int m_zi, m_zj;					// Zentrum der letzten Suche
	private boolean m_autoeps=false;		// Lernen mit oder ohne Autolernrate

	/*
	**
	**	Konstruktor der com.anna.som.KohonenNetz - Klasse, der die Dimension des
	**	Netzes, der Eingabevektoren und der Ausgabevektoren festlegt
	**	und die Maps initialisiert.
	**
	*/
	public KohonenNetz(int dim, int in, int out)
	{
		m_FeatureMap=new NeuroVektor[dim][dim];
		m_OutputMap=new NeuroVektor[dim][dim];
		// Neuronen (com.anna.som.NeuroVektor) erzeugen.
		for (int i=0; i<m_FeatureMap.length;i++)
			for (int j=0; j<m_FeatureMap[i].length;j++) {
				m_FeatureMap[i][j]=new NeuroVektor(in);
				m_OutputMap[i][j]=new NeuroVektor(out);
			}
	}

	public int GetMapDim()
	{
		// Liefert die Dimension der Feature - Map zur�ck.
		return m_FeatureMap.length;
	}

	public int GetInputDim()
	{
		// Liefert die Dimension der Eingabevektoren zur�ck.
		return m_FeatureMap[0][0].GetCount();
	}

	public int GetOutputDim()
	{
		// Liefert die Dimension der Ausgabevektoren zur�ck.
		return m_OutputMap[0][0].GetCount();
	}
	
	public NeuroVektor GetOutputVektor(int i, int j)
	{
		// Liefert das Neuron_v1 an der Stelle (i,j) zur�ck.
		return m_OutputMap[i][j];
	}

	public float Lernrate(float eps)
	{
		// Setzt die Lernrate neu.
		float tmp=m_eps;
		m_eps=eps;
		return tmp;
	}

	public float Varianz(float varianz)
	{
		// Setzt die Varianz (Radius) neu.
		float tmp=m_sig;
		m_sig=varianz;
		return tmp;
	}

	public float Varianz()
	{
		// Gibt die Varianz zur�ck.
		return m_sig;
	}

	public void	AutoLernrate(boolean a)
	{
		// Stellt die Lernmethode ein: 'a' = true -> Autolernrate aktiv.
		m_autoeps=a;
	}

	public boolean AutoLernrate()
	{
		// Liefert die aktuelle Lernmethode zur�ck.
		return m_autoeps;
	}


	/*
	**
	** InitFeatureMap - Methoden, zur Vorbelegung der Feature - Map
	** mit Anfnagswerten. Es gibt neben der klassischen Methode mit der
	** Vorbelegung durch Zufallszahlen noch die M�glichkeit die Map 
	** definiert, in einer Art Pyramidenlandschaft vorzubelegen. 
	**
	*/
	public void InitFeatureMap(byte val)
	{
		// Initialisiert die Feature - Map mit einem festen Wert 'val'.
		for (int i=0; i<m_FeatureMap.length;i++)
			for (int j=0; j<m_FeatureMap[i].length;j++) 
				m_FeatureMap[i][j].InitVektor(val);
	}

	public void InitFeatureMap(byte min, byte max)
	{
		// Initialisiert die Feature - Map mit Zufallswerten 
		// zwischen dem Wert 'min' und dem Wert 'max'.
		for (int i=0; i<m_FeatureMap.length;i++)
			for (int j=0; j<m_FeatureMap[i].length;j++) 
				m_FeatureMap[i][j].InitVektor(min,max);
	}
	
	public void InitFeatureMap()
	{
		// Initialisiert die Feature - Map pyramidenf�rmig, mit 
		// bereits vorhandenen Zentren an den Spitzen (Null - Vektor).
		byte max=50, ri, rj;
		byte n=(byte)(m_FeatureMap.length/5), n2=(byte)(n/2);
		double dy=(4*max)/(n*n), dx=0.0, val=0.0;

		for (int i=0; i<m_FeatureMap.length;i++) {
			if ((ri=(byte)(i%n))==0) dx=0;
			else dx+=((ri<=n2)?dy:-dy);
			for (int j=0; j<m_FeatureMap[i].length;j++) {
				if ((rj=(byte)(j%n))==0) val=max;
				else val+=((rj<=n2)?-dx:dx);
				if (val>max) val=max;
				m_FeatureMap[i][j].InitVektor((byte)val);
			}
		}
	}

	/*
	**
	**	InitOutput - Methoden, zum Vorbelegen der Output - Map.
	**
	*/
	public void InitOutputMap(byte val)
	{
		// Initialisiert die Output - Map mit dem festen Wert 'val'.
		for (int i=0; i<m_OutputMap.length;i++)
			for (int j=0; j<m_OutputMap[i].length;j++) 
				m_OutputMap[i][j].InitVektor(val);
	}

	public void InitOutputMap(byte min, byte max)
	{
		// Initialisiert die Output - Map mit Zufallswerten zwischen
		// dem Wert 'min' und dem Wert 'max'.
		for (int i=0; i<m_OutputMap.length;i++)
			for (int j=0; j<m_OutputMap[i].length;j++) 
				m_OutputMap[i][j].InitVektor(min,max);
	}

	/*
	**
	**	Zentrum - Funktion, die das Neuron_v1 mit der besten Aktivit�t
	**	bzgl. dem Eingabevektor ermittelt. Hief�r kommt die GetLevel-
	**	Methode der com.anna.som.NeuroVektor - Klasse zum Einsatz.
	**	
	**
	*/
	public int Zentrum(NeuroVektor input)
	{
		// Berechnet die Koordinaten des Neurons mit der gr��ten 
		// Aktivierungsenergie f�r das Eingabemuster 'muster'.
		int i, j, level, minlevel=Integer.MAX_VALUE;

		for (i=0; i<m_FeatureMap.length; i++)
			for (j=0; j<m_FeatureMap[i].length; j++) {
				// Aktivierungslevel f�r dieses Neuron_v1 berechnen.
				level=input.GetLevel(m_FeatureMap[i][j]);
				if (level<minlevel) {
					// Falls besser wie das bisherige, merken und 
					// altes verwerfen. 
					minlevel=level;
					m_zi=i;
					m_zj=j;
				}
			}

		return minlevel;
		// Falls 'minlevel' 0 ist, dann konnte bzw. wurde Muster zu 
		// 100% gelernt; es existiert ein genaues Zentrum
		// f�r diesen Eingabevektor
	}	

	public int ZentrumI()
	{
		// Gibt die Zeile des Zentrum der letzten Suche zur�ck 
		// (die von der Funktion Zentrum ermittelt wurde). 
		return m_zi;
	}

	public int ZentrumJ()
	{
		// Gibt die Spalte des Zentrums der letzten Suche zur�ck
		// (die von der Funktion Zentrum ermittelt wurde).
		return m_zj;
	}

	/*
	**
	**	LerneMuster - Funktion, die f�r ein Ausgabemuster ein com.anna.som.Eingabe-
	**	muster anhand der aktuellen Lernrate und Varianz lernt.
	**
	*/
	public int LerneMuster(NeuroVektor input, NeuroVektor output)
	{
		// F�hrt einen Lernschritt f�r das Eingabemuster 'input' und
		// das dazugeh�rige Ausgabemuster 'output' durch.
		int level=Zentrum(input), zi=0, zj=0, di, dj, i, j, z;
		float sig2=(m_sig*m_sig)*2, t, eps=(float)Math.sqrt(level+1);
		// Matrixgrenzen berechnen, anhand des Radius (Varianz).
		int iup=m_zi+(int)m_sig, idown=(m_zi<(int)m_sig)?0:(m_zi-(int)m_sig);
		int	jup=m_zj+(int)m_sig, jdown=(m_zj<(int)m_sig)?0:(m_zj-(int)m_sig);
 
		iup=(iup>m_FeatureMap.length)?m_FeatureMap.length:iup;
		jup=(jup>m_FeatureMap[0].length)?m_FeatureMap[0].length:jup;
	
		// Falls Autolernen aktiv ist, ignoriere gesetzte Lernrate in 'm_eps'.
		eps=(m_autoeps)?eps/(eps+10):m_eps;		
		
		for (i=idown; i<iup; i++) {
			di=i-m_zi; di*=di;
			for (j=jdown; j<jup; j++) {
				dj=j-m_zj; dj*=dj;
				t=(float)Math.exp(-((di+dj) / sig2))*eps;
				// Berechne die Gewichte in der Feature - Map neu.
				for (z=0; z<m_FeatureMap[i][j].GetCount();z++) 
					m_FeatureMap[i][j].Add((int)(t*(input.Value(z)-m_FeatureMap[i][j].Value(z))),z);
				// Berechne die Gewichte in der Output - Map neu.
				for (z=0; z<m_OutputMap[i][j].GetCount();z++)
					m_OutputMap[i][j].Add((int)(t*(output.Value(z)-m_OutputMap[i][j].Value(z))),z);
			}
		}
		return level;
		// Je kleiner 'level', um so besser ist das Zentrum ausgebildet
	}

	/*
	// Diese Methode arbeitet nach einem modifiziertem Verfahren,
	// das anhand der Varianz den Radius der Neuronenbeeinflussung
	// ermittelt. 
	public int LerneMuster(com.anna.som.NeuroVektor input, com.anna.som.NeuroVektor output)
	{
		int level=Zentrum(input), zi=0, zj=0, di, dj, i, j, z;
		float sig2=m_sig*2, t, eps=(float)Math.sqrt(level+1);
		// 'r' ist maximaler Radius um das Erregungszentrum 
		int r=(int) Math.round(Math.sqrt(sig2*m_lim)), iup=m_zi+r, jup=m_zj+r;
		int idown=(m_zi<r)?0:(m_zi-r), jdown=(m_zj<r)?0:(m_zj-r);
 
		iup=(iup>m_FeatureMap.length)?m_FeatureMap.length:iup;
		jup=(jup>m_FeatureMap[0].length)?m_FeatureMap[0].length:jup;

		// eps=(m_autoeps)?eps/(eps+6):m_eps;			// 97,4% 100%
		eps=(m_autoeps)?eps/(eps+4):m_eps;				// 97,3% 100%
		// eps=(m_autoeps)?eps/(eps+3):m_eps;			// 96,6% 100%		
		// eps=(m_autoeps)?eps/(eps+(float)2.5):m_eps; 	// 91,3% 99,1%
		// eps=(m_autoeps)?eps/(eps+2):m_eps;			// 97,7% 100%

		for (i=idown; i<iup; i++) {
			di=i-m_zi; di*=di;
			for (j=jdown; j<jup; j++) {
				dj=j-m_zj; dj*=dj;
				t=((float)(di+dj)) / sig2;
				t=(t<m_lim)?(float)Math.exp(-t)*eps:0;
				for (z=0; z<m_FeatureMap[i][j].GetCount();z++) 
					m_FeatureMap[i][j].Add((int)Math.round(t*(input.Value(z)-m_FeatureMap[i][j].Value(z))),z);
				for (z=0; z<m_OutputMap[i][j].GetCount();z++)
					m_OutputMap[i][j].Add((int)Math.round(t*(output.Value(z)-m_OutputMap[i][j].Value(z))),z);
			}
		}
		return level;
		// Je kleiner 'level', um so besser ist das Zentrum ausgebildet
	}
	*/

	/*
	**
	**	Die SucheMuster - Funktion liefert f�r einen Eingabevektor 
	**	einen Ausgabevektor zur�ck.
	**
	*/
	public NeuroVektor SucheMuster(NeuroVektor input)
	{
		// Sucht f�r das Eingabemuster 'input' das passende Ausgabemuster.
		// Nachdem das Zentrum berchnet wurde, werden dessen Koordinaten
		// zur Ermittlung des Ausgabemusters in der Output - Map verwendet.
		int level=Zentrum(input);
		return m_OutputMap[m_zi][m_zj];
	}

	public void drawZentrum(Image img, Color c, NeuroVektor nv, boolean drawOval)
	{
		// Zeichnet eine Markierung an die Stelle im Bild 'img', an der 
		// f�r das Eingabemuster 'nv' das Zentrum berechnet wurde. 
		double neurodim = img.getWidth(null) / m_FeatureMap[0].length;
		int d=(int)(neurodim * 2), d2=(int)neurodim;
		Zentrum(nv);
		int	x=(int)(m_zj*neurodim), y=(int)(m_zi*neurodim); 

		Graphics offgraphics = img.getGraphics();
				
		if (drawOval) {
			offgraphics.setColor(c);
			offgraphics.drawRect(x,y,d,d);
		} else {
			offgraphics.setColor(Color.black);
			offgraphics.fillRect(x-1,y+d2-1,d+3,3);
			offgraphics.fillRect(x+d2-1,y-1,3,d+3);
			offgraphics.setColor(c);
			offgraphics.drawLine(x,y+d2,x+d,y+d2);
			offgraphics.drawLine(x+d2,y,x+d2,y+d);
		}
	}

	public void drawMuster(Image img, Color c, NeuroVektor nv, int lp[][])
	{
		// Zeichnet die Aktivierungsenergien, die das Eingabemuster 'nv' in den
		// einzelnen Neuronen der Feature - Map erzeugt, in das Bild 'img'.
		// Dabei wird durch Farbabstufungen die st�rke der Aktivierung sichtbar.
		// Je kleiner der R�ckgabewert der Funktion GetLevel ist, desto gr��er
		// ist die Aktivierung und desto heller der gezeichnete Farbpunkt.
		double neurodim = img.getWidth(null) / m_FeatureMap[0].length;
		int d=(int)Math.round(neurodim), level; 
		int nr,r=c.getRed(), ng,g=c.getGreen(), nb,b=c.getBlue();

		Graphics offgraphics = img.getGraphics();

		for (int i=0; i<m_FeatureMap.length; i++)
			for (int j=0; j<m_FeatureMap[0].length; j++) {
				level=(int)(Math.sqrt(nv.GetLevel(m_FeatureMap[i][j])));
				if (level<lp[i][j]) {
					// Nur falls der Levelwert gr��er ist als der in der
					// Matrix 'lp', erfolgt eine Ausgabe.
					lp[i][j]=level;
					if		(level<2)	level=0;
					else if (level<5)	level=5;
					else if (level<10)	level=10;
					else if (level<20)	level=20;
					else if	(level<30)	level=30;
					else if (level<40)	level=40;
					else if (level<50)	level=50;
					else if	(level<100)	level=60;
					else if (level<200)	level=75;
					else if	(level<300)	level=90;
					else if (level<400) level=100;
					else if (level<500) level=110;
					else if (level<550)	level=120;
					else if (level<580) level=130;
					else if (level<610) level=140;
					else if (level<650)	level=150;
					else if (level<999)	level=165;
					else				level=255;
					nr=(r<=level)?0:(r-level);
					ng=(g<=level)?0:(g-level);
					nb=(b<=level)?0:(b-level);
					offgraphics.setColor(new Color(nr,ng,nb));
					offgraphics.fillRect((int)(j*neurodim),(int)(i*neurodim),d,d);
				}
			}
	}
}