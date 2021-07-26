# Internet Programming Project
Repository to work on the final project in Internet Programming course, HIT, third year, 2nd semester

# Note
You'll need Gradle and Lombok in order to open, and successfully run, the modules below  
The 3 of the modules can be opened and configured quickly if you open the project itself, which contains the 3 modules. For this, open [build.gradle](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/build.gradle) As Project.

# Server
[Source](https://github.com/haimadrian/InternetProgramming/tree/master/Project/Code/FinalProject/Server)  
Server is implemented with java, listening on port 8005 with [TCP server](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/common/TCPServer.java) (ServerSocket).  
The TCPServer class is a common TCP server implementation, supports [custom handler](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/common/RequestHandler.java) to re-use this implementation in other projects.  
TCPServer forwards the accepted operational sockets to [ClientHandler](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/common/ClientHandler.java) which reads requests from socket's input stream (using the custom handler), and forwards the requests to the custom handler mentioned above.  
Our custom handler is: [MatrixClientHandler](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/impl/MatrixClientHandler.java) which supports clear JSON body or clear HTTP GET requests. We support basic HTTP to make algorithms reachable through a Web Browser.  When the request content is a clear jJSON, we handle it by unmarshalling the request into [Request](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Common/src/main/java/org/hit/internetprogramming/eoh/common/comms/Request.java) model, and handling it using a **Command Pattern** implemented at [ActionExecutor](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/action/ActionExecutor.java). Each action knows how to handle its request, based on command type, and return a generic [Response](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Common/src/main/java/org/hit/internetprogramming/eoh/common/comms/Response.java).  
In case of HTTP response, we wrap the response content with our [index.html](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/resources/index.html) and send it back to the HTTP caller.  
In case of regular request (Java client) we marshall the Response model into JSON, and write it back to the operational socket's output stream.  
Scroll down to see class diagrams.  
The server will create a tray icon when it runs in a Desktop environment, which lets us to ordinary shutdown the server. Scroll down to see screenshots.

## Algorithms
The actions mentioned above (from command pattern) use their corresponding algorithms in order to perform user request, concurrently, and return the response.
- [DFSVisit](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/graph/algorithm/DFSVisit.java): Used for finding connected component in a graph, starting from its root. The class uses ThreadLocal so we can find all connected components using multiple threads.
- [BFSVisit](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/graph/algorithm/BFSVisit.java): Used for finding shortest paths between vertex s to vertex t. The class uses ThreadLocal, though we use a single thread to access it.
- [DijkstraWithNegCycleSupport](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/graph/algorithm/DijkstraWithNegCycleSupport.java): This is an improved version (though not that efficient) of dijkstra algorithm, used to find shortest paths in a weighted graph, including negative weights. When there is a negative cycle, we detect it and print a warning to log. Though this will not break the traverse operation, we'll continue looking for a shortest simple path. In this class we use ForkJoinPool with RecursiveAction, to make the traverse searching in parallel.
- [ConnectedComponents](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/graph/algorithm/ConnectedComponents.java): Used to find all connected components in a graph, concurrently. This algorithm uses DFSVisit.
- [Submarines](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/graph/algorithm/Submarines.java): Used to count how many submarines there are in a graph, concurrently. This algorithm uses the ConnectedComponents algorithm, in order to collect them and then check each CC and see if it is a submarine or not.
- [BellmanFord](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/graph/algorithm/BellmanFord.java): Used to find shortest paths in weighted graph. This algorithm was replaced by Dijkstra since we couldn't make it run in parallel.. It runs using a single thread.

## Thread Pools
There are four thread pools in the server.
- Single thread executor, used by TCPServer to launch the server and let the main thread continue its execution.
- Max-10-threads thread pool, used by TCPServer, to serve client requests concurrently
- 4*availableProcessors-threads thread pool, used by [ActionThreadService](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/action/ActionThreadService.java), to serve algorithms that use parallel-search. (e.g. ConnectedComponents, Submarines). It is shared to all instances in the server, to avoid of flooding the server with threads.
- 1*availableProcessors-threads ForkJoin thread pool, used by [ActionThreadService](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Server/src/main/java/org/hit/internetprogramming/eoh/server/action/ActionThreadService.java), to server Dijkstra algorithm, which is implemented using RecursiveAction, to run its search concurrently in Fork-Join mechanism. It is shared to all instances in the server, to avoid of flooding the server with threads.

# Common
[Source](https://github.com/haimadrian/InternetProgramming/tree/master/Project/Code/FinalProject/Common)  
Common project contains the models, as they are relevant for both client and server.  
Scroll down to see class diagrams.  
- [Index](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Common/src/main/java/org/hit/internetprogramming/eoh/common/mat/Index.java): A model that have row and column, used to access cells in an IMatrix. (This is also our vertex type in an IGraph)
- [IMatrix<T>](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Common/src/main/java/org/hit/internetprogramming/eoh/common/mat/IMatrix.java): A generic matrix for generic type T. In our project, the type is Integer which is used by binary matrices or weighted matrices.
- [IStandardMatrix<T>](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Common/src/main/java/org/hit/internetprogramming/eoh/common/mat/IStandardMatrix.java): A matrix that supports going in UP, DOWN, LEFT, RIGHT directions.
- [ICrossMatrix<T>](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Common/src/main/java/org/hit/internetprogramming/eoh/common/mat/ICrossMatrix.java): A matrix that supports going in UP-LEFT, UP-RIGHT, DOWN-LEFT, DOWN-RIGHT directions.  
The reason we use interfaces for Standard and Cross is to implement "multiple inheritance" in Matrix class, which supports going in ALL directions. (We re-use the default implementation of the interfaces mentioned above)
- [IGraph<T>](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Common/src/main/java/org/hit/internetprogramming/eoh/common/graph/IGraph.java): A traversable model, having root, indices and edges.
- [MatrixGraphAdapter<T>](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Common/src/main/java/org/hit/internetprogramming/eoh/common/graph/MatrixGraphAdapter.java): Adapts the IGraph API, where the generic type of IGraph is set to Index, for an instance of IMatrix.

## Notes
- We use Jackson for marshalling/unmarshalling requests and responses, thus avoiding of unsafe usage of ObjectInputStream and ObjectOutputStream
- We maintain a cache (string-heap-like) for instances of [Index](https://github.com/haimadrian/InternetProgramming/blob/master/Project/Code/FinalProject/Common/src/main/java/org/hit/internetprogramming/eoh/common/mat/Index.java) class, to avoid of too many creations of Index, and also make it possible to compare indices by using ==, and not equals. The cache is a weak cache, which means that the references can be garbage collected once there is no strong reference left referring to an index.
- We use log4j2 to write messages to console / log file, having details about the server while it is running in background, and gain the ability to print messages in DEBUG level when necessary.

# Client
[Source](https://github.com/haimadrian/InternetProgramming/tree/master/Project/Code/FinalProject/Client)  
The client has a standard menu, using console, to guide its user about input/output.  
We support saving graphs to file, so it will be easier to load graphs next time a client runs. The graphs are stored into json files at the client machine.

# Class Diagrams
Generated using Intellij. Powered by yFiles.  
## Matrix and Graph
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Diagrams/MatrixAndGraph.png" />

## Actions (Command Pattern)
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Diagrams/Actions.png" />

## Algorithms
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Diagrams/Algorithms.png" />

## Flow of request handling in server
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Diagrams/GET_GRAPH%20flow%20from%20client%20to%20server.png" height="860" />

# Screenshots
## Tray Icon
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/WindowsNotification.png" /> <img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/TrayIconExit.png" />

## Client Menu
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/ClientMenu.png" />

## Generate Graph
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/GenerateGraph.png" />

## Connected Components
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/ConnectedComponents.png" />

## Count Submarines
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/CountSubmarines.png" />

## Shortest Paths (BFS)
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/ShortestPathsBFS.png" />

## Shortest Paths (Dijkstra, standard matrix)
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/ShortestPathsWightedStandard.png" />

## Shortest Paths (Dijkstra, regular matrix)
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/ShortestPathsWeightedRegular.png" />

## Home (Web Browser)
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/WebBrowser1.png" />

## Generate Graph (Web Browser)
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/WebBrowser2.png" />

## Connected Components, Regular Matrix (Web Browser, with statistics)
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/WebBrowser3.png" />

## Connected Components, Standard Matrix (Web Browser, with statistics)
<img src="https://github.com/haimadrian/InternetProgramming/blob/master/Project/Screenshots/WebBrowser4.png" />

