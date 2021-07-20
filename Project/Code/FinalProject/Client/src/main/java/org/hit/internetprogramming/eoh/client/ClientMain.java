package org.hit.internetprogramming.eoh.client;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hit.internetprogramming.eoh.client.action.MenuAction;
import org.hit.internetprogramming.eoh.client.web.GraphWebService;
import org.hit.internetprogramming.eoh.common.action.ActionType;
import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Request;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.comms.TwoVerticesBody;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.log.LoggingStream;
import org.hit.internetprogramming.eoh.common.mat.IMatrix;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.common.mat.MatrixType;
import org.hit.internetprogramming.eoh.common.util.JsonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Haim Adrian
 * @since 23-Apr-21
 */
public class ClientMain {
    private static final String STDOUT_LOGGER_NAME = "stdout";
    private static final String STDERR_LOGGER_NAME = "stderr";

    private Logger log;

    public static void main(String[] args) {
        redirectStreamsToLog4j();

        new ClientMain().run();
    }

    private static void redirectStreamsToLog4j() {
        System.setOut(new PrintStream(new LoggingStream(STDOUT_LOGGER_NAME), true));
        System.setErr(new PrintStream(new LoggingStream(STDERR_LOGGER_NAME), true));

        System.out.println(getJavaVersionString());
    }

    private static String getJavaVersionString() {
        return "java version \"" + System.getProperty("java.version") + "\"" + System.lineSeparator() + System.getProperty("java.runtime.name") +
            " (build " + System.getProperty("java.runtime.version") + ")" + System.lineSeparator() + System.getProperty("java.vm.name") +
            " (build " + System.getProperty("java.vm.version") + ", " + System.getProperty("java.vm.info") + ")";
    }

    private void run() {
        log = LogManager.getLogger(ClientMain.class);

        log.info("Graph Client");
        try (Scanner scanner = new Scanner(System.in)) {
            boolean isRunning = true;
            do {
                MenuAction choice = showMenu(scanner);
                switch (choice) {
                    case CREATE_GRAPH:
                        createNewGraph(scanner);
                        break;
                    case LOAD_GRAPH:
                        loadGraphFromFile(scanner);
                        break;
                    case SAVE_GRAPH:
                        saveGraphToFile(scanner);
                        break;
                    case GENERATE_RANDOM_GRAPH:
                        generateRandomGraph(scanner);
                        break;
                    case GET_NEIGHBORS:
                    case GET_REACHABLES:
                        Index index = readIndex(scanner, "Please enter index in tuple format. e.g. (1, 1)");
                        executeRequest(new Request(choice.getActionType(), index), false, new TypeReference<List<Index>>() {});
                        break;
                    case CONNECTED_COMPONENTS:
                        executeRequest(new Request(ActionType.CONNECTED_COMPONENTS), false, new TypeReference<List<Set<Index>>>() {});
                        break;
                    case SHORTEST_PATHS:
                        Index source = readIndex(scanner, "Please enter source index in tuple format. e.g. (0, 0)");
                        Index dest = readIndex(scanner, "Please enter destination index in tuple format. e.g. (2, 2)");
                        log.info("Enter algorithm to use (1=BFS, 2=Bellman-Ford (weights))");
                        ActionType actionType = readChoice(scanner, 1, 2) == 1 ? ActionType.SHORTEST_PATHS : ActionType.SHORTEST_PATHS_IN_WEIGHTED_GRAPH;
                        executeRequest(new Request(actionType, new TwoVerticesBody<>(source, dest)), false, new TypeReference<List<Collection<Index>>>() {});
                        break;
                    case PRINT_GRAPH:
                        executeRequest(new Request(ActionType.PRINT_GRAPH), true, null);
                        break;
                    default:
                        isRunning = false;
                        GraphWebService.getInstance().disconnect();
                        log.info("Good bye!");
                }
            } while (isRunning);
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    private <T> void executeRequest(Request request, boolean resultAsMessage, TypeReference<T> responseType) {
        Response response = GraphWebService.getInstance().executeRequest(request);
        if (isOkResponse(response)) {
            log.info("Result: " + System.lineSeparator() + (resultAsMessage ? response.getMessage() : response.getBodyAs(responseType)) + System.lineSeparator());
        }
    }

    private void saveGraphToFile(Scanner scanner) {
        Response response = GraphWebService.getInstance().executeRequest(new Request(ActionType.GET_GRAPH));
        if (isOkResponse(response)) {
            File file = readFileName(scanner, "Please enter file path to save graph to. e.g. graph1.json", false);

            try {
                IGraph<Index> graph = response.getBodyAs(new TypeReference<>() {});
                Files.write(file.toPath(),
                    JsonUtils.writeValueAsString(graph).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

                log.info("Graph stored to: " + file.getCanonicalPath() + System.lineSeparator());
            } catch (IOException e) {
                log.error("Error has occurred while saving graph to disk: " + e, e);
            }
        }
    }

    private void loadGraphFromFile(Scanner scanner) {
        File file = readFileName(scanner, "Please enter file path to load graph from. e.g. graph1.json", true);

        try {
            IGraph<Index> graph = GraphWebService.getInstance().getObjectMapper().readValue(new FileInputStream(file), new TypeReference<>() {});
            Response response = GraphWebService.getInstance().executeRequest(new Request(ActionType.PUT_GRAPH, graph));
            if (isOkResponse(response)) {
                log.info("Graph sent to server successfully. file=" + file.getCanonicalPath() + System.lineSeparator());
            }
        } catch (IOException e) {
            log.error("Error has occurred while loading graph from disk: " + e, e);
        }
    }

    private void createNewGraph(Scanner scanner) {
        Index dimension = readIndex(scanner, "Enter matrix dimension. e.g. (3, 3)");
        log.info("Enter matrix kind (1=Standard, 2=Cross , 3=Regular)");
        int matrixKind = readChoice(scanner, 1, MatrixType.values().length);
        log.info("Select matrix content (1=Binary, 2=Any Integer)");
        boolean isBinary = readChoice(scanner, 1, 2) == 1;
        IMatrix<Integer> matrix = MatrixType.values()[matrixKind - 1].newInstance(dimension.getRow(), dimension.getColumn());

        // Minimum and maximum supported value for the selected matrix content.
        int minBound, maxBound;
        if (isBinary) {
            minBound = 0;
            maxBound = 1;
        } else {
            minBound = Integer.MIN_VALUE;
            maxBound = Integer.MAX_VALUE;
        }

        log.info("Enter matrix values, cell by cell. You have to enter 0 or 1 only, and you have chosen to create a matrix with " + (dimension.getRow() * dimension.getColumn()) + " elements");
        for (int i = 0; i < dimension.getRow(); i++) {
            for (int j = 0; j < dimension.getColumn(); j++) {
                Integer value = readChoice(scanner, minBound, maxBound);

                // In case of binary matrix, convert 0 to null, so we will have an indication for not having a value
                // at some index. (Cause 0 is a legal integer value)
                if (isBinary && (value == 0)) {
                    value = null;
                }

                matrix.setValue(Index.from(i, j), value);
            }
        }

        Index root;
        do {
            root = readIndex(scanner, "Enter root vertex location. e.g. (0, 0). (A root must have a value. e.g. 1 in binary matrix)");
        } while (!matrix.hasValue(root));

        IGraph<Index> graph = new MatrixGraphAdapter<>(matrix, root);

        Response response = GraphWebService.getInstance().executeRequest(new Request(ActionType.PUT_GRAPH, graph));
        if (isOkResponse(response)) {
            log.info("Graph sent to server successfully." + System.lineSeparator());
        }
    }

    private void generateRandomGraph(Scanner scanner) {
        Index dimension = readIndex(scanner, "Enter matrix dimension. e.g. (3, 3)");
        log.info("Enter matrix kind (1=Standard, 2=Cross , 3=Regular)");
        int matrixKind = readChoice(scanner, 1, MatrixType.values().length);
        log.info("Select matrix content (1=Binary, 2=Any Integer)");
        boolean isBinary = readChoice(scanner, 1, 2) == 1;

        ActionType actionType;
        switch (MatrixType.values()[matrixKind - 1]) {
            case STANDARD:
                actionType = isBinary ? ActionType.GENERATE_RANDOM_BINARY_GRAPH_STANDARD : ActionType.GENERATE_RANDOM_GRAPH_STANDARD;
                break;
            case CROSS:
                actionType = isBinary ? ActionType.GENERATE_RANDOM_BINARY_GRAPH_CROSS : ActionType.GENERATE_RANDOM_GRAPH_CROSS;
                break;
            default:
                actionType = isBinary ? ActionType.GENERATE_RANDOM_BINARY_GRAPH_REGULAR : ActionType.GENERATE_RANDOM_GRAPH_REGULAR;
                break;
        }

        executeRequest(new Request(actionType, dimension), true, null);
    }

    private boolean isOkResponse(Response response) {
        boolean isOk = response.getStatus() == HttpStatus.OK.getCode();

        if (!isOk) {
            log.error("Something went wrong: " + response.getMessage() + System.lineSeparator());
        }

        return isOk;
    }

    private MenuAction showMenu(Scanner scanner) {
        StringBuilder menu = new StringBuilder("Please select an action: ").append(System.lineSeparator());

        // Get all except disconnect, which we show last.
        for (int i = 1; i < MenuAction.values().length; i++) {
            MenuAction menuAction = MenuAction.values()[i];
            menu.append(menuAction.ordinal()).append(". ").append(menuAction.getText()).append(System.lineSeparator());
        }

        menu.append(MenuAction.DISCONNECT.ordinal()).append(". ").append(MenuAction.DISCONNECT.getText());
        log.info(menu);

        int choice = readChoice(scanner, 0, MenuAction.values().length - 1);
        return MenuAction.values()[choice];
    }

    private int readChoice(Scanner scanner, int minValue, int maxValue) {
        int choice = minValue - 1;
        do {
            String input = scanner.nextLine().trim();
            try { choice = Integer.parseInt(input); } catch (Exception ignore) { }

            if ((choice < minValue) || (choice > maxValue)) {
                log.warn("Wrong input. Try again");
            }
        } while ((choice < minValue) || (choice > maxValue));

        return choice;
    }

    private Index readIndex(Scanner scanner, String instruction) {
        Index index = null;

        log.info(instruction);

        do {
            // Ignore all whitespace characters, to ease parsing
            String input = scanner.nextLine().replaceAll("\\s+", "");
            try {
                // Get rid of "(" and ")", then split by "," so we will have the two numbers.
                String[] nums = input.substring(1, input.length() - 1).split(",");
                index = Index.from(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
            } catch (Exception ignore) { }

            if (index == null) {
                log.warn("Wrong input. Format is: (0, 0). Try again");
            }
        } while (index == null);

        return index;
    }

    private File readFileName(Scanner scanner, String instruction, boolean validateExisting) {
        File file;

        log.info(instruction);

        do {
            String filePath = scanner.nextLine();
            try {
                file = new File(filePath);
                if (validateExisting && !file.exists()) {
                    file = null;
                }
            } catch (Exception ignore) {
                file = null;
            }

            if (file == null) {
                log.warn("Wrong input. Make sure file exists and we have R/W permissions. Try again");
            }
        } while (file == null);

        return file;
    }

}

