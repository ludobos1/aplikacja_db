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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
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
    private CategoryService categoryService;
    private User myUser;
    private Order myOrder;
    private Role myRole = Role.USER;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        loginStage(primaryStage);
    }

    // Client usera.
    private void userClient(Stage primaryStage){
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        TextField bookName = new TextField();
        bookName.setPromptText("Enter the book name or author");
        Button searchButton = new Button("Search");
        Button refreshButton = new Button("Back");
        Button viewCartButton = new Button("View cart");
        Button viewMyOrdersButton = new Button("View my orders");
        searchButton.setOnAction(actionEvent -> {
            vBox.getChildren().clear();
            vBox.getChildren().add(hBox);
            List<Book> filteredBooks = booksService.getBooksByTitleOrAuthorOrCategory(bookName.getText());
            displayBooks(vBox, filteredBooks, hBox, primaryStage);
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
            bookName.clear();
            vBox.getChildren().clear();
            vBox.getChildren().add(hBox);
            displayBooks(vBox, refreshedBooks,hBox, primaryStage);
        });
        viewMyOrdersButton.setOnAction(actionEvent -> {
            List<Order> orders = orderService.findAllOrders();
            displayOrders(primaryStage, orders);
        });
        hBox.getChildren().addAll(bookName, searchButton, refreshButton, viewCartButton);
        List<Book> allBooks = booksService.getAllBooks();
        vBox.getChildren().add(hBox);
        displayBooks(vBox, allBooks,hBox, primaryStage);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vBox);
        Scene scene = new Scene(scrollPane,900,900);
        primaryStage.setTitle("User Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayBooks(VBox vBox, List<Book> books, HBox hBox, Stage primaryStage){
        for (Book book : books) {
            HBox bookHBox = new HBox();
            Label details = new Label(book.getTitle() + "; " + book.getAuthor() + "; category: " + book.getCategory().getName() +
                    "; Price: " + book.getPrice() + " $; Stock: " +  book.getStock());
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
                    List<Book> refreshedBooks = booksService.getAllBooks();
                    vBox.getChildren().clear();
                    vBox.getChildren().add(hBox);
                    displayBooks(vBox, refreshedBooks,hBox, primaryStage);
                }
            });
            writeAReviewButton.setOnAction(actionEvent -> reviewScene(book));
            seeReviewsButton.setOnAction(actionEvent -> {
                List<Review> reviews = reviewService.findAll();
                displayReviews(primaryStage, reviews, book);
            });
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
    // Client usera.

    //Client admina.
    public void adminClient(Stage primaryStage){
        VBox vBox = new VBox();
        Label booksLabel = new Label("Moderate books:");
        Button showBooksButton = new Button("Books");
        Label usersLabel = new Label("Moderate users:");
        Button createUserButton = new Button("Create User");
        Label categoriesLabel = new Label("Moderate categories:");
        Button showCategoriesButton = new Button("Categories");
        Label reviewsLabel = new Label("Moderate reviews:");
        Button showReviewsButton = new Button("Reviews");
        showBooksButton.setOnAction(actionEvent -> {
            List<Book> books = booksService.getAllBooks();
            displayBooksForAdmin(primaryStage, books);
        });
        createUserButton.setOnAction(actionEvent -> {
            Stage createUserStage = new Stage();
            createUserStage.setTitle("Create User");
            createUserStage.initModality(Modality.APPLICATION_MODAL);
            registerStage(createUserStage);
        });
        showCategoriesButton.setOnAction(actionEvent -> {
            List<Category> categories = categoryService.getAllCategories();
            displayCategories(primaryStage, categories);
        });
        showReviewsButton.setOnAction(actionEvent -> {
            List<Review> reviews = reviewService.findAll();
            displayReviews(primaryStage, reviews, null);
        });
        vBox.getChildren().addAll(booksLabel, showBooksButton, usersLabel, createUserButton, categoriesLabel,
                showCategoriesButton, reviewsLabel, showReviewsButton);
        primaryStage.setTitle("Admin Client");
        primaryStage.setScene(new Scene(vBox, 800, 800));
        primaryStage.show();
    }

    private void displayBooksForAdmin(Stage primaryStage, List<Book> books){
        VBox booksBox = new VBox();
        HBox buttonsBox = new HBox();
        TextField bookName = new TextField();
        bookName.setPromptText("Enter the book name or author");
        Button searchButton = new Button("Search");
        Button backButton = new Button("Back");
        Button addButton = new Button("Add book");
        buttonsBox.getChildren().addAll(bookName, searchButton, backButton, addButton);
        booksBox.getChildren().add(buttonsBox);
        searchButton.setOnAction(actionEvent -> {
            List<Book> filteredBooks = booksService.getBooksByTitleOrAuthorOrCategory(bookName.getText());
            displayBooksForAdmin(primaryStage, filteredBooks);
        });
        backButton.setOnAction(actionEvent -> {
            if(myRole == Role.ADMIN) {
                adminClient(primaryStage);
            } else if(myRole == Role.EMPLOYEE){
                employeeClient(primaryStage);
            }
        });
        addButton.setOnAction(actionEvent -> {
            addBookScene(primaryStage);
        });
        for (Book book : books) {
            HBox bookHBox = new HBox();
            Label details = new Label(book.getTitle() + "; " + book.getAuthor() + "; category: " + book.getCategory().getName() +
                    "; Price: " + book.getPrice() + " $; Stock: " +  book.getStock());
            Button editButton = new Button("Edit");
            Button deleteButton = new Button("Delete");
            editButton.setOnAction(actionEvent -> editBookScene(book, primaryStage));
            deleteButton.setOnAction(actionEvent -> {
                booksService.deleteBook(book);
                displayBooksForAdmin(primaryStage, booksService.getAllBooks());
            });
            bookHBox.getChildren().addAll(details, editButton, deleteButton);
            booksBox.getChildren().add(bookHBox);
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(booksBox);
        primaryStage.setScene(new Scene(scrollPane, 700, 700));
        primaryStage.show();
    }

    private void displayCategories(Stage primaryStage, List<Category> categories){
        VBox categoriesBox = new VBox();
        Button backButton = new Button("Back");
        Button addButton = new Button("Add category");
        categoriesBox.getChildren().addAll(backButton, addButton);
        backButton.setOnAction(actionEvent -> {
            adminClient(primaryStage);
        });
        addButton.setOnAction(actionEvent -> {
            addCategoryScene(primaryStage);
        });
        for (Category category : categories) {
            HBox categoryHBox = new HBox();
            Label categoryName = new Label(category.getName());
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(actionEvent -> {
                categoryService.deleteCategory(category);
                displayCategories(primaryStage, categoryService.getAllCategories());
            });
            categoryHBox.getChildren().addAll(categoryName, deleteButton);
            categoriesBox.getChildren().add(categoryHBox);
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(categoriesBox);
        primaryStage.setScene(new Scene(scrollPane, 700, 700));
        primaryStage.show();
    }

    private void addCategoryScene(Stage primaryStage){
        Stage stage = new Stage();
        VBox vBox = new VBox();
        stage.setTitle("Add category");
        stage.initModality(Modality.APPLICATION_MODAL);
        Label categoryName = new Label("Category: ");
        TextField categoryField = new TextField();
        Button addButton = new Button("Add category");
        addButton.setOnAction(actionEvent -> {
            Category category = new Category();
            category.setName(categoryField.getText());
            if(!categoryField.getText().isEmpty()) {
                categoryService.addCategory(category);
                displayCategories(primaryStage, categoryService.getAllCategories());
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid category name");
            }
        });
        vBox.getChildren().addAll(categoryName, categoryField, addButton);
        stage.setScene(new Scene(vBox, 800, 800));
        stage.show();
    }

    private void addBookScene(Stage primaryStage){
        Stage stage = new Stage();
        VBox vBox = new VBox();
        stage.setTitle("Add Book");
        stage.initModality(Modality.APPLICATION_MODAL);
        Label title = new Label("Title: ");
        Label author = new Label("Author: ");
        Label ISBN = new Label("ISBN: ");
        Label price = new Label("Price: ");
        Label category = new Label("Category: ");
        Label stock = new Label("Stock: ");
        TextField titleField = new TextField();
        TextField authorField = new TextField();
        TextField ISBNField = new TextField();
        TextField priceField = new TextField();
        ComboBox<String> categoryField = new ComboBox<>();
        TextField stockField = new TextField();
        List<Category> categories = categoryService.getAllCategories();
        Button addButton = new Button("Add");
        addButton.setOnAction(actionEvent -> {
            Book book = new Book();
            addBook(titleField, authorField, ISBNField, priceField, categoryField, stockField, book, stage, primaryStage);
        });
        for(Category category1 : categories){
            categoryField.getItems().add(category1.getName());
        }
        vBox.getChildren().addAll(title,titleField, author, authorField, ISBN, ISBNField, price, priceField,
                category, categoryField, stock, stockField, addButton);
        stage.setScene(new Scene(vBox, 800, 800));
        stage.show();
    }

    private void editBookScene(Book book, Stage primaryStage){
        Stage editStage = new Stage();
        VBox editBox = new VBox();
        editStage.setTitle("Edit Book");
        editStage.initModality(Modality.APPLICATION_MODAL);
        Label title = new Label("Title: ");
        Label author = new Label("Author: ");
        Label ISBN = new Label("ISBN: ");
        Label price = new Label("Price: ");
        Label category = new Label("Category: ");
        Label stock = new Label("Stock: ");
        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField ISBNField = new TextField(book.getIsbn());
        TextField priceField = new TextField(book.getPrice().toString());
        ComboBox<String> categoryField = new ComboBox<>();
        TextField stockField = new TextField(String.valueOf(book.getStock()));
        Button editButton = new Button("Edit");
        List<Category> categories = categoryService.getAllCategories();
        for(Category category1 : categories){
            categoryField.getItems().add(category1.getName());
        }
        editButton.setOnAction(actionEvent -> {
            addBook(titleField, authorField, ISBNField, priceField, categoryField, stockField, book, editStage, primaryStage);
        });
        editBox.getChildren().addAll(title,titleField, author, authorField, ISBN, ISBNField, price, priceField,
                category, categoryField, stock, stockField, editButton);
        editStage.setScene(new Scene(editBox, 700, 700));
        editStage.show();
    }

    private void addBook(TextField titleField, TextField authorField,TextField ISBNField, TextField priceField, ComboBox<String> categoryField, TextField stockField, Book book, Stage stage, Stage primaryStage){
        try {
            if (!titleField.getText().isEmpty() && !authorField.getText().isEmpty() && !ISBNField.getText().isEmpty() &&
                    !priceField.getText().isEmpty() && !stockField.getText().isEmpty() && !categoryField.getItems().isEmpty()) {
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setIsbn(ISBNField.getText());
                book.setPrice(new BigDecimal(priceField.getText()));
                book.setCategory(categoryService.getCategoryByName(categoryField.getValue()));
                book.setStock(Integer.parseInt(stockField.getText()));
                String updateResult = booksService.addBook(book);
                if (updateResult.equals("Book updated")){
                    displayBooksForAdmin(primaryStage, booksService.getAllBooks());
                    stage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText(updateResult);
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Fields cannot be empty");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid numbers in field Price and Stock.");
            alert.showAndWait();
        }
    }
    private void displayReviews(Stage primaryStage, List<Review> reviews, Book book){
        VBox allReviewsBox = new VBox();
        Button backButton = new Button("Back");
        backButton.setOnAction(actionEvent -> {
            if (myRole==Role.ADMIN){
                adminClient(primaryStage);
            } else if (myRole==Role.USER){
                userClient(primaryStage);
            } else {
                employeeClient(primaryStage);
            }
        });
        allReviewsBox.getChildren().add(backButton);
        for(Review review : reviews){
            if(myRole == Role.USER){
                if (review.getBook().getId() == book.getId()){
                    displaySingleReview(review, primaryStage, allReviewsBox, book);
                }
            } else {
                displaySingleReview(review, primaryStage, allReviewsBox, book);
            }
        }
        primaryStage.setScene(new Scene(allReviewsBox, 800, 800));
    }
    private void displaySingleReview(Review review, Stage primaryStage, VBox allReviewsBox, Book book){
        HBox hBox = new HBox();
        VBox vBox = new VBox();
        Label userLabel = new Label(review.getUser().getUsername());
        TextArea commentArea = new TextArea(review.getComment());
        commentArea.setEditable(false);
        commentArea.setPrefSize(200,100);
        Label starsLabel = new Label("  stars: " + review.getRating() + "/5");
        vBox.getChildren().addAll(starsLabel, commentArea);
        hBox.getChildren().addAll(userLabel,vBox);
        if (myRole==Role.USER){
            if (review.getUser().getId().equals(myUser.getId())){
                Button deleteMyReviewButton = new Button("Delete");
                deleteMyReviewButton.setOnAction(actionEvent -> {
                    reviewService.delete(review);
                    displayReviews(primaryStage, reviewService.findAll(), book);
                });
                hBox.getChildren().add(deleteMyReviewButton);
            }
        } else if (myRole==Role.ADMIN||myRole==Role.EMPLOYEE){
            Button deleteReviewButton = new Button("Delete");
            deleteReviewButton.setOnAction(actionEvent -> {
                reviewService.delete(review);
                displayReviews(primaryStage, reviewService.findAll(), book);
            });
            Label bookLabel = new Label("  review for: " + review.getBook().getTitle());
            hBox.getChildren().addAll(deleteReviewButton, bookLabel);
        }
        allReviewsBox.getChildren().add(hBox);
    }
    //Client admina.

    //Client pracownika

    private void employeeClient(Stage primaryStage){
        VBox vBox = new VBox();
        Label orderLabel = new Label("Handle orders:");
        Button orderButton = new Button("Orders");
        Label booksLabel = new Label("Moderate books:");
        Button booksButton = new Button("Books");
        Label reviewsLabel = new Label("Moderate reviews:");
        Button reviewsButton = new Button("Reviews");
        booksButton.setOnAction(actionEvent -> {
            List<Book> books = booksService.getAllBooks();
            displayBooksForAdmin(primaryStage, books);
        });
        reviewsButton.setOnAction(actionEvent -> {
            List<Review> reviews = reviewService.findAll();
            displayReviews(primaryStage, reviews, null);
        });
        orderButton.setOnAction(actionEvent -> {
            List<Order> orders = orderService.findAllOrders();
            displayOrders(primaryStage, orders);
        });
        vBox.getChildren().addAll(orderLabel, orderButton, booksLabel, booksButton, reviewsLabel, reviewsButton);
        primaryStage.setScene(new Scene(vBox, 800, 800));
        primaryStage.show();
    }

    private void displayOrders(Stage primaryStage, List<Order> orders){
        Stage stage = new Stage();
        stage.setTitle("Orders");
        stage.initModality(Modality.APPLICATION_MODAL);
        GridPane gp = new GridPane();
        Button backButton = new Button("Back");
        backButton.setOnAction(actionEvent -> {
            if (myRole==Role.USER){
                userClient(primaryStage);
            }else if (myRole==Role.EMPLOYEE){
                employeeClient(primaryStage);
            }
        });
        gp.add(backButton, 0, 0);
        int i = 1;
        for (Order order : orders){
            if(myRole==Role.USER) {
                if(order.getUser().getId().equals(myUser.getId())) {
                    displaySingleOrder(order, gp, i, primaryStage);
                }
            } else {
                displaySingleOrder(order, gp, i, primaryStage);
            }
            i++;
        }
        gp.setHgap(10);
        gp.setVgap(10);
        primaryStage.setScene(new Scene(gp, 800, 800));
        primaryStage.show();
    }

    private void displaySingleOrder(Order order, GridPane gp, int i, Stage primaryStage){
        VBox user = new VBox();
        Label userLabel = new Label("user:");
        Label userLabel1 = new Label(order.getUser().getUsername());
        user.getChildren().addAll(userLabel,userLabel1);
        gp.add(user, 0, i);
        VBox status = new VBox();
        Label statusLabel = new Label("Status:");
        Label statusLabel1 = new Label(order.getStatus().toString());
        status.getChildren().addAll(statusLabel,statusLabel1);
        gp.add(status, 1, i);
        List<Order_items> orderItems = order_itemsService.findByOrderId(order.getId());
        VBox items = new VBox();
        Label itemsLabel = new Label("Items:");
        items.getChildren().add(itemsLabel);
        for (Order_items orderItem : orderItems){
            HBox item = new HBox();
            Label itemLabel = new Label(orderItem.getBook().getTitle()+": ");
            Label priceLabel = new Label(orderItem.getPrice()+" $");
            item.getChildren().addAll(itemLabel,priceLabel);
            items.getChildren().add(item);
        }
        gp.add(items, 2, i);
        VBox price = new VBox();
        Label priceSum = new Label("Total price:");
        Label priceSum1 = new Label(order.getTotalPrice() + " $");
        price.getChildren().addAll(priceSum, priceSum1);
        gp.add(price, 3, i);
        if (myRole==Role.EMPLOYEE){
            Button changeStatusButton = new Button("Change Status");
            changeStatusButton.setOnAction(actionEvent -> {
                Stage stage = new Stage();
                stage.setTitle("Change Status");
                stage.initModality(Modality.APPLICATION_MODAL);
                VBox vBox = new VBox();
                Label pickStatusLabel = new Label("Pick Status:");
                ComboBox<OrderStatus> pickStatus = new ComboBox<>();
                pickStatus.getItems().addAll(OrderStatus.PROCESSING, OrderStatus.COMPLETED, OrderStatus.CANCELLED);
                pickStatus.setValue(OrderStatus.PROCESSING);
                Button confirmButton = new Button("Confirm");
                confirmButton.setOnAction(actionEvent1 -> {
                    order.setStatus(pickStatus.getValue());
                    orderService.updateOrder(order);
                    displayOrders(primaryStage, orderService.findAllOrders());
                    stage.close();
                });
                vBox.getChildren().addAll(pickStatusLabel,pickStatus, confirmButton);
                stage.setScene(new Scene(vBox, 400, 400));
                stage.show();
            });
            gp.add(changeStatusButton, 4, i);
        }
    }

    //Client pracownika

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
                switch(myUser.getRole()) {
                    case USER:
                        myRole = Role.USER;
                        userService.loginAsUser();
                        userClient(primaryStage);
                        break;
                    case ADMIN:
                        myRole = Role.ADMIN;
                        userService.loginAsAdmin();
                        adminClient(primaryStage);
                        break;
                    case EMPLOYEE:
                        myRole = Role.EMPLOYEE;
                        userService.loginAsEmployee();
                        employeeClient(primaryStage);
                        break;
                }
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
        root.getChildren().addAll(emailArea, email,usernameArea, username,passwordArea, password,confirmPasswordArea, confirmPassword);
        if(myRole == Role.ADMIN){
            Label roleArea = new Label("role:");
            ComboBox<Role> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().addAll(Role.EMPLOYEE, Role.ADMIN, Role.USER);
            roleComboBox.setValue(Role.EMPLOYEE);
            root.getChildren().addAll(roleArea, roleComboBox);
            register.setOnAction(actionEvent -> {
                registerHandler(username, password, email, confirmPassword, primaryStage, roleComboBox.getValue());
            });
        }else{
            register.setOnAction(actionEvent -> {
                registerHandler(username, password, email, confirmPassword, primaryStage, Role.USER);
            });
        }
        root.getChildren().add(register);
        Scene scene = new Scene(root,300,300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void registerHandler(TextField username, PasswordField password, TextField email, PasswordField confirmPassword, Stage primaryStage, Role role){
        if(password.getText().equals(confirmPassword.getText())) {
            User newUser = new User();
            newUser.setUsername(username.getText());
            newUser.setPassword(password.getText());
            newUser.setRole(role);
            newUser.setEmail(email.getText());
            String response = userService.registerUser(newUser);
            if(response.equals("account registered successfully")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText(response);
                alert.showAndWait();
                if(myRole == Role.USER) {
                    loginStage(primaryStage);
                }else{
                    primaryStage.close();
                }
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
        categoryService = springContext.getBean(CategoryService.class);
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
