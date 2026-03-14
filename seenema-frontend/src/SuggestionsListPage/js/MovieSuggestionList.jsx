import React, {useContext, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "../../Auth/js/AuthContext";
import Loading from "../../assets/loading.json";
import Lottie from "lottie-react";
import '../../Homepage/css/MovieList.css';
import '../../Auth/js/Auth';
import '../css/SuggestedMoviesList.css';
import ListOfMovies from "./ListOfMovies";
import NoMovieYet from "../../assets/NoDataYet.json";

// Component representing the movie suggestion list
const MovieSuggestionList = () => {
    const navigate = useNavigate();
    const {user} = useContext(AuthContext);
    const [suggestedMoviesList, setSuggestedMoviesList] = useState(new Set());
    const [loading, setLoading] = useState(false);

    // Function to navigate back
    const handleGoBack = () => {
        navigate(-1);
    };

    // Function to get the movie suggestion list for the user
    const handleGetMoviesList = async () => {
        try {
            setLoading(true);
            // Send a request to get user information, including the movie suggestion list
            const response = await fetch("https://9acdf5s7k2.execute-api.us-west-2.amazonaws.com/dev/getUserInfo", {
                method: "POST",
                body: JSON.stringify({
                    Email: user.email
                })
            });

            if (response.ok) {
                console.log("Movies list fetched successfully!");
                // Parse the response data
                const userData = await response.json();

                // Check if MovieSuggestionsList array exists before using map
                if (userData.MovieSuggestionsList && Array.isArray(userData.MovieSuggestionsList)) {
                    const suggestedMovieList = userData.MovieSuggestionsList;
                    // Update the suggested movie list in the state
                    setSuggestedMoviesList(new Set(suggestedMovieList));
                } else {
                    console.error("Invalid movies list data:", userData);
                }
            } else {
                console.error("Failed to fetch movie list:", response.status, response.statusText);

                // Log the response when it's not OK
                const errorResponse = await response.text();
                console.error("Error response:", errorResponse);
            }
        } catch (error) {
            console.error("Error fetching movie list:", error.message);
        } finally {
            setLoading(false);
        }
    };

    // Fetch movie suggestion list on component mount
    useEffect(() => {
        handleGetMoviesList();
    }, []);

    // Render the MovieSuggestionList component
    return (
        <div>
            <div className="mylist-header">
                <button onClick={handleGoBack} className="generic-button button-back-suggestionList">Back
                </button>
                <h2 className="my-list-title">Friend Suggestion List</h2>
            </div>
            <div className="list-main-area">
                {loading ? (
                    <div className="loading-container-suggestion-list">
                        <Lottie loop={true} animationData={Loading}/>
                    </div>
                ) : (
                    <div>
                        {suggestedMoviesList.size > 0 ? (
                            <ListOfMovies
                                moviesList={Array.from(suggestedMoviesList)}
                            />
                        ) : (
                            <div>
                                <p className="no-movie-suggestion-yet">No movie suggestions yet.</p>
                                <div className="no-data-yet-suggestion-list">
                                    <Lottie loop={true} animationData={NoMovieYet}/>
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};
export default MovieSuggestionList;