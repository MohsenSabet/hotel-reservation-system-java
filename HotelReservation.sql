USE hotelreservation;

CREATE TABLE Login (
    loginId INT PRIMARY KEY,
    loginPassword VARCHAR(255)
);

CREATE TABLE Guest (
    guestId INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50),
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    address VARCHAR(255),
    phone BIGINT,
    email VARCHAR(255)
);

CREATE TABLE Room (
    roomId INT AUTO_INCREMENT PRIMARY KEY,
    roomType VARCHAR(50),
    rate DECIMAL(10, 2),
    capacity INT,
    isAvailable BOOLEAN
);

CREATE TABLE Bill (
   billId INT AUTO_INCREMENT PRIMARY KEY,
    amountToPay DECIMAL(10,2),
    discountRate DECIMAL(5,2),
    taxRate DECIMAL(5,2),
    isPaid TINYINT(1),
    checkoutTime TIMESTAMP
);

CREATE TABLE Reservation (
    bookId INT AUTO_INCREMENT PRIMARY KEY,
    bookDate DATE,
    checkIn DATE,
    checkOut DATE,
    guestId INT,
    roomId INT,
    billId INT,
	numAdults INT,
    numChildren INT,
    source ENUM('KIOSK', 'ADMIN'),
    FOREIGN KEY (guestId) REFERENCES Guest(guestId),
    FOREIGN KEY (roomId) REFERENCES Room(roomId),
    FOREIGN KEY (billId) REFERENCES Bill(billId)
);

CREATE TABLE ReservationRoom (
    reservationId INT,
    roomId INT,
    PRIMARY KEY (reservationId, roomId),
    FOREIGN KEY (reservationId) REFERENCES Reservation(bookId),
    FOREIGN KEY (roomId) REFERENCES Room(roomId)
);


INSERT INTO Login (loginId, loginPassword) VALUES (1000, '1111'), (2000, '2222'), (3000, '3333');




-- Insert  single rooms
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Single', 100.00, 2, TRUE);


-- Insert  double rooms
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Double', 150.00, 4, TRUE);



-- Insert  deluxe rooms
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Deluxe', 200.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Deluxe', 200.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Deluxe', 200.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Deluxe', 200.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Deluxe', 200.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Deluxe', 200.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Deluxe', 200.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Deluxe', 200.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Deluxe', 200.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('Deluxe', 200.00, 2, TRUE);
-- ... Repeat until you have 10 entries

-- Insert penthouses
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('PentHouse', 300.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('PentHouse', 300.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('PentHouse', 300.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('PentHouse', 300.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('PentHouse', 300.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('PentHouse', 300.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('PentHouse', 300.00, 2, TRUE);
INSERT INTO Room (roomType, rate, capacity, isAvailable) VALUES ('PentHouse', 300.00, 2, TRUE);


