package com.anna.som;//
// Klasse		com.anna.som.MusterSpeicher + Hilfsklassen com.anna.som.Person und com.anna.som.Eingabe
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Die com.anna.som.MusterSpeicher - Klasse �bernimmt die Verwaltung aller com.anna.som.Eingabe-
// und Ausgabemuster und ordnet sie den entsprechnenden Personen zu.
// Der com.anna.som.MusterSpeicher ist also eine Art Datenbank zur Verwaltung s�mt-
// licher personenbezogener Daten im Projekt. Um einen Ausgabevektor des 
// Neuronalen Netztes einer com.anna.som.Person zuordnen zu k�nnen, ermittelt die Klasse
// in einer Funktion den Personennamen, dessen Identifikationsmuster am 
// besten mit dem berechneten Ausgabevektor des Netztes �bereinstimmt. 
// Die Klasse com.anna.som.Person dient hierbei zum Speichern aller notwendigen Personendaten,
// wie z.B. den Namen, einen Farbwert oder das entsprechenden Identifikationsmuster.
// Die com.anna.som.Eingabe - Klasse hingegen speichert alle notwendigen Daten f�r
// einen Eingabevektor. Dies ist unter anderem der Eingabevektor selbst,
// sowie eine Personen - ID und einen Wert, der angibt ob das Muster 
// zum Lernen verwendet werden soll. Zus�tzlich enth�lt die Klasse noch
// ein Textfeld, das einen eindeutigen Namen f�r dieses Muster enthalten 
// mu� und im Projekt zur Speicherung des Dateinamens verwendet wird.

import java.awt.Color;

//
//
// com.anna.som.Person
//
//
class Person
{
	public String		name;			// Name der com.anna.som.Person
	public String		text;			// Zus�tzliche Bemerkungen
	public Color		farbe;			// Farbwert f�r Netzausgaben
	public NeuroVektor	ausgabemuster;	// Identifikationsmuster der com.anna.som.Person
	
	/*
	**
	**	Konstruktoren der Personen - Klasse. 
	**
	*/
	public Person(String n, String t, Color f, byte []muster)
	{
		// Kopiert das Identifikationsmuster 'muster' in eine com.anna.som.NeuroVektor.
		name=n;
		text=t;
		farbe=f;
		ausgabemuster=new NeuroVektor(muster.length);
		ausgabemuster.InitVektor(muster);
	}

	public Person(String n, String t, Color f, NeuroVektor muster)
	{
		// Speichert direkt das �bergebenen Identifikationsmuster.
		name=n;
		text=t;
		farbe=f;
		ausgabemuster=muster;
	}
}

//
//
// com.anna.som.Eingabe
//
//
class Eingabe
{
	public NeuroVektor	eingabemuster;	// Eingabemuster (z.B. von der Bildverarbeitung)
	public String		text;			// Eindeutiger Text, das Muster identifiziert
	public int			personenid;		// Personen - ID, f�r die Zuordnung des Musters
	public boolean		lernen=false;	// Vektor wird vorerst nicht gelernt

	/*
	**
	**	Konstruktor der com.anna.som.Eingabe - Klasse.
	**
	*/
	public Eingabe(NeuroVektor muster, String t, int pid)
	{
		eingabemuster=muster;
		text=t;
		personenid=pid;
	}
}

//
//
// com.anna.som.MusterSpeicher
//
//
public class MusterSpeicher 
{
	private Person m_personen[];	// Array f�r die Personen - Objekte
	private Eingabe m_eingaben[];	// Array f�r die com.anna.som.Eingabe - Objekte

	private int m_personenanzahl,	// Maximale Anzahl von Personen 
				m_eingabeanzahl;	// Maximale Anzahl von Eingabevektoren	
	private int m_personenindex,	// Aktuelle Anzahl von Personen
				m_eingabeindex;		// Aktuelle Anzahl von Eingabevektoren	
	
	private int m_first,			// Summer der ersten Elemente eines 
				m_firstcount=2,		// Ausgabevektors, hier festgelegt auf 2
				m_suchindex;		// Personenindex der letzten Suche
	private float m_prozent;		// Trefferwahrscheinlichkeit der letzten Suche

	/*
	**
	**	Kontruktor der Klasse com.anna.som.MusterSpeicher, zur Initiierung des
	**	Personen- und Eingabearrays. 
	**
	*/
	public MusterSpeicher()
	{
		// Maximal 10 Personen
		m_personenanzahl=10;
		m_personenindex=0;
		m_personen=new Person[m_personenanzahl];

		// Maximal 30 Muster pro com.anna.som.Person
		m_eingabeanzahl=m_personenanzahl*30;
		m_eingabeindex=0;
		m_eingaben=new Eingabe[m_eingabeanzahl];
	}

	public void AnzahlErsteWerte(int count)
	{
		// Legt die Anzahl der Elemente fest, die zur Berechnung von
		// 'm_first' verwendet werden sollen.
		m_firstcount=count;
	}

	/*
	**
	**	Add - Methoden, zum Hinzuf�gen eines Personen - Datensatzes.
	**
	*/
	public void Add(String name, String text, Color c, byte []muster)
	{
		// Das Feld 'muster' wird im com.anna.som.NeuroVektor kopiert.
		m_personen[m_personenindex]=new Person(name,text,c,muster);
		m_personenindex++;
	}

	public void Add(String name, String text, Color c, NeuroVektor muster)
	{
		// Der Vektor 'muster' wird direkt �bernommen, 
		// ohne ihn zu kopieren!
		m_personen[m_personenindex]=new Person(name,text,c,muster);
		m_personenindex++;
	}

	public int AnzahlPersonen()
	{
		// Liefert die aktuelle Anzahl von Personen im Musterspeicher zur�ck.
		return m_personenindex;
	}

	public int GetPersonenIndex(String name)
	{
		// Liefert den Personenindex der com.anna.som.Person mit dem Namen 'name' zur�ck.
		for (int i=0; i<m_personenindex; i++) 
			if (name.equalsIgnoreCase(m_personen[i].name)) return i;
	
		return -1;
	}
	
	public Person GetPerson(int index)
	{
		// Liefert eine Personen - Klasse der com.anna.som.Person mit dem Index 'index' zur�ck.
		return m_personen[index];
	}

	public Person GetPerson(String name)
	{
		// Liefert eine Personen - Klasse der com.anna.som.Person mit dem Namen 'name' zur�ck.
		return m_personen[GetPersonenIndex(name)];
	}
	
	public Color GetColor(int person)
	{
		// Liefert den Farbwert der com.anna.som.Person mit dem Index 'person' zur�ck.
		return m_personen[person].farbe;
	}

	// ****************************************************************
	
	/*
	**
	**	Add - Methode zum Hinzuf�gen eines Eingabevektors.
	**
	*/
	public void Add(NeuroVektor eingabemuster, String text, int personenid)
	{
		// F�gt einen Eingabevektor hinzu.
		m_eingaben[m_eingabeindex]=new Eingabe(eingabemuster, text, personenid);
		m_eingabeindex++;
	}

	public int AnzahlEingaben()
	{
		// Gibt die aktuelle Anzahl von Eingabevektoren zur�ck.
		return m_eingabeindex;
	}

	public Eingabe GetEingabe(String text)
	{
		// Liefert eine com.anna.som.Eingabe anhand des eindeutigen Textes 'text' zur�ck.
		for (int i=0; i < m_eingabeindex; i++)
			if (text.equalsIgnoreCase(m_eingaben[i].text)) return m_eingaben[i];
	
		return null;
	}
	
	public Eingabe GetEingabe(int index)
	{
		// Liefert die com.anna.som.Eingabe mit dem Index 'index' zur�ck.
		return m_eingaben[index];
	}

	// ****************************************************************

	/*
	**
	**	Sucht f�r das Ausgabemuster (berechnet vom NN) die Personen - ID
	**	mit dem �hnlichsten Identifikationsmuster.
	**
	*/
	public int SucheIndex(NeuroVektor muster)
	{
		// Beeintr�chtigt nicht die letzten Ergebnisse der Funktion
		// SucheMuster im Bezug auf Trefferquote, Suchindex und Geschlecht.
		
		int index=-1, level, minlevel=Integer.MAX_VALUE, maxlevel=0;

		for (int i=0; i<m_personenindex; i++) {
			level = muster.GetLevel(m_personen[i].ausgabemuster);
			if (level > maxlevel) maxlevel=level;
			if (level < minlevel) {
				minlevel=level;
				index=i;
			}
		}
		return ((maxlevel==minlevel)?-1:index);
	}

	/*
	**
	**	Gibt nicht wie die vorherige Funktion den Index zur�ck, sondern
	**	gleich den Farbwert der com.anna.som.Person (wird f�r die Netzausgabe ben�tigt).
	**
	*/
	public Color SucheColor(NeuroVektor muster)
	{
		int index=SucheIndex(muster);
		return (index<0)?new Color(0,0,0):m_personen[index].farbe;
	}

	/*
	**
	**	Eigentliche Suche - Funktion des MusterSpeichers, die den Personen-
	**	namen zur�ckgibt, dessen Identifikationsmuster mit dem Ausgabemuster
	**	(des NN) am besten �bereinstimmt.
	**
	*/
	public String SucheMuster(NeuroVektor muster)
	{
		// Bei dem Mustervergleich wird die selbe Methode verwendet,
		// anhand der das Neuronale Netz das Neuron_v1 mit der gr��ten
		// Aktivit�t bzgl. eines Eingabemuster ermittelt.
		int index=-1, level, minlevel=Integer.MAX_VALUE, maxlevel=0;
		m_first=0;

		for (int i=0; i<m_personenindex; i++) {
			level = muster.GetLevel(m_personen[i].ausgabemuster);
			if (level > maxlevel) maxlevel=level;
			if (level < minlevel) {
				minlevel=level;
				index=i;
			}
		}
		
		m_suchindex=index;
		
		// Die Trefferquote wird anhand der minimalen und maximalen Level,
		// die beim Suchenvorgang berechnet wurde, ermittelt 
		m_prozent=(maxlevel>0)?((float)(maxlevel-minlevel)/(float)maxlevel):1;
		
		// Berechnet die Summe der ersten 'm_fistcount' Elemente des Ausgabe-
		// musters. Wird im Projekt zur Bestimmung des Geschlechts verwendet.
		for (int i=0;i<m_firstcount;i++)
			m_first+=muster.Value(i);
		
		return m_personen[index].name;
	}

	// ****************************************************************

	public int Index()
	{
		// Gibt den Personenindex der letzten Suche zur�ck.
		return m_suchindex;
	}

	public float Trefferquote()
	{
		// Gibt die Trefferquote der letzten Suche zur�ck.
		return m_prozent;
	}

	public int SummeErsterWerte()
	{
		// Gibt die Summe der ersten 'm_firstcount' Elemente, der letzen
		// Suche zur�ck. Anhand dieses Werts wird sp�ter im Projekt das 
		// Geschlecht der gefundenen com.anna.som.Person ermittelt. Hierf�r besitzen
		// die Identifikationsmuster der m�nnlichen Personen f�r die ersten
		// beiden Vektorelemente positive Werte, hingegeg die weibliche 
		// Personen negative Werte enthalten (Fraglich ist, ob �berhaupt 
		// Merkmale vorhanden sind, die darauf schlie�en lassen, da� es 
		// sich um eine m�nnliche oder weibliche com.anna.som.Person handelt ;-)).
		return m_first;
	}
}

