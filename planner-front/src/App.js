import * as React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./Components/Home";
import Footer from "./Components/Footer";
import SignIn from "./Components/Auth/SignIn";
import SignUp from "./Components/Auth/SignUp";
import BalanceField from "./Components/Balance/BalanceField";
import GoalList from "./Components/Goal/GoalList";
import EventList from "./Components/Event/EventList";
import ErrorPage from "./Components/Error/ErrorPage";
import NotFoundPage from "./Components/Error/NotFoundPage";
import { ACCESS_TOKEN, tokenStorage } from "./services/token-storage";

function App() {
    tokenStorage.accessToken = localStorage.getItem(ACCESS_TOKEN);

    return (
        <Router>
            <Routes>
                <Route path='/' element={ <Home /> } />
                <Route path='/sign-in' element={ <SignIn /> } />
                <Route path='/sign-up' element={ <SignUp /> } />
                <Route path='/balance' element={ <BalanceField /> } />
                <Route path='/goals' element={ <GoalList /> } />
                <Route path='/events' element={ <EventList /> } />
                <Route path='/error' element={ <ErrorPage /> } />
                <Route path='*' element={ <NotFoundPage /> } />
            </Routes>
            <Footer sx={{ mt: 5, mb: 3 }} />
        </Router>
  );
}

export default App;
