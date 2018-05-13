# Ketchup for Toggl
Simple, offline-first, fully reactive pomodoro timer with Toggl integration. Naming is inspired by the process of developing this app - I've created it during my personal weekend jam and it was my first app where I could go crazy with Kotlin.

## Motivation
Honestly app is being created to train my skills at developing in MVVM architecture, using newest Android Architecture Components with asynchronous solutions. Designing a timer was the most interesting idea of mine, because it completely tests the approach to reactive programming.

## Status of works - 40%
- [X] Connected **TimerFragment & OptionsFragment** to data
- [ ] Connected **MainActivity & AccountFragment** to data
- [X] Programmed timer functionality
- [ ] Programmed project selection functionality
- [ ] Created **TimeEntriesRepository**
- [ ] Created **UserRepository**
- [ ] Storing data in local database
- [ ] Synchronizing data with Toggl


## Used libraries
* **Android Architecture Components**
  * **LiveData** - Great, lifecycle-aware mechanism of exchanging data ViewModel -> View 
  * **ViewModel** - Going in tandem with LiveData, provides us completely dependency-free View classes
  * **Room** - I feel a need to try this library as a contrary to Firebase and Realm noSQL databases
* **Dagger 2** - App consists of fully injectable modules guided by KISS principle
* **RxKotlin (RxJava 2)** - Used in Repositories Layer allows me to fetch all informations without interrupting other threads
* **Timber** - Providing simple logs in development version
* **Retrofit** - It is always my first choice when it comes to fetching data from REST services

## License
This project is licensed under the **MIT License**. Feel free to use the code to make it even better. I also would be excited to hear about the effects.

## Author
I am **Robert Sreberski** and I'm the only author of this solution. If you like it or have any questions do not hestitate to contact my by [LinkedIn](https://www.linkedin.com/in/robert-sreberski/) or even [Telegram](https://t.me/RobertSreberski). I'll be happy to answer!
