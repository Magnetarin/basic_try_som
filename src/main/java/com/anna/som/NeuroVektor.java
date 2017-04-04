package com.anna.som;//
// Klasse		com.anna.som.NeuroVektor
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Die Klasse com.anna.som.NeuroVektor wird f�r mehrere Zwecke 'mi�braucht'. Die
// Hauptaufgabe der Klasse ist die Repr�sentation eines Neurons im
// Kohonennetz (com.anna.som.KohonenNetz). Hierf�r speichert sie in einem Array
// alle Gewichtswerte ab, die dann in den jeweiligen Lernschritten 
// angepa�t werden. Zus�tzlich dient die Klasse auch als com.anna.som.Eingabe-
// und Ausgabevektor, da diese ja im wesentlichen nichts anderes dar-
// stellen als ein Gewichtswektor im Neuron_v1 (zumindestens rein
// speichertechnisch). Die Zentrale Methode der Klasse ist die GetLevel-
// Funktion, die einen Wert f�r die �hlichkeit zweier Vektoren zur�ckliefert.

import java.lang.Math;

public class NeuroVektor 
{
	private byte m_feld[];		// Speicher f�r die einzelen Gewichte
	private int m_counter;		// Z�hlindex
	
	/*
	**
	** Konstruktor der Klasse, der im wesentlichen nur das Array f�r 
	** die spezifische Anzahl von Elementen 'dim' reserviert.
	**
	*/
	public NeuroVektor(int dim)
	{
		m_feld = new byte[dim];
	}

	/*
	**
	** Kopiekonstruktor f�r NeuroVektoren
	**
	*/
	public NeuroVektor(NeuroVektor nv)
	{
		// Initiiert das Array anhand der Gr��e des �bergebenen Vektors.
		m_feld = new byte[nv.GetCount()];

		for (int i=0; i<nv.GetCount(); i++)
			m_feld[i]=nv.m_feld[i];

		m_counter=nv.m_counter;
	}

	/*
	**
	**	InitVektor - Methoden zum initialisieren des Vektors mit Anfangswerten.
	**
	*/
	public void InitVektor(byte val)
	{
		// Initiieren mit festem Wert 'val'.
		for (int i=0; i<m_feld.length; i++)
			m_feld[i]=val;
	}

	public void InitVektor(byte min, byte max)
	{
		// Initiieren mit Zufallswerten im Bereich von min - max.
		for (int i=0; i<m_feld.length; i++)
			m_feld[i]=(byte)Math.round(min + Math.random()*(max-min));
	}

	public void InitVektor(byte []feld)
	{
		// Initialisierung anhand eines Byte - Arrays.
		for (int i=0; i<m_feld.length; i++)
			m_feld[i]=feld[i];
	}

	public void InitVektor(int p, byte max)
	{
		// Sinusf�rmige Initiierung des Vektors.
		double k = (Math.PI*2)/p;
		
		for (int i=0; i<m_feld.length; i++)
			m_feld[i]=(byte)(Math.sin(k*(i % p))*max);
	}

	public int GetCount()
	{
		// Gibt die Anzahl von Elementen im Vektor zur�ck (Dimension).
		return m_feld.length;
	}

	public int GetUpperBound()
	{
		// Liefert den gr��ten g�ltigen Index zur�ck.
		return m_feld.length-1;
	}

	public byte Value(int index)
	{
		// Liefert den Wert des Elements mit dem Index 'index' zur�ck.
		return m_feld[index];
	}

	public byte Value()
	{
		// Liefert den Wert des Elements auf den der Z�hlindex momentan zeigt. 
		return m_feld[m_counter];
	}

	public void Set(byte val, int index)
	{
		// Setzt den Wert des Elements mit dem Index 'index' auf den Wert 'val'.
		m_feld[index]=val;
	}

	public void Set(byte val)
	{
		// Setzt den Wert des Elements auf den der Z�hlindex momentan zeigt
		// auf den Wert 'val'.
		m_feld[m_counter]=val;
	}

	public byte Add(int val)
	{
		// Addiert zum Element auf den der Z�hlindex momentan zeigt den Wert 'val'.
		val+=m_feld[m_counter];
		m_feld[m_counter]=(byte)((val>127)?127:(val<-128)?-128:val); // (byte)(val+m_feld[m_counter]);
		return m_feld[m_counter];
	}

	public byte Add(int val, int index)
	{
		// Addiert zum Element mit dem Index 'index' den Wert 'val'.
		val+=m_feld[index];
		m_feld[index]=(byte)((val>127)?127:(val<-128)?-128:val); // (byte)(val+m_feld[index]);
		return m_feld[index];
	}

	public void InitCounter(int start)
	{
		// Initiialisiert den Z�hler mit dem Wert 'start'.
		m_counter=start;
	}

	public int GetNext() 
	{
		// Erh�ht den Z�hler um 1, bis er das Ende erreicht hat (-1: eof).
		m_counter++;
		if (m_counter>=m_feld.length) m_counter=-1;
		return m_counter;
	}

	public boolean IsEof()
	{
		// Pr�ft ob der Z�hler bereits auf 'eof' steht.
		if (m_counter==-1) return true;
		else return false;
	}

	/*
	**
	**	Die zentrale Funktion GetLevel der Klasse, die das Quadrat der
	**	euklidischen Distanz zweier Vektoren berechnet. 
	**	Je kleiner dieser Wert ist, desto '�hnlicher' sind sich die Vektoren. 
	**
	*/ 
	public int GetLevel(NeuroVektor w)
	{
		// Berechnet anhand des Neurovektors 'v' die Aktivierung.
		// Es wird das Quadrat der Euklidischen Distanz zur�ckgeliefert.
		int level = 0, tmp;
		

		for (int i=0; i<m_feld.length; i++) {
			tmp=m_feld[i]-w.m_feld[i];
			level+=tmp*tmp;
		}

		return level;
	}
	
	public int Skalar(NeuroVektor nv)
	{
		// Berechnet das Skalarprodukt aus dem eigen und dem �bergebenen 
		// com.anna.som.NeuroVektor 'nv'
		int s = 0;

		for (int i=0; i<m_feld.length; i++)
			s+=m_feld[i]*nv.m_feld[i];
		
		return s;
	}

	public int GetSignDifference(NeuroVektor nv)
	{
		// Ermittelt die Anzahl der Vorzeichenunterschiede zwischen
		// diesem und dem �bergebenem com.anna.som.NeuroVektor 'nv'.
		int signdif=0;

		for (int i=0; i<m_feld.length; i++)
			if ((m_feld[i]<0 && nv.m_feld[i]>0) || (m_feld[i]>0 && nv.m_feld[i]<0))
				signdif++;

		return signdif;
	}

	public String toString()
	{
		// Transformiert den Vektor in einen String, so da� er ausgegeben werden kann.
		String temp = "", val="";

		for (int i=0; i<m_feld.length; i++) {
			val=String.valueOf(m_feld[i]);
			temp=temp + val;
			for (int z=0; z<(5-val.length());z++) 
				temp=temp + " ";
		}
		return temp;
	}
}