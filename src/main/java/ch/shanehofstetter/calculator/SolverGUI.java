package ch.shanehofstetter.calculator;

import ch.shanehofstetter.calculator.Nodes.MathNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

//TODO insert operators or constants at cursor position ..

public class SolverGUI extends Application implements GuiListener {

    private final int DEFAULT_SPACING = 10;
    private final int DEFAULT_WIDTH = 750;
    private final int DEFAULT_HEIGHT = 500;
    private final int DEFAULT_TOOL_BUTTON_WIDTH = 40;
    private final int DEFAULT_TOOL_BUTTON_HEIGHT = 30;
    private final String TITLE = "Math Solver";
    private final String CONSTANTS_TITLE = "Constants";
    private final String OUTPUT_TITLE = "Steps";
    private final String OPERATORS_TITLE = "Operators";
    private final String TREE_TITLE = "Hierarchy";
    private final String LAST_RESULTS_TITLE = "Last Results";
    private final String SOLVE = "Solve";
    private final String INPUT_PROMPT = "Enter a math-term or equation, e.g. 2 * 3x = 50";
    private final String DEFAULT_ERROR_TEXT = "Please enter a valid mathematical term.";
    private final String RESULT_PROMPT = "Result";
    private final Font DEFAULT_FONT = Font.font("Verdana", FontWeight.BOLD, 12);
    private ListView<String> output;
    private TextField resultText;
    private TextField inputText;
    private Label errorLabel;
    private TreeView<String> resultTree;
    private Calculator calculator;
    private ListView<String> resultListView;
    private ObservableList<String> resultList;
    private ObservableList<String> outputList;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        calculator = new Calculator();
        calculator.addListener(this);
        calculator.addListener(new StdoutListener());
        Constants.init();

        primaryStage.setTitle(TITLE);
        TitledPane outputPane = new TitledPane(OUTPUT_TITLE, output);
        outputPane.setCollapsible(false);
        outputList = FXCollections.observableArrayList();
        output = new ListView<>();
        output.setItems(outputList);
        output.setEditable(false);
        BorderPane root = new BorderPane();

        VBox verticalLayout = new VBox(DEFAULT_SPACING);
        verticalLayout.setSpacing(DEFAULT_SPACING);
        outputPane.setContent(output);
        verticalLayout.getChildren().add(outputPane);

        VBox leftSideVerticalBox = new VBox();
        leftSideVerticalBox.setSpacing(DEFAULT_SPACING);
        GridPane constantsGrid = makeConstantsGridPane();
        TitledPane constantsPane = new TitledPane(CONSTANTS_TITLE, constantsGrid);
        constantsPane.setCollapsible(false);
        leftSideVerticalBox.getChildren().add(constantsPane);
        GridPane operatorsGrid = makeOperatorsGridPane();
        TitledPane operatorsPane = new TitledPane(OPERATORS_TITLE, operatorsGrid);
        operatorsPane.setCollapsible(false);
        leftSideVerticalBox.getChildren().add(operatorsPane);

        HBox overallLayout = new HBox(DEFAULT_SPACING);
        overallLayout.setPadding(new Insets(DEFAULT_SPACING));
        overallLayout.getChildren().add(leftSideVerticalBox);
        overallLayout.getChildren().add(verticalLayout);
        HBox.setHgrow(verticalLayout, Priority.ALWAYS);

        HBox middleLayout = new HBox(DEFAULT_SPACING);
        resultTree = new TreeView<>();
        TitledPane treePane = new TitledPane(TREE_TITLE, resultTree);
        treePane.setCollapsible(false);

        resultListView = new ListView<>();
        resultList = FXCollections.observableArrayList();
        resultListView.setItems(resultList);
        resultListView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                String currentItemSelected = resultListView.getSelectionModel()
                        .getSelectedItem();
                inputText.appendText(currentItemSelected);
            }
        });

        TitledPane resultListPane = new TitledPane(LAST_RESULTS_TITLE, resultListView);
        resultListPane.setCollapsible(false);

        HBox.setHgrow(treePane, Priority.ALWAYS);
        middleLayout.getChildren().add(treePane);
        middleLayout.getChildren().add(resultListPane);

        verticalLayout.getChildren().add(middleLayout);

        resultText = new TextField("");
        resultText.setEditable(false);
        resultText.promptTextProperty().setValue(RESULT_PROMPT);
        resultText.setFont(DEFAULT_FONT);
        verticalLayout.getChildren().add(resultText);
        verticalLayout.getChildren().add(new Separator(Orientation.HORIZONTAL));

        errorLabel = new Label("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setFont(DEFAULT_FONT);
        errorLabel.setTextFill(Color.RED);
        verticalLayout.getChildren().add(errorLabel);

        inputText = new TextField();
        inputText.promptTextProperty().setValue(INPUT_PROMPT);

        Button calculateBtn = new Button(SOLVE);
        calculateBtn.setMinWidth(50);
        calculateBtn.setDefaultButton(true);
        calculateBtn.setOnAction(ev -> solve());

        HBox horizontalLayout = new HBox(DEFAULT_SPACING);
        horizontalLayout.setSpacing(DEFAULT_SPACING);
        horizontalLayout.getChildren().add(inputText);
        horizontalLayout.getChildren().add(calculateBtn);
        horizontalLayout.setFillHeight(true);
        HBox.setHgrow(inputText, Priority.ALWAYS);

        verticalLayout.getChildren().add(horizontalLayout);
        root.setCenter(overallLayout);
        primaryStage.setScene(new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(350);
        primaryStage.show();

        Platform.runLater(() -> inputText.requestFocus());
    }

    private GridPane makeToolButtonGrid() {
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(5);
        buttonGrid.setVgap(5);
        return buttonGrid;
    }

    private GridPane makeConstantsGridPane() {
        GridPane buttonGrid = makeToolButtonGrid();
        EventHandler<Event> constantEventHandler = event -> {
            Button clickedBtn = (Button) event.getSource();
            appendToInputText(clickedBtn.getText());
        };
        EventHandler<Event> specialConstantEventHandler = event -> {
            Button clickedBtn = (Button) event.getSource();
            appendToInputText(Constants.specialConstants.get(clickedBtn.getText()).toString());
        };
        int row = 0;
        int col = 0;
        int totalCount = 0;
        for (int i = 0; i < Constants.constants.keySet().size(); i++) {
            totalCount = i;
            addButtonToGridPane(buttonGrid, Character.toString((Character) Constants.constants.keySet().toArray()[i]), constantEventHandler, col, row);
            if (i % 2 > 0)
                row++;
            col++;
            if (col == 2)
                col = 0;
        }
        for (int i = 0; i < Constants.specialConstants.keySet().size(); i++) {
            totalCount++;
            addButtonToGridPane(buttonGrid, (String) Constants.specialConstants.keySet().toArray()[i], specialConstantEventHandler, col, row);
            if (totalCount % 2 > 0)
                row++;
            col++;
            if (col == 2)
                col = 0;
        }
        return buttonGrid;
    }

    private void appendToInputText(String text) {
        inputText.appendText(Operators.WHITESPACE + text + Operators.WHITESPACE);
    }

    private void addButtonToGridPane(GridPane buttonGrid, String btnText, EventHandler eventHandler, int col, int row) {
        Button btn = new Button(btnText);
        btn.setOnMouseClicked(eventHandler);

        btn.setPrefSize(DEFAULT_TOOL_BUTTON_WIDTH, DEFAULT_TOOL_BUTTON_HEIGHT);
        buttonGrid.add(btn, col, row);
    }

    private GridPane makeOperatorsGridPane() {
        GridPane buttonGrid = makeToolButtonGrid();
        EventHandler<Event> operatorEventHandler = event -> {
            Button clickedBtn = (Button) event.getSource();
            appendToInputText(clickedBtn.getText());
        };
        int col = 0;
        int row = 0;
        for (int i = 0; i < Operators.OPERATORS.length; i++) {
            addButtonToGridPane(buttonGrid, Character.toString(Operators.OPERATORS[i]), operatorEventHandler, col, row);
            if (i % 2 > 0)
                row++;
            col++;
            if (col == 2)
                col = 0;
        }

        return buttonGrid;
    }

    private void showErrorLabel(boolean show) {
        errorLabel.setVisible(show);
        errorLabel.setManaged(show);
    }

    void solve() {
        if (inputText.getText() != null) {
            if (inputText.getText().length() > 0) {
                showErrorLabel(false);
                outputList.clear();
                resultText.clear();
                resultTree.setRoot(null);
                calculator.solveStringTerm(inputText.getText());
                inputText.clear();
                return;
            }
        }
        showInputErrorMessage();
    }

    private void showInputErrorMessage() {
        showErrorLabel(true);
        errorLabel.setText(DEFAULT_ERROR_TEXT);
    }

    @Override
    public void showOutput(String output) {
        outputList.add(output.trim());
    }

    @Override
    public void showTree(MathNode rootNode) {
        TreeItem<String> rootItem = new TreeItem<>(rootNode.getName());
        rootItem.setExpanded(true);
        makeTree(rootItem, rootNode);
        resultTree.setRoot(rootItem);
    }

    void makeTree(TreeItem rootItem, MathNode rootNode) {
        for (MathNode node : rootNode.getChildren()) {
            TreeItem<String> treeItem = new TreeItem<>(node.getName());
            rootItem.getChildren().add(treeItem);
            treeItem.setExpanded(true);
            makeTree(treeItem, node);
        }
    }

    @Override
    public void showResult(String result) {
        resultText.setText(result);
        resultList.add(0, result);
    }

    @Override
    public void showError(String error, Exception exception) {
        showErrorLabel(true);
        errorLabel.setText(error);
    }
}
