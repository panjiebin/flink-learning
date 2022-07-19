-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- 主机： 127.0.0.1
-- 生成日期： 2022-07-19 17:47:17
-- 服务器版本： 10.4.24-MariaDB
-- PHP 版本： 7.4.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 数据库： `pharmacy`
--

-- --------------------------------------------------------

--
-- 表的结构 `customer`
--

CREATE TABLE `customer` (
  `cust_ID` int(11) NOT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `contact_address` varchar(255) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 转存表中的数据 `customer`
--

INSERT INTO `customer` (`cust_ID`, `first_name`, `last_name`, `gender`, `age`, `contact_address`, `email`, `password`) VALUES
(1, 'George', 'Johnson', 1, 30, '3559  Poling Farm Road', 'qacvqmnutm@iubridge.com', '123456'),
(2, 'Stephen', 'Curry', 1, 34, '4814  Bird Street', 'eqljrotagu@iubridge.com', '345678');

-- --------------------------------------------------------

--
-- 表的结构 `medicines`
--

CREATE TABLE `medicines` (
  `med_ID` int(11) NOT NULL,
  `med_category` varchar(30) DEFAULT NULL,
  `name` varchar(30) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 转存表中的数据 `medicines`
--

INSERT INTO `medicines` (`med_ID`, `med_category`, `name`, `description`, `price`) VALUES
(1, 'A', 'Absinthol', 'hemostasis', 100),
(2, 'B', 'Ablukast', 'anti-allergy', 100),
(3, 'A', ' Arginine', ' hemostasis', 100),
(4, 'B', 'Bacitracin', 'anti-allergy', 100);

-- --------------------------------------------------------

--
-- 表的结构 `pharmacist`
--

CREATE TABLE `pharmacist` (
  `phar_ID` int(11) NOT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `gender` int(11) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `contact_address` varchar(255) DEFAULT NULL,
  `admin_email` varchar(100) DEFAULT NULL,
  `admin_password` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 转存表中的数据 `pharmacist`
--

INSERT INTO `pharmacist` (`phar_ID`, `first_name`, `last_name`, `gender`, `age`, `contact_address`, `admin_email`, `admin_password`) VALUES
(1, 'Larry', 'Jones', 1, 30, '345  Capitol Avenue', 'npsrvuywai@iubridge.com', '123456'),
(2, 'Tim', 'Hardaway', 1, 30, '346  Capitol Avenue', 'acubwunxpp@iubridge.com', '123456'),
(3, 'Larry', 'Jame', 1, 25, '345  Capitol Avenue', 'npsrvuywai@iubridge.com', '123456'),
(4, 'Larry', 'Jack', 1, 55, '345  Capitol Avenue', 'npsrvuywai@iubridge.com', '123456'),
(5, 'Larry', 'Jack', 1, 45, '345  Capitol Avenue', 'npsrvuywai@iubridge.com', '123456');

-- --------------------------------------------------------

--
-- 表的结构 `purchase`
--

CREATE TABLE `purchase` (
  `purchase_ID` int(11) NOT NULL,
  `cust_ID` int(11) DEFAULT NULL,
  `med_ID` int(11) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 转存表中的数据 `purchase`
--

INSERT INTO `purchase` (`purchase_ID`, `cust_ID`, `med_ID`, `amount`, `date`) VALUES
(1, 1, 1, 2, '2022-07-19'),
(2, 1, 2, 2, '2022-07-19'),
(3, 1, 2, 5, '2022-07-19'),
(4, 1, 1, 6, '2022-07-19'),
(5, 2, 2, 10, '2022-07-19'),
(6, 2, 1, 20, '2022-07-19');

-- --------------------------------------------------------

--
-- 表的结构 `sales`
--

CREATE TABLE `sales` (
  `sales_ID` int(11) NOT NULL,
  `purchase_ID` int(11) DEFAULT NULL,
  `phar_ID` int(11) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- 转存表中的数据 `sales`
--

INSERT INTO `sales` (`sales_ID`, `purchase_ID`, `phar_ID`, `count`, `total_amount`, `date`) VALUES
(1, 1, 1, 2, 200, '2022-07-19'),
(2, 1, 1, 2, 200, '2022-07-19'),
(3, 3, 1, 5, 500, '2022-07-19'),
(4, 4, 1, 6, 600, '2022-07-19');

--
-- 转储表的索引
--

--
-- 表的索引 `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`cust_ID`);

--
-- 表的索引 `medicines`
--
ALTER TABLE `medicines`
  ADD PRIMARY KEY (`med_ID`);

--
-- 表的索引 `pharmacist`
--
ALTER TABLE `pharmacist`
  ADD PRIMARY KEY (`phar_ID`);

--
-- 表的索引 `purchase`
--
ALTER TABLE `purchase`
  ADD PRIMARY KEY (`purchase_ID`),
  ADD KEY `purchase_customer_cust_ID_fk` (`cust_ID`),
  ADD KEY `purchase_medicines_med_ID_fk` (`med_ID`);

--
-- 表的索引 `sales`
--
ALTER TABLE `sales`
  ADD PRIMARY KEY (`sales_ID`),
  ADD KEY `sales_pharmacist_phar_ID_fk` (`phar_ID`),
  ADD KEY `sales_purchase_purchase_ID_fk` (`purchase_ID`);

--
-- 限制导出的表
--

--
-- 限制表 `purchase`
--
ALTER TABLE `purchase`
  ADD CONSTRAINT `purchase_customer_cust_ID_fk` FOREIGN KEY (`cust_ID`) REFERENCES `customer` (`cust_ID`),
  ADD CONSTRAINT `purchase_medicines_med_ID_fk` FOREIGN KEY (`med_ID`) REFERENCES `medicines` (`med_ID`);

--
-- 限制表 `sales`
--
ALTER TABLE `sales`
  ADD CONSTRAINT `sales_pharmacist_phar_ID_fk` FOREIGN KEY (`phar_ID`) REFERENCES `pharmacist` (`phar_ID`),
  ADD CONSTRAINT `sales_purchase_purchase_ID_fk` FOREIGN KEY (`purchase_ID`) REFERENCES `purchase` (`purchase_ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
