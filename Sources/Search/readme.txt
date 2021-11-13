
This sub-project contains the search algorithm of Bagatur chess engine.

The main interface of the Bagatur's searcher API is the bagaturchess.search.api.IRootSearch class
which is used directly from the IUCISearchAdaptor implementation located in package bagaturchess.search.impl.uci_adaptor.*
For more information about the purpose and the signature of the IUCISearchAdaptor interface
have a look at the readme file inside the UCI sub-project.

The IRootSearch interface has 3 implementations, one which supports single threaded search, one which supports parallel search
and one which supports the mixture of both in order to optimize search effectiveness for shallow depths and short think times. 
1. bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch
2. bagaturchess.search.impl.rootsearch.parallel.MTDParallelSearch
2. bagaturchess.search.impl.rootsearch.mixed.MTDMixedSearch
All implementations are using the MTD algorithm based on the PV search algorithm.
It is also possible to start the sequential search with standard Alpha-Beta negascout with appropriate configuration
(for more information have a look at the Engines sub-project which contains different engines' configurations).

There is also an internal interface bagaturchess.search.api.internal.ISearch which is used inside the 
implementors of IRootSearch. The main difference between the IRootSearch and ISearch interfaces is that the first one is
an object oriented model of "Searcher" with only few arguments of its methods' declarations compared to the second one which is
the standard back-tracking implementation of alpha-beta search and has a lot of arguments inside its methods' declarations.

Besides, the 'Search' sub-project contains important ideas and realizations on which you must have a look:
1. Adaptive move ordering
2. Adaptive extensions
3. MTD based on PV Alpha-Beta search
4. Unlimited parallel search based on MTD (unlimited from CPUs' core count perspective)
5. Efficient tracking of the PV (principal variation) from memory and performance perspective
6. Other little ideas everywhere inside the code ...

Have a nice usage ... and feel free to contribute.
