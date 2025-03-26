## ALGO BOWL S25- "NullPntrException" 
### Ty Gazaway, Thomas Dowd, John Silva

### Tents and Trees: Project Description

The puzzle game Tents and Trees is played on a two-dimensional grid. Each cell may contain a tent, a tree, or it may
be blank. Initially, the board contains only trees and blank cells, and the tents are placed on blank cells such that:
  1. No two tents are adjacent, even diagonally.
  2. Each row and column has the correct number of tents, as indicated by numbers marked outside the row or
column.
  3. Each tent has its own tree, either horizontally or verically adjacent.

• Note 1: Trees get lonely without a corresponding tent. Each tree must also have its own tent which is
horizontally or verically adjacent.

• Note 2: A tent may be adjacent to a tree assigned to another tent, so as long as it has another tree
adjacent that’s its own.

A web version of this game can be found here:
https://www.chiark.greenend.org.uk/~sgtatham/puzzles/js/tents.html

Implement a solver for Tents and Trees. You are to be given inputs which may not be fully solvable, and are to
create the best solution you can while minimizing the number of violations, as defined below:

1. A tent which has at least one adjacent tent (including diagonally) causes 1 violation. Note that tents with
multiple adjacent tents will result in only a single violation.

2. A tent or tree which doesn’t have its corresponding tent/tree either horizontally or verically adjacent causes a
violation.

3. A row or column which has too many or too few tents causes multiple violations: one violation for each tent to
many or too few.

### Input Format

The first line of the input file contains two positive integers separated by a single space, R and C, indicating the
number of rows and columns in the puzzle.

The next line contains R non-negative integers separated by spaces r1 . . . rR. ri indicates the number of tents which
must be in row i. In other words, the first number corresponds to the number of tents in the first row, the second
number corresponds to the number of tents in the second row, and so forth.

The next line contains C non-negative integers separated by spaces c1 . . . cC . ci indicates the number of tents which
must be in column i.

The next R lines each contain a row of the puzzle. Each line should contain exactly C characters. The characters
used are:

• .: Blank cell.

• T: Tree.

_Project Description and input format taken from Spring2025_AlgoBowl assignment rubric_

### Contributing
Contact Ty Gazaway or Thomas Dowd to contribute.

### Helpful Git Commands:
<b>Publishing a Branch:</b><br>
1. ```git add .``` 
2. ``` git commit -m "Description of your changes"```
3. ```git push -u origin <new-branch-name>```

<b>Deleting a Branch:</b><br>
~ Locally:<br>
1. ```git checkout main``` 
2. ```git branch -d <branchNameToDelete>```

<b>Deleting a Remote/TrackedBranch:</b><br>
1. ```git checkout main ```
2. ```git pull ``` 

~ Published: 
1. ```git push origin --delete <branch_name>```

~ Unpublished:
1. ```git branch -dr origin/<branch_name>```

<b>Updating an outdated Branch:</b><br>
1. ```git checkout <outofdatebranch>```
2. ```git merge main```
3. ```git push origin <outofdatebranch>```

<b>Check if merged: ```git branch --merged main```</b> <br>
<b>Check state of branches: ```git log --stat --graph```</b><br>
