Authors: Emma Neary, Peter Mehler

Filename: SudokuPlayer.java

Known Bugs: N/A

Information about customized solver:

Our customized solver implements the most constrained variable approach to choosing the next cell to search. 
It does this by using the AC3's effect on the globalDomains to find which unassigned cells are most constrained.
Thus the algorithm can choose the most constrained variable to search next, which reduces the amount of backtracking 
and recursive calls required. It also uses the domains modified by AC3 to also reduce backtracking.

This solver performs much fewer recursive operations than the AC3 solver and takes less time, especially for more difficult puzzles.
This is a result of fewer recursive calls. To further reduce the runtime, some of the initialization code could be streamlined, or further 
strategies of reducing the need for backtracking could be implemented. 
