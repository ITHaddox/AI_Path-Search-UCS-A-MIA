# AI_Path-Search-UCS-A-MIA

This program was an assignment in my AI class that utilized UCS (Uniform Cost Search)  and A* path search algorithms. 
I was also asked to create an improved version which I comment in the code as “Improved A*”. 

The terrain has multiple speeds attributed to their color: 
  • Tan has fast travel speed (small cost) and is the road. 
  • Green has medium travel speed (medium cost) and is grass. 
  • Blue has slow travel speed (high cost) and is water.

Click anywhere on the map to try out these different algorithms. There are three user inputs possible: 
  • Left click – UCS 
  • Right click – A* 
  • Middle click (scroll btn) – My Improved A*

You’ll notice the robot starts moving and I show two things: 
  1. There is a red line that shows you what the current path has planned out. 
  2. Black circles show a border representing what nodes have been checked. Anything past those black circles has not been checked.

The basic idea is each alogrithm searchs the 8 squares around itself and updates their total cost. 
Then each algorithm decides what node to check its’ 8 squares surrounding it.

The UCS does a decent job at finding an optimal path as long as it does not need to cross over water. 
If you click just on the road the nodes searched are minimized compaired to if water needs to be crossed. 
To reach an island the nodes that are searched are high and thus the algorithm is slow yet still optimal.

The A* tries to avoid compute time and just starts going. 
It may not find the optimal path but tries not to think to hard about the next decision. 
If staying on the one big road it does pretty terrible because it is trying to search through water where as UCS tries to avoid water searches. 
When it comes to reaching islands the A* shines. 
Since it is geared to not care to much about high cost water dsicoveries then it finds a decent path quickly vs UCS that searches around everywhere before deciding if it really needs to cross any water at all.

Now the best… MIA* (My Improved A*)… is really A* and UCS mixed a little. 
It does extremly well on the road. You can see it does not search very many nodes. 
It’s pretty complicated but what it is basically doing is trying to use a hueristic that says “if every node after this node had a straight route to the destination with the max speed possible, would it be faster than the next checked node and update my total cost if faster (less expensive)”.
Sometimes this algorithm gets beat in less node searchs by standard A* when going to islands in short distance but long distance islands MIA* wins.

To be honest it has been a while since I did this project and there are many other good details in the code. 
The one thing I do regret is the naming of my variables. 
