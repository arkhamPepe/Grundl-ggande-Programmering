import java.util.Random;
import java.util.Scanner;

import static java.lang.System.*;

/*
 * The Pig game
 * See http://en.wikipedia.org/wiki/Pig_%28dice_game%29
 */
public class Pig {

    public static void main(String[] args) {
        new Pig().program();
    }

    // The only allowed instance variables (i.e. declared outside any method)
    // Accessible from any method
    final Scanner sc = new Scanner(in);
    final Random rand = new Random();

    void program() {
        //test();                 // <-------------- Uncomment run to tests!
        int winPts = 20;          // Points to win
        Player[] players;         // The players (array of Player objects)
        boolean aborted = false;  // Game aborted by player
        int playerTurn = 0;
        boolean nextTurn; //if true then switches player
        boolean playVSComp = false;
        Computer cp = new Computer();
        cp.name = "Computer";

        welcomeMsg(winPts);

        playVSComp = gameMode(); // Player selects the gamemode (VS computer or not)

        players = getPlayers();

        while (!checkWin(players, winPts) && !aborted)
        {
            // Each player's total points is listed.
            statusMsg(players);
            nextTurn = false; // True --> the next player gets to play. | False --> the current player keeps playing.

            // So long as current player has not chosen to "hold" or rolled a 1, this keeps running.
            while (!nextTurn)
            {

                // Player chooses whether to roll dice, hold or quit the game entirely.
                switch (playerChoice(players[playerTurn]))
                {
                    case "r":
                        nextTurn = playerRoll(players[playerTurn]);
                        break;
                    case "n":
                        playerHold(players[playerTurn]);
                        nextTurn = true; // Break out of loop
                        break;
                    case "q":
                        aborted = true;
                        nextTurn = true;
                        break;
                    default:
                        out.println("That's not a valid option");
                        break;
                }
            }
            //above the human turns
            //below cumputer takes it's turns

            if (playVSComp && playerTurn + 1 == players.length)
            {
                while (cp.compChoice(comparePoints(players)))
                cp.compRoll();
                //TODO need to make it loop so that computer can keep rolling/ stop rolling when needed, probably related to compChoice
            }

            //Next player's turn
            playerTurn++;
            playerTurn = playerTurn % players.length;
        }

        if (aborted)
        {
            abortedMessage();
        }
        else{
            if (playerTurn == 0)
            {
                playerTurn = players.length;
            }
            gameOverMsg(players[playerTurn-1]);
        }
    }

    // ---- Game logic methods --------------



    // Player chooses to hold.
    void playerHold(Player p)
    {
        out.println(p.name + " decided to hold; points: " + p.roundPts);
        p.totalPts += p.roundPts;
        p.roundPts = 0;
    }

    // Player rolls the dice.
    boolean playerRoll(Player p)
    {
        int diceRoll = diceToss();

        if (diceRoll == 1)
        {
            p.roundPts = 0;
            out.println(p.name + " rolled a 1 and passes the turn...");
            return true;
        }
        else
        {
            p.roundPts += diceRoll;
            out.println(p.name + " rolled a " + diceRoll + ", <Round points: " + p.roundPts + "> <Total points: " + p.totalPts + ">");
            return false;
        }
    }

    // Player will win with enough points
    boolean checkWin(Player[] players, int winPoints)
    {
        for (int i = 0; i < players.length; i++)
        {
            if (players[i].totalPts >= winPoints)
            {
                return true;
            }
        }
        return false;
    }

    int diceToss()
    {
        return rand.nextInt(6) + 1;
    }

    int comparePoints(Player[] p)
    {
        int leader = 0; // Highest current score

        for (int i = 0; i < p.length; i++)
        {
            if (p[i].totalPts > leader)
            {
                leader = p[i].totalPts;
            }
        }
        return leader;
    }


    // ---- IO methods ------------------

    boolean gameMode()
    {
        String choice;
        out.print("Do you wish to play VS the computer? (y / n) > ");
        choice = sc.next();
        if (choice == "y")
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Returns player choice
    String playerChoice(Player p)
    {
        //"R" or "N"
        out.print(p.name + " roll (r) or hold (n) > ");
        return sc.next();
    }

    // Greets the players.
    void welcomeMsg(int winPoints) {
        out.println("Welcome to PIG!");
        out.println("First player to get " + winPoints + " points will win!" );
        out.println("Commands are: r = roll , n = next, q = quit");
        out.println();
    }

    // Show current points for all players
    void statusMsg(Player[] players) {
        out.println();
        out.print("Points: ");
        for (Player p : players) {
            out.print(p.name + " = " + p.totalPts + " ");
        }
        out.println();
    }

    Player[] getPlayers() {
        out.print("How many players? > ");

        int nPlayers;

        // Defaults to 3 players for bad input.
        try {
            nPlayers = sc.nextInt();
        }
        catch(Exception e) {
            out.println("Bad input!");
            nPlayers = 3;
            sc.nextLine(); // Empties scanner
        }

        //sc.nextLine();  // Read away \n
        Player[] players = new Player[nPlayers];

        // Initialize and give all players names
        for (int i = 0; i < nPlayers; i++){
            players[i] = new Player();
            out.println("Enter your name, player " + (i+1) + ": ");
            players[i].name = sc.next();
        }

        return players;
    }

    // Prints a game over message.
    void gameOverMsg(Player p) {
        out.println(p.name + " has won the game with a total of " + p.totalPts + " points!");
    }

    // Prints an "game aborted" message.
    void abortedMessage(){
        out.println("The game has been TERMINATED!");
    }

    // ---------- Class -------------
    // A class makes it possible to keep all data for a Player in one place
    // Use the class to create (instantiate) Player objects
    class Player {
        String name;
        int totalPts;    // Total points for all rounds, default 0
        int roundPts;    // Points for a single round, default 0
    }



    class Computer extends Player {

        // roll = true, hold = false
        public boolean compChoice(int leader)
        {
            if (leader - this.totalPts > 10)
            {
                if (this.roundPts < 16) {
                    return true;
                } else {
                    return false;
                }
            }
            else {
                if (this.roundPts < 9) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        // Computer rolls dice
        public boolean compRoll()
        {
            int diceRoll = diceToss();

            if (diceRoll == 1)
            {
                this.roundPts = 0;
                out.println(this.name + " rolled a 1 and passes the turn...");
                return true;

            }
            else
            {
                this.roundPts += diceRoll;
                out.println(this.name + " rolled a " + diceRoll + ", <Round points: " + this.roundPts + "> <Total points: " + this.totalPts + ">");
                return false;
            }
        }


    }
}



