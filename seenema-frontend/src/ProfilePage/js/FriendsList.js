import "../css/UserProfilePage.css";

// Renders the current user's friends as a styled list
const FriendsList = ({friendsList}) => {
    return (
        <div>
            <div className="friends-list-header">
                <h3>Friends List</h3>
                <hr/>
            </div>
            <div className="friends-list-container">
                <ul className="list-group">
                    {friendsList.map((friend) => (
                        <li key={friend} className="list-group-item"
                            style={{backgroundColor: "rgba(149,148,153,0.94)", borderColor: "#F1FAEE"}}>{friend}</li>
                    ))}
                </ul>
            </div>
        </div>
    );
};
export default FriendsList;
