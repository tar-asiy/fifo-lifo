import sum.ereignis.*;
import sum.komponenten.*;
import sum.werkzeuge.*;
import sum.strukturen.*;

/**
 * GrafikFenster_SuS:
 *  Dieses Fenster dient zur grafischen Darstellung von Sortierzeiten.
 *  
 *  - Die SuS sollen hier eine Warteschlange (FIFO) verwenden, 
 *    um sog. "SortierJobs" zu verwalten.
 *  - Jeder SortierJob besteht aus (Verfahren, Farbe).
 *  - Bei Klick auf "Job hinzufügen" könnte der Job in die Schlange eingereiht werden.
 *  - Bei Klick auf "Zeichnen" werden die Jobs nacheinander abgearbeitet,
 *    und es entsteht eine Kurve im Koordinatensystem.
 *  
 *  Aktuell ist der Code nur eine Grundlage. 
 *  Die Zeilen:
 *      // => SuS-Aufgabe: Schlange anlegen + Jobs abarbeiten
 *  zeigen, wo Sie selbst Ihre Warteschlange implementieren sollen.
 */
public class GrafikFenster_SuS extends Ereignisanwendung {

    // -----------------------------------------------------
    // 1) GUI-Felder
    // -----------------------------------------------------
    // Diese Felder sind die grafischen Elemente (Buttons, Auswahlen, usw.)
    // die links im Fenster angeordnet sind.
    private Auswahl auswahlVerfahren, auswahlFarbe;
    private Knopf   hinzufuegenKnopf, zeichnenKnopf, erneuernKnopf;
    private Zeilenbereich textWarteschlange;

    // => SuS: schlange selbst deklarieren

    // Zeichenwerkzeuge
    private Buntstift stift; 
    // Koordinaten für das Diagramm (linke obere Ecke, Breite, Höhe)
    private int diagrammX = 200, diagrammY = 20;
    private int diagrammBreite = 1000, diagrammHoehe = 600;

    // Liste mit Karten aus dem Hauptfenster
    // => Brauchen wir, um Sortierzeiten an realen Kartendaten zu messen
    private Liste<Kartenmanager_Hauptfenster_SuS.Karte> hauptKarten;

    // Fünf Farben. 
    //   Index = 0..4 => "Rosa, Grau, Orange, Blau, Grün"
    //   (Siehe setzeFarbe(...) unten)
    private String[] farbNamen = { "Rosa", "Grau", "Orange", "Blau", "Grün" };

    /**
     * Hilfsklasse SortierJob:
     *  - Enthält (verfahren, farbeIndex).
     *  - SuS sollen damit experimentieren, wie man Objekte in die Schlange speichert.
     */
    private class SortierJob {
        String verfahren;  // z.B. "Bubblesort", "InsertSort", ...
        int farbeIndex;    // 0..4 => Rosa..Grün

        public SortierJob(String v, int f) {
            verfahren  = v;
            farbeIndex = f;
        }
    }

    // -----------------------------------------------------
    // 2) Konstruktor
    // -----------------------------------------------------
    public GrafikFenster_SuS(){
        // Ruft den Konstruktor von Ereignisanwendung auf
        super();

        // Wir erzeugen einen Buntstift zum Zeichnen
        stift = new Buntstift();

        // => SuS: schlange selbst initialisieren

        // GUI links:
        //  1) Beschriftung: "Sortierverfahren:"
        //  2) Auswahl aus Bubblesort, InsertSort, ...
        Etikett e1 = new Etikett(20, 20, 120, 25, "Sortierverfahren:");
        auswahlVerfahren = new Auswahl(20, 50, 120, 25);
        auswahlVerfahren.haengeAn("Bubblesort"); 
        auswahlVerfahren.haengeAn("InsertSort");
        auswahlVerfahren.haengeAn("Selectionsort");
        auswahlVerfahren.haengeAn("Quicksort");
        // Standardauswahl = 1 => "Bubblesort"
        auswahlVerfahren.waehle(1);

        // Nächste Beschriftung + Auswahl: "Linien-Farbe"
        Etikett e2 = new Etikett(20, 90, 120, 25, "Linien-Farbe:");
        auswahlFarbe = new Auswahl(20,120,120,25);
        // Alle 5 Farbnamen einhängen
        for(String f : farbNamen) {
            auswahlFarbe.haengeAn(f);
        }
        // Standardauswahl = 1 => "Rosa"
        auswahlFarbe.waehle(1);

        // Dieser Zeilenbereich (TextArea) zeigt die aktuelle Warteschlange
        // => "textWarteschlange" kann man befüllen
        textWarteschlange = new Zeilenbereich(20,160,160,180,"");

        // 3 Buttons: "Job hinzufügen", "Zeichnen", "Erneuern"
        hinzufuegenKnopf = new Knopf(20,350,160,30,"Job hinzufügen");
        hinzufuegenKnopf.setzeBearbeiterGeklickt("jobHinzufuegen");

        zeichnenKnopf   = new Knopf(20,390,160,30,"Zeichnen");
        zeichnenKnopf.setzeBearbeiterGeklickt("zeichnenKlick");

        erneuernKnopf   = new Knopf(20,430,160,30,"Erneuern");
        erneuernKnopf.setzeBearbeiterGeklickt("erneuernKlick");

        // Koordinatensystem (Diagramm) wird am Anfang gleich gezeichnet
        zeichneKoordinatensystem();
    }

    // -----------------------------------------------------
    // 3) Hauptfenster -> Kartenliste
    // -----------------------------------------------------
    /**
     * Hier erhält das Grafikfenster die Kartenliste
     * aus dem Hauptfenster. Dann können wir an "echten" 
     * Daten Sortierzeiten messen.
     */
    public void setzeKartenliste(Liste<Kartenmanager_Hauptfenster_SuS.Karte> kliste){
        this.hauptKarten = kliste;
    }

    // -----------------------------------------------------
    // 4) Button-Methoden
    // -----------------------------------------------------
    /**
     * "Job hinzufügen" => 
     *  - SuS sollen hier in die Schlange
     *    einen neuen SortierJob einfügen (haengeAn).
     */
    public void jobHinzufuegen(){
        
    }

    /**
     * "Zeichnen" => 
     *  - SuS sollen hier alle Jobs (FIFO) abarbeiten,
     *    d.h. solange jobs nicht leer, 
     *         SortierJob = jobs.kopf()
     *         jobs.entferneErstes()
     *         zeichneKurveFuer( job )
     */
    public void zeichnenKlick(){
        // Wir löschen das Diagramm neu:
        zeichneKoordinatensystem();

        // Minimales Beispiel: 
        // => wir lesen die Auswahlen und zeichnen EINE Kurve
        String verfahren = auswahlVerfahren.text();
        int farbIx = auswahlFarbe.index()-1;  // index=1..5 => 0..4
        SortierJob job = new SortierJob(verfahren, farbIx);

        // Dann Kurve: 
        zeichneKurveFuer(job);
    }

    /**
     * "Erneuern" => 
     *  - Alles löschen und Koordinatensystem neu zeichnen,
     *    Warteschlange leeren.
     */
    public void erneuernKlick(){
        bildschirm().loescheAlles();
        textWarteschlange.loescheAlles();
        // => Warteschlange auch erneuern
        zeichneKoordinatensystem();
    }

    // -----------------------------------------------------
    // 5) Warteschlange anzeigen
    // -----------------------------------------------------
    /**
     * aktualisiereWarteschlange():
     *  - Diese Methode kann man aufrufen, 
     *    sobald sich die Schlange ändert, 
     *    um den Textbereich neu zu füllen.
     */
    private void aktualisiereWarteschlange(){
        textWarteschlange.loescheAlles();

    }

    // -----------------------------------------------------
    // 6) Koordinatensystem
    // -----------------------------------------------------
    /**
     * Zeichnet einfache X/Y-Achsen
     *  (x=200..1200, y=20..620).
     */
    private void zeichneKoordinatensystem(){
        stift.setzeFarbe(0); // 0= Schwarz

        // X-Achse
        stift.bewegeBis(diagrammX, diagrammY+diagrammHoehe);
        stift.runter();
        stift.bewegeUm(diagrammBreite);
        stift.hoch();

        // Y-Achse
        stift.bewegeBis(diagrammX, diagrammY+diagrammHoehe);
        stift.runter();
        stift.dreheBis(90);
        stift.bewegeUm(diagrammHoehe);
        stift.hoch();
        stift.dreheBis(0);

        // Beschriftungen am Rand
        stift.bewegeBis(diagrammX+diagrammBreite+5, diagrammY+diagrammHoehe);
        stift.schreibeText("Durchläufe (1..N) -->");
        stift.bewegeBis(diagrammX-60, diagrammY+5);
        stift.schreibeText("Zeit [ms]");
    }

    // -----------------------------------------------------
    // 7) Kurve zeichnen (Beispiel-Methode)
    // -----------------------------------------------------
    /**
     * zeichneKurveFuer(job):
     *  - misst Sortierzeiten (z.B. 10 Durchläufe),
     *  - malt eine Linie pro Messpunkt,
     *  - und wartet 300ms pro Schritt, 
     *    damit man die Linie langsam wachsen sieht.
     */
    private void zeichneKurveFuer(SortierJob job){
        // Falls wir keine Karten haben => Abbruch
        if(hauptKarten==null || hauptKarten.istLeer()){
            return;
        }

        // Anzahl der Durchläufe, hier 10
        int anzahlDurchlaeufe = 10;
        double[] messwerte = new double[anzahlDurchlaeufe];

        // Original-Kartenliste klonen
        Liste<Kartenmanager_Hauptfenster_SuS.Karte> original = kopiereListe(hauptKarten);

        // 1) Messen:
        for(int i=0; i<anzahlDurchlaeufe; i++){
            // Kopie + Mischen => unsortiert
            Liste<Kartenmanager_Hauptfenster_SuS.Karte> test = kopiereListe(original);
            shuffleListe(test);

            // Zeitmessung
            long start = System.nanoTime();
            sortiereListe(job.verfahren, test);
            long end   = System.nanoTime();
            double ms  = (end - start)/1_000_000.0;  // ns -> ms
            messwerte[i] = ms;
        }

        // 2) Maximalwert ermitteln => für y-Skalierung
        double maxTime=1;
        for(double mw : messwerte){
            if(mw> maxTime) maxTime= mw;
        }

        // 3) Farbe einstellen
        setzeFarbe(job.farbeIndex);

        // 4) Kurve: 
        int oldX=-1, oldY=-1;
        for(int i=0; i<anzahlDurchlaeufe; i++){
            // Zeit => ms
            double t= messwerte[i];

            // xPos => diagrammX + prozent * diagrammBreite
            int xPos= diagrammX + (int)(diagrammBreite * ((double)i/(anzahlDurchlaeufe-1)));
            // yPos => diagrammY+diagrammHoehe - skaliertes t
            int yPos= diagrammY + diagrammHoehe - (int)(diagrammHoehe*(t/maxTime));

            // Linie vom letzten Punkt zum aktuellen
            if(oldX!=-1 && oldY!=-1){
                stift.bewegeBis(oldX, oldY);
                stift.runter();
                stift.bewegeBis(xPos, yPos);
                stift.hoch();
            }
            oldX= xPos;
            oldY= yPos;

            // Nach jedem Schritt "bildschirm aktualisieren" + Warten
            fuehreAus();          
            warte(300); 
        }
    }

    // -----------------------------------------------------
    // 8) Sortierverfahren (listenbasiert), Shuffle
    // -----------------------------------------------------
    /**
     * sortiereListe(verfahren, liste):
     *  - Ruft je nach "verfahren" das passende Listen-Sort auf.
     *  - SuS können die Implementationen unten ansehen.
     */
    private void sortiereListe(String verfahren, Liste<Kartenmanager_Hauptfenster_SuS.Karte> liste){
        switch(verfahren.toLowerCase()){
            case "bubblesort":
                bubbleSortListe(liste);
                break;
            case "insertsort":
                insertSortListe(liste);
                break;
            case "selectionsort":
                selectionSortListe(liste);
                break;
            case "quicksort":
                quickSortListe(liste, 1, liste.laenge());
                break;
            default:
                // Falls unbekannt: Bubblesort
                bubbleSortListe(liste);
                break;
        }
    }

    /**
     * bubbleSortListe:
     *   Einfache Listen-basierte Variante
     *   => vergleicht benachbarte Karten und tauscht, 
     *      falls a > b
     */
    private void bubbleSortListe(Liste<Kartenmanager_Hauptfenster_SuS.Karte> list){
        int n= list.laenge();
        for(int end=n; end>1; end--){
            boolean swapped = false;
            list.geheZuPosition(1); 
            for(int i=1; i<end; i++){
                Kartenmanager_Hauptfenster_SuS.Karte a= list.aktuellesElement();
                list.geheZuPosition(i+1);
                Kartenmanager_Hauptfenster_SuS.Karte b= list.aktuellesElement();
                if( istGroesser(a,b) ){
                    list.geheZuPosition(i);
                    list.ersetzeAktuelles(b);
                    list.geheZuPosition(i+1);
                    list.ersetzeAktuelles(a);
                    swapped= true;
                }
            }
            if(!swapped) break;
        }
    }

    /**
     * insertSortListe:
     *   Wir "ziehen" die Karte i an ihre passende Stelle 
     *   in dem bereits sortierten Teil [1..i-1].
     */
    private void insertSortListe(Liste<Kartenmanager_Hauptfenster_SuS.Karte> list){
        int n= list.laenge();
        for(int i=2; i<=n; i++){
            list.geheZuPosition(i);
            Kartenmanager_Hauptfenster_SuS.Karte x= list.aktuellesElement();
            // Wir entfernen x, so dass wir "Platz" haben
            list.ersetzeAktuelles(null);

            int j= i-1;
            while(j>=1){
                list.geheZuPosition(j);
                Kartenmanager_Hauptfenster_SuS.Karte c= list.aktuellesElement();
                if(c==null || istGroesser(c,x)){
                    list.geheZuPosition(j+1);
                    list.ersetzeAktuelles(c);
                    j--;
                } else {
                    break;
                }
            }
            // x an die richtige Stelle (j+1)
            list.geheZuPosition(j+1);
            list.ersetzeAktuelles(x);
        }
    }

    /**
     * selectionSortListe:
     *   Sucht jeweils das Minimum in [i..n] und tauscht es an Position i.
     */
    private void selectionSortListe(Liste<Kartenmanager_Hauptfenster_SuS.Karte> list){
        int n= list.laenge();
        for(int i=1; i< n; i++){
            int minPos= i;
            for(int j=i+1; j<=n; j++){
                if( vergleich(list,j, minPos)<0 ){
                    minPos= j;
                }
            }
            if(minPos!= i){
                swap(list, i, minPos);
            }
        }
    }

    /**
     * quickSortListe:
     *   Teilt die Liste in <Pivot und >=Pivot,
     *   ruft sich rekursiv auf.
     */
    private void quickSortListe(Liste<Kartenmanager_Hauptfenster_SuS.Karte> list, int low, int high){
        if(low<high){
            int p= partition(list, low, high);
            quickSortListe(list, low, p-1);
            quickSortListe(list, p+1, high);
        }
    }

    private int partition(Liste<Kartenmanager_Hauptfenster_SuS.Karte> list, int low, int high){
        int pivot= high;
        int i= low-1;
        for(int j=low; j< high; j++){
            if( vergleich(list,j,pivot) <=0 ){
                i++;
                swap(list,i,j);
            }
        }
        swap(list, i+1, high);
        return i+1;
    }

    // -----------------------------------------------------
    // 9) Hilfsmethoden: Vergleich, Swap, Shuffle, Kopie
    // -----------------------------------------------------
    /**
     * istGroesser(a,b):
     *   Vergleicht zwei Karten a,b: 
     *   - zuerst nach wert (1..13),
     *   - bei Gleichheit nach farbe (0..3).
     */
    private boolean istGroesser(Kartenmanager_Hauptfenster_SuS.Karte a,
                                Kartenmanager_Hauptfenster_SuS.Karte b){
        if(a.wert!= b.wert) return (a.wert> b.wert);
        return (a.farbe> b.farbe);
    }

    /**
     * vergleich(list, posA, posB):
     *   greift auf die Karten in position A/B zu und 
     *   gibt (KarteA - KarteB) zurück.
     *   => negativ, falls A<B
     *   => 0, falls A=B
     *   => positiv, falls A>B
     */
    private int vergleich(Liste<Kartenmanager_Hauptfenster_SuS.Karte> list, int posA, int posB){
        list.geheZuPosition(posA);
        Kartenmanager_Hauptfenster_SuS.Karte ka= list.aktuellesElement();
        list.geheZuPosition(posB);
        Kartenmanager_Hauptfenster_SuS.Karte kb= list.aktuellesElement();
        if(ka.wert!= kb.wert) return ka.wert - kb.wert;
        return ka.farbe - kb.farbe;
    }

    /**
     * swap(list, posA, posB):
     *   tauscht die Karten an Position posA und posB in der Liste
     */
    private void swap(Liste<Kartenmanager_Hauptfenster_SuS.Karte> list, int posA, int posB){
        if(posA==posB) return;  // nichts zu tun

        // Karte an posA holen
        list.geheZuPosition(posA);
        Kartenmanager_Hauptfenster_SuS.Karte ka= list.aktuellesElement();

        // Karte an posB holen
        list.geheZuPosition(posB);
        Kartenmanager_Hauptfenster_SuS.Karte kb= list.aktuellesElement();

        // austauschen
        list.ersetzeAktuelles(ka);
        list.geheZuPosition(posA);
        list.ersetzeAktuelles(kb);
    }

    /**
     * shuffleListe(list):
     *   mischt die Karten, indem wir i..2 tauschen
     */
    private void shuffleListe(Liste<Kartenmanager_Hauptfenster_SuS.Karte> list){
        int n= list.laenge();
        for(int i=n; i>1; i--){
            int r= (int)(Math.random()*i)+1;
            swap(list, i, r);
        }
    }

    /**
     * kopiereListe(original):
     *   legt eine neue Liste an und kopiert jede Karte
     *   aus original als Duplikat rein.
     *   => So können wir "original" unverändert lassen.
     */
    private Liste<Kartenmanager_Hauptfenster_SuS.Karte> kopiereListe(
            Liste<Kartenmanager_Hauptfenster_SuS.Karte> original)
    {
        Liste<Kartenmanager_Hauptfenster_SuS.Karte> copy = new Liste<Kartenmanager_Hauptfenster_SuS.Karte>();
        original.zumAnfang();
        for(int i=1; i<= original.laenge(); i++){
            Kartenmanager_Hauptfenster_SuS.Karte alt = original.aktuellesElement();
            // Duplikat
            Kartenmanager_Hauptfenster_SuS.Karte neu = new Kartenmanager_Hauptfenster_SuS.Karte(alt.wert, alt.farbe);
            copy.zumEnde();
            copy.fuegeDahinterEin(neu);

            original.geheZuPosition(i+1);
        }
        return copy;
    }

    // -----------------------------------------------------
    // 10) Farbsystem
    // -----------------------------------------------------
    /**
     * setzeFarbe(idx):
     *   Index 0..4 => 5 Farben (Rosa,Grau,Orange,Blau,Grün).
     *   fallback = Schwarz
     */
    private void setzeFarbe(int idx){
         switch(idx){
            case 0: stift.setzeFarbe(7); break; // Rosa
            case 1: stift.setzeFarbe(6); break; // Grau
            case 2: stift.setzeFarbe(8); break; // Orange
            case 3: stift.setzeFarbe(2); break; // Blau
            case 4: stift.setzeFarbe(5); break; // Grün
            default: stift.setzeFarbe(0); break; // Schwarz
        }
    }
}
