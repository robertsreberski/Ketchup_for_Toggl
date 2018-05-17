package pl.robertsreberski.ketchup.local


import com.vicpin.krealmextensions.*
import io.reactivex.Flowable
import io.realm.Sort
import pl.robertsreberski.ketchup.pojos.TimeEntry
import pl.robertsreberski.ketchup.pojos.TimeEntryFields
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Author: Robert Sreberski
 * Creation time: 12:46 15/05/2018
 * Package name: pl.robertsreberski.ketchup.db
 */
@Singleton
class TimeEntriesLocal @Inject constructor() {

    fun getPomodorosFlowableForToday(today: Long): Flowable<List<TimeEntry>> {
        return TimeEntry().queryAsFlowable {
            greaterThan(TimeEntryFields.CREATED_AT, today).and()
                    .equalTo(TimeEntryFields.TYPE, TimeEntry.Type.POMODORO.name)
        }.onBackpressureLatest()
    }

    fun listNotFinishedEntries(): List<TimeEntry> {
        return TimeEntry().query { equalTo(TimeEntryFields.FINISHED, false) }
    }

    fun getLatestQuery(type: TimeEntry.Type? = null, finished: Boolean? = null): TimeEntry? {
        return TimeEntry().queryLast {
            if (type != null) equalTo(TimeEntryFields.TYPE, type.name)
            if (finished != null) equalTo(TimeEntryFields.FINISHED, finished)
        }
    }

    fun getCurrentEntry(): Flowable<List<TimeEntry>> {
        return TimeEntry().querySortedAsFlowable(TimeEntryFields.CREATED_AT, Sort.ASCENDING) {
            equalTo(TimeEntryFields.FINISHED, false)
        }.onBackpressureLatest()
    }

    fun save(entry: TimeEntry) {
        entry.save()
    }

    fun saveAll(entries: List<TimeEntry>) {
        entries.saveAll()
    }

    fun removeAllAfter(entry: TimeEntry, including: Boolean = false) {
        TimeEntry().delete {
            if (!including) greaterThan(TimeEntryFields.CREATED_AT, entry.createdAt)
            else greaterThanOrEqualTo(TimeEntryFields.CREATED_AT, entry.createdAt)
        }
    }


}