import java.awt.*;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.*; 
//import javax.swing.border.Border;
//import javax.swing.border.CompoundBorder;

import java.util.*;

public class SudokuLayout{ 
 
  JFrame f = new JFrame();
  int count = 0; 
  private static Set<Integer> myarray = new LinkedHashSet <>();
  public static void main(String [] args) throws Exception{

 // from main at Sudoku
    Random rand = new Random(); 
   int random = rand.nextInt(2); 
   //arraylist to store the initial grid you want to get solved
   ArrayList <String> list  = new ArrayList<String>(); 
   list.add("sudokuPuzzle.txt");
   list.add("puzzletwo.txt");
   
   //reads random file from the list
  InputStream in = new FileInputStream(list.get(random));
  int puzzleSize = 3;
  if( puzzleSize > 81) {
   System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
   System.exit(-1);
  }
  Sudoku s = new Sudoku(puzzleSize);
  s.read(in);
  s.solve();

  int[][] board = s.getGrid();
  // create SudokuLayout object name sudoku
    SudokuLayout sudoku = new SudokuLayout(); 
      int flag = 0;
    for(int row = 0 ;row<board.length;row+=3){
      for(int col = 0 ; col < board[row].length; col++){
      createSmallBox(board, row, col, sudoku);
      flag++;
      // if myarray hashset size reaches 9 and flag == 3
      if(myarray.size() == 9 && flag % 3 ==0){
        sudoku.createLayout(myarray.stream().filter(k -> k != null).mapToInt(k -> k).toArray());
        // after hashset reaches size 9 and has printed out array
        //clear the hashset to store new data
        // if not cleared same data gets printed over and over again
        myarray.clear();      
        }
      }
    }
    sudoku.build(); 
  }
  // 3*3 sub grid
  // reads 3*3 sub grid from solved board and store them in hashset
    public static void createSmallBox(int[][] board, int row, int column, SudokuLayout sudoku){
      int localRowBox = row - row % 3;
      int localColumnBox= column - column % 3;
      for(int i = localRowBox ; i< localRowBox + 3; i++){
          for(int j = localColumnBox; j<localColumnBox + 3; j++){    
            // add it to hashset myarray
            myarray.add(board[i][j]);
        }
      }
    }
  // swing layout 
    public void createLayout(int []arr){
    //grid layout consists of rows and columns
    GridLayout layout = new GridLayout(3,3); 
    JPanel pane = new JPanel(layout); 
    //border widths
    final int outerBorderWidth = 6;
    final int innerBorderWidth = 1; 
    JLabel label;
    for(int i = 0; i<arr.length;i++){
              String str = String.valueOf(arr[i]); 
        label = new JLabel(str,SwingConstants.CENTER);
        label.setFont(new Font("Verdana",Font.PLAIN,30));
        label.setBorder(BorderFactory.createMatteBorder(innerBorderWidth,  innerBorderWidth, innerBorderWidth,innerBorderWidth,Color.BLACK));
        pane.add(label);
    }
    //if count is even choose one color else choose another. 
    int check = count%2;
        if(check == 0){
          pane.setBackground(new Color(102, 204, 255));
        }
        else{
          pane.setBackground(new Color(153, 204, 255));
        }
        count++;
        //System.out.println(count); 
        
    pane.setBorder(BorderFactory.createMatteBorder(outerBorderWidth,  outerBorderWidth, outerBorderWidth,outerBorderWidth,Color.BLACK));
      f.add(pane);
    f.setLayout(new GridLayout(3,3));
    f.setSize(1000,1000); 
    }
    public void build(){
    f.setVisible(true); 
    }
    
}