package pl.robertsreberski.ketchup.exceptions

/**
 * Author: Robert Sreberski
 * Creation time: 21:36 14/05/2018
 * Package name: pl.robertsreberski.ketchup.exceptions
 */
class DatabaseTransactionNotSuccessful(override var message: String) : Exception(message)
