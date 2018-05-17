package pl.robertsreberski.ketchup.local

import com.vicpin.krealmextensions.save
import pl.robertsreberski.ketchup.pojos.Interval
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Author: Robert Sreberski
 * Creation time: 12:46 15/05/2018
 * Package name: pl.robertsreberski.ketchup.db
 */
@Singleton
class IntervalsLocal @Inject constructor() {

    fun createDefault(): Interval {
        val interval = Interval()
        interval.save()
        return interval
    }

    fun finishInterval(interval: Interval): Interval {
        interval.end = System.currentTimeMillis()
        interval.save()
        return interval
    }

}