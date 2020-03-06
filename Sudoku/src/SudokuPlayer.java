/**
* Authors: Emma Neary, Peter Mehler
* FileName: SudokuPlayer.java
* We have neither given nor received unauthorized aid on this assignment.
* All group members were present and contributing during all work on this project.
*
**/
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.DecimalFormat;

public class SudokuPlayer implements Runnable, ActionListener {

  int[][] vals = new int[9][9];
  Board board = null;

  /// --- AC-3 Constraint Satisfication --- ///

  // Useful Data-Structures;
  ArrayList<Integer>[] globalDomains = new ArrayList[81];  // Holds viable domains for each index
  ArrayList<Integer>[] neighbors = new ArrayList[81];		// Each index has an ArrayList of indexes with which it is in constraint with
  Queue<Arc> globalQueue = new LinkedList<Arc>();			// Queue of Arcs which are used to apply constraints
  int counter = 0;

  /*
  * This method sets up the data structures and the initial global constraints
  * (by calling allDiff()) and makes the initial call to backtrack().
  * You should not change this method header.
  */
  private final void AC3Init(){ //Initialize AC3
    //Do NOT remove these lines (required for the GUI)
    board.Clear();
    recursions = 0;


    fillGlobalDomains(0);	// Pre-assigned values receive one domain value, others receive 1,2,3,...9
    fillNeighbors();			// Add each index with which the current index is in constraint with
    fillGlobalQueue();		// Using neighbors, fill queue with arcs to be used to apply constraints

    // Initial call to backtrack() on cell 0 (top left)
    boolean success = backtrack(0);

    // fill vals before printing
    for(int i=0; i<globalDomains.length; i++) {
      vals[i/9][i%9] = globalDomains[i].get(0);
    }
    // Prints evaluation of run
    Finished(success);
  }


  /*
  *  This method defines constraints between a set of variables.
  *  Pass in all sets of constraining variables
  */
  private final void allDiff(int[] all){
    //fill neighbors
    for (int i: all){
      for(int j: all){
        if (j!=i){
          if (!neighbors[i].contains(j)){
            neighbors[i].add(j);
          }
        }
      }
    }
  }


  /*
  *  Fill global domains
  */
  private final void fillGlobalDomains(int custom){
    // fill globalDomains with empty ArrayLists
    for(int i =0; i<neighbors.length; i++) {
      neighbors[i] = new ArrayList<Integer>();
    }
    // create dummy ArrayList with domain 1->9
    ArrayList<Integer> listDomains = new ArrayList<Integer>();
    for(int j = 1; j<=9; j++){
      listDomains.add(j);
    }
    //populate global domains
    for (int i = 0; i < globalDomains.length; i++){
      if (vals[(int)i/9][i%9]>0){	// if already assigned, add that one value to domain
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(vals[(int)i/9][i%9]);
        globalDomains[i] = list;
        if(custom==1){ //used for evaluating end state of custom solver
          counter++; 
        }
      } else { // If not already assigned, fill domains with 1->9
        ArrayList<Integer> newList = new ArrayList<>(listDomains);
        globalDomains[i] = newList;
      }
    }
  }

  /*
  *  Fill global queue
  */
  private final void fillGlobalQueue(){
    for(int i = 0; i < neighbors.length; i++){ //81 tiles
      for(int val1= 0; val1< neighbors[i].size(); val1++){ // array at each index of neighbors
        if (i!=val1){ // if it is not itself
          Arc newArc = new Arc(i, neighbors[i].get(val1));	// Create Arc for that constraint and add it to gQueue
          globalQueue.add(newArc);
        }
      }
    }
  }


  /*
  *  Fill neighbors by adding each index with which the current index is in constraint with 
  */
  private final void fillNeighbors(){
    //call alldiff for all rows
    int[] currRow = new int[9];
    for(int row = 0; row < 9; row++){
      for(int index = 0; index<9; index++){
        currRow[index] = index + (9 * row);
      }
      allDiff(currRow);
    }
    //call alldiff for all columns
    int[] currCol = new int[9];
    for(int col = 0; col < 9; col++){
      for(int index = 0; index<9; index++){
        currCol[index] = (index * 9) + col;
      }
      allDiff(currCol);
    }
    //call alldiff for all boxes
    int[] box1 = new int[]{0,1,2,9,10,11,18,19,20};
    int[] box2 = new int[]{3,4,5,12,13,14,21,22,23};
    int[] box3 = new int[]{6,7,8,15,16,17,24,25,26};
    int[] box4 = new int[]{27,28,29,36,37,38,45,46,47};
    int[] box5 = new int[]{30,31,32,39,40,41,48,49,50};
    int[] box6 = new int[]{33,34,35,42,43,44,51,52,53};
    int[] box7 = new int[]{54,55,56,63,64,65,72,73,74};
    int[] box8 = new int[]{57,58,59,66,67,68,75,76,77};
    int[] box9 = new int[]{60,61,62,69,70,71,78,79,80};
    int[][] currBox = {box1,box2,box3,box4,box5,box6,box7,box8,box9};
    
    for(int i=0; i<currBox[0].length; i++) {
      allDiff(currBox[i]);
    }
  }




  /*
  * Recursive calls to determine whether to continue down a path of correct assignments
  * or to backtrack and re-assign
  */
  private final boolean backtrack(int cell) {

    recursions +=1;

    // Check in vals[][] already has an assignment for this cell
    if(cell!=81 && vals[cell/9][cell%9]!=0) {
      return backtrack(cell+1);
    }

    ArrayList<Integer>[] globalDomainsCopy = new ArrayList[81];
    for(int i=0; i<globalDomains.length; i++) {
      globalDomainsCopy[i] = new ArrayList<>(globalDomains[i]);
    }

    if(!AC3(globalDomainsCopy)) {
      return false;
    }

    // Check if board has been filled
    if(cell == 81) {
      return true;
    }

    ArrayList<Integer> temp = new ArrayList<>(globalDomains[cell]);
    while(!temp.isEmpty()) {
      globalDomains[cell].clear();
      globalDomains[cell].add(temp.get(0));
      if(backtrack(cell+1)) {
        return true;
      }
      else {
        //the next backtrack assignment failed
        //remove value from possible domains of next cell
        temp.remove(0);
      }
    }
    globalDomains[cell].clear();
    for(int j = 1; j<=9; j++){
      globalDomains[cell].add(j);
    }

    return false;
  }


  /*
  * Arc Constraint Method maintains the Queue which determine which arcs need to be updated by Revise()
  */
  private final boolean AC3(ArrayList<Integer>[] Domains) {
    Queue<Arc> arcQ = new LinkedList<>(globalQueue);
    Arc curr;
    while(true){
      if(arcQ.isEmpty()){
        return true;
      }
      curr = arcQ.remove();
      Boolean changed = Revise(curr, Domains);
      if(Domains[curr.Xi].isEmpty()){
        return false;
      }
      if (changed){
        ArrayList<Integer> xiNeighbors = neighbors[curr.Xi];
        for(int i = 0; i < xiNeighbors.size(); i++){
          Arc newArc = new Arc(xiNeighbors.get(i), curr.Xi);
          if(!arcQ.contains(newArc)){  // contains() may always evaluate to true without equals() override for Arc class
            arcQ.add(newArc);
          }
        }
      }
    }
  }


  /*
  * Revise() is called by AC3 and modifies the domains of given variables in an arc to be made consistent
  */
  private final boolean Revise(Arc t, ArrayList<Integer>[] Domains){
    // Determines if the domain of a variable can be reduced
    boolean revised = false;
    ArrayList<Integer> X = Domains[t.Xi];
    ArrayList<Integer> Y = Domains[t.Xj];
    if(Y.size()==1) {
      if(X.contains(Y.get(0))){
        X.remove(X.indexOf(Y.get(0)));
        revised = true;
      }
    }
    return revised;
  }


  private int mostConstrained(ArrayList<Integer>[] globalDomainsCopy) {
    int minCell = -1;
    int minCellSize = 10;
    for(int i = 0; i < globalDomainsCopy.length; i++) {
      if(globalDomains[i].size()!=1) {//if not assigned
        if(minCell==-1) {
          minCell=i;
          minCellSize = globalDomains[i].size();
        }
        else if(globalDomainsCopy[i].size()<=minCellSize) {
          minCell = i;
          minCellSize = globalDomainsCopy[i].size();
          if (minCellSize==1){
            //return early if reach smallest possible size
            return minCell;
          }
        }
      }
    }
    return minCell;
  }


  private final boolean custom_backtrack(int cell, ArrayList<Integer>[] Domains) {
    //Do NOT remove
    recursions +=1;

    ArrayList<Integer>[] globalDomainsCopy = new ArrayList[81];
    for(int i=0; i<globalDomains.length; i++) {
      globalDomainsCopy[i] = new ArrayList<>(globalDomains[i]);
    }

    if(!AC3(globalDomainsCopy)) {
      return false;
    }
    if(cell!=0){
      counter++;
    }

    // Check if board has been filled
    if(cell==-1 || counter==82){
      return true;
    }

    ArrayList<Integer> temp = new ArrayList<>(globalDomainsCopy[cell]);
    globalDomains[cell].clear();//needed to avoid choosing self as nextCell
    globalDomains[cell].add(1);//needed to avoid choosing self as nextCell
    int nextCell = mostConstrained(globalDomainsCopy);

    while(!temp.isEmpty()) {
      globalDomains[cell].clear();
      globalDomains[cell].add(temp.get(0));

      if(custom_backtrack(nextCell, globalDomains)) {
        return true;
      }
      else {
        //the next backtrack assignment failed
        //remove value from possible domains of next cell
        temp.remove(0);
      }
    }

    globalDomains[cell].clear();
    for(int j = 1; j<=9; j++){
      globalDomains[cell].add(j);
    }
    counter--;
    return false;
  }

  /*
  * This is where you will write your custom solver.
  * You should not change this method header.
  */
  private final void customSolver(){
    // count the number of neighbors that contain only one domain choice
    //set 'success' to true if a successful board
    //is found and false otherwise.
    board.Clear();
    System.out.println("Running custom algorithm");


    fillGlobalDomains(1);
    fillNeighbors();
    fillGlobalQueue();

    // Initial call to backtrack() on cell 0 (top left)
    boolean success = custom_backtrack(0,globalDomains);

    for(int i=0; i<globalDomains.length; i++) {
      vals[i/9][i%9] = globalDomains[i].get(0);
    }

    Finished(success);

  }

  /// ---------- HELPER FUNCTIONS --------- ///
  /// ----   DO NOT EDIT REST OF FILE   --- ///
  /// ---------- HELPER FUNCTIONS --------- ///
  /// ----   DO NOT EDIT REST OF FILE   --- ///
  public final boolean valid(int x, int y, int val){

    if (vals[x][y] == val)
    return true;
    if (rowContains(x,val))
    return false;
    if (colContains(y,val))
    return false;
    if (blockContains(x,y,val))
    return false;
    return true;
  }

  public final boolean blockContains(int x, int y, int val){
    int block_x = x / 3;
    int block_y = y / 3;
    for(int r = (block_x)*3; r < (block_x+1)*3; r++){
      for(int c = (block_y)*3; c < (block_y+1)*3; c++){
        if (vals[r][c] == val)
        return true;
      }
    }
    return false;
  }

  public final boolean colContains(int c, int val){
    for (int r = 0; r < 9; r++){
      if (vals[r][c] == val)
      return true;
    }
    return false;
  }

  public final boolean rowContains(int r, int val) {
    for (int c = 0; c < 9; c++)
    {
      if(vals[r][c] == val)
      return true;
    }
    return false;
  }

  private void CheckSolution() {
    // If played by hand, need to grab vals
    board.updateVals(vals);

    /*for(int i=0; i<9; i++){
    for(int j=0; j<9; j++)
    System.out.print(vals[i][j]+" ");
    System.out.println();
  }*/

  for (int v = 1; v <= 9; v++){
    // Every row is valid
    for (int r = 0; r < 9; r++)
    {
      if (!rowContains(r,v))
      {
        board.showMessage("Value "+v+" missing from row: " + (r+1));// + " val: " + v);
        return;
      }
    }
    // Every column is valid
    for (int c = 0; c < 9; c++)
    {
      if (!colContains(c,v))
      {
        board.showMessage("Value "+v+" missing from column: " + (c+1));// + " val: " + v);
        return;
      }
    }
    // Every block is valid
    for (int r = 0; r < 3; r++){
      for (int c = 0; c < 3; c++){
        if(!blockContains(r, c, v))
        {
          return;
        }
      }
    }
  }
  board.showMessage("Success!");
}



/// ---- GUI + APP Code --- ////
/// ----   DO NOT EDIT  --- ////
enum algorithm {
  AC3, Custom
}
class Arc implements Comparable<Object>{
  int Xi, Xj;
  public Arc(int cell_i, int cell_j){
    if (cell_i == cell_j){
      try {
        throw new Exception(cell_i+ "=" + cell_j);
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
    Xi = cell_i;      Xj = cell_j;
  }

  public int compareTo(Object o){
    return this.toString().compareTo(o.toString());
  }

  public String toString(){
    return "(" + Xi + "," + Xj + ")";
  }
}

enum difficulty {
  easy, medium, hard, random
}

public void actionPerformed(ActionEvent e){
  String label = ((JButton)e.getSource()).getText();
  if (label.equals("AC-3"))
  AC3Init();
  else if (label.equals("Clear"))
  board.Clear();
  else if (label.equals("Check"))
  CheckSolution();
  //added
  else if(label.equals("Custom"))
  customSolver();
}

public void run() {
  board = new Board(gui,this);

  long start=0, end=0;

  while(!initialize());
  if (gui)
  board.initVals(vals);
  else {
    board.writeVals();
    System.out.println("Algorithm: " + alg);
    switch(alg) {
      default:
      case AC3:
      start = System.currentTimeMillis();
      AC3Init();
      end = System.currentTimeMillis();
      break;
      case Custom: //added
      start = System.currentTimeMillis();
      customSolver();
      end = System.currentTimeMillis();
      break;
    }

    CheckSolution();

    if(!gui)
    System.out.println("time to run: "+(end-start));
  }
}

public final boolean initialize(){
  switch(level) {
    case easy:
    vals[0] = new int[] {0,0,0,1,3,0,0,0,0};
    vals[1] = new int[] {7,0,0,0,4,2,0,8,3};
    vals[2] = new int[] {8,0,0,0,0,0,0,4,0};
    vals[3] = new int[] {0,6,0,0,8,4,0,3,9};
    vals[4] = new int[] {0,0,0,0,0,0,0,0,0};
    vals[5] = new int[] {9,8,0,3,6,0,0,5,0};
    vals[6] = new int[] {0,1,0,0,0,0,0,0,4};
    vals[7] = new int[] {3,4,0,5,2,0,0,0,8};
    vals[8] = new int[] {0,0,0,0,7,3,0,0,0};
    break;
    case medium:
    vals[0] = new int[] {0,4,0,0,9,8,0,0,5};
    vals[1] = new int[] {0,0,0,4,0,0,6,0,8};
    vals[2] = new int[] {0,5,0,0,0,0,0,0,0};
    vals[3] = new int[] {7,0,1,0,0,9,0,2,0};
    vals[4] = new int[] {0,0,0,0,8,0,0,0,0};
    vals[5] = new int[] {0,9,0,6,0,0,3,0,1};
    vals[6] = new int[] {0,0,0,0,0,0,0,7,0};
    vals[7] = new int[] {6,0,2,0,0,7,0,0,0};
    vals[8] = new int[] {3,0,0,8,4,0,0,6,0};
    break;
    case hard:
    vals[0] = new int[] {1,2,0,4,0,0,3,0,0};
    vals[1] = new int[] {3,0,0,0,1,0,0,5,0};
    vals[2] = new int[] {0,0,6,0,0,0,1,0,0};
    vals[3] = new int[] {7,0,0,0,9,0,0,0,0};
    vals[4] = new int[] {0,4,0,6,0,3,0,0,0};
    vals[5] = new int[] {0,0,3,0,0,2,0,0,0};
    vals[6] = new int[] {5,0,0,0,8,0,7,0,0};
    vals[7] = new int[] {0,0,7,0,0,0,0,0,5};
    vals[8] = new int[] {0,0,0,0,0,0,0,9,8};
    break;
    case random:
    default:
    ArrayList<Integer> preset = new ArrayList<Integer>();
    while (preset.size() < numCells)
    {
      int r = rand.nextInt(81);
      if (!preset.contains(r))
      {
        preset.add(r);
        int x = r / 9;
        int y = r % 9;
        if (!assignRandomValue(x, y))
        return false;
      }
    }
    break;
  }
  return true;
}

public final boolean assignRandomValue(int x, int y){
  ArrayList<Integer> pval = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));

  while(!pval.isEmpty()){
    int ind = rand.nextInt(pval.size());
    int i = pval.get(ind);
    if (valid(x,y,i)) {
      vals[x][y] = i;
      return true;
    } else
    pval.remove(ind);
  }
  System.err.println("No valid moves exist.  Recreating board.");
  for (int r = 0; r < 9; r++){
    for(int c=0;c<9;c++){
      vals[r][c] = 0;
    }    }
    return false;
  }

  private void Finished(boolean success){

    if(success) {
      board.writeVals();
      //board.showMessage("Solved in " + myformat.format(ops) + " ops \t(" + myformat.format(recursions) + " recursive ops)");
      board.showMessage("Solved in " + myformat.format(recursions) + " recursive ops");

    } else {
      //board.showMessage("No valid configuration found in " + myformat.format(ops) + " ops \t(" + myformat.format(recursions) + " recursive ops)");
      board.showMessage("No valid configuration found");
    }
    recursions = 0;

  }

  public static void main(String[] args) {

    Scanner scan = new Scanner(System.in);

    System.out.println("Gui? y or n ");
    char g=scan.nextLine().charAt(0);

    if (g=='n')
    gui = false;
    else
    gui = true;

    if(gui) {
      System.out.println("difficulty? \teasy (e), medium (m), hard (h), random (r)");

      char c = '*';

      while (c != 'e' && c != 'm' && c != 'n' && c != 'h' && c != 'r') {
        c = scan.nextLine().charAt(0);
        if(c=='e')
        level = difficulty.valueOf("easy");
        else if(c=='m')
        level = difficulty.valueOf("medium");
        else if(c=='h')
        level = difficulty.valueOf("hard");
        else if(c=='r')
        level = difficulty.valueOf("random");
        else{
          System.out.println("difficulty? \teasy (e), medium (m), hard (h), random(r)");
        }
      }

      SudokuPlayer app = new SudokuPlayer();
      app.run();

    }
    else { //no gui

      boolean again = true;

      int numiters = 0;
      long starttime, endtime, totaltime=0;

      while(again) {

        numiters++;
        System.out.println("difficulty? \teasy (e), medium (m), hard (h), random (r)");

        char c = '*';

        while (c != 'e' && c != 'm' && c != 'n' && c != 'h' && c != 'r') {
          c = scan.nextLine().charAt(0);
          if(c=='e')
          level = difficulty.valueOf("easy");
          else if(c=='m')
          level = difficulty.valueOf("medium");
          else if(c=='h')
          level = difficulty.valueOf("hard");
          else if(c=='r')
          level = difficulty.valueOf("random");
          else{
            System.out.println("difficulty? \teasy (e), medium (m), hard (h), random(r)");
          }

        }

        System.out.println("Algorithm? AC3 (1) or Custom (2)");
        if(scan.nextInt()==1)
        alg = algorithm.valueOf("AC3");
        else
        alg = algorithm.valueOf("Custom");


        SudokuPlayer app = new SudokuPlayer();

        starttime = System.currentTimeMillis();

        app.run();

        endtime = System.currentTimeMillis();

        totaltime += (endtime-starttime);


        System.out.println("quit(0), run again(1)");
        if (scan.nextInt()==1)
        again=true;
        else
        again=false;

        scan.nextLine();

      }

      System.out.println("average time over "+numiters+" iterations: "+(totaltime/numiters));
    }



    scan.close();
  }



  class Board {
    GUI G = null;
    boolean gui = true;

    public Board(boolean X, SudokuPlayer s) {
      gui = X;
      if (gui)
      G = new GUI(s);
    }

    public void initVals(int[][] vals){
      G.initVals(vals);
    }

    public void writeVals(){
      if (gui)
      G.writeVals();
      else {
        for (int r = 0; r < 9; r++) {
          if (r % 3 == 0)
          System.out.println(" ----------------------------");
          for (int c = 0; c < 9; c++) {
            if (c % 3 == 0)
            System.out.print (" | ");
            if (vals[r][c] != 0) {
              System.out.print(vals[r][c] + " ");
            } else {
              System.out.print("_ ");
            }
          }
          System.out.println(" | ");
        }
        System.out.println(" ----------------------------");
      }
    }

    public void Clear(){
      if(gui)
      G.clear();
    }

    public void showMessage(String msg) {
      if (gui)
      G.showMessage(msg);
      System.out.println(msg);
    }

    public void updateVals(int[][] vals){
      if (gui)
      G.updateVals(vals);
    }

  }

  class GUI {
    // ---- Graphics ---- //
    int size = 40;
    JFrame mainFrame = null;
    JTextField[][] cells;
    JPanel[][] blocks;

    public void initVals(int[][] vals){
      // Mark in gray as fixed
      for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
          if (vals[r][c] != 0) {
            cells[r][c].setText(vals[r][c] + "");
            cells[r][c].setEditable(false);
            cells[r][c].setBackground(Color.lightGray);
          }
        }
      }
    }

    public void showMessage(String msg){
      JOptionPane.showMessageDialog(null,
      msg,"Message",JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateVals(int[][] vals) {

      // System.out.println("calling update");
      for (int r = 0; r < 9; r++) {
        for (int c=0; c < 9; c++) {
          try {
            vals[r][c] = Integer.parseInt(cells[r][c].getText());
          } catch (java.lang.NumberFormatException e) {
            System.out.println("Invalid Board: row col: "+(r+1)+" "+(c+1));
            showMessage("Invalid Board: row col: "+(r+1)+" "+(c+1));
            return;
          }
        }
      }
    }

    public void clear() {
      for (int r = 0; r < 9; r++){
        for (int c = 0; c < 9; c++){
          if (cells[r][c].isEditable())
          {
            cells[r][c].setText("");
            vals[r][c] = 0;
          } else {
            cells[r][c].setText("" + vals[r][c]);
          }
        }
      }
    }

    public void writeVals(){
      for (int r=0;r<9;r++){
        for(int c=0; c<9; c++){
          cells[r][c].setText(vals[r][c] + "");
        }   }
      }

      public GUI(SudokuPlayer s){

        mainFrame = new javax.swing.JFrame();
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel gamePanel = new javax.swing.JPanel();
        gamePanel.setBackground(Color.black);
        mainFrame.add(gamePanel, BorderLayout.NORTH);
        gamePanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        gamePanel.setLayout(new GridLayout(3,3,3,3));

        blocks = new JPanel[3][3];
        for (int i = 0; i < 3; i++){
          for(int j =2 ;j>=0 ;j--){
            blocks[i][j] = new JPanel();
            blocks[i][j].setLayout(new GridLayout(3,3));
            gamePanel.add(blocks[i][j]);
          }
        }

        cells = new JTextField[9][9];
        for (int cell = 0; cell < 81; cell++){
          int i = cell / 9;
          int j = cell % 9;
          cells[i][j] = new JTextField();
          cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
          cells[i][j].setHorizontalAlignment(JTextField.CENTER);
          cells[i][j].setSize(new java.awt.Dimension(size, size));
          cells[i][j].setPreferredSize(new java.awt.Dimension(size, size));
          cells[i][j].setMinimumSize(new java.awt.Dimension(size, size));
          blocks[i/3][j/3].add(cells[i][j]);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout());
        mainFrame.add(buttonPanel, BorderLayout.SOUTH);
        //JButton DFS_Button = new JButton("DFS");
        //DFS_Button.addActionListener(s);
        JButton AC3_Button = new JButton("AC-3");
        AC3_Button.addActionListener(s);
        JButton Clear_Button = new JButton("Clear");
        Clear_Button.addActionListener(s);
        JButton Check_Button = new JButton("Check");
        Check_Button.addActionListener(s);
        //buttonPanel.add(DFS_Button);
        JButton Custom_Button = new JButton("Custom");
        Custom_Button.addActionListener(s);
        //added
        buttonPanel.add(AC3_Button);
        buttonPanel.add(Custom_Button);
        buttonPanel.add(Clear_Button);
        buttonPanel.add(Check_Button);






        mainFrame.pack();
        mainFrame.setVisible(true);

      }
    }

    Random rand = new Random();

    // ----- Helper ---- //
    static algorithm alg = algorithm.AC3;
    static difficulty level = difficulty.easy;
    static boolean gui = true;
    static int numCells = 15;
    static DecimalFormat myformat = new DecimalFormat("###,###");

    //For printing
    static int recursions;
  }
