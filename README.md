## TEMPORARY README
## ALGO BOWL S25- "Teamname" 
### Ty Gazaway, Thomas Dowd, TEMPLATE TEAMATE

<br>
https://www.chiark.greenend.org.uk/~sgtatham/puzzles/js/tents.html
<br>

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
