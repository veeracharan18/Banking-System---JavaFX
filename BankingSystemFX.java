import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

abstract class AbstractBank {
    abstract void addCustomer(String name, int accountNumber);
    abstract java.util.List<String> getCustomerDescriptions();
    abstract boolean deleteCustomer(String name);
    abstract String searchCustomer(String name);
}

class CustomerNode {
    String customerName;
    int accountNumber;
    CustomerNode next, prev;

    CustomerNode(String name, int accountNumber) {
        this.customerName = name;
        this.accountNumber = accountNumber;
        this.next = null;
        this.prev = null;
    }
}

class DoublyBank extends AbstractBank {
    private CustomerNode head;

    @Override
    void addCustomer(String name, int accountNumber) {
        CustomerNode newNode = new CustomerNode(name, accountNumber);
        if (head == null) {
            head = newNode;
        } else {
            CustomerNode temp = head;
            while (temp.next != null)
                temp = temp.next;
            temp.next = newNode;
            newNode.prev = temp;
        }
    }

    @Override
    java.util.List<String> getCustomerDescriptions() {
        java.util.List<String> list = new java.util.ArrayList<>();
        CustomerNode temp = head;
        while (temp != null) {
            list.add("Name: " + temp.customerName + ", Account: " + temp.accountNumber);
            temp = temp.next;
        }
        return list;
    }

    @Override
    boolean deleteCustomer(String name) {
        CustomerNode temp = head;
        while (temp != null) {
            if (temp.customerName.equalsIgnoreCase(name)) {
                if (temp.prev != null) {
                    temp.prev.next = temp.next;
                } else {
                    head = temp.next;
                }
                if (temp.next != null) {
                    temp.next.prev = temp.prev;
                }
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    @Override
    String searchCustomer(String name) {
        CustomerNode temp = head;
        while (temp != null) {
            if (temp.customerName.equalsIgnoreCase(name)) {
                return "Found: Name: " + temp.customerName + ", Account: " + temp.accountNumber;
            }
            temp = temp.next;
        }
        return "Customer not found.";
    }
}

public class BankingSystemFX extends Application {

    private final DoublyBank bank = new DoublyBank();
    private final ListView<String> customerListView = new ListView<>();
    private final Label statusLabel = new Label();

    @Override
    public void start(Stage primaryStage) {
        // Input fields
        TextField nameInput = new TextField();
        nameInput.setPromptText("Customer Name");
        nameInput.setTooltip(new Tooltip("Enter the customer's full name"));

        TextField accInput = new TextField();
        accInput.setPromptText("Account Number");
        accInput.setTooltip(new Tooltip("Enter the customer's account number"));

        TextField searchInput = new TextField();
        searchInput.setPromptText("Enter name to search/delete");

        Button addButton = new Button("Add Customer");
        Button deleteButton = new Button("Delete");
        Button searchButton = new Button("Search");

        addButton.setOnAction(e -> {
            String name = nameInput.getText().trim();
            String accStr = accInput.getText().trim();
            if (name.isEmpty() || accStr.isEmpty()) {
                statusLabel.setText("Please enter name and account number.");
                return;
            }
            try {
                int accNum = Integer.parseInt(accStr);
                bank.addCustomer(name, accNum);
                updateCustomerList();
                statusLabel.setText("✅ Added customer: " + name);
                nameInput.clear();
                accInput.clear();
            } catch (NumberFormatException ex) {
                statusLabel.setText("❌ Account number must be an integer.");
            }
        });

        deleteButton.setOnAction(e -> {
            String name = searchInput.getText().trim();
            if (name.isEmpty()) {
                statusLabel.setText("Enter a name to delete.");
                return;
            }
            boolean deleted = bank.deleteCustomer(name);
            if (deleted) {
                updateCustomerList();
                statusLabel.setText("🗑️ Deleted customer: " + name);
            } else {
                statusLabel.setText("❌ Customer not found: " + name);
            }
            searchInput.clear();
        });

        searchButton.setOnAction(e -> {
            String name = searchInput.getText().trim();
            if (name.isEmpty()) {
                statusLabel.setText("Enter a name to search.");
                return;
            }
            String result = bank.searchCustomer(name);
            statusLabel.setText(result);
            searchInput.clear();
        });

        VBox addBox = new VBox(5, new Label("Add Customer"), nameInput, accInput, addButton);
        VBox searchBox = new VBox(5, new Label("Delete or Search Customer"), searchInput, new HBox(10, deleteButton, searchButton));
        VBox mainBox = new VBox(15, addBox, searchBox, new Label("Customer List:"), customerListView, statusLabel);
        mainBox.setPadding(new Insets(10));
        customerListView.setPrefHeight(150);

        Scene scene = new Scene(mainBox, 400, 450);
        primaryStage.setTitle("Banking System - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateCustomerList() {
        customerListView.getItems().setAll(bank.getCustomerDescriptions());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
