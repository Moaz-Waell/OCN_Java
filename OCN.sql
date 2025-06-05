-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 05, 2025 at 08:43 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `adv_ocn`
--

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `ADMIN_ID` int(11) NOT NULL,
  `ADMIN_Name` varchar(255) DEFAULT NULL,
  `ADMIN_Pin` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`ADMIN_ID`, `ADMIN_Name`, `ADMIN_Pin`) VALUES
(2122, 'Alaa', 998843);

-- --------------------------------------------------------

--
-- Table structure for table `allergy`
--

CREATE TABLE `allergy` (
  `ALLERGY_ID` int(11) NOT NULL,
  `ALLERGY_Name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `allergy`
--

INSERT INTO `allergy` (`ALLERGY_ID`, `ALLERGY_Name`) VALUES
(1, 'mozzarella'),
(2, 'cheese'),
(3, 'milk'),
(4, 'egg'),
(5, 'shrimp'),
(6, 'salmon'),
(7, 'mushroom'),
(8, 'raspberry'),
(9, 'walnuts'),
(10, 'date'),
(11, 'no allergies');

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `CART_ID` int(11) NOT NULL,
  `USERS_ID` int(11) DEFAULT NULL,
  `MEAL_ID` int(11) DEFAULT NULL,
  `NOTE` varchar(500) DEFAULT NULL,
  `QUANTITY` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart`
--

INSERT INTO `cart` (`CART_ID`, `USERS_ID`, `MEAL_ID`, `NOTE`, `QUANTITY`) VALUES
(206, 231005756, 5, 'Exclude: mozzarella', 1),
(207, 231005756, 40, 'Exclude: egg', 1),
(208, 231005756, 37, '', 2);

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `CATEGORY_ID` int(11) NOT NULL,
  `CATEGORY_Name` varchar(255) DEFAULT NULL,
  `CATEGORY_Icon` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`CATEGORY_ID`, `CATEGORY_Name`, `CATEGORY_Icon`) VALUES
(1, 'Pizza', 'pizza.png'),
(2, 'Sushi', 'sushi.png'),
(3, 'Pasta', 'spaghetti.png'),
(4, 'Salad', 'salad.png'),
(5, 'Desserts', 'desserts.png'),
(6, 'Breakfasts', 'breakfast.png'),
(7, 'Crepes', 'crepe.png');

-- --------------------------------------------------------

--
-- Table structure for table `ingredients`
--

CREATE TABLE `ingredients` (
  `INGREDIENT_ID` int(11) NOT NULL,
  `INGREDIENT_NAME` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ingredients`
--

INSERT INTO `ingredients` (`INGREDIENT_ID`, `INGREDIENT_NAME`) VALUES
(1, 'basil'),
(2, 'mozzarella'),
(3, 'tomato sauce'),
(4, 'pepperoni'),
(5, 'bell pepper'),
(6, 'mushroom'),
(7, 'olives'),
(8, 'onion'),
(9, 'ham'),
(10, 'pineapple'),
(11, 'bbq sauce'),
(12, 'chicken'),
(13, 'red onion'),
(14, 'avocado'),
(15, 'cucumber'),
(16, 'nori'),
(17, 'shrimp'),
(18, 'sushi rice'),
(19, 'salmon'),
(20, 'tuna'),
(21, 'beef mince'),
(22, 'garlic'),
(23, 'parmesan'),
(24, 'spaghetti'),
(25, 'alfredo sauce'),
(26, 'penner'),
(27, 'chili flakes'),
(28, 'carbonara sauce'),
(29, 'egg yolk'),
(30, 'lasagna sheets'),
(31, 'ricotta'),
(32, 'bacon'),
(33, 'croutons'),
(34, 'lettuce'),
(35, 'caesar dressing'),
(36, 'feta cheese'),
(37, 'tomato'),
(38, 'brownie mix'),
(39, 'chocolate chips'),
(40, 'cream cheese'),
(41, 'mixed greens'),
(42, 'quinoa'),
(43, 'cheesecake crust'),
(44, 'fruit'),
(45, 'mascarpone'),
(46, 'tiramisu layer'),
(47, 'crème brûlée mix'),
(48, 'cola'),
(49, 'lemon juice'),
(50, 'milk'),
(51, 'tea leaves'),
(52, 'apricot'),
(53, 'peach'),
(54, 'plum'),
(55, 'raspberry'),
(56, 'date'),
(57, 'fig'),
(58, 'persimmon'),
(59, 'pomegranate'),
(60, 'clementine'),
(61, 'dragon fruit'),
(62, 'grapefruit'),
(63, 'kiwi'),
(64, 'melon'),
(65, 'tangerine'),
(66, 'elderberry'),
(67, 'gooseberry'),
(68, 'loganberry'),
(69, 'starfruit'),
(70, 'ugli fruit'),
(71, 'brie'),
(72, 'cheddar'),
(73, 'gouda'),
(74, 'camembert'),
(75, 'gruyère'),
(76, 'manchego'),
(77, 'cottage cheese'),
(78, 'gorgonzola'),
(79, 'roquefort'),
(80, 'smoked salmon'),
(81, 'capers'),
(82, 'dill'),
(83, 'pine nuts'),
(84, 'pasta'),
(85, 'grilled chicken'),
(86, 'tomatoes'),
(87, 'cucumbers'),
(88, 'bananas'),
(89, 'flour'),
(90, 'sugar'),
(91, 'eggs'),
(92, 'walnuts'),
(93, 'chocolate'),
(94, 'butter'),
(95, 'cocoa powder'),
(96, 'cinnamon'),
(97, 'berries'),
(98, 'vanilla'),
(99, 'english muffin'),
(100, 'egg'),
(101, 'cheese'),
(102, 'bread'),
(103, 'mushrooms'),
(104, 'crepe'),
(105, 'cream');

-- --------------------------------------------------------

--
-- Table structure for table `meal`
--

CREATE TABLE `meal` (
  `MEAL_ID` int(11) NOT NULL,
  `MEAL_Name` varchar(255) DEFAULT NULL,
  `MEAL_Description` varchar(500) DEFAULT NULL,
  `MEAL_Price` decimal(10,2) DEFAULT NULL,
  `MEAL_Icon` varchar(255) DEFAULT NULL,
  `CATEGORY_ID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `meal`
--

INSERT INTO `meal` (`MEAL_ID`, `MEAL_Name`, `MEAL_Description`, `MEAL_Price`, `MEAL_Icon`, `CATEGORY_ID`) VALUES
(1, 'Margherita', 'Basil, Mozzarella, Tomato Sauce', 12.99, 'margherita.jpg', 1),
(2, 'Pepperoni', 'Mozzarella, Pepperoni, Tomato Sauce', 14.99, 'pepperoni.jpg', 1),
(3, 'Veggie Supreme', 'Bell Pepper, Mozzarella, Mushroom, Olives, Onion, Tomato Sauce', 13.99, 'veggie_supreme.jpg', 1),
(4, 'Hawaiian', 'Ham, Mozzarella, Pineapple, Tomato Sauce', 13.50, 'hawaiian.jpg', 1),
(5, 'BBQ Chicken', 'BBQ Sauce, Chicken, Mozzarella, Red Onion', 14.00, 'bbq_chicken.jpg', 1),
(6, 'California Roll', 'Avocado, Cucumber, Nori, Shrimp, Sushi Rice', 9.99, 'california_roll.jpg', 2),
(7, 'Salmon Nigiri', 'Nori, Salmon, Sushi Rice', 12.00, 'salmon_nigiri.jpg', 2),
(8, 'Tuna Roll', 'Nori, Sushi Rice, Tuna', 8.99, 'tuna_roll.jpg', 2),
(9, 'Shrimp Tempura Roll', 'Nori, Shrimp, Sushi Rice', 11.50, 'shrimp_tempura.jpg', 2),
(10, 'Sushi Platter', 'Avocado, Cucumber, Nori, Salmon, Shrimp, Sushi Rice, Tuna', 18.99, 'sushi_platter.jpg', 2),
(11, 'Spaghetti Bolognese', 'Beef Mince, Garlic, Parmesan, Spaghetti, Tomato Sauce', 11.99, 'spaghetti_bolognese.jpg', 3),
(12, 'Fettuccine Alfredo', 'Alfredo Sauce, Mozzarella, Penner', 13.50, 'alfredo.jpg', 3),
(13, 'Penne Arrabbiata', 'Chili Flakes, Onion, Parmesan, Tomato Sauce', 12.00, 'penne_arrabbiata.jpg', 3),
(14, 'Lasagna', 'Carbonara Sauce, Egg Yolk, Lasagna Sheets, Mozzarella, Ricotta', 14.00, 'lasagna.jpg', 3),
(15, 'Carbonara', 'Alfredo Sauce, Bacon, Croutons, Lettuce', 13.00, 'carbonara.jpg', 3),
(16, 'Caesar Salad', 'Caesar Dressing, Feta Cheese, Tomato', 8.50, 'caesar_salad.jpg', 4),
(17, 'Greek Salad', 'Brownie Mix, Chocolate Chips, Cream Cheese, Mixed Greens, Quinoa', 9.00, 'greek_salad.jpg', 4),
(18, 'Cobb Salad', 'Caesar Dressing, Cheesecake Crust, Fruit, Mascarpone, Tiramisu Layer', 9.50, 'cobb_salad.jpg', 4),
(19, 'Caprese Salad', 'Caesar Dressing, Cream Cheese, Crème Brûlée Mix, Mozzarella', 8.99, 'caprese.jpg', 4),
(20, 'Quinoa Salad', 'Cola, Lemon Juice, Milk, Tea Leaves', 9.50, 'quinoa_salad.jpg', 4),
(21, 'Pancakes', 'Apricot, Peach, Plum, Raspberry', 7.99, 'pancakes.jpg', 6),
(22, 'English Breakfast', 'Date, Fig, Persimmon, Pomegranate', 10.99, 'english_breakfast.jpg', 6),
(23, 'Avocado Toast', 'Clementine, Dragon Fruit, Grapefruit', 8.50, 'avocado_toast.jpg', 6),
(24, 'Omelette', 'Kiwi, Melon, Tangerine', 9.00, 'omelette.jpg', 6),
(25, 'Nutella Crepe', 'Elderberry, Gooseberry, Loganberry', 6.50, 'nutella_crepes.jpg', 7),
(26, 'Ham and Cheese Crepe', 'Elderberry, Starfruit, Ugli Fruit', 7.00, 'ham_cheese_crepes.jpg', 7),
(27, 'Fruit Crepe', 'Brie, Cheddar, Elderberry, Gouda', 6.99, 'fruit_crepes.jpg', 7),
(28, 'Chicken Crepe', 'Camembert, Elderberry, Gruyère, Manchego', 7.99, 'chicken_crepes.jpg', 7),
(29, 'Spinach and Feta Crepe', 'Cottage Cheese, Elderberry, Gorgonzola, Roquefort', 7.50, 'spinach_feta_crepes.jpg', 7),
(30, 'Salmon Pizza', 'Smoked Salmon, Cream Cheese, Red Onion, Capers, Dill', 12.99, 'salamonpizza.jpg', 1),
(31, 'Philadelphia', 'Smoked Salmon, Cream Cheese, Avocado, Cucumber', 12.99, 'philadelphia.jpg', 2),
(32, 'Pesto', 'Basil, Pine Nuts, Parmesan, Garlic, Pasta', 12.99, 'pesto.jpg', 3),
(33, 'Chicken Salad', 'Grilled Chicken, Lettuce, Tomatoes, Cucumbers, Caesar Dressing', 12.99, 'chickensalad.jpg', 4),
(34, 'Banana Cake', 'Bananas, Flour, Sugar, Eggs, Walnuts', 12.99, 'bananacake.jpg', 5),
(35, 'Brownies', 'Chocolate, Flour, Sugar, Butter, Eggs', 12.99, 'brownies.jpg', 5),
(36, 'Chocolate Cake', 'Chocolate, Flour, Sugar, Eggs, Cocoa Powder', 12.99, 'chocolatecake.jpg', 5),
(37, 'Cinnabonberry', 'Cinnamon, Sugar, Flour, Butter, Berries', 12.99, 'cinnabonberry.jpg', 5),
(38, 'Cookies', 'Chocolate Chips, Flour, Sugar, Butter, Vanilla', 12.99, 'cookies.jpg', 5),
(39, 'McMuffin', 'English Muffin, Egg, Cheese, Ham', 12.99, 'mcmuffin.jpg', 6),
(40, 'Egg Sandwich', 'Egg, Bread, Cheese, Lettuce, Tomato', 12.99, 'egg_sandwich.jpg', 6),
(41, 'Mushroom Crepe', 'Mushrooms, Crepe, Cheese, Garlic, Cream', 12.99, 'mushroomcrep.jpg', 7);

-- --------------------------------------------------------

--
-- Table structure for table `meal_ingredients`
--

CREATE TABLE `meal_ingredients` (
  `INGREDIENT_ID` int(11) NOT NULL,
  `MEAL_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `meal_ingredients`
--

INSERT INTO `meal_ingredients` (`INGREDIENT_ID`, `MEAL_ID`) VALUES
(1, 1),
(1, 32),
(2, 1),
(2, 2),
(2, 3),
(2, 4),
(2, 5),
(2, 12),
(2, 14),
(2, 19),
(3, 1),
(3, 2),
(3, 3),
(3, 4),
(3, 11),
(3, 13),
(4, 2),
(5, 3),
(6, 3),
(7, 3),
(8, 3),
(8, 13),
(9, 4),
(9, 39),
(10, 4),
(11, 5),
(12, 5),
(13, 5),
(13, 30),
(14, 6),
(14, 10),
(14, 31),
(15, 6),
(15, 10),
(15, 31),
(16, 6),
(16, 7),
(16, 8),
(16, 9),
(16, 10),
(17, 6),
(17, 9),
(17, 10),
(18, 6),
(18, 7),
(18, 8),
(18, 9),
(18, 10),
(19, 7),
(19, 10),
(20, 8),
(20, 10),
(21, 11),
(22, 11),
(22, 32),
(22, 41),
(23, 11),
(23, 13),
(23, 32),
(24, 11),
(25, 12),
(25, 15),
(26, 12),
(27, 13),
(28, 14),
(29, 14),
(30, 14),
(31, 14),
(32, 15),
(33, 15),
(34, 15),
(34, 33),
(34, 40),
(35, 16),
(35, 18),
(35, 19),
(35, 33),
(36, 16),
(37, 16),
(37, 40),
(38, 17),
(39, 17),
(39, 38),
(40, 17),
(40, 19),
(40, 30),
(40, 31),
(41, 17),
(42, 17),
(43, 18),
(44, 18),
(45, 18),
(46, 18),
(47, 19),
(48, 20),
(49, 20),
(50, 20),
(51, 20),
(52, 21),
(53, 21),
(54, 21),
(55, 21),
(56, 22),
(57, 22),
(58, 22),
(59, 22),
(60, 23),
(61, 23),
(62, 23),
(63, 24),
(64, 24),
(65, 24),
(66, 25),
(66, 26),
(66, 27),
(66, 28),
(66, 29),
(67, 25),
(68, 25),
(69, 26),
(70, 26),
(71, 27),
(72, 27),
(73, 27),
(74, 28),
(75, 28),
(76, 28),
(77, 29),
(78, 29),
(79, 29),
(80, 30),
(80, 31),
(81, 30),
(82, 30),
(83, 32),
(84, 32),
(85, 33),
(86, 33),
(87, 33),
(88, 34),
(89, 34),
(89, 35),
(89, 36),
(89, 37),
(89, 38),
(90, 34),
(90, 35),
(90, 36),
(90, 37),
(90, 38),
(91, 34),
(91, 35),
(91, 36),
(92, 34),
(93, 35),
(93, 36),
(94, 35),
(94, 37),
(94, 38),
(95, 36),
(96, 37),
(97, 37),
(98, 38),
(99, 39),
(100, 39),
(100, 40),
(101, 39),
(101, 40),
(101, 41),
(102, 40),
(103, 41),
(104, 41),
(105, 41);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `ORDER_ID` int(11) NOT NULL,
  `ORDER_Status` varchar(50) DEFAULT NULL,
  `ORDER_ScheduleDate` varchar(10) DEFAULT NULL,
  `ORDER_ScheduleTime` varchar(10) DEFAULT NULL,
  `ORDER_Amount` decimal(10,2) DEFAULT NULL,
  `ORDER_PaymentType` varchar(25) NOT NULL,
  `USERS_ID` int(11) DEFAULT NULL,
  `ORDER_Feedback` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`ORDER_ID`, `ORDER_Status`, `ORDER_ScheduleDate`, `ORDER_ScheduleTime`, `ORDER_Amount`, `ORDER_PaymentType`, `USERS_ID`, `ORDER_Feedback`) VALUES
(15, 'Delivered', '2025-05-08', '05:53:53.0', 27.28, 'Cash', 231005898, 5),
(17, 'Delivered', '2025-05-08', '07:36:28.0', 9.09, 'Cash', 231005898, 5),
(18, 'Delivered', '2025-05-08', '07:37:54.0', 12.99, 'Cash', 231005898, 2),
(19, 'Delivered', '2025-05-08', '07:38:57.0', 38.97, 'Cash', 231005898, 3),
(20, 'Delivered', '2025-05-08', '07:39:33.0', 40.97, 'Cash', 231005898, 5),
(21, 'Cancelled', '2025-05-08', '08:28:53.0', 12.99, 'Cash', 231005898, NULL),
(22, 'Cancelled', '2025-05-08', '08:30:09.0', 40.97, 'Cash', 231005898, NULL),
(23, 'Cancelled', '2025-05-08', '09:14:04.0', 45.12, 'Card', 231005756, NULL),
(24, 'Delivered', '2025-05-08', '09:15:22.0', 44.97, 'Cash', 231005756, 5),
(25, 'Delivered', '2025-05-08', '09:15:56.0', 64.47, 'Cash', 231005756, 2),
(26, 'Cancelled', '2025-05-08', '09:19:35.0', 64.47, 'Cash', 231005756, NULL),
(27, 'Cancelled', '2025-05-08', '09:22:34.0', 64.45, 'Cash', 231005756, NULL),
(33, 'Cancelled', '2025-05-08', '17:54:19.0', 12.99, 'Cash', 231014670, NULL),
(34, 'Cancelled', '2025-05-08', '17:54:50.0', 66.97, 'Cash', 231014670, NULL),
(37, 'Delivered', '2025-05-08', '17:56:24.0', 13.50, 'Cash', 231014670, 5),
(39, 'Delivered', '2025-05-08', '18:04:42.0', 72.95, 'Cash', 231014670, 2),
(40, 'Cancelled', '2025-05-08', '18:06:17.0', 12.99, 'Cash', 231014670, NULL),
(43, 'Delivered', '2025-05-08', '19:14:30.0', 18.19, 'Cash', 231014670, NULL),
(44, 'Delivered', '2025-05-08', '19:18:54.0', 79.02, 'Cash', 231014670, NULL),
(45, 'Cancelled', '2025-05-10', '06:23:03.0', 64.95, 'Cash', 231014670, NULL),
(46, 'Delivered', '2025-05-10', '06:27:33.0', 45.47, 'Cash', 231014670, NULL),
(47, 'Cancelled', '2025-05-10', '08:36:49.0', 45.47, 'Cash', 231014670, NULL),
(48, 'Cancelled', '2025-05-10', '08:37:17.0', 64.95, 'Cash', 231014670, NULL),
(49, 'Delivered', '2025-05-10', '08:37:53.0', 64.95, 'Cash', 231014670, 5),
(50, 'Delivered', '2025-05-10', '08:39:06.0', 64.95, 'Cash', 231014670, NULL),
(51, 'Delivered', '2025-05-10', '13:20:00.0', 254.60, 'Cash', 231014670, NULL),
(52, 'Delivered', '2025-05-10', '12:38:12.0', 368.00, 'Cash', 231014670, NULL),
(53, 'Delivered', '2025-05-10', '12:39:36.0', 662.49, 'Cash', 231014670, NULL),
(54, 'Delivered', '2025-05-10', '12:41:53.0', 212.50, 'Cash', 231014670, NULL),
(55, 'Cancelled', '2025-05-10', '13:48:31.0', 13.50, 'Cash', 231014670, NULL),
(56, 'Cancelled', '2025-05-10', '16:21:22.0', 12.99, 'Cash', 231014670, NULL),
(57, 'Cancelled', '2025-05-10', '16:22:19.0', 294.97, 'Cash', 231014670, NULL),
(58, 'Cancelled', '2025-05-10', '16:37:14.0', 36.00, 'Cash', 231014670, NULL),
(59, 'Delivered', '2025-05-10', '17:51:45.0', 212.50, 'Cash', 231014670, NULL),
(60, 'Cancelled', '2025-05-10', '17:57:16.0', 363.72, 'Cash', 231014670, NULL),
(61, 'Cancelled', '2025-05-10', '18:07:16.0', 64.95, 'Cash', 231014670, NULL),
(62, 'Cancelled', '2025-05-10', '18:08:00.0', 64.95, 'Cash', 231014670, NULL),
(63, 'Cancelled', '2025-05-10', '18:08:05.0', 363.72, 'Cash', 231014670, NULL),
(64, 'Delivered', '2025-05-10', '18:12:25.0', 51.96, 'Cash', 231014670, 5),
(65, 'Delivered', '2025-05-22', '21:53:36.0', 97.98, 'Cash', 231005898, NULL),
(66, 'Delivered', '2025-05-23', '09:28:50.0', 9.99, 'Cash', 231005898, 4),
(67, 'Cancelled', '2025-05-23', '16:46:00.0', 6.99, 'Cash', 231005898, NULL),
(68, 'Cancelled', '2025-05-23', '17:21:00.0', 34.42, 'Cash', 231005898, NULL),
(69, 'Cancelled', '2025-05-23', '17:54:00.0', 8.49, 'Cash', 231005898, NULL),
(70, 'Cancelled', '2025-05-23', '17:59:00.0', 8.49, 'Cash', 231005898, NULL),
(71, 'Cancelled', '2025-05-24', '13:51:00.0', 50.37, 'Cash', 231005756, NULL),
(72, 'Cancelled', '2025-05-24', '14:20:00.0', 71.95, 'Cash', 231005756, NULL),
(73, 'Cancelled', '2025-05-24', '14:22:00.0', 71.95, 'Cash', 231005756, NULL),
(74, 'Cancelled', '2025-05-24', '14:36:00.0', 71.95, 'Cash', 231005756, NULL),
(75, 'Cancelled', '2025-05-24', '07:18 PM', 71.95, 'Cash', 231005756, NULL),
(76, 'Cancelled', '2025-05-24', '07:49 PM', 25.98, 'Cash', 231005756, NULL),
(77, 'Cancelled', '2025-05-24', '08:14 PM', 25.98, 'Cash', 231005756, NULL),
(78, 'Cancelled', '2025-05-24', '08:33 PM', 11.50, 'Cash', 231005756, NULL),
(79, 'Delivered', '2025-05-25', '12:18 AM', 11.50, 'Cash', 231005756, NULL),
(80, 'Cancelled', '2025-05-25', '01:10 AM', 14.00, 'Cash', 231005756, NULL),
(81, 'Cancelled', '2025-05-25', '02:07 AM', 62.98, 'Cash', 231005756, NULL),
(82, 'Cancelled', '2025-05-25', '01:20 AM', 71.95, 'Cash', 231005756, NULL),
(83, 'Delivered', '2025-05-25', '01:13 AM', 12.99, 'Cash', 231005756, NULL),
(84, 'Cancelled', '2025-05-26', '01:15 AM', 8.50, 'Cash', 231005756, NULL),
(85, 'Cancelled', '2025-05-25', '01:34 AM', 9.99, 'Cash', 231005898, NULL),
(86, 'Cancelled', '2025-05-25', '01:52 AM', 11.50, 'Cash', 231005756, NULL),
(87, 'Cancelled', '2025-05-25', '02:09 AM', 14.00, 'Cash', 231005999, NULL),
(88, 'Cancelled', '2025-05-25', '10:48 AM', 9.80, 'Cash', 231005756, NULL),
(89, 'Cancelled', '2025-05-26', '10:40 AM', 18.89, 'Cash', 231005756, NULL),
(90, 'Delivered', '2025-05-26', '10:44 AM', 12.99, 'Cash', 231005756, 3),
(91, 'Delivered', '2025-05-29', '11:36 PM', 83.30, 'Cash', 231005898, 5),
(92, 'Delivered', '2025-06-01', '12:06 PM', 56.00, 'Cash', 231005898, 3),
(93, 'Cancelled', '2025-06-01', '12:13 PM', 45.02, 'Cash', 231005898, NULL),
(94, 'Cancelled', '2025-06-05', '12:47 AM', 56.00, 'Cash', 231005898, NULL),
(95, 'Delivered', '2025-06-05', '12:55 AM', 61.97, 'Cash', 231005898, 4);

-- --------------------------------------------------------

--
-- Table structure for table `order_details`
--

CREATE TABLE `order_details` (
  `MEAL_ID` int(11) NOT NULL,
  `ORDER_ID` int(11) NOT NULL,
  `M_Quantity` int(11) DEFAULT NULL,
  `NOTE` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_details`
--

INSERT INTO `order_details` (`MEAL_ID`, `ORDER_ID`, `M_Quantity`, `NOTE`) VALUES
(1, 45, 3, ''),
(1, 46, 3, ''),
(1, 47, 3, ''),
(1, 48, 3, ''),
(1, 49, 3, ''),
(1, 50, 3, ''),
(1, 51, 28, ''),
(1, 56, 1, ''),
(1, 60, 28, ''),
(1, 61, 3, ''),
(1, 62, 3, ''),
(1, 63, 28, ''),
(1, 64, 2, ''),
(1, 64, 2, 'Exclude: Mozzarella'),
(1, 68, 1, ''),
(1, 83, 1, ''),
(1, 90, 1, ''),
(2, 57, 1, ''),
(2, 95, 1, 'Exclude: tomato sauce'),
(3, 57, 1, ''),
(3, 65, 1, ''),
(3, 65, 1, 'Exclude: mushroom'),
(4, 55, 1, ''),
(4, 57, 3, ''),
(4, 68, 1, ''),
(5, 65, 5, ''),
(5, 68, 1, ''),
(5, 80, 1, ''),
(5, 87, 1, ''),
(5, 88, 1, 'Exclude: mozzarella'),
(5, 89, 1, 'Exclude: mozzarella'),
(5, 91, 7, ''),
(5, 92, 2, ''),
(5, 92, 2, 'Exclude: mozzarella'),
(5, 93, 1, 'Exclude: mozzarella'),
(5, 94, 3, ''),
(5, 94, 1, 'Exclude: mozzarella'),
(6, 66, 1, ''),
(6, 67, 1, ''),
(6, 69, 1, ''),
(6, 70, 1, ''),
(6, 85, 1, ''),
(9, 52, 32, ''),
(9, 78, 1, ''),
(9, 79, 1, ''),
(9, 81, 1, ''),
(9, 86, 1, ''),
(11, 81, 1, ''),
(12, 81, 1, ''),
(13, 58, 1, ''),
(13, 58, 1, 'Exclude: Onion'),
(13, 58, 1, 'Exclude: Tomato Sauce'),
(15, 81, 1, ''),
(15, 95, 2, 'Exclude: alfredo sauce'),
(16, 27, 1, ''),
(16, 44, 1, ''),
(16, 71, 2, ''),
(16, 72, 2, ''),
(16, 73, 2, ''),
(16, 74, 2, ''),
(16, 75, 2, ''),
(16, 82, 2, ''),
(17, 44, 1, ''),
(17, 44, 2, 'Exclude: Chocolate Chips, Quinoa'),
(19, 44, 1, 'Exclude: Cream Cheese'),
(20, 44, 1, 'Exclude: Lemon Juice'),
(21, 23, 1, 'Exclude: Raspberry'),
(21, 27, 1, 'Exclude: Raspberry'),
(21, 71, 2, 'Exclude: Raspberry'),
(21, 72, 2, 'Exclude: Raspberry'),
(21, 73, 2, 'Exclude: Raspberry'),
(21, 74, 2, 'Exclude: Raspberry'),
(21, 75, 2, 'Exclude: Raspberry'),
(21, 82, 2, 'Exclude: Raspberry'),
(21, 95, 1, ''),
(23, 54, 25, ''),
(23, 57, 25, ''),
(23, 59, 25, ''),
(23, 84, 1, ''),
(30, 76, 2, ''),
(30, 77, 2, ''),
(31, 23, 2, ''),
(31, 27, 2, ''),
(31, 71, 2, ''),
(31, 72, 2, ''),
(31, 73, 2, ''),
(31, 74, 2, ''),
(31, 75, 2, ''),
(31, 82, 2, ''),
(32, 43, 1, ''),
(32, 43, 1, 'Exclude: Parmesan'),
(32, 45, 2, ''),
(32, 46, 2, ''),
(32, 47, 2, ''),
(32, 48, 2, ''),
(32, 49, 2, ''),
(32, 50, 2, ''),
(32, 53, 26, ''),
(32, 61, 2, ''),
(32, 62, 2, ''),
(32, 81, 1, ''),
(33, 44, 1, 'Exclude: Cucumbers'),
(33, 44, 2, 'Exclude: Lettuce'),
(35, 53, 25, ''),
(35, 57, 1, 'Exclude: Eggs'),
(39, 23, 1, 'Exclude: Cheese'),
(39, 27, 1, 'Exclude: Cheese'),
(39, 71, 1, 'Exclude: Cheese'),
(39, 72, 1, 'Exclude: Cheese'),
(39, 73, 1, 'Exclude: Cheese'),
(39, 74, 1, 'Exclude: Cheese'),
(39, 75, 1, 'Exclude: Cheese'),
(39, 82, 1, 'Exclude: Cheese'),
(40, 89, 1, ''),
(40, 93, 3, 'Exclude: egg'),
(40, 95, 1, 'Exclude: cheese, egg');

-- --------------------------------------------------------

--
-- Table structure for table `uni_users`
--

CREATE TABLE `uni_users` (
  `USERS_ID` int(11) NOT NULL,
  `USERS_Phnumber` varchar(15) DEFAULT NULL,
  `USERS_Name` varchar(255) DEFAULT NULL,
  `USERS_Pincode` int(11) DEFAULT NULL,
  `USERS_Attendance` int(11) DEFAULT NULL,
  `USERS_Email` varchar(80) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `uni_users`
--

INSERT INTO `uni_users` (`USERS_ID`, `USERS_Phnumber`, `USERS_Name`, `USERS_Pincode`, `USERS_Attendance`, `USERS_Email`) VALUES
(231005756, '01020400868', 'Shahd Osama', 2005, 100, 'shahdosama197@gmail.com'),
(231005898, '01019972179', 'Moaz Wael', 998843, 80, 'm.sallam05898@student.aast.edu'),
(231005999, '01019972179', 'Ushii', 2005, 100, 'ushiisoud@gmail.com'),
(231014670, '123456789', 'Radwa Sherif', 123456, 100, '');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `USERS_ID` int(11) NOT NULL,
  `USERS_Phnumber` varchar(15) DEFAULT NULL,
  `USERS_Name` varchar(255) DEFAULT NULL,
  `USERS_Attendance` int(11) DEFAULT NULL,
  `USERS_Email` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`USERS_ID`, `USERS_Phnumber`, `USERS_Name`, `USERS_Attendance`, `USERS_Email`) VALUES
(231005756, '01020400868', 'Shahd Osama', 100, 'shahdosama197@gmail.com'),
(231005898, '01019972179', 'Moaz Wael', 80, 'm.sallam05898@student.aast.edu'),
(231005999, '01019972179', 'Ushii', 100, 'ushiisoud@gmail.com'),
(231014670, '01545765454', 'Radwa Sherif', 100, '');

-- --------------------------------------------------------

--
-- Table structure for table `user_allergies`
--

CREATE TABLE `user_allergies` (
  `USERS_ID` int(11) NOT NULL,
  `ALLERGY_ID` int(11) NOT NULL,
  `Has_Allergy` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_allergies`
--

INSERT INTO `user_allergies` (`USERS_ID`, `ALLERGY_ID`, `Has_Allergy`) VALUES
(231005898, 2, 'Yes'),
(231005898, 4, 'Yes'),
(231005898, 5, 'Yes');

-- --------------------------------------------------------

--
-- Table structure for table `user_vouchers`
--

CREATE TABLE `user_vouchers` (
  `USERS_ID` int(11) NOT NULL,
  `VOUCHER_ID` int(11) NOT NULL,
  `VOUCHER_StartDate` date NOT NULL,
  `VOUCHER_EndDate` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_vouchers`
--

INSERT INTO `user_vouchers` (`USERS_ID`, `VOUCHER_ID`, `VOUCHER_StartDate`, `VOUCHER_EndDate`) VALUES
(231005756, 1, '2025-05-26', '2025-05-31'),
(231005756, 1, '2025-06-01', '2025-06-06'),
(231005999, 1, '2025-06-01', '2025-06-06'),
(231014670, 1, '2025-05-26', '2025-05-31'),
(231014670, 1, '2025-06-01', '2025-06-06');

-- --------------------------------------------------------

--
-- Table structure for table `voucher`
--

CREATE TABLE `voucher` (
  `VOUCHER_ID` int(11) NOT NULL,
  `VOUCHER_Percentage` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `voucher`
--

INSERT INTO `voucher` (`VOUCHER_ID`, `VOUCHER_Percentage`) VALUES
(1, 30),
(2, 15),
(3, 5);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`ADMIN_ID`);

--
-- Indexes for table `allergy`
--
ALTER TABLE `allergy`
  ADD PRIMARY KEY (`ALLERGY_ID`);

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`CART_ID`),
  ADD KEY `USERS_ID` (`USERS_ID`),
  ADD KEY `MEAL_ID` (`MEAL_ID`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`CATEGORY_ID`);

--
-- Indexes for table `ingredients`
--
ALTER TABLE `ingredients`
  ADD PRIMARY KEY (`INGREDIENT_ID`);

--
-- Indexes for table `meal`
--
ALTER TABLE `meal`
  ADD PRIMARY KEY (`MEAL_ID`),
  ADD KEY `CATEGORY_ID` (`CATEGORY_ID`);

--
-- Indexes for table `meal_ingredients`
--
ALTER TABLE `meal_ingredients`
  ADD PRIMARY KEY (`INGREDIENT_ID`,`MEAL_ID`),
  ADD KEY `MEAL_ID` (`MEAL_ID`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`ORDER_ID`),
  ADD KEY `USERS_ID` (`USERS_ID`);

--
-- Indexes for table `order_details`
--
ALTER TABLE `order_details`
  ADD PRIMARY KEY (`MEAL_ID`,`ORDER_ID`,`NOTE`),
  ADD KEY `ORDER_ID` (`ORDER_ID`);

--
-- Indexes for table `uni_users`
--
ALTER TABLE `uni_users`
  ADD PRIMARY KEY (`USERS_ID`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`USERS_ID`);

--
-- Indexes for table `user_allergies`
--
ALTER TABLE `user_allergies`
  ADD PRIMARY KEY (`USERS_ID`,`ALLERGY_ID`),
  ADD KEY `ALLERGY_ID` (`ALLERGY_ID`);

--
-- Indexes for table `user_vouchers`
--
ALTER TABLE `user_vouchers`
  ADD PRIMARY KEY (`USERS_ID`,`VOUCHER_ID`,`VOUCHER_StartDate`),
  ADD KEY `VOUCHER_ID` (`VOUCHER_ID`);

--
-- Indexes for table `voucher`
--
ALTER TABLE `voucher`
  ADD PRIMARY KEY (`VOUCHER_ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `allergy`
--
ALTER TABLE `allergy`
  MODIFY `ALLERGY_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `cart`
--
ALTER TABLE `cart`
  MODIFY `CART_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=221;

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `CATEGORY_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `ingredients`
--
ALTER TABLE `ingredients`
  MODIFY `INGREDIENT_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=106;

--
-- AUTO_INCREMENT for table `meal`
--
ALTER TABLE `meal`
  MODIFY `MEAL_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `ORDER_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=96;

--
-- AUTO_INCREMENT for table `voucher`
--
ALTER TABLE `voucher`
  MODIFY `VOUCHER_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`USERS_ID`) REFERENCES `users` (`USERS_ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`MEAL_ID`) REFERENCES `meal` (`MEAL_ID`) ON DELETE CASCADE;

--
-- Constraints for table `meal`
--
ALTER TABLE `meal`
  ADD CONSTRAINT `meal_ibfk_1` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `category` (`CATEGORY_ID`);

--
-- Constraints for table `meal_ingredients`
--
ALTER TABLE `meal_ingredients`
  ADD CONSTRAINT `meal_ingredients_ibfk_1` FOREIGN KEY (`INGREDIENT_ID`) REFERENCES `ingredients` (`INGREDIENT_ID`),
  ADD CONSTRAINT `meal_ingredients_ibfk_2` FOREIGN KEY (`MEAL_ID`) REFERENCES `meal` (`MEAL_ID`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`USERS_ID`) REFERENCES `users` (`USERS_ID`);

--
-- Constraints for table `order_details`
--
ALTER TABLE `order_details`
  ADD CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`MEAL_ID`) REFERENCES `meal` (`MEAL_ID`),
  ADD CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`ORDER_ID`) REFERENCES `orders` (`ORDER_ID`);

--
-- Constraints for table `user_allergies`
--
ALTER TABLE `user_allergies`
  ADD CONSTRAINT `user_allergies_ibfk_1` FOREIGN KEY (`USERS_ID`) REFERENCES `users` (`USERS_ID`),
  ADD CONSTRAINT `user_allergies_ibfk_2` FOREIGN KEY (`ALLERGY_ID`) REFERENCES `allergy` (`ALLERGY_ID`);

--
-- Constraints for table `user_vouchers`
--
ALTER TABLE `user_vouchers`
  ADD CONSTRAINT `user_vouchers_ibfk_1` FOREIGN KEY (`USERS_ID`) REFERENCES `users` (`USERS_ID`),
  ADD CONSTRAINT `user_vouchers_ibfk_2` FOREIGN KEY (`VOUCHER_ID`) REFERENCES `voucher` (`VOUCHER_ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
