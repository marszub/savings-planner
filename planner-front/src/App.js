import * as React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./Components/Home";
import Footer from "./Components/Footer";
import SignIn from "./Components/Auth/SignIn";
import SignUp from "./Components/Auth/SignUp";
import Error from "./Components/Error";

function App() {
    return (
        <Router>
            <Routes>
                <Route path='/' element={ <Home /> } />
                <Route path='/sign-in' element={ <SignIn /> } />
                <Route path='/sign-up' element={ <SignUp /> } />
                <Route path='/error' element={ <Error /> } />
            </Routes>
            <Footer sx={{ mt: 5 }} />
        </Router>
  );
}

export default App;
