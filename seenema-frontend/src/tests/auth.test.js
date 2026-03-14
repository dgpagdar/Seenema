const authenticateUser = (username, password) => {
    if (username && password) {
        return true;
    }
    return false;
};

describe('authenticateUser', () => {
    test('returns true when valid credentials are provided', () => {
        expect(authenticateUser('testUser', 'testPassword')).toBe(true);
    });

    test('returns false when credentials are missing', () => {
        expect(authenticateUser('', '')).toBe(false);
    });
});
