const loadMainScreen = () => {
    return "Main screen loaded";
};

describe('loadMainScreen', () => {
    test('returns expected string when main screen loads', () => {
        expect(loadMainScreen()).toBe("Main screen loaded");
    });
});
