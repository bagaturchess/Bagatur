# Bagatur Chess Engine Search Algorithm Overview

## Introduction

Bagatur is an open-source UCI chess engine written in Java that implements a sophisticated search algorithm based on Principal Variation Search (PVS) with Null Window Search (NWS). The engine supports both single-threaded and multi-threaded search architectures using MTD (Memory-enhanced Test Driver) framework for iterative deepening and incorporates numerous modern chess programming techniques including Lazy SMP parallelization.

## Core Architecture

### Search Framework
- **Main Algorithm**: PVS (Principal Variation Search) with NWS (Null Window Search)
- **Root Search**: MTD-based sequential and parallel search with iterative deepening
- **Threading**: Single-threaded sequential implementation and multi-threaded Lazy SMP implementation
- **Depth Management**: Supports depths up to MAX_DEPTH with configurable start/end depths

### Key Components

#### 1. SequentialSearch_MTD (Root Search Manager)
- Manages the overall search process using MTD framework
- Handles iterative deepening from start depth to maximum depth
- Coordinates with mediators for UCI communication
- Manages time control and search termination
- Supports both regular and pondering modes

#### 2. MTDParallelSearch_BaseImpl (Parallel Search Framework)
- Abstract base class for multi-threaded search implementations
- Manages a pool of sequential searchers running in parallel
- Implements Lazy SMP (Symmetric Multi-Processing) approach
- Coordinates information sharing between parallel searchers
- Handles dynamic searcher management and load balancing

#### 3. MTDParallelSearch_ThreadsImpl (Concrete Parallel Implementation)
- Concrete implementation of parallel search using thread pool
- Creates multiple SequentialSearch_MTD instances for parallel execution
- Implements search result aggregation and best move selection
- Manages thread-specific move ordering for diversification

#### 4. Search_PVS_NWS (Core Search Engine)
- Implements the main search algorithm
- Handles both PV (Principal Variation) and non-PV nodes
- Manages move ordering and pruning decisions
- Integrates evaluation, transposition table, and endgame tablebase lookups

## Search Algorithm Details

### Move Generation and Ordering
The engine uses a sophisticated 8-phase move ordering system:

1. **PHASE_TT**: Transposition table move (highest priority)
2. **PHASE_ATTACKING_GOOD**: Good captures (positive SEE scores)
3. **PHASE_KILLER_1-4**: Four levels of killer moves
4. **PHASE_QUIET**: Non-capture moves ordered by history heuristics
5. **PHASE_ATTACKING_BAD**: Bad captures (negative SEE scores)

### Search Techniques

#### Principal Variation Search (PVS)
- Uses full window search for the first move in PV nodes
- Employs null window searches for subsequent moves
- Re-searches with full window if null window search fails high

#### Late Move Reduction (LMR)
- Reduces search depth for moves expected to be poor
- Uses a pre-computed LMR table based on depth and move number
- Formula: `LMR_TABLE[depth][move_number] = ceil(max(1, log(move_number) * log(depth) / 2))`
- Adjustable aggressiveness factor (currently 1.25)

#### Null Move Pruning
- Skips a turn to detect positions where even doing nothing maintains advantage
- Uses adaptive reduction: `depth/4 + 3 + min((max(0, static_eval - beta)) / 80, 3)`
- Includes mate threat detection for positions where null move search returns mate scores

### Pruning Techniques

#### Futility Pruning
- Prunes quiet moves when static evaluation + margin is below alpha
- Applied up to depth 7 with 80-point base margin
- Scaled by depth and pruning aggressiveness factor

#### Static Null Move Pruning
- Returns static evaluation when it's significantly above beta
- Applied up to depth 9 with 60-point margin per depth
- Provides early cutoffs in clearly winning positions

#### Razoring
- Reduces depth when static evaluation is well below alpha
- Uses quiescence search to verify the position is truly poor
- Applied up to depth 4 with 240-point base margin

#### SEE Pruning
- Skips moves with poor Static Exchange Evaluation scores
- Applied to both quiet moves and bad captures
- Uses 65-point margin scaled by depth

#### Multi-Cut Pruning
- Prunes when multiple moves exceed beta in reduced searches
- Part of singular extension framework

#### Prob-Cut Pruning
- Performs shallow searches on tactical moves to detect early cutoffs
- Uses 200-point margin above beta

### Extensions

#### Singular Extensions
- Extends search when only the TT move achieves a good score
- Performs reduced-depth search excluding the TT move
- Can extend by 1-3 plies based on how "singular" the move is
- Also detects multi-cut situations for pruning

#### Check Extensions
- Positions in check get extended search depth
- Implemented within the move loop logic

### Specialized Search Components

#### Quiescence Search
- Searches only captures and promotions to reach quiet positions
- Uses delta pruning and SEE pruning for move selection
- Handles check evasion with limited-depth normal search

#### Singular Move Search
- Special search routine for testing move singularity
- Excludes specific moves (typically TT move) from consideration
- Uses modified hash key to avoid TT pollution

## Advanced Features

### Transposition Table Integration
- Stores exact scores, bounds, and best moves
- Uses different replacement schemes and aging
- Provides move ordering hints and score bounds
- Supports depth-based entry management

### Endgame Tablebase Support
- Syzygy tablebase integration for perfect endgame play
- WDL (Win/Draw/Loss) and DTZ (Distance to Zero) probing
- Caching system for DTZ lookups to improve performance
- Handles 50-move rule considerations

### History Heuristics
- Maintains history tables for move ordering
- Tracks both regular history and continuation history
- Updates move scores based on search results
- Integrates with killer move tracking

### Evaluation Integration
- Supports multiple evaluation functions
- Lazy evaluation with error margin tracking
- Mate distance considerations
- Draw score handling for insufficient material

## Performance Optimizations

### Node Counting and Statistics
- Tracks searched nodes, selective depth, and tablebase hits
- Maintains statistics for different search phases
- Provides performance monitoring capabilities

### Memory Management
- Pre-allocated move lists and search stack information
- Efficient data structure reuse
- Careful object lifecycle management

### Search Control
- Configurable aggressiveness factors for reductions and pruning
- Time management integration
- Search depth and node limits
- Stop condition checking

## Notable Implementation Details

### Error Handling and Validation
- Optional PV validation for debugging
- Consistency checks between search results
- Illegal move detection and handling

### UCI Compliance
- Proper integration with UCI protocol mediators
- Real-time search information reporting
- Support for pondering and infinite analysis

### Configurability
- Multiple search parameters can be tuned
- Support for different evaluation functions
- Configurable tablebase usage and caching

## Conclusion

Bagatur's search algorithm represents a mature implementation of modern chess search techniques. It combines the proven PVS framework with aggressive pruning, sophisticated move ordering, and advanced features like singular extensions and tablebase integration. The engine demonstrates both traditional single-threaded optimization and modern parallel processing capabilities.

The **sequential search** provides a highly optimized single-threaded implementation with numerous pruning techniques and optimizations. The **parallel search** architecture implements Lazy SMP, allowing the engine to scale effectively across multiple cores while maintaining the benefits of the underlying sequential algorithm.

The modular design allows for easy experimentation and tuning while maintaining competitive strength across both single-core and multi-core systems. The engine shows how classical alpha-beta search can be enhanced with modern techniques to achieve strong playing performance while remaining relatively simple to understand and modify.

## Parallel Search Architecture

### Lazy SMP Implementation

Bagatur implements a **Lazy SMP** (Symmetric Multi-Processing) approach to parallel search, which is one of the most successful parallelization techniques in modern chess engines.

#### Core Concepts
- **Shared Transposition Table**: All searcher threads share the same transposition table, enabling information sharing
- **Independent Search Trees**: Each thread searches the same position but with slight variations
- **Move Ordering Diversification**: Different threads use different starting move indices to explore different move orders
- **Result Aggregation**: The parallel framework collects and merges results from all searcher threads

#### Parallel Components

##### MTDParallelSearch_BaseImpl (Parallel Framework)
- **Thread Pool Management**: Uses ThreadPoolExecutor for dynamic thread management
- **Searcher Lifecycle**: Manages creation, starting, stopping, and restarting of searcher threads
- **Information Collection**: Aggregates search information from multiple threads using BucketMediator
- **Synchronization**: Handles thread-safe access to shared resources and board states
- **Dynamic Load Balancing**: Monitors CPU load and adjusts active searcher count

##### SearchersInfo (Information Aggregation)
- **Multi-Thread Coordination**: Tracks search progress across all threads
- **Depth Consensus**: Determines when enough threads have reached a new depth
- **Best Move Selection**: Aggregates moves from multiple threads using voting mechanisms
- **Statistics Accumulation**: Combines node counts, tablebase hits, and other metrics

##### BucketMediator (Thread Communication)
- **Information Buffering**: Collects search information from individual threads
- **Asynchronous Communication**: Enables non-blocking information exchange
- **Result Filtering**: Separates major (depth completion) from minor (progress) updates

### Parallel Search Features

#### Thread Diversification Strategies
1. **Move Index Offset**: Each thread starts with a different first move index
2. **Independent Random Seeds**: Different threads may use different random components
3. **Varied Reduction Aggressiveness**: Slight variations in pruning parameters between threads

#### Information Sharing Mechanisms
1. **Transposition Table Sharing**: All threads benefit from each other's search results
2. **Best Move Propagation**: Good moves found by one thread influence others
3. **Depth Synchronization**: Threads coordinate to ensure comprehensive depth coverage

#### Consensus and Voting System
The parallel search uses a sophisticated consensus mechanism:
- **Depth Threshold**: Configurable percentage of threads must reach new depth before advancing
- **Move Voting**: Multiple threads voting for the same move increases confidence
- **Evaluation Averaging**: Multiple evaluations of the same position are averaged for stability

#### Dynamic Thread Management
- **Adaptive Starting**: Threads are started progressively based on time elapsed
- **Restart on New Depth**: Threads can be restarted when new depths are reached
- **Load Monitoring**: System can monitor CPU usage and adjust thread count accordingly

### Performance Characteristics

#### Scaling Efficiency
- **Near-Linear Scaling**: Up to 4-8 cores typically show good speedup
- **Diminishing Returns**: Additional cores provide smaller incremental benefits
- **Memory Bandwidth**: Performance limited by memory access patterns at high core counts

#### Search Quality
- **Improved Move Ordering**: Multiple threads exploring different orders find better moves faster
- **Error Compensation**: Mistakes by individual threads are compensated by consensus
- **Deeper Search**: Parallel execution allows reaching greater depths in the same time

### Implementation Details

#### Thread Safety
- **Synchronized Board Access**: Board modifications are protected by synchronization
- **Lock-Free Transposition Table**: Uses atomic operations for high-performance TT access
- **Concurrent Collections**: Uses thread-safe data structures for information sharing

#### Memory Management
- **Shared Read-Only Data**: Evaluation tables and other static data shared between threads
- **Thread-Local Caches**: Each thread maintains its own search stack and temporary data
- **Efficient Object Reuse**: Minimizes garbage collection through object pooling

#### UCI Integration
- **Unified Output**: All threads contribute to a single UCI information stream
- **Time Management**: Parallel search respects time controls and stop conditions
- **Best Move Selection**: Final best move represents consensus of all searcher threads