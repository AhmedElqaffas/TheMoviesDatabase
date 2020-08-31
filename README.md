# TheMoviesDatabase
An android application based on [TheMovieDatabase API](https://developers.themoviedb.org/3). 
**Users can** 
  * **View popular/top-rated movies and shows**
  * **Search for  movies and shows by name**
  * **View movie/show details**
  * **Add movies and shows to favorites**
  
## Main Technical Components
In this application, I have used
  * **Coroutines**
  * **Firebase Authentication & Cloud Firestore**
  * **MVVM architecure using Jetpack Livedata**
  * **Recyclerview** 
  * **Retrofit2 with Gson converter**
  * **Room persistence library**
  
I have also used **JUnit** for testing some features in my application.


## Application Preview
The rest of this document, I will be demonstrating the different screens in this application.

#### &nbsp;&nbsp;&nbsp;Logging In
<p align="center"><img src="/screens/login.png" alt="alt text" width="250" height="400"></p>
The first screen that the user encounters is the login screen. The user can either login to firebase using email and password or continue anonymously without logging in.
In anonymous mode, users will not be able to save their favorite movies on cloud firestore, data will only be saved locally.
If the user wishes to enjoy firebase services, the user can create a new account by clicking on "Don't have an account? Sign up" text.

#### &nbsp;&nbsp;&nbsp;Signing Up
<p align="center"><img src="/screens/signup.png" alt="alt text" width="250" height="400"></p>
The firebase authentication service validates the data provided when signing up. After signing up, the user is redirected automatically to the application main screen.
