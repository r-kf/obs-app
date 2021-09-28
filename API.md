# API-howto

Skrevet på engelsk siden det er enklere å forklare for meg.

## The structure
The application is structured in a so-called MVVM-Hilt-Kotlin-Coroutines-Retrofit-RoomDB-Android-Architechture stack, or in short
HKCRRAA-MVVM. I made that one up.

Basically this is the form:
![](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)
Or in our case:

![](https://i.imgur.com/x0OQZVB.png)

|Name|Explanation|Used for|
|-|-|-|
|data|The M in MVVM, holds every package related to managing remote content, repositories, models, local data|Network, Data management
|data.local|Local data management; the applications database reside in `AppDatabase.kt` and only holds SQL data that are annotated with `@Dao`. All remote data will be cached in this database to prevent unnecessary network calls.|Data management, Caching
|data.models|Data classes that model the remote and local data for use within the application. These data classes are held within objects for their APIs. Example: every data class for the `MetAlerts` API will be located in the object `MetAlertsModel.kt`|Data representation
|data.remote|Remote data management; classes that call on APIs are called `DataSources` and inherit the `BaseDataSource` class. The inheritance makes it as easy as defining the API call as `suspend fun get() = getResult { API.get() }` for remote data fetching. No need to worry about dispatchers. Services also locate here which are interfaces that define the API calls.|Network, Data parsing
|data.repository|Data gathering; does it seem redundant? Well this is where the Android Architecture part comes in. Repositories are located here which take care of binding models (as well as the local database) and the remote data sources. For every new API endpoint, there must be a data source to it, a model that models it and a database model to it. But don't worry, it's not a lot of code. Welcome to Android development.|Binding of remote and local data sources
|di|Dependency injection; this is where Hilt modules are located. Think of it like global variables for the application.|Dependency injection of Activities and Fragments
|ui|This is where all the UI-related classes are located. `MainActivity` is the sole activity of the application.|UI
|ui.views|Fragments and their view models are located here. The `MainActivity` class loads all of them within its own `FragmentContainer` which is managed by a `Navigation` component.|UI representation
|utils|Useful utilities and helper functions/classes are located here.|Making life easier
|`App.kt`|This is the entry point of the entire application.|Loading the application

## Getting data in the application (I want to get something from a repository)

As of pull request #5, every fragment has the `MetAlertsRepository` ready available for use within their view models.

1. In the view model of the fragment you wish to get data in, declare a variable that calls on the repositories functions like this
```kt
@HiltViewModel
class FragmentViewModel @Inject constructor(
    repository: Repository // Make sure there's a repository to the API you wish to get data from.
) : ViewModel() {
    // Every return value of a repositories functions are wrapped within LiveData objects which hold a Resource object
    // Data in this instance is the data you wish to get.
    val x: LiveData<Resource<Data>> = repository.function()
}
```
2. In the fragment class, you may wish to make a variable that holds the data from the response
```kt
@AndroidEntryPoint
class Fragment : Fragment() {
  ...
  private val viewModel: FragmentViewModel by viewModels() // The view model reference to this fragment
  private lateinit var x: Data
  ...
}
```
3. Within the fragments own `OnViewCreated` function, you call on the repositories function from the viewModel reference by observing the `LiveData`
```kt
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.x.observe(viewLifecycleOwner, { res ->
        when (res.status) {
            Resource.Status.SUCCESS -> {
                if (!res.data.isNullOrEmpty()) {
                    x = res.data
                 } else {
                     // Data is absent but the request was successful
                }
            }
            Resource.Status.ERROR -> {
              // Request failed.
            }
            Resource.Status.LOADING -> {
              // Currently requesting
            }
        }
    })
  ...
}
```
4. You can now make use of the data from the holder variable you created.
```kt
println(x.foo) // bar
```

## Create a new API endpoint (I want to create a new repository)

As of pull request #5, every fragment are prepared to take in any repository

### 1. Prepare the model

When you create a new API endpoint, you want to make sure the application can understand it. Prepare a new model in the `data.models`
with the name of the API endpoint followed by a `Model`. Example: `FoobarModel`.
```kt
object FoobarModel {
  data class Foo(
    var bar: String? = null
  )
  // For XML objects
  @Xml
  data class Foo(
    @PropertyElement
    var iAmMyself: String? = null,
    @Element
    var iGotChildren: List<Child>? = null
  )
}
```

For the new model you've created, you must tell the local database that it exists so it can be cached. In `data.local.AppDatabase`, add a new entry to the entities like this
```kt
@Database(
    entities = [
      FoobarModel.Foo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  ...
}

```

Then create a new `Dao` object of the API endpoint containing an interface of the model you created in `data.local`.
```kt
object FoobarDao {
    @Dao
    interface FooDao {
        @Query("SELECT * from foo")
        fun get(): LiveData<FoobarModel.Foo> // Have at least a function to get the model (wrapped within a LiveData)

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(foo: FoobarModel.Foo) // And a function to insert the model
    }
}
```

At this point your model is ready, but we still need to get data to map to the model. This is where we have to go in `data.remote` and `data.repository` to continue the progress.

## 2. Create a data source and a service

Inside of `data.remote` create a new data source class that inherits `BaseDataSource`

```kt
class FoobarDataSource @Inject constructor(
    private val service: FoobarService
) : BaseDataSource() {
    suspend fun get() = getResult { service.get() }
}
```

As you can see, we are missing the actual service the `service` parameter points to. Let's create that too.

```kt
interface FoobarService {
    @GET("/")
    suspend fun get(): Response<FoobarModel.Foo>
}
```

As noted in the [Retrofit documentation](https://square.github.io/retrofit/), we have to annotate what the request to the API endpoint is. To get data, use `@GET("url")`, and to send data, use `@POST("url")`.

The concept is familiar if you have any past experience with REST/HTTP clients.

At this point you now have a data source and a service to retrieve data from your API endpoint. Now we need to create a repository to bind these sources with the models and make it accessable to the rest of the application.

## 3. Create a repository

A repository is basically the interface that makes the data representable in form of models inside the view models of the application.

Creating the repository is simple, follow this structure:
```kt
class FoobarRepository @Inject constructor(
    private val remote: FoobarDataSource,
    private val database: AppDatabase
) {
    fun get() = Helpers.Network.performGetOperation(
        databaseQuery = { database.FooDao().get() },
        networkCall = { remote.get() }
    ) {
        if (it != null) {
            database.FooDao().insert(it)
        }
    }
}
```

A repository takes in 2 injected parameters, the remote data source and the local database. Each function inside the class must make use of the `performGetOperation` helper function to execute the API call. (Other forms of operation functions may be implemented later if needed).

Hopefully the code is enough to understand what it does, if it doesn't, each parameter of the helper function runs in 3 steps:

1. Check the local database first for data, if the same data is found on the database, it returns that.
2. Call on the API to get data and then it in a callback. Throw error if it fails.
3. If the data is not absent, insert it into the local database. (This is the lambda portion of the code)

Now you've successfully created a repository for your new API endpoint! But, wait. The application has no idea it exists?!

That's because all we've made so far hasn't been injected into the application for use in our view models!

This is the complicated part, so sit tight.

### 4. Dependency injection in a nutshell

[Hilt](https://dagger.dev/hilt/) is a dependency injector for Android developed by Google. This is what the application uses to make the repositories, data sources and etc. accessible to the rest of the code.

Annotations are used to declare a class, interface or object as injectable. This reduces a lot of boilerplate code and might sometimes make Android Studio act up, which can be solved with rebuilding the project. But don't worry, it's developed by Google, [remember?](https://github.com/google/dagger) It's in active development at the moment, but is stable for use in production already.

In our application, all you have to do is the create a module that holds all of our progress in the `di` folder.
```kt
@Module
@InstallIn(SingletonComponent::class)
object FoobarModule {
    @Provides
    fun provideFoobarURL(): String = "url"

    @Provides
    fun provideFoobarService(retrofit: Retrofit): FoobarService =
        retrofit.create(FoobarService::class.java)

    @Singleton
    @Provides
    fun provideFoobarRepository(
        remote: FoobarDataSource,
        database: AppDatabase
    ) =
        FoobarRepository(remote = remote, database = database)

    @Singleton
    @Provides
    fun provideFoobarDataSource(service: FoobarService) = FoobarDataSource(service)
}
```

This code can be read as:
```
Module Foobar:
  will provide URL to the API.
  will provide a service to the API.
  will provide a repository to the API.
  will provide a data source to the API.

Module Foobar will be installed as a Singleton in the application.
```

The last step will be to include this module in the applications own module, which can be seen as a collection of global variables.

```kt
@Module(includes = [FoobarModule::class])
@InstallIn(SingletonComponent::class)
object AppModule {
  ...
```

Now your newly made repository is available to every view model and any injectable classes in the application.

## Additional documentation regarding Android Architecture Components

The application makes heavy use of the Android Jetpack suite, which includes Android Architecture components.

[Android Developers documentation](https://developer.android.com/topic/libraries/architecture)

[Navigation Component (used for navigating between fragments)](https://developer.android.com/guide/navigation)

[LiveData documentation (how data is observable)](https://developer.android.com/topic/libraries/architecture/livedata)

[Data Binding documentation (enabled in our application)](https://developer.android.com/topic/libraries/data-binding)

[View Binding documentation (removes the need to use `findViewById`)](https://developer.android.com/topic/libraries/view-binding)