package com.anna.som;//
// Klasse		com.anna.som.NetzTester
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Die Netztester - Klasse bildet die Schnittstelle zwischen dem
// Neuronalen Netz und der Anwendung. Es �bernimmt die Erzeugung und
// Initialisierung der com.anna.som.KohonenNetz- und com.anna.som.MusterSpeicher - Klasse und
// stellt deren Funktionalit�t durch Schnittstellenfunktionen f�r 
// die Anwendung zur Verf�gung. Zus�tzlich implementiert sie eine 
// Lerne - Methode, die das Lernen aller markierten Eingabevektoren
// ausf�hrt. 

import java.awt.*;

public class NetzTester

{
	KohonenNetz m_netz;			// Das eigentliche Neuronale Netz
	MusterSpeicher m_speicher;	// Datenbank zur Zuordnung der 
								// Ausgabemuster zu Personennamen  

	// Definiton der Identifikationsmuster (ersten 2 Bytes bestimmen das Geschlecht).
	// Die Codes wurden mit einer Hamming - Distanz von 7 erstellt (ohne die beiden 1 Bytes).
	byte m_ausgabe[][]={{+120,+120,-100,-100,+100,+100,+100,-100,+100,-100,-100,-100,+100,-100,+100,+100},
						{+120,+120,-100,+100,-100,+100,+100,+100,-100,-100,-100,+100,+100,+100,+100,-100},
						{+120,+120,-100,+100,+100,-100,-100,+100,+100,-100,+100,-100,-100,+100,+100,-100},
						{-120,-120,+100,-100,-100,+100,+100,-100,-100,+100,+100,-100,+100,+100,-100,+100},
						{-120,-120,+100,+100,-100,-100,-100,+100,-100,+100,+100,+100,-100,-100,+100,+100}};
						
	// Definition der Personennamen
	String m_namen[]={"Christian","Peter","Sauer","Liane","Andrea"};

	// und zus�tzlichen Farbwerten f�r sp�tere Netzausgaben.
 	Color m_color[]={Color.cyan.brighter(),Color.green.brighter(),Color.pink,Color.red,Color.yellow};

	int m_netzdim=50;			// Dimension der Feature - Map
	int m_eingabedim=100;		// Standarddef. f�r die L�nge eines Eingabevektors (wird sp�ter ver�ndert)
	int m_ausgabedim=m_ausgabe[0].length;
								// Dimension der Ausgabevektoren	
	int m_lernschritte=25;		// Anzahl der Lernschritte
	float m_mineps=(float)0.1,	// Minimale Lernrate
		  m_maxeps=(float)1,	// Maximale Lernrate
		  m_minsig=1,			// Minimaler Radius (Varianz)
		  m_maxsig=10;			// Maximaler Radius (Varianz)


	/* 
	**
	** Konstruktor der com.anna.som.NetzTester - Klasse, dem als Parameter die
	** Dimension der Eingabevektoren �bergeben wird.
	**	
	**/
	public NetzTester(int eingabedim)
	{
		// Erzeugt ein Kohonennetz f�r Eingabevektoren 
		// der Dimension 'eingabedim'
		
		m_eingabedim=eingabedim;
		m_netz = new KohonenNetz(m_netzdim,m_eingabedim,m_ausgabedim);

		m_netz.InitFeatureMap();
		m_netz.InitOutputMap((byte)0);
		
		m_speicher = new MusterSpeicher();
		
		// Musterspeicher (Datenbank) mit Namen, Ausgabemuster 
		// und Farbwerten initialisieren
		for (int i=0; i<m_ausgabe.length; i++)
			m_speicher.Add(m_namen[i],"",m_color[i],m_ausgabe[i]);
	}

	/*
	**
	**	LoescheNetz - Methode, zum 'vergessen' der bereits gelernten Muster
	**
	*/
	public void LoescheNetz()
	{
		// Initialisiert die Feature- und Ausgabe - Map mit Anfangswerten
		m_netz.InitFeatureMap();
		m_netz.InitOutputMap((byte)0);
	}

	/*
	**
	**	Lernschritte - Methode, zur Einstellung der Anzahl von Lernschritten
	**
	*/
	public void Lernschritte(int l)
	{
		m_lernschritte=l;
	}

	public int Lernschritte()
	{
		// Gibt die momentane Anzahl von Lernschritten zur�ck
		return m_lernschritte;
	}

	/*
	**
	**	Die AutoLernrate - Methode bestimmt, ob das Netz bei jedem Lernschritt
	**	eine konstante Lernrate verwendet oder die Lernrate in Abh�ngigkeit
	**	von der Intensit�t in der das jeweilige Eingabemusters bereits 
	**	gelernt wurde variiert.  
	**
	*/
	public void AutoLernrate(boolean l)
	{
		m_netz.AutoLernrate(l);
	}
	
	public boolean AutoLernrate()
	{
		return m_netz.AutoLernrate();
	}

	/*
	**
	**	Die Varianz - Methode setzt den Varianzbereich (Radius), der w�hrend
	**	des Lernens vom Maximum zum Minimum hin verringert wird.
	**
	*/
	public void Varianz(float min, float max)
	{
		// Setzt die minimale und maximale Varianz (Radius).
		m_minsig=min;
		m_maxsig=max;
	}

	public float Varianz(boolean min)
	{
		// Gibt den Wert der minimalen bzw. maximalen Varianz zur�ck.
		return (min==true)?m_minsig:m_maxsig;
	}

	/*
	**
	**	Die Lernrate - Methode setzt den Lernratenbereich, der w�hrend
	**	des Lernens vom Maximum zum Minimum hin verringert wird.
	**
	*/
	public void Lernrate(float min, float max)
	{
		// Setzt die minimale und maximale Lernrate.
		m_mineps=min;
		m_maxeps=max;
	}

	public float Lernrate(boolean min)
	{
		// Gibt den Wert der minimalen bzw. maximalen Lernrate zur�ck.
		return (min)?m_mineps:m_maxeps;
	}

	public int GetPersonenIndex(String name)
	{
		// Liefert den Personenindex f�r den Namen 'name' zur�ck.
		return m_speicher.GetPersonenIndex(name);
	}

	public Person GetPerson(int index)
	{
		// Liefert eine Referenz auf eine Personen - Klasse der com.anna.som.Person
		// mit dem Index 'index' zur�ck.
		return m_speicher.GetPerson(index);
	}

	public Person GetPerson(String name)
	{
		// Liefert eine Referenz auf eine Personen - Klasse der com.anna.som.Person
		// mit dem Namen 'name' zur�ck.
		return m_speicher.GetPerson(name);
	}
	
	public Eingabe GetEingabe(String text)
	{
		// Liefert eine Referenz auf eine com.anna.som.Eingabe - Klasse mit dem
		// Text 'text' zur�ck
		return m_speicher.GetEingabe(text);
	}

	/*
	**
	**	Anhand der Add - Methode wird ein Eingabemuster in der Datenbank
	**	der com.anna.som.MusterSpeicher - Klasse aufgenommen und einer com.anna.som.Person zugewiesen
	**
	*/
	public void Add(NeuroVektor eingabe, String text, int personenid)
	{
		// F�gt ein Eingabemuster 'eingabe' mit dem Namen 'text' hinzu
		// und ordnet es der com.anna.som.Person mit dem Index 'personenid' zu.
		m_speicher.Add(eingabe, text, personenid);
	}

	/*
	**
	**	�ber die MarkiereMuster - Methode kann ein Eingabemuster f�r das
	**	Lernen markiert werden.
	*/
	public void MarkiereMuster(String text, boolean lernen)
	{
		// Markiert das Eingabemuster mit dem Text 'text' zum 
		// lernen (lernen=true).
		m_speicher.GetEingabe(text).lernen=lernen;
	}

	public void LoescheMarkierungen()
	{
		// Setzt alle zum Lernen markierten Vektoren zur�ck.
		for (int i=0; i<m_speicher.AnzahlEingaben(); i++) 
			m_speicher.GetEingabe(i).lernen=false;			
	}

	/*
	**
	**	Die LerneMuster - Funktion lernt f�r eine com.anna.som.Person einen Eingabevektor
	**
	*/
	public int LerneMuster(NeuroVektor nv, int index)
	{
		// com.anna.som.NeuroVektor 'nv' gibt den zu lernenden Eingabevektor an,
		// 'index' den dazugeh�rigen Index der com.anna.som.Person.
		// Es sind 5 Namen bereits vorgegeben:
		// drei m�nnliche: Christian(0), Peter(1), Sauer(2)
		// zwei weibliche: Liane(3), Andrea(4)
		
		// Falls der R�ckgabewert 0 ist, kann davon ausgegangen werden,
		// da� das Muster zu 100% gelernt wurde - Vorsicht, nicht ganz richtig!

		return m_netz.LerneMuster(nv,m_speicher.GetPerson(index).ausgabemuster);
	}

	/*
	**
	**	Die Lernen - Funktion lernt nun alle zum lernen markierten com.anna.som.Eingabe-
	**	vektoren und zwar so oft, wie f�r die Anzahl der Lernschritte ge- 
	**	speichert wurde. Nach jedem Lernschritt wird die Lernrate und 
	**	die Varianz neu berechnet.
	*/
	public int Lernen(Label debug, int l)
	{
		// �bergabe des aktuellen Lernschritts 'l', der um 4 Schritte
		// erh�ht und dann zur�ckgegeben wird.
		// Die Lernefunktion f�hrt somit maximal 4 Lernschritte aus
		// und gibt die Kontrolle dann wieder zur�ck an Hauptprogramm.
		// Dieses hat dann z.B. die M�glichkeit die Netzbilder f�r diese
		// Lernstadium neu zu berechnen.
		Person p;
		Eingabe e;
		int level, m=m_speicher.AnzahlEingaben(), flag=0, ml=l+4;
		
		for (; (l<ml) && (l<m_lernschritte); l++) {
			// Berechnung der Varianz
			m_netz.Varianz(Math.round(m_maxsig*(Math.pow(m_minsig/m_maxsig,l/m_lernschritte))));
			for (int i=0; i<m; i++) {
				e=m_speicher.GetEingabe((flag==0)?i:(m-1)-i);
				if (e.lernen) {
					// Muster ist zum lernen markiert.	
					p=m_speicher.GetPerson(e.personenid);
					level=m_netz.LerneMuster(e.eingabemuster,p.ausgabemuster);
					if (debug!=null) debug.setText((l+1) + ". Lernschritt: Lerne Muster '" + e.text + "' von '" + p.name + "'");
				}
			}
			// Die Lernrate und die Varianz nehmen nach jedem Lernschritt monoton ab,
			// im Bereich von 'm_maxeps - m_mineps' bzw. 'm_maxsig -m_minsig'.
			m_netz.Lernrate((float)(m_maxeps*(Math.pow(m_mineps/m_maxeps,l/m_lernschritte))));
			flag=1-flag;
		}
		
		return l;
	}

	/*
	**
	**	Die SucheMuster - Funktionen suchen f�r einen Eingabevektor den 
	**	passenden Personennamen in der com.anna.som.MusterSpeicher - Klasse.
	**
	*/
	public String SucheMuster(NeuroVektor nv)
	{
		// Sucht zu 'nv' den passenden Ausgabevektor und 
		// gibt den daf�r gespeicherten Namen zur�ck.
	
		return m_speicher.SucheMuster(m_netz.SucheMuster(nv));
	}

	public String SucheMuster(String text)
	{
		// Sucht zum Eingabemuster mit dem Text 'text' den passenden
		// Ausgabevektor und gibt den daf�r gespeicherten Namen zur�ck.
		return m_speicher.SucheMuster(m_netz.SucheMuster(m_speicher.GetEingabe(text).eingabemuster));
	}

	public float Trefferquote()
	{
		// Gibt die Trefferquote (in %) f�r den letzten Suchvorgang zur�ck.
		return m_speicher.Trefferquote()*100;
	}

	public int Geschlecht()
	{
		// Gibt das Geschlecht f�r die zuletzt gefundene com.anna.som.Person an:
		// R�ckgabewert <  0: weiblich
		// R�ckgabewert >= 0: m�nnlich

		// Der Wert wird anhand der Summe der ersten Elemente des Ausgabe-
		// vektors bestimmt. Hier sind dies die ersten beiden Elemente, die
		// f�r m�nnliche Personen beide negativ sind und f�r weibliche 
		// Personen positiv. 
		return m_speicher.SummeErsterWerte();
	}

	public void drawZentrum(Image img, NeuroVektor nv, int index, boolean drawOval)
	{
		// Zeichnet in das Bild 'img' das Zentrum f�r das 
		// Muster 'nv' und der com.anna.som.Person mit dem Index 'index'.
		// Mit 'drawOval=true' wird das Zentrum als Kreis dargestelle,
		// ansonsten als Kreuz.
		m_netz.drawZentrum(img,m_speicher.GetColor(index),nv,drawOval);
	}

	public void drawZentrum(Image img, String text, boolean drawOval)
	{
		// Zeichnet in das Bild 'img' das Zentrum f�r das Muster
		// mit der Bez. 'text'. Mit 'drawOval=true' wird das Zentrum  
		// als Kreis dargestellt, ansonsten als Kreuz.
		Eingabe e = GetEingabe(text);
		m_netz.drawZentrum(img,m_speicher.GetColor(e.personenid),e.eingabemuster,drawOval);
	}

	public void drawZentren(Image img, int person)
	{
		// Zeichnet alle Zentren (der gelernten Muster) einer com.anna.som.Person in das Bild 'img'.
		Eingabe e;

		for (int i=0; i<m_speicher.AnzahlEingaben(); i++) {
			e=m_speicher.GetEingabe(i);	
			if (e.lernen && e.personenid==person) 
				m_netz.drawZentrum(img,m_speicher.GetColor(person),e.eingabemuster,true);
		}
	}

	public void drawZentren(Image img)
	{
		// Zeichnet alle Zentren aller gelernten Muster in das Bild 'img'
		Eingabe e;

		for (int i=0; i<m_speicher.AnzahlEingaben(); i++) {
			e=m_speicher.GetEingabe(i);	
			if (e.lernen) 
				m_netz.drawZentrum(img,m_speicher.GetColor(e.personenid),e.eingabemuster,true);
		}
	}

	public void drawMuster(Image img, String text)
	{
		// Zeichnet die Aktivierungsaktivit�ten der Neuronen durch das 
		// Eingabemusters mit der Bez.'text' durch Farbabstufungen 
		// in das Bild 'img'. Je heller der Punkt im Bild erscheint, um so
		// st�rker wird das Neuron_v1 von dem Eingabemuster aktiviert.
		int d=m_netz.GetMapDim(), levelpuffer[][]=new int[d][d];

		for (int i=0; i<d; i++)
			for (int j=0; j<d; j++)
				levelpuffer[i][j]=Integer.MAX_VALUE;

		Eingabe e=m_speicher.GetEingabe(text);
		m_netz.drawMuster(img,m_speicher.GetColor(e.personenid),e.eingabemuster, levelpuffer);
	}

	public void drawMuster(Image img, int p)
	{
		// Zeichnet die Aktivierungsaktivit�ten der Neuronen f�r alle 
		// gelernten Muster einer com.anna.som.Person mit dem Index 'p' in das Bild 'img'.
		// Das Problem hier ist die �berschneidung der einzelnen Zentren,
		// so das es teilweise zu �berlagerungen kommen kann.
		Eingabe e;
		int d=m_netz.GetMapDim(), levelpuffer[][]=new int[d][d];

		// Erzeugung einer Matrix, die die Levelwerte aufnimmt.
		// Ein Muster darf nur dann an den Stellen im Bild etwas �ndern,
		// an denen es einen niedriegeren Levelwert besitzt als der in 
		// der Matrix 'levelpuffer'.
		for (int i=0; i<d; i++)
			for (int j=0; j<d; j++)
				levelpuffer[i][j]=Integer.MAX_VALUE;
		
		for (int i=0; i<m_speicher.AnzahlEingaben(); i++) {
			e=m_speicher.GetEingabe(i);
			if (e.lernen && e.personenid==p)
				m_netz.drawMuster(img,m_speicher.GetColor(e.personenid),e.eingabemuster, levelpuffer);
		}
	}

	public void drawMuster(Image img)
	{
		// Zeichnet die Aktivierungsaktivit�ten der Neuronen aller 
		// gelernten Muster aller Personen durch Farbabstufungen in
		// das Bild 'img'.
		// Hier tritt das selbe Problem auf, das bereits in der 
		// vorherigen drawMuster - Methode erl�utert wurde.
		
		Eingabe e;
		int d=m_netz.GetMapDim(), levelpuffer[][]=new int[d][d];

		for (int i=0; i<d; i++)
			for (int j=0; j<d; j++)
				levelpuffer[i][j]=Integer.MAX_VALUE;

		for (int i=0; i<m_speicher.AnzahlEingaben(); i++) {
			e=m_speicher.GetEingabe(i);
			if (e.lernen)
				m_netz.drawMuster(img,m_speicher.GetColor(e.personenid),e.eingabemuster,levelpuffer);
		}
	}

	public void drawAusgabeMap(Image img)
	{
		// Zeichnet in das Bild 'img' f�r alle Personen eine Fl�che, 
		// innerhalb der die jeweilige com.anna.som.Person in der Ausgabemap erkannt wird.
		double neurodim = img.getWidth(null) / m_netz.GetMapDim();
		int d = (int) Math.round(neurodim);
		Graphics offgraphics = img.getGraphics();
		
		for (int i=0; i<m_netz.GetMapDim(); i++)
			for (int j=0; j<m_netz.GetMapDim(); j++) {
				offgraphics.setColor(m_speicher.SucheColor(m_netz.GetOutputVektor(i,j)));
				offgraphics.fillRect((int)(j*neurodim),(int)(i*neurodim),d,d);
			}
	}
}