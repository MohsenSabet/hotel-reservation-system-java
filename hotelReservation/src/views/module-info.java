module hotelReservation {
	
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires java.sql;
	opens models to javafx.base;

    opens application to javafx.graphics, javafx.fxml;
    opens controllers to javafx.fxml;
    opens views to javafx.fxml;
}
