package pl.robertsreberski.ketchup.pojos

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Author: Robert Sreberski
 * Creation time: 10:36 12/05/2018
 * Package name: pl.robertsreberski.ketchup.pojos
 */
open class Interval(
        @PrimaryKey var id: String = UUID.randomUUID().toString(),
        open var start: Long = System.currentTimeMillis(),
        open var end: Long = 0
) : RealmObject()