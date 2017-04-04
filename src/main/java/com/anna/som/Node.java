package com.anna.som;

//
// Klasse		com.anna.som.Node
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Implementiert einen Knoten im skelletierten Bild eines
// Fingerabdrucks. Dieser Knoten stellt ein erkanntes
// Merkmal des Abdrucks dar. Es ist m�glich, Knoten mit
// unterschiedlichen Levels zu speichern, dies entspricht
// dann z.B. einem Linienende, einer Abzweigung, etc.
// Die Datenelemente sind public, so da� sie ohne 
// Zugriffsfunktionen ausgelesen werden k�nnen.
//
class Node
{
	public int x;		// x-Koordinate des Knotens
	public int y;		// y-Koordinate des Knotens
	public int level;	// Level
	public int id;		// ID

	/*
	** com.anna.som.Node Constructor
	**
	** Erzeugt einen neuen Knoten mit den angegebenen
	** Koordinaten und Level. Die ID wird auf -1 gesetzt.
	*/
	public Node(int _x, int _y, int _level)
	{
		x = _x;
		y = _y;
		level = _level;
		id = -1;
	}

	/*
	** com.anna.som.Node
	**
	** Erzeugt einen neuen Knoten mit den angegebenen
	** Koordinaten, Level und ID.
	*/
	public Node(int _x, int _y, int _level, int _id)
	{
		x = _x;
		y = _y;
		level = _level;
		id = _id;
	}
}