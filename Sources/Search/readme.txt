

/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */


For the latest and greatest version of this readme file you can visit the SVN repository and check the Search sub-project:
SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess


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


Have a nice usage ... and feel free to contribute at http://sourceforge.net/projects/bagaturchess/develop

