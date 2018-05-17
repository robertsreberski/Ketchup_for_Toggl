package pl.robertsreberski.ketchup.pojos

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Author: Robert Sreberski
 * Creation time: 19:14 11/05/2018
 * Package name: pl.robertsreberski.ketchup.pojos
 */
open class Project(
        @PrimaryKey var id: String = UUID.randomUUID().toString(),
        open var toggleId: Long = -1,
        open var name: String = "(no project)",
        open var color: String = "#546E7A",
        open var exists: Boolean = true
) : RealmObject()