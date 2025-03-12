import sum.ereignis.*;
import sum.komponenten.*;
import sum.werkzeuge.*;
import sum.strukturen.*;

/**
 * Kartenmanager_Hauptfenster_SuS:
 *  - Erzeugt ein Fenster mit GUI-Elementen, um einen Kartenstapel zu verwalten.
 *  - SuS sollen hier das Undo/Redo (LIFO per Stack) selbst implementieren
 *    und die entsprechenden Knöpfe (undoKnopf, redoKnopf) mit Logik füllen.
 */
public class Kartenmanager_Hauptfenster_SuS extends Ereignisanwendung {

    // -------------------------------------------------------------------
    // GUI-Elemente (Knöpfe, Textfelder, Auswahllisten, Etiketten)
    // -------------------------------------------------------------------
    private Knopf updateKnopf;    
    private Knopf einfKnopf;      
    private Knopf entfKnopf;      
    private Knopf undoKnopf;      // => SuS-Aufgabe: LIFO-Stack für Undo
    private Knopf redoKnopf;      // => SuS-Aufgabe: LIFO-Stack für Redo

    private Knopf grafikKnopf;    // Großer Button "Sortieren->..." (öffnet GrafikFenster)

    private Etikett infoEtikett;  
    private Etikett labelStapelsize; 
    private Etikett labelWert;  
    private Etikett labelFarbe; 
    private Etikett labelPos;   

    private Auswahl auswahlWert;  
    private Auswahl auswahlFarbe;

    private Textfeld tfUmfang;    
    private Textfeld tfPos;       

    private Buntstift stift;      

    // -------------------------------------------------------------------
    // Datenstrukturen
    // -------------------------------------------------------------------
    private Liste<Karte> karten;    // Die aktuelle Kartenliste (mit n Karten).
    private int kartenAnzahl = 52;  // Standardwert für "Stapelgröße" (52 ist ein typisches Kartendeck).

    // Namenstabellen, um Werte und Farben anzuzeigen
    private String[] werteNamen = {
        "", "2", "3", "4", "5", "6", 
        "7", "8", "9", "10", "Bube", "Dame", "König", "Ass"
    };
    private String[] farbenNamen = {
        "Kreuz", "Karo", "Herz", "Pik"
    };

    /**
     * Innere Klasse "Karte":
     *  - Enthält nur zwei Integer: wert (1..13) und farbe (0..3).
     *  - SuS müssen daran nichts verändern.
     */
    public static class Karte {
        public int wert;  
        public int farbe; 
        public Karte(int w, int f){
            wert=w; farbe=f;
        }
    }

    // -------------------------------------------------------------------
    // Konstruktor: Baut das Hauptfenster auf
    // -------------------------------------------------------------------
    public Kartenmanager_Hauptfenster_SuS() {
        // Ereignisanwendung-Konstruktor
        super();

        // Ein Buntstift zum Zeichnen von Karten
        stift = new Buntstift();

        // (1) GUI-Elemente anlegen: 
        //     infoEtikett: Zeigt Status-/Hilfetexte
        infoEtikett = new Etikett(50, 20, 500, 30, 
            "Willkommen zum Kartenmanager!");

        // labelStapelsize + tfUmfang: um die Stapelgröße einzugeben
        labelStapelsize = new Etikett(50, 60, 80, 30, "Stapelgr.:");
        tfUmfang        = new Textfeld(130, 60, 60, 30, "52");
        updateKnopf     = new Knopf(200, 60, 100, 30, "Erneuern");

        // labelWert, labelFarbe, Position => zum Einfügen/Entfernen
        labelWert   = new Etikett(310, 20, 40, 30, "Wert:");
        auswahlWert = new Auswahl(350, 20, 90, 30);

        labelFarbe  = new Etikett(460, 20, 50, 30, "Farbe:");
        auswahlFarbe= new Auswahl(520, 20, 150, 30);

        labelPos    = new Etikett(310, 60, 60, 30, "Position:");
        tfPos       = new Textfeld(380, 60, 60, 30, "0");

        // Buttons für Einfügen/Entfernen
        einfKnopf   = new Knopf(460, 60, 100, 30, "Einfügen");
        entfKnopf   = new Knopf(570, 60, 100, 30, "Entfernen");

        // Undo-/Redo-Knöpfe: Sollen per Stack (LIFO) implementiert werden
        undoKnopf   = new Knopf(680, 20, 100, 30, "Undo");
        redoKnopf   = new Knopf(680, 60, 100, 30, "Redo");

        // Ein großer Knopf zum "Sortieren->..." => öffnet das GrafikFenster
        grafikKnopf = new Knopf(800, 20, 120, 70, "Sortieren->...");
        // setzeBearbeiterGeklickt() => Legt fest, welche Methode ausgeführt wird, wenn geklickt
        grafikKnopf.setzeBearbeiterGeklickt("oeffneGrafikFenster");

        // (2) Auswahllisten füllen:
        //    - werteNamen (1..13) => "2..Ass"
        for(int w=1; w<=13; w++){
            auswahlWert.haengeAn(werteNamen[w]);
        }
        //    - farbenNamen (0..3) => "Kreuz..Pik"
        for(int f=0; f<4; f++){
            auswahlFarbe.haengeAn(farbenNamen[f]);
        }

        // (3) Ereignismethoden festlegen:
        //     Welcher Button ruft welche Methode auf?
        updateKnopf.setzeBearbeiterGeklickt("erneuernKlick");
        einfKnopf.setzeBearbeiterGeklickt("einfuegenKlick");
        entfKnopf.setzeBearbeiterGeklickt("entfernenKlick");
        undoKnopf.setzeBearbeiterGeklickt("undoKlick");
        redoKnopf.setzeBearbeiterGeklickt("redoKlick");

        // (4) Kartenliste anlegen + mit initialer Stapelgröße füllen
        karten = new Liste<Karte>();
        initialisiereKarten(kartenAnzahl);
        // Zeichnung ab y=150 => "Kartenlayout" 
        zeichneKarten(0, 50, 150);

        // Bildschirmausgabe
        this.fuehreAus();
    }

    // -------------------------------------------------------------------
    // (A) Initialisieren & Zeichnen
    // -------------------------------------------------------------------
    void initialisiereKarten(int anzahl){
        // Erzeugt "anzahl" zufällige Karten und legt sie in die Liste "karten"
        karten = new Liste<Karte>();
        for(int i=0; i< anzahl; i++){
            int w= (int)(Math.random()*13)+1;  // Wert 1..13
            int f= (int)(Math.random()*4);     // Farbe 0..3
            karten.zumEnde();
            karten.fuegeDahinterEin(new Karte(w,f));
        }
    }

    void zeichneKarten(int index, int x, int y){
        // Zeichnet alle Karten ab "index" rekursiv 
        if(index>=karten.laenge()) return;
        karten.geheZuPosition(index+1);
        Karte c= karten.aktuellesElement();

        // Erstelle den Text z.B. "Herz Ass" 
        String name = farbenNamen[c.farbe] +" "+ werteNamen[c.wert];

        // Rechteck + Text
        stift.bewegeBis(x,y);
        stift.zeichneRechteck(80,120);
        stift.bewegeBis(x+5, y+50);
        stift.schreibeText(name);

        // Nächste Karte 90px weiter rechts
        x+=90;
        // Nach 13 Karten => nächste Zeile
        if((index+1)%13==0){
            x=50; 
            y+=130;
        }
        // Rekursion
        zeichneKarten(index+1, x, y);
    }

    void loescheAnzeige(){
        // "radiere()" => alles neu zeichnen => Karten "leeren"?
        stift.radiere();
        zeichneKarten(0,50,150);
        stift.normal();
    }

    // -------------------------------------------------------------------
    // (B) Button-Ereignisse
    // -------------------------------------------------------------------
    public void erneuernKlick(){
        // Neuer Stapel + new Size

        int neu=52;
        try{
            // tfUmfang => Text -> Zahl
            neu= Integer.parseInt(tfUmfang.inhaltAlsText().trim());
            if(neu<=0) neu=52; // Schutz
        } catch(NumberFormatException ex){
            neu=52; // Fallback
        }
        kartenAnzahl= neu;

        loescheAnzeige();

        // *** Hier SuS: Stack-LIFO? (z.B. Undo-Vormerkung)

        // Neue Liste
        initialisiereKarten(kartenAnzahl);
        zeichneKarten(0,50,150);
        infoEtikett.setzeInhalt("Neuer Stapel: "+kartenAnzahl+" Karten.");
    }

    public void einfuegenKlick(){
        // "Einfügen" einer Karte an der vom SuS angegebenen Position
        loescheAnzeige();

        // *** Hier SuS: Stack-LIFO? => "undo"

        int pos=1;
        try{
            // tfPos => z.B. "4" => int => +1
            pos= Integer.parseInt(tfPos.inhaltAlsText().trim())+1;
        }catch(NumberFormatException e){ }

        int w= auswahlWert.index();     
        int f= auswahlFarbe.index()-1;

        // Grenzen korrigieren
        if(pos<1) pos=1;
        if(pos> karten.laenge()+1) pos= karten.laenge()+1;

        // Füge neue Karte (w,f) an Position "pos" ein
        karten.geheZuPosition(pos);
        karten.fuegeDavorEin(new Karte(w,f));

        zeichneKarten(0,50,150);
        infoEtikett.setzeInhalt("Karte an Pos "+(pos-1)+" eingefügt.");
    }

    public void entfernenKlick(){
        // "Entfernen" einer Karte an gegebener Position
        loescheAnzeige();
        
        // *** Hier SuS: Stack-LIFO? => "undo"

        int pos=1;
        try{
            pos= Integer.parseInt(tfPos.inhaltAlsText().trim())+1;
        }catch(NumberFormatException e){ }

        if(pos>=1 && pos<= karten.laenge()){
            karten.geheZuPosition(pos);
            karten.entferneAktuelles();
            infoEtikett.setzeInhalt("Karte an Pos "+(pos-1)+" entfernt.");
        } else {
            infoEtikett.setzeInhalt("Ungültige Position!");
        }
        zeichneKarten(0,50,150);
    }

    public void undoKlick(){
        // TODO SuS: LIFO-Logik => alten Zustand wiederherstellen
        infoEtikett.setzeInhalt("Undo ist noch zu implementieren.");
    }
    
    public void redoKlick(){
        // TODO SuS: LIFO-Logik => rückgängig gemachte Änderungen wiederherstellen
        infoEtikett.setzeInhalt("Redo ist noch zu implementieren.");
    }

    // -------------------------------------------------------------------
    // (C) Neuer großer Button => Öffnet das GrafikFenster
    // -------------------------------------------------------------------
    public void oeffneGrafikFenster(){
        // Erzeugt ein GrafikFenster_SuS
        GrafikFenster_SuS gf= new GrafikFenster_SuS();
        // Übergibt unsere "karten" (Liste<Karte>)
        gf.setzeKartenliste(karten);
        infoEtikett.setzeInhalt("Grafikfenster geöffnet.");
    }
}
