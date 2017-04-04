package com.anna.som;
//
// Klasse		com.anna.som.FingerPrint
//
// Projekt		com.anna.som.FingerPrint
//
// Written by Christian Birzer, Peter S�llner Juni 1998
//
// Implementiert das Applet com.anna.som.FingerPrint mit dem GUI und der
// Bedienerf�hrung.
//
import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Date;


public class FingerPrint extends Applet
{
	// UNTERST�TZUNG F�R EINZELPLATZANWENDUNGEN:
	//		m_fStandAlone wird auf true gesetzt, falls das Applet eigenst�ndig ausgef�hrt wird
	//--------------------------------------------------------------------------
	private boolean m_fStandAlone = false;
	private Image SourceImg, DestImg, NetImg[];
	private PictureCanvas pc2 = new PictureCanvas();
	private PixelArray SourceData;
	private PixelArray DestData;
	private Button BuEdge, BuFind, BuThin, BuStart, BuLearn, BuSearch, BuForget,
					BuAllV, BuNoneV, BuInvertV, BuDelV, BuAllI, BuNoneI, BuInvertI;
	private Label MsgLabel, InputLabel, VectorLabel, FilterLabel, StepsLabel, 
					MinEpsLabel, MaxEpsLabel, MinSigLabel, MaxSigLabel;
	private CheckboxGroup FilterGroup;
	private Checkbox FilterCheck1, FilterCheck2, FilterCheck3, AutoLearn;
	private TextField MinEpsText, MaxEpsText, MinSigText, MaxSigText;
	private Choice StepsChoice;
	private int w, h;
	private LineFinder lf;
	private List FingerPrintList, VectorList;
	private NeuroVektor Vector;
	private String PictureName;
	private NetzTester Netz;
	private TextArea DebugOut;
	private int JavaBugWorkaround;

	// UNTERST�TZUNG F�R EINZELPLATZANWENDUNGEN
	//	Die Methode main() stellt den Einsprungpunkt des Applets dar, wenn es als eigenst�ndige
	// Anwendung ausgef�hrt wird. Diese wird ignoriert, falls das Applet innerhalb
	// einer HTML-Seite ausgef�hrt wird.
	//--------------------------------------------------------------------------
	public static void main(String args[])
	{
		// Erstellen sie ein Fenster auf der obersten Ebene, das das Applet com.anna.som.FingerPrint enth�lt
		//----------------------------------------------------------------------
		FingerPrintFrame frame = new FingerPrintFrame("com.anna.som.FingerPrint");

		// Rahmen (Frame) muss vor dem Dimensionieren angezeigt werden, damit insets() g�ltige Werte zur�ckgibt
		//----------------------------------------------------------------------
		frame.show();
		frame.hide();
		frame.resize(frame.insets().left + frame.insets().right  + 830,
					 frame.insets().top  + frame.insets().bottom + 1000);

		// Der folgende Code startet die Ausf�hrung des Applets innerhalb des Rahmenfensters.
		// Hier wird auch GetParameters() aufgerufen, um die Parameterwerte der
		// Befehlszeile abzurufen, und m_fStandAlone auf true gesetzt, um zu verhindern,
		// dass init() diese Werte aus der HTML-Seite holt.
		//----------------------------------------------------------------------
		FingerPrint applet_FingerPrint = new FingerPrint();

		frame.add("Center", applet_FingerPrint);
		applet_FingerPrint.m_fStandAlone = true;
		applet_FingerPrint.init();
		applet_FingerPrint.start();
		frame.show();
	}

	// com.anna.som.FingerPrint Klassen-Konstruktor
	//--------------------------------------------------------------------------
	public FingerPrint()
	{
		// ZU ERLEDIGEN: Platzieren Sie hier Code f�r den Konstruktor
	}

	// APPLET-INFO-UNTERST�TZUNG:
	//		Die Methode getAppletInfo() gibt eine Zeichenfolge zur�ck, die Autor/Autorin,
	// Copyright-Datum oder verschiedene andere Informationen des Applets beschreibt
	//--------------------------------------------------------------------------
	public String getAppletInfo()
	{
		return "Name: com.anna.som.FingerPrint\r\n" +
			   "Autor/Autorin: Christian Birzer\r\n" +
			   "Erstellt mit Microsoft Visual J++ Version 1.1";
	}

	/*
	** LoadImage
	**
	** L�dt das angegebene Bild in SourceImg. Es wird versucht, allen Speicherm�ll
	** freizugeben. Funktioniert allerdings nicht. Nach 40 Aufrufen dieser Funktion
	** sind 16MB weg :-((
	*/
	private void LoadImage(String name)
	{
		SourceImg = null;
		Runtime.getRuntime().gc();
		MediaTracker pictureTracker = new MediaTracker(this);
		SourceImg = getImage(getDocumentBase(), name);
		pictureTracker.addImage(SourceImg,0);
		try {
			pictureTracker.waitForAll();
		} catch (InterruptedException e) {
			getAppletContext().showStatus("Oops, could not load Image");
		}
		pictureTracker = null;
	}

	/*
	** GetImageData
	**
	** Kopiert die Bilddaten aus dem SourceImg in den SourceData
	** Pixel-Array.
	*/
	private void GetImageData()
	{
		PixelGrabber grabber = new PixelGrabber(SourceImg, 0, 0, w, h, SourceData.Pixels, 0, w);
		try {
			grabber.grabPixels();
		} catch (InterruptedException e) {
			getAppletContext().showStatus("Oops, could not grab Pixels");
		}
		if	((grabber.status()	&  ImageObserver.ABORT)  !=  0)  {
			  getAppletContext().showStatus("image  fetch  aborted  or  errored");
		}

	}

	/*
	** SetImageData
	**
	** Erzeugt ein neues Bild aus den Bilddaten, die im Data-Array
	** angegeben wurden (sollte eigentlich ein com.anna.som.PixelArray sein...)
	** Falls scaled true ist, wird das Bild um 50% in jeder Richtung
	** verkleinert.
	*/
	private Image SetImageData(int Data[], boolean scaled)
	{
		Image Img;
		int BufferData[] = new int[(w/2) * (h/2)];
		int x, y;

		if(scaled) {
			for(x=0;x<w/2;x++) {
				for(y=0;y<h/2;y++) {
					BufferData[y*w/2+x] = Data[y*2*w+x*2];
				}
			}
			Img = createImage(new MemoryImageSource(w/2, h/2, BufferData, 0, w/2));
		} else {
			Img = createImage(new MemoryImageSource(w, h, Data, 0, w));
		}
		return Img;
	}


	//Die Methode init() wird vom AWT aufgerufen, wenn ein Applet erstmals geladen oder
	//neu geladen wird. �berschreiben Sie diese Methode, um jede Initialisierung auszuf�hren,
	//die das Applet ben�tigt (z. B. die Initialisierung von Datenstrukturen, das Laden von Bildern oder
	//Schriftarten, das Erstellen von Rahmenfenstern, das Festlegen des Layout-Managers oder das Hinzuf�gen von
	//Komponenten der Benutzeroberfl�che).
	//--------------------------------------------------------------------------
	public void init()
	{
		int i;
		Panel PanelNorth, PanelWest, DummyPanel;
		GridBagLayout WestLayout;
		GridBagConstraints Constraint;
		Graphics og;

		//Wenn Sie eine mit Hilfe des Ressourcen-Assistenten generierte Steuerelement-Erstellungklasse verwenden,
		//um die Steuerelemente in Ihrem Applet anzuordnen, k�nnen Sie dessen Methode
		//CreateControls() von dieser Methode aus aufrufen. Entfernen Sie dazu den folgenden
		//Aufruf von resize(), bevor Sie den Aufruf von CreateControls() einf�gen.
		//CreateControls() f�hrt eine eigene Gr��en�nderung durch.
		//----------------------------------------------------------------------
		resize(830,1000);

		NetImg = new Image[3];
		NetImg[0] = createImage(200,200);
		NetImg[1] = createImage(200,200);
		NetImg[2] = createImage(200,200);

		// Netz-Ausgabe-Bilder l�schen:
		for(i=0;i<3;i++) {
			og = NetImg[i].getGraphics();
			og.setColor(Color.black);
			og.fillRect(0,0,NetImg[i].getWidth(null)-1,NetImg[i].getHeight(null)-1);
		}


		LoadImage("Peter_0.gif");
		pc2.SetImage(SourceImg);
		Netz = new NetzTester(722);

		/*
		** GUI-Elemente erstellen:
		*/ 
		BuEdge = new Button("Kanten finden");
		BuFind = new Button("Linien finden");
		BuThin = new Button("Linien ausd�nnen");
		BuStart = new Button("Bildbearbeitung starten");
		BuLearn = new Button("Lernen");
		BuSearch = new Button("Suchen");
		BuForget = new Button("Vergessen");
		BuAllV = new Button("Alles");
		BuNoneV = new Button("Nichts");
		BuInvertV = new Button("Invertieren");
		BuDelV = new Button("Alle l�schen");
		BuAllI = new Button("Alles");
		BuNoneI = new Button("Nichts");
		BuInvertI = new Button("Invertieren");
		FingerPrintList = new List(8,true);
		VectorList = new List(8,true);
		MsgLabel = new Label("Bereit!");
		InputLabel = new Label("Eingabebilder:");
		VectorLabel = new Label("Vektoren:");
		DebugOut = new TextArea(10,50);
		FilterGroup = new CheckboxGroup();
		FilterCheck1 = new Checkbox("Schwellwert", FilterGroup, true);
		FilterCheck2 = new Checkbox("Sobel vertikal", FilterGroup, false);
		FilterCheck3 = new Checkbox("Logarithmisch", FilterGroup, false);
		FilterLabel = new Label("Kantendetektionsverfahren:");
		StepsLabel = new Label("Anzahl Lernschritte:");
		StepsChoice = new Choice();
		MinEpsText = new TextField();
		MaxEpsText = new TextField();
		MinSigText = new TextField();
		MaxSigText = new TextField();
		MinEpsLabel = new Label("Min. Lernrate:");
		MaxEpsLabel = new Label("Max. Lernrate:");
		MinSigLabel = new Label("Min. Varianz:");
		MaxSigLabel = new Label("Max. Varianz:");
		AutoLearn = new Checkbox("Auto-Lernrate");

		/*
		** Listbox mit Abdr�cken initialisieren:
		*/
		for(i=0;i<10;i++) {
			FingerPrintList.addItem("Christian_"+i);
		}
		for(i=0;i<10;i++) {
			FingerPrintList.addItem("Peter_"+i);
		}
		for(i=0;i<10;i++) {
			FingerPrintList.addItem("Andrea_"+i);
		}
		for(i=0;i<10;i++) {
			FingerPrintList.addItem("Liane_"+i);
		}

		/*
		** Wieso die folgenden Zeilen n�tig sind, wei� ich leider auch nicht.
		** Jedenfalls erscheinen in der zweiten Listbox nie Scrollbars, wenn nicht
		** die Listbox schon �berf�llt ist, bevor sie das erste mal engezeigt wird.
		** Nachdem die Listbox angezeigt wurde, k�nnen die Eintr�ge gel�scht werden.
		** Sp�ter erneut eingef�gte Eintr�ge erzeugen ggf. eine Scrollbar.
		** Falls jemand wei�, wie man auch ohne diesen Hack an eine Scrollbar kommt,
		** bitte Info an Christian.Birzer@t-online.de
		*/
		for(i=0;i<10;i++) {
			VectorList.addItem("JavaBugWorkaround");
		}

		/*
		** ComboBox mit Anzahl der Lernschritte f�llen:
		*/
		StepsChoice.addItem("1");
		StepsChoice.addItem("5");
		StepsChoice.addItem("10");
		StepsChoice.addItem("20");
		StepsChoice.addItem("30");
		StepsChoice.select(2);

		/*
		** GUI-Elemente in Panels h�ngen:
		*/
		PanelNorth = new Panel();
		PanelNorth.add(BuEdge);
		PanelNorth.add(BuFind);
		PanelNorth.add(BuThin);

		PanelWest = new Panel();
		WestLayout = new GridBagLayout();
		Constraint = new GridBagConstraints();
		Constraint.fill = GridBagConstraints.NONE;
		Constraint.gridwidth = GridBagConstraints.REMAINDER;
		Constraint.weightx = 1.5;
		Constraint.anchor = GridBagConstraints.NORTH;
		Constraint.weightx = 1.0;
		Constraint.insets = new Insets(1,4,1,4);
		Constraint.fill = GridBagConstraints.HORIZONTAL;

		WestLayout.addLayoutComponent("inputlabel", InputLabel);
		WestLayout.setConstraints(InputLabel, Constraint);

		// Input List:
		Constraint.gridwidth=1;
		Constraint.gridheight=3;
		Constraint.weighty=0.0;//1.0;
		WestLayout.addLayoutComponent("list", FingerPrintList);
		WestLayout.setConstraints(FingerPrintList, Constraint);

		// Input List Buttons:
		Constraint.gridheight=1;
		Constraint.gridwidth=GridBagConstraints.REMAINDER;
		Constraint.weighty=0.0;
		WestLayout.addLayoutComponent("bualli", BuAllI);
		WestLayout.setConstraints(BuAllI, Constraint);
		WestLayout.addLayoutComponent("bunonei", BuNoneI);
		WestLayout.setConstraints(BuNoneI, Constraint);
		WestLayout.addLayoutComponent("buinverti", BuInvertI);
		WestLayout.setConstraints(BuInvertI, Constraint);

		// com.anna.som.Filter-Auswahl Group Label:
		Constraint.gridwidth=1;
		Constraint.gridheight=3;
		WestLayout.addLayoutComponent("filterlabel", FilterLabel);
		WestLayout.setConstraints(FilterLabel, Constraint);

		// com.anna.som.Filter-Auswahl Group:
		Constraint.gridheight=1;
		Constraint.gridwidth=GridBagConstraints.REMAINDER;
		WestLayout.addLayoutComponent("filtergroup", FilterCheck1);
		WestLayout.setConstraints(FilterCheck1, Constraint);
		WestLayout.addLayoutComponent("filtergroup", FilterCheck2);
		WestLayout.setConstraints(FilterCheck2, Constraint);
		WestLayout.addLayoutComponent("filtergroup", FilterCheck3);
		WestLayout.setConstraints(FilterCheck3, Constraint);

		WestLayout.addLayoutComponent("bustart",BuStart);
		WestLayout.setConstraints(BuStart, Constraint);
		WestLayout.addLayoutComponent("label", MsgLabel);
		WestLayout.setConstraints(MsgLabel, Constraint);

		// Vektor Label:
		WestLayout.addLayoutComponent("vectorlabel", VectorLabel);
		WestLayout.setConstraints(VectorLabel, Constraint);

		// Vektor List:
		Constraint.gridwidth=1;
		Constraint.gridheight=4;
		Constraint.weighty=0.0;//1.0;
		WestLayout.addLayoutComponent("vectorlist",VectorList);
		WestLayout.setConstraints(VectorList, Constraint);

		// Vektor List Buttons:
		Constraint.gridheight=1;
		Constraint.gridwidth=GridBagConstraints.REMAINDER;
		Constraint.weighty=0.0;
		WestLayout.addLayoutComponent("buallv", BuAllV);
		WestLayout.setConstraints(BuAllV, Constraint);
		WestLayout.addLayoutComponent("bunonev", BuNoneV);
		WestLayout.setConstraints(BuNoneV, Constraint);
		WestLayout.addLayoutComponent("buinvertv", BuInvertV);
		WestLayout.setConstraints(BuInvertV, Constraint);

		Constraint.insets = new Insets(10,4,1,4);	// etwas Abstand...
		WestLayout.addLayoutComponent("budelv", BuDelV);
		WestLayout.setConstraints(BuDelV, Constraint);
		Constraint.insets = new Insets(1,4,1,4);

		// Lernschritte-Label:
		Constraint.gridwidth=1;
		WestLayout.addLayoutComponent("stepslabel", StepsLabel);
		WestLayout.setConstraints(StepsLabel, Constraint);

		// Lernschritte-Combo Box:
		Constraint.gridwidth = GridBagConstraints.REMAINDER;
		WestLayout.addLayoutComponent("stepschoice", StepsChoice);
		WestLayout.setConstraints(StepsChoice, Constraint);

		// Lernrate Labels und TextFields:
		Constraint.gridwidth=1;
		Constraint.weightx=1.0;
		Constraint.insets = new Insets(1,4,1,4);
		WestLayout.addLayoutComponent("minepslabel", MinEpsLabel);
		WestLayout.setConstraints(MinEpsLabel, Constraint);
		Constraint.insets = new Insets(0,0,0,0);
		WestLayout.addLayoutComponent("minepstext", MinEpsText);
		WestLayout.setConstraints(MinEpsText, Constraint);
		WestLayout.addLayoutComponent("maxepslabel", MaxEpsLabel);
		WestLayout.setConstraints(MaxEpsLabel, Constraint);
		Constraint.gridwidth = GridBagConstraints.REMAINDER;
		Constraint.insets = new Insets(1,4,1,4);
		WestLayout.addLayoutComponent("maxepstext", MaxEpsText);
		WestLayout.setConstraints(MaxEpsText, Constraint);

		// Varianz Labels und TextFields:
		Constraint.gridwidth=1;
		Constraint.insets = new Insets(1,4,1,4);
		WestLayout.addLayoutComponent("minsiglabel", MinSigLabel);
		WestLayout.setConstraints(MinSigLabel, Constraint);
		Constraint.insets = new Insets(0,0,0,0);
		WestLayout.addLayoutComponent("minsigtext", MinSigText);
		WestLayout.setConstraints(MinSigText, Constraint);
		WestLayout.addLayoutComponent("maxsiglabel", MaxSigLabel);
		WestLayout.setConstraints(MaxSigLabel, Constraint);
		Constraint.insets = new Insets(1,4,1,4);
		Constraint.gridwidth = GridBagConstraints.REMAINDER;
		WestLayout.addLayoutComponent("maxsigtext", MaxSigText);
		WestLayout.setConstraints(MaxSigText, Constraint);

		Constraint.weightx=0.0;
		Constraint.insets = new Insets(1,4,1,4);

		// Auto-Lernrate
		WestLayout.addLayoutComponent("autolearn", AutoLearn);
		WestLayout.setConstraints(AutoLearn, Constraint);

		// Netz Buttons:
		WestLayout.addLayoutComponent("bulearn", BuLearn);
		WestLayout.setConstraints(BuLearn, Constraint);
		WestLayout.addLayoutComponent("busearch", BuSearch);
		WestLayout.setConstraints(BuSearch, Constraint);
		WestLayout.addLayoutComponent("buforget", BuForget);
		WestLayout.setConstraints(BuForget, Constraint);

		// Debug Ausgabe:
		WestLayout.addLayoutComponent("debugout", DebugOut);
		WestLayout.setConstraints(DebugOut, Constraint);

		// Rest irgendwie auff�llen, damit der andere Mist oben ausgegeben wird...:
		DummyPanel = new Panel();
		Constraint.gridheight = GridBagConstraints.REMAINDER; //GridBagConstraints.REMAINDER;
		Constraint.weighty = 1.0;
		WestLayout.addLayoutComponent("dummypanel",DummyPanel);
		WestLayout.setConstraints(DummyPanel, Constraint);

		// GUI-Komponenten ins Panel einh�ngen:
		PanelWest.setLayout(WestLayout);
		PanelWest.add(InputLabel);
		PanelWest.add(FingerPrintList);
		PanelWest.add(BuAllI);
		PanelWest.add(BuNoneI);
		PanelWest.add(BuInvertI);
		PanelWest.add(FilterLabel);
		PanelWest.add(FilterCheck1);
		PanelWest.add(FilterCheck2);
		PanelWest.add(FilterCheck3);
		PanelWest.add(BuStart);
		PanelWest.add(MsgLabel);
		PanelWest.add(VectorLabel);
		PanelWest.add(VectorList);
		PanelWest.add(BuAllV);
		PanelWest.add(BuNoneV);
		PanelWest.add(BuInvertV);
		PanelWest.add(BuDelV);
		PanelWest.add(StepsLabel);
		PanelWest.add(StepsChoice);
		PanelWest.add(MinEpsLabel);
		PanelWest.add(MinEpsText);
		PanelWest.add(MaxEpsLabel);
		PanelWest.add(MaxEpsText);
		PanelWest.add(MinSigLabel);
		PanelWest.add(MinSigText);
		PanelWest.add(MaxSigLabel);
		PanelWest.add(MaxSigText); 
		PanelWest.add(AutoLearn);
		PanelWest.add(BuLearn);
		PanelWest.add(BuSearch);
		PanelWest.add(BuForget);
		PanelWest.add(DebugOut);
		PanelWest.add(DummyPanel);

		/*
		** Panels und GUI-Elemente in Layoutmanager h�ngen:
		*/
		setLayout(new BorderLayout());
		add("West", PanelWest);
		add("East", pc2);
//		add("North",PanelNorth); // Nur f�r Debug-Zwecke!

		/*
		** Bild-Daten ermitteln und Quell- und Zielbild anlegen:
		*/
		w = SourceImg.getWidth(null);
		h = SourceImg.getHeight(null);

		SourceData = new PixelArray(w,h);
		DestData = new PixelArray(w,h);
		pc2.resize(w,h);

		DestData.Clear();

		GetImageData();

		/*
		** Die Eintr�ge in der VectorList, die nur zum Bug-Workaround n�tig waren, wieder l�schen.
		** Ab jetzt kann er die Scrollbalken auch selbst darstellen!
		*/
		VectorList.clear();

		// Schrift f�r Message-Label fett
		MsgLabel.setFont(new Font(MsgLabel.getFont().getName(), Font.BOLD, MsgLabel.getFont().getSize()));

		// Lernrate und Varianz setzen:
		MinEpsText.setText(String.valueOf(Netz.Lernrate(true)));
		MaxEpsText.setText(String.valueOf(Netz.Lernrate(false)));
		MinSigText.setText(String.valueOf(Netz.Varianz(true)));
		MaxSigText.setText(String.valueOf(Netz.Varianz(false)));

		SourceImg = SetImageData(SourceData.Pixels, true);
		DestImg = SetImageData(DestData.Pixels, false);
		pc2.SetImage(DestImg);
	}

	// Hier zus�tzlichen Bereinigungscode f�r das Applet platzieren. destroy() wird aufgerufen, 
	// wenn das Applet beendet und entladen wird
	//-------------------------------------------------------------------------
	public void destroy()
	{
		// ZU ERLEDIGEN: Platzieren Sie hier Bereinigungscode f�r das Applet 
	}

	// com.anna.som.FingerPrint Zeichnungsbehandlungsroutine
	//--------------------------------------------------------------------------
	public void paint(Graphics g)
	{
		g.drawString("Erstellt mit Microsoft Visual J++ Version 1.1", 10, 20);
	}

	// Die Methode start() wird aufgerufen, wenn die Seite, die das Applet enth�lt,
	// erstmals auf dem Bildschirm erscheint. Die Startimplementierung des Applet-Assistenten
	// dieser Methode startet die Ausf�hrung des Threads des Applets.
	//--------------------------------------------------------------------------
	public void start()
	{
		// ZU ERLEDIGEN: Platzieren Sie hier zus�tzlichen Code f�r den Start des Applets
	}
	
	//		Die Methode stop() wird aufgerufen, wenn die Seite, die das Applet enth�lt,
	// nicht mehr auf dem Bildschirm angezeigt wird. Die Startimplementierung des Applet-Assistenten
	// dieser Methode beendet die Ausf�hrung des Threads des Applets.
	//--------------------------------------------------------------------------
	public void stop()
	{
	}

	/*
	** getFilterMode
	**
	** Ermittelt den gew�nschten Kantendetektions-Modus �ber die Auswahl der
	** Checkbox. Liefert 1 f�r Schwellwert, 2 f�r Sobel, 3 f�r Logarithmisch
	*/
	private int getFilterMode()
	{
		if(FilterGroup.getCurrent() == FilterCheck1) {
			return 1;
		} else if(FilterGroup.getCurrent() == FilterCheck2) {
			return 2;
		} else {
			return 3;
		}
	}

	/*
	** getFilterModeChar
	**
	** Ermittelt f�r den gew�nschten Kantendetektions-Modus ein Zeichen
	*/
	private char getFilterModeChar()
	{
		if(FilterGroup.getCurrent() == FilterCheck1) {
			return 'W'; // Schwellwert
		} else if(FilterGroup.getCurrent() == FilterCheck2) {
			return 'S'; // Sobel
		} else {
			return 'L'; // Log
		}
	}
	
	/*
	** OnBuStartPress
	**
	** Startet den Bildverarbeitungs-Prozess. Alle in der Listbox markierten
	** Bilder werden der Reihe nach geladen, Kantendetektiert, Skelettiert und
	** die Merkmale werden ermittelt und die erzeugten Vektoren ans Netz
	** weitergegeben.
	*/
	private void OnBuStartPress()
	{
		int picindex, i, VecIndex, x, y, Who;
		byte WEIGHT_3 = 10;
		byte WEIGHT_2 = -2;
		Filter f;
		Node n;
		String items[];
		char FilterModeC;

		/*
		** Listeneintrag ermitteln und ggf. abbrechen:
		*/
		items = FingerPrintList.getSelectedItems();
		if(items.length < 1) {
			MsgLabel.setText("Kein Listeneintrag gew�hlt!");
			return;
		}

		for(picindex = 0; picindex < items.length; picindex++) {
			/*
			** Eingabebild laden und anzeigen:
			*/
			MsgLabel.setText("Bild wird geladen (Bild "+(picindex+1)+"/"+items.length+")...");
			PictureName = items[picindex];
			LoadImage(PictureName+".gif");
			GetImageData(); // Bild in SourceData holen
			DestData.Clear();	// Ausgabebild l�schen
			// Daten in Bilder kopieren:
			SourceImg = SetImageData(SourceData.Pixels, false);
			DestImg = SetImageData(DestData.Pixels, false);

			pc2.SetImage(SourceImg);

			MsgLabel.setText("Kanten werden gesucht (Bild "+(picindex+1)+"/"+items.length+")...");

			/*
			** Kantendetektion:
			*/
			
			FilterModeC = getFilterModeChar();
			f = new Filter(SourceData);
			f.doFilter(getFilterMode());
			DestData = f.getResult();
			pc2.setPixels(DestData);

			MsgLabel.setText("Merkmale werden gesucht (Bild "+(picindex+1)+"/"+items.length+")...");

			/*
			** Linien Finden:
			*/
			SourceData = DestData.Clone();
			DestData.Clear();

			lf = new LineFinder(SourceData.Pixels, DestData.Pixels, w, h);
			lf.SetAppletContext(getAppletContext());

			i = 0;
			while(lf.Find()) {
				lf.Thin();
				lf.FindNodes();
				lf.Copy();
				i++;
				if(i%10 == 0) {
					MsgLabel.setText(""+i+" Linien bearbeitet (Bild "+(picindex+1)+"/"+items.length+")...");
				}
			}
			MsgLabel.setText(""+i+" Linien bearbeitet (Bild "+(picindex+1)+"/"+items.length+")...");

			/*
			** Ausgabe:
			*/
			for(i=0;i<lf.Nodes.size();i++) {
				n = (Node)lf.Nodes.elementAt(i);
				if(n.level==3) {
					DestData.drawCircle(n.x,n.y,7,0xff000000);
				} else if(n.level==1) {
					DestData.drawBox(n.x,n.y,7,0xff000000);
				}
			} 
			pc2.setPixels(DestData);

			/*
			** Merkmale Segmentieren:
			*/
			MsgLabel.setText("Merkmale werden segmentiert (Bild "+(picindex+1)+"/"+items.length+")...");

			Vector = new NeuroVektor(722);
			Vector.InitVektor((byte)0);

			VecIndex = 0;
			for(x=0;x<w-50;x+=25) {
				for(y=0;y<h-80;y+=40) {
					for(i=0;i<lf.Nodes.size();i++) {
						n = (Node)lf.Nodes.elementAt(i);
						if(n.x >= x && n.x < x+50 && n.y >= y && n.y < y+80) {
							if(n.level == 3) {
								Vector.Add(WEIGHT_3, VecIndex);
							} else {
								Vector.Add(WEIGHT_2, VecIndex+1);
							}
						}
					}
					VecIndex += 2;
					MsgLabel.setText("Index " + VecIndex +"/722 (Bild "+(picindex+1)+"/"+items.length+")...");
				}
			}

			/*
			** Nachsehen, wer es war
			*/
			if(PictureName.startsWith("Christian_"))
				Who = 0;
			else if(PictureName.startsWith("Peter_"))
				Who = 1;
			else if(PictureName.startsWith("Liane_"))
				Who = 3;
			else if(PictureName.startsWith("Andrea_"))
				Who = 4;
			else
				Who = 5;

			Netz.Add(Vector, PictureName+FilterModeC, Who);
			if(Vector == null) {
				MsgLabel.setText("NULL");
				return;
			}

			VectorList.addItem(PictureName+FilterModeC);
			VectorList.makeVisible(VectorList.countItems()-1);
		}
		MsgLabel.setText("Bereit!");
		Runtime.getRuntime().gc();
	}

	/*
	** OnBuSearchPress
	**
	** Reagiert auf einen Click auf den Butten "Suchen" und startet die Erkennung
	** der ausgew�hlten Muster. Eine Statistik wird dabei im Debug-Ausgabe-Fenster
	** angezeigt.
	*/
	private void OnBuSearchPress()
	{
		String vector;
		String items[], out, debout, search;
		int v, i, found;
		Date now;

		// Wenn kein Vektor ausgew�hlt ist, sofort zur�ck:
		items = VectorList.getSelectedItems();
		if(items.length < 1) {
			MsgLabel.setText("Kein Vektor ausgew�hlt!");
			return;
		}

		found = 0;	// Anzahl der richtig erkannten Muster
		now = new Date();	// Aktuelle Zeit festhalten

		DebugOut.appendText("\n===== Mustererkennung am " + now.toString() + " =====\n"); 

		for(i=0;i<items.length;i++) {
			MsgLabel.setText("Suche l�uft (Muster " + (i+1) + "/" + items.length + ") ...");
			// Vektor-Index suchen:

			out = Netz.SucheMuster(items[i]);
			search = //Netz.GetPerson(Netz.GetPersonenIndex(items[i])).name;
				Netz.GetPerson(Netz.GetEingabe(items[i]).personenid).name;

			if(Netz.Trefferquote() == 0.0) {
				out = "nicht gefunden";
			}

			debout = "Gesucht: " + search +
				", \tgefunden: " + out + ",       \tTrefferquote: " + Netz.Trefferquote() + 
				"%,    \tGeschlecht: ";
			if(Netz.Geschlecht()<0)
				debout += "weiblich";
			else
				debout += "m�nnlich";
			debout += "\n";

			if(search.equals(out)) {
				// Muster richtig erkannt!
				found++;
			}

			MsgLabel.setText("Gefundenes Muster: " + out + "  " + Netz.Trefferquote() + "%");
			DebugOut.appendText(debout);

			// Muster-Zentrum in Ausgabebild einzeichnen:
			Netz.drawZentrum(NetImg[0], items[i], false);
			pc2.Set3Image(NetImg[0], 1);
			Netz.drawZentrum(NetImg[1], items[i], false);
			pc2.Set3Image(NetImg[1], 2);
			Netz.drawZentrum(NetImg[2], items[i], false);
			pc2.Set3Image(NetImg[2], 3);
			pc2.update3Image();
		}
		DebugOut.appendText("-----\nZusammenfassung: Richtig erkannte Muster: " + found + 
			" von " + items.length + " = " + (float)found*100.0/(float)items.length + "%\n");
	}

	/*
	** OnBuLearnPress
	**
	** Startet den Lernvorgang. Alle in der Listbox markierten Vektoren
	** werden im Netz zum Lernen markiert und gelernt. Anschlie�end
	** wird die Netzstruktur grafisch ausgegeben.
	*/
	private void OnBuLearnPress()
	{
		int i, v, l, steps;
		String items[];
		int Who;
		Graphics og;
		Float mineps, maxeps, minsig, maxsig;

		// Auswahl pr�fen:
		items = VectorList.getSelectedItems();
		if(items.length < 1) {
			MsgLabel.setText("Keine Vektoren ausgew�hlt!");
			return;
		}

		// Varianz und Lernrate ermitteln:
		try {
			mineps = new Float(MinEpsText.getText());
		} catch (NumberFormatException e) {
			MsgLabel.setText("Keine Zahl bei minimaler Lernrate!");
			return;
		}
		try {
			maxeps = new Float(MaxEpsText.getText());
		} catch (NumberFormatException e) {
			MsgLabel.setText("Keine Zahl bei maximaler Lernrate!");
			return;
		}
		try {
			minsig = new Float(MinSigText.getText());
		} catch (NumberFormatException e) {
			MsgLabel.setText("Keine Zahl bei minimaler Varianz!");
			return;
		}
		try {
			maxsig = new Float(MaxSigText.getText());
		} catch (NumberFormatException e) {
			MsgLabel.setText("Keine Zahl bei maximaler Varianz!");
			return;
		}
		
		// Netz-Parameter einstellen:
		Netz.m_netz.Varianz((float)2.25);
		Netz.LoescheMarkierungen();
		Netz.Varianz(minsig.floatValue(), maxsig.floatValue());
		Netz.Lernrate(mineps.floatValue(), maxeps.floatValue());
		Netz.AutoLernrate(AutoLearn.getState());

		// Vektoren zum Lernen markieren:
		for(i=0;i<items.length;i++) {
			Netz.MarkiereMuster(items[i], true);
		}

		// Anzahl der Lernschritte einstellen:
		switch(StepsChoice.getSelectedIndex()) {
			case 0: steps = 1;
					break;
			case 1: steps = 5;
					break;
			case 2: steps = 10;
					break;
			case 3: steps = 20;
					break;
			case 4: steps = 30;
					break;
			default:steps = 0;
		}

		Netz.Lernschritte(steps);	// Anzahl Lernschritte setzen

		l=0;
		while (l < steps) {
			l=Netz.Lernen(MsgLabel,l);
						
			// Ausgabe des Netzes ***** alle 4 Lernschritte *****
			MsgLabel.setText("Netzbilder werden berechnet...");
			// Netz-Ausgabebilder l�schen:
			for(i=0;i<3;i++) {
				og = NetImg[i].getGraphics();
				og.setColor(Color.black);
				og.fillRect(0,0,NetImg[i].getWidth(null)-1,NetImg[i].getHeight(null)-1);
			}
			Netz.drawZentren(NetImg[0]);
			pc2.Set3Image(NetImg[0], 1);
			Netz.drawMuster(NetImg[1]);
			pc2.Set3Image(NetImg[1], 2);
			Netz.drawAusgabeMap(NetImg[2]);
			pc2.Set3Image(NetImg[2], 3);
			pc2.update3Image();
		}
		MsgLabel.setText("Bereit!");
	}

	/*
	** OnBuAllVPress
	** 
	** Selektiert alle Eintr�ge in der Vektoren-Liste.
	*/
	private void OnBuAllVPress()
	{
		int i;

		for(i=0;i<VectorList.countItems();i++) {
			VectorList.select(i);
		}
	}

	/*
	** OnBuNoneVPress
	**
	** Deselektiert alle Eintr�ge in der Vektoren-Liste.
	*/
	private void OnBuNoneVPress()
	{
		int i;

		for(i=0;i<VectorList.countItems();i++) {
			VectorList.deselect(i);
		}

	}

	/*
	** OnBuInvertVPress
	**
	** Invertiert die Selektion in der Vektoren-Liste.
	*/
	private void OnBuInvertVPress()
	{
		int i;

		for(i=0;i<VectorList.countItems();i++) {
			if(VectorList.isSelected(i)) {
				VectorList.deselect(i);
			} else {
				VectorList.select(i);
			}
		}
	}

	/*
	** OnBuDelVPress
	**
	** L�scht alle berechneten Vektoren, indem eine neue
	** Instanz der com.anna.som.NetzTester-Klasse erzeugt wird.
	*/
	private void OnBuDelVPress()
	{
		VectorList.clear();
		Netz = new NetzTester(722);

	}

	/*
	** OnBuAllIPress
	** 
	** Selektiert alle Eintr�ge in der Eingabebild-Liste.
	*/
	private void OnBuAllIPress()
	{
		int i;

		for(i=0;i<FingerPrintList.countItems();i++) {
			FingerPrintList.select(i);
		}
	}

	/*
	** OnBuNoneIPress
	**
	** Deselektiert alle Eintr�ge in der Eingabebild-Liste.
	*/
	private void OnBuNoneIPress()
	{
		int i;

		for(i=0;i<FingerPrintList.countItems();i++) {
			FingerPrintList.deselect(i);
		}
	}

	/*
	** OnBuInvertIPress
	**
	** Invertiert die Selektion in der Eingabebild-Liste.
	*/
	private void OnBuInvertIPress()
	{
		int i;

		for(i=0;i<FingerPrintList.countItems();i++) {
			if(FingerPrintList.isSelected(i)) {
				FingerPrintList.deselect(i);
			} else {
				FingerPrintList.select(i);
			}
		}
	}

	/*
	** OnBuForgetPress
	**
	** 'Vergisst' die gelernten Muster, l�scht das Netz und die Ausgabebilder.
	*/
	private void OnBuForgetPress()
	{
		int i;
		Graphics og;

		Netz.LoescheNetz();

		// Netz-Ausgabebilder l�schen:
		for(i=0;i<3;i++) {
			og = NetImg[i].getGraphics();
			og.setColor(Color.black);
			og.fillRect(0,0,NetImg[i].getWidth(null)-1,NetImg[i].getHeight(null)-1);
		}
	}

	/*
	** handleEvent 
	**
	** Message-Dispatch-Funktion. Reagiert auf Button-Clicks
	** und ruft die entsprechende Behandlungsfunktion auf.
	*/
	public boolean handleEvent (Event evt)
	{
		int i;

		if(evt.id == Event.ACTION_EVENT) {
/*			if(evt.target == BuFind) {
				OnBuFindPress();
			} else if (evt.target == BuThin) {
				OnBuThinPress();
			} else if (evt.target == BuEdge) {
				OnBuEdgePress();
			} else*/ if (evt.target == BuStart) {
				OnBuStartPress();
			}  else if (evt.target == BuLearn) {
				OnBuLearnPress();
			} else if (evt.target == BuSearch) {
				OnBuSearchPress();
			} else if (evt.target == BuForget) {
				OnBuForgetPress();
			} else if (evt.target == BuAllV) {
				OnBuAllVPress();
			} else if (evt.target == BuNoneV) {
				OnBuNoneVPress();
			} else if (evt.target == BuInvertV) {
				OnBuInvertVPress();
			} else if (evt.target == BuAllI) {
				OnBuAllIPress();
			} else if (evt.target == BuNoneI) {
				OnBuNoneIPress();
			} else if (evt.target == BuInvertI) {
				OnBuInvertIPress();
			} else if (evt.target == BuDelV) {
				OnBuDelVPress();
			}
		}
		return super.handleEvent(evt);
	}

/*	private void OnBuEdgePress()
	{
		String items[];
		com.anna.som.Filter f;

		items = FingerPrintList.getSelectedItems();
		if(items.length != 1) {
			MsgLabel.setText("Kein oder mehrere Vektoren ausgew�hlt!");
			return;
		}
		MsgLabel.setText("Bild wird geladen...");
		LoadImage(items[0]+".gif");
		GetImageData();
		SourceImg = SetImageData(SourceData.Pixels,true);

		MsgLabel.setText("Kanten werden gesucht...");
		f = new com.anna.som.Filter(SourceData);
		f.doFilter(getFilterMode());
		DestData = f.getResult();
		pc2.setPixels(DestData);

		MsgLabel.setText("Bereit!");

	}

	private void OnBuFindPress()
	{
		int i;
		com.anna.som.Node n;

		SourceData = DestData.Clone();		

		// DestData l�schen:
		DestData.Clear();

		MsgLabel.setText("Linien werden gesucht...");
		lf = new com.anna.som.LineFinder(SourceData.Pixels, DestData.Pixels, w, h);
		lf.SetAppletContext(getAppletContext());

		while(lf.Find()) {
			lf.Thin();
			lf.FindNodes();
			lf.Copy();
		}
		for(i=0;i<lf.Nodes.size();i++) {
			n = (com.anna.som.Node)lf.Nodes.elementAt(i);
			if(n.level==3) {
				DestData.drawCircle(n.x,n.y,7,0xff000000);
			} else if(n.level==1) {
				DestData.drawBox(n.x,n.y,7,0xff000000);
			}
		} 

		pc2.setPixels(DestData);
		MsgLabel.setText("Bereit!");
	}

	private void OnBuThinPress()
	{
		int i;
		com.anna.som.Node n;

		SourceData = DestData.Clone();

		lf = new com.anna.som.LineFinder(SourceData.Pixels, DestData.Pixels, w, h);
		lf.SetAppletContext(getAppletContext());

		try {
			while(lf.Find()) {
				lf.Thin();
				lf.FindNodes();
				lf.Copy();
				lf.DrawCenterLine();
			}
			for(i=0;i<lf.Nodes.size();i++) {
				n = (com.anna.som.Node)lf.Nodes.elementAt(i);
				if(n.level==3) {
					DestData.drawCircle(n.x,n.y,7,0xff000000);
				} else if(n.level==1) {
					DestData.drawBox(n.x,n.y,7,0xff000000);
				}
			} 
		} catch (ArrayIndexOutOfBoundsException e) {
			getAppletContext().showStatus("ERROR: Exception ArrayIndexOutOfBounds in handleEvent()");
		}
		pc2.setPixels(DestData);
	}
*/
}
