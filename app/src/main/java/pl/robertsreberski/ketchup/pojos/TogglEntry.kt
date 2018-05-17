package pl.robertsreberski.ketchup.pojos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Author: Robert Sreberski
 * Creation time: 08:14 17/05/2018
 * Package name: pl.robertsreberski.ketchup.pojos
 */
open class TogglEntry(
        @Expose(serialize = false) var id: Long = -1,
        var pid: Long? = null,
        var wid: Long? = null,
        var start: Date = Date(),
        var stop: Date? = Date(),
        var duration: Long = 0,
        var description: String = "",
        var tags: List<String>? = null,
        @Expose(deserialize = false) @SerializedName("created_with")
        var createdWith: String = "Ketchup_For_Toggl",
        @Expose(serialize = false) var at: Date = Date()
) {

//    constructor(timeEntry: TimeEntry) :
//            this(
//                    timeEntry.togglId,
//                    timeEntry.project?.toggleId,
//                    null,
//
//            )

}