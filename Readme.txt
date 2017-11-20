This is the code for the bachelor thesis "Finden von strukturierten abgeschlossenen Itemsets in Datenströmen" 
by Maximilian Thiessen.

src/ the java source files
data/ the transaction files and the used graph files. 
    -->roadNet-CA is from J. Leskovec, K. Lang, A. Dasgupta, M. Mahoney. Community Structure in Large Networks: Natural Cluster Sizes and the Absence of Large Well-Defined Clusters. Internet Mathematics 6(1) 29--123, 2009.
    -->mushrooms is from M. Lichman. UCI machine learning repository, 2013. URL http://archive.ics.uci.edu/ml.
EikeStadtlaender/ data stream implementations of Eike EikeStadtlaender (CFIStream.jar is also from this project)

You need Java 8 to run the jars.
To reproduce the experiments from the bachelor thesis just run (linux/windows):
java -jar <filename>.jar

For the original roadnetwork experiment 2-3 gb of ram are needed, so 
java -Xmx3072M -jar GraphExperiment.jar 
could be useful to increase available heap space.

If you want to try your own datasets/parameters, you can launch the jars with command line arguments:
java -jar GelyVsStreamGely.jar <transactionFile> <slidingWindowSteps> <slidingWindowSize> <minSupport1> <minSupport2> ...
java -jar CFIStream.jar  <slidingWindowSteps> <slidingWindowSize> 
        --> CFIStream.jar uses the mushrooms dataset.
java -Xmx3072M -jar GraphExperiment.jar <graphFile> <transactionFile> <slidingWindowSteps> <slidingWindowSize1> <slidingWindowSize2> ...

To generate your own traffic simulations of the roadnetwork graph:
java -jar <howManyTransaction> <averageLengthOfOneTransaction>
or
java -jar <intputGraphfile> <outputTransactionFile> <howManyTransaction> <howOftenStartWithSameVertex> <averageLengthOfOneTransaction> <standardDeviationOfLength>

