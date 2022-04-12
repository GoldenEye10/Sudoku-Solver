
import java.util.*;
import java.io.*;

class Sudoku
{
  //declare varible 
 int SIZE, N;

// declare 2d array
 int Grid[][];


// solve method removes all 0 and replaces them with suitable number
// from 1-9
 public void solve()
 {
   //create object of AlgorithmXSolver 
  AlgorithmXSolver solver= new AlgorithmXSolver();

  //pass the 2d grid as parameter
  solver.run(Grid);
 }

 // getter method that returns the grid
 public int[][] getGrid(){
  return Grid;
}


 // AlgorithmXSolver class
 public class AlgorithmXSolver 
 {       
  // Starting node of the linked list
  private ColumnNode root = null;
  //Arraylist to store the solution
  private ArrayList<Object> solution = new ArrayList<Object>(); 
  // we pass 2d array to the run method   
  private void run(int[][] initialMatrix) 
  {
    //create sparse matrix of type byte
   byte[][] matrix = createMatrix(initialMatrix);
   //create  circular doubly linked toroidal list
   ColumnNode doubleLinkedList = createDoubleLinkedLists(matrix); 
   // starts the dancing link process of searching and covering and uncovering
   //recursively
   search(0); 
  }
  
  
  //Define node that knows four other nodes 
  // also the starting point(column head)
  class Node  
  {
   Node left;
   Node right;
   Node up;
   Node down;
   ColumnNode head;
  }

  //Node that contains information about that particular column
  class ColumnNode extends Node 
  {
   int size = 0;
   ColumnID info;
  }

  //Stores information about that column
  class ColumnID   
  {
   int constraint = -1;
   int number = -1;
   int position = -1;
  }

// create sparse matrix for grid
  private byte[][] createMatrix(int[][] initialMatrix)
  {      
    // stores the numbers already given on the board
   int[][] clues = null; 
   ArrayList<Object> cluesList = new ArrayList<Object>();  
   int counter = 0;
   // outer loop iterates over rows of grid
   for(int r = 0; r < N; r++) 
   {
     // inner loop iterates over column of grid
    for(int c = 0; c < N; c++) 
    {
     // if num != 0  its not needed to be solved so store it
     if(initialMatrix[r][c] > 0)
     {
      // store the number, rowNum and colNum
      cluesList.add(new int[]{initialMatrix[r][c],r,c});
      // then increase the counter 
      counter++;
     }
    }
   }
   // store the number that does not need to be solved
   clues = new int[counter][]; 

   for(int i = 0; i < counter; i++) 
   {
    clues[i] = (int[])cluesList.get(i); 
   }

   // then build a sparse matrix
   byte[][] matrix = new byte[N*N*N][4*N*N];
    // The rows of our matrix represent all the possibilities, whereas the columns represent the constraints.
   // Hence, there are N^3 rows (N rows * N columns * N numbers), and N^2 * 4 columns (N rows * N columns * 4 constraints) 
 
   //iterates over possible digits d
   // here N is 9
   for(int d = 0; d < N; d++) 
   {
    //iterates over possible rows
    for(int r = 0; r < N; r++) 
    {
     // iterates over all possible columns
     for(int c = 0; c < N; c++) 
     {
      // if the cell is not already filled 
      if(!filled(d,r,c,clues)) 
      {
        //https://code.google.com/p/narorumo/wiki/SudokuDLX
       int rowIndex = c + (N * r) + (N * N * d);  
       //there is four 1's in each row
       // since we have four constraint
       // one for each constraint
       int blockIndex = ((c / SIZE) + ((r / SIZE) * SIZE));
       int colIndexRow = 3*N*d+r;
       int colIndexCol = 3*N*d+N+c;
       int colIndexBlock = 3*N*d+2*N+blockIndex;
       int colIndexSimple = 3*N*N+(c+N*r);
        // fill in the 1
       matrix[rowIndex][colIndexRow] = 1;
       matrix[rowIndex][colIndexCol] = 1;
       matrix[rowIndex][colIndexBlock] = 1;
       matrix[rowIndex][colIndexSimple] = 1;
      }
     }
    }
   }
   return matrix;
  }

  // method to
 // check if the cell is already filled with a digit 
  private boolean filled(int digit, int row, int col, int[][] prefill) {
   boolean filled = false;
   if(prefill != null) 
   {
    for(int i = 0; i < prefill.length; i++) 
    {
     int d = prefill[i][0]-1;
     int r = prefill[i][1];
     int c = prefill[i][2];

     
     //calcualte block indexes
     int blockStartIndexCol = (c/SIZE)*SIZE;
     int blockEndIndexCol = blockStartIndexCol + SIZE;
     int blockStartIndexRow = (r/SIZE)*SIZE;
     int blockEndIndexRow = blockStartIndexRow + SIZE;
     if(d != digit && row == r && col == c) {
      filled = true;
     } else if((d == digit) && (row == r || col == c) && !(row == r && col == c)) 
     {
      filled = true;
     } else if((d == digit) && (row > blockStartIndexRow) && (row < blockEndIndexRow) && (col > blockStartIndexCol) && (col < blockEndIndexCol) && !(row == r && col == c)) 
     {
      filled = true;
     }
    }
   }
   // returns boolean value true at the end 
   return filled;
  }
  // Doubly linked list starts from here
  // first convert the sparse matrix cover problem to doubly linked list
  // then perform Dancing link technique

  //we have four constraint
  // row, column, block and cell
  // every constraint contains N^2 columns for every cell
  private ColumnNode createDoubleLinkedLists(byte[][] matrix) 
  {
    // root is starting point of linked list
   root = new ColumnNode(); 
   ColumnNode curColumn = root;
   for(int col = 0; col < matrix[0].length; col++) 
   {
    // create column id that store the info that we ll use
    // to map this id to current curColumn
    ColumnID id = new ColumnID();
    if(col < 3*N*N) 
    {
      // identifying the digit
     int digit = (col / (3*N)) + 1;
     id.number = digit;
      //identify if it is row, column or block
     int index = col-(digit-1)*3*N;
     // if index is less than N
     // id.constraint = 0
     // that means row constraint
     if(index < N) 
     {
      id.constraint = 0; 
      id.position = index;
     } 
     // if index is less than 2N
     // id.constraint = 1
     // that means column constraint
     else if(index < 2*N) 
     {
      id.constraint = 1; 
      id.position = index-N;
     } 
     //else
     // id.constraint = 2
     // that means block constraint
     else 
     {
      id.constraint = 2; 
      id.position = index-2*N;
     }            
    }
    // if non of above
     // id.constraint = 3
     // that means cell constraint
    else
    {
     id.constraint = 3; 
     id.position = col - 3*N*N;
    }
    curColumn.right = new ColumnNode();
    curColumn.right.left = curColumn;
    curColumn = (ColumnNode)curColumn.right;

    // info abt col is set to the new col
    curColumn.info = id; 
    curColumn.head = curColumn; 
   }
   // making list circular 
   //i.e the right most ColumnHead is linked to the root
   curColumn.right = root; 
   root.left = curColumn;
   // once all columnhead are set, we iterate over entire matrix
  // iterate over all the rows
   for(int row = 0; row < matrix.length; row++) 
   {
    curColumn = (ColumnNode)root.right;
    Node lastCreatedElement = null;
    Node firstElement = null;
    // iterates over all the columns
    for(int col = 0; col < matrix[row].length; col++) {
     // if sparse matrix has 1 i.e 
     // it already has a value and does not need to be changed 
     if(matrix[row][col] == 1)  
     {
      //create new data element and link it
      Node colElement = curColumn;
      // iterates until last row of that column
      while(colElement.down != null) 
      {
       colElement = colElement.down;
      }
      colElement.down = new Node();
      if(firstElement == null) {
       firstElement = colElement.down;
      }
      colElement.down.up = colElement;
      colElement.down.left = lastCreatedElement;
      colElement.down.head = curColumn;
      if(lastCreatedElement != null) 
      {
       colElement.down.left.right = colElement.down;
      }
      lastCreatedElement = colElement.down;
      curColumn.size++;
     }
     curColumn = (ColumnNode)curColumn.right;
    }
    //making it circular
    if(lastCreatedElement != null) 
    {
     lastCreatedElement.right = firstElement;
     firstElement.left = lastCreatedElement;
    }
   }
   curColumn = (ColumnNode)root.right;

   //link the last column elements with the corresponding columnHeads
   for(int i = 0; i < matrix[0].length; i++) 
   {
    Node colElement = curColumn;
    while(colElement.down != null) 
    {
     colElement = colElement.down;
    }
    colElement.down = curColumn;
    curColumn.up = colElement;
    curColumn = (ColumnNode)curColumn.right;
   }

   // return the root of the doubly linked list
   return root; 
  }

  // searching algorithm
  private void search(int k) 
  {
    // if we've run out of columns 
    //we have solved the exact cover problem
   if(root.right == root) 
   {
    // map the solved linked list to grid
    mapSolvedToGrid(); 
    return;
   }
   // then choose a column to cover
   ColumnNode c = choose(); 
   cover(c);
   Node r = c.down;
   while(r != c) 
   {
    if(k < solution.size()) 
    {
      // if we enter this loop again
     solution.remove(k); 
    }      
    //otherwise
    // add the solution 
    solution.add(k,r); 
    Node j = r.right;
    while(j != r) {
     cover(j.head);
     j = j.right;
    }
    // recursion
    search(k+1); 
    Node r2 = (Node)solution.get(k);
    Node j2 = r2.left;
    while(j2 != r2) {
     uncover(j2.head);
     j2 = j2.left;
    }
    r = r.down;
   }
   uncover(c);
  }

  //Method to amp the solved linked list to the grid to display
  private void mapSolvedToGrid() 
  {
    //create a 1d array with size of the grid
   int[] result = new int[N*N];
   //use iterator to iterate over all the element of solution arraylist
   for(Iterator<Object> it = solution.iterator(); it.hasNext();)  
   {
    // we pull all the value of solved board from the linked list
    // to an array result[] in order
    // initialize the number and cell num to be value 
    //that cannot occur
    int number = -1; 
    int cellNo = -1;
    Node element = (Node)it.next();
    Node next = element;
    do {
     if (next.head.info.constraint == 0) 
     { 
       // if we're in row constraint
      number = next.head.info.number; 
     } 
     // if we are in cell constraint
     else if (next.head.info.constraint == 3) 
     { 
      cellNo = next.head.info.position;
     }
     next = next.right;
    } while(element != next);
    // put values into result
    result[cellNo] = number; 
   }
   int resultCounter=0;

   // Here we convert 1d array to 2d array
   /// iterates through row
   for (int r=0; r<N; r++) 
   {
     // iterates thorught col
    for (int c=0; c<N; c++) 
    {
      // put the value into the 2d array 
     Grid[r][c]=result[resultCounter];
     resultCounter++;
    }
   }  
  }
  // choose method
  // we choose most efficent column i.e. with the smallest size
  private ColumnNode choose() {
   ColumnNode rightOfRoot = (ColumnNode)root.right; 
   ColumnNode smallest = rightOfRoot; 
   // until we have reached the end of that row
   while(rightOfRoot.right != root) 
   {
    rightOfRoot = (ColumnNode)rightOfRoot.right;
    // choose the smallest size column
    if(rightOfRoot.size < smallest.size) 
    {
     smallest = rightOfRoot;
    }         
   }      
   return smallest;
  }

  // Helper method for search method
  //covers the column
  private void cover(Node column) 
  {
    //remove column head by remapping the node to its left of the node ot its right
    // this the linked list no longer contains way to access column
    // head
   column.right.left = column.left;
   column.left.right = column.right;
    // we do this covering for all rows in the col
   Node curRow = column.down;
   // loop until it reaches the end element of the col
   while(curRow != column) 
   {
    Node curNode = curRow.right;
    while(curNode != curRow) 
    {
     curNode.down.up = curNode.up;
     curNode.up.down = curNode.down;
     curNode.head.size--;
     curNode = curNode.right;
    }
    curRow = curRow.down;
   }
  }

 // Helper method to uncover all the column of the linked list
 // uncovers the column
  private void uncover(Node column) 
  {
   Node curRow = column.up;
   // do this for all the nodes of the column to be uncovered first
   // and then reinset the columnHead
   while(curRow != column) 
   {
    Node curNode = curRow.left;
    while(curNode != curRow) 
    {
     curNode.head.size++;
     // reinserts node into linked list
     curNode.down.up = curNode; 
     curNode.up.down = curNode;
     curNode = curNode.left;
    }
    curRow = curRow.up;
   }
   //reinserts column head
   column.right.left = column; 
   column.left.right = column;
  }

 }

 // constructor
 public Sudoku( int size )
 {
  SIZE = size;
  N = size*size;

  Grid = new int[N][N];
  for( int i = 0; i < N; i++ ) 
   for( int j = 0; j < N; j++ ) 
    Grid[i][j] = 0;
 }


 static int readInteger( InputStream in ) throws Exception
 {
  int result = 0;
  boolean success = false;
 
  while( !success ) {
   String word = readWord( in );

   try {
    result = Integer.parseInt( word );
    success = true;
   } catch( Exception e ) {

    if( word.compareTo("x") == 0 ) {
     result = 0;
     success = true;
    }

   }
  }

  return result;
 }


 
 static String readWord( InputStream in ) throws Exception
 {
 
  StringBuffer result = new StringBuffer();
  int currentChar = in.read();
  String whiteSpace = " \t\r\n";

  while( whiteSpace.indexOf(currentChar) > -1 ) {
   currentChar = in.read();
  }


  while( whiteSpace.indexOf(currentChar) == -1 ) {
   result.append( (char) currentChar );
   currentChar = in.read();
  }
  return result.toString();
 }



 public void read( InputStream in ) throws Exception
 {

  for( int i = 0; i < N; i++ ) {
   for( int j = 0; j < N; j++ ) {
    Grid[i][j] = readInteger( in );
   }
  }
 }


/** 
 void printFixedWidth( String text, int width )
 {
  for( int i = 0; i < width - text.length(); i++ )
   System.out.print( " " );
  System.out.print( text );
 }


 public void print()
 {
  int digits = (int) Math.floor(Math.log(N) / Math.log(10)) + 1;
  System.out.println(digits);
   
  int lineLength = (digits + 1) * N + 2 * SIZE - 3;
  StringBuffer line = new StringBuffer();
  for( int lineInit = 0; lineInit < lineLength; lineInit++ )
   line.append('-');


  for( int i = 0; i < N; i++ ) {
   for( int j = 0; j < N; j++ ) {
    printFixedWidth( String.valueOf( Grid[i][j] ), digits );

    if( (j < N-1) && ((j+1) % SIZE == 0) )
     System.out.print( " |" );
    System.out.print( " " );
   }
   System.out.println();


   if( (i < N-1) && ((i+1) % SIZE == 0) )
    System.out.println( line.toString() );
  }
 }
 */
}
