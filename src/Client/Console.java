package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * @author jo
 *
 * Die Klasse Console dient zur einfachen Ein- und
 * Ausgabe auf der Konsole.
 *
 */
public class Console {
	/**
	 * Der Konsolen-Eingabestrom
	 */
	private final InputStreamReader	input = new InputStreamReader(System.in);
	/**
	 * Das Konsolen-Leseobjekt
	 */
	private final BufferedReader		reader = new BufferedReader(input);	
	
	/**
	 * Parameterloser Konstruktor.
	 * Ein Console Object wird angelegt.
	 */
	public Console()
	{
			
	}
	
	/**
	 * Einlesen einer Textzeile.
	 * Bei einer I/O Exception erfolgt eine erneute Eingabeaufforderung.
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return die vom Benutzer eingegebene Textzeile.
	 */
	public String readString(String ask)
	{
		String		text = "";
		
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				return text;
			} 
			catch (IOException e) {
				handleException(e);
			}	
		}
	}
	
	/**
	 * Einlesen einer Zahl im byte Format.
	 * Bei einer I/O Exception erfolgt eine erneute Eingabeaufforderung.
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return die vom Benutzer eingegebene Zahl.
	 */
	public byte readByte(String ask)
	{
		byte		zeichen = 0;
		String		text;
		
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				zeichen = Byte.parseByte(text);
				return zeichen;				// Erfolg!
			} 
			catch (Exception e) {
				handleException(e);
			}	
		}
	}
	
	/**
	 * Einlesen eines Zeichen.
	 * Bei einer I/O Exception erfolgt eine erneute Eingabeaufforderung.
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return das vom Benutzer eingegebene Zeichen.
	 */
	public char readChar(String ask)
	{
		char		zeichen = 0;
		String		text;
		
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				zeichen = text.charAt(0);
				return zeichen;			// Erfolg!
			} 
			catch (Exception e) {
				handleException(e);			
			}	
		}
	}
	
	/**
	 * Einlesen einer 16-Bit Zahl (short).
	 * Bei einer I/O Exception erfolgt eine erneute Eingabeaufforderung.
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return die vom Benutzer eingegebene Zahl.
	 */	
	public short readShort(String ask)
	{
		short		zahl = 0;
		String		text;
		
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				zahl = Short.parseShort(text);
				return zahl;
			} 
			catch (Exception e) {
				handleException(e);			
			}	
		}
	}
	
	/**
	 * Einlesen einer 32-Bit Zahl (int).
	 * Bei einer I/O Exception erfolgt eine erneute Eingabeaufforderung.
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return die vom Benutzer eingegebene Zahl.
	 */	
	public int readInt(String ask)
	{
		int		zahl = 0;
		String		text;
		
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				zahl = Integer.parseInt(text);
				return zahl;
			} 
			catch (Exception e) {
				handleException(e);
			}	
		}
	}
	
	/**
	 * Einlesen einer 64-Bit Zahl (long).
	 * Bei einer I/O Exception erfolgt eine erneute Eingabeaufforderung.
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return die vom Benutzer eingegebene Zahl.
	 */		
	public long readLong(String ask)
	{
		long		zahl = 0;
		String		text;
		
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				zahl = Long.parseLong(text);
				return zahl;
			} 
			catch (Exception e) {
				handleException(e);
			}	
		}
	}
	
	/**
	 * Einlesen einer Fließkommazahl Zahl (float).
	 * <b>Achtung:</b> Das Trennzeichen ist der Punkt ("5.3").
	 * Bei einer I/O Exception erfolgt eine erneute Eingabeaufforderung.
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return die vom Benutzer eingegebene Zahl.
	 */	
	public float readFloat(String ask)
	{
		float		zahl = 0.0f;
		String		text;
		
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				zahl = Float.parseFloat(text);
				return zahl;
			} 
			catch (Exception e) {
				handleException(e);
			}	
		}
		
	}
	
	/**
	 * Einlesen einer Fließkommazahl Zahl (double).
	 * <b>Achtung:</b> Das Trennzeichen ist der Punkt ("5.3").
	 * Bei einer I/O Exception erfolgt eine erneute Eingabeaufforderung.
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return die vom Benutzer eingegebene Zahl.
	 */	
	public double readDouble(String ask)
	{
		double		zahl = 0;
		String		text;
		
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				zahl = Double.parseDouble(text);
				return zahl;
			} 
			catch (Exception e) {
				handleException(e);
			}	
		}
	}
	
	/**
	 * Einlesen einer Fließkommazahl Zahl (double).
	 * <b>Achtung:</b> Das Trennzeichen hängt von den Systemeinstellungen ab.
	 * Bei einer I/O Exception erfolgt eine erneute Eingabeaufforderung.
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return die vom Benutzer eingegebene Zahl.
	 */	
	public double readDoubleLoc(String ask)
	{
		double			zahl = 0;
		String			text;
			
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				zahl = String2Double(text);
				return zahl;
			} 
			catch (Exception e) {
				handleException(e);
			}	
		}
	}
	
	/**
	 * Ausgabe einer Fließkommazahl Zahl (double).
	 * <b>Achtung:</b> Das Trennzeichen hängt von den Systemeinstellungen ab.
	 * @param <i>form.</i> Das gewünschte Zahlenformat z.B. "#.###" entspricht
	 * 3 Nachkommastellen.
	 * <i>z.</i> Die auszugebende Zahl.
	 */	
	public void printDouble(String form, double z)
	{
		System.out.print(Double2String(form, z));		
	}
	
	/**
	 * Einlesen einer Uhrzeit (Date).
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return die vom Benutzer eingegebene Uhrzeit.
	 */	
	public Date readTime(String ask)
	{
		Date			time = null;
		String			text;
		DateFormat		df = DateFormat.getTimeInstance();
			
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				time = df.parse(text);
				return time;
			} 
			catch (Exception e) {
				handleException(e);
			}	
		}
	}

	/**
	 * Ausgeben einer Uhrzeit (Date).
	 */	
	public void printTime(Date time)
	{
		String			text;
		DateFormat		df = DateFormat.getTimeInstance();

		text = df.format(time);
		System.out.print(text);			
	}	

   /**
	 * Einlesen eines Datum (Date).
	 * @param ask Eingabeaufforderung an den Benutzer.
	 * @return das vom Benutzer eingegebene Datum.
	 * 
	 */	
	public Date readDate(String ask)
	{
		Date			date = null;
		String			text;
		DateFormat		df = DateFormat.getDateInstance();
			
		while (true) {
			System.out.print(ask);
			try {
				text = reader.readLine();
				date = df.parse(text);
				return date;
			} 
			catch (Exception e) {
				handleException(e);
			}	
		}
	}
	
   /**
	 * Ausgeben einer Uhrzeit (Date).
	 * 
	 */	
	public void printDate(Date time)
	{
		String			text;
		DateFormat		df = DateFormat.getDateInstance();

		text = df.format(time);
		System.out.print(text);			
	}
	
   /**
	 * Bearbeiten einer Exception. Message ausgeben. StackTrace anzeigen.
	 * @param e die zu verarbeitende Exception.
	 * 
	 */	
	static private void handleException(Exception e)
	{
		System.out.println(e.getMessage());
		e.printStackTrace();		
		System.out.println();		
	}
 
   /**
	 * Umwandeln eines Text in einen double Wert (localisiert)
	 * @param text - der Eingabetext
	 * @exception ParseException 
	 * @return der double Wert
	 * 
	 */		
	static public double String2Double(String text) throws ParseException
	{
		double 		zahl = 0.0;
		DecimalFormat	df 	 = new DecimalFormat();

		text = text.toUpperCase();
		zahl = df.parse(text).doubleValue();
		return zahl;
	}	

   /**
	 * Umwandeln eines double Wert in einen String (localisiert)
	 * @param form - der Eingabetext
	 * @param zahl - die Zahl
	 * @return der String 
	 * 
	 */			
	static public String Double2String(String form, double zahl)
	{
		String			text;
		DecimalFormat	df = new DecimalFormat(form);
		
		text = df.format(zahl);
		
		return text;	
	}
	
	
	/**
	 * Der String in wird von links mit Leerzeichen aufgefüllt bis
	 * er die gewünschte Länge (anz) hat. z.B. in = "3" und anz = 4
	 * ergibt "   3".
	 * 
	 * @param in - der Eingabestring
	 * @param anz - gewünscht Länge des String
	 * @return der aufgefüllte String
	 */
	static public String padStringLeft(String in, int anz)
	{
		String	out = new String();
		
		for (int i=0; i<anz-in.length();i++) {
			out += " ";	
		}
		out += in;
		
		return out;
	}
	
	
   /**
	 * Der String in wird von rechts mit Leerzeichen aufgefüllt bis
	 * er die gewünschte Länge (anz) hat. z.B. in = "3" und anz = 4
	 * ergibt "3   ".
	 * 
	 * @param in - der Eingabestring
	 * @param anz - gewünscht Länge des String
	 * @return der aufgefüllte String
	 */
	static public  String padStringRight(String in, int anz)
	{
		String	out = new String();
		
		out += in;
		for (int i=0; i<anz-in.length();i++) {
			out += " ";	
		}
		
		return out;
	}

};	// End-of-Class-->Console