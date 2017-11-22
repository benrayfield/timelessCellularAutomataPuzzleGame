# timelessCellularAutomataPuzzleGame
2d cellular automata that guarantees convergence even with random rules, if the boltzmann neuralnet weights are big enough, and some sets of weights (which are each on all pairs of pixels at the same distance, or can also vary by angles is more interesting patterns) are turing-complete similar to rule110 in 2d. Purpose is interesting puzzle games.

V0.2 [
Run this file to try it https://github.com/benrayfield/timelessCellularAutomataPuzzleGame/releases/download/0.2.0/timelessCellularAutomata_0.2.0_doubleClickToRun_or_unzipToGetSourceCode.jar or unzip it to get source code and take it in your own direction of AI research. If it doesnt work install java 8+ from https://java.com and try again.

Left mouse button makes it brighter, right button darker. Painting onto a pixel does not paint that pixel directly but trains the neuralnet to do that and the neuralnet (used convolutionally) chooses all pixel colors. Patterns you paint appear many places, so dont make the whole place too bright or dark or it will get stuck. It gets stuck anyways after you paint for maybe 30 seconds cuz the AI algorithm needs tuning. But its fun and a new experience. The neuralnet knows where you painted only indirectly by the echos of the random pixels that it started with at screen edge.
]

Next steps of coding are an editor for the small square weight array (same weights reused for all pixels to near pixels) or some way of statistically deriving weights of interesting patterns, then make it into some kind of puzzle game where you want certain pixels to be on and others to be off, and there would be bigger and fewer pixels in the puzzle game. The game might also include adjusting the automata rules aka boltzmann neuralnet weights.

v1 pics:
<img src="https://raw.githubusercontent.com/benrayfield/timelessCellularAutomataPuzzleGame/master/pics/timelessCellularAutomata_0.1_doubleClickToRun_or_unzipToGetSourceCode.jar.pic1.png"/>
<img src="https://raw.githubusercontent.com/benrayfield/timelessCellularAutomataPuzzleGame/master/pics/timelessCellularAutomata_0.1_doubleClickToRun_or_unzipToGetSourceCode.jar.pic2.png"/>
<img src="https://raw.githubusercontent.com/benrayfield/timelessCellularAutomataPuzzleGame/master/pics/timelessCellularAutomata_0.1_doubleClickToRun_or_unzipToGetSourceCode.jar.pic3.png"/>
<img src="https://raw.githubusercontent.com/benrayfield/timelessCellularAutomataPuzzleGame/master/pics/timelessCellularAutomata_0.1_doubleClickToRun_or_unzipToGetSourceCode.jar.pic4.png"/>

Cells affect eachother in the next time step gradually more the closer they are. Since their influence is so low far away, this is only computed up to 9 squares away in x and y, like conwaysGameOfLife computes 1 away. The amount of influence, in this demo (version 0.1), is  1/(83.35+distance^2.5).

Theres a number called "weight" between each pair of near cells. For the same offset of x and y, like (+2,-1), all cells that 2 and 1 apart in that direction (or its mirror (-2,+1)) have that same weight between eachother. So there are (9+1+9)^2 - 1 unique weights (-1 excluding self) reused as centered on each cell.

Theres also a number called "bias", which is the same for every cell. Total weights are designed to be twice as negative as bias is positive, since cell brightness is supposed to average about .5 (0 is black, 1 is white).

Bias pushes each cell toward bright. Weight pushes each near pair of cells to NOT be both bright. If one is bright, the other is pushed to be dark. Or both can be dark. This is the excluded pairs behavior of npcomplete math --- which BTW a million dollar millenium prize is still out for a certain kind of proof about how efficiently, for example in the context of this cellular automata, how to find a board setup of the lowest possible energy (see boltzmann energy equation below). This math appears almost everywhere in different contexts. I dont mean to solve that, only to converge to an approximation as neuralnets normally do.

Each of those weights is a random negative number multiplied by influence=1/(83.35+distance^2.5) then scaled so the total weights equals -2*bias.

Weights are negative. Bias is positive. Each cell's brightness ranges 0 to 1 and averages about .5. Since a cell averages about .5, the weights are designed to total twice as negative as bias is positive.

The cell update rule is:

    brightness_x = 1/(1+e^sumForAllY(weight_x_y*brightness_y))

Boltzmann was a physics guy. The boltzmann neuralnet energy equation says that this cell update rule, regardless of which cells are adjacent to which others (such as wormholes or a 2d grid), always converges toward (but may get stuck in valleys and not find the lowest) lower energy, which is why its timeless aka converges to a constant picture from any starting conditions. The cell update rule is statistically symmetric. This is also why we see https://en.wikipedia.org/wiki/Crystallographic_defect

    energy = -sumForAllPairs_brightnessX_and_brightnessY(brightness_x*brightness_y*weight_x_y) - sumForAllCell_z(bias_z*brightness_z)

In this software, bias is the same for all cells, foir the same reason the (+2,-1) weight is duplicated between all cells separated by it.

Each time you run that jar file (version 0.1) it once chooses random weights within those limits, then doesnt change the weights. The edges of the window have 9 cells thick of random square brightnesses that dont change. These are the awkwardly shaped environment that makes the world between not fit all as 1 pattern. Its like crushing a piece of paper then trying to flatten it. Each time step (which in version 0.1 is at each mouse movement), a random 10% of the cells are updated, since updating them all at once interferes with the "Weight pushes each near pair of cells to NOT be both bright. If one is bright, the other is pushed to be dark." not knowing which cell to make bright and the other dark, in each pair (or both could be dark or both bright, if the other weights and bias overpower this weight).

This is a very small software. You could print it out on a few pages of paper, especially the cellular automata logic which is in https://github.com/benrayfield/timelessCellularAutomataPuzzleGame/blob/master/src/timelessCellularAutomata/cboltz/BoltzCellAutomata.java Even if you're not trained in programming, you could try putting the .java files in Eclipse and changing the program logic, after a short tutorial video on java and eclipse. There are other tools you could use to change this java program. I'd be willing to explain a few things in this thread to anyone who wants to explore variations of the program. I could even make a javascript version in a html file if thats what it takes.
