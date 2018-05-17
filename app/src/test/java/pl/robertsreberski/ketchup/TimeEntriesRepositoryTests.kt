package pl.robertsreberski.ketchup

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import io.realm.RealmList
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import pl.robertsreberski.ketchup.local.IntervalsLocal
import pl.robertsreberski.ketchup.local.ProjectsLocal
import pl.robertsreberski.ketchup.local.TimeEntriesLocal
import pl.robertsreberski.ketchup.pojos.Interval
import pl.robertsreberski.ketchup.pojos.Project
import pl.robertsreberski.ketchup.pojos.TimeEntry
import pl.robertsreberski.ketchup.pojos.TimeEntry.Type.*
import pl.robertsreberski.ketchup.repos.TimeEntriesRepository

/**
 * Author: Robert Sreberski
 * Creation time: 10:02 16/05/2018
 * Package name: pl.robertsreberski.ketchup
 */
@RunWith(MockitoJUnitRunner::class)
class TimeEntriesRepositoryTests {

    @Mock
    lateinit var intervalsLocal: IntervalsLocal

    @Mock
    lateinit var timeEntriesLocal: TimeEntriesLocal

    @Mock
    lateinit var projectsLocal: ProjectsLocal

    lateinit var repository: TimeEntriesRepository

    lateinit var testScheduler: TestScheduler

    @Before
    fun init() {
        repository = TimeEntriesRepository(intervalsLocal, timeEntriesLocal, projectsLocal)
        testScheduler = TestScheduler()
    }

    @Test
    fun settingActiveProject_setsActiveProjectProperty() {
        val mockedProject = Project()
        val observer = TestObserver<Boolean>()

        repository.setActiveProject(mockedProject).subscribe(observer)

        observer.assertComplete()
        assertEquals(mockedProject, repository._activeProject)
    }

    @Test
    fun settingActiveProject_updatesProjectInCurrentlyRunningPomodoro() {
        val mockedPomodoro = TimeEntry(type = POMODORO.name, finished = false)
        val mockedProject = Project()
        val observer = TestObserver<Boolean>()

        `when`(timeEntriesLocal.getLatestQuery(POMODORO, false))
                .thenReturn(mockedPomodoro)

        repository.setActiveProject(mockedProject).subscribe(observer)

        observer.onComplete()
        verify(timeEntriesLocal, times(1)).save(mockedPomodoro)
        assertEquals(mockedProject, mockedPomodoro.project)
    }

    @Test
    fun fetchCurrentEntry_IfEntryIsNotFinished_ReturnsEntry() {
        val mockedPomodoro = TimeEntry(type = POMODORO.name, finished = false)
        val observer = TestObserver<TimeEntry>()

        `when`(timeEntriesLocal.getLatestQuery(null, false))
                .thenReturn(mockedPomodoro)

        repository.fetchCurrentEntry().subscribe(observer)
        observer.assertValue(mockedPomodoro)
    }

    @Test
    fun fetchCurrentEntry_IfEveryEntryFinished_ReturnsNull() {
        val observer = TestObserver<TimeEntry>()

        `when`(timeEntriesLocal.getLatestQuery(null, false))
                .thenReturn(null)

        repository.fetchCurrentEntry().subscribe(observer)
        observer.assertComplete().assertNoValues()
    }

    @Test
    fun startPomodoroEntry_IfLatestPomodoroNotFinished_UsesIt() {
        val mockedPomodoro = TimeEntry(type = POMODORO.name, finished = false)
        val observer = TestObserver<TimeEntry>()

        `when`(timeEntriesLocal.getLatestQuery(POMODORO, false))
                .thenReturn(mockedPomodoro)

        repository.startPomodoroEntry().subscribe(observer)

        observer.assertComplete()
        verify(timeEntriesLocal).save(mockedPomodoro)
        observer.assertValue(mockedPomodoro)
    }

    @Test
    fun startPomodoroEntry_IfEveryPomodoroFinished_CreatesNew() {
        val observer = TestObserver<TimeEntry>()

        `when`(timeEntriesLocal.getLatestQuery(POMODORO, false))
                .thenReturn(null)

        repository.startPomodoroEntry().subscribe(observer)

        observer.assertComplete()
        observer.assertValueCount(1)
        verify(timeEntriesLocal, times(1)).save(any())
    }

    @Test
    fun startPomodoroEntry_CreatesNewInterval_AndAddsItToPomodoro() {
        val mockedPomodoro = TimeEntry(type = POMODORO.name, finished = false, intervals = mock(RealmList::class.java) as RealmList<Interval>)
        val mockedInterval = Interval()
        val observer = TestObserver<TimeEntry>()

        `when`(timeEntriesLocal.getLatestQuery(POMODORO, false))
                .thenReturn(mockedPomodoro)

        `when`(intervalsLocal.createDefault()).thenReturn(mockedInterval)

        repository.startPomodoroEntry().subscribe(observer)

        observer.onComplete()
        verify(mockedPomodoro.intervals).add(mockedInterval)
        verify(timeEntriesLocal, times(1)).save(mockedPomodoro)
    }

    @Test
    fun stopCurrentEntry_StopsAllEntries() {
        val mockedInterval = Interval()
        val mockedList = mock(RealmList::class.java) as RealmList<Interval>
        val mockedPomodoros = listOf<TimeEntry>(
                TimeEntry(intervals = mockedList),
                TimeEntry(intervals = mockedList)
        )
        val observer = TestObserver<Boolean>()

        `when`(timeEntriesLocal.listNotFinishedEntries()).thenReturn(mockedPomodoros)
        `when`(mockedList.last()).thenReturn(mockedInterval)

        repository.stopCurrentEntry(false).subscribe(observer)

        observer.assertComplete().assertValue(true)
        verify(intervalsLocal, times(mockedPomodoros.size)).finishInterval(mockedInterval)
        verify(timeEntriesLocal).saveAll(mockedPomodoros)
    }

    @Test
    fun pauseCurrentEntry_StopsCurrentEntry_AndStartsPauseEntry() {
        val observer = TestObserver<TimeEntry>()

        repository.pauseCurrentEntry().subscribe(observer)

        observer.assertComplete().assertValue { it.type == PAUSE.name }
        verify(intervalsLocal, times(1)).createDefault()
        verify(timeEntriesLocal, times(1)).save(any())
    }

    @Test
    fun abandonCurrentEntry_RemovesEveryEntryAfterLastNotFinishedPomodoro() {
        val mockedPomodoro = TimeEntry(type = POMODORO.name, finished = false)
        val observer = TestObserver<Boolean>()

        `when`(timeEntriesLocal.getLatestQuery(POMODORO, false)).thenReturn(mockedPomodoro)

        repository.abandonCurrentEntry().subscribe(observer)

        observer.assertComplete()
        verify(timeEntriesLocal, times(1)).removeAllAfter(mockedPomodoro, true)
    }

    @Test
    fun startBreakEntry_StartsNewEntry() {
        val observer = TestObserver<TimeEntry>()

        repository.startBreakEntry().subscribe(observer)

        observer.assertComplete().assertValue { it.type == BREAK.name }
        verify(intervalsLocal, times(1)).createDefault()
        verify(timeEntriesLocal, times(1)).save(any())
    }


}