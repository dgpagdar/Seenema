import React, {useEffect, useState} from 'react';
import api from '../../Homepage/js/api';
import MovieCard from '../../Homepage/js/MovieCard';
import '../../MyListPage/css/MyMovieList.css'

// Component representing a list of movies based on the provided movie IDs
const ListOfMovies = ({moviesList}) => {
    // State to store the details of movies in the suggested movies list
    const [suggestedMoviesListMovies, setSuggestedMoviesListMovies] = useState([]);

    // Effect hook to fetch movie details for each movie ID in the suggested movies list
    useEffect(() => {
        // Function to fetch movie details for each movie ID in the suggested movies list
        const fetchMySuggestionListMovies = async () => {
            try {
                // Use Promise.all to concurrently fetch details for all movies
                const moviesData = await Promise.all(
                    moviesList.map(async (movieId) => {
                        try {
                            // Fetch movie details using the API
                            const response = await api.get(`movie/${movieId}`);
                            return response.data;
                        } catch (error) {
                            // Log an error if fetching details for a movie fails
                            console.error(`Failed to fetch movie details for ID ${movieId}:`, error);
                            return null; // Return null for failed requests
                        }
                    })
                );
                // Set the state with non-null movie details (remove failed requests)
                setSuggestedMoviesListMovies(moviesData.filter((movie) => movie !== null)); // Remove null entries
            } catch (error) {
                // Log an error if fetching movies fails
                console.error('Failed to fetch movies:', error);
            }
        };
        // Invoke the fetchMySuggestionListMovies function when the moviesList prop changes
        fetchMySuggestionListMovies();
    }, [moviesList]);

    // Render the ListOfMovies component
    return (
        <div>
            <div className="Cards">
                <div style={{width: '100%', overflow: 'auto', display: 'flex'}}>
                    <div className="movie-container-mylist">
                        {suggestedMoviesListMovies.map((movie) => (
                            <MovieCard key={movie?.id} movie={movie}/>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ListOfMovies;
