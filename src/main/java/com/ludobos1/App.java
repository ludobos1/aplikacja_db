package com.ludobos1;

import com.ludobos1.encje.*;
import com.ludobos1.encje.enums.OrderStatus;
import com.ludobos1.encje.enums.PaymentMethod;
import com.ludobos1.encje.enums.PaymentStatus;
import com.ludobos1.encje.enums.Role;
import com.ludobos1.services.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class App extends Application {
    private ConfigurableApplicationContext springContext;
    private UserService userService;
    private BooksService booksService;
    private OrderService orderService;
    private PaymentService paymentService;
    private Order_itemsService order_itemsService;
    private ReviewService reviewService;
    private User myUser;
    private Order myOrder;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        loginStage(primaryStage);
    }

    private void userClient(Stage primaryStage){
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        TextField bookName = new TextField();
        bookName.setPromptText("Enter the book name or author");
        Button searchButton = new Button("Search");
        Button refreshButton = new Button("Refresh");
        Button viewCartButton = new Button("View cart");
        searchButton.setOnAction(actionEvent -> {
            vBox.getChildren().clear();
            vBox.getChildren().add(hBox);
            List<Book> filteredBooks = booksService.getBooksByTitleOrAuthorOrCategory(bookName.getText());
            displayBooks(vBox, filteredBooks);
        });
        viewCartButton.setOnAction(actionEvent -> {
            if(myOrder != null) {
                Stage viewCartStage = new Stage();
                VBox itemsBox = new VBox();
                viewCartStage.setTitle("Cart");
                viewCartStage.initModality(Modality.APPLICATION_MODAL);
                List<Order_items> myItems = order_itemsService.findByOrderId(myOrder.getId());
                for(Order_items item : myItems) {
                    Book book = booksService.getBookById(item.getBook().getId());
                    Label itemsLabel = new Label(book.getTitle() + " " + book.getAuthor() + " price: " + book.getPrice() + " quantity: " + item.getQuantity());
                    itemsBox.getChildren().add(itemsLabel);
                }
                Label paymentLabel = new Label("Payment Method: ");
                ComboBox<PaymentMethod> paymentMethod = new ComboBox<>();
                paymentMethod.getItems().addAll(PaymentMethod.values());
                paymentMethod.setValue(PaymentMethod.BLIK);
                Button confirmButton = new Button("Confirm");
                Button cancelButton = new Button("Cancel");
                confirmButton.setOnAction(actionEvent1 -> {
                    Payment payment = new Payment();
                    payment.setPaymentStatus(PaymentStatus.PENDING);
                    payment.setPaymentMethod(paymentMethod.getValue());
                    payment.setOrder(myOrder);
                    myOrder.setTotalPrice(orderService.findOrderById(myOrder.getId()).getTotalPrice());
                    paymentScene(viewCartStage, paymentMethod.getValue(), payment);
                });
                cancelButton.setOnAction(actionEvent1 -> {
                    myOrder.setStatus(OrderStatus.CANCELLED);
                    orderService.updateOrder(myOrder);
                    myOrder = null;
                    viewCartStage.close();
                });
                itemsBox.getChildren().addAll(paymentLabel, paymentMethod, confirmButton);
                viewCartStage.setScene(new Scene(itemsBox, 800, 800));
                viewCartStage.show();
            }
        });
        refreshButton.setOnAction(actionEvent -> {
            List<Book> refreshedBooks = booksService.getAllBooks();
            vBox.getChildren().clear();
            vBox.getChildren().add(hBox);
            displayBooks(vBox, refreshedBooks);
        });
        hBox.getChildren().addAll(bookName, searchButton, refreshButton, viewCartButton);
        List<Book> allBooks = booksService.getAllBooks();
        vBox.getChildren().add(hBox);
        displayBooks(vBox, allBooks);
        Scene scene = new Scene(vBox,900,900);
        primaryStage.setTitle("User Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayBooks(VBox vBox, List<Book> books){
        for (Book book : books) {
            HBox bookHBox = new HBox();
            Label details = new Label(book.getTitle() + "; " + book.getAuthor() + "; category: " + book.getCategory().getName() + "; Price: " + book.getPrice() + " $; Stock: " +  book.getStock());
            Button addToCartButton = new Button("Add to Cart");
            Button writeAReviewButton = new Button("Write a review");
            Button seeReviewsButton = new Button("See Reviews");
            addToCartButton.setOnAction(actionEvent -> {
                if(myOrder == null){
                    myOrder = new Order();
                    myOrder.setUser(myUser);
                    orderService.createOrder(myOrder);
                }
                if(book.getStock()>0 && myOrder != null) {
                    System.out.println("placing order: " + myOrder.getId());
                    order_itemsService.addItem(myOrder, book);
                }
            });
            writeAReviewButton.setOnAction(actionEvent -> reviewScene(book));
            seeReviewsButton.setOnAction(actionEvent -> {});
            bookHBox.getChildren().addAll(details, addToCartButton, writeAReviewButton, seeReviewsButton);
            vBox.getChildren().add(bookHBox);
        }
    }

    private void reviewScene(Book book){
        Stage reviewStage = new Stage();
        VBox vBox = new VBox();
        reviewStage.setTitle("Review");
        reviewStage.initModality(Modality.APPLICATION_MODAL);
        Label scoreLabel = new Label("Score: ");
        ComboBox<Integer> score = new ComboBox<>();
        score.getItems().addAll(1, 2, 3, 4, 5);
        score.setValue(5);
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Write your opinion here");
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(actionEvent -> {
            Review review = new Review();
            review.setBook(book);
            review.setRating(score.getValue());
            review.setComment(commentArea.getText());
            review.setUser(myUser);
            Alert reviewAlert = new Alert(Alert.AlertType.CONFIRMATION);
            reviewAlert.setContentText(reviewService.save(review));
            reviewAlert.showAndWait();
            reviewStage.close();
        });
        vBox.getChildren().addAll(scoreLabel, score, commentArea, confirmButton);
        reviewStage.setScene(new Scene(vBox, 800, 800));
        reviewStage.show();
    }

    private void paymentScene(Stage stage, PaymentMethod paymentMethod, Payment payment){
        VBox vBox = new VBox();
        switch (paymentMethod){
            case CARD:
                Label toPayLabel = new Label("To pay: " + myOrder.getTotalPrice());
                Label cardNumberLabel = new Label("Card Number: ");
                Label cardHolderNameLabel = new Label("Card Holder Name: ");
                Label expiryDateLabel = new Label("Expiry Date: ");
                Label CVCLabel = new Label("CVC: ");
                TextField cardNumber = new TextField();
                TextField cardHolderName = new TextField();
                TextField expiryDate = new TextField();
                TextField cvc = new TextField();
                Button confirmButton = new Button("Confirm");
                confirmButton.setOnAction(actionEvent -> {
                    finalisePayment(payment, stage);
                });
                vBox.getChildren().addAll(toPayLabel, cardNumberLabel, cardNumber, cardHolderNameLabel, cardHolderName, expiryDateLabel, expiryDate, CVCLabel, cvc, confirmButton);
                break;
            case BLIK:
                Label toPayLabel1 = new Label("To pay: " + myOrder.getTotalPrice());
                Label BLIKLabel = new Label("BLIK code: ");
                TextField BLIKCode = new TextField();
                Button confirmButton1 = new Button("confirm");
                confirmButton1.setOnAction(actionEvent -> {
                    finalisePayment(payment, stage);
                });
                vBox.getChildren().addAll(toPayLabel1, BLIKLabel, BLIKCode, confirmButton1);
                break;
        }
        Scene scene = new Scene(vBox, 800, 800);
        stage.setTitle("Payment");
        stage.setScene(scene);
        stage.show();
    }

    private void finalisePayment(Payment payment,Stage stage){
        myOrder.setStatus(OrderStatus.PROCESSING);
        orderService.updateOrder(myOrder);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentService.updatePayment(payment);
        myOrder = null;
        stage.close();
    }

    private void loginStage(Stage primaryStage){
        PasswordField password = new PasswordField();
        TextField username = new TextField();
        password.setPromptText("Enter password");
        username.setPromptText("Enter username");
        Button login = new Button("Login");
        Button register = new Button("Register");
        login.setOnAction(actionEvent -> {
            myUser = userService.authenticateUser(username.getText(), password.getText());
            if(myUser!=null) {
                userClient(primaryStage);
            }
        });
        register.setOnAction(actionEvent -> {
            registerStage(primaryStage);
        });
        VBox root = new VBox();
        root.getChildren().addAll(username, password, login, register);
        Scene loginScene = new Scene(root,300,300);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void registerStage(Stage primaryStage){
        Label emailArea = new Label("email:");
        Label usernameArea = new Label("username:");
        Label passwordArea = new Label("password:");
        Label confirmPasswordArea = new Label("confirm password:");
        TextField email = new TextField();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        PasswordField confirmPassword = new PasswordField();
        Button register = new Button("Register");
        VBox root = new VBox();
        root.getChildren().addAll(emailArea, email,usernameArea, username,passwordArea, password,confirmPasswordArea, confirmPassword, register);
        Scene scene = new Scene(root,300,300);
        primaryStage.setScene(scene);
        primaryStage.show();
        register.setOnAction(actionEvent -> {
            registerHandler(username, password, email, confirmPassword, primaryStage);
        });
    }

    private void registerHandler(TextField username, PasswordField password, TextField email, PasswordField confirmPassword, Stage primaryStage){
        if(password.getText().equals(confirmPassword.getText())) {
            User newUser = new User();
            newUser.setUsername(username.getText());
            newUser.setPassword(password.getText());
            newUser.setRole(Role.USER);
            newUser.setEmail(email.getText());
            String response = userService.registerUser(newUser);
            if(response.equals("account registered successfully")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText(response);
                alert.showAndWait();
                loginStage(primaryStage);
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(response);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Passwords do not match");
            alert.showAndWait();
        }
    }
    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(App.class);
        userService = springContext.getBean(UserService.class);
        booksService = springContext.getBean(BooksService.class);
        orderService = springContext.getBean(OrderService.class);
        order_itemsService = springContext.getBean(Order_itemsService.class);
        paymentService = springContext.getBean(PaymentService.class);
        reviewService = springContext.getBean(ReviewService.class);
    }

    @Override
    public void stop() throws Exception {
        if (myOrder != null) {
            if (myOrder.getStatus().equals(OrderStatus.NEW))
            {
                myOrder.setStatus(OrderStatus.CANCELLED);
                orderService.updateOrder(myOrder);
            }
        }
        springContext.close();
    }
}
