package com.anna.som;//
// Klasse		com.anna.som.FingerPrintFrame
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Rahmenfenster, um das com.anna.som.FingerPrint-Applet auch als Application
// ausf�hren zu k�nnen.
//
import java.awt.*;

//==============================================================================
// UNTERST�TZUNG F�R EINZELPLATZANWENDUNGEN
// 	Diese Rahmenklasse hat die Funktion eines Fensters auf der obersten Ebene, in dem das Applet erscheint,
// wenn dieses als Einzelplatzanwendung ausgef�hrt wird.
//==============================================================================
class FingerPrintFrame extends Frame
{
	// com.anna.som.FingerPrint Frame-Konstruktor
	//--------------------------------------------------------------------------
	public FingerPrintFrame(String str)
	{
		// ZU ERLEDIGEN: F�gen Sie hier den zus�tzlichen Code f�r die Konstruktion ein
		super (str);
	}

	// Die Methode handleEvent() empf�ngt alle Ereignisse, die innerhalb des Rahmenfensters generiert 
	// wurden. Mit Hilfe dieser Methode k�nnen Sie auf Fensterereignisse reagieren. Um auf die Ereignisse
	// zu reagieren, die zwar durch Men�s, Schaltfl�chen oder andere Steuerelemente im
	// Rahmenfenster generiert wurden, jedoch nicht durch das Applet verwaltet werden, ist die
	// Methode action() des Fensters zu �berschreiben.
	//--------------------------------------------------------------------------
	public boolean handleEvent(Event evt)
	{
		switch (evt.id)
		{
			// Beenden der Anwendung (z. B.: Anwender hat den Befehl Schlie�en im Systemmen� gew�hlt).
			//------------------------------------------------------------------
			case Event.WINDOW_DESTROY:
				// ZU ERLEDIGEN: Platzieren Sie hier zus�tzlichen Bereinigungscode
				dispose();
				System.exit(0);
				return true;

			default:
				return super.handleEvent(evt);
		}			 
	}
}
