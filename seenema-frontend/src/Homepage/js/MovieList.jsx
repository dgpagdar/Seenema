import {useEffect, useState} from "react"
import CardRow from "./CardRow"
import api from "./api"
import '../css/MovieList.css'
import SearchedCardsPage from "./SearchedCardsPage"
import Lottie from "lottie-react";
import NoResultsFound from "../../assets/NoResultsFound.json";
import MovieCard from "./MovieCard"
import {useParams} from "react-router-dom"

const MovieList = () => {
    // State Variables for different categories of the movies.
    const [topRatedMovies, setTopRatedMovies] = useState([]);
    const [upcomingMovies, setUpcomingMovies] = useState([]);
    const [popularMovies, setPopularMovies] = useState([]);
    const [nowPlayingMovies, setNowPlayingMovies] = useState([]);
    const [searchResults, setSearchResults] = useState([])
    const [currentPage, setCurrentPage] = useState(1);
    const searchTerm = useParams();

    // Function to fetch searched movies based on the searchTerm and page number
    const fetchSearchMovies = async (page) => {
        if ((searchTerm.searchTerm !== undefined)) {
            const title = searchTerm.searchTerm;
            try {
                const {data} = await api.get("search/movie", {
                    params: {
                        query: title,
                        page: page
                    },
                })
                if (page === 1) {
                    setSearchResults(data.results);
                } else {
                    setSearchResults(prev => [...prev, ...data.results])
                }
            } catch (error) {
                console.error('Failed to fetch the searched movie:', error);
            }
        }
    }

    // Function to fetch top-rated movies
    const fetchTopRatedMovies = async () => {
        try {
            const {data} = await api.get("movie/top_rated");
            setTopRatedMovies(data.results);
        } catch (error) {
            console.error('Failed to fetch top rated movies:', error);
        }
    };

    // Function to fetch upcoming movies
    const fetchUpcomingMovies = async () => {
        try {
            const {data} = await api.get("movie/upcoming");
            setUpcomingMovies(data.results);
        } catch (error) {
            console.error('Failed to fetch upcoming movies:', error);
        }
    };

    // Function to fetch popular movies
    const fetchPopularMovies = async () => {
        try {
            const {data} = await api.get("movie/popular");
            setPopularMovies(data.results);
        } catch (error) {
            console.error('Failed to fetch popular movies:', error);
        }
    };

    // Function to fetch now playing movies
    const fetchNowPlayingMovies = async () => {
        try {
            const {data} = await api.get("movie/now_playing");
            setNowPlayingMovies(data.results);
        } catch (error) {
            console.error('Failed to fetch now playing movies:', error);
        }
    };
    // Re-run all fetches when the searchTerm route param changes
    useEffect(() => {
        setCurrentPage(1);
        fetchSearchMovies(1);
        fetchTopRatedMovies();
        fetchUpcomingMovies();
        fetchPopularMovies();
        fetchNowPlayingMovies();
    }, [searchTerm]);

    // Function to handle the load more button click
    const handleLoadMore = () => {
        const newPage = currentPage + 1;
        setCurrentPage(newPage);
        fetchSearchMovies(newPage)
    }

    // Boolean to check if there are more results to load
    const hasMoreResults = searchResults.length > 0 && searchResults.length % 20 === 0;

    // Rendering the component with movie data
    return (
        <div>
            {(searchResults.length > 0) && (searchTerm.searchTerm !== undefined) ? (
                <>
                    <h2 className="header-home">Search Results </h2>
                    <div className="movie-grid-search">
                        {searchResults.map(movie => (
                            <MovieCard key={movie.id} movie={movie}/>
                        ))}
                    </div>
                    {hasMoreResults && (
                        <div className="load-more-container-search-page">
                            <button onClick={handleLoadMore}
                                    className="search-button-load-more button-load-more-search">
                                Load More
                            </button>
                        </div>
                    )}
                </>
            ) : (searchTerm.searchTerm === undefined) ? (
                <>
                    <h2 className="header-home">Now Playing</h2>
                    <CardRow movies={nowPlayingMovies}/>
                    <h2 className="header-home">Top Rated Movies</h2>
                    <CardRow movies={topRatedMovies}/>
                    <h2 className="header-home">Upcoming Movies</h2>
                    <CardRow movies={upcomingMovies}/>
                    <h2 className="header-home">Popular Movies</h2>
                    <CardRow movies={popularMovies}/>
                </>
            ) : (searchResults.length === 0) ? (
                <>
                    <h2 className="header-home">No Results Found </h2>
                    <div style={{width: "70%", paddingLeft: "30%", paddingTop: "10%"}}>
                        <Lottie loop={true} animationData={NoResultsFound}/>
                    </div>
                    <SearchedCardsPage movies={searchResults}/>
                </>
            ) : (

                <>
                    <h2 className="header-home">Search Results </h2>
                    <div className="movie-grid-search">
                        {searchResults.map(movie => (
                            <MovieCard key={movie.id} movie={movie}/>
                        ))}
                    </div>
                    {hasMoreResults && (
                        <div className="load-more-container-search-page">
                            <button onClick={handleLoadMore}
                                    className="search-button-load-more button-load-more-search">
                                Load More
                            </button>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default MovieList;
