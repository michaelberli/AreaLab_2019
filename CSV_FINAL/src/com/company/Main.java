package com.company;


import java.io.*;
import java.util.concurrent.TimeUnit;
import com.fazecast.jSerialComm.*;

public class Main {
    public static boolean newline(char c){
        if (c=='\n')
            return true;
        else
            return false;
    }
    public static String temperatura(String s){
        String q=s.substring(1);
        int i=0;
        char umi;
        String temp="";
        while(q.charAt(i)!='U'&&i<s.length()-2){
            temp+=q.charAt(i);
            i++;
        }
        return temp;
    }
    public static String umidita(String s){
        int i=0;
        while (s.charAt(i)!='U'&&i<s.length()-1)
            i++;
        return s.substring(i+1);
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        SerialPort sp = SerialPort.getCommPort("/dev/cu.usbmodem143401"); //Porta seriale dal quale legge l'input
        sp.setComPortParameters(9600, 8, 1, 0); //Parametri default di Arduino
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); // Default lettura byte
        if (sp.openPort()) {
            System.out.println("In trasmissione");
        } else {
            System.out.println("Errore: porta seriale non collegata!");
            return;
        }
        int rigatemperatura=27;
        int rigaumidita=36;
        InputStream in = sp.getInputStream();
        BufferedReader fr = new BufferedReader(new FileReader("Sito.html"));
        String riga;
        int righedelfile=49;
        String righelette[]=new String [righedelfile];
        int i=0;
        while((riga=fr.readLine())!=null){
            righelette[i]=riga;
            i++;
        }
        BufferedWriter csvwriter = new BufferedWriter(new FileWriter("dati.csv"));
        csvwriter.write("Numero rilevamento;Temperatura;Umidita'");
        csvwriter.newLine();
        int iterazione=0;
        fr.close();
        String s;
        try
        {   for (int x = 0; x < 2; x++) {
            s = "";
            char z;
            while (!newline(z = (char) in.read()))
                s += z;
        }
            for (int j = 0; j < 10; j++) {
                s = "";
                char z;
                while (!newline(z = (char) in.read()))
                    s += z;
                righelette[rigatemperatura]=temperatura(s);
                righelette[rigaumidita]=umidita(s);
                BufferedWriter fw = new BufferedWriter(new FileWriter("Sito.html"));
                for (int d = 0; d < righedelfile; d++) {
                    fw.write(righelette[j]);
                    fw.newLine();
                }
                fw.close();
                csvwriter.write(iterazione+";"+righelette[rigatemperatura]+";"+righelette[rigaumidita]);
                csvwriter.newLine();
                iterazione++;
                TimeUnit.SECONDS.sleep(1);
            }
            csvwriter.close();
            //Converte il byte appena letto in char
        } catch (Exception e) { e.printStackTrace(); }
        sp.closePort();                            //Chiude il collegamento seriale da Arduino
    }

}