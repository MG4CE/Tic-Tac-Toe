import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL; 

/**
 * A class modelling a tic-tac-toe game using a GUI, this 
 * class handles both the logic of the game and the display 
 * of the GUI.
 */

public class TicTacToe implements ActionListener
{  
   //Logic and game properties fields
   private String[][] logicBoard;
   public static final String PLAYER_X = "X"; // player using "X"
   public static final String PLAYER_O = "O"; // player using "O"
   public static final String TIE = "Tie";
   private String currentPlayer;
   private int numFreeSquares, playerXWins, playerOWins, ties;
   
   //GUI fields
   private JLabel gameStatus, stats, endGameScreen;
   private JButton[][] playBoard;
   private JButton newGameB, quitB;
   private JMenuItem newGame, resetStats, quit, switchPlayer;
   private ImageIcon x, o;
   private static final int SIZE = 3;
   
   //Audio fields
   AudioClip click;
   URL gameWinSound, gameTieSound;
   
   /** 
    * Constructs a new Tic-Tac-Toe game frame, fetches 
    * resources and initializes all required fields.
    */
   public TicTacToe()
   { 
      //Setting up logic board and game properties
      currentPlayer = PLAYER_X;
      numFreeSquares = 9;
      playerXWins = 0;
      playerOWins = 0;
      ties = 0;
      logicBoard = new String[SIZE][SIZE];
      
      //Initializing sound files
      gameWinSound = TicTacToe.class.getResource("resources/001.wav");
      gameTieSound = TicTacToe.class.getResource("resources/002.wav");
      
      //Initializing X and O pictures
      x = new ImageIcon("resources/x.png");
      o = new ImageIcon("resources/o.png");
       
      JFrame frame = new JFrame("TicTacToe");
       
      Container contentPane = frame.getContentPane();
      contentPane.setSize(400, 500);
      
      //Menu bar setup
      JMenuBar menuBar = new JMenuBar();
      JMenu game = new JMenu("Game");
      newGame = new JMenuItem("New Game");
      newGame.addActionListener(this);
      switchPlayer = new JMenuItem("Switch First Player");
      switchPlayer.addActionListener(this);
      resetStats = new JMenuItem("Reset Stats");
      resetStats.addActionListener(this);
      quit = new JMenuItem("Quit");
      quit.addActionListener(this);
      game.add(newGame);
      game.add(switchPlayer);
      game.add(resetStats);
      game.add(quit);
      menuBar.add(game);
      frame.setJMenuBar(menuBar);
      
      //Shortcup setup for menu items
      final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); // to save typing
      newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
      quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
      switchPlayer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
      resetStats.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));
      
      //Label setup for game status and statistics
      Font myFont = new Font("Dialog", Font.BOLD, 16);
      stats = new JLabel("Player X Wins: " + playerXWins  + "     Ties: " + ties + "     Player O Wins: " + playerOWins, SwingConstants.CENTER);
      stats.setFont(myFont);
      contentPane.add(stats, BorderLayout.NORTH);
      gameStatus = new JLabel("Game in Progress: " + currentPlayer + "'s Turn", SwingConstants.CENTER);
      gameStatus.setFont(myFont);
      contentPane.add(gameStatus, BorderLayout.SOUTH);
      
      //Initializing play buttons in a 3 by 3 grid placed in the middle
      JPanel board = new JPanel();
      board.setLayout(new GridLayout(3, 3)); 
      playBoard = new JButton[SIZE][SIZE];
      for(int i = 0; i < SIZE; i++){
          for(int j = 0; j < SIZE; j++){
              playBoard[i][j] = new JButton();
              board.add(playBoard[i][j]);
              playBoard[i][j].addActionListener(this);
              playBoard[i][j].setVisible(true);
              playBoard[i][j].setOpaque(false);
              logicBoard[i][j] = "";
          }
      }
      contentPane.add(board, BorderLayout.CENTER);
      
      //Final frame setup
      frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
      frame.setResizable(false);
      frame.setVisible(true);
      frame.pack();
      frame.setSize(400, 500);
   }
   
   /**
    * Takes an incoming button and places an X or and O based
    * on the current player. Checks for win condtion after placement
    * and switchs current player or ends game if necessary. 
    * 
    * @param button, the JButton clicked by the player
    */
   public void play(JButton button)
   {
       String gameWinner = "";
       
       button.removeActionListener(this);
       numFreeSquares--;
       
       //Look for the button pressed in the logic board
       //and check for a winner.
       for(int i = 0; i < SIZE; i++){
           for(int j = 0; j < SIZE; j++){
               if(playBoard[i][j] == button){
                   logicBoard[i][j] = currentPlayer;
                   if(haveWinner(i, j)){ 
                        gameWinner = currentPlayer;
                        break;
                   }
               }
           }
       }
       
       //Set icon of button clicked
       if(currentPlayer.equals(PLAYER_X)){
           button.setIcon(x);
       } 
       else if (currentPlayer.equals(PLAYER_O)){
           button.setIcon(o);
       }
       
       //End game and play sound on game win or tie
       if(gameWinner.equals(PLAYER_X) || gameWinner.equals(PLAYER_O)){
           endGame(gameWinner);
           click = Applet.newAudioClip(gameWinSound);
           click.play();
           return;
       }
       else if(numFreeSquares == 0){
           endGame(TIE);
           click = Applet.newAudioClip(gameTieSound);
           click.play();
           return;
       }
       
       //Switch players
       if(currentPlayer.equals(PLAYER_X)){
           currentPlayer = PLAYER_O;
       } 
       else{
           currentPlayer = PLAYER_X;
       }
       
       gameStatus.setText("Game in Progress: " + currentPlayer + "'s Turn");
   }
   
   /**
    * Returns true if filling the given square gives us a winner, and false
    * otherwise.
    *
    * @param int row of square just set
    * @param int col of square just set
    * 
    * @return true if we have a winner, false otherwise
    */
   private boolean haveWinner(int row, int col) 
    {
        // unless at least 5 squares have been filled, we don't need to go any further
        // (the earliest we can have a winner is after player X's 3rd move).
        
        if (numFreeSquares>4) return false;
        
        // check row "row"
        
        if ( logicBoard[row][0].equals(logicBoard[row][1]) &&
        logicBoard[row][0].equals(logicBoard[row][2]) ) return true;
        
        // check column "col"
        if ( logicBoard[0][col].equals(logicBoard[1][col]) &&
        logicBoard[0][col].equals(logicBoard[2][col]) ) return true;
        
        // if row=col check one diagonal
        if (row==col)
        if ( logicBoard[0][0].equals(logicBoard[1][1]) &&
           logicBoard[0][0].equals(logicBoard[2][2]) ) return true;
        
        // if row=2-col check other diagonal
        if (row==2-col)
          if ( logicBoard[0][2].equals(logicBoard[1][1]) &&
               logicBoard[0][2].equals(logicBoard[2][0]) ) return true;
        
        // no winner yet
        return false;
   }
   
   /**
    * Disables all action listens on buttons,
    * and updates statisics and status labels 
    * 
    * @param winner, string of the winning player
    */
   public void endGame(String winner)
   {
       //Disable all buttons and gray out unclicked buttons
       for(int i = 0; i < SIZE; i++){
           for(int j = 0; j < SIZE; j++){
               playBoard[i][j].removeActionListener(this);
               if(playBoard[i][j].getIcon() == null){
                   playBoard[i][j].setBackground(Color.GRAY);
               }
           }
       }
       
       //Update stats and game status
       if(winner.equals(TIE)){
           gameStatus.setText("Game Ends in a Tie");
           ties++;
           updateStats();
       }
       else if(winner.equals(PLAYER_X)){
           gameStatus.setText(winner + " Wins the Game!");
           playerXWins++;
           updateStats();
       }
       else if(winner.equals(PLAYER_O)){
           gameStatus.setText(winner + " Wins the Game!");
           playerOWins++;
           updateStats();
       }
   }
   
   /**
    * Reset the play board of button, logic board and
    * game properties for a new game. 
    */
   public void resetGame()
   {
       if(numFreeSquares != 9){ //Only if game started 
           for(int i = 0; i < SIZE; i++){
               for(int j = 0; j < SIZE; j++){
                   //Remove action before adding to avoid double registration
                   if(playBoard[i][j].getIcon() == null){
                       playBoard[i][j].removeActionListener(this);
                   }
                   playBoard[i][j].setIcon(null);
                   playBoard[i][j].setBackground(null);
                   playBoard[i][j].addActionListener(this);
                   logicBoard[i][j] = "";
               }
           }
           numFreeSquares = 9;
           currentPlayer = PLAYER_X;
           gameStatus.setText("Game in Progress: " + currentPlayer + "'s Turn");
       }
   }
   
   /**
    * Updates the stats JLabel
    */
   public void updateStats()
   {
       stats.setText("Player X Wins: " + playerXWins  + "     Ties: " + ties + "     Player O Wins: " + playerOWins);
   }
   
   /**
    * Preforms the proper action based on the GUI
    * component clicked.
    * 
    * @param e, the component clicked by user
    */
   public void actionPerformed(ActionEvent e)
   {
       Object o = e.getSource();
       
       if(o instanceof JButton){ //When a game button is clicked
           JButton b = (JButton)o;
           play(b);
       }
       else if(o instanceof JMenuItem) { // When a menu item is clicked
           JMenuItem m = (JMenuItem)o;
           if(m == newGame){
               resetGame();
           }
           else if(m == resetStats){
               playerXWins = 0;
               playerOWins = 0;
               ties = 0;
               updateStats();
           }
           else if(m == quit){
               System.exit(0);
           }
           else if(m == switchPlayer){
               if(numFreeSquares == 9){
                   if(currentPlayer.equals(PLAYER_X)){
                       currentPlayer = PLAYER_O;
                   } 
                   else{
                       currentPlayer = PLAYER_X;
                   }
                   gameStatus.setText("Player's Switched: " + currentPlayer + "'s Turn");
               }
           }
       }
   }
}