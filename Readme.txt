This is the code for the bachelor thesis "Finden von strukturierten abgeschlossenen Itemsets in Datenströmen" 
by Maximilian Thiessen.

src/ the java source files
data/ the transaction files and the used graph files. 

You need Java 8 to run the jars.
To reproduce the experiments from the bachelor thesis just run (linux/windows):
java -jar <filename>.jar

For the original roadnetwork experiment 2-3 gb of ram are needed, so 
java -Xmx3072M -jar GraphExperiment.jar 
could be useful to increase available heap space.

If you want to try your own datasets/parameters, you can launch the jars with command line arguments:
java -jar GelyVsStreamGely.jar <transactionFile> <slidingWindowSteps> <slidingWindowSize> <minSupport1> <minSupport2> ...
java -Xmx3072M -jar GraphExperiment.jar <graphFile> <transactionFile> <slidingWindowSteps> <slidingWindowSize1> <slidingWindowSize2> ...

To generate your own traffic simulations of the roadnetwork graph:
java -jar <howManyTransaction> <averageLengthOfOneTransaction>
or
java -jar <intputGraphfile> <outputTransactionFile> <howManyTransaction> <howOftenStartWithSameVertex> <averageLengthOfOneTransaction> <standardDeviationOfLength>

