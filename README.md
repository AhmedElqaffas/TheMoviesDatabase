# TheMoviesDatabase
An android application based on [TheMovieDatabase API](https://developers.themoviedb.org/3). 
**Users can** 
  * **View popular/top-rated movies and shows**
  * **Search for  movies and shows by name**
  * **View movie/show details**
  * **Add movies and shows to favorites**
 <br></br>
## Main Technical Components
In this application, I have used
  * **Coroutines**
  * **Firebase Authentication & Cloud Firestore**
  * **MVVM architecure using Jetpack Livedata**
  * **Recyclerview** 
  * **Retrofit2 with Gson converter**
  * **Room persistence library**
  
I have also used **JUnit** for testing some features in my application.
 <br></br>
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

#### &nbsp;&nbsp;&nbsp;Browsing
##### &nbsp;&nbsp;&nbsp;The application allows the user to

&nbsp;&nbsp;&nbsp;Browse popular/top rated Movies
<p align="center"><img src="/screens/movies.jpeg" alt="alt text" width="250" height="400"></p>

&nbsp;&nbsp;&nbsp;Browse popular/top rated Series
<p align="center"><img src="/screens/series.jpeg" alt="alt text" width="250" height="400"></p>

&nbsp;&nbsp;&nbsp;Search for a movie or series by name
<p align="center"><img src="/screens/search_1.jpeg" alt="alt text" width="250" height="400">
<img src="/screens/search_2.jpeg" alt="alt text" width="250" height="400"></p>

&nbsp;&nbsp;&nbsp;The user can also logout and return to the login screen
<p align="center"><img src="/screens/sign_out.jpeg" alt="alt text" width="250" height="400"></p>

#### &nbsp;&nbsp;&nbsp;Movie/Series Details
&nbsp;&nbsp;&nbsp;By clicking on a movie or a tv show, users are redirected to a new screen that displays more details about that movie/show.
<p align="center"><img src="/screens/details_1.jpeg" alt="alt text" width="250" height="400">
<img src="/screens/details_2.jpeg" alt="alt text" width="250" height="400"></p>

The user can also add the movie or series to favorites, the item will be added to a favorites list. There are two separate lists; one for movies, and the other is for series.
<p align="center"><img src="/screens/favorite_animation.jpeg" alt="alt text" width="250" height="400"></p>
<p align="center"><img src="/screens/favorites_list.jpeg" alt="alt text" width="250" height="400"></p>
Notice that adding a movie to favorites shows a short animation of a full heart emerging from the movie/series poster. Similarly, removing an item form favorites shows a broken heart animation.
<br></br>
Adding a movie or series to favorites also gives the user a list of 'similar shows' that the user may be interested in.
<p align="center"><img src="/screens/similar.jpeg" alt="alt text" width="250" height="400"></p>
