package org.academiadecodigo.littlecoins.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;


/**
 * Created by codecadet on 17/06/16.
 */
public class ServerWorker implements Runnable {

    private Server server;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private Socket playerSocket = null;
    private boolean alreadyWin;
    private boolean correctName;
    private boolean correctBet;
    private boolean correctGuess;
    private String name;
    private String bet;
    private String guess;



    public ServerWorker(Socket playerSocket, Server server) {

        this.playerSocket = playerSocket;
        this.server = server;


    }

    @Override
    public void run() {

        try {

       /*
       * Create input and output streams for this client.
       */
            out = new PrintWriter(playerSocket.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));


            out.println("Hi ... Welcome to little coins Game...");


            out.println("Please enter your name.");


            acceptName();


            out.println("You have 3 Coins , choose how many coins you want to bet..");


            while (!isAlreadyWin()) {


                acceptBet();

                out.println("Please insert your guess between 0 and " + (server.getCounterPlayers() * 3) + " coins\"!!!");

                acceptGuess();


                if (server.getGame().hasWin()){

                    System.out.println("alguem ganhou");
                }




                while (true) {
                    String line = in.readLine();

                }


            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private synchronized void acceptName() {

        while (!correctName) {
            String tName = null;
            try {
                tName = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (server.containsName(tName) || tName.equals("")) {

                out.println("This name is already in the list. Please change name");

            } else {
                setName(tName);
                //out.println("Name changed to " + tName);
                correctName = true;
                out.println("Your name was accepted!");
            }


        }

    }

    private void acceptBet() {

        synchronized (server) {

            while (!correctBet) {

                String tempBet = null;
                try {
                    tempBet = in.readLine();


                    if (server.getGame().correctBet(tempBet)) {
                        bet = tempBet;
                        out.println(bet);
                        out.println("Bet accept!");//nao alterar este print é a condiçao de saida do while na playerThread
                        System.out.println("Player " + name + " hand is: " + bet);
                        correctBet = true;
                        server.bet(bet);


                    } else {
                        out.println("Make sure you hand between 0 or 3 coins!");
                        out.println("Place your hand , between 0 - 3 : ");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private synchronized void acceptGuess() {

        while (!correctGuess) {

            String tempGuess = null;
            try {
                tempGuess = in.readLine();

                if (!server.getGame().isHighGuess(tempGuess)) {

                    if (server.getGame().correctGuess(tempGuess)) {
                        guess = tempGuess;
                        out.println(guess);
                        out.println("Guess accept!");
                        System.out.println("Player " + name + " guess is: " + guess);
                        correctGuess = true;
                        server.guess(name, guess);

                    } else {
                        out.println("Your guess is already taken!");
                    }

                } else {
                    out.println("Make sure your guess is not bigger than " + server.getGame().getTotalCoins());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public void send(String msg, String name) {

        out.println("(" + new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()) + " ) " + name + " bet : " + msg);

    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getBet() {
        return bet;
    }

    public String getGuess() {
        return guess;
    }

    public boolean isAlreadyWin() {
        return alreadyWin;
    }

    public void setAlreadyWin() {
        this.alreadyWin = true;
    }
}
