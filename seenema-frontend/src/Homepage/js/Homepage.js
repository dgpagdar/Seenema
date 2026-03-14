import {useState} from 'react';
import Header from './Header';
import MovieList from "./MovieList";
import "../css/Homepage.css"
import "../../App.css"
import Sidebar from "./Sidebar";
import {useNavigate, useParams} from 'react-router-dom';

// Homepage composes the Header, Sidebar, and MovieList into the main layout
function Homepage() {
    const [searchValue, setSearchValue] = useState('');
    const [showSearchFlag, setShowSearchFlag] = useState(false);
    const searchTerm = useParams();
    const navigate = useNavigate();

    // Handler for search input changes
    const handleSearchChange = (showSearch) => {
        // Update search value and showSearchFlag based on the searchTerm and the argument passed
        if (searchTerm !== undefined) {
            setSearchValue(searchTerm);
            setShowSearchFlag(true);
        } else {
            setSearchValue();
        }
        setShowSearchFlag(showSearch);
    }

    // Handler for genre selection
    const handleOnGenreChange = (selectedGenre) => {
        // Navigate to the selected genre's route and reset search value
        navigate(`/genre/${selectedGenre}`);
        setSearchValue("");
    }

    return (
        <div className="home-layout">
            {/*Header Component */}
            <div><Header onSearch={handleSearchChange} isGenre={false}/></div>
            <div className="homepage-main-content">
                {/* Sidebar */}
                <div className="homepage-sidebar">
                    <Sidebar onGenreChange={handleOnGenreChange}/>
                </div>
                <div className="main-content-area">
                    {/* Rendering MovieList component */}
                    <MovieList/>
                </div>
            </div>
        </div>
    );
}

export default Homepage;