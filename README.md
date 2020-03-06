# sudoku_solver


Authors: Emma Neary, Peter Mehler

Filename: SudokuPlayer.java

Known Bugs: N/A

Information about customized solver:

Our customized solver implements the most constrained variable approach to choosing the next cell to search. It does this by using the AC3's effect on the globalDomains to find which unassigned cells are most constrained.

This solver performs fewer recursive operations than the AC3 solver, but it takes more time to perform them. This worse time performance is likely due to the extra overhead time required to select the most constrained variable, and to check to see if the board is yet filled. This check to see if the board is filled could be done differently (such as with a counter of filled tiles) to reduce the time required.
